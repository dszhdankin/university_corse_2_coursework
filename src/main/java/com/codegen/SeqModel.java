package com.codegen;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.*;

public class SeqModel {
    private String loss;
    private String optimizer;
    private String modelName;
    private List<String> metrics = new ArrayList<>();
    private List<Layer> layers = new ArrayList<>();
    private List<String> dependencies = new ArrayList<>();

    public SeqModel(JSONObject seqModel, String modelName) {
        this.modelName = modelName;
        loss = seqModel.getString("loss");
        optimizer = seqModel.getString("optimizer");
        List metricsBuffer = seqModel.getJSONArray("metrics").toList();
        for (Object cur : metricsBuffer) {
            metrics.add(cur.toString());
        }
        JSONArray layersBuffer = seqModel.getJSONArray("layers");
        Integer num = 0;
        for (int i = 0; i < layersBuffer.length(); i++) {
            JSONObject cur = layersBuffer.getJSONObject(i);
            layers.add(new DenseLayer(cur, "layer" + num.toString()));
            num++;
        }
        dependencies.add("keras.layers");
        dependencies.add("keras.models");
    }

    public String toCode() {
        String res = "";
        for (String cur : dependencies) {
            res += "import " + cur + "\n";
        }
        res += "\n";
        res += modelName + " = " + "keras.models.Sequential()\n\n";
        for (Layer cur : layers) {
            res += cur.toCode() + "\n";
        }
        res += "\n";
        for (Layer cur : layers) {
            res += modelName + ".add(" + cur.getLayerName() + ")\n";
        }
        res += "\n";
        res += modelName + ".compile(";
        res += "loss=\'" + loss + "\', ";
        res += "optimizer=\'" + optimizer + "\', ";
        StringBuilder metricsList = new StringBuilder("[");
        for (String cur : metrics) {
            metricsList.append("\'" + cur + "\', ");
        }
        metricsList.delete(metricsList.length() - 2, metricsList.length());
        metricsList.append("]");
        res += "metrics=" + metricsList.toString() + ")";
        return res;
    }
}
