package iode.olzserver.xml.utils;

import iode.olzserver.domain.Loop;

import java.io.StringReader;
import java.util.ArrayList;
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
			if(log.isDebugEnabled()) {
				log.debug("loop content:" + loop.getContent());
			}
			xmlDoc = builder.build(new StringReader(loop.getContent()));
		} catch (Exception e) {
			log.error(String.format("Error parsing xml [%s]", StringUtils.abbreviate(loop.getContent(), 500)), e);
			// Cause the transaction to rollback
			throw new RuntimeException("Failed to generate XML for loop", e);
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

	public Loop loopWithUpdatedContent() {
		return loop.copyWithNewContent(formatAsString());
	}

	public List<String> findAllTags() {
		return evaluateText(String.format("//a[contains(@class, '%s')]", Loop.TAG));
	}

	public List<String> findAllTags_() {
		List<String> tags = new ArrayList<String>();
		for(String tag : findAllTags()) {
			tag = tag.replaceAll("@!|@|#", "");
			tags.add(tag);
		}
		return tags;
	}

	public String findOwnerTag() {
		return evaluateText(String.format("//tag[@type='%s']", Loop.OWNERTAG)).get(0);
	}

	public String findOwnerTag_() {
		String ownerTag = findOwnerTag();
		return ownerTag.replaceAll("@!", "");
	}

	public List<String> findUserTags() {
		return evaluateText(String.format("//tag[@type='%s']", Loop.USERTAG));
	}

	public List<String> findUserTags_() {
		List<String> userTags = new ArrayList<String>();
		for(String tag : findUserTags()) {
			userTags.add(tag.replaceAll("@", ""));			
		}
		return userTags;
	}

	public List<String> getLoopRefs() {
		return evaluateText("//loop-ref");
	}

	/*public Loop ensureTagsExist(String... requiredTagsArgs) {
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

		for(String tag : requiredTags) {
			addTag(tag);
		}

		return loopWithUpdatedContent();
	}*/

	public XmlLoop addTag(String tag) {
		Element lastPara = evaluateFirst("//p[last()]");
		Element loopRefElement = new Element("loop-ref");
		loopRefElement.setText(tag);
		lastPara.addContent(loopRefElement);		
		return this;
	}

	public int childCount(String expression) {
		return evaluateAndGetCount(expression + "/child::*");
	}

}
