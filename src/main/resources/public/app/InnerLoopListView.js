var OlzApp = {};

$(function() {

	OlzApp.InnerLoopListView = OlzApp.AbstractLoopView.extend({
		tagName: 'ul',
		className: 'innerloop-list',

		initialize: function(options) {
			var self = this;
			this.loopItems = [];
			this.collection = options.collection;
			this.listenTo(this.collection, 'reset', this.render);
		},

		render: function() {
			this.$el.empty();
			this.loopItems = [];
			this.collection.each(this.addLoopItem, this);
			
			return this.el;
		},

		addLoopItem: function(model) {
			var loopItem = new OlzApp.InnerLoopItemView({model:model});
			this.$el.append(loopItem.render());
			this.loopItems.push(loopItem);
		},

		prependLoopItem: function(loopView) {
			if(!this.innerloopExists(loopView.model.get('id'))) {
				this.$el.prepend(loopView.render());
				this.loopItems.push(loopView);
			}
		},

		innerloopExists: function(loopId) {
			var found = false;
			for(var i=0; i<this.loopItems.length; i++) {
				if(this.loopItems[i].model.get('id') == loopId) {
					found = true;
					break;
				}
			}
			return found;
		},

		createLoop: function(body, options) {
			var self = this;
			var loopModel = new OlzApp.LoopModel({content:"<p>" + this.generateContent(body) + "</p>"});
			if(options && options.parentLoopId) {
				loopModel.parentLoopId = options.parentLoopId;
			}			
			loopModel.save();
			/*null, {
				success: function(loop) {
					var loopView = new LoopItemView({model:loopModel});
					self.addLoopItem(loopView);
				}
			})*/

			//this.stompClient.send("/app/hello", {}, JSON.stringify({ 'name': "BOOM" }));
		},

		findInnerLoopInModel: function(loopId) {
			var loop = null;
			var loops = this.model.get('loops');
			for(var i=0; i<loops.length; i++) {
				if(loops[i].id === loopId) {
					loop = loops[i];
					break;
				}				
			}
			if(!loop == null) {
				throw "Cannot find innerloop in model data";
			} 
			return loop;
		},

		onCreateInnerLoopButtonClicked: function() {
			var model = new OlzApp.LoopModel();
			model.set('content', this.model.get('id') + "/" + uuid.v4().substring(0,4) + ":");
			var loopView = new OlzApp.LoopItemView({model:model});
			this.prependLoopItem(loopView);
			loopView.toggleEditMode();			
		},

		getLoopBodyEl: function() {
			return ".loop-inner > .loop > .body";
		}
	});	
});
