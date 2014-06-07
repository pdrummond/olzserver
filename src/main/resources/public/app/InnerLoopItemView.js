var OlzApp = {};

$(function() {

	OlzApp.InnerLoopItemView = OlzApp.AbstractLoopView.extend({
		
		tagName: 'li',
		className: 'innerloop-item-container',
		
		events: {
			'click #innerloop-edit-button': 'onInnerLoopEditButtonClicked',
			'click #save-list-button': 'onSaveListButtonClicked',
			'click .last-updated-msg': 'onLoopSelected',
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
			this.renderLastUpdatedMsg();
			this.toggleVisible();
			
			return this.el;
		},
		
		toggleVisible: function () {
			this.$el.toggleClass('hide', !this.isVisible());
		},
		
		isVisible: function() {
			var visible = false;
			
			var query;// = this.query;
			
			if(!query) {
				var filterText = $('.filter-input').val();			
				if(filterText && filterText.length > 0) {
					query = filterText;
				}
			}

			if(query && query.length > 0) {
				query = query.toLowerCase();
				this.queryWords = query.split(' ');
				
				var id = this.model.get("id").toLowerCase();
				var content = this.model.get("content").toLowerCase();
				for(var i = 0; i<this.queryWords.length; i++) {
					var word = this.queryWords[i];
					visible = (content.indexOf(word) > -1 || id.indexOf(word) > -1);
					
					if(!visible) {
						break;
					}
				}
			} else {
				visible = true;
			}	
			return visible;
		},
		
		onInnerLoopEditButtonClicked: function() {
			this.onEditButtonClicked('innerloop');

//			var self = this;
//			this.editMode = !this.editMode;
//			if(this.editMode) {
//				this.$('#innerloop-edit-button').html('<span class="glyphicon glyphicon-floppy-disk">');
//				this.loopEditor = this.createLoopEditor(this.$('.innerloop .body'), 'xinnerloop-editor-toolbar');
//			} else {
//				this.$('#innerloop-edit-button').html('Saving...');
//				var newContent = this.loopEditor.getData();
//				this.destroyLoopEditor(this.loopEditor);
//				this.saveLoop(newContent, function() {
//					self.$('#innerloop-edit-button').html('<span class="glyphicon glyphicon-edit">');
//				});
//			}
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
