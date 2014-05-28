var OlzApp = {};

$(function() {

	OlzApp.ListSettingItemView = Backbone.View.extend({
		tagName: 'li',
		
		events: {
			'click #delete-list-button': 'onDeleteListButtonClicked',
			'click #list-order-button': 'onListOrderButtonClicked'
		},

		initialize: function(options) {
			var self = this;			
			this.template = _.template($('#list-setting-item-view-template').html());			
			this.listenTo(this.model, 'change', this.render);
		},

		render: function() {
			this.$el.html(this.template(this.model.attributes));
			this.$('.list-name-input').val(this.model.get('name'));
			this.$('.list-query-input').val(this.model.get('query'));
			this.$('.list-comparator-input').val(this.model.get('comparator'));
			if(this.model.get('sortOrder') == "ascending") {
				this.$('#list-order-button span').removeClass('glyphicon-chevron-down').addClass('glyphicon-chevron-up');
			} else {										   
				this.$('#list-order-button span').removeClass('glyphicon-chevron-up').addClass('glyphicon-chevron-down');
			}
			return this.el;
		},
		
		onDeleteListButtonClicked: function() {
			this.remove();
			this.trigger('delete', this);
		},
		
		onListOrderButtonClicked: function() {
			var sortOrder = this.model.get('sortOrder');
			this.model.set('sortOrder', (sortOrder === 'ascending'?'descending':'ascending'));
		}
	});	
});
