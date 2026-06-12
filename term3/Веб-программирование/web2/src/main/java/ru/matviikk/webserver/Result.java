package ru.matviikk.webserver;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import lombok.AllArgsConstructor;
import lombok.Data;
import ru.matviikk.webserver.validation.ValidR;
import ru.matviikk.webserver.validation.ValidY;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class Result implements Serializable {

    @DecimalMin(value = "-5.0", message = "X must be greater than or equal to -5")
    @DecimalMax(value = "3.0", message = "X must be less than or equal to 3")
    private double x;

    @ValidY
    private double y;

    @ValidR
    private double r;

    private boolean inside;
}
