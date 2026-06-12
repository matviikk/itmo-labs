function updateClock() {
    const clockDisplay = document.getElementById('clock-display');
    if (!clockDisplay) return;

    const now = new Date();
    clockDisplay.textContent = now.toLocaleString('ru-RU', {
        year: 'numeric',
        month: '2-digit',
        day: '2-digit',
        hour: '2-digit',
        minute: '2-digit',
        second: '2-digit'
    });
}

setInterval(updateClock, 7000);

updateClock();
