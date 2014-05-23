var OlzApp = {};

$(function() {

	OlzApp.Router = Backbone.Router.extend({
		routes: {
			'query/*query': 'setLoopViewFromQuery',
			'loop/*loopId': 'setSingleLoopView',
			'*path': 'setOuterLoopView',
		},

		initialize: function(options){
			this.appView = new OlzApp.AppView();
		},

		setLoopViewFromQuery: function (query) {
			console.log("Routing to: " + query);
			var loopView = new OlzApp.LoopView({query: query});
			this.appView.showView(loopView);
		},

		setSingleLoopView: function (loopId) {
			console.log("Routing to: " + loopId);
			var loopView = new OlzApp.SingleLoopView({loopId: loopId});
			this.appView.showView(loopView);
		},

		setOuterLoopView: function () {
			var loopView = new OlzApp.LoopView({showOuterLoop: true});
			this.appView.showView(loopView);
		}
	});

});
