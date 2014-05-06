var OlzApp = {};

$(function() {

	OlzApp.Router = Backbone.Router.extend({
		routes: {
			'loop/:id': 'setLoopView',
			'loop/:id/edit': 'setLoopViewInEditMode'
		},

		setLoopView: function (id) {			
			console.log("Routing to id=" + id + " in VIEW MODE");
			var loopView = new OlzApp.LoopView({id: id});
		},
		
		setLoopViewInEditMode: function (id) {			
			console.log("Routing to id=" + id + " in EDIT MODE");
			var loopView = new OlzApp.LoopView({id: id, editMode: true});
		}
	});

});
