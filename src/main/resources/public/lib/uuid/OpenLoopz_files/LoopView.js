var OlzApp = {};

$(function() {


	OlzApp.LoopView = OlzApp.AbstractLoopView.extend({
		className: 'loop-view',
		events: {
			'click #connect': 'connect',
			'click #send': 'send',
			'click #toggle-edit-mode-button': 'toggleEditMode',
			'dblclick .loop-inner .body': 'toggleEditMode',
			'click .innerloop-bar': 'toggleInnerLoops',
			'input .filter-input': 'onFilterInput',
			'click #create-innerloop-button': 'onCreateInnerLoopButtonClicked',
			'keypress .search-input': 'onSearchInput',

		},

		initialize: function(options) {
			var self = this;
			this.template = _.template($('#loop-template').html());
			this.model = new OlzApp.LoopModel();
			this.loopListView = new OlzApp.LoopListView({model: this.model});
			this.editMode = options.editMode;
			this.innerloops = [];
			this.listenTo(this.model, 'change', this.render);

			this.connect(function() {
				if(options.loopId) {
					self.changeLoop(options.loopId);
				}
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

		changeLoop: function(loopId) {
			var self = this;
			this.model.set({
				'id': loopId
				}, {silent:true});			
			this.model.fetch({
				success: function(model, resp, options) {
					self.$('.search-input').val(loopId);
					self.subscribeToHashtagChanges(loopId);
				},
				error: function(model, xhr) {
					alert("ERROR!");
				}
			});
		},

		render: function() {
			if(this.isViewLoaded()) {
				this.$el.html(this.template(this.model.attributes));
				this.$('.content-wrapper').append(this.loopListView.render());
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
			console.log(e);
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
		}
	});	
});
