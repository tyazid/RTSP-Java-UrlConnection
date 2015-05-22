package com.net.rtsp.imp.request.client;

import java.io.IOException;

import com.net.rtsp.ClientRequest;
import com.net.rtsp.RequestMessage;

public class Play extends ClientRequest {
	protected void fillRequest(com.net.rtsp.RtspURLConnection urlc, RequestMessage request, int cseq) throws IOException {
		request.setRequestLine(getCMDHeader(PLAY, urlc.getURL().toExternalForm()));
		request.set("CSeq", "" + cseq);
		request.set("Session", urlc.getSessionID());// ;client_port=4212");//AVP
	}
}
