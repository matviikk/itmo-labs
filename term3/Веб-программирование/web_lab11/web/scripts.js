let tableCreated = false;

function sendData() {
    const x = document.getElementById('x').value;
    const y = document.getElementById('y').value;
    const rElements = document.getElementsByName('r');
    let r;
    for (const element of rElements) {
        if (element.checked) {
            r = element.value;
            break;
        }
    }

    if (!validateInput(x, y, r)) {
        return;
    }

    // Создаем URL с параметрами запроса
    const queryString = `x=${encodeURIComponent(x)}&y=${encodeURIComponent(y)}&r=${encodeURIComponent(r)}`;

    fetch(`/fcgi-bin/server-1.0.jar?${queryString}`, {
        method: 'GET',
    })
        .then(response => response.json())
        .then(json => {
            if (!tableCreated) {
                createTable();
                tableCreated = true;
            }
            addRow(x, y, r, json.result, json.currentTime, json.executionTime);
        })
        .catch(error => console.error('Error:', error));
}

function createTable() {
    const resultContainer = document.getElementById('results');
    const table = document.createElement('table');
    table.setAttribute('id', 'resultTable');
    table.innerHTML = `
        <tr>
            <th>X</th>
            <th>Y</th>
            <th>R</th>
            <th>Result</th>
            <th>Current Time</th>
            <th>Execution Time</th>
        </tr>
    `;
    resultContainer.appendChild(table);
}

function validateInput(x, y, r) {
    const errorMessage = document.getElementById('error-message');

    // Преобразование введенных значений
    x = parseFloat(x);
    y = parseFloat(y);
    r = parseInt(r);

    // Проверка на корректность введенных значений
    if (
        isNaN(x) || isNaN(y) || isNaN(r) ||    // Проверка на числа
        r < 1 || r > 5 ||                      // R должен быть в диапазоне от 1 до 5
        x < -5 || x > 3 ||                     // X должен быть в диапазоне от -5 до 3
        y <= -3 || y >= 5                      // Y должен быть в диапазоне от -3 до 5
    ) {
        // Если данные некорректны, показываем сообщение об ошибке
        errorMessage.textContent = 'Invalid input values. Please enter correct X, Y, and R values.';
        errorMessage.style.display = 'block';
        return false;
    }

    // Если данные корректны, скрываем сообщение об ошибке
    errorMessage.textContent = '';
    errorMessage.style.display = 'none';
    return true;
}

