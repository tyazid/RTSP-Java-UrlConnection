package com.net.rtsp;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.InterruptedIOException;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.SocketPermission;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.net.UnknownServiceException;
import java.security.AccessController;
import java.security.Permission;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.EventListener;
import java.util.EventObject;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.StringTokenizer;
import java.util.Vector;

import com.net.rtsp.content.Content;
import com.net.rtsp.content.ResponseContent;
import com.net.rtsp.util.Dispatcher;
import com.net.rtsp.util.EventQueue;
import com.net.rtsp.util.XEventQueue;

/**
 * A URLConnection with support for RTSP-specific features. See <A HREF="http://www.ietf.org/rfc/rfc2326.txt?number=2326"> the RFC </A> for
 * details.
 * <br><i>General scheme illustrating this RTSP Protocol handling implementation based on Java net framework.</i>
 * <br><img src="rtsp.jpg"/>
 * 
 */
public class RtspURLConnection extends URLConnection implements Protocol, RtspMethods {
	/**
	 * Client init state - connection and describe done -.
	 */
	public static final int INIT_STATE = 0x1;

	/**
	 * Client in ready state - setup done from init state - or -pause done from
	 * playing state -.
	 */
	public static final int READY_STATE = 0x2 | INIT_STATE;

	/**
	 * Client in paying state - play done from ready state -
	 */
	public static final int PLAYING_STATE = 0x4 | READY_STATE;
	
	public static final int DOWN_STATE = 0x8;
	
	
	
	protected boolean describNeeded;

	public RtspURLConnection(URL url) {
		super(url);
		staticInit();
		computeHostAndPort();
		_Cseq = 1;
			AccessController.doPrivileged(new PrivilegedAction() {
				public Object run() {
					eventq = new XEventQueue(new Dispatcher() {
						public void dispatch(EventListener[] listener, EventObject event) {
							for (int i = 0; i < listener.length; i++)
								try {
									((ServerRequestMessageListener) listener[i]).RequestMessageReceived((ServerRequestMessageEvent) event);
								} catch (Exception e) {
									e.printStackTrace();
								}
						}
					});
					return null;
				}
			});

	}
	
	protected RtspURLConnection(boolean describeMandatory,URL url) {
		super(url);
		staticInit();
		computeHostAndPort();
		
			AccessController.doPrivileged(new PrivilegedAction() {
				public Object run() {
					eventq = new XEventQueue(new Dispatcher() {
						public void dispatch(EventListener[] listener, EventObject event) {
							for (int i = 0; i < listener.length; i++)
								try {
									((ServerRequestMessageListener) listener[i]).RequestMessageReceived((ServerRequestMessageEvent) event);
								} catch (Exception e) {
									e.printStackTrace();
								}
						}
					});
					return null;
				}
			});

	}

	/**
	 * Adds the specified server request message listener to receive server
	 * request events from this Rtsp URL connection.
	 * 
	 * @param l
	 *            the server request message listener.
	 * @see ServerRequestMessageListener
	 * @see ServerRequestMessageEvent
	 * @see #removeServerRequestMessageListener(ServerRequestMessageListener)
	 */
	public void addServerRequestMessageListener(ServerRequestMessageListener l) {
		Object[] objs = eventq.getListeners();
		for (int i = objs.length - 1; i >= 0; i--) {
			if (objs[i] == l)
				return;
		}

		eventq.addListener(l);
	}

	/**
	 * Removes the specified server request message listener so that it no
	 * longer receives server request events from this Rtsp URL connection.
	 * 
	 * @param l
	 *            the server request message listener.
	 * @see ServerRequestMessageListener
	 * @see ServerRequestMessageEvent
	 * @see #addServerRequestMessageListener(ServerRequestMessageListener)
	 */
	public void removeServerRequestMessageListener(ServerRequestMessageListener l) {
		eventq.removeListener(l);
	}
	
	public void setAutoReconnectEnabled(boolean enabled){
		autoReconnec = enabled;
	}
	
	public boolean isAutoReconnectEnabled() {
		return autoReconnec;
	}
	
	  /**
     * Sets a specified timeout value, in milliseconds, to be used
     * when opening a communications link to the resource referenced
     * by this URLConnection.  If the timeout expires before the
     * connection can be established, a
     * java.net.SocketTimeoutException is raised. A timeout of zero is
     * interpreted as an infinite timeout.

     * <p> Some non-standard implmentation of this method may ignore
     * the specified timeout. To see the connect timeout set, please
     * call getConnectTimeout().
     *
     * @param timeout an <code>int</code> that specifies the connect
     *               timeout value in milliseconds
     * @throws IllegalArgumentException if the timeout parameter is negative
     *
     * @see #getConnectTimeout()
     * @see #connect()
    
     */
    public void setConnectTimeout(int timeout) {
	if (timeout < 0) {
	    throw new IllegalArgumentException("timeout can not be negative");
	}
	connectTimeout = timeout;
    }

