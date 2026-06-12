## TanSeries module

API: `TanSeries.tan(x, eps, maxTerms)`

Rules:
- `x` must be finite
- `eps > 0`
- `maxTerms > 0`
- near discontinuities (`pi/2 + k*pi`) throws `ArithmeticException` when `|cos(x)| < eps`