var OlzApp = {};

$(function() {

	OlzApp.LoopListView = OlzApp.AbstractLoopView.extend({
		tagName: 'ul',
		className: 'loop-list',

		initialize: function(options) {
			this.showDetail = options.showDetail;
			this.query = options.query;
			this.expandLists = options.expandLists;
			var self = this;
			this.loopItems = [];
			this.collection = options.collection;
			this.listenTo(this.collection, 'reset', this.render);

			this.touchInterval = setInterval(function() {
				self.touchAllLoops();
			}, 60000);

			this.lastUpdatedTime = new Date().getTime();
			/*this.loopPoller = setInterval(function() { 
				this.fetchNewLoops(); 
			}.bind(this), 1200000);*/
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
			var newCollection = new OlzApp.LoopCollection();
			newCollection.query = this.query;
			newCollection.since = this.lastUpdatedTime;
			newCollection.fetch({
				success: function(collection, resp) {
					self.lastUpdatedTime = new Date().getTime();
					var existingNewLoopCount = $('.from-server').length;
					var numItemsAdded = self.addLoopItems(collection, {addToTop: true, fromServer:true});
					
					var newLoopCount = existingNewLoopCount + numItemsAdded;
					
					if(newLoopCount === 1) {
						$('#incoming-loops-msg').html(' 1 New Loop').fadeIn();
					} else if(newLoopCount > 1) {
						$('#incoming-loops-msg').html(newLoopCount + ' New Loops').fadeIn();
					}
				},
				error: function(collection, response) {
					self.showError("Error fetching new loops", response.statusText);
				}
			});			
		},

		touchAllLoops: function() {
			_.each(this.loopItems, function(loopView) {
				loopView.renderLastUpdatedMsg();
				loopView.renderLoopAge();
			});
		},

		addLoopItems: function(items, options) {
			var numItems = 0;
			items.each(function(model) {
				if(this.addLoopItem(model, options)) {
					numItems++;
				}
			}, this);
			return numItems;
		},

		addLoopItem: function(model, options) {
			var alreadyInList = _.find(this.loopItems, function(loopItem){ 
				return model.get('id') == loopItem.model.get('id'); 
			});
			if(!model.has('id') || !alreadyInList) {
				var fromServer = options && options.fromServer;
				var loopItem = new OlzApp.LoopItemView({
					model:model, 
					expandLists:this.expandLists, 
					query: this.query, 
					fromServer: fromServer,
					showDetail: this.showDetail
				});			
				if(options && options.addToTop) {
					this.$el.prepend(loopItem.render());
				} else {
					this.$el.append(loopItem.render());
				}
				this.loopItems.push(loopItem);
				return true;
			} else {
				return false;
			}
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

		findLoop: function(loopId) {
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
