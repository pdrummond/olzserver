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
			this.parentModel = options.parentModel;
			this.collection = new OlzApp.LoopCollection(this.listData.loops, {boom: "BOOM"}); 
			this.collection.comparatorField = this.listData.comparator;
			this.collection.sortOrder = this.listData.sortOrder;
			this.collection.sort();
			this.template = _.template($('#innerloop-list-template').html());
			this.listenTo(this.collection, 'reset', this.render);
		},

		render: function() {
			this.$el.html(this.template());
			this.renderList();
			return this.el;
		},

		renderList: function() {
			this.$('.item-list').empty();
			this.loopItems = [];
			this.collection.each(this.addLoopItem, this);
		},

		addLoopItem: function(model) {
			var loopItem = new OlzApp.InnerLoopItemView({model:model, query: this.listData.query});
			this.$('.item-list').append(loopItem.render());
			this.loopItems.push(loopItem);
		},
		
		createLoop: function(body, options) {
			var self = this;
			var ownerTag = '@!' + OlzApp.user.userId;
			
			var query = this.listData.query + " " + this.parentModel.get('id').substring(0, 5);			
			/*
			 * If the owner is not the current user, then need to change
			 * the current owner to a follower.
			 */
			var tags;
			var loopOwner = TagString.findOwnerTag_(query);
			if(loopOwner == OlzApp.user.userId) {
				tags = query;
			} else {
				tags = TagString.makeExistingOwnerAFollower(query);
			}
			if(!TagString.findTag(ownerTag, tags)) {
				tags = ownerTag + " " + query;
			}
 
			var content = 
				'<div data-type="loop">'
				+ '<div data-type="loop-header" class="loop-header">' + this.generateContent(body) + '</div>' 
				+ '<div data-type="loop-body"   class="loop-body"></div>'  
				+ '<div data-type="loop-footer" class="loop-footer">' + tags + '</div>' + 
				'</div>';		

			var now = new Date().getTime();

			var loopModel = new OlzApp.LoopModel({content:content, createdAt: now, updatedAt: now});
			if(options && options.parentLoopId) {
				loopModel.parentLoopId = options.parentLoopId;
			}			
			loopModel.save(null, {
				success: function(loop) {
					self.addLoopItem(loopModel);
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
