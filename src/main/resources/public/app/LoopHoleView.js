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

		createLoopEditor: function(el) {
			
			editorConfig = OlzApp.LoopEditor.getDefaultEditorConfig();
			editorConfig.sharedSpaces = {}/*
					top: 'loophole-editor-toolbar'
			};	*/

			
			this.loopEditor = new OlzApp.LoopEditor({
				editorConfig: editorConfig,
				el: this.$(el),
				loopView: this
			});	
		},

		render: function(){
			if(!this.loopEditor) {
				this.$el.html(this.template());
			}
			return this.el;
		},

		onCreateInnerLoopButtonClicked: function(e) {
			this.createLoop();
		},

		createLoop: function (e) {
			var loopContent = this.loopEditor.getData();
			if(this.loopEditor) {
				this.destroyLoopEditor();
			}
			this.$(".loophole").html("");
			this.trigger('create-loop', loopContent);
		},

		onFocus: function() {
			if(!this.loopEditor) {
				this.createLoopEditor(".loophole");
			}
		},

		destroyLoopEditor: function() {
			this.loopEditor.destroy();
			delete this.loopEditor;
		} 
		
	});

});
