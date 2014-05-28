var OlzApp = {};

$(function() {

	OlzApp.LoopItemView = OlzApp.AbstractLoopView.extend({

		tagName: 'li',
		className: 'loop-item-container',

		events: {
			'click #loop-edit-button': 'onLoopEditButtonClicked',
			'click #expand-button': 'onExpandButtonClicked',
			'click #list-settings-button': 'onListSettingsButtonClicked',
			'click #save-lists-button': 'onSaveListsButtonClicked',
			'click #cancel-lists-button': 'onCancelListsButtonClicked',
			'click .loop-item': 'onLoopSelected',
			'click #add-list-setting-item-button': 'onAddListSettingItemButtonClicked'
			
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
			this.renderListSettings();

			if(this.showDetail) {
				this.$('#loop-edit-button').show();
				this.$('#list-settings-button').show();
				this.$('#expand-button').show();
				this.$el.removeClass('hide-detail').addClass('show-detail');	
				this.$(".innerloop-container").show();
				this.$(".list-totals-box").hide();
				$(".create-input").hide();
				this.renderLists();

			} else {
				this.$('#loop-edit-button').hide();
				this.$('#list-settings-button').hide();
				this.$('#expand-button').hide();
				this.$el.removeClass('show-detail').addClass('hide-detail');
				this.$(".innerloop-container").hide();
				this.$(".list-totals-box").show();
				$(".create-input").show();
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

				this.listViews[i] = new OlzApp.InnerLoopListView({listData: list, expandLists:true});
				this.$('.tab-content').append("<div class='tab-pane fade' id='" + tabId + "'></div>");

				this.$('#' + tabId).append(this.listViews[i].render());
			}
			this.$('.list-button-bar :first').addClass('active');
			this.$('.tab-content :first').addClass('fade').addClass('in').addClass('active');

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
		
		renderListSettings: function() {
			this.listSettingViewItems = [];
			this.lists = this.model.get('lists');
			this.$('.list-settings-box ul').empty();
			for(var i=0; i<this.lists.length; i++) {
				this.addListSettingItem(this.lists[i]);
			}
		},
		
		onAddListSettingItemButtonClicked: function () {
			this.addListSettingItem();
		},
		
		addListSettingItem: function(list) {
			var view = new OlzApp.ListSettingItemView({model: new Backbone.Model(list)});
			this.listenTo(view, 'delete', this.onListSettingItemDeleted);
			this.listSettingViewItems.push(view);
			this.$('.list-settings-box ul').append(view.render());
		},
		
		onListSettingItemDeleted: function(view) {
			this.listSettingViewItems.pop(view);
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

		onExpandButtonClicked: function() {
			this.expandLoop = !this.expandLoop;
			this.render();
		},

		createList: function(name, query, comparator, sortOrder) {
			var listModel = new Backbone.Model();
			listModel.url = "/lists";
			listModel.set("loopId", this.model.get("id"));
			listModel.set("name", name);
			listModel.set("query", query);
			listModel.set("comparator", comparator);
			listModel.set("sortOrder", sortOrder);
			listModel.save();
			this.model.get('lists').push(listModel.toJSON());			
		},

		onListSettingsButtonClicked: function() {
			if($('.list-settings-box').css('right') === "200px") {
				this.showListSettingsBox();
			} else {
				this.hideListSettingsBox();
			}
		},
		
		showListSettingsBox: function() {
			$('#list-settings-button span').removeClass('glyphicon-chevron-right').addClass('glyphicon-chevron-left');
			$('.list-settings-box').animate({'right': '-600px', 'opacity': '1'});
			$(".content-wrapper").animate({'left': '-=650px'});
		},
		
		hideListSettingsBox: function() {
			$('#list-settings-button span').removeClass('glyphicon-chevron-left').addClass('glyphicon-chevron-right');
			$('.list-settings-box').animate({'right': '200px', 'opacity': '0'});
			$(".content-wrapper").animate({'left': '+=650px'});

		},

		onSaveListsButtonClicked: function() {
			var self = this;
			var url = '/loops/' + encodeURIComponent(this.model.get('id')) + '/lists';
			
			$.ajax({
				method: 'DELETE',
		    	beforeSend: function(xhr) {
		    		if(OlzApp.csrfToken) {
		    			xhr.setRequestHeader('X-CSRF-TOKEN', OlzApp.csrfToken);
		    		}
		    	},
				url: url,
				success: function() {
					_.each(self.listSettingViewItems, function(view) {
						var name = view.$(".list-name-input").val().trim();
						var query = view.$(".list-query-input").val().trim();
						var comparator = view.$(".list-comparator-input").val().trim();
						var sortOrder = view.model.get('sortOrder');
						self.createList(name, query, comparator, sortOrder);
					});
					Backbone.history.navigate("#loop/" + encodeURIComponent(self.model.get('id')), {trigger:true});
					self.hideListSettingsBox();
				}
			});
			
		},
		
		onCancelListsButtonClicked: function() {
			this.hideListSettingsBox();
		},

		saveLoop: function(body, callback) {
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
