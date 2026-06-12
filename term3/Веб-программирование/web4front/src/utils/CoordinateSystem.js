export class CoordinateSystem {
    constructor(canvasId, width = 400, height = 400) {
        this.canvas = document.getElementById(canvasId);
        this.ctx = this.canvas.getContext('2d');
        this.canvas.width = width;
        this.canvas.height = height;
        this.centerX = width / 2;
        this.centerY = height / 2;
        this.scale = 40;
    }

    clear() {
        this.ctx.clearRect(0, 0, this.canvas.width, this.canvas.height);
    }

    drawGrid() {
        this.ctx.strokeStyle = '#ddd';
        this.ctx.lineWidth = 0.5;

        for (let x = -this.centerX; x <= this.centerX; x += this.scale) {
            this.ctx.beginPath();
            this.ctx.moveTo(this.centerX + x, 0);
            this.ctx.lineTo(this.centerX + x, this.canvas.height);
            this.ctx.stroke();
        }

        for (let y = -this.centerY; y <= this.centerY; y += this.scale) {
            this.ctx.beginPath();
            this.ctx.moveTo(0, this.centerY + y);
            this.ctx.lineTo(this.canvas.width, this.centerY + y);
            this.ctx.stroke();
        }
    }

    drawAxes() {
        this.ctx.strokeStyle = '#000';
        this.ctx.lineWidth = 1;

        this.ctx.beginPath();
        this.ctx.moveTo(0, this.centerY);
        this.ctx.lineTo(this.canvas.width, this.centerY);
        this.ctx.stroke();

        this.ctx.beginPath();
        this.ctx.moveTo(this.centerX, 0);
        this.ctx.lineTo(this.centerX, this.canvas.height);
        this.ctx.stroke();

        this.ctx.fillStyle = '#000';
        this.ctx.font = '12px Arial';
        for (let i = -5; i <= 5; i++) {
            if (i === 0) continue;
            this.ctx.fillText(i.toString(), this.centerX + i * this.scale - 5, this.centerY + 15);
            this.ctx.fillText((-i).toString(), this.centerX + 5, this.centerY + i * this.scale + 5);
        }
    }

    drawQuarterCircle(radius) {
        if (!radius || radius <= 0) return;

        this.ctx.beginPath();
        this.ctx.arc(this.centerX, this.centerY, radius/2 * this.scale, 0, Math.PI/2);
        this.ctx.lineTo(this.centerX, this.centerY);
        this.ctx.closePath();
        this.ctx.fillStyle = 'rgba(0, 0, 255, 0.3)';
        this.ctx.fill();
        this.ctx.strokeStyle = 'blue';
        this.ctx.stroke();
    }

    drawTriangle(radius) {
        if (!radius || radius <= 0) return;

        this.ctx.beginPath();
        this.ctx.moveTo(this.centerX, this.centerY);
        this.ctx.lineTo(this.centerX + radius * this.scale, this.centerY);
        this.ctx.lineTo(this.centerX, this.centerY - radius * this.scale);
        this.ctx.closePath();
        this.ctx.fillStyle = 'rgba(255, 0, 0, 0.3)';
        this.ctx.fill();
        this.ctx.strokeStyle = 'red';
        this.ctx.stroke();
    }

    drawSquare(radius) {
        if (!radius || radius <= 0) return;

        this.ctx.beginPath();
        this.ctx.moveTo(this.centerX, this.centerY);
        this.ctx.lineTo(this.centerX - radius * this.scale, this.centerY);
        this.ctx.lineTo(this.centerX - radius * this.scale, this.centerY + radius * this.scale);
        this.ctx.lineTo(this.centerX, this.centerY + radius * this.scale);
        this.ctx.closePath();
        this.ctx.fillStyle = 'rgba(0, 255, 0, 0.3)';
        this.ctx.fill();
        this.ctx.strokeStyle = 'green';
        this.ctx.stroke();
    }

    drawPoint(x, y, r, hit) {
        const scale = this.scale;

        const screenX = this.centerX + x * scale;
        const screenY = this.centerY - y * scale;

        this.ctx.beginPath();
        this.ctx.arc(screenX, screenY, 4, 0, 2 * Math.PI);
        this.ctx.fillStyle = hit ? '#00ff00' : '#ff0000';
        this.ctx.fill();
        this.ctx.strokeStyle = '#000';
        this.ctx.stroke();
    }

    getCoordinatesFromClick(event) {
        const rect = this.canvas.getBoundingClientRect();
        const x = (event.clientX - rect.left - this.centerX) / this.scale;
        const y = -(event.clientY - rect.top - this.centerY) / this.scale;
        return {
            x: Math.round(x * 100) / 100,
            y: Math.round(y * 100) / 100
        };
    }

    drawAreas(radius) {
        this.clear();
        this.drawGrid();
        this.drawAxes();
        if (radius && radius > 0) {
            this.drawQuarterCircle(radius);
            this.drawTriangle(radius);
            this.drawSquare(radius);
        }
    }
}