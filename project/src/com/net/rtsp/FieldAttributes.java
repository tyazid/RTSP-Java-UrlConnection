package com.net.rtsp;

import java.util.HashMap;
import java.util.Set;

/**
 * The FieldAttributes class maps the message header field names to associated
 * string values
 */
public final class FieldAttributes implements Cloneable {
	private HashMap<String,String> map;

	private static final String[] EMPTY_STRINGS = new String[0];

	private FieldAttributes(HashMap<String,String> map) {
		this.map = map;
	}

	/**
	 * Default constructor.
	 */
	public FieldAttributes() {
		this(new HashMap<String,String>());
	}

	/**
	 * @return an array of all field keys .
	 */
	public String[] getKeys() {
		if (map.isEmpty())
			return EMPTY_STRINGS;
		Set<String> kys = map.keySet();
		return  kys.toArray(new String[kys.size()]);
	}

	/**
	 * Removes the mapping for this key from this attributes if present.
	 * 
	 * @param key
	 *            whose mapping is to be removed from the attributes, if
	 *            present.
	 * @return true if the Vector contained the specified element.
	 * 
	 */
	public boolean removeField(String key) throws IllegalArgumentException {
		return map.remove(key) != null;
	}

	/**
	 * Returns a copy of this <tt>FieldAttributes</tt> instance: the keys and
	 * values themselves are not cloned.
	 */
	public Object clone() throws CloneNotSupportedException {
		return new FieldAttributes(  (HashMap<String, String>) map.clone());

	}

	/**
	 * Sets the specified integer value with the specified field attribute key,
	 * 
	 * @param key the attribute field key as a string
	 * @param value the integer value
	 */
	public boolean setField(String key, int value) {
		return setField(key, Integer.toString(value));
	}

	/**
	 * Associates the specified integer value with the specified field attribute
	 * key,
	 * 
	 * @param key the
	 *            attribute field key as a string
	 * @param value
	 *            the integer value
	 */
	public void addField(String key, int value) {
		addField(key, Integer.toString(value));
	}

	/**
	 * Sets the specified float value with the specified field attribute key,
	 * 
	 * @param  key the
	 *            attribute field key as a string
	 * @param value
	 *            the integer value
	 */
	public boolean setField(String key, float value) {
		return setField(key, Float.toString(value));
	}

	/**
	 * Associates the specified float value with the specified field attribute
	 * key,
	 * 
	 * @param  key the
	 *            attribute field key as a string
	 * @param value
	 *            the integer value
	 */
	public void addField(String key, float value) {
		addField(key, Float.toString(value));
	}

	/**
	 * Sets the specified byte value with the specified field attribute key,
	 * 
	 * @param  key the
	 *            attribute field key as a string
	 * @param value
	 *            the integer value
	 */
	public boolean setField(String key, byte value) {
		return setField(key, Byte.toString(value));
	}

	/**
	 * Associates the specified byte value with the specified field attribute
	 * key,
	 * 
	 * @param  key the
	 *            attribute field key as a string
	 * @param value
	 *            the integer value
	 */
	public void addField(String key, byte value) {
		addField(key, Byte.toString(value));
	}

	/**
	 * Sets the specified short value with the specified field attribute key,
	 * 
	 * @param  key the
	 *            attribute field key as a string
	 * @param value
	 *            the integer value
	 */
	public boolean setField(String key, short value) {
		return setField(key, Short.toString(value));
	}

	/**
	 * Associates the specified short value with the specified field attribute
	 * key,
	 * 
	 * @param  key the
	 *            attribute field key as a string
	 * @param value
	 *            the integer value
	 */
	public void addField(String key, short value) {
		addField(key, Short.toString(value));
	}

	/**
	 * Sets the specified double value with the specified field attribute key,
	 * 
	 * @param  key the
	 *            attribute field key as a string
	 * @param value
	 *            the integer value
	 */
	public boolean setField(String key, double value) {
		return setField(key, Double.toString(value));
	}

	/**
	 * Associates the specified double value with the specified field attribute
	 * key,
	 * 
	 * @param  key the
	 *            attribute field key as a string
	 * @param value
	 *            the integer value
	 */
	public void addField(String key, double value) {
		addField(key, Double.toString(value));
	}

	/**
	 * Sets the specified long value with the specified field attribute key,
	 * 
	 * @param  key the
	 *            attribute field key as a string
	 * @param value
	 *            the integer value
	 */
	public boolean setField(String key, long value) {
		return setField(key, Long.toString(value));
	}

	/**
	 * Associates the specified long value with the specified field attribute
	 * key,
	 * 
	 * @param  key the
	 *            attribute field key as a string
	 * @param value
	 *            the integer value
	 */
	public void addField(String key, long value) {
		map.put(key, Long.toString(value));
	}

	/**
	 * Sets the specified String value with the specified field attribute key,
	 * 
	 * @param  key the
	 *            attribute field key as a string
	 * @param value
	 *            the integer value
	 */
	public boolean setField(String key, String value) {

		if (map.containsKey(key)) {
			map.put(key, value);
			return true;
		}
		return false;
	}

	/**
	 * Associates the specified String value with the specified field attribute
	 * key,
	 * 
	 * @param  key the
	 *            attribute field key as a string
	 * @param value
	 *            the integer value
	 */
	public void addField(String key, String value) {
		map.put(key, value);
	}

	/**
	 * Returns the value of the specified Attributes.field key , or null if the
	 * attribute was not found.
	 * <p>
	 * This method is defined as:
	 * 
	 * <pre>
	 * return (String) get(key);
	 * </pre>
	 * 
	 * @param key
	 *            the Attributes.key string
	 *            @return the Attributes.value string
	 */
	public String getField(String key) {
		return (String) map.get(key);
	}

}
