"""
query by committee strategies
"""

# Author: Pascal Mergard <Pascal.Mergard@student.uni-kassel.de>

import numpy as np
import warnings
import inspect

from sklearn import clone

from ..base import SingleAnnotPoolBasedQueryStrategy, SkactivemlClassifier

from sklearn.ensemble import BaggingClassifier, BaseEnsemble
from sklearn.utils import check_array

from ..classifier import SklearnClassifier
from ..utils import simple_batch, check_classes, ExtLabelEncoder


class QBC(SingleAnnotPoolBasedQueryStrategy):
    """QBC

    The Query-By-Committee (QBC) algorithm minimizes the version space, which
    is the set of hypotheses that are consistent with the current labeled
    training data.

    Parameters
    ----------
    clf : SkactivemlClassifier
        If clf is an wrapped ensemble, it will used as committee. If clf is a
        classifier, it will used for ensemble construction with the specified
        ensemble or with BaggigngClassifier, if ensemble is None. clf must
        implementing the methods 'fit', 'predict'(for vote entropy) and
        'predict_proba'(for KL divergence).
    ensemble : BaseEnsemble, default=None
        wrapped sklear.ensemble used as committee. If None, baggingClassifier
        is used.
    method : string, default='KL_divergence'
        The method to calculate the disagreement.
        'vote_entropy' or 'KL_divergence' are possible.
    random_state : numeric | np.random.RandomState
        Random state to use.
    ensemble_dict : dictionary
        will be passed on to the ensemble.

    Attributes
    ----------
    ensemble : sklearn.ensemble
        Ensemble used as committee. Implementing the methods 'fit',
        'predict'(for vote entropy) and 'predict_proba'(for KL divergence).
    method : string, default='KL_divergence'
        The method to calculate the disagreement. 'vote_entropy' or
        'KL_divergence' are possible.
    random_state : numeric | np.random.RandomState
        Random state to use.

    References
    ----------
    [1] H.S. Seung, M. Opper, and H. Sompolinsky. Query by committee.
        In Proceedings of the ACM Workshop on Computational Learning Theory,
        pages 287-294, 1992.
    [2] N. Abe and H. Mamitsuka. Query learning strategies using boosting and
        bagging. In Proceedings of the International Conference on Machine
        Learning (ICML), pages 1-9. Morgan Kaufmann, 1998.
    """

    def __init__(self, clf, ensemble=None, method='KL_divergence',
                 random_state=None, ensemble_dict=None):

        super().__init__(random_state=random_state)

        self.method = method
        self.ensemble = ensemble
        self.clf = clf
        self.ensemble_dict = ensemble_dict


    def query(self, X_cand, X, y, sample_weight=None,  batch_size=1,
              return_utilities=False):
        """
        Queries the next instance to be labeled.

        Parameters
        ----------
        X_cand : array-like
            The unlabeled pool from which to choose.
        X : array-like
            The labeled pool used to fit the classifier.
        y : array-like
            The labels of the labeled pool X.
        sample_weight : array-like of shape (n_samples,) (default=None)
            Sample weights.
        batch_size : int, optional (default=1)
            The number of samples to be selected in one AL cycle.
        return_utilities : bool (default=False)
            If True, the utilities are returned.

        Returns
        -------
        best_indices : np.ndarray, shape (batch_size)
            The index of the queried instance.
        batch_utilities : np.ndarray,  shape (batch_size, len(X_cnad))
            The utilities of all instances of
            X_cand(if return_utilities=True).
        """
        self._clf = clone(self.clf)

        if self.ensemble_dict is None:
            ensemble_dict = dict()
        else:
            ensemble_dict = self.ensemble_dict

        # Validate input parameters.
        X_cand, return_utilities, batch_size, random_state = \
            self._validate_data(X_cand, return_utilities, batch_size,
                                self.random_state, reset=True)

        # Check if the attribute clf is valid
        if not isinstance(self._clf, SkactivemlClassifier):
            raise TypeError('clf as to be from type SkactivemlClassifier. The #'
                            'given type is {}. Use the wrapper in '
                            'skactiveml.classifier to use a sklearn '
                            'classifier/ensemble.'.format(type(self.clf)))

        # Extract classes from clf
        label_encoder = ExtLabelEncoder(missing_label=self._clf.missing_label,
                                        classes=self.clf.classes).fit(y)
        classes = label_encoder.classes_

        # Check self.clf and self.method
        if not isinstance(self.method, str):
            raise TypeError("{} is an invalid type for the attribute "
                            "'self.method'.".format(type(self.method)))
        if self.method not in ['KL_divergence', 'vote_entropy']:
            raise ValueError(
                "The given method {} is not valid. Supported methods are "
                "'KL_divergence' and 'vote_entropy'".format(self.method))
        if self.method == 'vote_entropy' and \
                ((getattr(self._clf, 'fit', None) is None or
                  getattr(self._clf, 'predict', None) is None)):
            raise TypeError(
                "'clf' must implement the methods 'fit' and 'predict'")
        elif self.method == 'KL_divergence' and \
                ((getattr(self._clf, 'fit', None) is None or
                  getattr(self._clf, 'predict_proba', None) is None)):
            raise TypeError(
                "'clf' must implement the methods 'fit' and 'predict_proba'")

        if not isinstance(self._clf, SklearnClassifier) or \
                not isinstance(self._clf.estimator, BaseEnsemble):
            if self.ensemble is None:
                warnings.warn('\'ensemble\' is not specified, '
                              '\'BaggingClassifier\' will be used.')
                ensemble = BaggingClassifier
            elif not callable(self.ensemble):
                raise TypeError("{} is not valid for the attribute "
                                "'self.ensemble'.".format(self.ensemble))
            elif not isinstance(self.ensemble(), BaseEnsemble):
                raise TypeError("{} is an invalid type for the attribute "
                                "'self.ensemble'.".format(type(self.ensemble)))
            else:
                ensemble = self.ensemble
            parameters = inspect.signature(ensemble.__init__).parameters
            if not isinstance(ensemble_dict, dict):
                raise TypeError("ensemble_dict is not a dictionary.")
            if 'base_estimator' in parameters:
                ensemble_dict['base_estimator'] = self._clf
            self._clf = SklearnClassifier(
                ensemble(**ensemble_dict, random_state=random_state),
                classes=classes
            )

        # fit the classifier
        self._clf.fit(X, y, sample_weight=sample_weight)

        # choose the disagreement method and calculate the utilities
        if hasattr(self._clf, 'estimators_') and self._clf.is_fitted_:
            est_arr = self._clf.estimators_
        else:
            est_arr = [self._clf] * self._clf.n_estimators
        if self.method == 'KL_divergence':
            probas = [est.predict_proba(X_cand) for est in est_arr]
            utilities = average_kl_divergence(probas)
        elif self.method == 'vote_entropy':
            votes = [est.predict(X_cand) for est in est_arr]
            utilities = vote_entropy(votes, classes)

        return simple_batch(utilities, random_state,
                            batch_size=batch_size,
                            return_utilities=return_utilities)


