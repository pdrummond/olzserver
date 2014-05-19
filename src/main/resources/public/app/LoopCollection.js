var OlzApp = {};

$(function() {

	OlzApp.LoopCollection = Backbone.Collection.extend({
		url: '/loops',
	});

});
