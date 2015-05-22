package com.net.rtsp;

import java.io.PrintStream;

public abstract class Debug {
	public static final boolean debug_enabled = !false;

	static final PrintStream org_out = System.out;
	static PrintStream out = org_out;

	public static void setPrintStream(PrintStream out) {
		Debug.out = out;
	}

	public static void setDefaultPrintStream() {
		if (out != org_out)
			out = org_out;
	}
	public static void println( ) {
		 if (debug_enabled)
		  out.println();
	}
	public static void print(Object object) {
		 if (debug_enabled)
		   out.print(object);
	}
	public static void println(Object object) {
 		 if (debug_enabled)
		   out.println(object);
	}

	public static void dump(byte[] data) {
		if (debug_enabled)
			for (int i = 0; i < data.length; ++i) {
				int value = data[i] & 0xFF;

				out.println(i + ": " + Integer.toHexString(value));
			}
	}
}