package com.net.rtsp;

import java.util.ArrayList;
import java.util.List;



/**
 * This class defines a factory for the creation of client requests.
 */
public abstract class ClientRequestFactory {
	private static final String prep = "rtsp.request.client.factory.";

	private static ClientRequestFactory instance;
/**
 * Default constructor.
 */
	protected ClientRequestFactory() {
	}
	
	/**
     * Provides an instance of ClientRequestFactory.
     * @return an instance of ClientRequestFactory.
     */
	public static synchronized ClientRequestFactory getInstance() {
		synchronized (ClientRequestFactory.class) {
			if (ClientRequestFactory.instance == null) {
				int i = 0;
				List<Object> l = new ArrayList<Object>();
				java.util.Properties sysprop = System.getProperties();
				String cln;
				while ((cln = sysprop.getProperty(prep + i + ".cl")) != null) {
					try {
						com.net.rtsp.Debug.println("ClientRequestFactory.getInstance() cln="+cln);
						Class<?> cl = Class.forName(cln);
						l.add(cl.newInstance());
					} catch (Exception e) {
						e.printStackTrace();
					}
					i++;
				}
				try {
					if (l.size() <= 1) {
						ClientRequestFactory.instance = (ClientRequestFactory) (l.size() == 0 ? 
								Class.forName(Config.getValue("rtsp.client.request.factory.default")).newInstance() : l.get(0));
					} else {
						ClientRequestFactory.instance = 
							  new XReqFactory((ClientRequestFactory[]) l.toArray(new ClientRequestFactory[l.size()]));
					}
				} catch (Exception e) {
					e.printStackTrace();
					throw new Error("Cannot instanciate RequestFactory");
				}
			}
		}
		return instance;
	}
	/**
	 * Creates a ClientRequest for the specified rtsp method name and RtspURLConnection.
	 * @param method rtsp method name (see {@link RtspMethods})
	 * @param con RtspURLConnection expresses the current rtsp session.
	 * @return ClientRequest implementation which match with the given method name.
	 */
	public abstract ClientRequest getRequest(String method,RtspURLConnection con);
	
	private static class XReqFactory extends ClientRequestFactory {
		ClientRequestFactory[] factories;

		XReqFactory(ClientRequestFactory[] factories) {
			this.factories = factories;
		}

		public ClientRequest getRequest(String method,RtspURLConnection con) {
			ClientRequest m;
			for (int i = 0; i < factories.length; i++) {
				m = factories[i].getRequest(method,con);
				if (m != null)
					return m;
			}
			return null;
		}
	}
}
