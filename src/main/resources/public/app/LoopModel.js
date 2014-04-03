var OlzApp = {};

$(function() {

	OlzApp.LoopModel = Backbone.Model.extend({
		idAttribute: 'id',
		urlRoot: '/loops',
		url: function() {
			var url = null;
			if(this.get('id')) {
				url = this.urlRoot + '/' + encodeURIComponent(this.get('id'));
			} else {
				url = this.urlRoot;
			}
			if(this.parentLid) {
				url += "?parentLid=" + encodeURIComponent(this.parentLid);
			}
			return url;
		}
	});
	
});
