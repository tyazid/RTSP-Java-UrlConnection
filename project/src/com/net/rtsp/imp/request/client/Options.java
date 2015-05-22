package com.net.rtsp.imp.request.client;

import java.io.IOException;

import com.net.rtsp.ClientRequest;
import com.net.rtsp.RequestMessage;


public class Options extends ClientRequest {
	protected void fillRequest(com.net.rtsp.RtspURLConnection urlC, RequestMessage request, int cseq) throws IOException {
		request.setRequestLine(OPTIONS + " * " +  RTSP_VERSION);
		request.set("CSeq", "" + cseq);
		
	}	
}
