package com.codegen;

public abstract class Layer {
    protected String layerName;

    public String getLayerName() {
        return layerName;
    }

    public abstract String toCode();

}