var OlzApp = {};

$(function() {

	OlzApp.LoopModel = Backbone.Model.extend({
		defaults: {
			"editMode": false
		},
		blacklist: ['editMode','handle'],
		urlRoot: '/loops',
		url: function() {
			var url = null;
			if(this.get('id')) {
				url = this.urlRoot + '/' + encodeURIComponent(this.get('id'));
			} else {
				url = this.urlRoot;
			}
			if(this.parentLoopHandle) {
				url += "?parentLoopHandle=" + encodeURIComponent(this.parentLoophandle);
			}
			return url;
		},
		
		toJSON: function(options) {
			return _.omit(this.attributes, this.blacklist);
		}
	});

});
