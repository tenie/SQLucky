package net.tenie.fx.main;

import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.tenie.Sqlucky.sdk.utility.CommonUtility;
import net.tenie.Sqlucky.sdk.utility.StrUtils;

public class Restart {	
	private static Logger logger = LogManager.getLogger(Restart.class);
	// 重启应用
	public static void reboot(){
		try {
			String os_name = System.getProperty("os.name");
			String java_home = System.getProperty("java.home");
			logger.info("os_name = " + os_name);
			logger.info("java_home = " + java_home);

			if (os_name.toLowerCase().startsWith("win")) {
				String app = java_home.replace("runtime", "SQLucky.exe");
				logger.info("win app = " + app);
				execCmdAndExit(app);
			} else if (os_name.toLowerCase().startsWith("mac")) {
				String app = java_home.replace("runtime/Contents/Home", "MacOS/SQLucky");
				logger.info("mac app = " + app);
				execCmdAndExit(app);
			} else if (os_name.toLowerCase().startsWith("linux")) {
				String app = java_home.replace("lib/runtime", "bin/SQLucky");
				logger.info(" linux app = " + app);
				execCmdAndExit(app);
			}
			runDev();
		} catch (Exception e) {
			logger.error(" Exception = " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	private static void execCmdAndExit(String cmd) throws IOException {
		if(StrUtils.isNotNullOrEmpty(cmd)) {
			if ( CommonUtility.checkFileExist(cmd) ) {
				Runtime.getRuntime().exec(cmd); 
				logger.info(" 执行 app  " +cmd);
				System.exit(0);
			}else {
				runDev(); 
			}  
		}		
	}
	
	private static void runDev( ) throws IOException {
		String[] args = {};
		if(MainMyDB.argsList != null && MainMyDB.argsList.size() > 0) {
			 args =(String[]) MainMyDB.argsList.toArray();
		} 
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
		logger.info(" cmd " +cmd);
		Runtime.getRuntime().exec(cmd.toString()); 
		
		System.exit(0);
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
