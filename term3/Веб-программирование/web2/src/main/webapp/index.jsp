<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Проверка попадания точки в область</title>
    <link rel="stylesheet" href="styles.css">
</head>
<body>
<header>
    <h1>Проверка попадания точки в область</h1>
    <p>Студент: Здор Матвей Максимович, Группа: Р3224, Вариант: 24158</p>
</header>

<main class="index-main">
    <form id="coordinateForm" action="${pageContext.request.contextPath}/controller" method="post">
        <div class="form-group">
            <label for="x">X:</label>
            <input type="text" id="x" name="x" required>
        </div>

        <fieldset>
            <legend>Y:</legend>
            <div class="options">
                <% double[] yValues = {-2, -1.5, -1, -0.5, 0, 0.5, 1, 1.5, 2};
                    for (double value : yValues) { %>
                <label>
                    <input type="checkbox" name="y" value="<%= value %>">
                    <%= value %>
                </label>
                <% } %>
            </div>
        </fieldset>

        <fieldset>
            <legend>R:</legend>
            <div class="options">
                <% double[] rValues = {1, 1.5, 2, 2.5, 3};
                    for (double value : rValues) { %>
                <label>
                    <input type="radio" name="r" value="<%= value %>">
                    <%= value %>
                </label>
                <% } %>
            </div>
        </fieldset>

        <button type="submit">Проверить</button>
    </form>
    <div class="graph-container">
        <canvas id="graph"></canvas>
        <button id="clearPointsButton" class="clear-button">Очистить точки</button>
    </div>

    <div class="results-container index-results-container">
        <table id="resultsTable" class="index-results-table">
            <thead>
            <tr>
                <th>X</th>
                <th>Y</th>
                <th>R</th>
                <th>Результат</th>
            </tr>
            </thead>
            <tbody>
            <c:forEach var="result" items="${sessionScope.resultBean.results}">
                <tr>
                    <td>${result.x}</td>
                    <td>${result.y}</td>
                    <td>${result.r}</td>
                    <td>${result.inside ? "Внутри" : "Снаружи"}</td>
                </tr>
            </c:forEach>
            </tbody>
        </table>
    </div>
</main>

<script src="script.js"></script>
</body>
</html>