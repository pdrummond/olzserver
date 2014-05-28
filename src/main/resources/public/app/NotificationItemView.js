var OlzApp = {};

$(function() {

	OlzApp.NotificationItemView = Backbone.View.extend({
		tagName: 'li',
		
		events: {
			'click #delete-notification-button': 'onDeleteNotificationButtonClicked'
		},

		initialize: function(options) {
			var self = this;
			this.template = _.template($('#notification-item-view-template').html());
		},

		render: function() {			
			this.$el.html(this.template(this.model.attributes));
			return this.el;
		},
		
		onDeleteNotificationButtonClicked: function() {
			this.remove();
			this.trigger('delete', this);
		}
	});	
});
