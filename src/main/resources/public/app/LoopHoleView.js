var OlzApp = {};

$(function() {

	OlzApp.LoopHoleView = Backbone.View.extend({

		events: {
			'keydown'		  : 'onKeyDown',
			'focus .loophole' : 'onFocus'
		},

		initialize: function() {
			this.template = _.template($('#loophole-template').html());
			this.render();
		},

		render: function(){
			this.$el.html(this.template());
			return this.el;
		},

		onKeyDown: function(e) {
			var text = this.$('.loophole').text();
			if(e.keyCode == 13) {
				e.preventDefault();
				this.createLoop();
				return false;
			} 
			return true;
		},

		createLoop: function (e) {
			var loopContent = this.$('.loophole').val();
			this.trigger('create-loop', loopContent);
		},

		onFocus: function() {
			//this.$('.loophold').selectText();
		}
	});

});
