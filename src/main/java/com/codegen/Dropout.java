package com.codegen;

import org.json.JSONObject;

public class Dropout extends Layer {
    Double rate;

    public Dropout(JSONObject layer, String layerName) {
        this.layerName = layerName;
        this.rate = layer.getDouble("fraction");
    }

    @Override
    public String toCode() {
        String res = "";
        res += this.layerName + " = keras.layers.Dropout(";
        res += "rate=" + this.rate.toString() + ")";
        return res;
    }
}
