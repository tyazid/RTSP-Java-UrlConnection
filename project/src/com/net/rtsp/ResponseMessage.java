package com.net.rtsp;




/**
 * Wraps up a RTSP response message.
 */
public class ResponseMessage extends Message{
	
	private String statusLine,status;
	private int code;

	/**
	 * construct a response message instance with a status line
	 * @param statusLine
	 */
	public ResponseMessage(String statusLine) {
		super();
		setStatusLine(statusLine);
		
	}
	
	ResponseMessage() {
		this(null);
	}
	
	/**
	 * Implementation of {@link Message#getFirstHeaderEntity()}
	 */
	protected String getFirstHeaderEntity() {
		return getStatusLine();
	}
	/**
	 * Return the status line of the message header
	 * @return the status line
	 */
	public String getStatusLine() {
		return statusLine;
	}
	
//	public int getResponseCode() throws IOException {
//		int m_responseCode;
//		// make sure we've gotten the headers
//		getInputStream();
//		String resp = getHeaderField(0);
//		int ind;
//		try {
//			ind = resp.indexOf(' ');
//			while (resp.charAt(ind) == ' ')
//				ind++;
//			m_responseCode = Integer.parseInt(resp.substring(ind, ind + 3));
//			responseStatusLine = resp.substring(ind + 4).trim();
//			return m_responseCode;
//		} catch (Exception ex) {
//			ex.printStackTrace();
//			return 0;
//		}
//	}
	
	/**
	 * Clears the content of this response message object.
	 */
	public void setEmpty() {
		statusLine = null;
		super.setEmpty();
		
	}

	/* (non-Javadoc)
	 * @see com.net.rtsp.Message#add(java.lang.String, java.lang.String)
	 */
	public void add(String k, String v) {
		if(k == null && list.isEmpty()){
			setStatusLine( v );
		}
		super.add(k, v);
	}
	/**
	 * return the status code of this message. 
	 * @see ResponseCodes for possible values
	 * @return the status code of this response object. 
	 */
	int getStatusCode(){
		return code;
	}
	
	/**
	 *return the status label of this response (such as OK for code 200) 
	 * @return string the status label
	 */
	
	String getStatus(){
		return status;
	}
	private void setStatusLine(String statusLine){
		this.statusLine = statusLine;
		if(statusLine == null)
			return;
		int ind;
		try {
			ind = statusLine.indexOf(' ');
			while (statusLine.charAt(ind) == ' ')
				ind++;
			code = Integer.parseInt(statusLine.substring(ind, ind + 3));
			status = statusLine.substring(ind + 4).trim();
			 
		} catch (Exception ex) {
			ex.printStackTrace();
			 
		}
	}
}
