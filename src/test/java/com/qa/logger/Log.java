package com.qa.logger;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;

public class Log {

	static Logger Log = Logger.getLogger(Log.class.getName());

	public static void INFO(String Message) {
		DOMConfigurator.configure("log4j.xml");
		Log.info(Message);
	}

	public static void ERROR(String Message) {
		DOMConfigurator.configure("log4j.xml");
		Log.error(Message);
	}

	public static void error(String message)
	{
		Log.error(message);
	}

	public static void fatal(String message)
	{
		Log.fatal(Log);
	}
	public static void debug(String message)
	{
		Log.debug(Log);
	}

	/**
	 * startTime : This method is to returns start time in long
	 * @return Start time
	 */
	public static long startTime() {
		long x = System.currentTimeMillis();
		return(x);
	}

	/**
	 * elapsedTime : This method is to returns time difference
	 * @return Time difference
	 */
	public static long elapsedTime(long startTime) {

		return ((System.currentTimeMillis() - startTime)/1000);

	}

}
