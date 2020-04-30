
example.Toolbar = Class.extend({

	init:function(elementId, view, layerEditId){
		this.html = $("#"+elementId);
		this.view = view;
		this.layerEdit = $("#" + layerEditId);

		//html to edit dense layer
		this.denseEdit = "<form id=\"dense_edit\" >\n" +
      "      <label for=\"units\">Output dimension (should be integer)</label>\n" +
      "      <input type=\"text\" name=\"units\" id=\"units\">\n" +
      "      <br><br><br>\n" +
      "      <label for=\"activation\">Activation function</label>\n" +
      "      <select id=\"activation\" name=\"activation\">\n" +
      "        <option value=\"softmax\">softmax</option>\n" +
      "        <option value=\"softplus\">softplus</option>\n" +
      "        <option value=\"softsign\">softsign</option>\n" +
      "        <option value=\"relu\">relu</option>\n" +
      "        <option value=\"tanh\">tanh</option>\n" +
      "        <option value=\"sigmoid\">sigmoid</option>\n" +
      "        <option value=\"hard_sigmoid\">hard_sigmoid</option>\n" +
      "        <option value=\"linear\">linear</option>\n" +
      "      </select>\n" +
      "      <br><br><br>\n" +
      "      <button id=\"save_layer_button\">Save</button>\n" +
      "    </form>";

		//html to edit dropout layer
		this.dropoutEdit = "<form id=\"dropout_edit\" >\n" +
      "      <label for=\"fraction\">Fraction to drop</label>\n" +
      "      <input type=\"text\" name=\"fraction\" id=\"fraction\">\n" +
      "      <br><br><br>\n" +
      "      <button id=\"save_layer_button\">Save</button>\n" +
      "    </form>";

		//html for flatten layer
        this.flattenEdit = " <form id=\"flatten_edit\" >\n" +
          "      <p> Flattens the input. does not affect the batch size. </p>\n" +
          "    </form>";

        //html for input layer
        this.inputEdit = "<form id=\"input_edit\" >\n" +
          "      <p> Layer that represents a particular input port in the network. </p>\n" +
          "    </form>";

        //html for output layer
        this.outputEdit = "<form id=\"output_edit\" >\n" +
          "      <p> Layer that represents a particular output port in the network. </p>\n" +
          "    </form>";

		// register this class as event listener for the canvas
		// CommandStack. This is required to update the state of
		// the Undo/Redo Buttons.
		//
		view.getCommandStack().addEventListener(this);

		// Register a Selection listener for the state handling
		// of the Delete Button
		//
        view.on("select", $.proxy(this.onSelectionChanged,this));

            // Inject the DELETE Button
            //
            this.deleteButton  = $("<button>Delete</button>");
            this.html.append(this.deleteButton);
            this.deleteButton.button().click($.proxy(function(){
                var node = this.view.getPrimarySelection();
                var command= new draw2d.command.CommandDelete(node);
                this.view.getCommandStack().execute(command);
            },this)).button( "option", "disabled", true );

            this.saveButton = $("<button>Save</button>");
            this.html.append(this.saveButton);
            this.saveButton.button().click($.proxy(function () {
                let requestData = this.layersJSONArrays();
                if (requestData === null) {
                    alert("Only sequential api is supported. " +
                        "Their should be no more than one connection to each port!");
                } else {
                    $.ajax({
                        url: example.siteRoot + "/upload_layers",
                        type: "POST",
                        data: requestData,
                        contentType: "application/json; charset=utf-8",
                        dataType: "json",
                        async: true
                    });
                }
            }  , this));
	},

	/**
	 * @method
	 * Called if the selection in the cnavas has been changed. You must register this
	 * class on the canvas to receive this event.
	 *
     * @param {draw2d.Canvas} emitter
     * @param {Object} event
     * @param {draw2d.Figure} event.figure
	 */
	onSelectionChanged : function(emitter, event){
	  if (event.figure instanceof example.Layer) {
	    if (event.figure.layerData.type === "dense") {
	      this.layerEdit[0].innerHTML = this.denseEdit;
      } else if (event.figure.layerData.type === "dropout") {
        this.layerEdit[0].innerHTML = this.dropoutEdit;
      } else if (event.figure.layerData.type === "flatten") {
	      this.layerEdit[0].innerHTML = this.flattenEdit;
      } else if (event.figure.layerData.type === "input") {
	      this.layerEdit[0].innerHTML = this.inputEdit;
      } else if (event.figure.layerData.type === "output") {
        this.layerEdit[0].innerHTML = this.outputEdit;
      } else {
        this.layerEdit[0].innerHTML = "";
      }
      this.displayLayer(event.figure);
    }
		this.deleteButton.button( "option", "disabled", event.figure===null );
	},


  displayLayer: function(figure) {
	  if (figure.layerData.type === "dense") {
	    let units = $("#units");
      units[0].value = figure.layerData.units.toString();
	    let activation = $("#activation");
	    activation[0].value = figure.layerData.activation;
	    let saveButton = $("#save_layer_button");
      saveButton.off();
      saveButton.on("click", $.proxy(function (event) {
        event.preventDefault();
        let units = $("#units");
        let num = parseInt(units[0].value);
        if (isNaN(num)) {
          alert("Output dimension of a dense layer should be a positive integer up to 10000");
        } else if (num > 10000 || num <= 0) {
          alert("Output dimension of a dense layer should be a positive integer up to 10000");
        } else {
          this.layerData.units = num;
          let activation = $("#activation");
          this.layerData.activation = activation[0].value;
        }
      }, figure))
    } else if (figure.layerData.type === "dropout") {
	    let fraction = $("#fraction");
	    fraction[0].value = figure.layerData.fractionToDrop.toFixed(4).toString();
      let saveButton = $("#save_layer_button");
      saveButton.off();
      saveButton.on("click", $.proxy(function (event) {
        event.preventDefault();
        let fraction = $("#fraction");
        let num = parseFloat(fraction[0].value);
        if (isNaN(num)) {
          alert("Dropout parameter should be a fraction from (0, 1)");
        } else if (num < 0.0001 || num > 0.9999) {
          alert("Dropout parameter should be a fraction from (0, 1)");
        }
        else {
          this.layerData.fractionToDrop = num;
        }
      }, figure));
    }
  },

  layersJSONArrays: function() {
    let result = [];
    let ports = this.view.getAllPorts();
    for (let i = 0; i < ports.getSize(); i++) {
        if (ports.get(i).getConnections().getSize() > 1)
            return null;
    }
    for (let i = 0; i < ports.getSize(); i++) {
      let port = ports.get(i);
      if (!port.layerNode.visited) {
        let layer = this.go(port.layerNode, "input", function (layer) {}, null);
        let curArray = [];
        this.go(layer, "output", function (layer) {
          layer.visited = true;
        }, curArray);
        result.push(curArray);
      }
    }
    this.setAllLayersUnvisited();
    return JSON.stringify(result);
  },

  setAllLayersUnvisited: function() {
	  let ports = this.view.getAllPorts();
	  for (let i = 0; i < ports.getSize(); i++) {
	    let port = ports.get(i);
	    if (port.layerNode.visited) {
	      let layer = this.go(port.layerNode, "input", function (layer) {}, null);
	      this.go(layer, "output", function (layer) {
          layer.visited = false;
        }, null);
      }
    }
  },

  /**
   * Goes from the certain layer to the specified direction.
   * Accumulates the layers' data in the accumulator if it is not null or undefined.
   * @param {Layer} layer
   * @param {String} direction
   * @param {Array} accumulator
   * @param {Function} setColor
   * @return {Layer} last visited layer
   */
  go: function(layer, direction, setColor, accumulator) {
    let lastVisitedLayer = null;
    while (layer !== null) {
      if (accumulator !== null && accumulator !== undefined)
        accumulator.push(layer.objectForJSON());
      setColor(layer);
      lastVisitedLayer = layer;
      let port = layer.getPort(direction);
      if (port === null) {
        layer = null;
        break;
      }
      if (port.getConnections().getSize() === 0) {
        layer = null;
        break;
      }
      let connection = port.getConnections().get(0);
      let otherPort = null;
      if (direction === "input") {
        if (connection.getSource() instanceof draw2d.OutputPort)
          otherPort = connection.getSource();
        else if (connection.getTarget() instanceof draw2d.OutputPort)
          otherPort = connection.getTarget();
      } else if (direction === "output") {
        if (connection.getSource() instanceof draw2d.InputPort)
          otherPort = connection.getSource();
        else if (connection.getTarget() instanceof draw2d.InputPort)
          otherPort = connection.getTarget();
      }
      if (otherPort === null)
        layer = null;
      else
        layer = otherPort.layerNode;
    }
    return lastVisitedLayer;
  },

	/**
	 * @method
	 * Sent when an event occurs on the command stack. draw2d.command.CommandStackEvent.getDetail()
	 * can be used to identify the type of event which has occurred.
	 *
	 * @template
	 *
	 * @param {draw2d.command.CommandStackEvent} event
	 **/
	stackChanged:function(event)
	{
		/*this.undoButton.button( "option", "disabled", !event.getStack().canUndo() );
		this.redoButton.button( "option", "disabled", !event.getStack().canRedo() );*/
	}
});
