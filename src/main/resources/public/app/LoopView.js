var OlzApp = {};

$(function() {


	OlzApp.LoopView = OlzApp.AbstractLoopView.extend({
		className: 'loop-view',
		events: {
			'keypress .search-input': 'onSearchInput',
			'keypress .create-input': 'onCreateInput',
			'click #loop-settings-button': 'toggleSettings',
			'click #incoming-loops-msg': 'onIncomingLoopsMsgClicked',
			'click #outerloop-button-label': 'onOuterloopButtonClicked',
			'click #pd-button-label': 'onPdButtonClicked',
			'click #em-button-label': 'onEmButtonClicked',
			'click .user-image': 'onUserImageClicked'
				
		},

		initialize: function(options) {
			var self = this;
			this.template = _.template($('#loop-template').html());
			this.showDetail = options.showDetail;
			this.collection = new OlzApp.LoopCollection();
			this.loopListView = new OlzApp.LoopListView({collection: this.collection, expandLists:false, showDetail:this.showDetail});
			this.editMode = options.editMode;
			this.innerloops = [];
			this.currentLoopView = 'list';
			this.outerloopPod = true;
			this.pdPod = false;
			this.emPod = false;

			self.changeLoop(options);

			this.setupUnsavedDataAlert();

			/*this.lastSavedInterval = setInterval(function() {
				self.renderLastSaved();
			}, 60000);*/

			this.recenterLoopOnWindowResize();
			this.notificationView = new OlzApp.NotificationView();
		},

		close: function(){
			this.notificationView.close();
			clearInterval(this.lastSavedInterval);
			clearTimeout(this.autoSaveTimeout);
			this.destroyLoopEditor();
		},

		changeLoop: function(options) {
			var self = this;
			if(options) {
				this.collection.query = options.query;
				this.collection.loopId = options.loopId;
				this.collection.showDetail = options.showDetail;
			}
			this.collection.fetch({
				success: function(model, resp) {
					$.get( "/user/current", function( user) {
						OlzApp.user = user;
						self.notificationView.fetchNewItems();
						self.render();
					});
				},
				error: function(model, response) {
					self.showError("Error getting loop!", response.statusText);
				}
			});
		},

		render: function(options) {
			this.$el.html(this.template());
			this.$('.search-input').val(this.collection.query);
			this.$('#outerloop-button-label').toggleClass('active', this.outerloopPod);
			this.$('#pd-button-label').toggleClass('active', this.pdPod);
			this.$('#em-button-label').toggleClass('active', this.emPod);

			if(this.collection.length > 0) {

				switch(this.currentLoopView) {
				case 'list': 
					this.$('.main-list-container').append(this.loopListView.render());
					break;
				}

			}
			this.recenterLoop();
			this.renderUserBox();
			this.$('#notification-box-container').html(this.notificationView.render());			
			$('.tag').autumn('backgroundColor', 'data-content');
			return this.el;
		},

		onIncomingLoopsMsgClicked: function() {
			$('.from-server').show();
			$('#incoming-loops-msg').html('').fadeOut();
			$('.loop-item-container').removeClass('from-server');
		},

		renderUserBox: function() {
			if(OlzApp.user) {
				var userTag = "@" + OlzApp.user.userId; 
				this.$('.user-box .user-image').html("<img title='" + userTag + "' src='" + OlzApp.user.imageUrl + "' style='width:28px;margin-left:10px'/>");
			}
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

		/*connect: function(callback) {
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
		},*/

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
			var loopView = new OlzApp.LoopItemView({model:model, showDetail: this.showDetail});
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

		recenterLoopOnWindowResize: function() {
			$(window).on('resize', this.recenterLoop);/*function() {
				var $settingsView = this.$(".settings-wrapper");
				var $loopView = this.$("#content-wrapper");
				if($settingsView.css('left') === '-800px') {
					$settingsView.css({left:'-800px'});
					var width = Math.max(0, (($(window).width() - $loopView.outerWidth()) / 2));
					$loopView.css({left: width + "px"});
				}
			});*/
		},

		recenterLoop: function() {
			var $settingsView = this.$(".settings-wrapper");
			var $loopView = this.$(".content-wrapper");
			if($settingsView.css('left') === '-800px') {
				var width = Math.max(0, (($(window).width() - $loopView.outerWidth()) / 2));				
				$loopView.css({left: width + "px"});
			}
		},

		toggleSettings: function() {

			var $settingsView = this.$(".settings-wrapper");
			var $loopView = this.$(".content-wrapper");
			var self = this;
			if($settingsView.css('left') === '-800px') {
				$settingsView.animate({left:'20px'});
				$loopView.animate({left: '850px'}, function() {
					//self.updatePageTitle();
				});
			} else {
				$settingsView.animate({left:'-800px'});
				var width = ( document.body.clientWidth - $loopView.outerWidth() ) / 2+$(window).scrollLeft();
				$loopView.animate({left: width + "px"}, function() {
					//self.updatePageTitle();
				});
			}
		},

		onOuterloopButtonClicked: function() {
			this.outerloopPod = !this.outerloopPod;
		},
		
		onPdButtonClicked: function() {
			this.pdPod = !this.pdPod;
		},
		
		onEmButtonClicked: function() {
			this.emPod = !this.emPod;
		},
		
		onUserImageClicked: function() {
			this.changeLoop();
		}
	});	
});
