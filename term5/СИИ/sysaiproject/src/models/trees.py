import math
from dataclasses import dataclass
from typing import Optional, Tuple, Union

import numpy as np
import torch


ArrayLike = Union[np.ndarray, torch.Tensor]


def _to_numpy(x: ArrayLike) -> np.ndarray:
    if isinstance(x, torch.Tensor):
        return x.detach().cpu().numpy()
    if isinstance(x, np.ndarray):
        return x
    raise TypeError(f"Unsupported type {type(x)}")


def _sigmoid(z: np.ndarray) -> np.ndarray:
    return 1.0 / (1.0 + np.exp(-z))


def _softmax(logits: np.ndarray) -> np.ndarray:
    logits = logits - np.max(logits, axis=1, keepdims=True)
    exp = np.exp(logits)
    return exp / np.sum(exp, axis=1, keepdims=True)


def _majority_vote(y: np.ndarray) -> int:
    values, counts = np.unique(y, return_counts=True)
    return int(values[np.argmax(counts)])


@dataclass
class _TreeNode:
    feature_index: Optional[int] = None
    threshold: Optional[float] = None
    left: Optional["_TreeNode"] = None
    right: Optional["_TreeNode"] = None
    value: Optional[float] = None

    def is_leaf(self) -> bool:
        return self.left is None or self.right is None


