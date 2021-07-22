import numpy as np
from sklearn.base import clone

from skactiveml.base import SingleAnnotPoolBasedQueryStrategy
from skactiveml.base import ClassFrequencyEstimator
from skactiveml.utils import check_classifier_params, \
    check_X_y, is_labeled, simple_batch


class ExpectedErrorReduction(SingleAnnotPoolBasedQueryStrategy):
    """Expected Error Reduction.

    This class implements the expected error reduction algorithm with different
    loss functions:
     - log loss (log_loss) [1],
     - expected misclassification risk (emr) [2],
     - and cost-sensitive learning (csl) [2].

    Parameters
    ----------
    clf : ClassFrequencyEstimator
        Model implementing the methods 'fit' and and 'predict_proba'.
    method: {'log_loss', 'emr', 'csl'}, optional (default='emr')
        Variant of expected error reduction to be used: 'log_loss' is
        cost-insensitive, while 'emr' and 'csl' are cost-sensitive variants.
    cost_matrix: array-like, shape (n_classes, n_classes),
                 optional (default=None)
        Cost matrix with C[i,j] defining the cost of predicting class j for a
        sample with the actual class i. Only supported for least confident
        variant.
    random_state: numeric | np.random.RandomState, optional (defatult=None)
        Random state for annotator selection.

    References
    ----------
    [1] Settles, Burr. "Active learning literature survey." University of
        Wisconsin, Madison 52.55-66 (2010): 11.
    [2] Margineantu, D. D. (2005, July). Active cost-sensitive learning.
        In IJCAI (Vol. 5, pp. 1622-1623).
    """

    EMR = 'emr'
    CSL = 'csl'
    LOG_LOSS = 'log_loss'

    def __init__(self, clf, method=EMR, cost_matrix=None,
                 random_state=None):
        super().__init__(random_state=random_state)
        self.clf = clf
        self.method = method
        self.cost_matrix = cost_matrix

    def query(self, X_cand, X, y, sample_weight=None, sample_weight_cand=None,
              batch_size=1, return_utilities=False):
        """Query the next instance to be labeled.

        Parameters
        ----------
        X_cand: array-like, shape (n_candidates, n_features)
            Unlabeled candidate samples
        X: array-like, shape (n_samples, n_features)
            Complete data set
        y: array-like, shape (n_samples)
            Labels of the data set
        sample_weight: array-like, shape (n_samples), optional (default=None)
            Weights for uncertain annotators
        sample_weight_cand: array-like, shape (n_candidates),
                            optional (default=None)
            Weights for the candidate samples
        batch_size: int, optional (default=1)
            The number of instances to be selected.
        return_utilities: bool, optional (default=False)
            If True, the utilities are additionally returned.

        Returns
        -------
        query_indices: np.ndarray, shape (batch_size)
            The index of the queried instance.
        utilities: np.ndarray, shape (batch_size, n_candidates)
            The utilities of all instances in X_cand
            (only returned if return_utilities is True).
        """
        # Validate input parameters.
        X_cand, return_utilities, batch_size, random_state = \
            self._validate_data(X_cand, return_utilities, batch_size,
                                self.random_state, reset=True)

        # Calculate utilities
        utilities = _expected_error_reduction(self.clf, X_cand, X, y,
                                              self.cost_matrix, self.method,
                                              sample_weight,
                                              sample_weight_cand)

        return simple_batch(utilities, random_state,
                            batch_size=batch_size,
                            return_utilities=return_utilities)


def _expected_error_reduction(clf, X_cand, X, y, C, method='emr',
                              sample_weight_cand=None, sample_weight=None):
    """Compute least confidence as uncertainty scores.

    In case of a given cost matrix C, maximum expected cost is implemented as
    score.

    Parameters
    ----------
    clf: sklearn classifier with predict_proba method
        Model whose expected error reduction is measured.
    X_cand: array-like, shape (n_candidates, n_features)
        Unlabeled candidate samples
    X: array-like, shape (n_samples, n_features)
        Complete data set
    y: array-like, shape (n_samples)
        Labels of the data set
    C: array-like, shape (n_classes, n_classes)
        Cost matrix with C[i,j] defining the cost of predicting class j for a
        sample with the actual class i.
        Only supported for least confident variant.
    method: {'log_loss', 'emr', 'csl'}, optional (default='emr')
        Variant of expected error reduction to be used: 'log_loss' is
        cost-insensitive, while 'emr' and 'csl' are cost-sensitive variants.
    sample_weight: array-like, shape (n_samples), optional (default=None)
        Weights for uncertain annotators
    sample_weight_cand: array-like, shape (n_candidates),
                        optional (default=None)
        Weights for the candidate samples

    Returns
    -------
    utilities: np.ndarray, shape (n_unlabeled_samples)
        The utilities of all unlabeled instances.
    """
    # Check if the classifier and its arguments are valid
    if not isinstance(clf, ClassFrequencyEstimator):
        raise TypeError("'clf' must implement methods according to "
                        "'ClassFrequencyEstimator'.")
    check_classifier_params(clf.classes, clf.missing_label, C)

    # Check the given data
    X, y, X_cand, sample_weight, sample_weight_cand = check_X_y(
        X, y, X_cand, sample_weight, sample_weight_cand,
        force_all_finite=False, missing_label=clf.missing_label
    )

    clf = clone(clf)
    clf.fit(X, y, sample_weight)

    n_classes = len(clf.classes)
    P = clf.predict_proba(X_cand)
    C = 1 - np.eye(np.size(P, axis=1)) if C is None else C
    errors = np.zeros(len(X_cand))
    errors_per_class = np.zeros(n_classes)
    for i, x in enumerate(X_cand):
        w = sample_weight_cand[i]
        for yi in range(n_classes):
            clf.fit(np.vstack((X, [x])),
                    np.append(y, [[yi]]),
                    np.append(sample_weight, w))
            if method == 'emr':
                P_new = clf.predict_proba(X_cand)
                costs = np.sum((P_new.T[:, None] * P_new.T).T * C)
            elif method == 'csl':
                labeled_indices = is_labeled(y, clf.missing_label)
                X_labeled = X[labeled_indices]
                y_labeled = y[labeled_indices]
                y_indices = [np.where(clf.classes == label)[0][0]
                             for label in y_labeled]
                if len(X_labeled) > 0:
                    costs = np.sum(clf.predict_proba(X_labeled) * C[y_indices])
                else:
                    costs = 0
            elif method == 'log_loss':
                P_new = clf.predict_proba(X_cand)
                costs = -np.sum(P_new * np.log(P_new + np.finfo(float).eps))
            else:
                raise ValueError(
                    f"Supported methods are [{ExpectedErrorReduction.EMR}, "
                    f"{ExpectedErrorReduction.CSL}, "
                    f"{ExpectedErrorReduction.LOG_LOSS}], the given one is: "
                    f"{method}"
                )
            errors_per_class[yi] = P[i, yi] * costs
        errors[i] = errors_per_class.sum()
    return -errors
