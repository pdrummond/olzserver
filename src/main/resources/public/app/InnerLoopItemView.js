var OlzApp = {};

$(function() {

	OlzApp.InnerLoopItemView = OlzApp.AbstractLoopView.extend({
		
		tagName: 'li',
		className: 'innerloop-item-container',
		
		events: {
			'click #innerloop-edit-button': 'onEditButtonClicked',
			'click #save-list-button': 'onSaveListButtonClicked',
			'click .loop .body': 'onLoopSelected',
		},		

		initialize: function(options) {
			console.log("OlzApp.LoopItemView");
			this.query = options.query;
			this.expandInnerLoops = options.expandInnerLoops;
			this.collection = options.collection;
			this.template = _.template($('#innerloop-item-template').html());
			this.listenTo(this.model, 'change', this.render);
			this.editMode = false;
			
			var self = this;			
		},

		render: function() {
			var self = this;
			var attrs = _.clone(this.model.attributes);
			this.$el.html(this.template(_.extend(attrs, {id: this.model.get('id') || ""}, this.getViewHelpers())));
			this.toggleVisible();
			return this.el;
		},
		
		toggleVisible: function () {
			this.$el.toggleClass('hide', !this.isVisible());
		},
		
		isVisible: function() {
			var visible = false;
			
			var query = this.query;
			
			if(!query) {
				var filterText = $('.filter-input').val();			
				if(filterText && filterText.length > 0) {
					query = filterText;
				}
			}

			if(this.query && this.query.length > 0) {
				
				this.queryWords = this.query.split(' ');
				
				var content = this.model.get("content").toLowerCase();
				for(var i = 0; i<this.queryWords.length; i++) {
					visible = content.indexOf(this.queryWords[i]) > -1;
					if(!visible) {
						break;
					}
				}
			} else {
				visible = true;
			}	
			return visible;
		},
		
		onEditButtonClicked: function() {
			var self = this;
			this.editMode = !this.editMode;
			if(this.editMode) {
				this.$('#innerloop-edit-button').html('<span class="glyphicon glyphicon-floppy-disk">');
				this.loopEditor = this.createLoopEditor(this.$('.innerloop .body'), 'xinnerloop-editor-toolbar');
			} else {
				this.$('#innerloop-edit-button').html('Saving...');
				var newContent = this.loopEditor.getData();
				this.destroyLoopEditor(this.loopEditor);
				this.saveLoop(newContent, function() {
					self.$('#innerloop-edit-button').html('<span class="glyphicon glyphicon-edit">');
				});
			}
		},

		onLoopSelected: function() {
			Backbone.history.navigate("#query/" + encodeURIComponent(this.model.get('id')), {trigger:true});
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
