var OlzApp = {};

$(function() {

	OlzApp.Router = Backbone.Router.extend({
		routes: {
			'/': 'setOuterLoopView',
			'loop/*loopId': 'setLoopView',
		},

		initialize: function(options){
			this.appView = new OlzApp.AppView();
		},

		setLoopView: function (loopId) {
			console.log("Routing to: " + loopId);
			var loopView = new OlzApp.LoopView({loopId: loopId});
			this.appView.showView(loopView);
		},

		setOuterLoopView: function (loopId) {
			var loopView = new OlzApp.LoopView({loopId: ' '});
			this.appView.showView(loopView);
		}		
	});

});
