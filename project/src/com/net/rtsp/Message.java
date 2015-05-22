package com.net.rtsp;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import com.net.rtsp.content.Content;

/**
 * Base abstract class for RTSP messages.
 */

public abstract class Message {
	private static final String EOL = "\r\n";

	protected List<kvs> list;

	private Content content;

	class kvs {
		String key;

		String value;

		kvs(String key, String value) {
			this.key = key;
			this.value = value;
		}

		public boolean equals(Object obj) {
			kvs o = (kvs) obj;
			if ((o.key == null ^ key == null))
				return false;
			return (o.key == null && null == key) || o.key.equalsIgnoreCase(key);
		}
	}

	/**
	 * Clears this message header so that it contains no keys.
	 */
	public void setEmpty() {
		list.clear();
		content = null;
	}

	/**
	 * Constructor
	 */
	public Message() {
		list = new ArrayList<kvs>();
	}

	/**
	 * Find the value that corresponds to this key. It finds only the first
	 * occurrence of the key.
	 * 
	 * @param k
	 *            the key to find.
	 * @return null if not found.
	 */
	public String findValue(String k) {
		kvs ks = new kvs(k, null);
		int i = list.indexOf(ks);
		if (i >= 0)
			return ((kvs) list.get(i)).value;
		return null;
	}

	/**
	 * Find the Key that corresponds to this index. if n is out of bound null
	 * reference is returned.
	 * 
	 * @param n
	 *            index of the key
	 * @return null if not found.
	 */
	public String getKey(int n) {
		try {
			return ((kvs) list.get(n)).key;
		} catch (IndexOutOfBoundsException e) {
			return null;
		}
	}

	/**
	 * Find the value that corresponds to this index. if n is out of bound null
	 * reference is returned.
	 * 
	 * @param n
	 *            index of the key
	 * @return null if not found.
	 */
	public String getValue(int n) {
		try {
			return ((kvs) list.get(n)).value;
		} catch (IndexOutOfBoundsException e) {
			return null;
		}
	}

	/**
	 * converts the key-value pairs represented by this header to into a
	 * sequence of bytes
	 * 
	 */
	public byte[] toBytes() {
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		PrintWriter p = new PrintWriter(new OutputStreamWriter(bout));
		p.print(getFirstHeaderEntity());// getHeaderHead()
		p.print(EOL);
		kvs[] kvss = (kvs[]) list.toArray(new kvs[list.size()]);
		for (int i = 0; i < kvss.length; i++)
			p.print(kvss[i].key + (kvss[i].value != null ? ": " + kvss[i].value : "") + EOL);
		p.print(EOL);
		if (getContent() != null)
			p.print(getContent().getContent());
		p.flush();
		return bout.toByteArray();
	}
	
	/**
	 * The method returns the 1st line of message header 
	 */
	protected abstract String getFirstHeaderEntity();

	/**
	 * Adds a key value pair to the end of the header. Duplicates are allowed
	 */
	public void add(String k, String v) {
		list.add(new kvs(k, v));
	}

	/**
	 * Sets the body content of this message
	 * 
	 * @param content
	 */
	public void setContent(Content content) {
		this.content = content;
	}

	/**
	 * returns the body content of this message
	 * 
	 * @return Content describing the body of the message
	 */
	public Content getContent() {
		return content;
	}

	/**
	 * Sets the value of a key. If the key already exists in the header, it's
	 * value will be changed. Otherwise a new key/value pair will be added to
	 * the end of the header.
	 */
	public void set(String k, String v) {
		int i = list.indexOf(new kvs(k, null));
		if (i >= 0)
			((kvs) list.get(i)).value = v;
		else
			add(k, v);
	}

	/**
	 * Set's the value of a key only if there is no value already.
	 */
	public void setIfNotSet(String k, String v) {
		if (findValue(k) == null) {
			add(k, v);
		}
	}

	public String toString() {
		String result = super.toString();
		kvs[] kvss = (kvs[]) list.toArray(new kvs[list.size()]);
		for (int i = 0; i < kvss.length; i++) {
			result += "{" + kvss[i].key + ": " + kvss[i].value + "}\n";
		}
		return result;
	}
}
