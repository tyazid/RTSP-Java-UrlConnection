package com.net.rtsp.imp.request.client;

import com.net.rtsp.ClientRequest;
import com.net.rtsp.RequestMessage;


public class Teardown extends ClientRequest {
	protected void fillRequest(com.net.rtsp.RtspURLConnection urlC, RequestMessage request, int cseq) {
		request.setRequestLine(getCMDHeader(TEARDOWN, urlC.getURL().toExternalForm()));
		request.set("CSeq", "" + cseq);
		request.set("Session", urlC.getSessionID());// ;client_port=4212");//AVP
		
	}
}
