package net.tenie.plugin.note.impl;

import net.tenie.Sqlucky.sdk.AppComponent;
import net.tenie.Sqlucky.sdk.PluginDelegate;

public class NoteDelegateImpl implements PluginDelegate {
	public static AppComponent appCom ;
	@Override
	public void load(AppComponent cpn) {
		System.out.println("load:  NoteDelegateImp...");
		System.out.println(cpn);
		appCom = cpn;
	}

	@Override
	public void unload() {
		System.out.println("unload: NoteDelegateImp...");

	}

	@Override
	public String pluginName() { 
		return "note";
	}

}
