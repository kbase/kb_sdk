package us.kbase.kidl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Class helps to transform data from xml (which is output from perl KIDL parser) into JSON format.
 */
@SuppressWarnings("unchecked")
public class SpecXmlHelper {
	public static Map<String,Object> parseXml(File parsingFile) 
			throws ParserConfigurationException, SAXException, IOException, KidlParseException {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = dbf.newDocumentBuilder();
        Document document = builder.parse(parsingFile);
        Node mainNode = document.getDocumentElement();
        return (Map<String,Object>)processMap(getSubNodes(mainNode).get(0), new HashMap<String, Object>()).get("parsed_data");
	}
	
	private static Object processChild(Node parNode, Map<String, Object> memRefs) throws KidlParseException {
		List<Node> subNodes = getSubNodes(parNode);
		Object ret = null;
		if (subNodes.size() == 1) {
			Node subNode = subNodes.get(0);
			if (subNode.getNodeName().equals("hashref")) {
				ret = processMap(subNode, memRefs);
			} else if (subNode.getNodeName().equals("arrayref")) {
				ret = processList(subNode, memRefs);
			} else {
				throw new KidlParseException("Unknown node type [" + subNode.getNodeName() + "]");
			}
		} else if (subNodes.size() == 0) {
			ret = getTextContent(parNode);
		} else {
			throw new KidlParseException("Node [" + parNode.getNodeName() + "] has more than 1 child");
		}
		return ret;
	}
	
	private static Map<String,Object> processMap(Node mapNode, Map<String, Object> memRefs) throws KidlParseException {
		if (!mapNode.getNodeName().equals("hashref"))
			return null;
		String memRef = extractMemRef(mapNode);
		if (memRef != null && memRefs.containsKey(memRef)) 
			return (Map<String,Object>)memRefs.get(memRef);
		Map<String, Object> ret = new LinkedHashMap<String, Object>();
		if (mapNode.getAttributes().getNamedItem("blessed_package") != null) {
			ret.put("!", mapNode.getAttributes().getNamedItem("blessed_package").getNodeValue());
		}
		for (Node child : getSubNodes(mapNode)) {
			if (!child.getNodeName().equals("item"))
				throw new KidlParseException("Wrong hash element node type: " + child.getNodeName());
			String key = child.getAttributes().getNamedItem("key").getNodeValue();
			Object value = processChild(child, memRefs);
			ret.put(key, value);
		}
		if (memRef != null)
			memRefs.put(memRef, ret);
		return ret;
	}

	private static String extractMemRef(Node node) {
		String memRef = null;
		if (node.getAttributes().getNamedItem("memory_address") != null) {
			memRef = node.getAttributes().getNamedItem("memory_address").getNodeValue();
		}
		return memRef;
	}

	private static List<?> processList(Node listNode, Map<String, Object> memRefs) throws KidlParseException {
		if (!listNode.getNodeName().equals("arrayref"))
			return null;
		String memRef = extractMemRef(listNode);
		if (memRef != null && memRefs.containsKey(memRef)) 
			return (List<?>)memRefs.get(memRef);
		List<Object> ret = new ArrayList<Object>();
		for (Node child : getSubNodes(listNode)) {
			if (!child.getNodeName().equals("item"))
				throw new KidlParseException("Wrong array element node type: " + child.getNodeName());
			Object value = processChild(child, memRefs);
			ret.add(value);
		}
		if (memRef != null)
			memRefs.put(memRef, ret);
		return ret;
	}

	private static List<Node> getSubNodes(Node parNode) {
		List<Node> ret = new ArrayList<Node>();
		NodeList childNodes = parNode.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node node = childNodes.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
            	ret.add(node);
            }
        }
        return ret;
	}
	
	public static String getTextContent(Node node) {
		NodeList nl = node.getChildNodes();
		for(int i = 0; i < nl.getLength(); i++) {
			if (nl.item(i).getNodeType() == Node.TEXT_NODE)
				return nl.item(i).getNodeValue();
		}
		return "";
	}
}
