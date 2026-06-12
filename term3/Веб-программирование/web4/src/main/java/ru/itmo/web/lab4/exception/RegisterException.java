package ru.itmo.web.lab4.exception;

public class RegisterException extends RuntimeException {
    public RegisterException(String message) {
        super(message);
    }
}
