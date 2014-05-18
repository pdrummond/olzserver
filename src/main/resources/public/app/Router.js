var OlzApp = {};

$(function() {

	OlzApp.Router = Backbone.Router.extend({
		routes: {
			'query/*query': 'setLoopViewFromQuery',
			'loop/*loopId': 'setLoopViewFromId',
			'outerloop': 'setOuterLoopView',
		},

		initialize: function(options){
			this.appView = new OlzApp.AppView();
		},

		setLoopViewFromQuery: function (query) {
			console.log("Routing to: " + query);
			var loopView = new OlzApp.LoopView({query: query});
			this.appView.showView(loopView);
		},

		setLoopViewFromId: function (loopId) {
			console.log("Routing to: " + loopId);
			var loopView = new OlzApp.LoopView({loopId: loopId});
			loopView.currentLoopView = 'loop';
			this.appView.showView(loopView);
		},

		setOuterLoopView: function (loopId) {
			var loopView = new OlzApp.LoopView({showOuterLoop: true});
			this.appView.showView(loopView);
		}		
	});

});
