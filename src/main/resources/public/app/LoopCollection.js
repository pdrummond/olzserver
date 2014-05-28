var OlzApp = {};

$(function() {

	OlzApp.LoopCollection = Backbone.Collection.extend({
		model: OlzApp.LoopModel,

		//comparatorField: "updatedAt",
		sortOrder: false,

		comparator: function(model) {
			if(this.comparatorField) {
				console.log(">> " + this.comparatorField);
				if(this.sortOrder == 'ascending') {
					return -model.get(this.comparatorField);
				} else {
					return model.get(this.comparatorField);
				}
			} else {
				return -model.get('lastUpdated');
			}
		},

		url: function() {
			var url = '/loops';

			var ch = '?';
			if(this.loopId) {
				//This looks wrong but it's not - we are intentionally
				//doing a query for a single loop in a collection.  Don't ask! 
				//That's just how it is ;-) 
				url += '?query=' + encodeURIComponent(this.loopId);
				ch = '&';			
			} else if(this.query) {
				url += '?query=' + encodeURIComponent(this.query);
				ch = '&';
			}

			/** 
			 * For now, we always show the detail so we can easily get the loop 
			 * totals.  In future we should have a 'loopCount' field for the 
			 * non-detail loop request.
			 */
			//if(this.showDetail) {
			url += ch + 'detail=true';  
			//}
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
