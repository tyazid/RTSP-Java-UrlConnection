package com.net.rtsp.imp.request.client;

import java.io.IOException;

import com.net.rtsp.ClientRequest;
import com.net.rtsp.RequestMessage;

public class Get_Parameter extends ClientRequest {// C-->S
	protected void fillRequest(com.net.rtsp.RtspURLConnection urlC, RequestMessage request, int cseq) throws IOException {
		request.setRequestLine(getCMDHeader(GET_PARAMETER, urlC.getURL().toExternalForm()));
		request.set("CSeq", "" + cseq);
		
	}
}
