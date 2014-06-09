CKEDITOR.plugins.add( 'olzsave', {
	icons: 'olzsave',
    init: function( editor ) {
        editor.addCommand( 'olzsave', {
            exec: function( editor ) {
            	editor.fire( 'olzsave');
            }
        });
        editor.ui.addButton( 'OlzSave', {
            label: 'Save Loop',
            command: 'olzsave',
        });
    }
});