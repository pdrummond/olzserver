var OlzApp = {};

$(function() {

	OlzApp.LoopModel = Backbone.Model.extend({
		defaults: {
			"editMode": false
		},
		blacklist: ['editMode',],
		idAttribute: 'sid',
		urlRoot: '/loops',
		url: function() {
			var url = null;
			if(this.get('sid')) {
				url = this.urlRoot + '/' + encodeURIComponent(this.get('sid'));
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
