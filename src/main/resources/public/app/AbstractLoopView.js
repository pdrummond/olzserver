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
			var searchTags = "";
			var regex = r = /(#[^@.][\w-]*)|(@[^#.][\w-]*)/g;			
			while (matches = regex.exec(input)) {
				searchTags += " " + matches[0];   
			}
			return searchTags;
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
			return this.model.get("id");
		},
		
		getViewHelpers: function() {
			return {
				md2html: function(text) {
					var converter = new Showdown.converter();
					var html = converter.makeHtml(text);
					return html;
				}
			}
		}
	});
});
