var OlzApp = {};

$(function() {

	OlzApp.Router = Backbone.Router.extend({
		routes: {
//			'query/*query': 'setLoopViewFromQuery',
			'loop/*loopId': 'setLoopView',
//			'outerloop': 'setOuterLoopView',
		},

		initialize: function(options){
			this.appView = new OlzApp.AppView();
		},

		setLoopViewFromQuery: function (query) {
			console.log("Routing to: " + query);
			var loopView = new OlzApp.LoopView({query: query});
			this.appView.showView(loopView);
		},

		setLoopView: function (loopId) {
			console.log("Routing to: " + loopId);
			var loopView = new OlzApp.LoopView({loopId: loopId});
			this.appView.showView(loopView);
		},

		setOuterLoopView: function (loopId) {
			var loopView = new OlzApp.LoopView({showOuterLoop: true});
			this.appView.showView(loopView);
		}		
	});

});