class DecisionTree:

    def __init__(
        self,
        max_depth: Optional[int] = None,
        min_samples_split: int = 2,
        min_samples_leaf: int = 1,
        max_features: Optional[Union[int, float, str]] = None,
        n_thresholds: int = 20,
        classification: bool = True,
        random_state: Optional[int] = None,
    ):
        self.max_depth = max_depth
        self.min_samples_split = min_samples_split
        self.min_samples_leaf = min_samples_leaf
        self.max_features = max_features
        self.n_thresholds = n_thresholds
        self.classification = classification
        self.random_state = random_state
        self.root: Optional[_TreeNode] = None
        self.n_classes_: Optional[int] = None
        self.rng = np.random.default_rng(random_state)

    def _gini(self, y: np.ndarray) -> float:
        if y.size == 0:
            return 0.0
        _, counts = np.unique(y, return_counts=True)
        probs = counts / counts.sum()
        return 1.0 - np.sum(probs ** 2)

    def _variance(self, y: np.ndarray) -> float:
        return float(np.var(y)) if y.size else 0.0

    def _impurity(self, y: np.ndarray) -> float:
        return self._gini(y) if self.classification else self._variance(y)

    def _candidate_thresholds(self, values: np.ndarray) -> np.ndarray:
        uniq = np.unique(values)
        if uniq.size <= 1:
            return np.array([])
        if uniq.size <= self.n_thresholds:
            return (uniq[:-1] + uniq[1:]) / 2.0
        qs = np.linspace(0, 1, self.n_thresholds + 2)[1:-1]
        quantiles = np.quantile(values, qs)
        return np.unique(quantiles)

    def _best_split(self, X: np.ndarray, y: np.ndarray) -> Tuple[Optional[int], Optional[float], float]:
        n_samples, n_features = X.shape
        best_feat, best_thr, best_impurity = None, None, float("inf")

        if self.max_features is None:
            feat_count = n_features
        elif isinstance(self.max_features, int):
            feat_count = max(1, min(n_features, self.max_features))
        elif isinstance(self.max_features, float):
            feat_count = max(1, min(n_features, int(n_features * self.max_features)))
        elif isinstance(self.max_features, str):
            if self.max_features == "sqrt":
                feat_count = max(1, int(math.sqrt(n_features)))
            elif self.max_features == "log2":
                feat_count = max(1, int(math.log2(n_features)))
            else:
                raise ValueError(f"Unknown max_features: {self.max_features}")
        else:
            raise ValueError(f"Unsupported max_features type: {type(self.max_features)}")

        feature_indices = self.rng.choice(n_features, size=feat_count, replace=False)
        base_impurity = self._impurity(y)
        if base_impurity == 0.0:
            return None, None, 0.0

        for feat in feature_indices:
            thresholds = self._candidate_thresholds(X[:, feat])
            for thr in thresholds:
                left_mask = X[:, feat] <= thr
                right_mask = ~left_mask
                if left_mask.sum() < self.min_samples_leaf or right_mask.sum() < self.min_samples_leaf:
                    continue
                left_imp = self._impurity(y[left_mask])
                right_imp = self._impurity(y[right_mask])
                impurity = (left_mask.mean() * left_imp) + (right_mask.mean() * right_imp)
                if impurity < best_impurity:
                    best_feat, best_thr, best_impurity = feat, float(thr), float(impurity)
        return best_feat, best_thr, best_impurity

    def _build_tree(self, X: np.ndarray, y: np.ndarray, depth: int) -> _TreeNode:
        node = _TreeNode()
        node.value = self._leaf_value(y)

        if (
            self.max_depth is not None
            and depth >= self.max_depth
            or y.size < self.min_samples_split
            or self._impurity(y) == 0.0
        ):
            return node

        feat, thr, _ = self._best_split(X, y)
        if feat is None or thr is None:
            return node

        left_mask = X[:, feat] <= thr
        right_mask = ~left_mask
        if left_mask.sum() < self.min_samples_leaf or right_mask.sum() < self.min_samples_leaf:
            return node

        node.feature_index = feat
        node.threshold = thr
        node.left = self._build_tree(X[left_mask], y[left_mask], depth + 1)
        node.right = self._build_tree(X[right_mask], y[right_mask], depth + 1)
        return node

    def _leaf_value(self, y: np.ndarray) -> float:
        if self.classification:
            return float(_majority_vote(y))
        return float(np.mean(y)) if y.size else 0.0

    def fit(self, X: ArrayLike, y: ArrayLike):
        X_np = _to_numpy(X)
        y_np = _to_numpy(y).reshape(-1)
        self.rng = np.random.default_rng(self.random_state)
        if self.classification:
            unique = np.unique(y_np)
            self.class_map_ = {v: i for i, v in enumerate(unique)}
            inv_map = {i: v for v, i in self.class_map_.items()}
            self.inv_class_map_ = inv_map
            y_np = np.vectorize(self.class_map_.get)(y_np)
            self.n_classes_ = len(unique)
        self.root = self._build_tree(X_np, y_np, depth=0)
        return self

    def _predict_one(self, x: np.ndarray) -> float:
        node = self.root
        while node and not node.is_leaf():
            if node.feature_index is None or node.threshold is None:
                break
            if x[node.feature_index] <= node.threshold:
                node = node.left
            else:
                node = node.right
        return 0.0 if node is None or node.value is None else node.value

    def predict(self, X: ArrayLike) -> np.ndarray:
        X_np = _to_numpy(X)
        preds = np.array([self._predict_one(row) for row in X_np], dtype=float)
        if self.classification:
            preds = preds.astype(int)
            if hasattr(self, "inv_class_map_"):
                inv_vec = np.vectorize(self.inv_class_map_.get)
                preds = inv_vec(preds)
        return preds


