CKEDITOR.plugins.add( 'olzloopbody', {
	icons: 'olzloopbody',
    init: function( editor ) {
        editor.addCommand( 'olzloopbody', {
            exec: function( editor ) {
                var now = new Date();
                editor.insertHtml( '<div class="loop-body">Loop body goes here...</div>');
            }
        });
        editor.ui.addButton( 'LoopBody', {
            label: 'Add Loop Body',
            command: 'olzloopbody',
        });
    }
});