package com.net.rtsp.imp.request.client;

import java.util.HashMap;

import com.net.rtsp.ClientRequest;
import com.net.rtsp.Debug;
import com.net.rtsp.RtspURLConnection;

public class RequestFactory extends com.net.rtsp.ClientRequestFactory  {
	
	
	HashMap requests = new HashMap();

	private String resolveClassName(String method){
		method = method.toLowerCase();
		char[] chrs = method.toCharArray();
		char toUpper =(char)('A'-'a');
		chrs[0]+=toUpper;
		int indx=0;
		while( (indx =  method.indexOf('_', indx))>=0)
			chrs[++indx ]+=toUpper;
		String pkg = this.getClass().getName();
		
		int jj=this.getClass().getName().lastIndexOf('.');
		if( jj >0)
			pkg = this.getClass().getName().substring(0, jj+1);
		else pkg = "";
		return pkg+new String(chrs);
	}
	public ClientRequest getRequest(String method,RtspURLConnection con) {
		ClientRequest req = (ClientRequest)requests.get(method);
		if(req == null){
			String cln = resolveClassName(method);
			if(cln != null){
		     try {
				req = (ClientRequest)Class.forName(cln).newInstance();
				requests.put(method, req); 
			} catch ( Exception e) {
				if(Debug.debug_enabled && !(e instanceof java.lang.ClassNotFoundException)) {
					 com.net.rtsp.Debug.println(" class name= "+cln);
				  e.printStackTrace();
				}
			 }
			}
		}
		return req;
	}
}