    /**
     * Returns setting for connect timeout.
     * <p>
     * 0 return implies that the option is disabled
     * (i.e., timeout of infinity).
     *
     * @return an <code>int</code> that indicates the connect timeout
     *         value in milliseconds
     * @see #setConnectTimeout(int)
     * @see #connect()
     */
    public int getConnectTimeout() {
	return connectTimeout;
    }

	/**
	 * Determines whether the last response message status is successful or not
	 * 
	 * @return <code>true</code> if the status is successful is visible;
	 *         <code>false</code> otherwise.
	 */
	public boolean isSuccessfullResponse() {
		int r = getResponseCode();
		//Debug.println("RtspURLConnection.isSuccessfullResponse() r = " + r);
		r = (r / 100) * 100;
		return r == ResponseCodes.RTSP_OK;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.net.URLConnection#getInputStream()
	 */
	public InputStream getInputStream() throws IOException {
		if (!connected)
			throw new IOException("not connected");
		return connection.getInputStream();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.net.URLConnection#getOutputStream()
	 */
	public OutputStream getOutputStream() throws IOException {
		if (!connected)
			throw new IOException("not connected");
		return connection.getOutputStream();
	}

	/**
	 * Sets the request time out (ie: max waiting for server response), 0 value
	 * means that the client waits until it receive a response from the server.
	 * 
	 * @param timeout in ms
	 */
	public void setRequestTimeout(int timeout) {
		this.requesTimout = timeout;
		 
	}

	/**
	 * Return the request time out
	 * 
	 * @return the value of time out
	 */

	public int getRequestTimeout() {
		return requesTimout < 0 ? REQ_TIME_OUT : requesTimout;
	}

	public int getCseq() {
		return _Cseq;
	}

	/**
	 * Performs the RTSP OPTIONS request
	 * 
	 * @throws IOException
	 *             if an IO error occures
	 */
	public void options() throws IOException {
		options(null);
	}

	public void options(FieldAttributes attributes) throws IOException {
		// no check for options...
	//	com.net.rtsp.Debug.println("RtspURLConnection.options()");
		ClientProcessor client = getClient();
		if (client != null) {
			ClientRequest r = getRequestFactory().getRequest(OPTIONS, this);
			com.net.rtsp.Debug.println("RtspURLConnection.options() response = " + responses);
			r.setAppData(attributes);
			setCseq(r.doRequest(this, getClient(), requests, responses, getCseq()));
		} else
			throw new IOException("not connected");
	}

	/**
	 * Performs the RTSP SETUP request
	 * 
	 * @throws IOException
	 *             if an IO error occurs
	 */
	public void setup() throws IOException {
		setup(null);
	}

	/**
	 * Performs the RTSP SETUP request with extended field attributes. This
	 * method call {@link #setup(MediaDescriptions, FieldAttributes)} with null
	 * MediaDescriptions parameter
	 * 
	 * @throws IOException
	 *             if an IO error occurs
	 */
	public void setup(FieldAttributes attributes) throws IOException {
		setup(null, attributes);

	}

	/**
	 * Performs the RTSP SETUP request using a specific media description which
	 * will be used as media stream urls source. The field attributes are used
	 * to endow the message header with extra header field.
	 * 
	 * @throws IOException
	 *             if an IO error occurs
	 */
	public void setup(MediaDescriptions mediaCfg, FieldAttributes attributes) throws IOException {
		// 2 mode setup default == use decribe result to setup all media or
		// setup
		// using setup config [] url/port!
		Debug.println("RtspURLConnection.setup() desc == null?" + (descContent == null));
		if (describNeeded&&descContent == null && (mediaCfg == null || getSessionID() == null)) // setup
			// needs
			// describe
			// content
			// or a
			// media
			// stream
			// cfg
			// (in
			// this
			// case
			// session
			// id
			// should
			// be
			// valide)
			throw new UnknownServiceException(" No describe method was made to perform setup method.");
		//long t0=System.currentTimeMillis();
		doCmd(SETUP, new Object[] { mediaCfg, attributes });
		//System.err.println(" SETUP DO CMD = "+(System.currentTimeMillis() - t0));
	//	System.err.println(" SETUP IS OK = "+isSuccessfullResponse());
		Debug.println("RtspURLConnection.setup() success = " + isSuccessfullResponse());
		if (isSuccessfullResponse()) {
		//	t0=System.currentTimeMillis();
			String sid = responses.findValue("Session");
		//	System.err.println(" FND VALUE= "+(System.currentTimeMillis() - t0)+" SID="+sid);
			com.net.rtsp.Debug.println("RtspURLConnection.setup() sid = " + sid);
		//	t0=System.currentTimeMillis();
			if (sid != null) {
				int i = sid.indexOf(';');
				if (i > 0) {
					String s = sid;
					setSessionID(s.substring(0, i));
					try {
						setSessionTimeout(Integer.parseInt(s.substring(i + 1)));
					} catch (NumberFormatException e) {
						Debug.println("RtspURLConnection.setup() WARNING : time value syntax err : " + s);
					}
				} else
					setSessionID(sid);
				Debug.println("---> SESSION = " + sid);
				state = READY_STATE;
			//	System.err.println(" REST "+(System.currentTimeMillis() - t0));
				return;
			}
		}
		throw new IOException("cannot perform setup: Resource not found");
	}

	/**
	 * Performs the RTSP PLAY request. This method call
	 * {@link #play(FieldAttributes)} with null field attributes
	 * 
	 * @throws IOException
	 *             if an IO error occurs
	 */
	public void play() throws IOException {
		play(null);
	}

	/**
	 * Performs the RTSP PLAY request using a specific field attributes that are
	 * used to endow the message header with extra header field.
	 * 
	 * @param attributes
	 *            FieldAttributes object.
	 * @throws IOException
	 *             if an IO error occurs
	 */
	public void play(FieldAttributes attributes) throws IOException {
		if (getSessionID() == null)
			throw new IOException("cannot perform play without setup.");
		doCmd(PLAY, attributes);
		if (!isSuccessfullResponse())
			throw new IOException("cannot perform play.");
		state = PLAYING_STATE;

	}

	/**
	 * Performs the RTSP PAUSE request. This method call
	 * {@link #pause(FieldAttributes)} with null field attributes
	 * 
	 * @throws IOException
	 *             if an IO error occurs
	 */
	public void pause() throws IOException {
		pause(null);
	}

	/**
	 * Performs the RTSP PAUSE request using a specific field attributes that
	 * are used to endow the message header with extra header field.
	 * 
	 * @param attributes
	 *            FieldAttributes object.
	 * @throws IOException
	 *             if an IO error occurs
	 */
	public void pause(FieldAttributes attributes) throws IOException {
		if (getSessionID() == null)
			throw new IOException("cannot perform pause without setup.");
		doCmd(PAUSE, attributes);
		state = READY_STATE;
	}

	/**
	 * Performs the RTSP TEARDOWN request, it calls
	 * {@link #teardown(FieldAttributes)} with null field attributes
	 * 
	 * @throws IOException
	 *             if an IO error occurs
	 */
	public void teardown() throws IOException {
		teardown(null);
	}

	/**
	 * Performs the RTSP TEARDOWN request using a specific field attributes that
	 * are used to endow the message header with extra header field.
	 * 
	 * @param attributes
	 *            FieldAttributes object.
	 * @throws IOException
	 *             if an IO error occurs
	 */
	public void teardown(FieldAttributes attributes) throws IOException {
		synchronized (this) {
			if (getSessionID() == null)
				throw new IOException("cannot perform teardown without setup.");
			try {
				doCmd(TEARDOWN, attributes);
				state = DOWN_STATE;
			} finally {
				cleanup();
			}
		}
	}

	/**
	 * Performs the RTSP SET_PARAMETER request using a specific body content
	 * which contains the set of (key,value) parameters to set.
	 * 
	 * @param content
	 * 
	 * @throws IOException
	 *             if an IO error occurs
	 */
	public void setParameter(Object content) throws IOException {// C->S parm
		setParameter(content, null);

	}

	/**
	 * Performs the RTSP SET_PARAMETER request using a specific field attributes that
	 * are used to endow the message header with extra header field.
	 * 
	 * @param content
	 * @param attributes
	 *            FieldAttributes object.
	 * 
	 * @throws IOException
	 *             if an IO error occurs
	 */
	public void setParameter(Object content, FieldAttributes attributes) throws IOException {
		doCmd(SET_PARAMETER, new Object[] { content, attributes });
	}

	/**
	 * Performs the RTSP GET_PARAMETER request using a specific {@link Content}
	 * instance, this content body may contains the parameters keys of values
	 * that we will read from the server.
	 * 
	 * @param content
	 *            content parameters
	 * @throws IOException
	 *             if an IO error occurs
	 */
	public void getParameter(Object content) throws IOException {
		getParameter(content, null);
	}

	/**
	 * Performs the RTSP GET_PARAMETER request, it calls
	 * {@link #getParameter(Object)} with null content object.
	 * 
	 * @throws IOException
	 *             if an IO error occurs
	 */
	public void getParameter() throws IOException {
		getParameter(null);
	}

	/**
	 * Performs the RTSP GET_PARAMETER request using a specific {@link Content}
	 * instance, this content body may contains the parameters keys of values
	 * that we will read from the server. Attributes are used to endow the
	 * message header with extra header field.
	 * 
	 * @param attributes
	 *            FieldAttributes object.
	 * @param content
	 *            content parameters
	 * @throws IOException
	 *             if an IO error occurs
	 */
	public void getParameter(Object content, FieldAttributes attributes) throws IOException {
	//	Thread.dumpStack();
		doCmd(GET_PARAMETER, new Object[] { attributes, content });
	}

	/* CPECIFIC URL CONNECTION */

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.net.URLConnection#getContent()
	 */
	public Object getContent() throws IOException {
		return descContent == null ? (descContent = (ResponseContent) super.getContent()) : descContent;
	}

	/**
	 * The optional timeout specifies the heartbeat interval in seconds. When a
	 * heart beat is missed, the server may decide to tear down the session.
	 * Heart beats should be sent at 80% of this interval, e.g. via a ping call
	 * 
	 * @return the session timeout (number of ms)
	 */
	public long getSessionTimeout() {
		return sessionTimeout;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.net.URLConnection#connect()
	 */
	public synchronized void connect() throws IOException {
		//com.net.rtsp.Debug.println("RtspURLConnection.connect("+this+") connected = "+connected);
		if (connected)
			return;
		Vector v = Debug.debug_enabled ? new Vector() : null;
		for (int i = 0; i < port.length; i++) {
			try {
				connection = new Connection(host, port[i]);
				break;
			} catch (Exception e) {
				
				if (Debug.debug_enabled)
					v.addElement(e);
			}
		}
		if (connection == null) {
			if (Debug.debug_enabled) {
				
				Object[] errs = new Object[v.size()];
				v.copyInto(errs);
				//(IOException[]) v.toArray(new IOException[v.size()]);
				for (int i = 0; i < errs.length; i++) {
					com.net.rtsp.Debug.println(" ERR WHEN TRYING TO CONNECT USING PORT " + port[i]);
					((Exception)errs[i]).printStackTrace();
				}
			}
			throw new ConnectException("cannot create a connection.");
		}

		ClientProcessor client = getClient();
		if (client != null)
			connection.addDataProcessor(client);
		ServerProcessor server = getServer();
		if (server != null)
			connection.addDataProcessor(getServer());
		connected = true;
		//com.net.rtsp.Debug.println(">RtspURLConnection.connect()");
		
		setSupportedMethods();
	//	com.net.rtsp.Debug.println("RtspURLConnection.connect() describe supported =" +isSupportedMethod(DESCRIBE));
		describNeeded =  isSupportedMethod(DESCRIBE);
		if (describNeeded) {
			describe(getDescribeAttributes());
			Object cnt = getContent();
			if (!(cnt instanceof ResponseContent) || !((ResponseContent) cnt).getContentType().equalsIgnoreCase("application/sdp")) {
				Thread.dumpStack();
				com.net.rtsp.Debug.println("###### RtspURLConnection.connect() ERR CASE TYPE OF CNT = " + ((ResponseContent) cnt).getContentType());
				cleanup();
			}
			descContent = (ResponseContent) cnt;
			Debug.println("##### RtspURLConnection.connect() DESCRIBE RES = " + descContent);
		}
		state = INIT_STATE;

	}

	/**
	 * Returns the client state<br>
	 * <b>Note:</b> <br>
	 * transitions: <br>
	 * Init -> SETUP --> Ready <br> -> TEARDOWN --> Init <br>
	 * Ready -> PLAY --> Playing <br> -> TEARDOWN --> Init <br> -> SETUP -->
	 * Ready <br> -> PAUSE --> Ready <br> -> TEARDOWN --> Init <br> -> PLAY -->
	 * Playing <br> -> SETUP --> Playing (changed transport)
	 * 
	 * @return int value of the client state
	 * 
	 */
	public int getState() {
		return state;
	}

	/**
	 * Return the RTSP/1.0 response code  of the last request.
	 * 
	 * @return the response code
	 * @throws IOException
	 */
	public int getResponseCode() {
		return responses.getStatusCode();
	}

	/**
	 * Re turn the status string of the last request.
	 * 
	 * @return status
	 * @throws IOException
	 */
	public String getStatus() throws IOException {
		return responses.getStatus();
	}

	/**
	 * return the current RTSP session id. The no setup was performed the null
	 * value is returned.
	 * 
	 * @return session id
	 */
	public String getSessionID() {
		return sessionID;
	}

	/**
	 * This Method set the describe extra attributes that are used to endow the
	 * message header with extra header field.
	 * <p>
	 * <b>Note:</b>This method should be called before {@link #connect()}
	 * method since those attributes are used by the describe rtsp method during
	 * the connection to the server.
	 * 
	 * @param attribute
	 *            FieldAttributes object.
	 */

	public void setDescribeAttributes(FieldAttributes attribute) {
		try {
			this.dattribute = (FieldAttributes) attribute.clone();
		} catch (CloneNotSupportedException e) {
			this.dattribute = attribute;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.net.URLConnection#getPermission()
	 */
	public Permission getPermission() throws IOException {
		String host = url.getHost() + ":" + port;
		Permission permission = new SocketPermission(host, "connect");
		return permission;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.net.URLConnection#getHeaderField(java.lang.String)
	 */
	public String getHeaderField(String name) {
		try {
			getInputStream();
		} catch (IOException e) {
		}
		return responses.findValue(name);
	}

	/**
	 * Gets a header field by index. Returns null if not known.
	 * 
	 * @param n
	 *            the index of the header field
	 */
	public String getHeaderField(int n) {
		try {
			getInputStream();
		} catch (IOException e) {
		}
		return responses.getValue(n);
	}

	/**
	 * Returns the Media Descriptions corresponding the describe result.
	 * <p>
	 * <b>Note:</b> the method should be called just after describe method ,
	 * otherwise the url connection content (see {@link #getContent()}) will be
	 * no more available from input stream.
	 * 
	 * @return media descriptions object
	 * @throws IOException
	 */
	public MediaDescriptions getMediaStreamConfig() throws IOException {
		if (!connected)
			throw new IOException("not connected");
		synchronized (this) {
			MediaDescriptions streamCfg = this.streamCfg;
			if (streamCfg == null) {
				ResponseContent cnt = (ResponseContent) getContent();
				Object[] mediaStreams = cnt.getContentFields("m");
				for (int i = 0; i < mediaStreams.length; i++) {
					if (streamCfg == null)
						streamCfg = new MediaDescriptions(mediaStreams[i].toString());
					else
						streamCfg.addDescritpion(mediaStreams[i].toString());
				}
			}
			return streamCfg;
		}
	}

	protected ClientRequestFactory getRequestFactory() {
		return ClientRequestFactory.getInstance();
	}

	/**
	 * Return the used <code>ServerRequestHandlerFactory</code> object.
	 * 
	 * @return {@link ServerRequestHandlerFactory} object
	 */
	protected ServerRequestHandlerFactory getServerRequestHandlerFactory() {
		return ServerRequestHandlerFactory.getInstance();
	}

	/**
	 * Returns the current sequence number
	 * 
	 * @param cseq
	 *            sequence number.
	 */
	protected void setCseq(int cseq) {
		_Cseq = cseq;
	}
	
	/**
	 * Check if the RTSP method with this method label supported by this rtsp
	 * url connection.
	 * 
	 * @param method
	 *            Method label
	 * @throws IOException
	 *             id an IO error occurs.
	 */
	protected void checkMethod(String method) throws IOException {
		if (!isSupportedMethod(method))
			throw new UnknownServiceException("Unsupported method by server :" + method);
	}

	/**
	 * Do Rtsp command (e.i: request)
	 * 
	 * @param cmd
	 * @param appData
	 * @throws IOException
	 */
	protected synchronized void doCmd(String cmd, Object appData) throws IOException {
		
		com.net.rtsp.Debug.println("####### RtspURLConnection.doCmd("+cmd+") CSEQ B4 = "+getCseq());
		//Thread.dumpStack();
		if (!connected)
			throw new IOException("not connected");
		
		try {
			checkMethod(cmd);
			if (cmd.equals(DESCRIBE) || cmd.equals(ANNOUNCE) || cmd.equals(SET_PARAMETER))
				descContent = null;
			ClientRequest r = getRequestFactory().getRequest(cmd, this);
			r.setAppData(appData);
			setCseq(r.doRequest(this, getClient(), requests, responses, getCseq()));
			com.net.rtsp.Debug.println("####### RtspURLConnection.doCmd("+cmd+") CSEQ SET !!");
		} catch (IOException e) {
			e.printStackTrace();
			if (e instanceof InterruptedIOException)
				cleanup();
			throw e;
		}finally {
			com.net.rtsp.Debug.println("###### RtspURLConnection.doCmd("+cmd+") CSEQ AFTER = "+getCseq());
		}
	}

	/**
	 * Set the rtsp session time out.
	 * 
	 * @param sessionTimeout
	 */
	protected void setSessionTimeout(int sessionTimeout) {

		this.sessionTimeout = sessionTimeout;
	}

	/**
	 * Return the DESCRIBE method attribute fields that the connection process
	 * will used when performing DESCRIBE request to the server.
	 * 
	 * @return {@link FieldAttributes} object.
	 */
	protected FieldAttributes getDescribeAttributes() {
		return dattribute;
	}

	/**
	 * Return the platform supported RTSP method.
	 * 
	 * @return <code>String</code> array of RTSP method label
	 */
	protected String[] getPlatformPossibleMethods() {
		return (String[]) POSSIBLE_METHODS.clone();
	}

	/**
	 * Sets the supported RTSP method by the server
	 * 
	 * @throws IOException
	 */
	protected void setSupportedMethods() throws IOException {
		options();
		if (isSuccessfullResponse()) {
			String pub = responses.findValue("Public");
			if (pub != null) {
				StringTokenizer st = new StringTokenizer(pub, ",");
				if (st.countTokens() > 0) {
					List l = new ArrayList();
					l.add(OPTIONS);
					String s;
					while (st.hasMoreTokens()) {
						s = st.nextToken().trim();
						if (ArraysTools.binarySearch(getPlatformPossibleMethods(), s) >= 0) {
							Debug.println(" ADD SRV METHOD " + s);
							l.add(s);
						}
					}
					this.supportedMethods = (String[]) l.toArray(new String[l.size()]);
					return;
				}
			}
		}
		throw new UnknownServiceException("Cannot retreive server supported rtsp methods.");
	}

	/**
	 * Determines whether the RTSP method label is supported.
	 * 
	 * @param method
	 *            the method label
	 * @return code>true</code> if the method is supported; <code>false</code>
	 *         otherwise.
	 */
	protected boolean isSupportedMethod(String method) {
		for (int i = 0; i < supportedMethods.length; i++) {
			if (supportedMethods[i].equals(method))
				return true;
		}
		return false;

	}

	protected EventQueue eventq;

	private void computeHostAndPort() {
		String host;
		try {
			InetAddress addr = InetAddress.getByName(url.getHost());
			host = addr.getHostAddress();
		} catch (UnknownHostException ignored) {
			host = url.getHost();
		}

		boolean useProxy = proxyHost != null
				&& (noProxy == null || (ArraysTools.binarySearch(noProxy, url.getHost().toLowerCase()) < 0 && ArraysTools.binarySearch(noProxy, host) < 0));
		if (useProxy) {
			host = proxyHost;
			if (proxyPort != -1) {
				this.port = new int[] { proxyPort };
			} else {
				this.port = PORT;
			}
		} else {
			int port = url.getPort();
			com.net.rtsp.Debug.println("RtspURLConnection.computeHostAndPort() /*//*/*/*/*/ PORT = " + port);
			if (port == -1)
				this.port = PORT;
			else
				this.port = new int[] { port };
		}
		this.host = useProxy ? proxyHost : host;
	}

	private void describe(FieldAttributes attributes) throws IOException {
		doCmd(DESCRIBE, attributes);
	}

	private static final int REQ_TIME_OUT = 4500;

	/**
	 * Connection object used as net resource for this connection.
	 */

	protected Connection connection;

	/**
	 * The request message object used by the connection to send request to
	 * server.
	 */
	protected RequestMessage requests = new RequestMessage();

	/**
	 * The response message used as server response message container.
	 */
	protected ResponseMessage responses = new ResponseMessage();

	private FieldAttributes dattribute;

	private ResponseContent descContent;

	private int requesTimout = REQ_TIME_OUT;

	private int _Cseq;

	// private String responseStatusLine;
	/**
	 * RTSP session ID
	 */
	protected String sessionID;

	/**
	 * Media descriptions value (see {@link MediaDescriptions})
	 */
	protected MediaDescriptions streamCfg;

	/**
	 * Session supported RTSP method.
	 */
	protected String[] supportedMethods;
	
	private boolean autoReconnec;

	private int sessionTimeout;

	private static String[] POSSIBLE_METHODS = new String[] { OPTIONS, DESCRIBE, PLAY, PAUSE, TEARDOWN, SETUP, SET_PARAMETER, GET_PARAMETER, ANNOUNCE };
	static {
		ArraysTools.sort(POSSIBLE_METHODS);
	}

	private static boolean init;

	private static String proxyHost;

	private static int proxyPort;

	private static String[] noProxy;

	/**
	 * All possible rtsp ports.
	 */
	protected int[] port;

	/**
	 * Connection host.
	 */
	protected String host;
	
	 private int connectTimeout;

	private static void staticInit() {

		synchronized (RtspURLConnection.class) {
			if (!init) {
				setContentHandlerFactory(RtspContentHandlerFactory.getInstance());
				proxyHost = System.getProperty("rtsp.proxyHost");
				proxyPort = Integer.getInteger("rtsp.proxyPort", 554).intValue();
				List l = new ArrayList();
				String rawList = System.getProperty("rtsp.nonProxyHosts");
				if (rawList != null) {
					java.util.StringTokenizer st = new java.util.StringTokenizer(rawList, "|", false);
					try {
						while (st.hasMoreTokens()) {
							l.add(st.nextToken().toLowerCase());
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				noProxy = (String[]) l.toArray(new String[l.size()]);
				ArraysTools.sort(noProxy);
				init = true;
			}
		}

	}

	/**
	 * Free all allocated resource by this connection. This connection becomes
	 * no more available after calling this method. connect method should be
	 * called to establish new rtsp connection with the server.
	 */
	protected void cleanup() {
		setSessionID(null);
		descContent = null;
		if (connection != null)
			try {
				connection.cleanup();
				connection = null;
			} catch (Exception e) {
			}
		connected = false;

		if (server != null)
			((Server) server).dispose();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#finalize()
	 */
	protected void finalize() throws Throwable {
		cleanup();
	}

	/**
	 * Returns the <code>ClientProcessor</code> of this rtsp url connection
	 * 
	 * @return {@link ClientProcessor} object
	 */
	protected ClientProcessor getClient() {
		synchronized (this) {
			if (client == null)
				client = new Client(connection, getRequestTimeout());
		}
		return client;
	}

	/**
	 * Return the current RTSP session ID
	 * 
	 * @param sessionID
	 *            as <code>String</code>
	 */
	protected void setSessionID(String sessionID) {
		this.sessionID = sessionID;
	}

	/**
	 * Returns the <code>ServerProcessor</code> of this rtsp url connection
	 * 
	 * @return {@link ServerProcessor} object
	 */
	protected ServerProcessor getServer() {
		synchronized (this) {
			if (server == null) {
				Server srv = new Server(connection, this);
				srv.addObserver(new ServerReplayer());

				server = srv;
			}
		}
		return server;
	}

	/*
	 * --------------------------------------------------------------------------
	 * ----------------
	 * ----------------------------------------------------------
	 * ----------------------------------
	 * ----------------------------------------
	 * ----------------------------------------------------
	 */
	private class ServerReplayer implements Observer {
		ResponseMessage error(int code, RequestMessage req) {
			String scseq = req.findValue("CSeq");
			int cseq = scseq != null ? Integer.parseInt(scseq) : 0;
			cseq++;
			ResponseMessage response = new ResponseMessage(StatusLineBuilder.getStatusLine(code));
			response.set("CSeq", "" + cseq);
			return response;
		}

		public void update(Observable o, Object arg) {
			com.net.rtsp.Debug.println("....update() server request received...");
			ServerProcessor s = (ServerProcessor) o;
			RequestMessage req = s.getServerMessage();
			// s.getServerMessage().getContent();
			String[] supportedMethod = getServerRequestHandlerFactory().getSupportedClientMethod();
			//com.net.rtsp.Debug.println("ServerReplayer.update()  supported methods : " );
//			for (int i = 0; i < supportedMethod.length; i++) {
//				com.net.rtsp.Debug.println(supportedMethod[i]);
//			}
			String rl = req.getRequestLine();
			// REQ : S -> C
//			if (!ParserTools.isRtspRequest(rl.getBytes())) {
//				s.sendResponse(error(ResponseCodes.RTSP_VERSION_NOT_SUPPORTED, req));
//				return;
//			}
			rl = rl.substring(0, rl.indexOf(' '));
			//com.net.rtsp.Debug.println("*/*/1/*/");
			if (ArraysTools.binarySearch(supportedMethod, rl) < 0) {
				s.sendResponse(error(ResponseCodes.RTSP_NOT_IMPLEMENTED, req));
				return;
			}
		//	com.net.rtsp.Debug.println("*/*/2/*/");
			try {
				
				getServerRequestHandlerFactory().getServerHandler(rl, RtspURLConnection.this).handleRequest(req, s);
			} catch ( Exception e) {
				e.printStackTrace();
			}
		//	System.err.println("--NOTIF CLIENT WITH EVENT ServerRequestMessageEvent");
			eventq.pushEvent(new ServerRequestMessageEvent(RtspURLConnection.this,req));
		}
	}

	private ClientProcessor client;

	private ServerProcessor server;

	private int state;

	/**
	 * Default <code>ClientProcessor</code> implementation.
	 * 
	 * @author tyazid
	 * 
	 */
	protected  class Client implements com.net.rtsp.ClientProcessor {
		private boolean[] reponseLock = new boolean[] { false };

		private byte[] response;

		Connection connection;

		private int timeOut;

		public Client(Connection connection, int timeOut) {
			this.connection = connection;
			this.timeOut = timeOut;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.net.rtsp.ClientProcessor#sendRequest(com.net.rtsp.RequestMessage,
		 *      com.net.rtsp.ResponseMessage)
		 */
		public synchronized void sendRequest(RequestMessage request, ResponseMessage response) throws IOException {
			byte[] bytes = request.toBytes();
			
			System.err.println("\n\n# [ C1:"+this.getClass() == RtspURLConnection.class+"] SEND REQUEST =\n"+new String(bytes));
			reponseLock[0] = false;
			//reconnect here if auto reconnect is enabled.
			boolean autoRec = isAutoReconnectEnabled();
			if( autoRec)
				connection.reconnect();
			
			

			if(!connection.sendData(bytes)){
				//trying with force reconnect 
				if( autoRec)
					connection.forceConnect();
				if(!connection.sendData(bytes))
				throw new IOException("Cannot send data");
			}
			
			Debug.println("Client.sendRequest() IS CONNECTION ALIVE = " + connection.isConnectionAlive());
			Debug.println("Client.sendRequest() REQ =\n\n" + new String(bytes));
			// --> state = waiting for response
			// --> only receive response with same CSeq
			// --> Response should starts by RTSP/1....
			boolean timeout = connection.isConnectionAlive()?waitForResponse(timeOut):true;
			com.net.rtsp.Debug.println("Client.sendRequest() T-OUT =" + timeout);
			if (!timeout) {
				if (parse(response, this.response)) {
					return;
				}
				throw new IOException("server response syntax error");// send
				// it to
				// srv
				// --> server message OPTION,ANNOUNCE,PARM
			}
			throw new IOException("server response time out");
		}

		private synchronized boolean waitForResponse(int time) {
			boolean timeout = false;
			try {
				synchronized (reponseLock) {
					if (!reponseLock[0])
						reponseLock.wait(time);
					timeout = !reponseLock[0];
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			return timeout;
		}

		/**
		 * Parse the first line of the RTSP request. It usually looks something
		 * like: "RTSP/1.0 <number> comment\r\n".
		 */

		private boolean parse(ResponseMessage responses, byte[] b) throws IOException {
			try {
			 ///	Thread.dumpStack();
			 	com.net.rtsp.Debug.println(new String(b));
				//com.net.rtsp.Debug.println("# SDP  = "+getContent());
				//System.err
				
				
				if (ParserTools.isRtspResponse(b)) { // is valid RTSP -
					// response started w/
					// "RTSP/1." so C->S
					// response
					ParserTools.parseHeader(b, responses);
					return true;
				}
				com.net.rtsp.Debug.println("Client.parse() IS NOT RTSP VALID!!!");
			} catch (IOException e) {
				throw e;
			}
			return false;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.net.rtsp.DataProcessor#processData(byte[])
		 */
		public void processData(byte[] response) {
			synchronized (reponseLock) {
				reponseLock[0] = true;
				reponseLock.notify();
				this.response = response;
			}
		}
	}

	/**
	 * Default <code>ServerProcessor</code> implementation.
	 * 
	 * @author tyazid
	 * 
	 */
	protected   class Server extends Observable implements ServerProcessor {
		private RequestMessage h;

		Connection connection;

		RtspURLConnection rtspURLConnection;

		public Server(Connection connection, RtspURLConnection rtspURLConnection) {

			this.connection = connection;

			this.rtspURLConnection = rtspURLConnection;
		}

		public void processData(byte[] data) {
			try {
				h = null;
				parse(data);
				if (h != null) {
				//	com.net.rtsp.Debug.println("Server.processData() NOTIF "+((Observable)this).countObservers());
					setChanged();
					notifyObservers();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		private void parse(byte[] b) throws IOException {
		 	com.net.rtsp.Debug.println("Server.parse() "+new String(b));
		   if (!ParserTools.isRtspResponse(b)) {//request is for client processor.
				// may be a method...
				ByteArrayInputStream bin = new ByteArrayInputStream(b);
				BufferedReader br = new BufferedReader(new InputStreamReader(bin));
				List lines = new ArrayList();
				String l;
				RequestMessage h = new RequestMessage();
				// header
				while (((l = br.readLine()).trim()).length() > 0) {
					l=l.trim();
					lines.add(l);
					if (lines.size() == 1) {// 1st line ==> method SO  METHOD ANNOUNCE [URL] RTSP/1.0
						if (!l.endsWith(RTSP_VERSION) ) 
							throw new UnknownServiceException("Protocol version not supported :" + l);
						h.setRequestLine(l);
					} else {
						int jj = l.indexOf(':');
						if (jj > 0) {
							String s1=l.substring(0, jj).trim(),
								   s2=l.substring(jj+1).trim();
					 		com.net.rtsp.Debug.println("**Server.parse() "+s1+" / "+s2);
							h.add(s1,s2 );
						}
						else
							h.add(l, null);
					}
				}
				Content cnt =  null;
				try{cnt = (Content) rtspURLConnection.getContent();}catch(IOException ex) {}
				if (cnt != null)
					h.setContent(cnt);
				this.h = h;
		    }
			/*
			 * String[] supportedMethod =
			 * ServerRequestHandlerFactory.getInstance
			 * ().getSupportedClientMethod(); rl=rl.substring(0,rl.indexOf('
			 * ')); if(supportedMethod)
			 * 
			 * request.findValue("CSeq");
			 */
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.net.rtsp.ServerProcessor#sendResponse(com.net.rtsp.ResponseMessage)
		 */
		public synchronized void sendResponse(ResponseMessage response) {
			byte[] bytes = response.toBytes();
			connection.sendData(bytes);
		}

		void dispose() {
			deleteObservers();
			rtspURLConnection = null;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.net.rtsp.ServerProcessor#getServerMessage()
		 */
		public RequestMessage getServerMessage() {
			return h;
		}
	}

}
