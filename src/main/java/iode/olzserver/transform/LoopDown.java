package iode.olzserver.transform;

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
		Pattern p = Pattern.compile("(@[^#/][\\w-]*)");
		Matcher m = p.matcher(input);
		int start = 0;
		while(m.find()) {
			output += input.substring(start, m.start());
			String uid = m.group(1);
			output += "<a class='loopref' title='" + uid + "' href='/#loop/" + uid + "'>" + uid.substring(0, 5) + "</a>";
			start = m.end();
		}
		output += input.substring(start, input.length());
		return output;
	}

}