package net.tenie.fx.main;

import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.tenie.Sqlucky.sdk.subwindow.MyAlert;
import net.tenie.Sqlucky.sdk.utility.CommonUtils;
import net.tenie.Sqlucky.sdk.utility.StrUtils;

public class Restart {	
	private static Logger logger = LogManager.getLogger(Restart.class);
	// 重启应用
	public static void reboot(){
		try {
			String SqluckyAppPath = CommonUtils.sqluckyAppPath();
			if(SqluckyAppPath != null && !"".equals(SqluckyAppPath)) {
				execCmdAndExit(SqluckyAppPath);
				runDev();
			}else {
				MyAlert.errorAlert("Error!");
			}
			
		} catch (Exception e) {
			logger.error(" Exception = " + e.getMessage());
			MyAlert.errorAlert(e.getMessage());
			e.printStackTrace();
		}
	}
	
	private static void execCmdAndExit(String cmd) throws IOException {
		if(StrUtils.isNotNullOrEmpty(cmd)) {
			if ( CommonUtils.checkFileExist(cmd) ) {
				Runtime.getRuntime().exec(cmd); 
				logger.info(" 执行 app  " +cmd);
				System.exit(0);
			}else {
				runDev(); 
			}  
		}		
	}
	// 开发环境时候从其
	private static void runDev( ) throws IOException {
		String[] args = {};
		if(SQLucky.argsList != null && SQLucky.argsList.size() > 0) {
			 args =(String[]) SQLucky.argsList.toArray();
		} 
		StringBuilder cmd = new StringBuilder();
		cmd.append(System.getProperty("java.home") 
				+ File.separator + "bin" 
				+ File.separator + "java ");
		for (String jvmArg : ManagementFactory.getRuntimeMXBean().getInputArguments()) {
			cmd.append(jvmArg + " "); 
		} 
		cmd.append("-cp ").append(ManagementFactory.getRuntimeMXBean().getClassPath()).append(" ");
		cmd.append(SQLucky.class.getName()).append(" ");
		for (String arg : args) {
			cmd.append(arg).append(" "); 
		}
		logger.info(" cmd " +cmd);
		Runtime.getRuntime().exec(cmd.toString()); 
		
		System.exit(0);
	}
	
}
