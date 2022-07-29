package net.tenie.Sqlucky.sdk.utility;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MyOption { 
	private static Logger logger = LogManager.getLogger(MyOption.class);
	
	public static void gc(final Class<?> clazz, String func) {
		logger.info(clazz.toString()+"."+func+" call: System.gc();");
		System.gc();
	}
	
	
}
