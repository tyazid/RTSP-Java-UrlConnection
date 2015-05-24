

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URL;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;
import java.util.Date;

import com.net.rtsp.FieldAttributes;
import com.net.rtsp.ResponseTimedOutException;
import com.net.rtsp.RtspContentHandlerFactory;
import com.net.rtsp.RtspURLConnection;
import com.net.rtsp.RtspURLStreamHandlerFactory;
import com.net.rtsp.ServerRequestMessageEvent;
import com.net.rtsp.ServerRequestMessageListener;

import sun.security.x509.CertAndKeyGen;

public class RTSP_TEST {
	
	static void tstArrays() {
		String[] bg = new String[20*3];
		for (int i =bg.length - 1; i >=0; i--) {
			bg[i] ="S"+i;
		//	com.net.rtsp.Debug.println(bg[i]);
		}
		String val = "S"+10;
		com.net.rtsp.Debug.println("RTSP_TEST.tstArrays() val = "+val);
		long t = System.currentTimeMillis();
		int p = Arrays.binarySearch(bg, val);
		com.net.rtsp.Debug.println("RTSP_TEST.tstArrays() p="+p+"  T0 : "+(System.currentTimeMillis() - t));
		t = System.currentTimeMillis();
		p=ArraysTools.binarySearch(bg, val);
		com.net.rtsp.Debug.println("RTSP_TEST.tstArrays() p="+p+"  T1 : "+(System.currentTimeMillis() - t));
		
		ArraysTools.sort(bg);
		for (int i =bg.length - 1; i >=0; i--) {
			//bg[i] ="S"+i;
		 	com.net.rtsp.Debug.println(bg[i]);
		}
		
		com.net.rtsp.Debug.println("---");
		Arrays.sort(bg);
		for (int i =bg.length - 1; i >=0; i--) {
			//bg[i] ="S"+i;
		 	com.net.rtsp.Debug.println(bg[i]);
		}
		
	}
	
	static void tstCon() throws Throwable{
		InetAddress ia =InetAddress.getByName("192.168.0.13");
		
		com.net.rtsp.Debug.println("ia = "+ia);
 		Socket s = new Socket(ia,5554);
 		com.net.rtsp.Debug.println("S = "+s);
 		//s.isClosed();
		URL url = new URL("http://intranet:80");
		//URLConnection c =  url.openConnection();
		com.net.rtsp.Debug.println("start loop:");
		while(true) {
			Thread.sleep(1000L);
			
			
			com.net.rtsp.Debug.println("con = "+!s.isClosed());
			try{s.getOutputStream().write(0);
			 
			    com.net.rtsp.Debug.println(" Tst = true");}catch(Exception e) {
				com.net.rtsp.Debug.println(" Tst = false--> try new connection ");
				try{
					com.net.rtsp.Debug.println("CLOSE");
					s.close();
				}catch(Throwable t) {}
				s=null;
				while(s==null) {
					try{
						com.net.rtsp.Debug.println("CREATE");
						s = new Socket(ia,5554);
						
					
					}catch(Throwable t) {}
				
				}
			}
		}
	}
	public static String signRSASHA1(String data) throws Exception {
        File keyFile = new File("private.pem");
        byte[] encodedKey = new byte[(int) keyFile.length()];
        new FileInputStream(keyFile).read(encodedKey);
        
        CertAndKeyGen cakg = new CertAndKeyGen("RSA", "MD5WithRSA");
        cakg.generate(1024);
        
        PublicKey publicKey = cakg.getPublicKey();
        com.net.rtsp.Debug.println(publicKey);
        System.out.println();
        PrivateKey privateKey = cakg.getPrivateKey();
        com.net.rtsp.Debug.println(privateKey);
 
        
        
       // KeyFactory fac = KeyFactory.getInstance("RSA");
     //   EncodedKeySpec spec = new PKCS8EncodedKeySpec(encodedKey);
    //    PrivateKey privateKey = fac.generatePrivate(spec);
 
        Signature signer = Signature.getInstance("SHA1withRSA");
        signer.initSign(privateKey);
        signer.update(data.getBytes("UTF-8"));
        byte[] signatureBytes = signer.sign();
       // signatureBytes = Base64.encodeBase64(signatureBytes);
        return new String(signatureBytes, "UTF-8");
    }
	static Object[] cert2(String hash) throws Throwable{
		MessageDigest md = MessageDigest.getInstance("SHA");			 
		md.update(hash.getBytes());
		byte[] userSeed = md.digest();// HASH !
		
		
		KeyPairGenerator kpg = KeyPairGenerator.getInstance("DSA");			
		SecureRandom random = SecureRandom.getInstance("SHA1PRNG"/*, "SUN"*/);
		random.setSeed(userSeed);//any private user seed can be used here.
		kpg.initialize(1024, random);			
		
		Signature dsa = Signature.getInstance("SHA1withDSA"); 
		KeyPair pair = kpg.generateKeyPair();
		PrivateKey priv = pair.getPrivate();
		dsa.initSign(priv);
		/* Update and sign the data */
		dsa.update(userSeed);
		byte[] sig = dsa.sign();
		
		String h;
		hash = "";
		for (int i = 0; i < sig.length; i++) {
			h = Integer.toHexString(sig[i] & 0xff);
			if (h.length() != 2)
				h = '0' + h;
			hash += h;
		}
		com.net.rtsp.Debug.println("RTSP_TEST.cert2() sign ="+hash);
		return new Object[] {sig,pair.getPublic() };
	}
	
