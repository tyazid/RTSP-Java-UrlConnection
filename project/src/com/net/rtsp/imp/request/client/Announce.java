package com.net.rtsp.imp.request.client;

import java.io.IOException;
import java.net.ProtocolException;

import com.net.rtsp.ClientRequest;
import com.net.rtsp.RequestMessage;


public class Announce extends ClientRequest {
	protected void fillRequest(com.net.rtsp.RtspURLConnection urlC, RequestMessage request, int cseq) throws IOException{
		throw new ProtocolException("Invalid rtsp method: Announce");
		
	}

	public boolean isSuccessfullResponse() {
		// TODO Auto-generated method stub
		return false;
	}
	 
}
