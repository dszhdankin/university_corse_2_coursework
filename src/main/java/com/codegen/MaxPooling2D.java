package com.codegen;

import org.json.JSONObject;

public class MaxPooling2D extends Layer {
    private Integer height;
    private Integer width;

    public MaxPooling2D(JSONObject layer, String layerName) {
        width = layer.getInt("width");
        height = layer.getInt("height");
        this.layerName = layerName;
    }

    public String toCode() {
        String res = "";
        res = layerName + " = keras.layers.MaxPooling2D(";
        if (sideName != null)
            res += "input_shape=(" + sideName + ", " + sideName + ", 3), ";
        res += "pool_size=(" + height + ", " + width + "))";
        return res;
    }
}
