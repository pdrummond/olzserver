package iode.olz.server.xml.utils;

import iode.olz.server.domain.Loop;

import java.io.StringReader;
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
        return new XMLOutputter(Format.getPrettyFormat()).outputString(xmlDoc);
	}

	public List<String> getHashtags() {
		return getTags("hashtag");
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
}
