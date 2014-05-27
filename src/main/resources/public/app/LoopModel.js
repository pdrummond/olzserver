var OlzApp = {};

$(function() {
	
	var oldSync = Backbone.sync;
	Backbone.sync = function(method, model, options){
	    options.beforeSend = function(xhr){
	    	if(OlzApp.csrfToken) {
	    		xhr.setRequestHeader('X-CSRF-TOKEN', OlzApp.csrfToken);
	    	}
	    };
	    return oldSync(method, model, options);
	};

	OlzApp.LoopModel = Backbone.Model.extend({
		blacklist: ['editMode','handle'],
		urlRoot: '/loops',
		url: function() {
			var url = null;
			if(this.get("id")) {
				url = this.urlRoot + '/' + encodeURIComponent(this.get("id"));
			} else {
				url = this.urlRoot;
			}
			return url;
		},
		
	  
		toJSON: function(options) {
			return _.omit(this.attributes, this.blacklist);
		},
		
//		secureSync: function(method, model, options) {
//			var csrf = readCookie("X-CSRF-Token");
//			 if (csrf) {
//			    myApp.originalSync = Backbone.sync;
//			    Backbone.sync = function(method, model, options) {
//			        options || (options = {});
//			        options.headers = { "X-CSRF-Token": csrf };
//			        return myApp.originalSync(method,model,options);
//			    };
//			 }
//		}
	});

});
