var OlzApp = {};

$(function() {

	OlzApp.Router = Backbone.Router.extend({
		routes: {
			'loop/:id': 'setLoopView',
		},
		
		initialize: function(options){
		    this.appView = new OlzApp.AppView();
		  },
		
		setLoopView: function (id) {			
			console.log("Routing to id=" + id + " in VIEW MODE");
			this.createLoopView(id);
		},
		
		setLoopViewInEditMode: function (id) {			
			console.log("Routing to id=" + id + " in EDIT MODE");
			this.createLoopView(id, true);
		},
		
		createLoopView: function(id, editMode) {
			var loopView = new OlzApp.LoopView({id: id, editMode:editMode});
			this.appView.showView(loopView);
		}
	});

});
