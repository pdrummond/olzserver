var OlzApp = {};

$(function() {

	OlzApp.Router = Backbone.Router.extend({
		routes: {
<<<<<<< HEAD
<<<<<<< HEAD
			'outerloop': 'setOuterLoopView',
			'loop/*loopId': 'setLoopView',
=======
//			'query/*query': 'setLoopViewFromQuery',
			'loop/*loopId': 'setLoopView',
//			'outerloop': 'setOuterLoopView',
>>>>>>> exp-single-loop
=======
			'query/*query': 'setLoopViewFromQuery',
			'loop/*loopId': 'setLoopViewFromId',
			'outerloop': 'setOuterLoopView',
>>>>>>> parent of ff2bce4... Revert 39cd058..244d737
		},

		initialize: function(options){
			this.appView = new OlzApp.AppView();
		},

<<<<<<< HEAD
<<<<<<< HEAD
=======
=======
>>>>>>> parent of ff2bce4... Revert 39cd058..244d737
		setLoopViewFromQuery: function (query) {
			console.log("Routing to: " + query);
			var loopView = new OlzApp.LoopView({query: query});
			this.appView.showView(loopView);
		},

<<<<<<< HEAD
>>>>>>> exp-single-loop
		setLoopView: function (loopId) {
=======
		setLoopViewFromId: function (loopId) {
>>>>>>> parent of ff2bce4... Revert 39cd058..244d737
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
