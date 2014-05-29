/**
 * Utility functions for manipulating tags in strings.
 */
var TagString = {

		findTag: function(tagToFind, str) {
			var tagFound = _.find(str.split(' '), function(tag) { return tag === tagToFind; });
			return tagFound;
		},

		findOwnerTag: function(str) {
			var tags = str.split(' ');
			var tagFound = _.find(tags, function(tag) {
				console.log(">>!!: " + tag);
				return tag.indexOf('@!') != -1;
			});
			return tagFound;
		},
		
		findOwnerTag_: function(str) {
			var tag = TagString.findOwnerTag(str);
			if(tag) {
				tag = tag.replace('@!', '');
			}
			return tag;
		},

		makeExistingOwnerAFollower: function(tagString) {
			return _.map(tagString.split(' '), function(tag){
				if(tag.indexOf('@!') != -1) {
					return tag.replace('@!', '@');
				} else {
					return tag;
				}				
			}).join(' ');	
		},
};