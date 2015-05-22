package com.net.rtsp;

import java.net.ContentHandler;
import java.net.ContentHandlerFactory;
import java.util.HashMap;
import java.util.Vector;

/**
 * This class defines and implementation of {@link ContentHandlerFactory} which
 * maps a MIME type into an instance of
 * {@link com.net.rtsp.content.ContentHandler}.
 * <br><i>General schemes of how a new rtsp URL protocol handling can be introduced in Java net framework.</i>
 * <br><img src="rtsp2.jpg"/>
 * @author tyazid
 * 
 */
public class RtspContentHandlerFactory implements ContentHandlerFactory {
	private static HashMap<String, Class<?>> types;

	private  String defaultPrefixPackage;
	static {
		types = new HashMap<String, Class<?>>();

		// prefixPackage = RtspContentHandlerFactory.class.getName();
		//		
		// int endIndex = prefixPackage.lastIndexOf('.');
		// if (endIndex > 0) {
		// prefixPackage = prefixPackage.substring(0, endIndex);
		// prefixPackage += '.';
		// prefixPackage += "content";
		// com.net.rtsp.Debug.println("RtspContentHandlerFactory.P=" +
		// prefixPackage);
		// }
	}

	private static RtspContentHandlerFactory instance;

	private Vector<String> prefixes;

	private RtspContentHandlerFactory() {
		prefixes = new Vector<String>();
		defaultPrefixPackage = Config.getValue("rtsp.content.handler.factory.package.prefix.default");
		prefixes.addElement(Config.getValue("rtsp.content.handler.factory.package.prefix.default"));
	}

	/**
	 * Provides an instance of RtspContentHandlerFactory
	 * 
	 * @return an instance of RtspContentHandlerFactory
	 */
	public static RtspContentHandlerFactory getInstance() {
		synchronized (RtspContentHandlerFactory.class) {
			if (RtspContentHandlerFactory.instance == null)
				RtspContentHandlerFactory.instance = new RtspContentHandlerFactory();
		}
		return RtspContentHandlerFactory.instance;
	}

	/**
	 * add a package prefix of a package which contains the implementation of
	 * the content handler. this implementation should respect some naming and
	 * package structure rules as following: each mime type (like “text”,
	 * “application/sdp”..) implementation should contains a content hander
	 * called ContentHandler, a response content called ResponseContent and a
	 * request content called ResponseContent those implementations should
	 * belong to a package with mime type as suffix (for “application/sdp” the
	 * package suffix became “application.sdp”)
	 * 
	 * @param packageName
	 *            prefix (parent) package name.
	 */
	public void addContentPackagePrefix(String packageName) {
		if (!prefixes.contains(packageName))
			prefixes.insertElementAt(packageName, 0);

	}

	/**
	 * remove the prefix package name.
	 * 
	 * @param packageName
	 */
	public void removeContentPackagePrefix(String packageName) {
		if (!packageName.equals(defaultPrefixPackage))
			prefixes.removeElement(packageName);
	}

	/**
	 * Determines whether the prefix packageName is managed or not. A prefix
	 * package name is managed if it has been added using
	 * addContentPackagePrefix method.
	 * 
	 * @param packageName
	 *            prefix
	 * @return <code>true</code> if the packageName is managed;
	 *         <code>false</code> otherwise.
	 */
	public boolean isPackagePrefixManaged(String packageName) {
		return prefixes.contains(packageName);
	}

	/* (non-Javadoc)
	 * @see java.net.ContentHandlerFactory#createContentHandler(java.lang.String)
	 */
	public final ContentHandler createContentHandler(String mimetype) {
		mimetype = mimetype.toLowerCase();
		Class<?> ctcl =  types.get(mimetype);
		if (ctcl == null) {

			String mime_cl = mimetype.replace('\\', '.');
			mime_cl = mime_cl.replace('/', '.');

			String[] prefxs =new String[prefixes.size()];
			prefixes.copyInto(prefxs);
			 
			for (int i = 0; i < prefxs.length; i++) {
				String cln = prefxs[i];// RtspContentHandlerFactory.prefixPackage;
				cln += '.';
				cln += mime_cl;
				cln += '.';

				String p = ContentHandler.class.getName();// rename the class
				int endIndex = p.lastIndexOf('.');
				cln += endIndex > 0 ? p.substring(endIndex + 1) : p;
				com.net.rtsp.Debug.println("RtspContentHandlerFactory.createContentHandler(" + mimetype + ") Resolved cl = " + cln);
				try {
					ctcl = Class.forName(cln);
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
				if (ctcl != null) {
					types.put(mimetype, ctcl);
					break;
				}
			}
		}
		if (ctcl != null) {
			try {
				return (ContentHandler) ctcl.newInstance();
			} catch (Exception e) {
				com.net.rtsp.Debug.println("Warn : CL ="+ctcl);
				e.printStackTrace();
			}
		}
		return null;
	}

}
