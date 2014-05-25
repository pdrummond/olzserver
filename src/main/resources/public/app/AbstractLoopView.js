var OlzApp = {};

$(function() {

	OlzApp.AbstractLoopView = Backbone.View.extend({
		
		createLoopEditor: function(el) {
			this.loopEditor = new OlzApp.LoopEditor({
				el: this.$(el),
				loopView: this
			});	
			this.$(el).focus();
		},

		destroyLoopEditor: function() {
			if(this.loopEditor) { 
				this.loopEditor.destroy();
				delete this.loopEditor;				
			}
		},

		toggleEditMode: function() {
			this.editMode = !this.editMode;
			if(this.editMode) {
				this.$("#toggle-edit-mode-button .glyphicon").removeClass("glyphicon-edit").addClass("glyphicon-floppy-disk");
				this.createLoopEditor(this.getLoopBodyEl());
			} else {
				this.$("#toggle-edit-mode-button .glyphicon").removeClass("glyphicon-floppy-disk").addClass("glyphicon-edit");
				this.saveLoop();
			}
		},

		generateContent: function(body) {
			var content = body;		
			//body = $(".body", content).html($(this.getAllowedBodyTags(), content).wrapLoopRefs());			
			//var content = '<div class="loop"><div class="body">' + body.html() + '</div></div>';		
			content = content.replace(/&nbsp;/g, ' ');
			console.log("CONTENT: " + content);
			
			return content;
		},
		
		extractTags: function(input) {
			var tags = [];
			var regex = r = /(#[^@.][\w-]*)|(@[^#.][\w-]*)/g;			
			while (matches = regex.exec(input)) {
				tags.push(matches[0]);   
			}
			return tags;
		},
		
		extractTagsAsString: function(input) {
			var tags = this.extractTags(input);
			return tags.join(' ');
		},

		renderLastSaved: function(options) {
			var error = options && options.error;
			if(error) {
				$('#last-saved-msg-inner').html("Error saving.  RED ALERT!");
			} else if(this.lastSaved) {
				//$('#last-saved-msg-inner').html("Last saved " + moment(this.lastSaved).fromNow());
			}
		},

		showError: function(title, message) {
			title = title + " at " + moment().format('h:mm a');
			$.growl.error({ title: title, message: message, duration: 99999});
		},

		isViewLoaded: function() {
			return this.model.attributes.length > 0;
		},
		
		getViewHelpers: function() {
			return {
				md2html: function(text) {
					var converter = new Showdown.converter();
					var html = converter.makeHtml(text);
					return html;
				},
				
			}
		},
		
		createLoop: function(body, options) {
			var self = this;

			var content = this.generateContent(body);

			/*var searchTags = this.extractTags($('.search-input').val().trim());
			var loopTags = this.extractTags(content);

			for(var i=0; i<searchTags.length; i++) {
				if(!_.contains(loopTags, searchTags[i])) {
					content += " " + searchTags[i];
				}
			}*/

			var loopModel = new OlzApp.LoopModel({content:content});
			if(options && options.parentLoopId) {
				loopModel.parentLoopId = options.parentLoopId;
			}			
			loopModel.save(null, {
				success: function(loop) {
					var loopView = new OlzApp.LoopItemView({model:loopModel});
					self.loopListView.prependLoopItem(loopView);
				}
			})

			//this.stompClient.send("/app/hello", {}, JSON.stringify({ 'name': "BOOM" }));
		},
		
		saveLoop: function(body, callback) {
			var self = this;
			this.model.save({'content': this.generateContent(body) }, {
				success: function() {
					self.lastSaved = new Date();
					self.renderLastSaved();
					if(callback) {
						callback(true);
					}
				},
				error: function(model, response, options) {
					self.renderLastSaved({error:true});
					self.showError("Save Error", response.statusText);
					if(callback) {
						callback(false);
					}
				}
			});
		},

	});
});
