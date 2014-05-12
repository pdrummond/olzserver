package iode.olzserver.transform;

import iode.olzserver.domain.Loop;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoopDown {
	private String input;


	public LoopDown(String input) {
		this.input = input;
	}

	public String toLoopDown() {
		return null;
	}

	public String toHtml() {
		String output = "";
		Pattern p = Pattern.compile(Loop.TAG_REGEX);
		Matcher m = p.matcher(input);
		int start = 0;
		while(m.find()) {
			output += input.substring(start, m.start());
			String id = m.group();
			output += "<a class='loopref' title='" + id + "' href='/#loop/" + id + "'>" + id + "</a>";
			start = m.end();
		}
		output += input.substring(start, input.length());
		return output;
	}

}