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
			this.$el.html(this.template(this.model.attributes));
			return this.el;
		},
		
		onDeleteListButtonClicked: function() {
			this.remove();
			this.trigger('delete', this);
		},		
	});	
});
