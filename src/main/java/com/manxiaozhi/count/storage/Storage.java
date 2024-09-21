package com.manxiaozhi.count.storage;

import org.json.JSONObject;

public interface Storage {
    void saveMetric(String metricName, JSONObject dimension) throws Exception;
    void close();
}
