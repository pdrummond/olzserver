var OlzApp = {};

$(function() {

	OlzApp.AbstractLoopView = Backbone.View.extend({

		createLoopEditor: function(loopType, el, toolbarElement) {
			var loopEditor = new OlzApp.LoopEditor({
				el: this.$(el),
				toolbarElement: toolbarElement,
				loopView: this,
				loopType: loopType
			});
			this.listenTo(loopEditor, 'olzsave', this.onEditButtonClicked);
			this.$(el).focus();
			return loopEditor;
		},

		createLoopIdEditor: function(el, toolbarElement) {
			var editor = new OlzApp.LoopEditor({
				el: this.$(el),
				toolbarElement: toolbarElement,
				loopView: this
			});	
			return editor;
		},

		destroyLoopEditor: function(loopEditor) {
			if(loopEditor) { 
				loopEditor.destroy();
				delete loopEditor;
			}
		},

		toggleEditMode: function() {
			this.editMode = !this.editMode;
			if(this.editMode) {
				this.setEditMode();
			} else {
				this.setViewMode();
				this.saveLoop();
			}
		},

		setEditMode: function() {
			this.editMode = true;
			this.$("#toggle-edit-mode-button .glyphicon").removeClass("glyphicon-edit").addClass("glyphicon-floppy-disk").show();
			this.createLoopEditor(this.getLoopBodyEl());
		},

		setViewMode: function() {
			this.editMode = false;
			this.$("#toggle-edit-mode-button .glyphicon").removeClass("glyphicon-floppy-disk").addClass("glyphicon-edit");
		},

		generateContent: function(content) {
			/*
			 * 1. Replace nbsp with numeric entity ref (which is needed for XML transform.
			 * 2. Replace any <image> element with <image/> respectively.  This shouldn't 
			 *    happen but sometimes it does (usually when pasting).
			 * 3. Remove <br> tags - they shouln't ever be in the content but sometimes they
			 *    are.
			 */
			var content = content.replace(/&nbsp;/g, '&#160;').replace(/(<img [^>]+[^\/])>/gi,'$1 />').replace(/<br>/g, "");
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

			var content = 
				'<div data-type="loop">'
				+ '<div data-type="loop-header" class="loop-header">' + this.generateContent(body) + '</div>' 
				+ '<div data-type="loop-body"   class="loop-body"></div>'  
				+ '<div data-type="loop-footer" class="loop-footer"></div>' + 
				'</div>';		

			/*var searchTags = this.extractTags($('.search-input').val().trim());
			var loopTags = this.extractTags(content);

			for(var i=0; i<searchTags.length; i++) {
				if(!_.contains(loopTags, searchTags[i])) {
					content += " " + searchTags[i];
				}
			}*/

			var now = new Date().getTime();

			var loopModel = new OlzApp.LoopModel({content:content, createdAt: now, updatedAt: now, podId: 1});
			if(options && options.parentLoopId) {
				loopModel.parentLoopId = options.parentLoopId;
			}			
			loopModel.save(null, {
				success: function(loop) {
					self.loopListView.addLoopItem(loopModel, {addToTop: true});
				}
			})

			//this.stompClient.send("/app/hello", {}, JSON.stringify({ 'name': "BOOM" }));
		},

		onEditButtonClicked: function(loopType) {
			var self = this;
			var $editButton = this.$('#' + loopType + '-edit-button');

			this.editMode = !this.editMode;
			if(this.editMode) {
				this.$('#' + loopType + '-edit-button').hide();
				this.$('#list-settings-button').hide();
				this.$('.' + loopType + ' .loop-content-wrapper').show();
				$editButton.html('<span class="glyphicon glyphicon-floppy-disk">');

				this.$('.id-tag').attr('contenteditable', true);
				var toolbarDiv = "editor-toolbar-" + this.model.get('id');
				//Order important - header last so it gets focus.
				this.loopFooterEditor = this.createLoopEditor(loopType, this.$('.'  + loopType + '-content-wrapper .loop-footer'), toolbarDiv);			
				this.loopBodyEditor = this.createLoopEditor(loopType, this.$('.'  + loopType + '-content-wrapper .loop-body'), toolbarDiv);
				this.loopHeaderEditor = this.createLoopEditor(loopType, this.$('.'  + loopType + '-content-wrapper .loop-header'), toolbarDiv);


			} else {
				this.$('#' + loopType + '-edit-button').show();
				this.$('#list-settings-button').show();
				this.$('.id-tag').attr('contenteditable', false);

				$editButton.html('Saving...');
				var headerContent = this.loopHeaderEditor.getData();
				var bodyContent = this.loopBodyEditor.getData();
				var footerContent = this.loopFooterEditor.getData();
				this.destroyLoopEditor(this.loopHeaderEditor);
				this.destroyLoopEditor(this.loopBodyEditor);
				this.destroyLoopEditor(this.loopFooterEditor);

				var content = "<div data-type='loop-header'> " + headerContent + "</div>";
				content += "<div data-type='loop-body'> " + bodyContent + "</div>";
				content += "<div data-type='loop-footer'> " + footerContent + "</div>";

				this.saveLoop(content, function() {
					$editButton.html('<span class="glyphicon glyphicon-edit">');					
				});
			}
		},

		onLoopSelected: function() {
			var handle = this.model.get('id');
			if(handle.indexOf('@') == -1) {
				handle += this.model.get('ownerTag');
			}

			Backbone.history.navigate("#loop/" + encodeURIComponent(handle), {trigger:true});
		},

		renderLastUpdatedMsg: function() {
			if(this.model.get('createdAt') === this.model.get('updatedAt')) {
				this.$(".last-updated-msg").html("Created " + moment(this.model.get('createdAt')).fromNow());
			} else if(this.model.get('updatedAt')) {
				this.$(".last-updated-msg").html("Updated " + moment(this.model.get('updatedAt')).fromNow());
			} 
		},

		navigateToLoop: function(loopId, options) {
			options = options || {trigger:true};
			Backbone.history.navigate("#loop/" + encodeURIComponent(loopId), options);
		},

		renderListTotals: function() {
			this.lists = this.model.get('lists');
			this.$('.list-totals-box ul').empty();
			for(var i=0; i<this.lists.length; i++) {
				var list = this.lists[i];
				if(list.loops.length > 0) {
					var listName = list.loops.length + " " + list.name;
					this.$('.list-totals-box ul').append('<li><span class="glyphicon glyphicon-list-alt"/><strong> ' + list.loops.length + '</strong> ' + list.name + '</li>');
				}
			}
		},

		saveLoop: function(body, callback) {
			var self = this;

			var newLoopId = false;
			var loopId = $.trim(this.$('.id-tag').text());
			if(loopId != this.model.get('id')) {
				this.model.set('newId', loopId);
				newLoopId = true;
			}
			this.model.set('saving', true);
			var content = "<div data-type='loop'>" + this.generateContent(body) + "</div>";
			this.model.save({'content': content }, {
				success: function(model) {
					self.model.unset('errorDetails');
					self.model.unset('saving');
					self.lastSaved = new Date();

					self.renderLastSaved();
					if(newLoopId) {
						self.navigateToLoop(model.get('id'), {trigger:false});
					} else if(callback) {
						callback(true);
					}
				},
				error: function(model, response, options) {
					//self.renderLastSaved({error:true});
					self.model.set({
						'saving': false,
						'errorDetails': response.responseJSON,
						});
					self.showError("Save Error", response.responseJSON?response.responseJSON.message:response.statusText);
					if(callback) {
						callback(false);
					}
				}
			});
		},
		
		showBusyIcon: function() {
			this.$('.busy-icon').show();
		},

		hideBusyIcon: function() {
			this.$('.busy-icon').hide();
		}

	});
});
