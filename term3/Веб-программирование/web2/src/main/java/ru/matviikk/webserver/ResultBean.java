package ru.matviikk.webserver;

import java.util.ArrayList;
import java.util.List;

public class ResultBean {
    private List<Result> results = new ArrayList<>();

    public List<Result> getResults() {
        return results;
    }

    public void addResult(Result result) {
        results.add(result);
    }
}