var OlzApp = {};

$(function() {

	OlzApp.LoopItemView = OlzApp.AbstractLoopView.extend({

		tagName: 'li',
		className: 'loop-item-container',

		events: {
			'click #loop-edit-button': 'onLoopEditButtonClicked',
			'click #expand-button': 'onExpandButtonClicked',
			'click #save-list-button': 'onSaveListButtonClicked',
			'click #do-save-list-button': 'onDoSaveListButtonClicked',
			'click .loop-item': 'onLoopSelected',
		},		

		initialize: function(options) {

			this.showDetail = options.showDetail || false;
			this.query = options.query;
			this.expandLists = options.expandLists;
			this.template = _.template($('#loop-item-template').html());
			this.listenTo(this.model, 'change', this.render);
			this.editMode = false;	
			this.fromServer = options.fromServer;
			this.expandLoop = false;
			if(this.fromServer) {
				this.$el.css('display', 'none');
			}
			var self = this;			
			this.render();
		},

		render: function() {
			var self = this;
			var attrs = _.clone(this.model.attributes);
			this.$el.html(this.template(_.extend(attrs, {id: this.model.get('id') || ""}, this.getViewHelpers())));

			this.renderLastUpdatedMsg();


			if(this.showDetail) {
				this.$('#loop-edit-button').show();
				this.$('#expand-button').show();
				this.$el.removeClass('hide-detail').addClass('show-detail');	
				this.$(".innerloop-container").show();
				this.$(".list-totals-box").hide();
				this.renderLists();

			} else {
				this.$('#loop-edit-button').hide();
				this.$('#expand-button').hide();
				this.$el.removeClass('show-detail').addClass('hide-detail');
				this.$(".innerloop-container").hide();
				this.$(".list-totals-box").show();
				this.renderListTotals();
			}			

			if(this.fromServer) {
				this.$el.toggleClass('from-server', this.fromServer);			
			} else {
				this.$el.removeClass('from-server');
			}
			this.toggleVisible();

			return this.el;
		},

		renderLists: function() {
			this.listViews = [];
			this.lists = this.model.get('lists');
			this.$('.list-button-bar').empty();
			for(var i=0; i<this.lists.length; i++) {
				var list = this.lists[i];

				var listName = list.loops.length + " " + list.name;
				var tabId = "tab" + i;
				this.$('.list-button-bar').append("<li><a href='#" + tabId + "' data-toggle='tab'>" + listName + "</a></li>");

				this.listViews[i] = new OlzApp.InnerLoopListView({listData: list, collection: new OlzApp.LoopCollection(list.loops), expandLists:true});
				this.$('.tab-content').append("<div class='tab-pane' id='" + tabId + "'></div>");

				this.$('#' + tabId).append(this.listViews[i].render());

			}

		},
		renderListTotals: function() {
			this.lists = this.model.get('lists');
			this.$('.list-totals-box ul').empty();
			for(var i=0; i<this.lists.length; i++) {
				var list = this.lists[i];
				if(list.loops.length > 0) {
					var listName = list.loops.length + " " + list.name;
					this.$('.list-totals-box ul').append('<li><span class="glyphicon glyphicon-list-alt"/><strong> ' + list.loops.length + '</strong> ' + list.name + '</li>');
				}
			}
		},

		renderLastUpdatedMsg: function() {
			if(this.model.get('createdAt') === this.model.get('updatedAt')) {
				this.$(".last-updated-msg").html("Created " + moment(this.model.get('createdAt')).fromNow());
			} else if(this.model.get('updatedAt')) {
				this.$(".last-updated-msg").html("Updated " + moment(this.model.get('updatedAt')).fromNow());
			} 
		},

		renderLoopAge: function() {
			if(this.model.get('updatedAt') > new Date().getTime() - 10000) {
				this.$el.addClass('new-loop');
			} else {
				this.$el.removeClass('new-loop');
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

		onLoopEditButtonClicked: function() {
			this.$('.loop-body').show();
			this.onEditButtonClicked('loop');
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
			Backbone.history.navigate("#loop/" + encodeURIComponent(this.model.get('id')), {trigger:true});
		},

		onExpandButtonClicked: function() {
			this.expandLoop = !this.expandLoop;
			this.render();
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
			/*try {
			console.log("saveLoop: " + JSON.stringify(this.model));
			} catch(err) {
				alert("THAT FEKING CIRCULAR ERROR AGAIN: " + err);
				debugger;
			}*/

			var self = this;
			var content = "<div data-type='loop'>" + this.generateContent(body) + "</div>";
			this.model.save({'content': content }, {
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