class RandomForest:

    def __init__(
        self,
        n_estimators: int = 20,
        max_depth: Optional[int] = None,
        min_samples_split: int = 2,
        min_samples_leaf: int = 1,
        max_features: Optional[Union[int, float, str]] = "sqrt",
        n_thresholds: int = 20,
        bootstrap: bool = True,
        classification: bool = True,
        random_state: Optional[int] = None,
    ):
        self.n_estimators = n_estimators
        self.max_depth = max_depth
        self.min_samples_split = min_samples_split
        self.min_samples_leaf = min_samples_leaf
        self.max_features = max_features
        self.n_thresholds = n_thresholds
        self.bootstrap = bootstrap
        self.classification = classification
        self.random_state = random_state
        self.trees: list[DecisionTree] = []
        self.rng = np.random.default_rng(random_state)

    def fit(self, X: ArrayLike, y: ArrayLike):
        X_np = _to_numpy(X)
        y_np = _to_numpy(y).reshape(-1)
        self.trees = []
        for i in range(self.n_estimators):
            if self.bootstrap:
                idx = self.rng.choice(len(X_np), size=len(X_np), replace=True)
                X_boot, y_boot = X_np[idx], y_np[idx]
            else:
                X_boot, y_boot = X_np, y_np
            tree = DecisionTree(
                max_depth=self.max_depth,
                min_samples_split=self.min_samples_split,
                min_samples_leaf=self.min_samples_leaf,
                max_features=self.max_features,
                n_thresholds=self.n_thresholds,
                classification=self.classification,
                random_state=None if self.random_state is None else self.random_state + i,
            )
            tree.fit(X_boot, y_boot)
            self.trees.append(tree)
        return self

    def predict(self, X: ArrayLike) -> np.ndarray:
        if not self.trees:
            raise RuntimeError("Model is not fitted yet")
        preds = np.stack([t.predict(X) for t in self.trees], axis=0)
        if self.classification:
            voted = []
            for col in preds.T:
                voted.append(_majority_vote(col))
            return np.array(voted, dtype=int)
        return preds.mean(axis=0)


