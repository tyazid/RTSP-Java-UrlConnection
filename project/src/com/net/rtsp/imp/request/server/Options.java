package com.net.rtsp.imp.request.server;

import java.io.IOException;

import com.net.rtsp.RequestMessage;
import com.net.rtsp.ResponseCodes;
import com.net.rtsp.ResponseMessage;
import com.net.rtsp.ServerProcessor;
import com.net.rtsp.ServerRequestHandler;
import com.net.rtsp.ServerRequestHandlerFactory;
import com.net.rtsp.StatusLineBuilder;

public class Options extends ServerRequestHandler {

	public void handleRequest(RequestMessage request, ServerProcessor server) throws IOException {
		String[] supportedMethod = ServerRequestHandlerFactory.getInstance().getSupportedClientMethod();
		String scseq = request.findValue("CSeq");
		int cseq = Integer.parseInt(scseq);
		cseq++;
		ResponseMessage response = new ResponseMessage(StatusLineBuilder.getStatusLine(ResponseCodes.RTSP_OK) );
		response.set("CSeq", "" + cseq);
		StringBuffer sb = new StringBuffer();// "Public: ");
		for (int i = 0; i < supportedMethod.length; i++) {
			sb.append(' ');
			sb.append(supportedMethod[i]);
			if (i < (supportedMethod.length - 1)) {
				sb.append(',');
			}
		}
		response.set("Public:", sb.toString());
		sendResponse(response, server);

	}
}
