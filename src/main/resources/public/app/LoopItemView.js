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
			
			return this.el;
		},
	});
});
