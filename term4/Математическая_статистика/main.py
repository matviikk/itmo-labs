import matplotlib.pyplot as plt
import numpy as np

# Данные
x = np.array([1.5, 2.6, 4.4, 6.0, 7.9, 9.6, 11.2, 12.7, 14.0, 15.5])
y = np.array([4.7, 5.3, 5.5, 5.9, 6.7, 7.0, 7.5, 7.6, 8.1, 8.5])

# Уравнение регрессии
a = 0.2544  # наклон линии
b = 4.5076  # пересечение с осью y
x_line = np.linspace(min(x), max(x), 100)  # значения x для линии
y_line = a * x_line + b  # значения y для линии регрессии

# Построение графика
plt.figure(figsize=(10, 6))  # размер графика
plt.scatter(x, y, color='blue', label='Данные')  # точки данных
plt.plot(x_line, y_line, color='red', label='Линия регрессии')  # линия регрессии
plt.xlabel('x')  # подпись оси x
plt.ylabel('y')  # подпись оси y
plt.title('Линейная регрессия для варианта 11')  # заголовок
plt.legend()  # легенда
plt.grid(True)  # сетка
plt.show()  # отображение графика