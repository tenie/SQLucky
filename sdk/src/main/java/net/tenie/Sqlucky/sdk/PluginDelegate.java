package net.tenie.Sqlucky.sdk;

public interface PluginDelegate {
	String pluginName();
	void load();
	public void showed();
	void unload();
}
