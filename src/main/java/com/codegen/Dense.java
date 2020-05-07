package com.codegen;

import org.json.*;

public class Dense extends Layer {
    String activation;
    Integer units;

    public Dense(JSONObject layer, String layerName) throws JSONException {
        this.layerName = layerName;
        this.units = layer.getInt("units");
        this.activation = layer.getString("activation");
    }

    public String toCode() {
        String res = "";
        res += layerName + " = ";
        res += "keras.layers.Dense(";
        res += "units=" + units.toString() + ", ";
        if (sideName != null)
            res += "input_shape=(" + sideName + ", " + sideName + ", 3)" + ", ";
        res += "activation=" + "\'" + activation + "\')";
        return res;
    }
}
