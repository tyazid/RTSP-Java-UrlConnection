package com.net.rtsp.imp.request.client;

import java.io.IOException;

import com.net.rtsp.ClientRequest;
import com.net.rtsp.RequestMessage;


public class Set_Parameter extends ClientRequest {// C-->S
	protected void fillRequest(com.net.rtsp.RtspURLConnection urlc, RequestMessage request, int cseq) throws IOException {
		request.setRequestLine(getCMDHeader(SET_PARAMETER, urlc.getURL().toExternalForm()));
		request.set("CSeq", "" + cseq);
	}
	  
}
