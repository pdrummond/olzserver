var OlzApp = {};

$(function() {


	OlzApp.LoopView = Backbone.View.extend({
		el: '.loop-view',
		events: {
			'click #connect': 'connect',
			'click #send': 'send',
			'click #save-button': 'onSaveButtonClicked',
			'click #edit-button': 'onEditButtonClicked',
		},

		initialize: function(options) {
			var self = this;
			this.template = _.template($('#loop-template').html());
			this.model = new OlzApp.LoopModel();
			this.innerloops = [];
			this.listenTo(this.model, 'change', this.render);
			//this.unibarView = new OlzApp.UnibarView();
			this.loopHoleView = new OlzApp.LoopHoleView();
			this.listenTo(this.loopHoleView, 'create-loop', this.createInnerLoop);

			this.connect(function() {
				if(options.uid) {
					self.changeLoop(options.uid);
				}
			});

		},

		changeLoop: function(uid) {
			var self = this;
			this.model.set({'uid': uid}, {silent:true});			
			this.model.fetch({
				success: function(model, resp, options) {
					//self.unibarView.setLoopId(id);
					self.subscribeToHashtagChanges(model.get('lid'));
				},
				error: function(model, xhr) {
					alert("ERROR!");
				}
			});
		},

		render: function(){
			this.$el.html(this.template(this.model.attributes));
			//this.$('.unibar-container').append(this.unibarView.render());
			this.$('.loophole-container').append(this.loopHoleView.render());
			var self = this;
			_.each(this.model.get('loops'), function(loop) {
				self.addLoopItem(new OlzApp.LoopItemView({model:new OlzApp.LoopModel(loop)}));
			});
			return this.el;
		},
		
		editMode: function() {
			this.model.set("editMode", true);
			if(!this.loopEditor) {
				this.loopEditor = new OlzApp.LoopEditor({
					el: self.$(".loop-inner > .loop > .body") //select only the main loop (.loop .body selects innerloops too!). 
				});	
			}
			
			/*for(var i = 0; i<this.innerloops.length; i++) {
				var innerloop = this.innerloops[i];
				innerloop.toggleEditMode(editMode);
			}*/
		},
		
		addLoopItem: function(loopItemView) {
			this.$('#items').append(loopItemView.render());
			this.innerloops.push(loopItemView);
		},

		prependLoopItem: function(loopView) {
			this.$('#items').prepend(loopView.render());
		},
		
		createInnerLoop: function(body, parentUid) {
			this.createLoop(body, {parentUid: this.model.get("uid")});
		},

		createLoop: function(body, options) {
			var self = this;
			var loopModel = new OlzApp.LoopModel({content:this.generateContent('<p>' + body + '</p>')});
			if(options && options.parentUid) {
				loopModel.parentUid = options.parentUid;
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

		subscribeToHashtagChanges: function(hashtag) {
			var self = this;
			console.log("Listening for " + hashtag + " changes");
			this.stompClient.subscribe('/topic/hashtag/' + hashtag, function(resp){
				self.prependLoopItem(new OlzApp.LoopItemView({model:new OlzApp.LoopModel($.parseJSON(resp.body))}));
			});
		},

		disconnect: function() {
			stompClient.disconnect();         
			console.log("Disconnected");
		},

		onSaveButtonClicked: function() {
			this.saveLoop();
		},
		
		onEditButtonClicked: function() {
			this.editMode();
		},

		saveLoop: function() {
			var self = this;
			if(this.loopEditor) {
				var body = this.loopEditor.getData();
				
				this.model.save({'content': this.generateContent(body) }, {
					success: function(model, response, options) {					
						self.loopEditor.destroy();
						delete self.loopEditor;
						self.model.set('editMode', false);
						
					},
					error: function(model, response, options) {
						$('body').html(response.responseText);						
					}
				});
			}
		},
		
		generateContent: function(body) {
			var content = '<div class="loop"><div class="body">' + body + '</div></div>';		
			body = $(".body p", content).wrapHashtags().wrapUsertags().html();
			content = '<div class="loop"><div class="body">' + body + '</div></div>';
			
			content = content.replace(/&nbsp;/g, '&#160;');
			console.log("CONTENT: " + content);
			return content;
		}
	});

	jQuery.fn.wrapHashtags = function () {
		$(this).contents().filter(function() { 
			return this.nodeType == Node.TEXT_NODE;
		}).each(function () {
			var t = $(this).text();
			console.log("h>> " + t);
			$(this).replaceWith($(this).text().replace(/(#\w\w+)/g, '<span class="hashtag">$1</span>'));
		});
		return this;
	}
	jQuery.fn.wrapUsertags = function () {
		$(this).contents().filter(function() { 
			return this.nodeType == Node.TEXT_NODE;
		}).each(function () {
			var t = $(this).text();
			console.log("u>> " + t);
			$(this).replaceWith($(this).text().replace(/(@\w\w+)/g, '<span class="usertag">$1</span>'));
		});
		return this;
	}
});
