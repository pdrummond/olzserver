var curl;
(function () {
	curl({
		main: 'app',
		paths: {
			sockjs: 'lib/sockjs/sockjs',
			ckeditor: 'lib/ckeditor/ckeditor'
		},
		packages: {
			// Your application's package
			app: { location: 'app' },
			// Third-party packages
			curl: { location: 'lib/curl/src/curl'},
			jquery: { location: 'lib/jquery/dist/jquery', main: '.' },
			Backbone: { location: 'lib/backbone-amd/backbone', main: '.' },
			underscore: { location: 'lib/lodash/lodash', main: '.' },
			bootstrap: { location: 'lib/bootflatv2', main: 'js/bootstrap.min.js'},			
			stomp: { location: 'lib/stomp-websocket', main: 'dist/stomp' },			
		}
	});
}());