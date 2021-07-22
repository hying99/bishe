"""
Epistemic uncertainty query strategy
"""

# Author: Pascal Mergard <Pascal.Mergard@student.uni-kassel.de>
import warnings

import numpy as np
from scipy.interpolate import griddata
from scipy.optimize import minimize_scalar, minimize, LinearConstraint
from sklearn.base import clone
from sklearn.linear_model import LogisticRegression
from sklearn.linear_model._logistic import _logistic_loss

from ..base import SingleAnnotPoolBasedQueryStrategy, SkactivemlClassifier
from ..classifier import SklearnClassifier, PWC
from ..utils import is_labeled, check_X_y, simple_batch, check_scalar


class EpistemicUncertainty(SingleAnnotPoolBasedQueryStrategy):
    """
    Epistemic uncertainty sampling query strategy for two class problems.
    Based on [1]. This strategy is only implemented for skactiveml parzen
    window classifier and sklearn logistic regression classifier.

    Parameters
    ----------
    clf : SkactivemlClassifier
        A classifier. Only skactiveml.classifier.PWC or
        sklearn.linear_model.LogisticRegression are supported.
    precompute : boolean (default=False)
        Whether the epistemic uncertainty should be precomputed.
        Only for PWC significant.
    random_state : numeric | np.random.RandomState
        The random state to use.

    Attributes
    ----------
    clf : SkactivemlClassifier
        The given SkactivemlClassifier.
    precompute : boolean (default=False)
        Whether the epistemic uncertainty should be precomputed.
        Only for PWC significant.
    random_state : numeric | np.random.RandomState
        Random state to use.

    References
    ---------
    [1] Nguyen, Vu-Linh, Sébastien Destercke, and Eyke Hüllermeier.
        "Epistemic uncertainty sampling." International Conference on
        Discovery Science. Springer, Cham, 2019.
    """

    def __init__(self, clf, precompute=False, random_state=None):
        super().__init__(random_state=random_state)

        self.clf = clf
        self.precompute = precompute

    def query(self, X_cand, X, y, sample_weight=None, batch_size=1,
              return_utilities=False):
        """
        Queries the next instance to be labeled.

        Parameters
        ----------
        X_cand : np.ndarray
            The unlabeled pool from which to choose.
        X : np.ndarray
            The labeled pool used to fit the classifier.
        y : np.array
            The labels of the labeled pool X.
        sample_weight : array-like of shape (n_samples,) (default=None)
            Sample weights for X, only used if clf is a logistic regression
            classifier.
        batch_size : int, optional (default=1)
            The number of samples to be selected in one AL cycle.
        return_utilities : bool (default=False)
            If True, the utilities are returned.

        Returns
        -------
        best_indices : np.ndarray, shape (batch_size)
            The index of the queried instance.
        batch_utilities : np.ndarray,  shape (batch_size, len(X_cand))
            The utilities of all instances of
            X_cand(if return_utilities=True).
        """
        # Validate input parameters.
        X_cand, return_utilities, batch_size, random_state = \
            self._validate_data(X_cand, return_utilities, batch_size,
                                self.random_state, reset=True)

        X, y, sample_weight = check_X_y(X, y, sample_weight=sample_weight)
        # Check if the attribute clf is valid
        if not isinstance(self.clf, SkactivemlClassifier):
            raise TypeError(
                'clf as to be from type SkactivemlClassifier. The #'
                'given type is {}. Use the wrapper in '
                'skactiveml.classifier to use a sklearn '
                'classifier/ensemble.'.format(type(self.clf)))

        # fit the classifier and get the probabilities
        clf = clone(self.clf)
        clf.fit(X, y, sample_weight=sample_weight)

        # chose the correct method for the given clf
        if isinstance(clf, PWC):
            if not hasattr(self, 'precompute_array'):
                self._precompute_array = None

            # create precompute_array if necessary
            if not isinstance(self.precompute, bool):
                raise TypeError("'precompute' should be from type bool but {} "
                                "were given".format(type(self.precompute)))
            if self.precompute and self._precompute_array is None:
                self._precompute_array = np.full((2, 2), np.nan)

            freq = clf.predict_freq(X_cand)
            utilities, self._precompute_array = _epistemic_uncertainty_pwc(
                freq, self._precompute_array)
        elif isinstance(clf, SklearnClassifier) and \
                isinstance(clf.estimator, LogisticRegression):
            mask_labeled = is_labeled(y, clf.missing_label)
            utilities = _epistemic_uncertainty_logreg(
                X_cand=X_cand, X=X[mask_labeled], y=y[mask_labeled], clf=clf,
                sample_weight=sample_weight[mask_labeled]
            )
        else:
            raise TypeError("'clf' must be from type PWC or "
                            "a wrapped LogisticRegression classifier. "
                            "The given is from type {}."
                            "".format(type(self.clf)))

        return simple_batch(utilities, random_state,
                            batch_size=batch_size,
                            return_utilities=return_utilities)


