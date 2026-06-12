package utility;

import model.LabWork;

import java.io.Serializable;

public class Request implements Serializable {

    private String command;
    private LabWork labWork;

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    private String login;
    private String passowrd;
    public int getUserId() {
        return userId;
    }


    private int userId;
    public LabWork getLabWork() {
        return labWork;
    }

    public void setLabWork(LabWork labWork) {
        this.labWork = labWork;
    }

    @Override
    public String toString() {
        return "Request{" +
                "command='" + command + '\'' +
                ", labWork=" + labWork +
                '}';
    }

    public Request(String command, LabWork labWork, String login, String password) {
        this.command = command;
        this.labWork = labWork;
        this.login = login;
        this.passowrd = password;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public String getPassowrd() {
        return passowrd;
    }

    public void setPassowrd(String passowrd) {
        this.passowrd = passowrd;
    }
}
