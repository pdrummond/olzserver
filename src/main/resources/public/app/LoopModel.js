var OlzApp = {};

$(function() {

	OlzApp.LoopModel = Backbone.Model.extend({
		defaults: {
			"editMode": false
		},
		blacklist: ['editMode',],
		idAttribute: 'uid',
		urlRoot: '/loops',
		url: function() {
			var url = null;
			if(this.get('uid')) {
				url = this.urlRoot + '/' + encodeURIComponent(this.get('uid'));
			} else {
				url = this.urlRoot;
			}
			if(this.parentUid) {
				url += "?parentUid=" + encodeURIComponent(this.parentUid);
			}
			return url;
		},

		toJSON: function(options) {
			return _.omit(this.attributes, this.blacklist);
		},
	});

});