function drawCoordinateSystem() {
    const canvas = document.getElementById("plotCanvas");
    const ctx = canvas.getContext("2d");
    if (!ctx) {
        console.error("Не удалось получить 2D контекст для канваса");
        return;
    }

    // Определяем минимальный размер родительского элемента для сохранения соотношения 1:1
    const parentElement = canvas.parentElement;
    const size = Math.min(parentElement.clientWidth, parentElement.clientHeight);
    canvas.width = size;
    canvas.height = size;

    // Очищаем холст
    ctx.clearRect(0, 0, canvas.width, canvas.height);

    // Центр канваса (центр системы координат)
    const centerX = canvas.width / 2;
    const centerY = canvas.height / 2;
    const scale = canvas.width / 4; // Масштаб для отображения R (динамически)

    // Рисуем закрашенные области

    // Прямоугольник: от (0, 0) до (-R, -R/2) (в нижнем левом углу)
    ctx.fillStyle = "#4a90e2";
    ctx.beginPath();
    ctx.rect(centerX - scale, centerY, scale, scale / 2);
    ctx.closePath();
    ctx.fill();

    // Треугольник: (0, 0), (R, 0), (0, -R) (в верхнем правом углу)
    ctx.beginPath();
    ctx.moveTo(centerX, centerY); // (0, 0)
    ctx.lineTo(centerX + scale, centerY); // (R, 0)
    ctx.lineTo(centerX, centerY - scale); // (0, -R)
    ctx.closePath();
    ctx.fill();

    // Четверть круга: с центром в (0, 0), радиус R/2 (в нижнем правом углу)
    ctx.beginPath();
    ctx.moveTo(centerX, centerY); // Начальная точка в центре
    ctx.arc(centerX, centerY, scale, 0, 0.5 * Math.PI, false); // Дуга от 0° до 90°
    ctx.lineTo(centerX, centerY); // Возвращаемся к центру
    ctx.closePath();
    ctx.fill();

    // Рисуем оси координат
    ctx.strokeStyle = "#000";
    ctx.lineWidth = 2;

    // Ось X
    ctx.beginPath();
    ctx.moveTo(0, centerY);
    ctx.lineTo(canvas.width, centerY);
    ctx.stroke();

    // Ось Y
    ctx.beginPath();
    ctx.moveTo(centerX, 0);
    ctx.lineTo(centerX, canvas.height);
    ctx.stroke();

    // Добавляем стрелки на осях
    ctx.fillStyle = "#000";

    // Стрелка на оси X
    ctx.beginPath();
    ctx.moveTo(canvas.width - 10, centerY - 5);
    ctx.lineTo(canvas.width, centerY);
    ctx.lineTo(canvas.width - 10, centerY + 5);
    ctx.closePath();
    ctx.fill();

    // Стрелка на оси Y
    ctx.beginPath();
    ctx.moveTo(centerX - 5, 10);
    ctx.lineTo(centerX, 0);
    ctx.lineTo(centerX + 5, 10);
    ctx.closePath();
    ctx.fill();

    // Рисуем отметки на осях для R
    ctx.font = "20px Arial";
    ctx.fillStyle = "#000";

    // Отметки по оси X
    ctx.fillText("R", centerX + scale - 10, centerY + 20); // R
    ctx.fillText("R/2", centerX + scale / 2 - 20, centerY + 20); // R/2
    ctx.fillText("-R", centerX - scale - 20, centerY + 20); // -R
    ctx.fillText("-R/2", centerX - scale / 2 - 30, centerY + 20); // -R/2

    // Отметки по оси Y
    ctx.fillText("R", centerX - 20, centerY - scale + 10); // R
    ctx.fillText("R/2", centerX - 30, centerY - scale / 2 + 10); // R/2
    ctx.fillText("-R/2", centerX - 30, centerY + scale / 2 + 10); // -R/2
    ctx.fillText("-R", centerX - 30, centerY + scale + 10); // -R

    // Добавление делений на осях
    ctx.lineWidth = 1;

    // Деления по оси X
    ctx.beginPath();
    ctx.moveTo(centerX + scale, centerY - 5);
    ctx.lineTo(centerX + scale, centerY + 5);
    ctx.stroke();

    ctx.beginPath();
    ctx.moveTo(centerX + scale / 2, centerY - 5);
    ctx.lineTo(centerX + scale / 2, centerY + 5);
    ctx.stroke();

    ctx.beginPath();
    ctx.moveTo(centerX - scale, centerY - 5);
    ctx.lineTo(centerX - scale, centerY + 5);
    ctx.stroke();

    ctx.beginPath();
    ctx.moveTo(centerX - scale / 2, centerY - 5);
    ctx.lineTo(centerX - scale / 2, centerY + 5);
    ctx.stroke();

    // Деления по оси Y
    ctx.beginPath();
    ctx.moveTo(centerX - 5, centerY - scale);
    ctx.lineTo(centerX + 5, centerY - scale);
    ctx.stroke();

    ctx.beginPath();
    ctx.moveTo(centerX - 5, centerY - scale / 2);
    ctx.lineTo(centerX + 5, centerY - scale / 2);
    ctx.stroke();

    ctx.beginPath();
    ctx.moveTo(centerX - 5, centerY + scale / 2);
    ctx.lineTo(centerX + 5, centerY + scale / 2);
    ctx.stroke();

    ctx.beginPath();
    ctx.moveTo(centerX - 5, centerY + scale);
    ctx.lineTo(centerX + 5, centerY + scale);
    ctx.stroke();
}

// Функция для обновления размеров и перерисовки при изменении размера окна
function resizeCanvas() {
    drawCoordinateSystem();
}

window.onload = function () {
    drawCoordinateSystem(); // Рисуем график при загрузке страницы
    drawCoordinateSystem();
    window.addEventListener('resize', resizeCanvas); // Перерисовываем график при изменении размера окна
    loadResults();
};


function loadResults() {
    const results = JSON.parse(localStorage.getItem('results')) || [];
    if (results.length > 0) {
        if (!tableCreated) {
            createTable(); // Создаём таблицу, если её ещё нет
            results.forEach(result => addRow(result.x, result.y, result.r, result.result, result.currentTime, result.executionTime));
            tableCreated = true; // Указываем, что таблица создана
        }
    }
}

function addRow(x, y, r, result, currentTime, executionTime) {
    const resultTable = document.getElementById('resultTable');

    // Проверяем, есть ли уже строка с такими значениями
    const rows = resultTable.getElementsByTagName("tr");
    for (let i = 1; i < rows.length; i++) { // Начинаем с 1, чтобы пропустить заголовок
        const cells = rows[i].getElementsByTagName("td");
        if (cells[0].innerText === x && cells[1].innerText === y && cells[2].innerText === r) {
            return; // Если такая строка уже есть, ничего не добавляем
        }
    }

    // Создаем новую строку с результатами
    const newRow = document.createElement('tr');
    newRow.innerHTML = `
        <td>${x}</td>
        <td>${y}</td>
        <td>${r}</td>
        <td>${result}</td>
        <td>${currentTime}</td>
        <td>${executionTime}</td>
    `;
    resultTable.appendChild(newRow);

    // Сохраняем результат в localStorage
    let results = JSON.parse(localStorage.getItem('results')) || [];
    results.push({ x, y, r, result, currentTime, executionTime });
    localStorage.setItem('results', JSON.stringify(results));
}