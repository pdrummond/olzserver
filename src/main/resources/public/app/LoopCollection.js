var OlzApp = {};

$(function() {

	OlzApp.LoopCollection = Backbone.Collection.extend({
		url: function() {
			var url = '/loops';

			var ch = '?';
			if(this.query) {
				url += '?query=' + encodeURIComponent(this.query);
				ch = '&';
			}
			if(this.since) {
				url += ch + 'since=' + this.since;
			}
			return url;
		},

		parse: function(resp, options) {
			OlzApp.csrfToken = options.xhr.getResponseHeader('X-CSRF-TOKEN');
			return resp;
		},
	});

});
