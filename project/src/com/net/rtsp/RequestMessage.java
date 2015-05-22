package com.net.rtsp;



/**
 * An RFC 2326: Real Time Streaming Protocol (RTSP) request message header. 
 * 
 */
public class RequestMessage extends Message{
	
	private String rl;
	/**
	 * sets the request line value of this request message.
	 * @param rl
	 */
	public void setRequestLine(String rl) {
		this.rl = rl;
	}	
/**
 * Returns the request line of the request.
 * @return string defines the request line.
 */
	public String getRequestLine() {
		return rl;
	}

	/* (non-Javadoc)
	 * @see com.net.rtsp.Message#getFirstHeaderEntity()
	 */
	protected String getFirstHeaderEntity() {
		return getRequestLine();
	}
	
	public String toString() {
		// TODO Auto-generated method stub
		return rl +"\n" +super.toString();
	}
	
}
