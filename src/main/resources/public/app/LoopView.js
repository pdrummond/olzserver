var OlzApp = {};

//Boom
$(function() {


	OlzApp.LoopView = OlzApp.AbstractLoopView.extend({
		className: 'loop-view',
		events: {
			'input .filter-input': 'onFilterInput',
			'keypress .search-input': 'onSearchInput',
			'keypress .create-input': 'onCreateInput',
			'click #edit-button': 'onEditButtonClicked',
			'click .innerloop-bar': 'toggleInnerLoops',
		},

		initialize: function(options) {
			var self = this;
			this.template = _.template($('#loop-template').html());
			this.model = new OlzApp.LoopModel();
			this.loopListView = new OlzApp.LoopListView({model: this.model});
			this.loopTabView = new OlzApp.LoopTabView({model: this.model});
			this.singleLoopView = new OlzApp.SingleLoopView({model: this.model});
			this.editMode = options.editMode;
			this.innerloops = [];
			this.listenTo(this.model, 'change', this.render);
			this.currentLoopView = 'list';

			this.connect(function() {
				self.changeLoop(options);
			});		

			this.setupUnsavedDataAlert();

			this.lastSavedInterval = setInterval(function() {
				self.renderLastSaved();
			}, 60000);


		},

		close: function(){
			clearInterval(this.lastSavedInterval);
			clearTimeout(this.autoSaveTimeout);
			this.destroyLoopEditor();
		},

		changeLoop: function(options) {
			var self = this;
			this.model.options = options;
			this.model.fetch({
				success: function(model, resp) {
					//self.subscribeToHashtagChanges(loopId);
				},
				error: function(model, response) {
					self.showError("Error getting loop!", response.statusText);
				}
			});
		},

		render: function() {

			if(this.isViewLoaded()) {
				this.$el.html(this.template(_.extend(this.model.attributes, this.getViewHelpers())));
				
				if(this.model.get('showInnerLoops')) {
					this.renderInnerLoops();
					this.$(".innerloop-container").show();
				} else {
					this.$(".innerloop-container").hide();
				}				
			}

			return this.el;


			/*if(this.isViewLoaded()) { 
				this.$('.filter-input').val(this.model.get('filterText'));

				this.renderLastSaved();

				if(this.model.get('showInnerLoops')) {
					this.renderInnerLoops();
					this.$(".innerloop-container").show();
				} else {
					this.$(".innerloop-container").hide();
				}
			}
			return this.el;*/
		},

		createLoopEditor: function(el) {
			console.log("CREATED EDITOR");

			this.loopEditor = new OlzApp.LoopEditor({
				el: this.$(el),
				loopView: this
			});	
			this.$(el).focus();
		},

		destroyLoopEditor: function() {
			if(this.loopEditor) { 
				this.loopEditor.destroy();
				delete this.loopEditor;				
			}
		},

		renderInnerLoops: function() {
			this.$("#items").empty();
			this.innerloops = [];
			var self = this;
			_.each(this.model.get('loops'), function(loop) {		
				var loopItemView = new OlzApp.LoopItemView({model:new OlzApp.LoopModel(loop)});
				loopItemView.editMode = self.editMode;
				self.addLoopItem(loopItemView);
			});	
		},

		onSearchInput: function(e) {
			if(e.keyCode == 13) {
				var input = this.$('.search-input').val().trim();
				Backbone.history.navigate("#query/" + encodeURIComponent(input), {trigger:true});
			}
		},

		onCreateInput: function(e) {
			if(e.keyCode == 13) {
				var input = this.$('.create-input').val().trim();
				this.createLoop(input);
				this.$('.create-input').select();
			}
		},
		
		onFilterInput: function() {
			this.model.set("filterText", this.$(".filter-input").val(), {silent:true});
			this.renderInnerLoops();
			this.saveLoopFieldToServer('filterText', this.model.get('filterText'));
		},


		addLoopItem: function(loopView) {
			if(!this.innerloopExists(loopView.model.get('id'))) {
				this.$('#items').append(loopView.render());
				this.innerloops.push(loopView);
			}
		},

		prependLoopItem: function(loopView) {
			if(!this.innerloopExists(loopView.model.get('id'))) {
				this.$('#items').prepend(loopView.render());
				this.innerloops.push(loopView);
			}
		},

		innerloopExists: function(loopId) {
			var found = false;
			for(var i=0; i<this.innerloops.length; i++) {
				if(this.innerloops[i].model.get('id') == loopId) {
					found = true;
					break;
				}
			}
			return found;
		},

		createLoop: function(body, options) {
			var self = this;
			
			body = "@pd: " + body + this.extractTags(this.model.get('content').trim());
			
			var loopModel = new OlzApp.LoopModel({content:this.generateContent(body)});
			if(options && options.parentLoopId) {
				loopModel.parentLoopId = options.parentLoopId;
			}			
			loopModel.save(null, {
				success: function(loop) {
					var loopView = new OlzApp.LoopItemView({model:loopModel});
					self.prependLoopItem(loopView);
				}
			})

			//this.stompClient.send("/app/hello", {}, JSON.stringify({ 'name': "BOOM" }));
		},
		
		connect: function(callback) {
			var self = this;
			var socket = new SockJS('/changes');
			this.stompClient = Stomp.over(socket);            
			this.stompClient.connect('', '',  function(frame) {
				console.log('Connected: ' + frame);
				if(callback) {
					callback();
				}
			});
		},

		subscribeToHashtagChanges: function(id) {
			var self = this;
			console.log("Listening for [" + id + "] changes");
			this.stompClient.subscribe('/topic/loop-changes/' + id, function(resp){
				self.prependLoopItem(new OlzApp.LoopItemView({model:new OlzApp.LoopModel($.parseJSON(resp.body))}));
			});
		},

		disconnect: function() {
			stompClient.disconnect();         
			console.log("Disconnected");
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

		setupUnsavedDataAlert: function() {
			var self = this;
			function beforeUnload( evt ) {
				if (self.hasUnsavedChanges()) {
					return evt.returnValue = "You will lose the changes made in the editor.";					
				}
			}

			if ( window.addEventListener ) {
				window.addEventListener( 'beforeunload', beforeUnload, false );
			} else {
				window.attachEvent( 'onbeforeunload', beforeUnload );
			}
		},

		hasUnsavedChanges: function() {
			return this.hasChanged;
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
		},

		onEditButtonClicked: function() {
			var self = this;
			if($('#edit-button').hasClass('btn-primary')) {
				this.$('#edit-button').removeClass('btn-primary').addClass('btn-success').html('Save');
				this.$('.loop .body').hide();
				this.$('.loop').append("<textarea class='loop-textarea'>" +  this.model.get('content') + "</textarea>")
			} else {
				var newContent = this.$('.loop-textarea').val();
				
				this.$('#edit-button').removeClass('btn-success').addClass('btn-error').html('Saving...');
				this.$('.loop-textarea').hide();
				this.$('.loop .body').show();
				
				this.saveLoop(newContent, function() {
					self.$('#edit-button').removeClass('btn-error').addClass('btn-primary').html('Edit');
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


	});	
});
