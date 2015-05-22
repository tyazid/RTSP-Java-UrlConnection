package com.net.rtsp.imp.request.server;

import com.net.rtsp.RtspMethods;
import com.net.rtsp.RtspURLConnection;
import com.net.rtsp.ServerRequestHandler;
import com.net.rtsp.ServerRequestHandlerFactory;

public class DefaultServerMessageRequestHandler extends ServerRequestHandlerFactory implements RtspMethods {
	private static final String[] supported_server_side_methods = new String[] { OPTIONS };

	ServerRequestHandler[] htab = new ServerRequestHandler[supported_server_side_methods.length];
	
	private static int searchIn(String[] in, String s) {
		for (int i = 0; i < in.length; i++) 
			if(in[i].equals(s))
				return i;
		return -1;
	}

	public ServerRequestHandler getServerHandler(String method, RtspURLConnection con) {
		int pos = searchIn(supported_server_side_methods, method);
		if (pos >= 0) {
			if (htab[pos] == null) {
				String m = supported_server_side_methods[pos];
				if (m.equals(OPTIONS)) htab[pos] = new Options();
			}
			return htab[pos];
		}
		return null;
	}

	public String[] getSupportedClientMethod() {
		return (String[]) supported_server_side_methods.clone();
	}
}
