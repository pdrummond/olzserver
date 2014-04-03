var OlzApp = {};

$(function() {
	
	OlzApp.LoopEditor = Backbone.View.extend({
		
		initialize: function(options) {
    		var editorConfig = options && options.editorConfig?options.editorConfig:this.getDefaultEditorConfig();
    		if(this.$el.length > 1) {
    			throw "LoopEditor requires a single element";
    		}
    		this.$el.attr('contenteditable', true);
			this.editorInstance = CKEDITOR.inline(this.el, editorConfig);
			this.editorInstance.options = options;
    		var self = this;
            this.editorInstance.on('blur', function( e ) {
                self.trigger('blur');
            });
            this.editorInstance.on('change', function( e ) {
                self.trigger('change');
            });
            this.editorInstance.on('instanceReady', function( e ) {
                console.log("Editor instance ready");
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
		    this.$el.attr('contenteditable', false);
    	},
    	
    	getDefaultEditorConfig: function() {
    		var editorConfig = {};
			editorConfig.entities_processNumerical = 'force';
    		editorConfig.stylesSet = 'default_styles';
    		editorConfig.removePlugins = 'div,image,forms';
			editorConfig.extraPlugins = 'widget,image2';//,olztags';
			editorConfig.enterMode = CKEDITOR.ENTER_P;
			editorConfig.toolbar = [];
			editorConfig.toolbar = [
			                         ['Undo', 'Redo' ],
			                         //['Cut', 'Copy', 'Paste'],
			                         ['InsertTable', 'ImageUpload', 'BulletedList', 'SpecialChar']];
			editorConfig.removeButtons = '';
			editorConfig.fillEmptyBlocks = false; //http://goo.gl/9Guafw
    		editorConfig.disableNativeSpellChecker = false;
    		//editorConfig.allowedContent = true;
			editorConfig.allowedContent = {
					'div': {
						classes: 'tags-box'
					},
					'p': true,
					'b ul i': true					
			};
			
			return editorConfig;
		}
    });
    
    //GLOBAL CKEDITOR config.
    CKEDITOR.disableAutoInline = true;

});