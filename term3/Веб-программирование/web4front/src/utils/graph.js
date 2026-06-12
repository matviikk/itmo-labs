import { CoordinateSystem } from './CoordinateSystem';

const xValues = [-3, -2, -1, 0, 1, 2, 3, 4, 5];

function findNearestX(x) {
    return xValues.reduce((prev, curr) => {
        return Math.abs(curr - x) < Math.abs(prev - x) ? curr : prev;
    });
}

export class Graph {
    constructor(component) {
        this.component = component;
        this.coordSystem = new CoordinateSystem('coordinateCanvas');
        this.currentRadius = parseFloat(component.radius) || 2;

        this.initEventListeners();
        this.drawPoints();
    }

    initEventListeners() {
        this.coordSystem.canvas.addEventListener('click', (event) => {
            const coords = this.coordSystem.getCoordinatesFromClick(event);
            const nearestX = findNearestX(coords.x);

            this.component.x = nearestX;
            this.component.y = parseFloat(coords.y.toFixed(2));
            this.component.checkPoint();
        });
    }

    handleRadiusChange(value) {
        this.currentRadius = parseFloat(value) || 2;
        this.coordSystem.drawAreas(this.currentRadius);
        this.drawPoints();
    }

    drawPoints() {
        if (!this.coordSystem) return;

        this.coordSystem.clear();
        this.coordSystem.drawAreas(this.currentRadius);

        const points = this.component.points || [];
        points.forEach(point => {
            if (point.radius === this.currentRadius) {
                this.coordSystem.drawPoint(point.x, point.y, point.radius, point.hit);
            }
        });
    }
}