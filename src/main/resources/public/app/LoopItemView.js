var OlzApp = {};

$(function() {

	OlzApp.LoopItemView = OlzApp.AbstractLoopView.extend({
		
		tagName: 'li',
		className: 'loop-item-container',
		
		events: {
			'click #edit-button': 'onEditButtonClicked',
			'click #expand-button': 'onExpandButtonClicked',
			'click #save-list-button': 'onSaveListButtonClicked',
			'click #do-save-list-button': 'onDoSaveListButtonClicked',
			'click .loop .body': 'onLoopSelected',
			'keypress .innerloop-create-input': 'onCreateInput',			
		},		

		initialize: function(options) {
			console.log("OlzApp.LoopItemView");
			this.query = options.query;
			this.expandInnerLoops = options.expandInnerLoops;
			this.collection = options.collection;
			this.template = _.template($('#loop-item-template').html());
			this.listenTo(this.model, 'change', this.render);
			this.editMode = false;
			
			var self = this;
			this.lastSavedInterval = setInterval(function() {
				self.renderLastUpdatedMsg();
			}, 60000);
		},

		render: function(){
			var self = this;
			var attrs = _.clone(this.model.attributes);
			this.$el.html(this.template(_.extend(attrs, {id: this.model.get('id') || ""}, this.getViewHelpers())));
			if(this.model.get('showInnerLoops')) {
				//this.renderInnerLoops();
				this.$(".innerloop-container").show();
			} else {
				this.$(".innerloop-container").hide();
			}
			
			this.renderLastUpdatedMsg();
			
			this.lists = this.model.get('lists');
			
			for(var i=0; i<this.lists.length; i++) {
				var list = this.lists[i];				
				this.$('.list-button-bar').append("<li><a>" + list.name + "</a></li>");

				if(this.expandInnerLoops) {					
					var collection = new OlzApp.LoopCollection();
					collection.options = {
							query: list.query
					};
					list.view = new OlzApp.LoopListView({collection: collection, expandInnerLoops:false, query:list.query});
					this.loopListView = list.view;
					this.$('.innerloop-container').append(list.view.render());
					list.view.collection.fetch({
						success: function(model, resp) {
							//self.subscribeToHashtagChanges(loopId);
							list.view.render();
						},
						error: function(model, response) {
							self.showError("Error getting loop!", response.statusText);
						}
					});
				
				/*<% for
					<li class="active"><a>Comments</a></li>
					<li><a><span class="badge pull-right">42</span>Up Votes </a></li>
					<li><a>History</a></li>*/

				}
			}
			
			
			this.toggleVisible();
			
			return this.el;
		},
		
		renderLastUpdatedMsg: function() {
			if(this.model.get('updatedAt')) {
				this.$(".last-updated-msg").html("Updated " + moment(this.model.get('updatedAt')).fromNow());
			}
		},
		
		onExpandButtonClicked: function() {
			this.model.set('showInnerLoops', !this.model.get('showInnerLoops'));
			this.saveLoopFieldToServer('showInnerLoops', this.model.get('showInnerLoops'));
		},

		
		toggleVisible: function () {
			this.$el.toggleClass('hide', !this.isVisible());
		},
		
		isVisible: function() {
			var visible = false;
			
			var query = this.query;
			
			if(!query) {
				var filterText = $('.filter-input').val();			
				if(filterText && filterText.length > 0) {
					query = filterText;
				}
			}

			if(this.query && this.query.length > 0) {				
				var content = this.model.get("content").toLowerCase();
				visible = content.indexOf(query) > -1;
			} else {
				visible = true;
			}	
			return visible;
		},
		
		onEditButtonClicked: function() {
			var self = this;
			this.editMode = !this.editMode;
			if(this.editMode) {
				this.$('#edit-button').html('Save');
				this.$('.loop .body').hide();
				this.$('.loop').append("<textarea class='loop-textarea'>" +  this.model.get('content') + "</textarea>")
			} else {
				var newContent = this.$('.loop-textarea').val();

				this.$('#edit-button').html('Saving...');
				this.$('.loop-textarea').hide();
				this.$('.loop .body').show();

				this.saveLoop(newContent, function() {
					self.$('#edit-button').html('Edit');
				});
			}
		},

		saveLoop: function(body, callback) {
			var self = this;
			this.model.save({'content': this.generateContent(body) }, {
				success: function() {
					self.lastSaved = new Date();
					self.renderLastSaved();
					if(callback) {
						callback(true);
					}
				},
				error: function(model, response, options) {
					self.renderLastSaved({error:true});
					self.showError("Save Error", response.statusText);
					if(callback) {
						callback(false);
					}
				}
			});
		},

		saveLoopFieldToServer: function(fieldName, value) {
			var data = {};
			data[fieldName] = value;
			var self = this;
			$.post('/loop/field?loopId=' + encodeURIComponent(this.model.get('id')) + "&" + fieldName + "=" + value).fail(function(xhr) {
				self.showError("Error Saving field " + fieldName, "BOOM - this is for testing - remove this growl once working");
			});
		},
		
		onLoopSelected: function() {
			Backbone.history.navigate("#query/" + encodeURIComponent(this.model.get('id')), {trigger:true});
		},
		
		onSaveListButtonClicked: function() {			
			this.$(".list-input-container").animate({width:'toggle'});
			this.$("#do-save-list-button").fadeIn();
		},
		
		onDoSaveListButtonClicked: function() {
			var model = new Backbone.Model();
			model.url = "/lists";
			model.set("loopId", this.model.get("id"));
			model.set("name", this.$(".list-input").val().trim());
			model.set("query", this.$(".filter-input").val().trim());
			model.save();
		},
		
		onCreateInput: function(e) {
			if(e.keyCode == 13) {
				var input = this.$('.innerloop-create-input').val().trim();
				this.createLoop(input);
				this.$('.innerloop-create-input').select();
			}
		},
		
		getLoopBodyEl: function() {
			return ".loop > .body";
		}

	});
});
