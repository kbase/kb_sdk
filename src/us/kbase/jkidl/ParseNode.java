package us.kbase.jkidl;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public class ParseNode {
	private String name;
	private Map<String, String> props = new LinkedHashMap<String, String>();
	private List<ParseNode> children = new ArrayList<ParseNode>();
	
	public ParseNode(String name) {
		this.name = name;
	}
	
	public void addChild(ParseNode child) {
		children.add(child);
	}
	
	public void setProperty(String name, String value) {
		props.put(name, value);
	}
	
	public String getName() {
		return name;
	}
	
	public Map<String, String> getProps() {
		return props;
	}
	
	public List<ParseNode> getChildren() {
		return children;
	}
	
	public void printTreeInfo() {
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(SerializationFeature.INDENT_OUTPUT, true);
		try {
			mapper.writeValue(System.out, this);
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
	}
}
