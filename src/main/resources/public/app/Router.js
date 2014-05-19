var OlzApp = {};

$(function() {

	OlzApp.Router = Backbone.Router.extend({
		routes: {
<<<<<<< HEAD
			'outerloop': 'setOuterLoopView',
			'loop/*loopId': 'setLoopView',
=======
//			'query/*query': 'setLoopViewFromQuery',
			'loop/*loopId': 'setLoopView',
//			'outerloop': 'setOuterLoopView',
>>>>>>> exp-single-loop
		},

		initialize: function(options){
			this.appView = new OlzApp.AppView();
		},

<<<<<<< HEAD
=======
		setLoopViewFromQuery: function (query) {
			console.log("Routing to: " + query);
			var loopView = new OlzApp.LoopView({query: query});
			this.appView.showView(loopView);
		},

>>>>>>> exp-single-loop
		setLoopView: function (loopId) {
			console.log("Routing to: " + loopId);
			var loopView = new OlzApp.LoopView({loopId: loopId});
			this.appView.showView(loopView);
		},

		setOuterLoopView: function (loopId) {
			var loopView = new OlzApp.LoopView({loopId: -1});
			this.appView.showView(loopView);
		}		
	});

});
