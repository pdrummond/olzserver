var OlzApp = {};

$(function() {

	OlzApp.Router = Backbone.Router.extend({
		routes: {
			'loop/:loopId': 'setLoopViewFromId',
			'query/*query': 'setLoopViewFromQuery',
			'*path': 'setOuterLoopView',
		},

		initialize: function(options){
			$.fn.autumn.init({colorProfile: 'pastel'});
			this.appView = new OlzApp.AppView();
		},

		setLoopViewFromQuery: function (query) {
			console.log("Routing to: " + query);
			var loopView = new OlzApp.LoopView({query: query});
			this.appView.showView(loopView);
		},
		
		setLoopViewFromId: function (loopId) {
			console.log("Routing to: " + loopId);
			var loopView = new OlzApp.LoopView({loopId: loopId, showDetail:true});
			this.appView.showView(loopView);
		},

		setOuterLoopView: function () {
			console.log("OUTERLOOP");
			var loopView = new OlzApp.LoopView({loopId: '#outerloop@openloopz', showDetail:true});
			this.appView.showView(loopView);
		}
	});

});
