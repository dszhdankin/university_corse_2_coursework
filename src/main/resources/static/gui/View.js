example.View = draw2d.Canvas.extend({

	init:function(id){
		this._super(id);

		this.setScrollArea("#"+id);

		this.currentDropConnection = null;
	},

    /**
     * @method
     * Called if the DragDrop object is moving around.<br>
     * <br>
     * Graphiti use the jQuery draggable/droppable lib. Please inspect
     * http://jqueryui.com/demos/droppable/ for further information.
     *
     * @param {HTMLElement} droppedDomNode The dragged DOM element.
     * @param {Number} x the x coordinate of the drag
     * @param {Number} y the y coordinate of the drag
     *
     * @template
     **/
    onDrag:function(droppedDomNode, x, y )
    {
    },

    /**
     * @method
     * Called if the user drop the droppedDomNode onto the canvas.<br>
     * <br>
     * Draw2D use the jQuery draggable/droppable lib. Please inspect
     * http://jqueryui.com/demos/droppable/ for further information.
     *
     * @param {HTMLElement} droppedDomNode The dropped DOM element.
     * @param {Number} x the x coordinate of the drop
     * @param {Number} y the y coordinate of the drop
     * @param {Boolean} shiftKey true if the shift key has been pressed during this event
     * @param {Boolean} ctrlKey true if the ctrl key has been pressed during the event
     * @private
     **/
    onDrop : function(droppedDomNode, x, y, shiftKey, ctrlKey)
    {
        var type = $(droppedDomNode).data("shape");
        var figure = new example.Layer();
        figure.setType(type);
        // create a command for the undo/redo support
        var command = new draw2d.command.CommandAdd(this, figure, x, y);
        this.getCommandStack().execute(command);
    },

    drawServerLayers : function (serverLayers) {
        var self = this;
        let figures = [];
        serverLayers.forEach(function (value) {
            for (let i = 0; i < value.length; i++) {
                let cur = value[i];
                if (cur.type === "dense") {
                    let figure = new example.Layer();
                    figure.setType(cur.type);
                    figure.layerData.units = cur.units;
                    figure.layerData.activation = cur.activation;
                    let command = new draw2d.command.CommandAdd(self, figure, cur.x, cur.y);
                    self.getCommandStack().execute(command);
                    figures.push(figure);
                } else if (cur.type === "dropout") {
                    let figure = new example.Layer();
                    figure.setType(cur.type);
                    figure.layerData.fractionToDrop = cur.fraction;
                    let command = new draw2d.command.CommandAdd(self, figure, cur.x, cur.y);
                    self.getCommandStack().execute(command);
                    figures.push(figure);
                } else if (cur.type === "conv2d") {
                    let figure = new example.Layer();
                    figure.setType(cur.type);
                    figure.layerData.kernelHeight = cur.height;
                    figure.layerData.kernelWidth = cur.width;
                    figure.layerData.activation = cur.activation;
                    figure.layerData.filters = cur.filters;
                    let command = new draw2d.command.CommandAdd(self, figure, cur.x, cur.y);
                    self.getCommandStack().execute(command);
                    figures.push(figure);
                } else if (cur.type === "maxpooling2d") {
                    let figure = new example.Layer();
                    figure.setType(cur.type);
                    figure.layerData.poolingHeight = cur.height;
                    figure.layerData.poolingWidth = cur.width;
                    let command = new draw2d.command.CommandAdd(self, figure, cur.x, cur.y);
                    self.getCommandStack().execute(command);
                    figures.push(figure);
                } else {
                    let figure = new example.Layer();
                    figure.setType(cur.type);
                    let command = new draw2d.command.CommandAdd(self, figure, cur.x, cur.y);
                    self.getCommandStack().execute(command);
                    figures.push(figure);
                }
            }
            for (let i = 1; i < value.length; i++) {
                let connection = new draw2d.Connection({
                    router: new draw2d.layout.connection.InteractiveManhattanConnectionRouter(),
                    radius: 5,
                    stroke: 1.35
                });
                connection.setSource(figures[i - 1].getPort("output"));
                connection.setTarget(figures[i].getPort("input"));
                self.add(connection);
            }
        });
    }
});

