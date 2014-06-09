var OlzApp = {};

$(function() {

	OlzApp.ShortcutsView = Backbone.View.extend({
		tagName: 'ul',
		className: 'shortcut-list',

		initialize: function(options) {
			var self = this;
			this.items = [];
			this.collection = new OlzApp.ShortcutsCollection();		
			this.listenTo(this.collection, 'all', this.render);
			
			this.collection.fetch();
		},

		render: function() {
			this.$el.empty();
			this.items = [];
			this.addItems(this.collection);
			return this.el;
		},

		addItems: function(items, options) {
			items.each(function(model) {
				this.addItem(model, options);
			}, this);
		},

		addItem: function(model, options) {
			var item = new OlzApp.ShortcutItemView({
				model:model, 
			});			
			this.$el.append(item.render());
			this.items.push(item);
		},
	});	
});
