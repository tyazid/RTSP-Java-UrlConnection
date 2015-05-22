package com.net.rtsp;

import java.util.HashMap;
import java.util.Set;

/**
 * Defines Media description as defined in    
 * <A HREF="http://www.ietf.org/rfc/rfc2326.txt?number=2326"> RTSP rfc 2326 section 10.2</A> and
 *  <A HREF="http://www.ietf.org/rfc/rfc4566.txt"> RFC4566 section 5.14</A> 
 * <br>Media Descriptions ("m=") m=< media > < port > < proto > < fmt >.
 * <br> < media > is the media type. <port> is the transport port to which the
 * media stream is sent
 * <br>< proto > is the transport protocol.
 * <br>< fmt > is a media format description.
 * 
 * @author tyazid
 * 
 */
public final class MediaDescriptions implements Cloneable {
	private HashMap<String, Object[]> map;

	private static final String[] EMPTY_STRINGS = new String[0];

	private MediaDescriptions(HashMap<String, Object[]> map) {
		this.map = map;
	}

	/**
	 * Creates a <code>MediaDescriptions</code> object from the
	 * <code>String</code> representation.
	 * <p>
	 * 
	 * @param spec
	 *            a <code>String</code> representation of a
	 *            MediaDescriptions(pattern= < media > < port > < proto > < fmt >)
	 */
	MediaDescriptions(String spec) {
		map = new HashMap<String, Object[]>();
		addDescritpion(spec);
	}

	public MediaDescriptions(String mediaType, int mediaPort, String transport, String fmt) {
		map = new HashMap<String, Object[]>();
		addMediaType(mediaType, mediaPort, transport, fmt);
	}

	/**
	 * This method returns the media streams as described in the response of
	 * DECRIBE RTSP method
	 * 
	 * @return an array of all stream type of current media.
	 */
	public String[] getMediaTypes() {
		if (map.isEmpty())
			return EMPTY_STRINGS;
		Set<String> kys = map.keySet();
		return  kys.toArray(new String[kys.size()]);
	}

	/**
	 * This method returns the port number serves as a recommendation from the
	 * server of a type of media, 0 means that the server has no port preference
	 * for this media type.
	 * 
	 * @param mediaType
	 *            the media type [audio, video ...]
	 * @return port number.
	 * @exception IllegalArgumentException
	 *                is raised if the type is not one of type returned by the
	 *                method getMediaTypes
	 * @see #getMediaTypes()
	 */
	public int getMediaPort(String mediaType) throws IllegalArgumentException {
		Integer i = (Integer) getField(mediaType, 0);
		return i != null ? i.intValue() : 0;
	}
	
	
/**
 * Returns the transport type for the given media type
 * @param mediaType the media type (such as "AUDIO", "VIDEO" ..)
 * @return return the transport type as string ("RTP/AVP"...), see rtsp rfc 2326 sec 10.2 & RFC4566 section 5.14 for more details
 * @throws IllegalArgumentException
 */
	public String getMediaTransportTytpe(String mediaType) throws IllegalArgumentException {
		return (String) getField(mediaType, 1);
	}

	/**
	 * Returns the media format type for the given media type
	 * @param mediaType
	 * @return the media format depending on the media type and tranport type.see rtsp RFC2326 sec 10.2 & RFC4566 section 5.14 for more details
	 * @throws IllegalArgumentException
	 */
	public String getMediaFormatDescription(String mediaType) throws IllegalArgumentException {
		return (String) getField(mediaType, 2);
	}

	private Object getField(String key, int indx) {
		if (map.containsKey(key))
			return  map.get(key)[indx];

		return null;
	}

	public Object clone() {
		return new MediaDescriptions( (HashMap<String, Object[]>) map.clone());

	}

	public void setMediaTransportPort(String mediaType, int mediaPort) {
		if (!setField(mediaType, 0, new Integer(mediaPort)))
			throw new IllegalArgumentException(mediaType + " media type is not reconized for this server & session.");
	}

	public void setMediaTransportTytpe(String mediaType, String transport) {
		if (!setField(mediaType, 1, transport))
			throw new IllegalArgumentException(mediaType + " media type is not reconized for this server & session.");
	}

	public void setMediaFormatDescription(String mediaType, String fmd) {
		if (!setField(mediaType, 2, fmd))
			throw new IllegalArgumentException(mediaType + " media type is not reconized for this server & session.");
	}

	private boolean setField(String key, int indx, Object value) {
		if (map.containsKey(key)) {
			((Object[]) map.get(key))[indx] = value;
			return true;
		}
		return false;
	}

	public void addMediaType(String mediaType, int mediaPort, String transport, String fmt) {
		map.put(mediaType, new Object[] { new Integer(mediaPort), transport, fmt });
	}

	public  void addDescritpion(String m) {
		String type, trsprt, fmt;
		int prt;
		fmt = trsprt = null;
		Debug.println("RtspURLConnection.getMediaStreamConfig() ::: m=" + m);
		type = m.substring(0, m.indexOf(' '));
		m = m.substring(type.length()).trim();
		trsprt = m.substring(0, m.indexOf(' '));// port
		try {
			prt = Integer.parseInt(trsprt);
		} catch (NumberFormatException e) {
			prt = 0;
			e.printStackTrace();
		}
		m = m.substring(trsprt.length()).trim();
		if (m.indexOf(' ') > 0) {
			trsprt = m.substring(0, m.indexOf(' ') + 1).trim();// port
			m = m.substring(trsprt.length()).trim();
			m = m.trim();
			if (m.length() > 0) {
				if (m.indexOf(' ') > 0)
					fmt = m.substring(0, m.indexOf(' ') + 1);
				else
					fmt = m;
			}
		}
		Debug.println("RtspURLConnection.getMediaStreamConfig() ===> type=" + type + ", port=" + prt + ". trans=" + trsprt + "." + " fmt=" + fmt + ".");
		addMediaType(type, prt, trsprt, fmt);

	}
}
