package com.net.rtsp;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

class Config {
	private static Properties props;

	private static void init() {
		synchronized (Config.class) {
			if (props == null) {
				InputStream in = Config.class.getResourceAsStream(System.getProperty("rtsp.config", "rtsp_config.properties"));
				props = new Properties();
				try {
					props.load(in);
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	static String getValue(String key) {
		init();
		return props.getProperty(key);
	}

}
