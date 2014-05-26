var OlzApp = {};

$(function() {

	OlzApp.LoopListView = OlzApp.AbstractLoopView.extend({
		tagName: 'ul',
		className: 'loop-list',

		initialize: function(options) {
			this.query = options.query;
			this.expandLists = options.expandLists;
			var self = this;
			this.loopItems = [];
			this.collection = options.collection;
			this.listenTo(this.collection, 'reset', this.render);
			
			this.renderLastUpdatedMsgInterval = setInterval(function() {
				self.renderLastUpdatedMsgForAllLoops();
			}, 60000);
			
			/*this.collection.since = new Date().getTime();
			this.loopPoller = setInterval(function() { 
				this.fetchNewLoops(); 
			}.bind(this), 60000);*/
			
			//this.fetchNewLoops();
		},
		
		close: function() {
			clearInterval(this.renderLastUpdatedMsgInterval);
			clearInterval(this.loopPoller);
		},

		render: function() {
			this.$el.empty();
			this.loopItems = [];
			this.addLoopItems(this.collection);
			return this.el;
		},
		
		fetchNewLoops: function() {
			var self = this;
			this.collection.fetch({
				success: function(collection, resp) {
					self.collection.since = new Date().getTime();
					self.addLoopItems(collection);
				},
				error: function(collection, response) {
					self.showError("Error fetching new loops", response.statusText);
				}
			});			
		},
		
		renderLastUpdatedMsgForAllLoops: function() {
			_.each(this.loopItems, function(loopView) {
				loopView.renderLastUpdatedMsg();
			});
		},
		
		addLoopItems: function(items) {
			items.each(this.addLoopItem, this);
		},
		
		addLoopItem: function(model) {
			var loopItem = new OlzApp.LoopItemView({model:model, expandLists:this.expandLists, query: this.query});
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

		getAllowedBodyTags: function() {
			return ".body p, .body b, .body i, .body ul, .body h1, .body h2, .body h3";
		},

		toggleInnerLoops: function() {
			this.model.set('showInnerLoops', !this.model.get('showInnerLoops'));
			this.saveLoopFieldToServer('showInnerLoops', this.model.get('showInnerLoops'));
		},

		saveLoopFieldToServer: function(fieldName, value) {
			var data = {};
			data[fieldName] = value;
			var self = this;
			$.post('/loop/field?loopId=' + encodeURIComponent(this.model.get('id')) + "&" + fieldName + "=" + value).fail(function(xhr) {
				self.showError("Error Saving field " + fieldName, "BOOM - this is for testing - remove this growl once working");
			});
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
