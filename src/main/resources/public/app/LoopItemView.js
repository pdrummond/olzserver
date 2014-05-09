var OlzApp = {};

$(function() {

	OlzApp.LoopItemView = OlzApp.AbstractLoopView.extend({

		className: 'loop-item-container',
		events: {
			'click #loop-item-edit-button': 'toggleEditMode',
		},


		initialize: function() {
			this.template = _.template($('#loop-item-template').html());
			this.listenTo(this.model, 'change', this.render);
			this.editMode = false;
		},

		render: function(){			
			this.$el.html(this.template(this.model.attributes));
			this.toggleVisible();
			return this.el;
		},
		
		toggleVisible: function () {
			this.$el.toggleClass('hide', !this.isVisible());
		},
		
		isVisible: function() {
			var visible = false;

			var filterText = $('.filter-input').val().trim();
			if(filterText && filterText.length > 0) {
				filterText = filterText.toLowerCase();
				var content = this.model.get("content").toLowerCase();
				visible = content.indexOf(filterText) > -1;
			} else {
				visible = true;
			}	
			return visible;
		},
		
		getLoopBodyEl: function() {
			return ".loop > .body";
		}

	});
});
