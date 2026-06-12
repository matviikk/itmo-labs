package utility;

import model.LabWork;

import java.io.Serializable;

public class Request implements Serializable {

    private String command;
    private LabWork labWork;

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

    public Request(String command, LabWork labWork) {
        this.command = command;
        this.labWork = labWork;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }
}
