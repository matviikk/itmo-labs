let coordSystem;
let currentRadius = 2;

const xValues = [-3, -2, -1, 0, 1, 2, 3, 4, 5];

function findNearestX(x) {
    return xValues.reduce((prev, curr) => {
        return Math.abs(curr - x) < Math.abs(prev - x) ? curr : prev;
    });
}

function handleRadiusChange(value) {
    currentRadius = parseFloat(value) || 2;
    coordSystem.drawAreas(currentRadius);
    drawPoints();
}

document.addEventListener('DOMContentLoaded', () => {
    coordSystem = new CoordinateSystem('coordinateCanvas');

    const radiusSelect = document.querySelector('#coordinateForm\\:radius_input');
    if (radiusSelect) {
        currentRadius = parseFloat(radiusSelect.value) || 2;
        this.drawPoints();
    }

    coordSystem.canvas.addEventListener('click', (event) => {
        const coords = coordSystem.getCoordinatesFromClick(event);
        const nearestX = findNearestX(coords.x);

        const xSelect = document.querySelector('#coordinateForm\\:xCoord_input');
        if (xSelect) {
            xSelect.value = nearestX;
            const changeEvent = new Event('change');
            xSelect.dispatchEvent(changeEvent);
        }

        const yInput = document.querySelector('#coordinateForm\\:yCoord');
        if (yInput) {
            yInput.value = coords.y.toFixed(2);
        }

        document.querySelector('#coordinateForm\\:submitButton').click();
    });

    drawPoints();
});


function drawPoints() {
    coordSystem.clear();
    coordSystem.drawAreas(currentRadius);

    const rows = document.querySelectorAll('#coordinateForm\\:resultsTable_data tr');
    if (rows.length === 0) {
        return;
    }

    const points = Array.from(rows).map(row => {
        const cells = row.cells;

        if (cells.length < 4) {
            return null;
        }

        try {
            return {
                x: parseFloat(cells[0].textContent.trim()),
                y: parseFloat(cells[1].textContent.trim()),
                r: parseFloat(cells[2].textContent.trim()),
                hit: cells[3].querySelector('.ui-tag-value').textContent.trim() === 'Hit'
            };
        } catch (error) {
            return null;
        }
    }).filter(point => point !== null && point.r === currentRadius);

    points.forEach(point => {
        coordSystem.drawPoint(point.x, point.y, point.r, point.hit);
    });
}
