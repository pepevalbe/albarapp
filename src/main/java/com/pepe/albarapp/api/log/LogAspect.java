package com.pepe.albarapp.api.log;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.boot.logging.LogLevel;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Component
@Aspect
public class LogAspect {

	@Around(value = "@within(Log) || @annotation(Log)")
	public Object around(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {

		// No logging if api log level is 0
		if (ApiLog.apiLogLevel == 0) {
			return proceedingJoinPoint.proceed();
		}

		MethodSignature signature = (MethodSignature) proceedingJoinPoint.getSignature();
		Method method = signature.getMethod();

		// Get annotation log level
		Log logMethod = method.getAnnotation(Log.class);
		Log logClass = proceedingJoinPoint.getTarget().getClass().getAnnotation(Log.class);
		LogLevel logLevel = logMethod != null ? logMethod.level() : logClass.level();

		// Only show parameters and return value if api log level is 2 or higher
		boolean showParameters = ApiLog.apiLogLevel >= 2;
		boolean showReturnValue = ApiLog.apiLogLevel >= 2;

		StringBuilder logMessage = new StringBuilder();
		logMessage.append(proceedingJoinPoint.getSignature().getName());

		if (showParameters) {
			logMessage.append("(");
			if (proceedingJoinPoint.getArgs() != null) {
				for (int i = 0; i < proceedingJoinPoint.getArgs().length; i++) {
					logMessage.append(method.getParameterTypes()[i].getSimpleName()).append(":").append(proceedingJoinPoint.getArgs()[i]);
					if (i < proceedingJoinPoint.getArgs().length - 1) {
						logMessage.append(", ");
					}
				}
			} else {
				logMessage.append("void");
			}
			logMessage.append(")");
		}

		ApiLog.log(proceedingJoinPoint.getTarget().getClass(), logLevel, logMessage.toString());
		logMessage.setLength(0);
		logMessage.trimToSize();

		// Execute annotated method
		Object result = proceedingJoinPoint.proceed();

		// After method execution
		if (showReturnValue) {
			logMessage.append(proceedingJoinPoint.getSignature().getName()).append(" = ");
			logMessage.append(result == null ? "void" : result);
			ApiLog.log(proceedingJoinPoint.getTarget().getClass(), logLevel, logMessage.toString());
		}

		return result;
	}
}