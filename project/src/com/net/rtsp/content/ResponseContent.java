package com.net.rtsp.content;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
/**
 * The abstract class <code>ResponseContent</code> is the superclass of all RTSP response Content classes that read a response body content Object from a RtspURLConnection.


 * @author tyazid
 *
 */
public abstract class ResponseContent extends Content {
	private static final String[] empty_strings = new String[0];

	private Map fields;
	protected byte[] content;
/**
 * construct a new <code>ResponseContent</code> using the mime type and the content byte array.
 * @param content byte array content
 * @param mimeType mime type of the content.
 */
	protected ResponseContent(byte[] content, String mimeType) {
		 super(mimeType);
		 fields = new HashMap();
		 this.content = (byte[]) content.clone();
		// com.net.rtsp.Debug.println("Content.Content() : " + new String(content));
		 parseContent();
		
	}
	
	/* (non-Javadoc)
	 * @see com.net.rtsp.content.Content#getContent()
	 */
	public byte[] getContent() {
		return (byte[]) content.clone();

	}

	protected final void appendFields(String key, Object value) {
		List l = (List) fields.get(key);
		if (l == null)
			fields.put(key, l = new ArrayList());
		l.add(value);
	}

	public final int getContentLenght() {
		return content.length;

	}

	/**
	 * Return the content field values that correspond to the content field key 
	 * @param key the content field key
	 * @return the content field value as object array or an empty array if the key is not found
	 */
	public final Object[] getContentFields(String key) {
		List l = (List) fields.get(key);
		if (l != null)
			return l.toArray();
		return empty_strings;
	}
/**
 * Parses the content of this response. This method is called by the constructor. 
 * 
 */
	protected void parseContent() {// ok, u can override
		 
		BufferedReader br = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(content)));
		String l;
		String k, v;
		try {
			while ((l = br.readLine()) != null) {
				int idx = l.indexOf('=');
				if(idx > 0){
					k = l.substring(0,idx);
					v = l.substring(idx + 1);
					appendFields(k, v);
				}
				 
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	public String toString() {
		StringBuffer sb = new StringBuffer(getClass().getName() + ":\n");
		String k;
		for (Iterator i = fields.keySet().iterator(); i.hasNext();) {
			k = (String) i.next();
			Object[] f = getContentFields(k);
			sb.append("\t" + k + ":");
			for (int j = 0; j < f.length; j++)
				sb.append("\t\t" + f[j] + "\n");

			sb.append("\n");
		}

		return sb.toString();
	}

}
