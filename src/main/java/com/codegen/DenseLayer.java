package com.codegen;

import org.json.*;

public class DenseLayer extends Layer {
    Integer input_shape = null;
    String activation;
    Integer units;

    public DenseLayer(JSONObject layer, String layerName) throws JSONException {
        this.layerName = layerName;
        this.units = layer.getInt("units");
        if (layer.has("input_shape"))
            this.input_shape = layer.getInt("input_shape");
        this.activation = layer.getString("activation");
    }

    public String toCode() {
        String res = "";
        if (layerName != null) {
            res += layerName + " = ";
        }
        res += "keras.layers.Dense(";
        res += "units=" + units.toString() + ", ";
        res += "activation=" + "\'" + activation + "\', ";
        if (input_shape != null)
            res += "input_shape=(" + input_shape.toString() + ", )" + ", ";
        StringBuilder builder = new StringBuilder(res);
        builder.delete(res.length() - 2, res.length());
        res = builder.toString();
        res += ")";
        return res;
    }
}
