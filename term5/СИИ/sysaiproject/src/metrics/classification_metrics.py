import torch
from typing import Optional


def binary_accuracy(prob: torch.Tensor, y: torch.Tensor, thresh: float = 0.5) -> float:
    y = y.view(-1, 1)
    preds = (prob >= thresh).float()
    return float((preds == y).float().mean().item())


def binary_f1(prob: torch.Tensor, y: torch.Tensor, thresh: float = 0.5) -> float:
    y = y.view(-1, 1)
    preds = (prob >= thresh).float()
    tp = ((preds == 1) & (y == 1)).sum().float()
    fp = ((preds == 1) & (y == 0)).sum().float()
    fn = ((preds == 0) & (y == 1)).sum().float()
    prec = tp / (tp + fp + 1e-8)
    rec = tp / (tp + fn + 1e-8)
    return float((2 * prec * rec / (prec + rec + 1e-8)).item())


def binary_precision(prob: torch.Tensor, y: torch.Tensor, thresh: float = 0.5) -> float:
    y = y.view(-1, 1)
    preds = (prob >= thresh).float()
    tp = ((preds == 1) & (y == 1)).sum().float()
    fp = ((preds == 1) & (y == 0)).sum().float()
    return float((tp / (tp + fp + 1e-8)).item())


def binary_recall(prob: torch.Tensor, y: torch.Tensor, thresh: float = 0.5) -> float:
    y = y.view(-1, 1)
    preds = (prob >= thresh).float()
    tp = ((preds == 1) & (y == 1)).sum().float()
    fn = ((preds == 0) & (y == 1)).sum().float()
    return float((tp / (tp + fn + 1e-8)).item())


def multiclass_accuracy(y_pred: torch.Tensor, y_true: torch.Tensor) -> float:
    y_true = y_true.view(-1).long()
    y_pred = y_pred.view(-1).long()
    return float((y_pred == y_true).float().mean().item())


def multiclass_f1_macro(y_true: torch.Tensor, y_pred: torch.Tensor, n_classes: Optional[int] = None) -> float:
    y_true = y_true.view(-1).long()
    y_pred = y_pred.view(-1).long()
    if n_classes is None:
        n_classes = int(max(y_true.max(), y_pred.max()).item()) + 1 if y_true.numel() else 0
    if n_classes == 0:
        return 0.0

    f1_sum = 0.0
    eps = 1e-12
    for c in range(n_classes):
        tp = ((y_true == c) & (y_pred == c)).sum().float()
        fp = ((y_true != c) & (y_pred == c)).sum().float()
        fn = ((y_true == c) & (y_pred != c)).sum().float()
        precision = tp / (tp + fp + eps)
        recall = tp / (tp + fn + eps)
        f1 = 2 * precision * recall / (precision + recall + eps)
        f1_sum += float(f1.item())
    return f1_sum / n_classes


class ClassificationMetrics:

    @staticmethod
    def hinge_loss(y_pm1: torch.Tensor, scores: torch.Tensor) -> torch.Tensor:
        y_pm1 = y_pm1.view(-1).float()
        scores = scores.view(-1).float()
        return torch.clamp(1.0 - y_pm1 * scores, min=0.0).mean()

    @staticmethod
    def accuracy(y_true01: torch.Tensor, y_pred01: torch.Tensor) -> torch.Tensor:
        y_true01 = y_true01.view(-1).float()
        y_pred01 = y_pred01.view(-1).float()
        return (y_true01 == y_pred01).float().mean()

    @staticmethod
    def f1(y_true01: torch.Tensor, y_pred01: torch.Tensor) -> torch.Tensor:
        y = y_true01.view(-1).bool()
        p = y_pred01.view(-1).bool()
        tp = (y & p).sum().float()
        fp = (~y & p).sum().float()
        fn = (y & ~p).sum().float()
        precision = tp / (tp + fp + 1e-12)
        recall = tp / (tp + fn + 1e-12)
        return 2 * precision * recall / (precision + recall + 1e-12)

    @staticmethod
    def metrics(y_true01: torch.Tensor, y_pred01: torch.Tensor, y_prob: torch.Tensor) -> dict:
        return {
            "accuracy": ClassificationMetrics.accuracy(y_true01, y_pred01),
            "f1_score": ClassificationMetrics.f1(y_true01, y_pred01),
            "hinge_loss": ClassificationMetrics.hinge_loss(torch.where(y_true01 == 0, -1.0, 1.0), y_prob.view(-1)),
        }
