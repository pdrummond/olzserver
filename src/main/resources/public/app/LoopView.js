var OlzApp = {};

$(function() {


	OlzApp.LoopView = OlzApp.AbstractLoopView.extend({
		className: 'loop-view',
		events: {
			'input .filter-input': 'onFilterInput',
			'keypress .search-input': 'onSearchInput',
			'keypress .create-input': 'onCreateInput',
		},

		initialize: function(options) {
			var self = this;
			this.template = _.template($('#loop-template').html());
			this.collection = new OlzApp.LoopCollection();
			this.loopListView = new OlzApp.LoopListView({collection: this.collection, expandInnerLoops:true});
			this.editMode = options.editMode;
			this.innerloops = [];
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
			this.query = options.query;
			this.collection.options = options;
			this.collection.fetch({
				success: function(model, resp) {
					//self.subscribeToHashtagChanges(loopId);
					self.render();
				},
				error: function(model, response) {
					self.showError("Error getting loop!", response.statusText);
				}
			});
		},

		render: function() {
			this.$el.html(this.template());
			this.$('.search-input').val(this.query);
			if(this.collection.length > 0) {

				switch(this.currentLoopView) {
				case 'list': 
					this.$('.main-list-container').append(this.loopListView.render());
					break;
				}

			}
			return this.el;
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

		onLoopListViewButtonClicked: function() {
			this.currentLoopView = 'list';
			this.render();
		},
		
		createLoop: function(body, options) {
			var self = this;

			var content = this.generateContent(body);

			/*var searchTags = this.extractTags($('.search-input').val().trim());
			var loopTags = this.extractTags(content);

			for(var i=0; i<searchTags.length; i++) {
				if(!_.contains(loopTags, searchTags[i])) {
					content += " " + searchTags[i];
				}
			}*/

			var loopModel = new OlzApp.LoopModel({content:content});
			if(options && options.parentLoopId) {
				loopModel.parentLoopId = options.parentLoopId;
			}			
			loopModel.save(null, {
				success: function(loop) {
					var loopView = new OlzApp.LoopItemView({model:loopModel});
					self.loopListView.prependLoopItem(loopView);
				}
			})

			//this.stompClient.send("/app/hello", {}, JSON.stringify({ 'name': "BOOM" }));
		},


	});	
});
