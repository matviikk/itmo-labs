package ru.itmo.web.lab3.beans;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Named;
import lombok.Data;

/**
 * Управляемый бин для предоставления информации о студенте, группе и варианте.
 */
@Named("userInfo")
@ApplicationScoped
@Data
public class UserInfoBean {
    private final String studentName = "Здор Матвей Максимович";
    private final String groupNumber = "P3234";
    private final String variant = "24222";
}