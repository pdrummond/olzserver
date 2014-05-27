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
		
		/*try {
			SAXBuilder builder = new SAXBuilder();
			xmlDoc = builder.build(new StringReader(input));
		} catch (Exception e) {
			log.error(String.format("Error parsing input [%s]", StringUtils.abbreviate(input, 500)), e);
			throw new RuntimeException("Failed to Htmlify input", e);
		}
		XPathExpression<Text> xpath = XPathFactory.instance().compile("//div/text()", Filters.text());
		for(Text e : xpath.evaluate(xmlDoc)) {
			Element parent = e.getParent();
			String text = e.getText();
			int index = parent.indexOf(e);
			String newContent = htmlifyString(text);
 			if(newContent != null) {
 				Node
				Element newElement = new Content(newContent);
				e.detach();
				parent.addContent(index, newElement);
			}
		}
		String output = new XMLOutputter(Format.getRawFormat()).outputString(xmlDoc);
		return output;*/
		return htmlifyString(input);
	}
	
	public String htmlifyString(String str) {
		String output = "";
		Pattern p = Pattern.compile(Loop.TAG_REGEX);
		
		Matcher m = p.matcher(str);
		int start = 0;
		while(m.find()) {
			output += str.substring(start, m.start());
			String tag = m.group();
			String tagType = "";
			if(tag.contains("@!")) {
				tagType = Loop.OWNERTAG;
			} else if(tag.contains("@")){
				tagType = Loop.USERTAG;
			} else if(tag.contains("#")) {
				tagType = Loop.HASHTAG;
			}
			output += String.format("<a class='tag %s' title='%s' href='/#query/%s'>%s</a>", tagType, tag, tag, tag);
			start = m.end();
		}
		output += str.substring(start, str.length());
		return output;
	}
	
	@Override 
	public String toString() {
		return new XMLOutputter(Format.getPrettyFormat()).outputString(xmlDoc);
	}

}