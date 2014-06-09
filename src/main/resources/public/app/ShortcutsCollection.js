var OlzApp = {};

$(function() {

	OlzApp.ShortcutsCollection = Backbone.Collection.extend({
		//model: OlzApp.LoopModel,

		url: function() {
			var url = '/shortcuts/' + OlzApp.user.userId;
			return url;
		},

		parse: function(resp, options) {
			OlzApp.csrfToken = options.xhr.getResponseHeader('X-CSRF-TOKEN');
			return resp;
		},
	});

});
