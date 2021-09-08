package net.tenie.plugin.sdk;

public interface PluginDelegate {
	String pluginName();
	void load(AppComponent cpn);
	void unload();
}
