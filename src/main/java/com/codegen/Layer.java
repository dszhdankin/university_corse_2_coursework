package com.codegen;

import org.json.JSONObject;

public abstract class Layer {

    public static Layer CreateLayer(JSONObject layer, String layerName) {
        Layer res;
        if (layer.getString("type").equals("dense"))
            res = new Dense(layer, layerName);
        else if (layer.getString("type").equals("flatten"))
            res = new Flatten(layerName);
        else if (layer.getString("type").equals("dropout"))
            res = new Dropout(layer, layerName);
        else if (layer.getString("type").equals("conv2d"))
            res = new Conv2d(layer, layerName);
        else if (layer.getString("type").equals("maxpooling2d"))
            res = new MaxPooling2D(layer, layerName);
        else if (layer.getString("type").equals("input"))
            res = new Input();
        else
            res = new Output();
        return res;
    }

    protected String layerName;
    protected String sideName = null;

    public String getLayerName() {
        return layerName;
    }

    public void setSideName(String sideName) {
        this.sideName = sideName;
    }

    public abstract String toCode();

}