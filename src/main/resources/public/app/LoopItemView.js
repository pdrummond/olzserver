var OlzApp = {};

$(function() {

	OlzApp.LoopItemView = Backbone.View.extend({

		className: 'loop-item-container',

		initialize: function() {
			this.template = _.template($('#loop-item-template').html());
			this.listenTo(this.model, 'change', this.render);
			this.editMode = false;
		},

		render: function(){			
			this.$el.html(this.template(_.extend(this.model.attributes, {editMode: this.editMode})));
			var self = this;
			if(this.editMode && !this.loopEditor) {
				this.loopEditor = new OlzApp.LoopEditor({
					el: self.$(".loop > .body")  
				});	
			} else {
				if(self.loopEditor) {
					self.loopEditor.destroy();
					delete self.loopEditor;				
				}
				self.editMode = false;
			}
			
			this.toggleVisible();
			
			return this.el;
		},
		
		toggleVisible: function () {
			this.$el.toggleClass('hide', !this.isVisible());
		},
		
		isVisible: function() {
			var visible = false;

			var filterText = $('.filter-input').val().trim();
			if(filterText && filterText.length > 0) {
				filterText = filterText.toLowerCase();
				var content = this.model.get("content").toLowerCase();
				visible = content.indexOf(filterText) > -1;
			} else {
				visible = true;
			}	
			return visible;
		}


	});
});
