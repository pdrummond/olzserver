package iode.olzserver.service;

import iode.olzserver.domain.Loop;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UpdateTags {

	public Loop execute(Loop loop) {
		Pattern p = Pattern.compile("(#\\w*)");
		Matcher m = p.matcher(loop.getContent());
		String input = loop.getContent();
		String output = "";
		int start = 0;
		while(m.find()) {
		    output += input.substring(start, m.start());
		    output += "<span class='hashtag'>" + m.group(1).replace("#", "") + "</span>";
		    start = m.end();
		}
		output += input.substring(start, input.length());
		return loop.copyWithNewContent(output);
	}

}
