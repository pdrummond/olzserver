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
		
		findTagInString: function(tagToFind, str) {
			var tagFound = _.find(str.split(' '), function(tag) { return tag === tagToFind; });
			return tagFound;
		},
		
		makeExistingOwnerAFollower: function(tagString) {
			return _.map(tagString.split(' '), function(tag){
				if(tag.indexOf('@!') != -1) {
					return tag.replace('@!', '@');
				} else {
					return tag;
				}				
			});			
		},

		createLoop: function(body, options) {
			var self = this;
			var ownerTag = '@!' + OlzApp.user.userId;
			
			/*
			 * FIXME: If the owner is not the current user, then need to change
			 * the current owner to a follower.
			 */
			
			var	tags = this.listData.query;//this.makeExistingOwnerAFollower(this.listData.query);
			if(!this.findTagInString(ownerTag, this.listData.query)) {
				tags = ownerTag + " " + this.listData.query;
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
