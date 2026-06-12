package ru.matviikk.webserver;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import java.io.IOException;
import java.util.Set;

@WebServlet("/check-point")
public class AreaCheckServlet extends HttpServlet {
    private Validator validator;

    @Override
    public void init() throws ServletException {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            double x = Double.parseDouble(request.getParameter("x"));
            double y = Double.parseDouble(request.getParameter("y"));
            double r = Double.parseDouble(request.getParameter("r"));

            boolean inside = checkPoint(x, y, r);

            Result result = new Result(x, y, r, inside);

            Set<ConstraintViolation<Result>> violations = validator.validate(result);

            if (!violations.isEmpty()) {
                StringBuilder errorMessages = new StringBuilder();
                for (ConstraintViolation<Result> violation : violations) {
                    errorMessages.append(violation.getMessage()).append("\n");
                }
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, errorMessages.toString());
                return;
            }

            ResultBean resultBean = (ResultBean) request.getSession().getAttribute("resultBean");
            if (resultBean == null) {
                resultBean = new ResultBean();
                request.getSession().setAttribute("resultBean", resultBean);
            }
            resultBean.addResult(result);

            request.setAttribute("result", result);
            request.getRequestDispatcher("/result.jsp").forward(request, response);
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid input parameters");
        }
    }

    private boolean checkPoint(double x, double y, double r) {
        // Прямоугольник
        if (x >= 0 && x <= r / 2 && y >= 0 && y <= r) {
            return true;
        }

        // Четверть круга
        if (x <= 0 && y <= 0 && (x * x + y * y) <= r * r) {
            return true;
        }

        // Треугольник
        if (x >= 0 && y <= 0 && y >= x - r / 2) {
            return true;
        }

        return false;
    }
}