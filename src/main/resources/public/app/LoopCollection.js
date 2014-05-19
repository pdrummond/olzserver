var OlzApp = {};

$(function() {

	OlzApp.LoopCollection = Backbone.Collection.extend({
		url: function() {
			var url = '/loops';
			
			if(this.options && this.options.query) {
				url += '?query=' + encodeURIComponent(this.options.query);
			}
			this.options = null;
			return url;
		},

	});

});
