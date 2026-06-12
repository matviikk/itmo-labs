from typing import Optional

import torch

from src.metrics.regression_metrics import mae, mse, rmse


class LinearRegression:
    def __init__(
        self,
        n_features,
        lr=5e-2,
        seed=42,
        reg_type: Optional[str] = None,
        reg_lambda: float = 0.0,
        elastic_alpha: float = 0.5,
    ):
        torch.manual_seed(seed)
        self.W = 0.01 * torch.randn(n_features, 1)
        self.b = torch.zeros(1)
        self.lr = lr
        self.reg_type = reg_type
        self.reg_lambda = reg_lambda
        self.elastic_alpha = elastic_alpha
        self.history = {
            "train_mse": [],
            "val_mse": [],
            "train_rmse": [],
            "val_rmse": [],
            "train_mae": [],
            "val_mae": [],
        }

    def _forward(self, X):
        return X @ self.W + self.b

    def _reg_loss_and_grad(self):
        if not self.reg_type or self.reg_lambda <= 0:
            zeros = torch.zeros_like(self.W)
            return torch.tensor(0.0), zeros

        if self.reg_type == "l2":
            reg_loss = 0.5 * self.reg_lambda * (self.W ** 2).sum()
            reg_grad_W = self.reg_lambda * self.W
        elif self.reg_type == "l1":
            reg_loss = self.reg_lambda * self.W.abs().sum()
            reg_grad_W = self.reg_lambda * torch.sign(self.W)
        elif self.reg_type == "elasticnet":
            l1_part = self.elastic_alpha * self.W.abs().sum()
            l2_part = 0.5 * (1 - self.elastic_alpha) * (self.W ** 2).sum()
            reg_loss = self.reg_lambda * (l1_part + l2_part)
            reg_grad_W = self.reg_lambda * (
                self.elastic_alpha * torch.sign(self.W) + (1 - self.elastic_alpha) * self.W
            )
        else:
            raise ValueError(f"Unknown reg_type: {self.reg_type}")
        return reg_loss, reg_grad_W

    def fit(
        self,
        X_train,
        y_train,
        X_val=None,
        y_val=None,
        epochs=300,
        print_every=50,
    ):
        N = X_train.shape[0]
        for epoch in range(1, epochs + 1):
            preds = self._forward(X_train)
            err = preds - y_train
            loss = mse(preds, y_train)
            reg_loss, reg_grad_W = self._reg_loss_and_grad()
            # масштабируем вклад регуляризации относительно размера батча
            reg_loss = reg_loss / N
            reg_grad_W = reg_grad_W / N
            total_loss = loss + reg_loss

            grad_W = (2.0 / N) * (X_train.T @ err)
            grad_b = (2.0 / N) * err.sum()

            grad_W += reg_grad_W

            self.W -= self.lr * grad_W
            self.b -= self.lr * grad_b

            self.history["train_mse"].append(float(total_loss.item()))
            self.history["train_rmse"].append(float(rmse(preds, y_train).item()))
            self.history["train_mae"].append(float(mae(preds, y_train).item()))

            if X_val is not None and y_val is not None:
                with torch.no_grad():
                    val_preds = self._forward(X_val)
                    val_loss = mse(val_preds, y_val) + reg_loss
                    self.history["val_mse"].append(float(val_loss.item()))
                    self.history["val_rmse"].append(float(rmse(val_preds, y_val).item()))
                    self.history["val_mae"].append(float(mae(val_preds, y_val).item()))

            if epoch % print_every == 0 or epoch in (1, epochs):
                msg = (
                    f"[epoch {epoch:3d}] "
                    f"train_mse={self.history['train_mse'][-1]:.1f} "
                    f"rmse={self.history['train_rmse'][-1]:.1f} "
                    f"mae={self.history['train_mae'][-1]:.1f}"
                )
                if X_val is not None and y_val is not None:
                    msg += (
                        f" | val_mse={self.history['val_mse'][-1]:.1f} "
                        f"rmse={self.history['val_rmse'][-1]:.1f} "
                        f"mae={self.history['val_mae'][-1]:.1f}"
                    )
                print(msg)

    def evaluate(self, X, y):
        with torch.no_grad():
            preds = self._forward(X)
            reg_loss, _ = self._reg_loss_and_grad()
        return {
            "mse": float((mse(preds, y) + reg_loss).item()),
            "rmse": float(rmse(preds, y).item()),
            "mae": float(mae(preds, y).item()),
        }


# Обратная совместимость
LinearRegressionGD = LinearRegression
