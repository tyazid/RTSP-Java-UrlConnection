

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URL;
import java.util.Date;

import com.net.rtsp.FieldAttributes;
import com.net.rtsp.ResponseTimedOutException;
import com.net.rtsp.RtspContentHandlerFactory;
import com.net.rtsp.RtspURLConnection;
import com.net.rtsp.RtspURLStreamHandlerFactory;
import com.net.rtsp.ServerRequestMessageEvent;
import com.net.rtsp.ServerRequestMessageListener;

public class RTSP_TEST {
	

	
private static String urlStr;
private static FileOutputStream fout;
private static void setArgs(String[] args) {
	 if(args.length == 0 ) {
		 com.net.rtsp.Debug.println("Args usage : <RTSP_URL> [<LOG_FILE_PATH>] ");
		 System.exit(0);
	 }
	 com.net.rtsp.Debug.println(" RTSP CLIENT TST : ARGS :");
	 for (int i = 0; i < args.length; i++) {
		com.net.rtsp.Debug.println(args[i]);
	}
	 urlStr = args[0];
	 
	 File flog =  args.length>1?new File(args[1]):null;
	 
	 
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
