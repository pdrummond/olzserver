var OlzApp = {};

$(function() {

	OlzApp.LoopItemView = OlzApp.AbstractLoopView.extend({

		className: 'loop-item-container',
		events: {
			'click': 'onItemClicked',
		},

		initialize: function() {
			this.template = _.template($('#loop-item-template').html());
			this.listenTo(this.model, 'change', this.render);
			this.editMode = false;
		},

		render: function(){
			var attrs = _.clone(this.model.attributes);
			this.$el.html(this.template(_.extend(attrs, {id: this.model.get('id') || ""}, this.getViewHelpers())));
			this.toggleVisible();
			return this.el;
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
		
		onItemClicked: function() {
			Backbone.history.navigate("#loop/" + this.model.get('id'), {trigger:true});
		},
		
		getLoopBodyEl: function() {
			return ".loop > .body";
		}

	});
});
