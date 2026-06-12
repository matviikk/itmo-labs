from typing import Optional

import torch

from src.metrics.classification_metrics import ClassificationMetrics
from src.models.base_model import BaseModel


class SVM(BaseModel):

    def __init__(self, learning_rate: float = 1e-3, max_epochs: int = 100, batch_size: int = 1024, C: float = 1.0, device: str = "cpu"):
        super().__init__(learning_rate, max_epochs, device)
        self.batch_size = int(batch_size)
        self.C = float(C)
        self.threshold = 0.0

    def _hinge_loss(self, X: torch.Tensor, y_pm1: torch.Tensor) -> torch.Tensor:
        scores = X @ self.weights + self.bias
        hinge_mean = ClassificationMetrics.hinge_loss(y_pm1, scores)
        reg = 0.5 * torch.sum(self.weights ** 2)
        return reg + self.C * hinge_mean

    def fit(self, X: torch.Tensor, y01: torch.Tensor, X_val: Optional[torch.Tensor] = None, y_val: Optional[torch.Tensor] = None):
        N, D = X.shape
        if self.weights is None or self.bias is None:
            self.weights = torch.zeros(D, device=self.device)
            self.bias = torch.zeros(1, device=self.device)

        X = X.to(self.device).float()
        y_pm1 = torch.where(y01 == 0, -1.0, 1.0).to(self.device).float()

        for epoch in range(self.max_epochs):
            perm = torch.randperm(N, device=self.device)
            total_loss = 0.0

            for i in range(0, N, self.batch_size):
                idx = perm[i : i + self.batch_size]
                Xb, yb = X[idx], y_pm1[idx]

                scores = Xb @ self.weights + self.bias
                margins = 1.0 - yb * scores
                mask = margins > 0

                B = max(1, Xb.shape[0])
                grad_w = self.weights - self.C * (Xb[mask].T @ yb[mask]) / B
                grad_b = -self.C * yb[mask].sum() / B

                self.weights -= self.learning_rate * grad_w
                self.bias -= self.learning_rate * grad_b

                total_loss += self._hinge_loss(Xb, yb).item()

            avg_loss = total_loss / max(1, (N + self.batch_size - 1) // self.batch_size)
            self.history["train_loss"].append(avg_loss)

            train_metrics = self.score(X, y01)
            self.history["train_metrics"].append(train_metrics)

            val_metrics = None
            if X_val is not None and y_val is not None:
                val_metrics = self.score(X_val, y_val)
                self.history["val_metrics"].append(val_metrics)

            if (epoch % 10 == 0) or (epoch == self.max_epochs - 1):
                msg = (
                    f"Epoch {epoch+1}/{self.max_epochs} | "
                    f"loss={avg_loss:.4f} | "
                    f"train acc={float(train_metrics['accuracy']):.4f} "
                    f"f1={float(train_metrics['f1_score']):.4f}"
                )
                if val_metrics is not None:
                    msg += (
                        f" | val acc={float(val_metrics['accuracy']):.4f} "
                        f"f1={float(val_metrics['f1_score']):.4f}"
                    )
                print(msg)

    def decision_function(self, X: torch.Tensor) -> torch.Tensor:
        X = X.to(self.device).float()
        return X @ self.weights + self.bias

    def predict(self, X: torch.Tensor) -> torch.Tensor:
        scores = self.decision_function(X)
        return (scores > self.threshold).float()

    def score(self, X: torch.Tensor, y01: torch.Tensor) -> dict:
        X = X.to(self.device).float()
        y01 = y01.to(self.device).float()
        scores = self.decision_function(X)
        y_pred = (scores > self.threshold).float()
        y_pm1 = torch.where(y01 == 0, -1.0, 1.0)
        hinge = ClassificationMetrics.hinge_loss(y_pm1, scores)
        acc = ClassificationMetrics.accuracy(y01, y_pred)
        f1 = ClassificationMetrics.f1(y01, y_pred)
        return {"hinge_loss": hinge, "accuracy": acc, "f1_score": f1}


# Для совместимости с предыдущим именем
LinearSVM = SVM
