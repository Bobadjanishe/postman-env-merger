package model;

import java.util.HashMap;
import java.util.Map;

public class MergeResult {

    private Map<String, String> finalData = new HashMap<>();
    private Map<String, String> added = new HashMap<>();
    private Map<String, String> updated = new HashMap<>();

    public Map<String, String> getFinalData() {
        return finalData;
    }

    public Map<String, String> getAdded() {
        return added;
    }

    public Map<String, String> getUpdated() {
        return updated;
    }
}