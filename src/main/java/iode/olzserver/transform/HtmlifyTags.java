package iode.olzserver.transform;

import iode.olzserver.domain.Loop;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jdom2.Document;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

public class HtmlifyTags {
	//private final Logger log = Logger.getLogger(getClass());
	private String input;
	private Document xmlDoc;

	public HtmlifyTags(String input) {
		this.input = input;
	}

	public String execute() {
		String output = "";
		Pattern p = Pattern.compile(Loop.TAG_REGEX);
		
		Matcher m = p.matcher(input);
		int start = 0;
		while(m.find()) {
			output += input.substring(start, m.start());
			String tag = m.group();
			String tagType = "";
			if(tag.contains("@!")) {
				tagType = Loop.OWNERTAG;
			} else if(tag.contains("@")){
				tagType = Loop.USERTAG;
			} else if(tag.contains("#")) {
				tagType = Loop.HASHTAG;
			}
			output += String.format("<tag type='%s'>%s</tag>", tagType, tag);
			start = m.end();
		}
		output += input.substring(start, input.length());
		return output;
	}
	
	@Override 
	public String toString() {
		return new XMLOutputter(Format.getPrettyFormat()).outputString(xmlDoc);
	}
}