import math
from typing import Optional

import numpy as np
import torch


class Linear:
    def __init__(self, in_features: int, out_features: int):
        if in_features <= 0 or out_features <= 0:
            raise ValueError("in_features and out_features must be positive")

        self.W = torch.randn(out_features, in_features, dtype=torch.float32) * math.sqrt(2.0 / in_features)
        self.b = torch.zeros(out_features, dtype=torch.float32)
        self.W.grad = None
        self.b.grad = None

        self._x: Optional[torch.Tensor] = None

    def forward(self, x: torch.Tensor, train: bool = True) -> torch.Tensor:
        self._x = x
        return x @ self.W.T + self.b

    def backward(self, grad: torch.Tensor) -> torch.Tensor:
        if self._x is None:
            raise RuntimeError("Linear.backward called before forward")

        self.W.grad = grad.T @ self._x
        self.b.grad = grad.sum(dim=0)
        return grad @ self.W


class ReLU:
    def __init__(self):
        self._mask: Optional[torch.Tensor] = None

    def forward(self, x: torch.Tensor, train: bool = True) -> torch.Tensor:
        self._mask = (x > 0).to(x.dtype)
        return x * self._mask

    def backward(self, grad: torch.Tensor) -> torch.Tensor:
        if self._mask is None:
            raise RuntimeError("ReLU.backward called before forward")
        return grad * self._mask


class Flatten:
    def __init__(self):
        self._shape: Optional[torch.Size] = None

    def forward(self, x: torch.Tensor, train: bool = True) -> torch.Tensor:
        self._shape = x.shape
        return x.reshape(x.shape[0], -1)

    def backward(self, grad: torch.Tensor) -> torch.Tensor:
        if self._shape is None:
            raise RuntimeError("Flatten.backward called before forward")
        return grad.reshape(self._shape)


class Dropout:
    def __init__(self, p: float = 0.5):
        if not (0.0 <= p < 1.0):
            raise ValueError("p must be in [0, 1)")
        self.p = float(p)
        self._mask: Optional[torch.Tensor] = None

    def forward(self, x: torch.Tensor, train: bool = True) -> torch.Tensor:
        if (not train) or self.p == 0.0:
            self._mask = None
            return x
        keep_prob = 1.0 - self.p
        self._mask = (torch.rand_like(x) < keep_prob).to(x.dtype)
        return x * self._mask / keep_prob

    def backward(self, grad: torch.Tensor) -> torch.Tensor:
        if self._mask is None:
            return grad
        keep_prob = 1.0 - self.p
        return grad * self._mask / keep_prob


def softmax(logits: torch.Tensor) -> torch.Tensor:
    ex = torch.exp(logits - logits.max(dim=1, keepdim=True).values)
    return ex / ex.sum(dim=1, keepdim=True)


def softmax_crossentropy(logits: torch.Tensor, y: torch.Tensor) -> tuple[torch.Tensor, torch.Tensor]:
    probs = softmax(logits)
    idx = torch.arange(y.shape[0], device=y.device)
    loss = -torch.log(probs[idx, y] + 1e-12).mean()
    return probs, loss


def grad_softmax_crossentropy(probs: torch.Tensor, y: torch.Tensor) -> torch.Tensor:
    grad = probs.clone()
    idx = torch.arange(y.shape[0], device=y.device)
    grad[idx, y] -= 1.0
    return grad / y.shape[0]


class Adam:
    def __init__(self, params: list[torch.Tensor], lr: float = 1e-3, betas: tuple[float, float] = (0.9, 0.999), eps: float = 1e-8):
        if lr <= 0:
            raise ValueError("lr must be > 0")
        self.params = params
        self.lr = float(lr)
        self.b1, self.b2 = betas
        self.eps = float(eps)

        self.m = [torch.zeros_like(p) for p in params]
        self.v = [torch.zeros_like(p) for p in params]
        self.t = 0

    def step(self) -> None:
        self.t += 1
        for i, p in enumerate(self.params):
            g = getattr(p, "grad", None)
            if g is None:
                continue

            self.m[i] = self.b1 * self.m[i] + (1 - self.b1) * g
            self.v[i] = self.b2 * self.v[i] + (1 - self.b2) * (g * g)

            m_hat = self.m[i] / (1 - self.b1**self.t)
            v_hat = self.v[i] / (1 - self.b2**self.t)

            with torch.no_grad():
                p -= self.lr * m_hat / (torch.sqrt(v_hat) + self.eps)
                p.grad = None


