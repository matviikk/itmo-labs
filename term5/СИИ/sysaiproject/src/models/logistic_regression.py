from typing import Optional

import torch

from src.metrics.classification_metrics import binary_accuracy, binary_f1, binary_precision, binary_recall


class LogisticRegression:
    def __init__(
        self,
        learning_rate: float = 1e-3,
        max_epochs: int = 200,
        device: str = "cpu",
        reg_type: Optional[str] = None,
        l1_lambda: float = 0.0,
        l2_lambda: float = 0.0,
        alpha: float = 0.5,
        n_features: Optional[int] = None,  # сохраняем для совместимости, не используется
    ):
        if reg_type not in (None, "l1", "l2", "elasticnet"):
            raise ValueError("reg_type must be None, 'l1', 'l2' or 'elasticnet'")
        self.lr = learning_rate
        self.max_epochs = max_epochs
        self.device = device
        self.reg_type = reg_type
        self.l1_lambda = l1_lambda
        self.l2_lambda = l2_lambda
        self.alpha = alpha
        self.weights: Optional[torch.Tensor] = None
        self.bias: Optional[torch.Tensor] = None
        self.history: dict[str, list] = {
            "train_loss": [],
            "val_metrics": [],
            "train_loss_data": [],
            "val_loss_data": [],
            "val_acc": [],
            "val_f1": [],
        }

    def _init_params(self, n_features: int):
        self.weights = 0.01 * torch.randn((n_features, 1), device=self.device, dtype=torch.float32)
        self.bias = torch.zeros(1, device=self.device, dtype=torch.float32)

    def _forward(self, X: torch.Tensor) -> torch.Tensor:
        return torch.sigmoid(X @ self.weights + self.bias)

    def _bce(self, prob: torch.Tensor, y: torch.Tensor) -> torch.Tensor:
        y = y.view(-1, 1)
        eps = 1e-8
        return (-(y * torch.log(prob + eps) + (1 - y) * torch.log(1 - prob + eps))).mean()

    def _reg_loss_and_grad(self):
        if self.reg_type is None:
            zeros = torch.zeros_like(self.weights)
            return torch.tensor(0.0, device=self.device), zeros

        reg_grad = torch.zeros_like(self.weights)
        reg_loss = torch.tensor(0.0, device=self.device)

        if self.reg_type == "l1":
            reg_loss = self.l1_lambda * self.weights.abs().sum()
            reg_grad = self.l1_lambda * torch.sign(self.weights)
        elif self.reg_type == "l2":
            reg_loss = 0.5 * self.l2_lambda * (self.weights ** 2).sum()
            reg_grad = self.l2_lambda * self.weights
        elif self.reg_type == "elasticnet":
            l1_part = self.alpha * self.l1_lambda * self.weights.abs().sum()
            l2_part = 0.5 * (1 - self.alpha) * self.l2_lambda * (self.weights ** 2).sum()
            reg_loss = l1_part + l2_part
            reg_grad = self.alpha * self.l1_lambda * torch.sign(self.weights) + (1 - self.alpha) * self.l2_lambda * self.weights
        return reg_loss, reg_grad

    def fit(
        self,
        X_train: torch.Tensor,
        y_train: torch.Tensor,
        X_val: Optional[torch.Tensor] = None,
        y_val: Optional[torch.Tensor] = None,
    ):
        X_train = X_train.to(self.device, dtype=torch.float32)
        y_train = y_train.to(self.device, dtype=torch.float32).view(-1, 1)
        if X_val is not None and y_val is not None:
            X_val = X_val.to(self.device, dtype=torch.float32)
            y_val = y_val.to(self.device, dtype=torch.float32).view(-1, 1)

        self._init_params(X_train.shape[1])
        N = X_train.shape[0]

        for epoch in range(1, self.max_epochs + 1):
            prob = self._forward(X_train)
            base_loss = self._bce(prob, y_train)
            reg_loss, reg_grad = self._reg_loss_and_grad()
            # масштабируем вклад регуляризации относительно размера батча
            reg_loss = reg_loss / N
            reg_grad = reg_grad / N
            loss = base_loss + reg_loss

            error = prob - y_train
            grad_W = (X_train.T @ error) / N + reg_grad
            grad_b = error.mean()

            self.weights -= self.lr * grad_W
            self.bias -= self.lr * grad_b

            self.history["train_loss"].append(float(loss.item()))
            self.history["train_loss_data"].append(float(base_loss.item()))

            if X_val is not None and y_val is not None:
                with torch.no_grad():
                    p_val = self._forward(X_val)
                    val_base = self._bce(p_val, y_val)
                    val_loss = float((val_base + reg_loss).item())
                    val_acc = binary_accuracy(p_val, y_val)
                    val_prec = binary_precision(p_val, y_val)
                    val_rec = binary_recall(p_val, y_val)
                    val_f1 = binary_f1(p_val, y_val)
                    self.history["val_metrics"].append(
                        {
                            "loss": val_loss,
                            "accuracy": val_acc,
                            "precision": val_prec,
                            "recall": val_rec,
                            "f1_score": val_f1,
                        }
                    )
                    self.history["val_loss_data"].append(float(val_base.item()))
                    self.history["val_acc"].append(val_acc)
                    self.history["val_f1"].append(val_f1)

    def predict_proba(self, X: torch.Tensor) -> torch.Tensor:
        X = X.to(self.device, dtype=torch.float32)
        with torch.no_grad():
            return self._forward(X).view(-1, 1)

    def predict(self, X: torch.Tensor, thresh: float = 0.5) -> torch.Tensor:
        proba = self.predict_proba(X)
        return (proba >= thresh).float().view(-1)

    def score(self, X: torch.Tensor, y: torch.Tensor, thresh: float = 0.5) -> dict[str, float]:
        proba = self.predict_proba(X)
        y = y.to(proba.device).view(-1, 1)
        acc = binary_accuracy(proba, y, thresh)
        prec = binary_precision(proba, y, thresh)
        rec = binary_recall(proba, y, thresh)
        f1 = binary_f1(proba, y, thresh)
        loss = float(self._bce(proba, y).item())
        return {"loss": loss, "accuracy": acc, "precision": prec, "recall": rec, "f1_score": f1}


# Для обратной совместимости со старым именем
LogisticRegressionGD = LogisticRegression