# Epistemic uncertainty scores for pwc.
def _epistemic_uncertainty_pwc(freq, precompute_array=None):
    """
    Computes the epistemic uncertainty score for a parzen window classifier
    [1]. Only for two class problems.

    Parameters
    ----------
    freq : np.ndarray of shape (n_samples, 2)
        The class frequency estimates.
    precompute_array : np.ndarray of a quadratic shape (default=None)
        Used to interpolate and speed up the calculation. Will be enlarged if
        necessary. All entries that are 'np.nan' will be filled.

    Returns
    -------
    utilities : np.ndarray of shape (n_samples,)
        The calculated epistemic uncertainty scores.
    precompute_array : quadratic np.nparray of lenght int(np.max(freq) + 1)
        The enlarged precompute_array. Will be None if the given is None.

    References
    ---------
    [1] Nguyen, Vu-Linh, Sébastien Destercke, and Eyke Hüllermeier.
        "Epistemic uncertainty sampling." International Conference on
        Discovery Science. Springer, Cham, 2019.
    """
    if freq.shape[1] != 2:
        raise ValueError('epistemic is only implemented for two-class '
                         'problems, {} classes were given.'
                         ''.format(freq.shape[1]))
    n = freq[:, 0]
    p = freq[:, 1]
    utilities = np.full((len(freq)), np.nan)
    if precompute_array is not None:
        # enlarges the precompute_array array if necessary:
        if precompute_array.shape[0] < np.max(n) + 1:
            new_shape = (int(np.max(n)) - precompute_array.shape[0] + 2,
                         precompute_array.shape[1])
            precompute_array = np.append(precompute_array,
                                         np.full(new_shape, np.nan), axis=0)
        if precompute_array.shape[1] < np.max(p) + 1:
            new_shape = (precompute_array.shape[0],
                         int(np.max(p)) - precompute_array.shape[1] + 2)
            precompute_array = np.append(precompute_array,
                                         np.full(new_shape, np.nan), axis=1)

        # precompute the epistemic uncertainty:
        for N in range(precompute_array.shape[0]):
            for P in range(precompute_array.shape[1]):
                if np.isnan(precompute_array[N, P]):
                    pi1 = -minimize_scalar(
                        _pwc_ml_1, method='Bounded',
                        bounds=(0.0, 1.0), args=(N, P)
                    ).fun

                    pi0 = -minimize_scalar(
                        _pwc_ml_0, method='Bounded',
                        bounds=(0.0, 1.0), args=(N, P)
                    ).fun

                    pi = np.array([pi0, pi1])
                    precompute_array[N, P] = np.min(pi, axis=0)
        utilities = _interpolate(precompute_array, freq)
    else:
        for i, f in enumerate(freq):
            pi1 = -minimize_scalar(
                _pwc_ml_1, method='Bounded', bounds=(0.0, 1.0),
                args=(f[0], f[1])).fun

            pi0 = -minimize_scalar(
                _pwc_ml_0, method='Bounded', bounds=(0.0, 1.0),
                args=(f[0], f[1])).fun

            pi = np.array([pi0, pi1])
            utilities[i] = np.min(pi, axis=0)
    return utilities, precompute_array


def _interpolate(precompute_array, freq):
    """
    Linearly interpolation.
    For further informations see scipy.interpolate.griddata.

    Parameters
    ----------
    precompute_array : np.ndarray of a quadratic shape
        Data values. The length should be greater than int(np.max(freq) + 1).
    freq : np.ndarray of shape (n_samples, 2)
        Points at which to interpolate data.

    Returns
    -------
        Array of interpolated values.
    """
    points = np.zeros(
        (precompute_array.shape[0] * precompute_array.shape[1], 2)
    )
    for n in range(precompute_array.shape[0]):
        for p in range(precompute_array.shape[1]):
            points[n * precompute_array.shape[1] + p] = n, p
    return griddata(points, precompute_array.flatten(), freq, method='linear')


