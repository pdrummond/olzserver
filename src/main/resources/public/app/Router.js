var OlzApp = {};

$(function() {

	OlzApp.Router = Backbone.Router.extend({
		routes: {
			'loop/:uid': 'setLoopView'
		},

		setLoopView: function (uid) {			
			console.log("Routing to uid=" + uid);
			var loopView = new OlzApp.LoopView({uid: uid});
		}
	});

});
