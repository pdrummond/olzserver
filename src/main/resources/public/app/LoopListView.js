var OlzApp = {};

$(function() {

	OlzApp.LoopListView = OlzApp.AbstractLoopView.extend({

		initialize: function(options) {
			var self = this;
			this.collection = options.collection;
			this.loopItems = [];
			this.template = _.template($('#loop-list-template').html());
		},

		render: function() {
			this.$el.html(this.template());
			this.$("#items").empty();
			this.loopItems = [];
			this.collection.each(this.addLoopItem, this);
			
			/*
			this.$("#items").empty();
			this.loopItems = [];
			var self = this;
			_.each(this.model.get('loops'), function(loop) {		
				var loopItemView = new OlzApp.LoopItemView({model:new OlzApp.LoopModel(loop)});
				self.addLoopItem(loopItemView);
			});*/
			return this.el;
		},

		addLoopItem: function(model) {
			var loopItem = new OlzApp.LoopItemView({model:model});
			this.$('#items').append(loopItem.render());
			this.loopItems.push(loopItem);

		},

		prependLoopItem: function(loopView) {
			if(!this.innerloopExists(loopView.model.get('id'))) {
				this.$('#items').prepend(loopView.render());
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
