var OlzApp = {};

$(function() {

	OlzApp.LoopModel = Backbone.Model.extend({
		defaults: {
			"editMode": false
		},
		blacklist: ['editMode',],
		urlRoot: '/loops',
		url: function() {
			var url = null;
			if(this.get('id')) {
				url = this.urlRoot + '/' + encodeURIComponent(this.get('id'));
			} else {
				url = this.urlRoot;
			}
			if(this.parentSid) {
				url += "?parentSid=" + encodeURIComponent(this.parentSid);
			}
			return url;
		},

		toJSON: function(options) {
			return _.omit(this.attributes, this.blacklist);
		},
	});

});
