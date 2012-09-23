package org.alternadev.whatsup;

import java.util.HashMap;
import java.util.Map;

public class BinTreeNodeWriter {
	private String output = "";
	private Map<String, Integer> tokenMap;

	public BinTreeNodeWriter(String[] dic) {
		tokenMap = new HashMap<String, Integer>();
		for (int i = 0; i < dic.length; i++)
			if (dic[i] != null)
				if (!dic[i].isEmpty())
					this.tokenMap.put(dic[i], i);
	}

	public String startStream(String domain, String resource) {
		Map<String, String> attributes = new HashMap<String, String>();
		output = "WA";
		output += "\u0001\u0001\u0000\u0019";
		
		attributes.put("to", domain);
		attributes.put("resource", resource);
		this.writeListStart(attributes.size() * 2 + 1);
		
		this.output += "\u0001";
		this.writeAttributes(attributes);
		String ret = this.output;
		this.output = "";	

		return ret;

	}

	public String write(ProtocolNode node) {
		if (node == null)
			this.output += "\u0000";
		else
			this.writeInternal(node);
		return this.flushBuffer();
	}

	private void writeInternal(ProtocolNode node) {
		int len = 1;
		if (node.attributeHash != null)
			len += node.attributeHash.size() * 2;
		if (node.children != null && node.children.size() > 0)
			len += 1;
		if (node.data.length() > 0)
			len += 1;
		this.writeListStart(len);
		this.writeString(node.tag);
		this.writeAttributes(node.attributeHash);
		if (node.data.length() > 0)
			this.writeBytes(node.data);
		if (node.children != null && node.children.size() > 0) {
			this.writeListStart(node.children.size());
			for (ProtocolNode node1 : node.children) {
				this.writeInternal(node1);
			}
		}
	}

	private String flushBuffer() {
		int size = this.output.length();
		String ret = this.writeInt16(size);
		ret += output;
		output = "";
		return ret;
	}

	private void writeToken(int token) {
		if (token < 0xf5)
			this.output += (char) token;
		else if (token <= 0x1f4)
			this.output += "\u00fe" + (char) (token - 0xf5);
	}

	private void writeJid(String user, String server) {
		this.output += "\u00fa";
		if (user.length() > 0)
			this.writeString(user);
		else
			this.writeToken(0);
		this.writeString(server);
	}

	private void writeInt8(int v) {
		this.output += (char) (v);
	}

	private String writeInt16(int v) {
		String ret = "";
		ret += (char) ((v & 0xff00) >> 8);
		ret += (char) ((v & 0x00ff) >> 0);
		return ret;
	}

	private void writeInt24(int v) {
		output += (char) ((v & 0xff0000) >> 16);
		output += (char) ((v & 0x00ff00) >> 8);
		output += (char) ((v & 0x0000ff) >> 0);
	}

	private void writeBytes(String bytes) {
		int len = bytes.length();
		if (len >= 0x100) {
			this.output += "\u00fd";
			this.writeInt24(len);
		} else {
			this.output += "\u00fc";
			this.writeInt8(len);
		}
		this.output += bytes;
	}

	private void writeString(String tag) {
		if (this.tokenMap.get(tag) != null) {
			Integer key = this.tokenMap.get(tag);
			this.writeToken(key);
		} else {
			int index = tag.indexOf('@');
			if (index >= 0) {
				String server = tag.substring(index + 1);
				String user = tag.substring(0, index);
				this.writeJid(user, server);
			} else
				this.writeBytes(tag);
		}
	}

	private void writeAttributes(Map<String, String> attributes) {
		if (attributes.size() > 0) {
			for (Map.Entry<String, String> entry : attributes.entrySet()) {
				this.writeString(entry.getKey());
				this.writeString(entry.getValue());
			}
		}
	}

	private void writeListStart(int len) {
		if (len == 0)
			this.output += "\u0000";
		else if (len < 256)
			this.output += "\u00f8" + (char) len;
		else
			this.output += "\u00f9" + (char) len;
	}
}
