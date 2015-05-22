package com.net.rtsp.imp.request.client;

import java.io.IOException;
import java.net.URL;

import com.net.rtsp.ClientRequest;
import com.net.rtsp.MediaDescriptions;
import com.net.rtsp.RequestMessage;
import com.net.rtsp.ResponseMessage;
import com.net.rtsp.content.ResponseContent;

public class Setup extends ClientRequest {
	protected void fillRequest(com.net.rtsp.RtspURLConnection urlc, RequestMessage request, int cseq) throws IOException {
	}

	private String getAbsoluteURL(String relativeURL, URL url) {
		// tst if relative url?
		if (relativeURL.toUpperCase().startsWith(PROTOCOL))
			return relativeURL;
		return url.toExternalForm() + URL_SEPARATOR + relativeURL;

	}

	// private String[] getMediaURLs(RtspURLConnection urlc) throws IOException
	// {
	// Content cnt = (Content) urlc.getContent();
	// Object[] mediaStreams = cnt.getContentFields("m");
	// for (int i = 0; i < mediaStreams.length; i++) {
	// com.net.rtsp.Debug.println(" m " + i + " = " + mediaStreams[i]);
	// }
	//	
	// int nbMedia = mediaStreams.length;
	//	
	// String url[] = new String[nbMedia];
	//	
	// Object[] appCtrl = cnt.getContentFields("a");
	//	
	// if (nbMedia == 1 && (appCtrl == null || appCtrl.length == 0))
	// url[0] = urlc.getURL().toExternalForm();
	// else {
	// int i = 0;
	// String ac;
	// for (int j = 0; j < appCtrl.length; j++) {
	// ac = (String) appCtrl[j];
	// if (ac.startsWith("control:"))
	// url[i++] = getAbsoluteURL(ac.substring(ac.indexOf(':') + 1),
	// urlc.getURL());
	//	
	// }
	// if (i != url.length) {
	// String[] tmp = new String[url.length];
	// System.arraycopy(url, 0, tmp, 0, i);
	// url = tmp;
	// }
	//	
	// }
	// return url;
	// }

	private String[] getMediaURLs(MediaDescriptions msf, com.net.rtsp.RtspURLConnection urlc, String[] url, int[] ports, String[] trsprt ) throws IOException {
		String[] types = msf.getMediaTypes();
		// String url[] = new String[types.length];
		Object[] appCtrl = ((ResponseContent) urlc.getContent()).getContentFields("a");

		if (types.length == 1 && (appCtrl == null || appCtrl.length == 0))
			url[0] = urlc.getURL().toExternalForm();
		else {
			int i = 0;
			String ac;
			for (int j = 0; j < appCtrl.length; j++) {
				ac = (String) appCtrl[j];
				if (ac.startsWith("control:")) {
					String ctrl = ac.substring(ac.indexOf(':') + 1).trim();
					if(ctrl.charAt(0)=='*')
						continue;
					com.net.rtsp.Debug.println("Setup.getMediaURLs() ctrl ="+ctrl);
					url[i] = getAbsoluteURL(ac.substring(ac.indexOf(':') + 1), urlc.getURL());
					ports[i] = msf.getMediaPort(types[i]);
					trsprt[i] = msf.getMediaTransportTytpe(types[i]);
					i++;
				}
			}
			if (i != url.length) {
				String[] tmp = new String[url.length];
				System.arraycopy(url, 0, tmp, 0, i);
				url = tmp;
			}
		}
		return url;
	}

	private MediaDescriptions getmsfg() {
		Object obj = getAppData();
		if (obj instanceof Object[]) {
			Object[] arr = (Object[]) obj;
			for (int i = 0; i < arr.length; i++) {
				if (arr[i] instanceof MediaDescriptions)
					return (MediaDescriptions) arr[i];
			}
		} else if (obj instanceof MediaDescriptions)
			return (MediaDescriptions) obj;
		return null;
	}

	public int doRequest(com.net.rtsp.RtspURLConnection urlc, com.net.rtsp.ClientProcessor client, RequestMessage request, ResponseMessage response, int _Cseq) throws IOException {
	 
			com.net.rtsp.Debug.println("##requestors.doRequest(SETUP_CONTROL)");
		String[] url = null;
		int[] ports = null;
		String[] trsprt;
		MediaDescriptions mscfg = getmsfg();
		if (mscfg == null)
			try {
				mscfg = urlc.getMediaStreamConfig();
			} catch (IOException e) {
				com.net.rtsp.Debug.println("Setup#doRequest:: WARN : cannot get media stream cfg.");
			}

		if (mscfg != null) {// keep all streams
			String[] types = mscfg.getMediaTypes();
			ports = new int[types.length];
			url = new String[types.length];
			 
			trsprt = new String[types.length];
			
			getMediaURLs(mscfg, urlc, url, ports,trsprt );
			 
				com.net.rtsp.Debug.println("Setup.doRequest() ports = " + ports.length);
				com.net.rtsp.Debug.println("Setup.doRequest() urls = " + url.length);
				for (int i = 0; i < ports.length; i++)
					com.net.rtsp.Debug.println("  TYPE =  " + types[i] + " , port = " + ports[i] + ", url=" + url[i]);
			 
		} else {// maybe this server cannot support describe so in this case do
				// setup using the original url.
			url = new String[] { urlc.getURL().toExternalForm() };
			ports = new int[] { 0 };// use default server port cause there no
									// recommended ports & nor user port too.
			trsprt = new String[]{"RTP/AVP"};
		 
			
		}
		String sessionID = urlc.getSessionID();
		for (int i = 0; i < url.length && url[i] != null; i++) {
			if (sessionID == null && i != 0)
				sessionID = response.findValue("Session");
			request.setEmpty();
			response.setEmpty();
			request.setRequestLine(getCMDHeader(SETUP, url[i]));
			request.set("CSeq", "" + _Cseq++);
			request.set("Transport", trsprt[i]+ ";unicast;client_port=" + ports[i] + "-" + (ports[i] + 1));
			if (sessionID != null)// should include session id for next media
				request.set("Session", sessionID);
			setWithAppData(request);
			writeRequest(client, request, response);
		}
		return _Cseq;
	}
}
