// Layer of the network.
// It is a control and stores data that is to be loaded on server as well.
example.Layer = draw2d.shape.layout.FlexGridLayout.extend({
  init: function(attr, setter, getter)
  {
    this._super($.extend({
      columns:"grow",
      rows:   "grow",
      bgColor:"#FFFFFF",
      stroke:2
    },attr, setter, getter));

    // Will be used when enumerating layers
    this.visited = false;

    this.label = new draw2d.shape.basic.Label({resizeable:true, stroke:0});
    this.label.setPadding(25);
    this.label.setFontSize(20);
    this.add(this.label, {row:0, col:0});
    this.setResizeable(false);
    this.setRadius(20);
    this.setBackgroundColor("#f0ffff");


  },

  // !!!Call this right after constructor to initialize layer properly!!!
  // Know, that it is stupid
  setType: function (layerType) {
    var self = this;
    // Data to be loaded on server
    this.layerData = {type: layerType};

    if (layerType === "dense") {
      this.layerData.units = 1;
      this.layerData.activation = "sigmoid";
    } else if (layerType === "dropout") {
      this.layerData.fractionToDrop = 0.0001;
    }

    let inputLocator = new draw2d.layout.locator.InputPortLocator();
    let inputPort = new draw2d.InputPort();
    let outputLocator = new draw2d.layout.locator.OutputPortLocator();
    let outputPort = new draw2d.OutputPort();
    inputPort.setName("input");
    outputPort.setName("output");

    //all ports should contain link to their node so that the layers are enumerated properly
    inputPort.layerNode = this;
    outputPort.layerNode = this;

    if (layerType !== "input")
      this.addPort(inputPort, inputLocator);
    if (layerType !== "output")
      this.addPort(outputPort, outputLocator);

    this.label.setText(layerType);
  },

  objectForJSON: function () {
    let result = {
      x: this.getX().valueOf(),
      y: this.getY().valueOf(),
      type: this.layerData.type
    }
    if (this.layerData.type === "dense") {
      result.units = this.layerData.units;
      result.activation = this.layerData.activation;
    } else if (this.layerData.type === "dropout") {
      result.fraction = this.layerData.fractionToDrop;
    }
    return result;
  }

});
