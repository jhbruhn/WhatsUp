package org.alternadev.whatsup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BinTreeNodeReader {
	private String[] dic;
	private int[] input;

	public BinTreeNodeReader(String[] dic) {
		this.dic = dic;
	}

	public ProtocolNode nextTree() throws InvalidTokenException,
			IncompleteMessageException {
		return nextTree(null);
	}

	public ProtocolNode nextTree(int[] input) throws InvalidTokenException,
			IncompleteMessageException {
		if (input != null)
			this.input = input;
		int stanzaSize = this.peekInt16();
		if (stanzaSize > this.input.length)
			throw new IncompleteMessageException(
					("Die Message war mal so mäßig incomplete et ita. ("
							+ stanzaSize + ", " + this.input.length + ")"),
					this.input);
		this.readInt16();
		if (stanzaSize > 0)
			return this.nextTreeInternal();
		return null;
	}

	protected String getToken(int token) throws InvalidTokenException {
		if (token >= 0 && token < dic.length)
			return dic[token];
		throw new InvalidTokenException(token);
	}

	protected String readString(int token) throws InvalidTokenException {
		String ret = "";
		if (token == -1)
			throw new InvalidTokenException(token);

		if (token > 4 && token < 0xf5)
			ret = this.getToken(token);
		if (token == 0)
			ret = "";
		else if (token == 0xfc) {
			int size = this.readInt8();
			ret = this.fillArray(size);
		} else if (token == 0xfd) {
			int size = this.readInt24();
			ret = this.fillArray(size);
		} else if (token == 0xfe) {
			int size = this.readInt8();
			ret = this.fillArray(size + 0xf5);
		} else if (token == 0xfa) {
			String user = this.readString(this.readInt8());
			String server = this.readString(this.readInt8());
			if (user.length() > 0 && server.length() > 0)
				ret = user + "@" + server;
			else if (server.length() > 0)
				ret = server;
		}
		return ret;
	}

	protected Map<String, String> readAttributes(int size)
			throws InvalidTokenException {
		Map<String, String> map = new HashMap<String, String>();
		int attribCount = (size - 2 + size % 2) / 2;
		for (int i = 0; i < attribCount; i++) {
			String key = this.readString(this.readInt8());
			String value = this.readString(this.readInt8());
			map.put(key, value);
		}
		return map;
	}

	protected ProtocolNode nextTreeInternal() throws InvalidTokenException {
		int token = this.readInt8();
		int size = this.readListSize(token);

		token = this.readInt8();
		if (token == 1) {
			return new ProtocolNode("start", this.readAttributes(size), null,
					"");

		} else if (token == 2)
			return null;
		String tag = this.readString(token);
		Map<String, String> attributes = this.readAttributes(size);
		if (size % 2 == 1)
			return new ProtocolNode(tag, attributes, null, "");
		token = this.readInt8();
		if (this.isListTag(token))
			return new ProtocolNode(tag, attributes, this.readList(token), "");
		return new ProtocolNode(tag, attributes, null, this.readString(token));
	}

	protected boolean isListTag(int token) {
		return (token == 248 || token == 0 || token == 259);
	}

	protected List<ProtocolNode> readList(int token)
			throws InvalidTokenException {
		int size = this.readListSize(token);
		List<ProtocolNode> ret = new ArrayList<ProtocolNode>();
		for (int i = 0; i < size; i++)
			ret.add(this.nextTreeInternal());
		return ret;
	}

	protected int readListSize(int token) throws InvalidTokenException {
		int size = 0;
		if (token == 0xf8) {
			size = this.readInt8();
		} else if (token == 0xf9) {
			size = this.readInt16();
		} else {
			throw new InvalidTokenException(token);
		}

		return size;
	}

	protected int readInt24() {
		int ret = 0;
		if (this.input.length >= 3) {
			ret = input[0] << 16;
			ret += input[1] << 8;
			ret += input[2] << 0;

			removeFromInput(3);
		}
		return ret;
	}

	private void removeFromInput(int num) {
		int[] karl = new int[input.length - num];
		for (int i = num; i < input.length; i++)
			karl[i - num] = input[i];
		input = karl;
	}

	protected int peekInt16() {
		int ret = 0;
		if (this.input.length >= 2) {
			ret = input[0] << 8;
			ret += input[1] << 0;
		}

		return ret;
	}

	protected int readInt16() {
		int ret = peekInt16();
		if (ret > 0) {
			removeFromInput(2);
		}
		return ret;
	}

	protected int readInt8() {
		int ret = 0;
		if (this.input.length >= 1) {
			ret = input[0];
			removeFromInput(1);
		}

		return ret;
	}

	protected String fillArray(int len) {
		String ret = "";
		if (this.input.length >= len) {
			ret = new String(intToCharArray(input)).substring(0, len);
			removeFromInput(len);
		}
		return ret;
	}
	
	private char[] intToCharArray(int[] in) {
		char[] peda = new char[in.length];
		for(int i = 0; i < in.length; i++)
			peda[i] = (char) (((char) in[i]));
		return peda;
	}
}