class ManualClassifier:
    def __init__(self, layers: list):
        self.layers = layers
        self.history: dict[str, list[float]] = {
            "train_loss": [],
            "val_loss": [],
            "train_acc": [],
            "val_acc": [],
            "train_auc": [],
            "val_auc": [],
        }

    def parameters(self) -> list[torch.Tensor]:
        params: list[torch.Tensor] = []
        for layer in self.layers:
            for name in ("W", "b"):
                if hasattr(layer, name):
                    params.append(getattr(layer, name))
        return params

    def forward(self, x: torch.Tensor, train: bool = True) -> torch.Tensor:
        out = x
        for layer in self.layers:
            out = layer.forward(out, train=train)
        return out

    def backward(self, grad: torch.Tensor) -> torch.Tensor:
        out = grad
        for layer in reversed(self.layers):
            out = layer.backward(out)
        return out

    def predict_logits(self, X: torch.Tensor) -> torch.Tensor:
        return self.forward(X, train=False)

    def predict(self, X: torch.Tensor) -> torch.Tensor:
        return self.predict_logits(X).argmax(dim=1)

    def predict_proba(self, X: torch.Tensor) -> torch.Tensor:
        return softmax(self.predict_logits(X))

    def state_dict(self) -> dict:
        layers_state: list[dict] = []
        for layer in self.layers:
            layer_state: dict = {"type": layer.__class__.__name__}
            for name in ("W", "b", "p"):
                if hasattr(layer, name):
                    val = getattr(layer, name)
                    layer_state[name] = val.detach().cpu() if torch.is_tensor(val) else val
            layers_state.append(layer_state)
        return {"layers": layers_state}

    def load_state_dict(self, state: dict, device: str = "cpu") -> None:
        layers_state = state.get("layers", [])
        if len(layers_state) != len(self.layers):
            raise ValueError("state_dict layers count mismatch")
        for layer, layer_state in zip(self.layers, layers_state):
            for name in ("W", "b"):
                if hasattr(layer, name):
                    tensor = torch.as_tensor(layer_state[name], dtype=torch.float32, device=device)
                    setattr(layer, name, tensor)
                    getattr(layer, name).grad = None
            if hasattr(layer, "p") and "p" in layer_state:
                layer.p = float(layer_state["p"])

    def fit(
        self,
        X_train: torch.Tensor,
        y_train: torch.Tensor,
        X_val: Optional[torch.Tensor] = None,
        y_val: Optional[torch.Tensor] = None,
        *,
        epochs: int = 5,
        batch_size: int = 128,
        lr: float = 1e-3,
        device: str = "cpu",
        train_metrics_subset: int = 10000,
        compute_auc: bool = False,
        auc_n_classes: int = 10,
        auc_subset: int = 5000,
        seed: int = 42,
        verbose: bool = True,
    ) -> dict[str, list[float]]:
        torch.manual_seed(seed)

        def _roc_curve_binary(y_true: np.ndarray, y_score: np.ndarray) -> tuple[np.ndarray, np.ndarray]:
            y_true = y_true.astype(np.int32).reshape(-1)
            y_score = y_score.astype(np.float64).reshape(-1)
            if y_true.size == 0:
                return np.array([0.0, 1.0]), np.array([0.0, 1.0])

            order = np.argsort(-y_score, kind="mergesort")
            y_true = y_true[order]
            y_score = y_score[order]

            distinct_value_idx = np.where(np.diff(y_score))[0]
            threshold_idx = np.r_[distinct_value_idx, y_true.size - 1]

            tps = np.cumsum(y_true)[threshold_idx]
            fps = 1 + threshold_idx - tps

            tps = np.r_[0, tps]
            fps = np.r_[0, fps]

            P = float(tps[-1])
            N = float(fps[-1])
            tpr = tps / P if P > 0 else np.zeros_like(tps, dtype=np.float64)
            fpr = fps / N if N > 0 else np.zeros_like(fps, dtype=np.float64)
            return fpr, tpr

        def _auc(x: np.ndarray, y: np.ndarray) -> float:
            return float(np.trapezoid(y, x))

        def _macro_auc_ovr(y_true_np: np.ndarray, probas_np: np.ndarray, n_classes: int) -> float:
            aucs = []
            for c in range(n_classes):
                y_bin = (y_true_np == c).astype(np.int32)
                fpr, tpr = _roc_curve_binary(y_bin, probas_np[:, c])
                aucs.append(_auc(fpr, tpr))
            return float(np.mean(aucs)) if aucs else 0.0

        X_train = X_train.to(device=device, dtype=torch.float32)
        y_train = y_train.to(device=device, dtype=torch.long).view(-1)
        if X_val is not None and y_val is not None:
            X_val = X_val.to(device=device, dtype=torch.float32)
            y_val = y_val.to(device=device, dtype=torch.long).view(-1)

        for layer in self.layers:
            for name in ("W", "b"):
                if hasattr(layer, name):
                    t = getattr(layer, name)
                    setattr(layer, name, t.to(device))
                    getattr(layer, name).grad = None

        opt = Adam(self.parameters(), lr=lr)
        n = int(X_train.shape[0])

        for epoch in range(1, epochs + 1):
            perm = torch.randperm(n, device=device)
            total_loss = 0.0
            n_batches = 0

            for start in range(0, n, batch_size):
                idx = perm[start : start + batch_size]
                xb = X_train[idx]
                yb = y_train[idx]

                logits = self.forward(xb, train=True)
                probs, loss = softmax_crossentropy(logits, yb)
                grad = grad_softmax_crossentropy(probs, yb)
                self.backward(grad)
                opt.step()

                total_loss += float(loss.item())
                n_batches += 1

            self.history["train_loss"].append(total_loss / max(n_batches, 1))

            with torch.no_grad():
                if train_metrics_subset and train_metrics_subset < n:
                    sub_idx = perm[:train_metrics_subset]
                    train_pred = self.predict(X_train[sub_idx])
                    train_acc = float((train_pred == y_train[sub_idx]).float().mean().item())
                else:
                    train_pred = self.predict(X_train)
                    train_acc = float((train_pred == y_train).float().mean().item())
            self.history["train_acc"].append(train_acc)

            if X_val is not None and y_val is not None:
                with torch.no_grad():
                    val_logits = self.forward(X_val, train=False)
                    _, val_loss = softmax_crossentropy(val_logits, y_val)
                    val_pred = val_logits.argmax(dim=1)
                    val_acc = float((val_pred == y_val).float().mean().item())
                self.history["val_loss"].append(float(val_loss.item()))
                self.history["val_acc"].append(val_acc)

            if compute_auc:
                with torch.no_grad():
                    if auc_subset and auc_subset < n:
                        auc_idx = perm[:auc_subset]
                        y_auc = y_train[auc_idx].detach().cpu().numpy()
                        p_auc = self.predict_proba(X_train[auc_idx]).detach().cpu().numpy()
                    else:
                        y_auc = y_train.detach().cpu().numpy()
                        p_auc = self.predict_proba(X_train).detach().cpu().numpy()
                    train_auc = _macro_auc_ovr(y_auc, p_auc, auc_n_classes)
                    self.history["train_auc"].append(train_auc)

                    if X_val is not None and y_val is not None:
                        yv = y_val.detach().cpu().numpy()
                        pv = self.predict_proba(X_val).detach().cpu().numpy()
                        val_auc = _macro_auc_ovr(yv, pv, auc_n_classes)
                        self.history["val_auc"].append(val_auc)

            if verbose:
                msg = f"Epoch {epoch}: train_loss={self.history['train_loss'][-1]:.4f}, train_acc={train_acc:.4f}"
                if self.history["val_loss"]:
                    msg += f", val_loss={self.history['val_loss'][-1]:.4f}, val_acc={self.history['val_acc'][-1]:.4f}"
                if self.history["train_auc"]:
                    msg += f", train_auc={self.history['train_auc'][-1]:.4f}"
                if self.history["val_auc"]:
                    msg += f", val_auc={self.history['val_auc'][-1]:.4f}"
                print(msg)

        return self.history
