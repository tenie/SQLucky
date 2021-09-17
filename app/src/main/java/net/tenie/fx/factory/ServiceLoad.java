package net.tenie.fx.factory;

import java.util.ServiceLoader;

import javafx.application.Platform;
import net.tenie.Sqlucky.sdk.PluginDelegate;

public class ServiceLoad {
	private static ServiceLoader<PluginDelegate> loader ;
	public static void myLoader(){
		loader = ServiceLoader.load(PluginDelegate.class);
		for(PluginDelegate plugin: loader ) {
			String name = plugin.pluginName();
			System.out.println("\n====================");
			System.out.println(name);
			 plugin.load();
			System.out.println("====================\n");
		}
	}
	
	public static void myShowed(){
		Platform.runLater(()->{
			for(PluginDelegate plugin: loader ) {
				String name = plugin.pluginName();
				System.out.println("\n====================");
				System.out.println(name);
				plugin.showed();
				System.out.println("====================\n");
			}
		});	

	}
	
}
