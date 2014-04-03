var OlzApp = {};

$(function() {

	OlzApp.Router = Backbone.Router.extend({
		routes: {
			'loop/:id': 'setLoopView'
		},

		setLoopView: function (id) {			
			console.log("Routing to " + id);
			var loopView = new OlzApp.LoopView({id: id});
		}
	});

});
