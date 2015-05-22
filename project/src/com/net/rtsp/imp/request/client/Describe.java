package com.net.rtsp.imp.request.client;

import java.io.IOException;

import com.net.rtsp.ClientRequest;
import com.net.rtsp.RequestMessage;


public class Describe extends ClientRequest {
	private static final String acceptString = "application/sdp, text/parameters";
	protected String getAcceptedContentType(){
		return acceptString;
	}
	
	protected void fillRequest(com.net.rtsp.RtspURLConnection urlC, RequestMessage request, int cseq) throws IOException{
		request.setRequestLine(getCMDHeader(DESCRIBE, urlC.getURL().toExternalForm()));
		request.set("CSeq", "" + cseq);
		request.set("Accept", getAcceptedContentType());
		
	
	}
}