def average_kl_divergence(probas):
    """
    Calculate the average Kullback-Leibler (KL) divergence for measuring the
    level of disagreement in QBC.

    Parameters
    ----------
    probas : array-like, shape (n_estimators, n_X_cand, n_classes)
        The probability estimations of all estimators, instances and classes.

    Returns
    -------
    scores: np.ndarray, shape (n_X_cand)
        The Kullback-Leibler (KL) divergences.

    References
    ----------
    [1] A. McCallum and K. Nigam. Employing EM in pool-based active learning
    for text classification. In Proceedings of the International Conference on
    Machine Learning (ICML), pages 359-367. Morgan Kaufmann, 1998.
    """

    # validation:
    # check P
    probas = check_array(probas, accept_sparse=False,
                         accept_large_sparse=True, dtype="numeric", order=None,
                         copy=False, force_all_finite=True, ensure_2d=True,
                         allow_nd=True, ensure_min_samples=1,
                         ensure_min_features=1, estimator=None)
    if probas.ndim != 3:
        raise ValueError("Expected 2D array, got 1D array instead:"
                         "\narray={}.".format(probas))

    # calculate the average KL divergence:
    probas = np.array(probas)
    probas_mean = np.mean(probas, axis=0)
    with np.errstate(divide='ignore', invalid='ignore'):
        scores = np.nansum(
            np.nansum(probas * np.log(probas / probas_mean), axis=2), axis=0)
    scores = scores / probas.shape[0]

    return scores


def vote_entropy(votes, classes):
    """
    Calculate the vote entropy for measuring the level of disagreement in QBC.

    Parameters
    ----------
    votes : array-like, shape (n_estimators, n_X_cand)
        The class predicted by the estimators for each instance.
    classes : array-like, shape (n_classes)
        A list of all possible classes.

    Returns
    -------
    vote_entropy : np.ndarray, shape (n_X_cand))
        The vote entropy of each instance in X_cand.

    References
    ----------
    [1] Engelson, Sean P., and Ido Dagan.
    "Minimizing manual annotation cost in supervised training from corpora."
    arXiv preprint cmp-lg/9606030 (1996).
    """
    # check votes to be valid
    votes = check_array(votes, accept_sparse=False,
                        accept_large_sparse=True, dtype=None, order=None,
                        copy=False, force_all_finite=True, ensure_2d=True,
                        allow_nd=False, ensure_min_samples=1,
                        ensure_min_features=1, estimator=None)

    # Check classes to be valid
    check_classes(classes)
    try:
        for c in np.unique(votes.flatten()):
            if c not in classes:
                raise ValueError("The votes element '{}' "
                                 "is not in classes.".format(c))
    except TypeError:
        raise TypeError("The type of classes and votes are not compatible.")
    # count the votes
    vote_count = np.zeros((votes.shape[1], len(classes)))
    for i in range(votes.shape[1]):
        for c_idx, c in enumerate(classes):
            for m in range(len(votes)):
                vote_count[i, c_idx] += (votes[m, i] == c)

    # compute vote entropy
    v = vote_count / len(votes)
    with np.errstate(divide='ignore', invalid='ignore'):
        scores = -np.nansum(v*np.log(v), axis=1) / np.log(len(votes))
    return scores
