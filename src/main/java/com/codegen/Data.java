package com.codegen;

import org.json.*;

import java.util.List;

public class Data {
    List<Double> data;

    public Data(JSONArray numbers) {
        List buffer = numbers.toList();
        for (Object cur : buffer) {
            data.add((Double) cur);
        }
    }
}
