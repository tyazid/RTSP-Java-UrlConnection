package com.net.rtsp;

import java.io.IOException;

public class ResponseTimedOutException extends IOException {

	ResponseTimedOutException(String message, Throwable cause) {
		super(message, cause);

	}

	ResponseTimedOutException(String message) {
		super(message);

	}

	ResponseTimedOutException(Throwable cause) {
		super(cause);

	}

}
