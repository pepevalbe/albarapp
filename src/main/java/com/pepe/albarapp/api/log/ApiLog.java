package com.pepe.albarapp.api.log;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.boot.logging.LogLevel;

public class ApiLog {

	public static int apiLogLevel = 2;

	public static void startLoggingContext(String endpoint) {
		MDC.put("timestamp", Long.toString(System.currentTimeMillis()));
		MDC.put("elapsedTime", "0ms");
		MDC.put("endpoint", "-");
		MDC.put("uid", "-");

		if (endpoint != null) {
			MDC.put("endpoint", endpoint);
		}
	}

	public static void addUserToLoggingContext(String username) {
		MDC.put("uid", username);
	}

	public static void updateElapsedTime() {
		long elapsedTime = 0;
		String timestamp = MDC.get("timestamp");
		if (timestamp != null) {
			elapsedTime = System.currentTimeMillis() - Long.parseLong(MDC.get("timestamp"));
		}
		MDC.put("elapsedTime", elapsedTime + "ms");
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
