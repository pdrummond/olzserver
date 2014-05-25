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
			'click .last-updated-msg': 'onLoopSelected',
		},		

		initialize: function(options) {
			console.log("OlzApp.LoopItemView");
			this.query = options.query;
			this.expandLists = options.expandLists;
			this.collection = options.collection;
			this.template = _.template($('#loop-item-template').html());
			this.listenTo(this.model, 'change', this.render);
			this.editMode = false;

			var self = this;			
		},

		render: function() {
			console.log("LoopItemView.render()");

			var self = this;
			var attrs = _.clone(this.model.attributes);
			this.$el.html(this.template(_.extend(attrs, {id: this.model.get('id') || ""}, this.getViewHelpers())));

			this.renderLastUpdatedMsg();

			if(this.model.get('showInnerLoops')) {
				this.renderLists();
				this.$(".innerloop-container").show();
			} else {
				this.$(".innerloop-container").hide();
			}


			this.toggleVisible();

			return this.el;
		},

		renderLists: function() {
			console.log("renderLists");
			this.lists = this.model.get('lists');
			this.$('.list-button-bar').empty();
			for(var i=0; i<this.lists.length; i++) {
				var list = this.lists[i];

				var listName = list.loops.length + " " + list.name;
				var tabId = "tab" + i;
				this.$('.list-button-bar').append("<li><a href='#" + tabId + "' data-toggle='tab'>" + listName + "</a></li>");

				list.view = new OlzApp.InnerLoopListView({listData: list, collection: new OlzApp.LoopCollection(list.loops), expandLists:true});
				this.loopListView = list.view;				  
				this.$('.tab-content').append("<div class='tab-pane' id='" + tabId + "'></div>");

				this.$('#' + tabId).append(list.view.render());

			}

		},

		renderLastUpdatedMsg: function() {
			if(this.model.get('createdAt') === this.model.get('updatedAt')) {
				this.$(".last-updated-msg").html("Created " + moment(this.model.get('createdAt')).fromNow());
			} else if(this.model.get('updatedAt')) {
				this.$(".last-updated-msg").html("Updated " + moment(this.model.get('updatedAt')).fromNow());
			} 
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
				this.$('#edit-button').html('<span class="glyphicon glyphicon-floppy-disk">');
				this.loopEditor = this.createLoopEditor(this.$('.loop .body'));
			} else {
				this.$('#edit-button').html('Saving...');
				var newContent = this.loopEditor.getData();
				this.destroyLoopEditor(this.loopEditor);
				this.saveLoop(newContent, function() {
					self.$('#edit-button').html('<span class="glyphicon glyphicon-edit">');
					
				});
			}
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

		onExpandButtonClicked: function() {
			this.model.set('showInnerLoops', !this.model.get('showInnerLoops'));
		},

		createList: function(name, query) {
			var listModel = new Backbone.Model();
			listModel.url = "/lists";
			listModel.set("loopId", this.model.get("id"));
			listModel.set("name", name);
			listModel.set("query", query);
			listModel.save();
			this.model.get('lists').push(listModel.toJSON());			
		},

		onSaveListButtonClicked: function() {
			this.$(".list-input-container").animate({width:'toggle'});
			this.$("#do-save-list-button").fadeIn();
		},

		onDoSaveListButtonClicked: function() {
			this.createList(this.$(".list-input").val().trim(), this.$(".filter-input").val().trim());
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


	});
});
