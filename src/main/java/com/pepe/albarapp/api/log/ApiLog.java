package com.pepe.albarapp.api.log;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.boot.logging.LogLevel;

public class ApiLog {

	public static int apiLogLevel = 2;

	private static final String TIMESTAMP = "timestamp";
	private static final String ELAPSED_TIME = "elapsedTime";
	private static final String ENDPOINT = "endpoint";
	private static final String UID = "uid";

	public static void startLoggingContext(String endpoint) {
		MDC.put(TIMESTAMP, Long.toString(System.currentTimeMillis()));
		MDC.put(ELAPSED_TIME, "0ms");
		MDC.put(ENDPOINT, endpoint != null ? endpoint : "-");
		MDC.put(UID, "-");
	}

	public static void addUserToLoggingContext(String username) {
		MDC.put(UID, username);
	}

	public static void updateElapsedTime() {
		String timestamp = MDC.get(TIMESTAMP);
		if (timestamp != null) {
			MDC.put(ELAPSED_TIME, System.currentTimeMillis() - Long.parseLong(MDC.get(TIMESTAMP)) + "ms");
		}
	}

	public static void clearLoggingContext() {
		MDC.clear();
	}

	static void log(Class c, LogLevel logLevel, String message) {
		Logger logger = LoggerFactory.getLogger(c);
		updateElapsedTime();

		switch (logLevel) {
			case TRACE:
				logger.trace(message);
				break;
			case DEBUG:
				logger.debug(message);
				break;
			case INFO:
				logger.info(message);
				break;
			case WARN:
				logger.warn(message);
				break;
			case ERROR:
				logger.error(message);
				break;
		}
	}
}
