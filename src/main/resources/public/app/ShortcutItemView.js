var OlzApp = {};

$(function() {

	OlzApp.ShortcutItemView = Backbone.View.extend({
		tagName: 'li',
		
		events: {
			'click #delete-list-button': 'onDeleteListButtonClicked',
		},

		initialize: function(options) {
			var self = this;			
			this.template = _.template($('#shortcut-item-view-template').html());			
			this.listenTo(this.model, 'change', this.render);
		},

		render: function() {
			var attrs = _.clone(this.model.attributes);
			if(this.model.get('title') === 'Now List') {
				attrs.newLoopCount = 2;
			} else {
				attrs.newLoopCount = 0;
			}
			this.$el.html(this.template(attrs));
			return this.el;
		},
		
		onDeleteListButtonClicked: function() {
			this.remove();
			this.trigger('delete', this);
		},		
	});	
});
