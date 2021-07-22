import numpy as np
import unittest

from sklearn.exceptions import NotFittedError
from skactiveml.utils import ExtLabelEncoder, is_labeled, is_unlabeled


class TestLabel(unittest.TestCase):

    def setUp(self):
        self.y1 = [np.nan, 2, 5, 10, np.nan]
        self.y2 = [np.nan, '2', '5', '10', np.nan]
        self.y3 = [None, 2, 5, 10, None]
        self.y4 = [None, '2', '5', '10', None]
        self.y5 = [8, -1, 1, 5, 2]
        self.y6 = ['paris', 'france', 'tokyo', 'nan']
        self.y7 = ['paris', 'france', 'tokyo', -1]

    def test_is_unlabeled(self):
        self.assertRaises(TypeError, is_unlabeled, y=self.y1,
                          missing_label='2')
        self.assertRaises(ValueError, is_unlabeled, [],
                          missing_label='2')
        self.assertRaises(ValueError, is_unlabeled, [[]],
                          missing_label='2')
        self.assertRaises(TypeError, is_unlabeled, y=self.y2,
                          missing_label=np.nan)
        self.assertRaises(TypeError, is_unlabeled, y=self.y2,
                          missing_label='2')
        self.assertRaises(TypeError, is_unlabeled, y=self.y2,
                          missing_label=None)
        self.assertRaises(TypeError, is_unlabeled, y=self.y3,
                          missing_label='2')
        self.assertRaises(TypeError, is_unlabeled, y=self.y3,
                          missing_label=np.nan)
        self.assertRaises(TypeError, is_unlabeled, y=self.y4, missing_label=2)
        self.assertRaises(TypeError, is_unlabeled, y=self.y4,
                          missing_label='2')
        self.assertRaises(TypeError, is_unlabeled, y=self.y5,
                          missing_label='2')
        self.assertRaises(TypeError, is_unlabeled, y=self.y6, missing_label=2)
        self.assertRaises(TypeError, is_unlabeled, y=self.y6,
                          missing_label=np.nan)
        self.assertRaises(TypeError, is_unlabeled, y=self.y7,
                          missing_label=np.nan)
        self.assertRaises(TypeError, is_unlabeled, y=self.y7,
                          missing_label=None)
        self.assertRaises(TypeError, is_unlabeled, y=self.y7,
                          missing_label='2')
        self.assertRaises(TypeError, is_unlabeled, y=self.y7, missing_label=-1)
        np.testing.assert_array_equal(np.array([1, 0, 0, 0, 1], dtype=bool),
                                      is_unlabeled(self.y1))
        np.testing.assert_array_equal(np.array([1, 0, 0, 0, 1], dtype=bool),
                                      is_unlabeled(self.y3,
                                                   missing_label=None))
        np.testing.assert_array_equal(np.array([1, 0, 0, 0, 1], dtype=bool),
                                      is_unlabeled(self.y4,
                                                   missing_label=None))
        np.testing.assert_array_equal(np.array([0, 0, 0, 0, 0], dtype=bool),
                                      is_unlabeled(self.y5,
                                                   missing_label=None))
        np.testing.assert_array_equal(np.array([0, 0, 0, 0, 0], dtype=bool),
                                      is_unlabeled(self.y5,
                                                   missing_label=np.nan))
        np.testing.assert_array_equal(np.array([0, 1, 0, 0, 0], dtype=bool),
                                      is_unlabeled(self.y5, missing_label=-1))
        np.testing.assert_array_equal(np.array([0, 0, 0, 0], dtype=bool),
                                      is_unlabeled(self.y6,
                                                   missing_label=None))
        np.testing.assert_array_equal(np.array([0, 0, 0, 1], dtype=bool),
                                      is_unlabeled(self.y6,
                                                   missing_label='nan'))

    def test_is_labeled(self):
        np.testing.assert_array_equal(~np.array([1, 0, 0, 0, 1], dtype=bool),
                                      is_labeled(self.y1))
        np.testing.assert_array_equal(~np.array([1, 0, 0, 0, 1], dtype=bool),
                                      is_labeled(self.y3, missing_label=None))
        np.testing.assert_array_equal(~np.array([1, 0, 0, 0, 1], dtype=bool),
                                      is_labeled(self.y4, missing_label=None))
        np.testing.assert_array_equal(~np.array([0, 0, 0, 0, 0], dtype=bool),
                                      is_labeled(self.y5, missing_label=None))
        np.testing.assert_array_equal(~np.array([0, 0, 0, 0, 0], dtype=bool),
                                      is_labeled(self.y5,
                                                 missing_label=np.nan))
        np.testing.assert_array_equal(~np.array([0, 1, 0, 0, 0], dtype=bool),
                                      is_labeled(self.y5, missing_label=-1))
        np.testing.assert_array_equal(~np.array([0, 0, 0, 0], dtype=bool),
                                      is_labeled(self.y6, missing_label=None))
        np.testing.assert_array_equal(~np.array([0, 0, 0, 1], dtype=bool),
                                      is_labeled(self.y6, missing_label='nan'))

    def test_ExtLabelEncoder(self):
        ext_le = ExtLabelEncoder(classes=[2, '2'])
        self.assertRaises(TypeError, ext_le.fit, self.y1)
        ext_le = ExtLabelEncoder(classes=['1', '2'], missing_label=np.nan)
        self.assertRaises(TypeError, ext_le.fit, self.y1)
        self.assertRaises(NotFittedError, ExtLabelEncoder().transform,
                          y=['1', '2'])

        # missing_label=np.nan
        ext_le = ExtLabelEncoder().fit(self.y1)
        y_enc = ext_le.transform(self.y1)
        np.testing.assert_array_equal([np.nan, 0, 1, 2, np.nan], y_enc)
        y_dec = ext_le.inverse_transform(y_enc)
        np.testing.assert_array_equal(self.y1, y_dec)

        # missing_label=None
        ext_le = ExtLabelEncoder(missing_label=None).fit(self.y3)
        y_enc = ext_le.transform(self.y3)
        np.testing.assert_array_equal([np.nan, 0, 1, 2, np.nan], y_enc)
        y_dec = ext_le.inverse_transform(y_enc)
        np.testing.assert_array_equal(self.y3, y_dec)
        ext_le = ExtLabelEncoder(missing_label=None).fit(self.y4)
        y_enc = ext_le.transform(self.y4)
        np.testing.assert_array_equal([np.nan, 1, 2, 0, np.nan], y_enc)
        y_dec = ext_le.inverse_transform(y_enc)
        np.testing.assert_array_equal(self.y4, y_dec)

        # missing_label=-1
        ext_le = ExtLabelEncoder(missing_label=-1).fit(self.y5)
        y_enc = ext_le.transform(self.y5)
        np.testing.assert_array_equal([3, np.nan, 0, 2, 1], y_enc)
        y_dec = ext_le.inverse_transform(y_enc)
        np.testing.assert_array_equal(self.y5, y_dec)

        # missing_label='nan'
        ext_le = ExtLabelEncoder(missing_label='nan').fit(self.y6)
        y_enc = ext_le.transform(self.y6)
        np.testing.assert_array_equal([1, 0, 2, np.nan], y_enc)
        y_dec = ext_le.inverse_transform(y_enc)
        np.testing.assert_array_equal(self.y6, y_dec)

        # classes=[0, 2, 5, 10], missing_label=np.nan
        cls = [0, 2, 5, 10]
        np.testing.assert_array_equal([np.nan, 1, 2, 3, np.nan],
                                      ExtLabelEncoder(
                                          classes=cls).fit_transform(self.y1))


if __name__ == '__main__':
    unittest.main()
