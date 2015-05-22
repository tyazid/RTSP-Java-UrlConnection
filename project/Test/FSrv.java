import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

public class FSrv {
	static String adress = null;
	static {
		try {
			adress = InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} 
	}
	
	
	int port;
	public FSrv(int port) {
		this.port = port;
	}
	static String setup_sdp = ("v=0\r\n"
			+ "o=- 777 2890842817 IN IP4 1.2.3.4\r\ns=\r\nt=0 0\r\na=control:rtsp://"+adress+":5552/zzDaredevil\r\nc=IN IP4 0.0.0.0\r\nm=video 0 udp MP2T"
			);
	
	static String[] methods = { "OPTIONS","DESCRIBE" ,"SETUP", "PLAY", "PAUSE", "TEARDOWN", "PING" };
	static String[] responses = {
			// OPTIONS
			"RTSP/1.0 200 Ok\r\nCseq: 14\r\nServer: VLC Server\r\nPublic: OPTIONS, DESCRIBE,SETUP, TEARDOWN, PLAY, PAUSE, PING\r\nContent-Length: 0\r\n\r\n",
			//DESCRIBE
			"RTSP/1.0 200 OK\r\nContent-type: application/sdp\r\nServer: VLC Server\r\nContent-Length: 326\r\nCseq: 4\r\nCache-Control: no-cache"+
			"\r\n\r\nv=0\r\n"+
"o=- 275411406000 3 IN IP4 127.0.0.1\r\n"+
"t=0 0\r\n"+
"a=tool:vlc 0.8.4-fbx-1\r\n"+
"c=IN IP4 0.0.0.0/0\r\n"+
"a=range:npt=0-108.468\r\n"+
"m=video 0 RTP/AVP 32\r\n"+
"a=rtpmap:32 MPV/90000\r\n"+
"a=control:rtsp://"+adress+":5554/zzDaredevil/trackid=0\r\n"+
"m=audio 0 RTP/AVP 14\r\n"+
"a=rtpmap:14 MPA/90000\r\n"+
"a=control:rtsp://"+adress+":5554/zzDaredevil/trackid=1\r\n",
			
			// SETUP
			"RTSP/1.0 200 OK\r\nCSeq: 123\r\nSession: 716195834\r\nTransport: MP2T/DVBC/QAM;unicast;\r\ndestination=24000000.23\r\nOnDemandSessionId: be074250-cc5a-11d9-8cd5-0800200c9a66\r\nClientSessionId: 00AF123456DE00000001\r\nEMMData: 40203F21A5\r\nContent-type: application/sdp\r\nContent-length: "+setup_sdp.getBytes().length+"\r\n\r\n"+
			setup_sdp,			
			// PLAY
			"RTSP/1.0 200 OK\r\nCSeq: 456\r\nSession: 77\r\nRange: npt=0-\r\n\r\n",
			// PAUSE
			"RTSP/1.0 200 OK\r\nCSeq: 836\r\nSession: 77\r\nRange: npt=1742-\r\n\r\n",

			/* TEARDOWN */
			"RTSP/1.0 200 OK\r\nCSeq: 789\r\nSession: 98765\r\nOnDemandSessionId: be074250-cc5a-11d9-8cd5-0800200c9a66\r\nClientSessionId: 00AF123456DE00000001\r\n\r\n",
			// PING
			"RTSP/1.0 200 OK\r\nCSeq: 123\r\nSession:12345678\r\nOnDemandSessionId: be074250-cc5a-11d9-8cd5-0800200c9a66\r\n\r\n"
	};
	static byte[] not_fnd = "RTSP/1.0 404 Not found\r\nContent-Length: 0\r\n\r\n".getBytes();
	
	static String ANNOUCE="ANNOUNCE rtsp://sessionmanager2.comcast.com:554 RTSP/1.0\r\nCSeq: 3\r\nRequire: com.comcast.ngod.s1\r\nSession: 94155497\r\nNotice: 5402 \"Client Session Terminated\" event-date=19930310T023735.013Z npt=342554\r\nOnDemandSessionId: be074250-cc5a-11d9-8cd5-0800200c9a66\r\n\r\n"; 
 

	  String checkMethod(Socket socket) throws IOException {
		InputStream in = socket.getInputStream();
		InputStreamReader ir = new InputStreamReader(in);
		BufferedReader reader = new BufferedReader(ir);
		String l = null;

		String l0 = null;
		System.err.println(" CLIENT REQ: ");
		while ((l = reader.readLine()) != null) {
			 com.net.rtsp.Debug.println("SEVER.PORT:"+port + "#");
			if (l0 == null)
				l0 = l;
			l = l.trim();
			if (l.length() == 0)
				break;
			
		}
		System.err.println("------\n\n");
		return l0;
	}
	
	  byte[] getResponse(String cmdLine) {
		for (int i = 0; i < methods.length; i++) {
			if(cmdLine.indexOf(methods[i]) != -1) {
				System.err.println("FSrv.getResponse() --> DO RESPONSE FOR "+methods[i]);
				com.net.rtsp.Debug.println("SEVER.PORT:"+port+" SEND :"+responses[i]);
				return responses[i].getBytes();
			}
		}
		System.err.println("RET 404");
		return not_fnd;
	}
	
	void tst() throws Exception {
		System.err.println("FSrv.tst()");
		ServerSocket serv = new ServerSocket(port);
		
		while (true) {
			com.net.rtsp.Debug.println("SEVER.PORT:"+port+"waiting for client on port = "+port);
			final Socket s = serv.accept();
			if(port == 5552) {
				//ANNOUCE
				new Thread() {
					public void run() {
					  System.err.println("**SEND ANNOUCE IN 20 sec");
					 
					  try { sleep(8000);
						s.getOutputStream().write(ANNOUCE.getBytes());
						 System.err.println("**SEND ANNOUCE SENT");
					} catch ( Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					}
					
				}.start();
			}
			com.net.rtsp.Debug.println("SEVER.PORT:"+port+"FSrv.main() connection from "
					+ s.getInetAddress());
			
			String l = null;
			while((l=checkMethod(s))!=null) {
			com.net.rtsp.Debug.println("SEVER.PORT:"+port+"CMD L = "+l);
			byte[] res = getResponse(l);
			 
		s.getOutputStream().write(res);
		   }
			//s.close();
			// serv.close();
			// break;
		}

	
	}

	/**
	 * @param args
	 * @throws Throwable
	 */
	public static void main(String[] args) throws Throwable {
		
		new Thread() {
			public void run() {
			try {
				new FSrv(5552).tst();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}}.start();
		new FSrv(5553).tst();
	}

}