	static void checkSing(byte[] sign, PublicKey pkey) throws Throwable{
		X509EncodedKeySpec pubKeySpec = new X509EncodedKeySpec(pkey.getEncoded());

	    KeyFactory keyFactory = KeyFactory.getInstance("DSA");
	    PublicKey pubKey = keyFactory.generatePublic(pubKeySpec);

	    Signature sig = Signature.getInstance("SHA1withDSA");
	    sig.initVerify(pubKey);
	   // sig.update(sign);
	    boolean check = sig.verify(sign);
	    com.net.rtsp.Debug.println("RTSP_TEST.checkSing() c = "+check);
		
	}
	
	
	static final String CONTROLE_PREFIX = "control:";
	static final String RANGE_PREFIX="range:";
	static final String NPT_RANGE_PREFIX="npt=";
	
static void add() {
//	 try {
//	 String[] resps = {"range:npt=0-108.468"};
//		String si;
//		for (int i = 0; i < resps.length; i++) {
//			if( ( si=(String)resps[i]).startsWith(RANGE_PREFIX)) {
//				String range = si.substring(RANGE_PREFIX.length());
//				if(range.startsWith(NPT_RANGE_PREFIX)) {//npt syntax
//					range=range.substring(NPT_RANGE_PREFIX.length());
//					com.net.rtsp.Debug.println("RTSP_TEST.main() range = "+range);
//					int idx=range.indexOf('-');
//					if(idx != -1) {
//					String start = range.substring(0,idx).trim();
//					String end= range.substring(idx+1).trim();
//					com.net.rtsp.Debug.println(start +" ; "+end);
//					com.net.rtsp.Debug.println((int)(Double.parseDouble(end)-Double.parseDouble(start)));
//					}
//					
//				}
//				
//				 //parsing the time range 
//				//in seconf plz.
//			}
//		}
//	 
//	 
//	 
//	Object[] os = cert2("TOTO AND TITI");
//	 //com.net.rtsp.Debug.println("RTSP_TEST.main() s = "+os[0]+", "+os[1]);
//	 checkSing((byte[]) os[0] , (PublicKey)os[1]);
//	 
//	 
//if(true)return;
//	 com.net.rtsp.Debug.println();
//	 SimpleDateFormat sdv = new  SimpleDateFormat("yyyyMMddHHMMSS");
//	 Calendar c1 = Calendar.getInstance(); // today
//	    com.net.rtsp.Debug.println("Today is " + sdv.format(c1.getTime()));
//     com.net.rtsp.Debug.println(InetAddress.getLocalHost().getHostName());
//     com.net.rtsp.Debug.println(InetAddress.getLocalHost().getHostAddress());
//     
//   } catch (UnknownHostException e) {
//     e.printStackTrace();
//   }

//URL url0 = new URL("rtsp://H:20/;purchaseToken=<asset-id>[;serverId=<server-id>]");
//com.net.rtsp.Debug.println("RTSP_TEST.main() f ="+url0.getFile());
//
//String server_id = url0.getFile();
//	String k ="purchaseToken=";
//	
//	StringTokenizer st = new StringTokenizer(server_id,";");
//	if(st.countTokens()>0) {
//		String s;
//		do {
//			
//			  s = st.hasMoreElements() ? st.nextToken().trim() : null;
//		} while (s!=null &&  !s.startsWith(k) );
//		 if(s != null) {
//			 s = s.substring(k.length() ).trim();
//			  com.net.rtsp.Debug.println(s);
//		 }
//	}
//InetAddress[] ias = InetAddress.getAllByName("localhost");
//for (int i = 0; i < ias.length; i++) {
//	 com.net.rtsp.Debug.println("RTSP_TEST.tstCon() "+ias[i].getHostAddress());
//}
//
//cert() ;
// Compiler.disable();
////tstArrays();
////  tstCon();
// if(true) return;
	
	//  new RtspURLStreamHandlerFactory());
//	URL httpUrl = new URL("rtsp://sessionmanager2.comcast.com:554/;purchaseToken=c0c2d8b0-cc82-11d9-8cd50-800200c9a66;serverID=1.1.1.1 RTSP/1.0");
//	com.net.rtsp.Debug.println("RTSP_TEST.main() url = "+httpUrl.getHost()+" : "+httpUrl.getPort());

}	
static String urlStr;
static FileOutputStream fout;
static void setArgs(String[] args) {
	 if(args.length == 0 ) {
		 com.net.rtsp.Debug.println("Args usage : <RTSP_URL> [<LOG_FILE_PATH>] ");
		 System.exit(0);
	 }
	 com.net.rtsp.Debug.println(" RTSP CLIENT TST : ARGS :");
	 for (int i = 0; i < args.length; i++) {
		com.net.rtsp.Debug.println(args[i]);
	}
	 urlStr = args[0];
	 
	 File flog =null;// args.length>1?new File(args[1]):null;
	 
	 
	 try {
		  // fout = new FileOutputStream(flog);
		PrintStream out =flog!=null? 	new PrintStream(flog) { } :  System.out;
		com.net.rtsp.Debug.println("RTSP_TEST.setArgs() out = "+System.out +" / "+out);
		System.setOut(out);
		System.setErr(out);
	} catch (FileNotFoundException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	// System.setOut(out)
	 
	 
	 
}



  
	
 public static void main(String[] args) throws Throwable {
	  setArgs(args);
	
	// URL.setURLStreamHandlerFactory( new S1C1URLStreamHandlerFactory());
	  java.net.URL.setURLStreamHandlerFactory( new RtspURLStreamHandlerFactory());

 
	 //	if(true)return;
		 java.net.URL url = new URL(urlStr);// RTSP/1.0");//;//;;;
		//java.net.URL url = new URL("rtsp://172.21.104.62:554/Dynosaure.mpg");
		//rtsp://172.21.104.62:554/Dynosaure.mpg
		com.net.rtsp.Debug.println(new Date()+" connecting....");
		final RtspURLConnection urlc = (RtspURLConnection) url.openConnection();
		urlc.connect();
		urlc.addServerRequestMessageListener(new ServerRequestMessageListener() {

			public void requestMessageReceived(ServerRequestMessageEvent event) {
				
				com.net.rtsp.Debug.println(new Date()+" EVENT FROM SRV : "+event.getMessage());
				
				urlc.removeServerRequestMessageListener(this);
				
				
			}
			
		});
		com.net.rtsp.Debug.println(""+new Date()+" URL CON = " + urlc);
		
		try {
			System.err.println(""+new Date()+" STATUS LINE ="+urlc.getStatus()+" C="+urlc.getResponseCode());
			System.err.println(""+new Date()+" connected. urlc.CT=" + urlc.getContentType() + " C.L=" + urlc.getContentLength()+" call setup");
			FieldAttributes attrib = new FieldAttributes();
		
			urlc.setup(null, null);//1234);
			System.err.println(""+new Date()+" TRANSPORT ="+urlc.getHeaderField( "Transport"));
			
			System.err.println(""+new Date()+" CNT ="+urlc.getContent());
		 
			
			System.err.println("\n\n"+new Date()+" Wait for4 sec");
			Thread.sleep(4000); 
			System.err.println("\n\n"+new Date()+" SEND PLAY");
			urlc.play( );
			com.net.rtsp.Debug.println(""+new Date()+"  PLAY RESPONSE : "+urlc.getResponseCode());
			 
			System.err.println("\n\n["+new Date()+"] SEND PAUSE");
			boolean ioerr=false;
			try {
				urlc.pause();
				com.net.rtsp.Debug.println(new Date()+" PAUSE RESPONSE : "+urlc.getResponseCode());
			} catch ( Exception e) {
				System.err.println("["+new Date()+"]  PAUSE ERROR...");
				e.printStackTrace();
				ioerr=e instanceof IOException;
			}
			System.err.println("\n\n["+new Date()+"] Wait for 4 sec");
			Thread.sleep(4000);
			System.err.println("\n\n["+new Date()+"] SEND PLAY TOO");
			if(!ioerr)
			{
		 	urlc.play( );
			com.net.rtsp.Debug.println(" ["+new Date()+"] PLAY RESPONSE : "+urlc.getResponseCode());
		    }
			System.err.println("\n\n["+new Date()+"] Wait for 10 MIN c");
			 
			for (int i = 0; i < 4; i++) {
				 System.out.print(".");
				 Thread.sleep(1000*5);
			}
			
			//System.err.println("OPTIONS");
			//urlc.options();
			
			java.net.ContentHandler handler = RtspContentHandlerFactory.getInstance().createContentHandler("text/parameters");
			com.net.rtsp.Debug.println("["+new Date()+"]  Content Handler = "+handler);
			if(handler instanceof com.net.rtsp.content.text.parameters.ContentHandler) {
				com.net.rtsp.content.text.parameters.RequestContent txtRespCnt =(com.net.rtsp.content.text.parameters.RequestContent)((com.net.rtsp.content.text.parameters.ContentHandler)handler).getRequestContent();
				txtRespCnt.getContentAttributes().addField("Speed", null);
				System.err.println("\n\n["+new Date()+"] SEND GET_PARAMETER");
			 
				urlc.getParameter(txtRespCnt)  ;
				com.net.rtsp.Debug.println(" ["+new Date()+"] GET_PARAMETERS RESPONSE : "+urlc.getResponseCode());
			}
 

			System.err.println("------------------------------------------");
			Thread.sleep(1000);
			
			com.net.rtsp.Debug.println(" WAIT FOR EVENT FROM SRV.");
			Thread.sleep(10000);
			com.net.rtsp.Debug.println("["+new Date()+"]    teardown");
				urlc.teardown();
			
		} catch (Exception e) {
			if(!(e instanceof ResponseTimedOutException))
		        e.printStackTrace();
			else System.out.println( " Serever connection time out; connection seems closed without response after teardown cmd.");
		}
		
		System.out.flush();
		if(fout!=null)
			fout.close();
		
	}

}
