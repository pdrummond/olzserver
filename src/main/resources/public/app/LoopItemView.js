var OlzApp = {};

$(function() {

	OlzApp.LoopItemView = Backbone.View.extend({

		className: 'loop-item-container',

		initialize: function() {
			this.template = _.template($('#loop-item-template').html());
			this.listenTo(this.model, 'change', this.render);
		},

		render: function(){
			this.model.set("firstParagraph", "<p>" + ($("p", this.model.get("content")).first().html()) + "</p>");
			this.$el.html(this.template(this.model.attributes));
			return this.el;
		}
	});
});
