var OlzApp = {};

$(function() {

	OlzApp.LoopItemView = OlzApp.AbstractLoopView.extend({

		className: 'loop-item-container',
		events: {
			'click #edit-button': 'onEditButtonClicked',
			'click #expand-button': 'onExpandButtonClicked',
		},		

		initialize: function() {
			this.template = _.template($('#loop-item-template').html());
			this.listenTo(this.model, 'change', this.render);
			this.editMode = false;
		},

		render: function(){
			var attrs = _.clone(this.model.attributes);
			this.$el.html(this.template(_.extend(attrs, {id: this.model.get('id') || ""}, this.getViewHelpers())));
			if(this.model.get('showInnerLoops')) {
				//this.renderInnerLoops();
				this.$(".innerloop-container").show();
			} else {
				this.$(".innerloop-container").hide();
			}
			this.toggleVisible();
			
			return this.el;
		},
		
		onExpandButtonClicked: function() {
			this.model.set('showInnerLoops', !this.model.get('showInnerLoops'));
			this.saveLoopFieldToServer('showInnerLoops', this.model.get('showInnerLoops'));
		},

		
		toggleVisible: function () {
			this.$el.toggleClass('hide', !this.isVisible());
		},
		
		isVisible: function() {
			var visible = false;

			var filterText = $('.filter-input').val();			
			if(filterText && filterText.length > 0) {
				filterText = filterText.trim().toLowerCase();
				var content = this.model.get("content").toLowerCase();
				visible = content.indexOf(filterText) > -1;
			} else {
				visible = true;
			}	
			return visible;
		},
		
		onEditButtonClicked: function() {
			var self = this;
			if(this.$('#edit-button').hasClass('btn-primary')) {
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

		saveLoopFieldToServer: function(fieldName, value) {
			var data = {};
			data[fieldName] = value;
			var self = this;
			$.post('/loop/field?loopId=' + encodeURIComponent(this.model.get('id')) + "&" + fieldName + "=" + value).fail(function(xhr) {
				self.showError("Error Saving field " + fieldName, "BOOM - this is for testing - remove this growl once working");
			});
		},
		
		
		getLoopBodyEl: function() {
			return ".loop > .body";
		}

	});
});
