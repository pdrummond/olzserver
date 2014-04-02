(function() {
	CKEDITOR.plugins.add( 'olztags', {
		init: function( editor ) {
			//var pluginName = 'olztags';

			editor.on('key', function( ev ) {
				console.log(">> KEY EVENT:" + JSON.stringify(ev.data));//.keyCode);
				
				if(ev.data.keyCode == "4456499") {
					ev.cancel();
					editor.insertHtml("<span class='hashtag'>#</span>");
				} else if(ev.data.keyCode == "2228274") {
					ev.cancel();
					editor.insertHtml("<span class='usertag'>@</span>");
				}
				 /*else if(ev.data.keyCode == "32") {
					var $el = $(editor.getSelection().getRanges()[0].startContainer.$);
					if($el.parent().hasClass("tag")) {
						var range = document.createRange();
						var sel = window.getSelection();
						range.setStart($($el.parent()).next(), 0);
						range.collapse(true);
						sel.removeAllRanges();
						sel.addRange(range);						
					}
				}*/
			}); 
			
			editor.on('selectionChange', function( ev ) {
				var $el = $(editor.getSelection().getRanges()[0].startContainer.$);
				//console.log(">> SELECTION CHANGED EVENT. ctx = " + $el.parent().get()[0].nodeName);
					
				if(elementIsHashtag($el)) {
				}			
			}); 
		}
	});
	
	function elementIsHashtag($el) {
		//console.log("BOOM: " + $el.startCont);
	}
})();