package net.tenie.Sqlucky.sdk;

public interface SqluckyPluginDelegate {
	String pluginName();
	
	/**
	 * 主窗口创建前调用
	 */
	void register();
	
	/**
	 * 主程序创建单没有显示的时候加载调用
	 */
	void load();
	/**
	 * 主程序显示完后, 调用显示的方法
	 */
	public void showed();
	/**
	 * 主程序退出前调用
	 */
	void unload();
}
