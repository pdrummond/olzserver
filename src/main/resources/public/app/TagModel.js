var OlzApp = {};

$(function() {

	OlzApp.TagModel = Backbone.Model.extend({
		idAttribute: 'id',
		urlRoot: '/hashtags',
		url: function() {
			if(this.get('id')) {
				return this.urlRoot + '/' + this.get('id');
			} else {
				return this.urlRoot;
			}
		}
	});
	
});
