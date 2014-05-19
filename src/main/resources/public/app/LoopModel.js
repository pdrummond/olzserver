var OlzApp = {};

$(function() {

	OlzApp.LoopModel = Backbone.Model.extend({
		blacklist: ['editMode','handle'],
		urlRoot: '/loops',
		url: function() {
			var url = null;
			if(this.options) {
				if(this.options.loopId) {
					url = this.urlRoot + '?loopId=' + encodeURIComponent(this.options.loopId);
				} else if(this.options.query) {
					url = this.urlRoot + '?query=' + encodeURIComponent(this.options.query);
				} else if(this.options.showOuterLoop) {
					url = this.urlRoot + '?showOuterLoop=true';
				} 
			} else if(this.get("id")) {
				url = this.urlRoot + '?loopId=' + encodeURIComponent(this.get("id"));
			} else {
				url = this.urlRoot;
			}

			if(this.parentLoopId) {
				url += "&parentLoopId=" + encodeURIComponent(this.parentLoopId);
			}
			return url;
		},

		toJSON: function(options) {
			return _.omit(this.attributes, this.blacklist);
		}
	});

});
