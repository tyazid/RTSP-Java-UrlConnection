package com.net.rtsp.imp.request.client;

import java.io.IOException;

import com.net.rtsp.ClientRequest;
import com.net.rtsp.RequestMessage;


public class Pause extends ClientRequest {
	protected void fillRequest(com.net.rtsp.RtspURLConnection urlC, RequestMessage request, int cseq) throws IOException {
		request.setRequestLine(getCMDHeader(PAUSE, urlC.getURL().toExternalForm()));
		request.set("CSeq", "" + cseq);
		request.set("Session", urlC.getSessionID());// ;client_port=4212");//AVP
		
	}	
}
