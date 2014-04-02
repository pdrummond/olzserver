var OlzApp = {};

$(function() {

	OlzApp.LoopModel = Backbone.Model.extend({
		idAttribute: 'id',
		urlRoot: '/loops',
		url: function() {
			if(this.get('id')) {
				return this.urlRoot + '/' + this.get('id');
			} else {
				return this.urlRoot;
			}
		}
	});
	
});
