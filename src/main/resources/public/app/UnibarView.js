var OlzApp = {};

$(function() {

	jQuery.fn.selectText = function(){
		var doc = document;
		var element = this[0];
		console.log(this, element);
		if (doc.body.createTextRange) {
			var range = document.body.createTextRange();
			range.moveToElementText(element);
			range.select();
		} else if (window.getSelection) {
			var selection = window.getSelection();        
			var range = document.createRange();
			range.selectNodeContents(element);
			selection.removeAllRanges();
			selection.addRange(range);
		}
	};

	OlzApp.UnibarView = Backbone.View.extend({

		events: {
			'keydown'				: 'onKeyDown',
			'click #create-loop' 	: 'onCreateLoopButtonClicked',
			'focus .unibar'			: 'onFocus'

		},

		initialize: function() {
			this.model = new OlzApp.UnibarModel({id: ''});
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
				var urlFragment = "loop/" + this.$('.unibar').text();
				Backbone.history.navigate(urlFragment, {trigger: true});				
				return false;
			} 
			return true;
		},

		setLoopId: function(id) {
			this.model.set('id', id);
			this.render();
		},

		onCreateLoopButtonClicked: function (e) {
			var id = this.model.get('id');
			var loopContent = this.$('.unibar').html();

			loopContent += " " + id
			this.trigger('create-loop', loopContent);
			this.setLoopId(id);
		},

		onFocus: function() {

			this.$('.unibar').selectText();
		}
	});

});
