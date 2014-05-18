var OlzApp = {};

$(function() {

	OlzApp.UnibarView = Backbone.View.extend({

		events: {
			'keydown'				: 'onKeyDown',
			'click #create-loop' 	: 'onCreateLoopButtonClicked',

		},

		initialize: function() {
			this.model = new OlzApp.UnibarModel({sid: ''});
			this.listenTo(this.model, 'change', this.render);
			this.template = _.template($('#unibar-template').html());
			this.listenTo(this.model, 'change', this.render);
		},

		render: function(){
			this.$el.html(this.template(this.model.attributes));
			return this.el;
		},

		onKeyDown: function(e) {
			var text = this.$('.unibar').text();
			if(e.keyCode == 13 && text[0] === '#') {
				e.preventDefault();
				var urlFragment = "loop/" + encodeURIComponent(this.$('.unibar').text());
				Backbone.history.navigate(urlFragment, {trigger: true});				
				return false;
			} 
			return true;
		},

		setLoopId: function(sid) {
			this.model.set('sid', sid);
			this.render();
		},

		onCreateLoopButtonClicked: function (e) {
			var sid = this.model.get('sid');
			var loopContent = this.$('.unibar').html();

			loopContent += " " + sid;
			this.trigger('create-loop', loopContent);
			this.setLoopId(sid);
		},

	});

});
