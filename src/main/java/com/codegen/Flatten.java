package com.codegen;

public class Flatten extends Layer {

    public Flatten(String layerName) {
        this.layerName = layerName;
    }

    @Override
    public String toCode() {
        String res = "";
        res += layerName + " = keras.layers.Flatten(";
        if (this.sideName != null)
            res += "input_shape=(" + sideName + ", " + sideName + ", 3)";
        res += ")";
        return res;
    }
}
