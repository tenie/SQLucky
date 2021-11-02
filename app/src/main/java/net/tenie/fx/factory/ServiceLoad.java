package net.tenie.fx.factory;

import java.util.ServiceLoader;

import javafx.application.Platform;
import net.tenie.Sqlucky.sdk.SqluckyPluginDelegate;

public class ServiceLoad {
	private static ServiceLoader<SqluckyPluginDelegate> loader ;
	
	public static void callRegister(){
		loader = ServiceLoader.load(SqluckyPluginDelegate.class);
		for(SqluckyPluginDelegate plugin: loader ) {
			String name = plugin.pluginName();
			System.out.println("\n====================");
			System.out.println(name);
			 plugin.register();
			System.out.println("====================\n");
		}
	}
	
	
	public static void callLoad(){
		loader = ServiceLoader.load(SqluckyPluginDelegate.class);
		for(SqluckyPluginDelegate plugin: loader ) {
			String name = plugin.pluginName();
			System.out.println("\n====================");
			System.out.println(name);
			 plugin.load();
			System.out.println("====================\n");
		}
	}
	
	public static void callShowed(){
		Platform.runLater(()->{
			for(SqluckyPluginDelegate plugin: loader ) {
				String name = plugin.pluginName();
				System.out.println("\n====================");
				System.out.println(name);
				plugin.showed();
				System.out.println("====================\n");
			}
		});	

	}
	
}
