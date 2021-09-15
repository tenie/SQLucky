package net.tenie.fx.factory;

import java.util.ServiceLoader;

import net.tenie.Sqlucky.sdk.PluginDelegate;

public class ServiceLoad {
	public static void myLoader(){
		ServiceLoader<PluginDelegate> loader = ServiceLoader.load(PluginDelegate.class);
		for(PluginDelegate plugin: loader ) {
			String name = plugin.pluginName();
			System.out.println("\n====================");
			System.out.println(name);
			System.out.println("====================\n");
		}
	}
}
