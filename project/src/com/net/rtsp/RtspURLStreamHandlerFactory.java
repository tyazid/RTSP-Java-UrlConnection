package com.net.rtsp;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.net.URLStreamHandlerFactory;
import java.util.HashMap;

/**
 * This class implements {@link URLStreamHandlerFactory} which maps a protocol
 * into an instance of {@link RtspHandler} object.
 * 
 * @author tyazid
 * 
 */
public class RtspURLStreamHandlerFactory implements URLStreamHandlerFactory {

	private   URLStreamHandler handler;
	
	protected   RtspURLConnection createUrlConnection(URL u) {
		return new RtspURLConnection(u);
	}

	public final URLStreamHandler createURLStreamHandler(String protocol) { 
		com.net.rtsp.Debug
				.println("RtspURLStreamHandlerFactory.createURLStreamHandler() url . p="
						+ protocol);
		if(protocol.equalsIgnoreCase("rtsp")) {
			   if(handler == null) {
				   com.net.rtsp.Debug.println("RtspURLStreamHandlerFactory.createURLStreamHandler() create handler...");
				   /** * An URLStreamHandler implementation  */
				   handler = new  java.net.URLStreamHandler() {
						HashMap refs = new HashMap();
						protected URLConnection openConnection(URL u) throws IOException {
							RtspURLConnection uc = (RtspURLConnection) refs.get(u);
							if (uc == null || uc.getState() == RtspURLConnection.DOWN_STATE)
								refs.put(u, uc = createUrlConnection(u));
							else com.net.rtsp.Debug.println("########  KEEP CACHED URL CONNECTION "+uc);
							return uc;
						}
				   };  
			   }
			  return handler; 
		}
		return null;
	}

}
