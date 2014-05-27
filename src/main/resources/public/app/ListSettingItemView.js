var OlzApp = {};

$(function() {

	OlzApp.ListSettingItemView = Backbone.View.extend({
		tagName: 'li',
		
		events: {
			'click #delete-list-button': 'onDeleteListButtonClicked'
		},

		initialize: function(options) {
			var self = this;
			this.template = _.template($('#list-setting-item-view-template').html());
		},

		render: function() {
			this.$el.html(this.template(this.model.attributes));
			this.$('.list-name-input').val(this.model.get('name'));
			this.$('.list-query-input').val(this.model.get('query'));
			return this.el;
		},
		
		onDeleteListButtonClicked: function() {
			this.remove();
			this.trigger('delete', this);
		}
	});	
});
