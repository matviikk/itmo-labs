<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page import="ru.matviikk.webserver.Result" %>
<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Результат проверки</title>
    <link rel="stylesheet" href="styles.css">
</head>
<body>
<header>
    <h1>Результат проверки попадания точки в область</h1>
</header>

<main class="result-main">
    <%
        Result result = (Result) request.getAttribute("result");
        String resultText = result.isInside() ? "Точка находится внутри области" : "Точка находится вне области";
        String resultClass = result.isInside() ? "result-inside" : "result-outside";
    %>
    <div class="result-message <%= resultClass %>">
        <p><%= resultText %></p>
    </div>
    <div class="results-container history-results-container">
        <h2>История результатов</h2>
        <table id="resultsTable" class="history-results-table">
            <tr>
                <th>X</th>
                <th>Y</th>
                <th>R</th>
                <th>Результат</th>
            </tr>
            <c:forEach var="historyResult" items="${sessionScope.resultBean.results}">
                <tr>
                    <td><c:out value="${historyResult.x}"/></td>
                    <td><c:out value="${historyResult.y}"/></td>
                    <td><c:out value="${historyResult.r}"/></td>
                    <td>
                        <c:out value="${historyResult.inside ? 'Внутри' : 'Снаружи'}"/>
                    </td>
                </tr>
            </c:forEach>
        </table>
    </div>
    <a href="index.jsp" class="back-link">Вернуться на главную страницу</a>
</main>
</body>
</html>