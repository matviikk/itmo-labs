import torch
from typing import Optional


class KNN:
    """KNN для классификации/регрессии с L1/L2 расстояниями и весами."""

    def __init__(
        self,
        task: str = "classification",
        k: int = 5,
        p: int = 2,
        weights: str = "uniform",
        device: str = "cpu",
        chunk_size: int = 2048,
    ):
        if task not in ("classification", "regression"):
            raise ValueError("task must be 'classification' or 'regression'")
        if weights not in ("uniform", "distance"):
            raise ValueError("weights must be 'uniform' or 'distance'")
        if p not in (1, 2):
            raise ValueError("p must be 1 or 2")

        self.task = task
        self.k = k
        self.p = p
        self.weights = weights
        self.device = device
        self.chunk_size = chunk_size

        self.X: Optional[torch.Tensor] = None
        self.y: Optional[torch.Tensor] = None
        self.classes_: Optional[torch.Tensor] = None

    def fit(self, X: torch.Tensor, y: torch.Tensor):
        self.X = X.to(self.device, dtype=torch.float32, non_blocking=True)

        if self.task == "classification":
            classes, inverse = torch.unique(y, sorted=True, return_inverse=True)
            self.classes_ = classes.to(self.device)
            self.y = inverse.to(self.device, dtype=torch.long)
        else:
            self.y = y.to(self.device, dtype=torch.float32)
        return self

    @torch.no_grad()
    def predict(self, Xq: torch.Tensor, return_prob: bool = False, batch_size: Optional[int] = None):
        if self.X is None or self.y is None:
            raise RuntimeError("Call fit before predict.")

        Xq = Xq.to(self.device, dtype=torch.float32, non_blocking=True)
        step = batch_size or self.chunk_size

        preds: list[torch.Tensor] = []
        prob_parts: Optional[list[torch.Tensor]] = [] if (self.task == "classification" and return_prob) else None

        for start in range(0, Xq.shape[0], step):
            end = start + step
            y_block, prob_block = self._predict_chunk(Xq[start:end], return_prob)
            preds.append(y_block)
            if prob_parts is not None and prob_block is not None:
                prob_parts.append(prob_block)

        y_hat_idx = torch.cat(preds, dim=0)
        if self.task == "classification":
            labels = self.classes_[y_hat_idx].detach().cpu()
            if prob_parts:
                probs = torch.cat(prob_parts, dim=0).detach().cpu()
                return labels, probs
            return labels
        return y_hat_idx.detach().cpu()

    @torch.no_grad()
    def _predict_chunk(self, Xq: torch.Tensor, return_prob: bool):
        dists = torch.cdist(Xq, self.X, p=self.p)
        dk, idx = torch.topk(dists, k=self.k, largest=False)
        y_neighbors = self.y[idx]

        weights = torch.ones_like(dk) if self.weights == "uniform" else 1.0 / (dk + 1e-12)

        if self.task == "classification":
            num_classes = int(self.y.max().item()) + 1
            scores = torch.zeros((Xq.shape[0], num_classes), device=self.device, dtype=torch.float32)
            scores.scatter_add_(1, y_neighbors, weights)
            y_hat_idx = scores.argmax(dim=1)
            if return_prob:
                probs = scores / (scores.sum(dim=1, keepdim=True) + 1e-12)
                return y_hat_idx, probs
            return y_hat_idx, None

        num = (weights * y_neighbors.to(torch.float32)).sum(dim=1)
        den = weights.sum(dim=1) + 1e-12
        y_hat = num / den
        return y_hat, None

    @torch.no_grad()
    def score(self, Xq: torch.Tensor, y_true: torch.Tensor):
        if self.task == "classification":
            y_pred = self.predict(Xq)
            return (y_pred == y_true.cpu()).float().mean().item()
        y_pred = self.predict(Xq).float()
        rmse = torch.sqrt(torch.mean((y_pred - y_true.float()) ** 2) + 1e-12).item()
        return {"rmse": rmse}
