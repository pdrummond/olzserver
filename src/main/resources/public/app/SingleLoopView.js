var OlzApp = {};

$(function() {

	OlzApp.SingleLoopView = OlzApp.AbstractLoopView.extend({
		events: {
			'click #edit-button': 'onEditButtonClicked',
		},

		initialize: function(options) {
			var self = this;
			this.loopItems = [];
			this.template = _.template($('#single-loop-view-template').html());
		},

		render: function() {
			this.$el.html(this.template(_.extend(this.model.attributes, this.getViewHelpers())));			
			$('.create-input').hide();
			return this.el;
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


		getLoopBodyEl: function() {
			return ".loop-inner > .loop > .body";
		}
	});	
});
