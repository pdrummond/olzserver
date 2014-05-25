var OlzApp = {};

$(function() {

	OlzApp.InnerLoopItemView = OlzApp.AbstractLoopView.extend({
		
		tagName: 'li',
		className: 'innerloop-item-container',
		
		events: {
			'click #edit-button': 'onEditButtonClicked',
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
				var content = this.model.get("content").toLowerCase();
				visible = content.indexOf(query) > -1;
			} else {
				visible = true;
			}	
			return visible;
		},
		
		onEditButtonClicked: function() {
			var self = this;
			this.editMode = !this.editMode;
			if(this.editMode) {
				this.$('#edit-button').html('Save');
				this.$('.loop .body').hide();
				this.$('.loop').append("<textarea class='loop-textarea'>" +  this.model.get('content') + "</textarea>")
			} else {
				var newContent = this.$('.loop-textarea').val();

				this.$('#edit-button').html('Saving...');
				this.$('.loop-textarea').hide();
				this.$('.loop .body').show();

				this.saveLoop(newContent, function() {
					self.$('#edit-button').html('Edit');
				});
			}
		},

		onLoopSelected: function() {
			Backbone.history.navigate("#query/" + encodeURIComponent(this.model.get('id')), {trigger:true});
		},
		
		getLoopBodyEl: function() {
			return ".loop > .body";
		}

	});
});