def _pwc_ml_1(theta, n, p):
    """
    Calulates the maximum likelihood for class 1 of epistemic for pwc.

    Parameters
    ----------
    theta : array-like
        The parameter vector.
    n : float
        frequency estimate for the negative class.
    p : float
        frequency estimate for the positive class.

    Returns
    -------
        float
        The maximum likelihood for class 1 of epistemic for pwc.
    """
    if (n == 0.0) and (p == 0.0):
        return -1.0
    piH = ((theta ** p) * ((1 - theta) ** n)) / \
          (((p / (n + p)) ** p) * ((n / (n + p)) ** n))
    return -np.minimum(piH, 2 * theta - 1)


def _pwc_ml_0(theta, n, p):
    """
    Calulates the maximum likelihood for class 0 of epistemic for pwc.

    Parameters
    ----------
    theta : array-like
        The parameter vector.
    n : float
        frequency estimate for the negative class.
    p : float
        frequency estimate for the positive class.

    Returns
    -------
        float
        The maximum likelihood for class 0 of epistemic for pwc.
    """
    if ((n == 0.0) and (p == 0.0)):
        return -1.0
    piH = ((theta ** p) * ((1 - theta) ** n)) / \
          (((p / (n + p)) ** p) * ((n / (n + p)) ** n))
    return -np.minimum(piH, 1 - 2 * theta)


# Epistemic uncertainty scores for logistic regression.
def _epistemic_uncertainty_logreg(X_cand, X, y, clf, sample_weight=None):
    """
    Calculates the epistemic uncertainty score for logistic regression [1].
    Only for two class problems.

    Parameters
    ----------
    X_cand : np.ndarray
        The unlabeled pool from which to choose.
    X : np.ndarray
        The labeled pool used to fit the classifier.
    y : np.array
        The labels of the labeled pool X.
    clf : LogisticRegression
        The fitted classifier.
    sample_weight : array-like of shape (n_samples,) (default=None)
        Sample weights for X, only used if clf is a logistic regression
        classifier.

    Returns
    -------
    utilities : np.ndarray of shape (n_samples_cand,)
        The calculated epistemic uncertainty scores.

    References
    ---------
    [1] Nguyen, Vu-Linh, Sébastien Destercke, and Eyke Hüllermeier.
        "Epistemic uncertainty sampling." International Conference on
        Discovery Science. Springer, Cham, 2019.
    """
    if not isinstance(clf, SklearnClassifier) \
            or not isinstance(clf.estimator, LogisticRegression):
        raise TypeError('clf has to be a wrapped LogisticRegression classifier '
                        'but \n{}\n was given.'.format(clf))
    if len(clf.classes) != 2:
        raise ValueError('epistemic is only implemented for two-class '
                         'problems, {} classes were given.'
                         ''.format(len(clf.classes)))

    # Get the probability predictions.
    probas = clf.predict_proba(X_cand)

    # Get the regularization parameter from the clf.
    gamma = 1/clf.C

    # Get weights from the classifier.
    if clf.is_fitted_:
        w_ml = np.append(clf.coef_, clf.intercept_).flatten()
    else:
        warnings.warn('The given classifier is not fitted or was fitted with '
                      'zero labels. Epistemic uncertainty sampling will fall '
                      'back to random sampling.')
        w_ml = np.zeros(X.shape[1]+1)

    # Calculate the maximum likelihood of the logistic function.
    L_ml = np.exp(
        -_loglike_logreg(w=w_ml, X=X, y=y, gamma=gamma,
                         sample_weight=sample_weight)
    )

    # Set the initial guess for minimize function.
    x0 = np.zeros((X_cand.shape[1] + 1))

    # Set initial epistemic scores.
    pi1 = np.maximum(2 * probas[:, 0] - 1, 0)
    pi0 = np.maximum(1 - 2 * probas[:, 0], 0)

    # Compute pi0, pi1 for every x in X_cand.
    for i, x in enumerate(X_cand):
        Qn = np.linspace(0.01, 0.5, num=50, endpoint=True)
        Qp = np.linspace(0.5, 1.0, num=50, endpoint=False)

        A = np.append(x, 1)  # Used for the LinearConstraint
        for q in range(50):
            alpha_n, alpha_p = Qn[0], Qp[-1]
            if 2 * alpha_p - 1 > pi1[i]:
                # Compute theta for alpha_p and x.
                theta_p = _theta(func=_loglike_logreg, alpha=alpha_p, x0=x0,
                                 A=A, args=(X, y, sample_weight, gamma))
                # Compute the  degrees of support for theta_p.
                pi1[i] = np.maximum(
                    pi1[i], np.minimum(_pi_h(theta=theta_p, L_ml=L_ml, X=X,
                                             y=y, sample_weight=sample_weight,
                                             gamma=gamma),
                                       2 * alpha_p - 1)
                )
            if 1 - 2 * alpha_n > pi0[i]:
                # Compute theta for alpha_n and x.
                theta_n = _theta(func=_loglike_logreg, alpha=alpha_n, x0=x0,
                                 A=A, args=(X, y, sample_weight, gamma))
                # Compute the  degrees of support for theta_n.
                pi0[i] = np.maximum(
                    pi0[i], np.minimum(_pi_h(theta=theta_n, L_ml=L_ml, X=X,
                                             y=y, sample_weight=sample_weight,
                                             gamma=gamma),
                                       1 - 2 * alpha_p)
                )
            Qn, Qp = np.delete(Qn, 0), np.delete(Qp, -1)

    utilities = np.min(np.array([pi0, pi1]), axis=0)
    return utilities


