package net.tenie.Sqlucky.sdk;

public interface PluginDelegate {
	String pluginName();
	void load(AppComponent cpn);
	void unload();
}
