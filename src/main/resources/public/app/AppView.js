var OlzApp = {};

$(function() {
	OlzApp.AppView = Backbone.View.extend({
		
		showView: function(view) {

			if (this.currentView){
				if(this.currentView.close) {
					this.currentView.close();
				}
				this.currentView.remove();
			}	

			this.currentView = view;
			this.currentView.render();

			$("#loop-container-inner").html(this.currentView.el);
		},
		
	});

});
