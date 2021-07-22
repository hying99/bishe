import unittest

import numpy as np
from numpy.random import RandomState

from skactiveml.pool import RandomSampler


class TestRandomSampler(unittest.TestCase):

    def setUp(self):
        self.X_cand = np.zeros((100, 2))

    def test_init_param_random_state(self):
        rs = RandomSampler(random_state='string')
        self.assertTrue(hasattr(rs, 'random_state'))
        self.assertRaises(ValueError, rs.query, X_cand=self.X_cand)

    def test_query_param_X_cand(self):
        rand = RandomSampler()
        self.assertRaises(ValueError, rand.query, X_cand=None)

    def test_query_param_batch_size(self):
        rand = RandomSampler()
        self.assertRaises(TypeError, rand.query, X_cand=self.X_cand,
                          batch_size=1.2)
        self.assertRaises(ValueError, rand.query, X_cand=self.X_cand,
                          batch_size=0)

    def test_query_param_return_utilities(self):
        rand = RandomSampler()
        self.assertRaises(TypeError, rand.query, X_cand=self.X_cand,
                          return_utilities=None)
        self.assertRaises(TypeError, rand.query, X_cand=self.X_cand,
                          return_utilities=[])
        self.assertRaises(TypeError, rand.query, X_cand=self.X_cand,
                          return_utilities=0)

    def test_query(self):
        rand1 = RandomSampler(random_state=RandomState(14))
        rand2 = RandomSampler(random_state=14)

        self.assertEqual(rand1.query(self.X_cand), rand1.query(self.X_cand))
        self.assertEqual(rand1.query(self.X_cand), rand2.query(self.X_cand))

        qidx = rand1.query(self.X_cand)
        self.assertEqual(len(qidx), 1)
        qidx, u = rand1.query(self.X_cand, batch_size=5, return_utilities=True)
        self.assertEqual(len(qidx), 5)
        self.assertEqual(len(u), 5, msg='utility score should have shape (5xN)')
        self.assertEqual(len(u[0]), len(self.X_cand),
                         msg='utility score should have shape (5xN)')

