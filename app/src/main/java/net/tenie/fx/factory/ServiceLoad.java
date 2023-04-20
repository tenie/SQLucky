package net.tenie.fx.factory;

import java.sql.Connection;
import java.util.Date;
import java.util.ServiceLoader;

import javafx.application.Platform;
import net.tenie.Sqlucky.sdk.SqluckyPluginDelegate;
import net.tenie.Sqlucky.sdk.db.PoDao;
import net.tenie.Sqlucky.sdk.db.SqluckyAppDB;
import net.tenie.Sqlucky.sdk.po.PluginInfoPO;

public class ServiceLoad {
	private static ServiceLoader<SqluckyPluginDelegate> loader ;
	// 应用还没实例化前
	public static void callRegister(){
		loader = ServiceLoader.load(SqluckyPluginDelegate.class); 
		if(loader == null  ) return ;
		Connection conn = SqluckyAppDB.getConn();
		try {
			for(SqluckyPluginDelegate plugin: loader ) {
				String pluginName = plugin.pluginName();
				String pluginCode = plugin.pluginCode();
				String pluginDescribe = plugin.pluginDescribe();
				String version = plugin.version();
				PluginInfoPO ppo = new PluginInfoPO();
				ppo.setPluginCode(pluginCode);
				
				var ls = PoDao.select(conn, ppo);
				var loadStatus = 0;
				// 已经存在
				if(ls != null && ls.size()> 0) {
					ppo = ls.get(0);
					loadStatus = ppo.getReloadStatus();
					
				}else { // 数据库中不存在就新建一条数据
					ppo.setPluginName(pluginName);
					ppo.setPluginCode(pluginCode);
					ppo.setPluginDescribe(pluginDescribe);
					ppo.setVersion(version);
					ppo.setDownloadStatus(1);
					ppo.setReloadStatus(1);
					
					ppo.setCreatedTime(new Date());
					PoDao.insert(conn, ppo);
					loadStatus = 1;
				}
				if(loadStatus == 1) {
//					System.out.println("\n====================");
//					System.out.println(pluginName);
					plugin.register();
//					System.out.println("====================\n");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			SqluckyAppDB.closeConn(conn);
		}
		
		
	}
	
	// 应用实例化完成后, 但还没显示前
	public static void callLoad(){
		loader = ServiceLoader.load(SqluckyPluginDelegate.class); 
		if(loader == null  ) return ;
		Connection conn = SqluckyAppDB.getConn();
		try {
			for(SqluckyPluginDelegate plugin: loader ) {
				String pluginName = plugin.pluginName();
				String pluginCode = plugin.pluginCode();
				String pluginDescribe = plugin.pluginDescribe();
				String version = plugin.version();
				PluginInfoPO ppo = new PluginInfoPO();
				ppo.setPluginCode(pluginCode);
				
				var ls = PoDao.select(conn, ppo);
				var loadStatus = 0;
				// 已经存在
				if(ls != null && ls.size()> 0) {
					ppo = ls.get(0);
					loadStatus = ppo.getReloadStatus();
				} 
				if(loadStatus == 1) {
//					System.out.println("\n====================");
//					System.out.println(pluginName);
					plugin.load();
//					System.out.println("====================\n");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			SqluckyAppDB.closeConn(conn);
		}
		
	}
	// 应用显示后
	public static void callShowed(){
		Platform.runLater(()->{
			loader = ServiceLoader.load(SqluckyPluginDelegate.class); 
			if(loader == null  ) return ;
			Connection conn = SqluckyAppDB.getConn();
			try {
				for(SqluckyPluginDelegate plugin: loader ) {
					String pluginName = plugin.pluginName();
					String pluginCode = plugin.pluginCode();
					String pluginDescribe = plugin.pluginDescribe();
					String version = plugin.version();
					PluginInfoPO ppo = new PluginInfoPO();
					ppo.setPluginCode(pluginCode);
					
					var ls = PoDao.select(conn, ppo);
					var loadStatus = 0;
					// 已经存在
					if(ls != null && ls.size()> 0) {
						ppo = ls.get(0);
						loadStatus = ppo.getReloadStatus();
						
					} 
					if(loadStatus == 1) {
						String name = plugin.pluginName();
//						System.out.println("\n====================");
//						System.out.println(name);
						plugin.showed();
//						System.out.println("====================\n");
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}finally {
				SqluckyAppDB.closeConn(conn);
			}
			
		});	

	}
	
}