def _pi_h(theta, L_ml, X, y, sample_weight=None, gamma=1):
    """
    Computes np.exp(-_loglike_logreg())/L_ml, the normalized likelihood.

    Parameters
    ----------
    theta : np.ndarray of shape (n_features + 1,)
        Coefficient vector.
    L_ml : float
        The maximum likelihood estimation on the training data.
        Use np.exp(-_loglike_logreg) to compute.
    X : np.ndarray
        The labeled pool used to fit the classifier.
    y : np.array
        The labels of the labeled pool X.
    sample_weight : np.ndarray of shape (n_samples,) (default=None)
        Sample weights for X, only used if clf is a logistic regression
        classifier.
    gamma : float
        The regularization parameter.

    Returns
    -------
    pi_h : float
        The normalized likelihood.

    References
    ---------
    [1] Nguyen, Vu-Linh, Sébastien Destercke, and Eyke Hüllermeier.
        "Epistemic uncertainty sampling." International Conference on
        Discovery Science. Springer, Cham, 2019.

    """
    check_scalar(L_ml, name='L_ml', target_type=(float, int))

    L_theta = np.exp(
        -_loglike_logreg(w=theta, X=X, y=y, sample_weight=sample_weight,
                         gamma=gamma)
    )
    return L_theta / L_ml


def _loglike_logreg(w, X, y, sample_weight=None, gamma=1):
    """Computes the logistic loss.

    Parameters
    ----------
    w : np.ndarray of shape (n_features + 1,)
        Coefficient vector.

    X : {array-like, sparse matrix} of shape (n_samples, n_features)
        Training data.

    y : np.ndarray of shape (n_samples,)
        The labels of the training data X.

    gamma : float
        Regularization parameter. gamma is equal to 1 / C.

    sample_weight : array-like of shape (n_samples,) default=None
        Array of weights that are assigned to individual samples.
        If not provided, then each sample is given unit weight.

    Returns
    -------
    out : float
        Logistic loss, the negative of the log of the logistic function.
    """
    if len(y) == 0:
        return np.log(2)*len(X)
    return _logistic_loss(w=w, X=X, y=y, alpha=gamma,
                          sample_weight=sample_weight)


def _theta(func, alpha, x0, A, args=()):
    """
    This function calculates the parameter vector as it is shown in equation 22
     in [1].
    Parameters
    ----------
    func : callable
        The function to be optimized.
    alpha : float
        ln(alpha/(1-alpha)) will used as bound for the constraint.
    x0 : np.ndarray of shape (n,)
        Initial guess. Array of real elements of size (n,), where ‘n’ is the
        number of independent variables.
    A : np.ndarray
        Matrix defining the constraint.
    args : tuple
        Will be pass to func.

    Returns
    -------
    x : np.ndarray
        The optimized parameter vector.

    References
    ---------
    [1] Nguyen, Vu-Linh, Sébastien Destercke, and Eyke Hüllermeier.
        "Epistemic uncertainty sampling." International Conference on
        Discovery Science. Springer, Cham, 2019.
    """
    bounds = np.log(alpha / (1 - alpha))
    constraints = LinearConstraint(A=A, lb=bounds, ub=bounds)
    res = minimize(func, x0=x0, method='SLSQP', constraints=constraints,
                   args=args)
    return res.x
