var OlzApp = {};

$(function() {

	OlzApp.LoopHoleView = Backbone.View.extend({

		events: {
			'focus .loophole'   : 'onFocus',
			'click #create-innerloop-button': 'onCreateInnerLoopButtonClicked'
		},

		initialize: function() {
			this.template = _.template($('#loophole-template').html());
		},

		render: function(){
			this.$el.html(this.template());
			return this.el;
		},

		onCreateInnerLoopButtonClicked: function(e) {
			this.createLoop();
		},

		createLoop: function (e) {
			var loopContent = this.$('.loophole').html();
			this.trigger('create-loop', loopContent);
		},

		onFocus: function() {
			//this.$('.loophold').selectText();
		}
	});

});
