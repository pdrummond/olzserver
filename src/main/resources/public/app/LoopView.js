var OlzApp = {};

$(function() {


	OlzApp.LoopView = Backbone.View.extend({
		className: 'loop-view',
		events: {
			'click #connect': 'connect',
			'click #send': 'send',
			'click #toggle-edit-mode-button': 'toggleEditMode',
			'dblclick': 'toggleEditMode',
			'click .innerloop-bar': 'toggleInnerLoops',
			'input .filter-input': 'onFilterInput'
		},

		initialize: function(options) {
			var self = this;
			this.template = _.template($('#loop-template').html());
			this.model = new OlzApp.LoopModel();
			this.editMode = options.editMode;
			this.innerloops = [];
			this.listenTo(this.model, 'change', this.render);
			this.unibarView = new OlzApp.UnibarView();
			this.loopHoleView = new OlzApp.LoopHoleView();
			this.listenTo(this.loopHoleView, 'create-loop', this.createInnerLoop);

			this.connect(function() {
				if(options.id) {
					self.changeLoop(options.id);
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

		changeLoop: function(id) {
			var self = this;
			this.model.set({'id': id}, {silent:true});			
			this.model.fetch({
				success: function(model, resp, options) {
					self.unibarView.setLoopId(id);
					self.subscribeToHashtagChanges(model.get('id'));
				},
				error: function(model, xhr) {
					alert("ERROR!");
				}
			});
		},

		render: function() {
			if(this.model.get("id")) {
				if(!this.editMode) {
					this.$el.html(this.template(_.extend(this.model.attributes, {editMode: this.editMode})));
					this.$("#toggle-edit-mode-button .glyphicon").removeClass("glyphicon-chevron-right").addClass("glyphicon-edit");
					//If editor is defined in editMode, get rid.
					this.destroyLoopEditor();
				} else {				
					if(!this.loopEditor) { //if no editor in editMode, spawn one immediately.
						this.$el.html(this.template(_.extend(this.model.attributes, {editMode: this.editMode})));
						this.createLoopEditor(".loop-inner > .loop > .body"); //Of course, one must select only the main loop (.loop .body selects innerloops too darn it!).
					} else {
						//The editor is still open so don't refresh it.
						//We are assuming that the save was successful.
					}
					this.$("#toggle-edit-mode-button .glyphicon").removeClass("glyphicon-edit").addClass("glyphicon-chevron-right");
				}
				this.$('.loophole-container').html(this.loopHoleView.render());
				this.$('.unibar-container').html(this.unibarView.render());
				this.$('.filter-input').val(this.model.get('filterText'));
				this.renderLastSaved();

				//this.renderInnerLoops();

				if(this.model.get('showInnerLoops')) {
					this.$(".innerloop-container").show();
				} else {
					this.$(".innerloop-container").hide();
				}
			}
			return this.el;
		},

		createLoopEditor: function(el) {
			console.log("CREATED EDITOR");

			this.loopEditor = new OlzApp.LoopEditor({
				el: this.$(el),
				loopView: this
			});	
			//this.listenTo(this.loopEditor, "change", this.onChange());
		},

		onChange: function() {
			this.hasChanged = true;
			console.log("RESET IDLE TIME");
			var self = this;
			clearTimeout(this.autoSaveTimeout);
			this.autoSaveTimeout = setTimeout(function(){
				console.log("TIME UP");
				self.saveLoop();
			}, 5000);
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

		onFilterInput: function() {
			this.model.set("filterText", this.$(".filter-input").val(), {silent:true});
			this.renderInnerLoops();
			this.saveLoopFieldToServer('filterText', this.model.get('filterText'));
		},

		toggleEditMode: function() {
			var self = this;
			this.saveLoop(function(result) {
				if(result) {
					self.editMode = !self.editMode;
					self.render();
				} else {
					alert("Oop!  Couldn't save loop.");
				}

			});

		},

		addLoopItem: function(loopItemView) {
			this.$('#items').append(loopItemView.render());
			this.innerloops.push(loopItemView);
		},

		prependLoopItem: function(loopView) {
			this.$('#items').prepend(loopView.render());
		},

		createInnerLoop: function(body) {
			this.createLoop(body, {parentLoopId: this.model.get("id")});
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

		saveLoop: function(callback) {
			var self = this;
			if(this.loopEditor) {
				var body = this.loopEditor.getData();
				//console.log("EDITOR DATA: " + body);

				for(var i=0; i<self.innerloops.length; i++) {
					var innerloop = self.innerloops[i];
					var innerLoopData = this.findInnerLoopInModel(innerloop.model.get('id'));
					innerLoopData.content = this.generateContent(innerloop.loopEditor.getData());
				}

				this.model.save({'content': this.generateContent(body) }, {
					wait:true,
					success: function() {
						self.hasChanged = false;
						self.lastSaved = new Date();
						self.renderLastSaved();
						if(callback) {
							callback(true);
						}
					},
					error: function(model, response, options) {
						self.showError("Save Error", response.statusText);
						if(callback) {
							callback(false);
						}
					}
				});
			} else {
				if(callback) {
					callback(true);
				}
			}
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

		generateContent: function(body) {
			var content = '<div class="loop"><div class="body">' + body + '</div></div>';		
			//body = $(".body", content).html($(this.getAllowedBodyTags(), content).wrapLoopRefs());			
			//var content = '<div class="loop"><div class="body">' + body.html() + '</div></div>';		
			//content = content.replace(/&nbsp;/g, '&#160;');
			console.log("CONTENT: " + content);
			return content;
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
			$.post('/loop/' + encodeURIComponent(this.model.get('id')) + "?" + fieldName + "=" + value).fail(function(xhr) {
				self.showError("Error Saving field " + fieldName, "BOOM - this is for testing - remove this growl once working");
			});
		},

		showError: function(title, message) {
			title = title + " at " + moment().format('h:mm a');
			$.growl.error({ title: title, message: message, duration: 99999});
		},

		renderLastSaved: function() {
			if(this.lastSaved) {
				this.$('#last-saved-msg-inner').html("Last saved " + moment(this.lastSaved).fromNow());
			}
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
		}

	});	
});
