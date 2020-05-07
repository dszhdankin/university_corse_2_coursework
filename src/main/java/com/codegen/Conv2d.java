package com.codegen;

import org.json.JSONObject;

public class Conv2d extends Layer {
    Integer filters;
    Integer height;
    Integer width;
    String activation;

    Conv2d(JSONObject layer, String layerName) {
        this.layerName = layerName;
        this.activation = layer.getString("activation");
        this.width = layer.getInt("width");
        this.height = layer.getInt("height");
        this.filters = layer.getInt("filters");
    }

    public String toCode() {
        String res = "";
        res += this.layerName + " = keras.layers.Conv2D(";
        if (sideName != null)
            res += "input_shape=(" + sideName + ", " + sideName + ", 3), ";
        res += "filters=" + filters + ", ";
        res += "kernel_size=" + "(" + height + ", " + width + "), ";
        res += "activation=\'" + activation + "\')";
        return res;
    }
}
