var OlzApp = {};

$(function() {

	OlzApp.NotificationView = Backbone.View.extend({

		initialize: function(options) {
			var self = this;
			this.items = [];
			this.template = _.template($('#notification-view-template').html());
			this.collection = new OlzApp.LoopCollection();			
			this.lastUpdatedTime = 0;//new Date().getTime();
			this.poller = setInterval(function() { 
				self.fetchNewItems(); 
			}.bind(this), 10000);	
		},
		
		close: function() {			
			clearInterval(this.poller);
		},

		render: function() {
			this.$el.html(this.template());
			this.$("#notify-box ul").empty();
			this.items = [];
			var self = this;
			_.each(this.collection.models, function(item) {				
				self.addItem(item);
			});
			return this.el;
		},
		
		fetchNewItems: function () {
			var self = this;
			var newCollection = new OlzApp.LoopCollection();
			newCollection.query = "#notification @!" + OlzApp.user.userId;
			newCollection.since = this.lastUpdatedTime;
			newCollection.fetch({
				success: function(collection, resp) {
					self.lastUpdatedTime = new Date().getTime();
					//var existingNewLoopCount = $('.from-server').length;
					var numItemsAdded = self.addItems(collection, {addToTop: true, fromServer:true});

					var newItemCount = numItemsAdded + self.collection.length;

					if(newItemCount === 0) {
						$('#notification-count').html('0');
						$('#notification-button').hide();
					} else if(newItemCount === 1) {
						$('#notification-count').html('1');
						$('#notification-button').show();
					} else if(newItemCount > 1) {
						$('#notification-count').html(newItemCount);
						$('#notification-button').show();					
					}
				},
				error: function(collection, response) {
					self.showError("Error fetching notifications", response.statusText);
				}
			});
		}, 
		
		addItems: function(items) {
			var numItems = 0;
			items.each(function(model) {
				if(this.addItem(model)) {
					numItems++;
				}
			}, this);
			return numItems;
		},

		addItem: function(itemModel) {
			var itemView = new OlzApp.NotificationItemView({model:itemModel});
			if(!this.itemExists(itemView.model.get('id'))) {
				this.$('#notify-box ul').prepend(itemView.render());
				this.collection.add(itemView);
				return true;
			} else {
				return false;
			}
		},

		itemExists: function(id) {
			var alreadyInList = _.find(this.items, function(item){ 
				return item.model.get('id') == id; 
			});			
			return alreadyInList;
		}		
	});	
});
