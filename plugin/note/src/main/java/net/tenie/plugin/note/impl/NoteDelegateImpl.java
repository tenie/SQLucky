package net.tenie.plugin.note.impl;

import net.tenie.plugin.sdk.AppComponent;
import net.tenie.plugin.sdk.PluginDelegate;

public class NoteDelegateImpl implements PluginDelegate {

	@Override
	public void load(AppComponent cpn) {
		System.out.println("load:  NoteDelegateImp...");
		System.out.println(cpn);
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
