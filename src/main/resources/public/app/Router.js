var OlzApp = {};

$(function() {

	OlzApp.Router = Backbone.Router.extend({
		routes: {
			'loop/:loopId': 'setLoopView',
		},
		
		initialize: function(options){
		    this.appView = new OlzApp.AppView();
		  },
		
		setLoopView: function (loopId) {
			var loopView = new OlzApp.LoopView({loopId: loopId});
			this.appView.showView(loopView);
		}		
	});

});
