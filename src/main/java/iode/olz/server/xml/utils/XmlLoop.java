package iode.olz.server.xml.utils;

import iode.olz.server.domain.Loop;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.filter.Filters;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import org.jdom2.xpath.XPathExpression;
import org.jdom2.xpath.XPathFactory;

public class XmlLoop {
	private final Logger log = Logger.getLogger(getClass());
	private Loop loop;
	private Document xmlDoc;
	
	public XmlLoop(Loop loop) {
		this.loop = loop;
		try {
			SAXBuilder builder = new SAXBuilder();
			xmlDoc = builder.build(new StringReader(loop.getContent()));
		} catch (Exception e) {
			log.error(String.format("Error parsing xml [%s]", StringUtils.abbreviate(loop.getContent(), 20)), e);
			// Cause the transaction to rollback
			throw new RuntimeException("Failed to genrate XML for loop", e);
		}
	}
	
	public List<Element> evaluate(String expression) {
		XPathExpression<Element> xpath = XPathFactory.instance().compile(expression, Filters.element());
		return xpath.evaluate(xmlDoc);
	}

	public List<String> evaluateText(String expression) {
		XPathExpression<Element> xpath = XPathFactory.instance().compile(expression, Filters.element());
		List<String> list = new ArrayList<String>();
		for(Element e : xpath.evaluate(xmlDoc)) {
			list.add(e.getText());
		}
		return list;
	}

	public Element evaluateFirst(String expression) {
		XPathExpression<Element> xpath = XPathFactory.instance().compile(expression, Filters.element());
		return xpath.evaluateFirst(xmlDoc);
	}

	public int evaluateAndGetCount(String expression) {
		XPathExpression<Element> xpath = XPathFactory.instance().compile(expression, Filters.element());
		return xpath.evaluate(xmlDoc).size();
	}
	
	public Loop getLoop() {
		return loop;
	}
	
	@Override 
	public String toString() {               
        return formatAsString();
	}
	
	public String formatAsString() {
		return new XMLOutputter(Format.getPrettyFormat()).outputString(xmlDoc);
	}
	
	private Loop loopWithUpdatedContent() {
		return loop.copyWithNewContent(formatAsString());
	}

	public List<String> getHashtags() {
		return getTags("hashtag");
	}
	
	public List<String> getTags() {
		return evaluateText("//tag");
	}
	
	public List<String> getUsertags() {
		return getTags("usertag");
	}
	
	private List<String> getTags(String type) {
		List<String> userTags = new LinkedList<String>();
		List<Element> tags = evaluate(String.format("//tag[@type='%s']", type));
		for(Element e : tags) {
			userTags.add(e.getText());
		}
		return userTags;
	}

	public Loop ensureTagsExist(String... requiredTagsArgs) {
		return ensureTagsExist(Arrays.asList(requiredTagsArgs));
	}
	public Loop ensureTagsExist(List<String> requiredTags) {
		List<Element> tags = evaluate("//tag");
		
		for(Element e : tags) {
			String tag = e.getText();
			if(requiredTags.contains(tag)) {
				requiredTags.remove(tag);
			}
		}
		Element tagsBox = getOrCreateTagBox();
		for(String tag : requiredTags) {
			Element tagElement = new Element("tag");
			tagElement.setAttribute("type", tag.startsWith("@")?"usertag":"hashtag");
			tagElement.setText(tag);
			tagsBox.addContent(tagElement);
		}
		
		return loopWithUpdatedContent();
	}

	private Element getOrCreateTagBox() {
		Element tagsBoxElement = evaluateFirst("//tags-box");
		if(tagsBoxElement == null) {
			tagsBoxElement = new Element("tags-box");
			evaluateFirst("//body").addContent(tagsBoxElement);
		}
		return tagsBoxElement;
	}
}
