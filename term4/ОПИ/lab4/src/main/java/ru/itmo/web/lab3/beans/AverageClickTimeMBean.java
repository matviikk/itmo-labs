package ru.itmo.web.lab3.beans;

import ru.itmo.web.lab3.model.Point;

public interface AverageClickTimeMBean {
    /**
    * @return среднее время между кликами в миллисекундах (long), или -1, если недостаточно данных
    * (меньше двух кликов)
    */
    long getAverageClickTime();
    void reset();
    void proceedPoint(Point point);
}