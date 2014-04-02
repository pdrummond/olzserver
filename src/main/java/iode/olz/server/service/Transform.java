package iode.olz.server.service;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;

import javax.xml.crypto.dsig.TransformException;
import javax.xml.transform.ErrorListener;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

public final class Transform {
    private final Log log = LogFactory.getLog(Transform.class);

    private static final class TransformHolder {
        private static final Transform INSTANCE = new Transform();
    }

    private static final String TEMPLATES_PATH = "/xslt/*.xsl";
    private static final int INITIAL_BUFFER_SIZE = 100;

    private final Map<String, Templates> templates = new HashMap<String, Templates>();
    private final TransformerFactory transformerFactory = SAXTransformerFactory.newInstance();

    public static Transform getInstance() {
        return TransformHolder.INSTANCE;
    }

    private Transform() {
        try {
            ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
            Resource[] resources = resolver.getResources(TEMPLATES_PATH);
            for (Resource resource : resources) {
                templates.put(getName(resource), transformerFactory.newTemplates(new StreamSource(resource.getURL().openStream())));
            }
        }
        catch (IOException e) {
            throw new RuntimeException("Failed to discover XSLT resources in '" + TEMPLATES_PATH + "'", e);
        }
        catch (TransformerConfigurationException e) {
            throw new RuntimeException("Error loading XSLT stylesheets", e);
        }
        if (log.isDebugEnabled()) {
            log.debug("templates=" + new TreeSet<String>(templates.keySet()));
        }
    }

    public String transform(String type, String input) throws TransformException {
        return transform(type, input, null);
    }
    
    public String transform(String type, String input, Map<String, String> transformModel) throws TransformException {
        if (log.isDebugEnabled()) {log.debug("transform(input=" + input + ")");}
        try {
            Templates template = templates.get(type);
            if (log.isDebugEnabled()) {
                log.debug("template=" + template);
            }
            if (template != null) {
                Transformer transformer = template.newTransformer();
                StringWriter writer = new StringWriter(INITIAL_BUFFER_SIZE);
                transformer.setErrorListener(new DefaultErrorListener());
                if(transformModel != null) {
                    for(Map.Entry<String, String> entry : transformModel.entrySet()) {
                        transformer.setParameter(entry.getKey(), entry.getValue());
                    }
                }
                transformer.transform(new StreamSource(new StringReader(input)), new StreamResult(writer));
                String result = writer.toString().trim();
                if (log.isDebugEnabled()) {log.debug("result=" + result);}
                return result;
            }
            else {
                String errMsg = "Transformation '" + type + "' not available";
                log.error(errMsg);
                throw new RuntimeException(errMsg);
            }
        }
        catch (Exception e) {
            log.error("Error during transform", e);
            throw new TransformException(e);
        }
    }

    /**
     * Translate the name of a resource into a transformation name.
     *
     * @param resource resouce
     * @return name
     */
    private String getName(Resource resource) {
        String name = resource.getFilename();
        return name.substring(0, name.lastIndexOf('.'));
    }

    /**
     * Error handler.
     */
    final class DefaultErrorListener implements ErrorListener {

        @Override
        public void warning(TransformerException e) throws TransformerException {
            log.warn("warning()", e);
        }

        @Override
        public void error(TransformerException e) throws TransformerException {
            log.error("error()", e);
            throw e;
        }

        @Override
        public void fatalError(TransformerException e) throws TransformerException {
            log.error("fatalError()", e);
            throw e;
        }
    }
}
