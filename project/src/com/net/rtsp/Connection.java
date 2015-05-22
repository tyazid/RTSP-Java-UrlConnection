package com.net.rtsp;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.net.ConnectException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Vector;

/**
 * A connection with a specific rtsp server. This component is in charge  to
 * establish net connection with the server, send and receive data to/from it and
 * close it when the session done.
 * 
 * @author tyazid
 * 
 */

public class Connection implements Runnable {
	private Socket socket;

	private Vector processors;

	private boolean[] connectionAlive = new boolean[] { false };

	private InputStream serverInput;

	private PipedOutputStream pOut;

	private Thread thread;

	private int port;

	private String host;

	/**
	 * Creates a stream Connection and connects it to the specified port number on
	 * the named host.
	 * <p>
	 * If the application has specified a server socket factory, that factory's
	 * <code>createSocketImpl</code> method is called to create the actual
	 * socket implementation. Otherwise a "plain" socket is created.
	 * 
	 * @param host
	 *            the host name.
	 * @param port
	 *            the port number.
	 * @exception IOException
	 *                if an I/O error occurs when creating the Connection.
	 */

	public Connection(String host, int port) throws IOException {
		try {
			this.host = host;
			this.port = port;
			forceConnect();
			processors = new Vector();
			pOut = new PipedOutputStream();
			serverInput = new PipedInputStream(pOut);
		} catch (IOException e) {
			if (Debug.debug_enabled)
				e.printStackTrace();
			throw new ConnectException();
		} catch (SecurityException e) {
			if (Debug.debug_enabled)
				e.printStackTrace();
			throw new ConnectException(e.getMessage());
		}

	}

	/**
	 * Add a data processor to this connection.
	 * <p>
	 * When the data is received form the server, it is dispatched to all
	 * registered processor
	 * 
	 * @param processor
	 */
	public void addDataProcessor(DataProcessor processor) {
		processors.addElement(processor);
	}

