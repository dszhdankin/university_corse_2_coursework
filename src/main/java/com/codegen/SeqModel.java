package com.codegen;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.*;

public class SeqModel {
    private String loss;
    private String optimizer;
    private String modelName;
    private String dataName;
    private String labelsName;
    private int epochs;
    private int batch;
    private String sideName;
    private List<String> metrics = new ArrayList<>();
    private List<Layer> layers = new ArrayList<>();

    public SeqModel(JSONObject seqModel, String modelName, String dataName,
                    String labelsName, String sideName) throws JSONException {
        this.sideName = sideName;
        this.modelName = modelName;
        this.dataName = dataName;
        this.labelsName = labelsName;
        loss = seqModel.getString("loss");
        optimizer = seqModel.getString("optimizer");
        metrics.add("accuracy");
        batch = seqModel.getInt("batch");
        epochs = seqModel.getInt("epochs");
        JSONArray allLayers = seqModel.getJSONArray("layers");
        JSONArray neededLayers = allLayers.getJSONArray(0);
        Integer num = 0;
        while (num < neededLayers.length()) {
            Layer cur = Layer.CreateLayer(neededLayers.getJSONObject(num), "layer" + num);
            layers.add(cur);
            num++;
        }
    }

    public String toCode() {
        if (!layers.get(0).toCode().equals("input") || !layers.get(layers.size() - 1).toCode().equals("output"))
            return "Error";
        String res = "";
        res += modelName + " = " + "keras.models.Sequential()\n\n";
        for (int i = 1; i < layers.size() - 1; i++) {
            Layer cur = layers.get(i);
            if (i == 1)
                cur.setSideName(sideName);
            res += cur.toCode() + "\n";
        }
        res += "\n";
        for (int i = 1; i < layers.size() - 1; i++) {
            Layer cur = layers.get(i);
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
        res += "metrics=" + metricsList.toString() + ")\n";
        res += modelName + ".fit(" + dataName + ", " + labelsName + ", ";
        res += "batch_size=" + batch + ", " + "epochs=" + epochs + ")\n";
        return res;
    }
}
