package com.net.rtsp;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 
 * The abstract class <code>ServerRequestHandlerFactory</code> is the
 * superclass of all classes that map map an RTSP method name type into an
 * instance of <code>ServerRequestHandler</code>.
 * 
 * @author tyazid
 * 
 */
public abstract class ServerRequestHandlerFactory {
	private static final String prep = "rtsp.request.server.handler.factory.";

	protected ServerRequestHandlerFactory() {

	}

	private static ServerRequestHandlerFactory instance;

	/**
	 * Provides an instance of ServerRequestHandlerFactory
	 * 
	 * @return an instance of ServerRequestHandlerFactory
	 */

	public static synchronized ServerRequestHandlerFactory getInstance() {

		synchronized (ServerRequestHandlerFactory.class) {
			if (ServerRequestHandlerFactory.instance == null) {
				int i = 0;
				List l = new ArrayList();
				java.util.Properties sysprop = System.getProperties();
				String cln;
				while ((cln = sysprop.getProperty(prep + i++ + ".cl")) != null) {
					try {
						
						Class cl = Class.forName(cln);
						l.add(cl.newInstance());
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				try {
					if (l.size() <= 1) {
						ServerRequestHandlerFactory.instance = (ServerRequestHandlerFactory) (l.size() == 0 ? 
								  Class.forName(Config.getValue("rtsp.server.request.handler.factory.default")).newInstance() : 
									  l.get(0));
					} else {
						ServerRequestHandlerFactory.instance = new XServerMessageRequestHandler((ServerRequestHandlerFactory[]) l
								.toArray(new ServerRequestHandlerFactory[l.size()]));
					}
				} catch (Exception e) {
					e.printStackTrace();
					throw new Error("Cannot instanciate ServerMessageRequestHandler");
				}
			}
		}
		return instance;

	}
	/**
	 * Returns ServerRequestHandler object matching with the RTSP method definition for a given rtsp url connection.
	 * @param method the method definition
	 * @param con the rtsp url connection whose retrieving the server request handler.
	 * @return the ServerRequestHandler object.
	 */

	public abstract ServerRequestHandler getServerHandler(String method, RtspURLConnection con);

	/**
	 * all supported RTSP method definition of this handler.
	 * @return list of support method as an array of <code>String</code>. 
	 */
	public abstract String[] getSupportedClientMethod();

	private static class XServerMessageRequestHandler extends ServerRequestHandlerFactory {
		ServerRequestHandlerFactory[] factories;

		XServerMessageRequestHandler(ServerRequestHandlerFactory[] factories) {
			this.factories = factories;
		}

		public ServerRequestHandler getServerHandler(String method, RtspURLConnection con) {
			ServerRequestHandler srh;
			for (int i = 0; i < factories.length; i++) {
				srh = factories[i].getServerHandler(method, con);
				if (srh != null)
					return srh;
			}
			return null;
		}

		public String[] getSupportedClientMethod() {
			Set set = new HashSet();
			for (int i = 0; i < factories.length; i++) {
				String[] sub = factories[i].getSupportedClientMethod();
				if (sub != null)
					for (int j = 0; j < sub.length; j++)
						set.add(sub[j]);
			}
			return (String[]) set.toArray(new String[set.size()]);
		}
	}
}
