document.addEventListener('DOMContentLoaded', function () {
    const canvas = document.getElementById('graph');
    const ctx = canvas.getContext('2d');
    const form = document.getElementById('coordinateForm');

    let currentR = null;
    let points = loadPoints() || [];

    function savePoints() {
        localStorage.setItem('graphPoints', JSON.stringify(points));
    }

    function loadPoints() {
        const savedPoints = localStorage.getItem('graphPoints');
        return savedPoints ? JSON.parse(savedPoints) : [];
    }

    document.querySelectorAll('input[name="r"]').forEach(radio => {
        radio.addEventListener('change', function () {
            currentR = parseFloat(this.value);
            drawGraph();
        });
    });

    function setCanvasSize() {
        const size = canvas.parentElement.clientWidth;
        canvas.width = size;
        canvas.height = size;
        drawGraph();
    }

    setCanvasSize();
    window.addEventListener('resize', setCanvasSize);

    function drawGraph() {
        ctx.clearRect(0, 0, canvas.width, canvas.height);
        const centerX = canvas.width / 2;
        const centerY = canvas.height / 2;
        const scale = canvas.width / 4;


        ctx.beginPath();
        ctx.moveTo(0, centerY);
        ctx.lineTo(canvas.width, centerY);
        ctx.moveTo(centerX, 0);
        ctx.lineTo(centerX, canvas.height);
        ctx.stroke();


        ctx.font = '14px Arial';
        ctx.fillStyle = 'black';
        ctx.textAlign = 'center';

        ctx.fillText('-R', centerX - scale, centerY + 20);
        ctx.fillText('-R/2', centerX - scale / 2, centerY + 20);
        ctx.fillText('R/2', centerX + scale / 2, centerY + 20);
        ctx.fillText('R', centerX + scale, centerY + 20);
        ctx.fillText('-R', centerX - 20, centerY + scale);
        ctx.fillText('-R/2', centerX - 20, centerY + scale / 2);
        ctx.fillText('R/2', centerX - 20, centerY - scale / 2);
        ctx.fillText('R', centerX - 20, centerY - scale);

        ctx.fillStyle = 'rgba(0, 0, 255, 0.3)';

        // Прямоугольник
        ctx.fillRect(centerX, centerY - scale, scale / 2, scale);

        // Четверть круга
        ctx.beginPath();
        ctx.moveTo(centerX, centerY);
        ctx.arc(centerX, centerY, scale, Math.PI / 2, Math.PI);
        ctx.closePath();
        ctx.fill();

        // Треугольник
        ctx.beginPath();
        ctx.moveTo(centerX, centerY);
        ctx.lineTo(centerX + scale / 2, centerY);
        ctx.lineTo(centerX, centerY + scale / 2);
        ctx.closePath();
        ctx.fill();

        // Точки
        points.forEach(point => {
            const pointScale = scale / point.r;
            const x = centerX + point.graphX * pointScale;
            const y = centerY - point.graphY * pointScale;
            drawPoint(ctx, x, y, point.color);
        });
    }

    function validateX(x) {
        return !isNaN(x) && x >= -5 && x <= 3;
    }

    function validateY() {
        const checkedY = document.querySelectorAll('input[name="y"]:checked');
        return checkedY.length === 1;
    }

    function validateR() {
        const selectedR = document.querySelector('input[name="r"]:checked');
        return selectedR !== null;
    }

    function findNearestY(y) {
        const yValues = Array.from(document.querySelectorAll('input[name="y"]'))
            .map(input => parseFloat(input.value));

        return yValues.reduce((prev, curr) => {
            return Math.abs(curr - y) < Math.abs(prev - y) ? curr : prev;
        });
    }

    form.addEventListener('submit', function (e) {
        e.preventDefault();

        const x = parseFloat(document.getElementById('x').value);
        const yChecked = document.querySelector('input[name="y"]:checked');
        const r = document.querySelector('input[name="r"]:checked');

        if (!validateX(x)) {
            alert('Некорректное значение X. Допустимый диапазон от -5 до 3.');
            return;
        }

        if (!validateY()) {
            alert('Выберите одно значение Y.');
            return;
        }

        if (!validateR()) {
            alert('Выберите значение R.');
            return;
        }

        currentR = parseFloat(r.value);
        drawGraph();

        this.submit();
    });

    canvas.addEventListener('click', function (event) {
        const selectedR = document.querySelector('input[name="r"]:checked');
        if (!selectedR) {
            alert('Сначала выберите значение R.');
            return;
        }

        currentR = parseFloat(selectedR.value);

        const rect = canvas.getBoundingClientRect();
        const scaleX = canvas.width / rect.width;
        const scaleY = canvas.height / rect.height;

        const x = (event.clientX - rect.left) * scaleX;
        const y = (event.clientY - rect.top) * scaleY;

        const centerX = canvas.width / 2;
        const centerY = canvas.height / 2;
        const scale = canvas.width / 4;

        const graphX = ((x - centerX) / scale) * currentR;
        const graphY = ((centerY - y) / scale) * currentR;

        const roundedX = parseFloat(graphX.toFixed(2));

        if (!validateX(roundedX)) {
            alert('Координата X вне допустимого диапазона (-5 до 3).');
            return;
        }

        const nearestY = findNearestY(graphY);

        document.getElementById('x').value = roundedX;

        const yInputs = document.querySelectorAll('input[name="y"]');
        yInputs.forEach(input => {
            input.checked = parseFloat(input.value) === nearestY;
        });

        const isHit = isInsideArea(graphX, graphY, currentR);

        const color = isHit ? 'green' : 'red';

        const point = {
            graphX,
            graphY,
            color,
            r: currentR
        };
        points.push(point);
        savePoints();

        const pointScale = scale / currentR;
        const canvasX = centerX + graphX * pointScale;
        const canvasY = centerY - graphY * pointScale;
        drawPoint(ctx, canvasX, canvasY, color);

        form.submit();
    });

    function isInsideArea(x, y, R) {
        // Прямоугольник
        if (x >= 0 && x <= R/2 && y >= 0 && y <= R) {
            return true;
        }

        // Четверть круга
        if (x <= 0 && y <= 0 && x * x + y * y <= R * R) {
            return true;
        }

        // Треугольник
        if (x >= 0 && y <= 0 && y >= x - R/2) {
            return true;
        }

        return false;
    }

    function drawPoint(ctx, x, y, color) {
        ctx.fillStyle = color;
        ctx.beginPath();
        ctx.arc(x, y, 5, 0, 2 * Math.PI);
        ctx.fill();
    }

    document.getElementById('clearPointsButton').addEventListener('click', function () {
        localStorage.removeItem('graphPoints');
        points = [];
        drawGraph();

        alert('Все точки удалены!');
    });

    const initialR = document.querySelector('input[name="r"]:checked');
    if (initialR) {
        currentR = parseFloat(initialR.value);
    }
    drawGraph();
});