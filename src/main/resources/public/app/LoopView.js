var OlzApp = {};

$(function() {


	OlzApp.LoopView = Backbone.View.extend({
		el: '.loop-view',
		events: {
			'click #connect': 'connect',
			'click #send': 'send',
			'click #save-button': 'onSaveButtonClicked',
			'click #edit-button': 'onEditButtonClicked',
			'dblclick .loop': 'onEditButtonClicked' 
		},

		initialize: function(options) {
			var self = this;
			this.template = _.template($('#loop-template').html());
			this.model = new OlzApp.LoopModel();
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

		render: function(){
			this.$el.html(this.template(this.model.attributes));
			this.$('.loophole-container').html(this.loopHoleView.render());
			this.$('.unibar-container').html(this.unibarView.render());
			this.$("#items").empty();
			this.innerloops = [];
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
		
		createInnerLoop: function(body) {
			this.createLoop(body, {parentLoopId: this.model.get("id")});
		},

		createLoop: function(body, options) {
			var self = this;
			var loopModel = new OlzApp.LoopModel({content:this.generateContent('<p>' + body + '</p>')});
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
			body = $(".body", content).html($(".body p", content).wrapHashtags().wrapLoopRefs());			
			var content = '<div class="loop"><div class="body">' + body.html() + '</div></div>';		
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
			$(this).replaceWith($(this).text().replace(/(#\w\w+)/g, '<a class="hashtag" data-type="hashtag">$1</a>'));
		});
		return this;
	},
	
	jQuery.fn.wrapLoopRefs = function () {
		$(this).contents().filter(function() { 
			return this.nodeType == Node.TEXT_NODE;
		}).each(function () {
			var t = $(this).text();
			console.log("u>> " + t);
			$(this).replaceWith($(this).text().replace(/(@\w\w+)/g, '<a class="loop-ref" data-type="loop-ref">$1</a>'));
		});
		return this;
	}
		
	/*collectTextNodes = function(element, texts) {
	    for (var child= element.firstChild; child!==null; child= child.nextSibling) {
	        if (child.nodeType===3)
	            texts.push(child);
	        else if (child.nodeType===1)
	            collectTextNodes(child, texts);
	    }
	};

	window.doTags = function() {
		var texts = [];
		collectTextNodes($("body")[0], texts);
		_.each(texts, function(text) {
			if(!$(text).parent().hasClass("tag")) {
				$(text).replaceWith($(text).text().replace(/(#\w*)/g, '<a href="#" class="tag">$1</a>'));
			}
		});
	};*/
	
});
