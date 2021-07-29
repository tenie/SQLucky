package net.tenie.fx.main;

import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.tenie.fx.utility.CommonUtility;
import net.tenie.lib.tools.StrUtils;

public class Restart {
	
	
	
	private static Logger logger = LogManager.getLogger(Restart.class);
	// 重启应用
	public static void reboot() throws IOException, InterruptedException {
		String[] args = {};
		if(MainMyDB.argsList != null && MainMyDB.argsList.size() > 0) {
			 args =(String[]) MainMyDB.argsList.toArray();
		} 
		 
		String userDir  = MainMyDB.userDir; 
		String os_name = System.getProperty("os.name");
		String java_home =  System.getProperty("java.home");
		logger.info("userDir = " + userDir);
		logger.info("os_name = " + os_name);
		logger.info("java_home = " + java_home);
		
		
		if (os_name.toLowerCase().startsWith("win")  ) {
			String  app = userDir + File.separator + "SQLucky.exe";
			if ( CommonUtility.checkFileExist(app) ) { 
				logger.info("app = " + app);
				execCmdAndExit(app);
			} 
		}else if (os_name.toLowerCase().startsWith("mac")  ) {
			String app = System.getProperty("java.home");
//			String app  = "/Volumes/SQLucky/SQLucky.app/Contents/runtime/Contents/Home";
			app = app.replace("runtime/Contents/Home", "MacOS/SQLucky");
			
			if ( CommonUtility.checkFileExist(app) ) {
				logger.info("app = " + app);
				execCmdAndExit(app); 
			}

		}else if (os_name.toLowerCase().startsWith("linux")  ) {
			
			String app = System.getProperty("java.home"); 
			app += "/SQLucky";
			logger.info("linux app = " + app);
					
//			String app = System.getProperty("java.home"); 
//			app = app.replace("runtime/Contents/Home", "MacOS/SQLucky");
//			if ( CommonUtility.checkFileExist(app) ) {
//				logger.info("app = " + app);
//				execCmdAndExit(app); 
//			}
		}
		 
		runDev(args);
		 

		
		
	}
	
	private static void execCmdAndExit(String cmd) throws IOException {
		if(StrUtils.isNotNullOrEmpty(cmd)) {
			Runtime.getRuntime().exec(cmd.toString()); 
			logger.info(" cmd " +cmd);
			System.exit(0);
		}		
	}
	
	private static void runDev(String[] args ) throws IOException {
		StringBuilder cmd = new StringBuilder();
		cmd.append(System.getProperty("java.home") 
				+ File.separator + "bin" 
				+ File.separator + "java ");
		for (String jvmArg : ManagementFactory.getRuntimeMXBean().getInputArguments()) {
			cmd.append(jvmArg + " "); 
		} 
		cmd.append("-cp ").append(ManagementFactory.getRuntimeMXBean().getClassPath()).append(" ");
		cmd.append(MainMyDB.class.getName()).append(" ");
		for (String arg : args) {
			cmd.append(arg).append(" "); 
		}
		
		execCmdAndExit(cmd.toString());
	}
	public static void main(String[] args) {
		String ops = System.getProperty("os.name");
		String userDir = System.getProperty("user.dir");
		String val =System.getProperty("sun.java.command");
		System.out.println(ops);
		System.out.println(userDir);
		System.out.println(val);
	}
	
}