	/**
	 * send data to the connected server.
	 * @param data
	 * @return true if data was successfully sent.
	 */
	public boolean sendData(byte data[]) {
		boolean success = false;
		 Debug.println("########\nSEND TO "+host+":"+port+"\n"+new String(data)+"\n########\n");
		 

		try {
			OutputStream out = getOutputStream();
			out.write(data);
			out.flush();
			success = true;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return success;
	}

	public final void run() {

		setConnectionAlive(true, true);
		MASTER: while (isConnectionAlive()) {
			try {
				InputStream in = socket.getInputStream();

				DataInputStream din = new DataInputStream(in);
				if (!isConnectionAlive())
					break;
				byte ch = din.readByte();
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				if (!isConnectionAlive())
					break;
				// read message header:
				baos.write(ch);
				byte b;
				com.net.rtsp.Debug.println("[CONNECTION.DUMPING] .... RECEIVEING:");
				while (!eomReached(baos.toByteArray())) {
					
					if (!isConnectionAlive())
						break MASTER;
					baos.write(b=din.readByte());
					com.net.rtsp.Debug.print((char)b);
					
				}
				
				com.net.rtsp.Debug.println();
				com.net.rtsp.Debug.println("[CONNECTION.DUMPING] .... DONE:");

				// read message body:
				int length = getContentLength(new String(baos.toByteArray()));
				if (length > 0 && isConnectionAlive()) {
					for (int i = 0; i < length; i++) {
						if (!isConnectionAlive())
							break MASTER;
						pOut.write(din.read());
					}
					pOut.flush();
				}
				if (!isConnectionAlive())
					break MASTER;
				send2Processor(baos.toByteArray());
				
			} catch (Exception e) {
				cleanup(true);
			//	e.printStackTrace();
			}
		}
	}

	/**
	 * Releases this <code>Connection</code> object's immediately.
	 * <P>
	 * Calling the method <code>cleanup</code> on a <code>Connection</code>
	 * object that is already closed is a no-op.
	 * <P>
	 * <B>Note:</B> A <code>Connection</code> object is automatically cleaned up
	 * when it is garbage collected. Certain fatal errors also close a
	 * <code>Connection</code> object.
	 */
	public void cleanup() {
		cleanup(false);
	}
	public void cleanup(boolean internal) {
		if (processors != null)
			processors.removeAllElements();
		setConnectionAlive(false);
		
		if (!internal && thread != null && thread.isAlive()) {
			thread.interrupt();
			try {
				thread.join(500);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
		}

		try {
			if (socket != null) {
				socket.close();
				socket = null;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	
	}

	/**
	 * Determines whether this connection is alive (still connected) or not
	 * @return <code>true</code> if still connected:<code>false</code> otherwise.
	 */
	public boolean isConnectionAlive() {
		synchronized (connectionAlive) {
			return connectionAlive[0];
		}
	}

	private void setConnectionAlive(boolean alive, boolean notify) {
		synchronized (connectionAlive) {
			connectionAlive[0] = alive;
			if (notify)
				connectionAlive.notify();
			
		}
	}

	private void setConnectionAlive(boolean alive) {
		setConnectionAlive(alive, false);
	}

	public void reconnect() throws UnknownHostException, IOException {
		if (socket == null || !isConnectionAlive())
			forceConnect();
	}

	/**
	 * force connection, i.e: although the current connection is alive, it will be closed and new one will be established. 
	 * @throws UnknownHostException
	 * @throws IOException
	 */
	public synchronized void forceConnect() throws UnknownHostException, IOException {
		Vector pro = processors != null ? (Vector) processors.clone() : null;
		cleanup();
		// InetAddress adresseServeur = InetAddress.getByName(host);
		//socket = new Socket(adresseServeur, port);
		socket = new Socket(host, port);
	//	socket.setSoTimeout(1000);
		thread = new Thread(this);
		setConnectionAlive(false);
		thread.start();
		synchronized (connectionAlive) {
			if (!connectionAlive[0])
				try {
					connectionAlive.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
		}
		processors = pro;
	}

	/**
	 * Returns an input stream for this connection.
	 * 
	 * @return an input stream for reading bytes from this connection.
	 * @exception IOException
	 *                if an I/O error occurs when creating the input stream.
	 * @throws IOException
	 */
	public InputStream getInputStream() throws IOException {
		if (socket == null)
			throw new IOException("Not connected");
		return serverInput;
	}

	/**
	 * 
	 * Returns an output stream for this connection.
	 * 
	 * @return an output stream for writing bytes to this connection.
	 * @exception IOException
	 *                if an I/O error occurs when creating the output stream.
	 */
	public OutputStream getOutputStream() throws IOException {
		if (socket == null)
			throw new IOException("Not connected");
		return socket.getOutputStream();
	}

	protected void finalize() throws Throwable {
		cleanup();

	}

	private static int getContentLength(String msg_header) {
		int length;
		int start = msg_header.toLowerCase().indexOf("content-length");
		if (start == -1) {
			length = 0;
		} else {
			start = msg_header.indexOf(':', start) + 2;
			int end = msg_header.indexOf('\r', start);
			String length_str = msg_header.substring(start, end);
			 length = Integer.parseInt(length_str) ;
		}
		return length;
	}

	private void send2Processor(byte[] b) {
		
		DataProcessor[] all =new DataProcessor[processors.size()];
	     processors.copyInto(all)  ;
		for (int i = 0; i < all.length; i++)
			try {
				all[i].processData((byte[]) b.clone());
			} catch (Exception e) {
				e.printStackTrace();
			}

	}

	private boolean eomReached(byte buffer[]) {
		boolean endReached = false;
		int size = buffer.length;
		if (size >= 4) {
			if (buffer[size - 4] == '\r' && buffer[size - 3] == '\n' && buffer[size - 2] == '\r' && buffer[size - 1] == '\n') {
				endReached = true;
			}
		}
		return endReached;
	}

}
