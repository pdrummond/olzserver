var OlzApp = {};

$(function() {

	OlzApp.Router = Backbone.Router.extend({
		routes: {
			'loop/:sid': 'setLoopView'
		},

		setLoopView: function (sid) {			
			console.log("Routing to sid=" + sid);
			var loopView = new OlzApp.LoopView({sid: sid});
		}
	});

});
