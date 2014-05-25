var OlzApp = {};

$(function() {

	OlzApp.InnerLoopListView = OlzApp.AbstractLoopView.extend({
		className: 'innerloop-list',
		
		events: {
			'keypress .innerloop-create-input': 'onInnerLoopCreateInput',
			'input .filter-input': 'onFilterInput',
		},

		initialize: function(options) {
			this.listData = options.listData;
			var self = this;
			this.loopItems = [];
			this.collection = options.collection;
			this.template = _.template($('#innerloop-list-template').html());
			this.listenTo(this.collection, 'reset', this.render);
		},

		render: function() {
			this.$el.html(this.template());
			this.$('.filter-input').val(this.listData.query);
			this.renderList();
			return this.el;
		},
		
		renderList: function() {
			this.$('.item-list').empty();
			this.loopItems = [];
			this.collection.each(this.addLoopItem, this);
		},

		addLoopItem: function(model) {
			var loopItem = new OlzApp.InnerLoopItemView({model:model, query: this.$('.filter-input').val().trim()});
			this.$('.item-list').append(loopItem.render());
			this.loopItems.push(loopItem);
		},
		
		createLoop: function(body, options) {
			var self = this;
			var content = this.generateContent(body);
			var loopModel = new OlzApp.LoopModel({content:content});
			loopModel.save(null, {
				success: function(loop) {
					self.addLoopItem(loop);
				}
			})

			//this.stompClient.send("/app/hello", {}, JSON.stringify({ 'name': "BOOM" }));
		},
		
		onInnerLoopCreateInput: function(e) {
			if(e.keyCode == 13) {
				var input = this.$('.innerloop-create-input:first').val();
				this.createLoop(input + " " + $('.filter-input').val().trim());
				this.$('.innerloop-create-input').select();
			}
		},
		
		onFilterInput: function() {
			this.renderList();
			//this.saveLoopFieldToServer('filterText', this.model.get('filterText'));
		},

	});	
});
