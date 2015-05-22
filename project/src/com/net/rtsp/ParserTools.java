package com.net.rtsp;



import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;



 class ParserTools {
	private static final boolean DBG = Debug.debug_enabled;
private ParserTools(){}


 /** Parse a MIME header from byte[] data */  
static void parseHeader(byte[] data, Message header) throws IOException {
	if (data.length == 0)
		return;
	//System.err.println("\n\n\nParserTools.parseHeader()  "+new String(data)+"\n\n\n\n");
	InputStream is = new ByteArrayInputStream(data);
	StringBuffer sb = DBG?new StringBuffer():null;
	char s[] = new char[10];
	int firstc = is.read();
	String lastK = null;
	while (firstc != '\n' && firstc != '\r' && firstc >= 0) {
		if(DBG)sb.append((char)firstc);
		int len = 0;
		int keyend = -1;
		int c;
		boolean inKey = firstc > ' ';
		s[len++] = (char) firstc;
		
		parseloop: {
			while ((c = is.read()) >= 0) {
				if(DBG)sb.append((char)c);
				switch (c) {
				case ':':
					if (inKey && len > 0)
						keyend = len;
					inKey = false;
					break;
				case '\t':
					c = ' ';
				case ' ':
					inKey = false;
					break;
				case '\r':
				case '\n':
					firstc = is.read();
					if(DBG)sb.append((char)firstc);
					if (c == '\r' && firstc == '\n') {
						firstc = is.read();
						if(DBG)sb.append((char)firstc);
						if (firstc == '\r'){
							firstc = is.read();
							if(DBG)sb.append((char)firstc);
						}
					}
					if (firstc == '\n' || firstc == '\r' || firstc > ' ')
						break parseloop;
					/* continuation */
					c = ' ';
					break;
				}
				if (len >= s.length) {
					char ns[] = new char[s.length * 2];
					System.arraycopy(s, 0, ns, 0, len);
					s = ns;
				}
				s[len++] = (char) c;
			}
			firstc = -1;
		}
		while (len > 0 && s[len - 1] <= ' ')
			len--;
		String k;
		if (keyend <= 0) {
			k = null;
			keyend = 0;
		} else {
			k = String.copyValueOf(s, 0, keyend);
			if (keyend < len && s[keyend] == ':')
				keyend++;
			while (keyend < len && s[keyend] <= ' ')
				keyend++;
		}
		String v;
		if (keyend >= len)
			v = "";
		else
			v = String.copyValueOf(s, keyend, len - keyend);
		//com.net.rtsp.Debug.println("#### ParserTools.parseHeader() k = "+k+" lastk="+lastK+" v ="+v);
		if(k==null && lastK!=null) {
			
			String pv =  header.findValue(lastK)+v;
			header.set(lastK, pv);			
		//	com.net.rtsp.Debug.println("#### ---> ParserTools.parseHeader() k = "+k+" lastk="+lastK+" v ="+pv);
			lastK = null;
		}else {
			header.add(k, v);
		//	com.net.rtsp.Debug.println("####  k = "+k+" v ="+v);
			lastK = k!=null?k:lastK; 
		}
		
		
	}
}





static boolean isRtspRequest(byte[] buffer){
	 int size = buffer.length;
	 com.net.rtsp.Debug.println(""+(char)buffer[0]+""+(char)buffer[1]+"..."+(char)buffer[6] );
	 
	return  size> 7 && (buffer[0] == 'R' || buffer[0] == 'r') &&
    (buffer[1] == 'T' || buffer[1] == 't') && 
    (buffer[2] == 'S' || buffer[2] == 's') &&
    (buffer[3] == 'P' || buffer[3] == 'p') &&
	                   buffer[4] == ':' && 
	                   buffer[5] == '/' &&
	                   buffer[6] == '/';
}

static boolean isRtspResponse(byte[] buffer){
	 int size = buffer.length;
	
	return  size> 7 && (buffer[0] == 'R' || buffer[0] == 'r') &&
	                   (buffer[1] == 'T' || buffer[1] == 't') && 
	                   (buffer[2] == 'S' || buffer[2] == 's') &&
	                   (buffer[3] == 'P' || buffer[3] == 'p') &&
	                   buffer[4] == '/' && 
	                   buffer[5] == '1' &&
	                   buffer[6] == '.';
}

static boolean rtspReached(byte buffer[]) {
    boolean endReached = false;

    int size = buffer.length;

    if (size >= 4) {
        if (buffer[size - 4] == '\r' && buffer[size - 3] == '\n' &&
                buffer[size - 2] == '\r' && buffer[size - 1] == '\n') {
            endReached = true;
        }
    }

    return endReached;
}
 
	
 
}
