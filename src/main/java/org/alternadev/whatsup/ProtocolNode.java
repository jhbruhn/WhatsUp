package org.alternadev.whatsup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProtocolNode {
	public String tag;
	public Map<String, String> attributeHash;
	public List<ProtocolNode> children;
	public String data;

	public ProtocolNode(String tag, Map<String, String> attributeHash,
			List<ProtocolNode> children, String data) {
		this.tag = tag;
		this.attributeHash = attributeHash;
		if (attributeHash == null)
			this.attributeHash = new HashMap<String, String>();
		this.children = children;
		if (children == null)
			children = new ArrayList<ProtocolNode>();
		this.data = data;
	}

	public String nodeString() {
		return nodeString("");
	}

	public String nodeString(String indent) {
		String ret = "\n" +indent+ "<" + this.tag;
		for (Map.Entry<String, String> entry : attributeHash.entrySet()) {
			ret += " " + entry.getKey() + "=\"" + entry.getValue() + "\"";
		}
		ret += ">";
		if (data.length() > 0)
			ret += data;
		if (children != null)
			if (!children.isEmpty()) {
				for (ProtocolNode child : children)
					ret += child.nodeString(indent+"  ");
				ret += "\n" + indent;
			}
		ret +=  "</" + this.tag + ">";
		return ret;
	}

	public String getAttribute(String attribute) {
		return attributeHash.get(attribute);
	}

	public ProtocolNode getChild(String tag) {
		if (!this.children.isEmpty()) {
			for (ProtocolNode child : children) {
				if (child.tag.equals(tag))
					return child;
			}
		}
		return null;
	}
}
