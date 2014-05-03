var OlzApp = {};

$(function() {

	OlzApp.LoopModel = Backbone.Model.extend({
		defaults: {
			"editMode": false
		},
		urlRoot: '/loops',
		url: function() {
			var url = null;
			if(this.get('id')) {
				url = this.urlRoot + '/' + encodeURIComponent(this.get('id'));
			} else {
				url = this.urlRoot;
			}
			if(this.parentLoopId) {
				url += "?parentLoopId=" + encodeURIComponent(this.parentLoopId);
			}
			return url;
		},
	});

});