class GradientBoosting:

    def __init__(
        self,
        n_estimators: int = 50,
        learning_rate: float = 0.1,
        max_depth: int = 3,
        min_samples_split: int = 2,
        min_samples_leaf: int = 1,
        max_features: Optional[Union[int, float, str]] = None,
        n_thresholds: int = 15,
        subsample: float = 1.0,
        classification: bool = True,
        random_state: Optional[int] = None,
    ):
        self.n_estimators = n_estimators
        self.learning_rate = learning_rate
        self.max_depth = max_depth
        self.min_samples_split = min_samples_split
        self.min_samples_leaf = min_samples_leaf
        self.max_features = max_features
        self.n_thresholds = n_thresholds
        self.subsample = subsample
        self.classification = classification
        self.random_state = random_state
        self.trees: list = []
        self.init_pred_: float = 0.0
        self.init_logits_: Optional[np.ndarray] = None
        self.n_classes_: Optional[int] = None
        self.rng = np.random.default_rng(random_state)

    def _sample_indices(self, n_samples: int) -> np.ndarray:
        if self.subsample >= 1.0:
            return np.arange(n_samples)
        k = max(1, int(n_samples * self.subsample))
        return self.rng.choice(n_samples, size=k, replace=False)

    def fit(self, X: ArrayLike, y: ArrayLike):
        X_np = _to_numpy(X)
        y_np = _to_numpy(y).reshape(-1)
        n_samples = len(y_np)
        self.trees = []
        self.init_logits_ = None
        self.n_classes_ = None

        if self.classification:
            classes = np.unique(y_np.astype(int))
            if classes.size < 2:
                raise ValueError("Need at least 2 classes for classification")
            if classes.min() < 0 or not np.array_equal(classes, np.arange(classes.size)):
                raise ValueError("GradientBoosting expects classification labels 0..K-1")
            self.n_classes_ = int(classes.size)

            if self.n_classes_ == 2:
                y_bin = y_np.astype(float)
                p = np.clip(y_bin.mean(), 1e-6, 1 - 1e-6)
                self.init_pred_ = float(math.log(p / (1 - p)))
                logit = np.full(n_samples, self.init_pred_, dtype=float)
                for m in range(self.n_estimators):
                    prob = _sigmoid(logit)
                    residual = y_bin - prob
                    idx = self._sample_indices(n_samples)
                    tree = DecisionTree(
                        max_depth=self.max_depth,
                        min_samples_split=self.min_samples_split,
                        min_samples_leaf=self.min_samples_leaf,
                        max_features=self.max_features,
                        n_thresholds=self.n_thresholds,
                        classification=False,
                        random_state=None if self.random_state is None else self.random_state + m,
                    )
                    tree.fit(X_np[idx], residual[idx])
                    update = tree.predict(X_np).astype(float)
                    logit += self.learning_rate * update
                    self.trees.append(tree)
            else:
                counts = np.bincount(y_np.astype(int), minlength=self.n_classes_).astype(float)
                priors = np.clip(counts / counts.sum(), 1e-12, 1.0)
                self.init_logits_ = np.log(priors)
                logits = np.repeat(self.init_logits_[None, :], n_samples, axis=0).astype(float)

                Y = np.eye(self.n_classes_, dtype=float)[y_np.astype(int)]
                for m in range(self.n_estimators):
                    prob = _softmax(logits)
                    residual = Y - prob
                    idx = self._sample_indices(n_samples)
                    stage_trees: list[DecisionTree] = []
                    for k in range(self.n_classes_):
                        tree_k = DecisionTree(
                            max_depth=self.max_depth,
                            min_samples_split=self.min_samples_split,
                            min_samples_leaf=self.min_samples_leaf,
                            max_features=self.max_features,
                            n_thresholds=self.n_thresholds,
                            classification=False,
                            random_state=None
                            if self.random_state is None
                            else self.random_state + m * self.n_classes_ + k,
                        )
                        tree_k.fit(X_np[idx], residual[idx, k])
                        logits[:, k] += self.learning_rate * tree_k.predict(X_np).astype(float)
                        stage_trees.append(tree_k)
                    self.trees.append(stage_trees)
        else:
            self.init_pred_ = float(np.mean(y_np))
            pred = np.full(n_samples, self.init_pred_, dtype=float)
            for m in range(self.n_estimators):
                residual = y_np - pred
                idx = self._sample_indices(n_samples)
                tree = DecisionTree(
                    max_depth=self.max_depth,
                    min_samples_split=self.min_samples_split,
                    min_samples_leaf=self.min_samples_leaf,
                    max_features=self.max_features,
                    n_thresholds=self.n_thresholds,
                    classification=False,
                    random_state=None if self.random_state is None else self.random_state + m,
                )
                tree.fit(X_np[idx], residual[idx])
                update = tree.predict(X_np)
                pred += self.learning_rate * update
                self.trees.append(tree)
        return self

    def predict_proba(self, X: ArrayLike) -> np.ndarray:
        if not self.classification:
            raise RuntimeError("predict_proba is available only for classification mode")
        X_np = _to_numpy(X)
        if self.n_classes_ is None:
            self.n_classes_ = 2

        if self.n_classes_ == 2:
            logit = np.full(len(X_np), self.init_pred_, dtype=float)
            for tree in self.trees:
                logit += self.learning_rate * tree.predict(X_np)
            prob = _sigmoid(logit)
            return np.stack([1 - prob, prob], axis=1)

        if self.init_logits_ is None:
            raise RuntimeError("Model is not fitted yet")
        logits = np.repeat(self.init_logits_[None, :], len(X_np), axis=0).astype(float)
        for stage_trees in self.trees:
            for k, tree_k in enumerate(stage_trees):
                logits[:, k] += self.learning_rate * tree_k.predict(X_np).astype(float)
        return _softmax(logits)

    def predict(self, X: ArrayLike) -> np.ndarray:
        if self.classification:
            proba = self.predict_proba(X)
            if proba.shape[1] == 2:
                return (proba[:, 1] >= 0.5).astype(int)
            return np.argmax(proba, axis=1).astype(int)
        X_np = _to_numpy(X)
        pred = np.full(len(X_np), self.init_pred_, dtype=float)
        for tree in self.trees:
            pred += self.learning_rate * tree.predict(X_np)
        return pred
