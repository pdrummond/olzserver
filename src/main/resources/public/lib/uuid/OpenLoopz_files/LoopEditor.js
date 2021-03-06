var OlzApp = {};

$(function() {

	OlzApp.LoopEditor = Backbone.View.extend({

		initialize: function(options) {
			var editorConfig = options && options.editorConfig?options.editorConfig:OlzApp.LoopEditor.getDefaultEditorConfig();
			this.loopView = options.loopView;
			if(this.$el.length > 1) {
				throw "LoopEditor requires a single element";
			}
			this.$el.attr('contenteditable', true);
			this.editorInstance = CKEDITOR.inline(this.el, editorConfig);
			this.editorInstance.options = options;
			var self = this;
			this.editorInstance.on('instanceReady', function( e ) {
				console.log("Editor instance ready");
			});
			this.editorInstance.on('change', function( e ) {
				console.log("CHANGE DETECTED")
				if(self.loopView.onChange) {
					self.loopView.onChange();
				}
			});
		},

		setData: function (data) {
			this.editorInstance.setData(data);
		},

		getData: function() {
			return this.editorInstance.getData();
		},

		moveCursorToStart: function() {
			var range = this.editorInstance.createRange();
			range.moveToPosition( range.root, CKEDITOR.POSITION_AFTER_START );
			this.editorInstance.getSelection().selectRanges( [ range ] );
		},

		moveCursorToEnd: function() {
			var range = this.editorInstance.createRange();
			range.moveToPosition( range.root, CKEDITOR.POSITION_BEFORE_END );
			this.editorInstance.getSelection().selectRanges( [ range ] );
		},

		destroy: function() {
			this.editorInstance.destroy();    		    
			//this.$el.attr('contenteditable', false);
		},

	});

	//GLOBAL CKEDITOR config.
	CKEDITOR.disableAutoInline = true;
	CKEDITOR.stylesSet.add('default_styles', [
	                                          // Block-level styles.
	                                          { name: 'Heading 1', element: 'h1', styles: { color: 'Blue' } },
	                                          { name: 'Heading 2',  element: 'h2', styles: { color: 'Red' } },

	                                          // Inline styles.
	                                          { name: 'CSS Style', element: 'span', attributes: { 'class': 'my_style' } },
	                                          { name: 'Marker: Yellow', element: 'span', styles: { 'background-color': 'Yellow' } }
	                                          ]);
	
	OlzApp.LoopEditor.getDefaultEditorConfig = function() {
		var editorConfig = {};

		editorConfig.entities_processNumerical = 'force';
		editorConfig.stylesSet = 'default_styles';
		editorConfig.removePlugins = 'div,image,forms';
		editorConfig.extraPlugins = 'widget,image2,sharedspace';//,olztags';
		editorConfig.enterMode = CKEDITOR.ENTER_P;
		editorConfig.removeButtons = '';
		editorConfig.sharedSpaces = {
				top: 'loop-editor-toolbar'
		};	
		
		/* fillEmptyBlocks
		 * 
		 * This is the default, but making it explicit as important.  It ensures 
		 * empty paragraph elements are displayed properly.  When saving a loop\
		 * the saveLoop function swaps &nbsp for #160; to make it compatible with XML.
		 */
		editorConfig.fillEmptyBlocks = true;
					
		editorConfig.disableNativeSpellChecker = false;
		//editorConfig.allowedContent = true;
		editorConfig.toolbarGroups = [ 
              { name: 'basicstyles', groups: [ 'basicstyles', 'cleanup' ] },
              { name: 'paragraph',   groups: [ 'list', 'indent', 'blocks', 'align' ] }
        ];
		editorConfig.allowedContent = {
				'div': {
					classes: 'tags-box'
				},
				'a': {
					attributes: 'href, title',
					classes: 'loopref'
				},
				'p br': true,
				'b ul i': true,
				'ul li': true,
				'h1 h2 h3': true,
		};

		return editorConfig;
	}


});