package net.tenie.fx.factory;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.sql.Connection;
import java.util.*;
import javafx.application.Platform;
import net.tenie.Sqlucky.sdk.SqluckyPluginDelegate;
import net.tenie.Sqlucky.sdk.db.PoDao;
import net.tenie.Sqlucky.sdk.db.SqluckyAppDB;
import net.tenie.Sqlucky.sdk.po.PluginInfoPO;
import net.tenie.Sqlucky.sdk.utility.CommonUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ServiceLoad {
	private static ServiceLoader<SqluckyPluginDelegate> loader ;
	// 已经注册的插件
	private static List<SqluckyPluginDelegate> registerPlugin = new ArrayList<>();

	private static Logger logger = LogManager.getLogger(ServiceLoad.class);
	static {
        try {
			loader = loadFromUserHome();
        } catch (MalformedURLException | IllegalAccessException | InstantiationException | InvocationTargetException |
                 NoSuchMethodException | ClassNotFoundException e) {
			logger.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }
	/**
	 * 从用户目录下加载插件
	 * @return
	 * @throws MalformedURLException
	 * @throws ClassNotFoundException
	 * @throws NoSuchMethodException
	 * @throws InvocationTargetException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 */
	public static  	ServiceLoader<SqluckyPluginDelegate> loadFromUserHome() throws MalformedURLException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
		String pluginDir = CommonUtils.sqluckyAppPluginModsPath();
		File dir = new File(pluginDir);
		File[] files = dir.listFiles();
		ArrayList<URL> urls = new ArrayList<URL>();
        if (files != null) {
            for (File file : files) {
                if (file.getName().endsWith(".jar")) {
					logger.info("loading new plugin = " + file.getAbsolutePath() );
                    urls.add(file.toURI().toURL());
                }
            }
        }
        int validSize = urls.size();
		logger.info("find plugin count = " + validSize  );
		URLClassLoader pluginClassLoader = new URLClassLoader(urls.toArray(new URL[validSize]) , ServiceLoad.class.getClassLoader());

		ServiceLoader<SqluckyPluginDelegate> serviceLoader = ServiceLoader.load(SqluckyPluginDelegate.class,pluginClassLoader);
		return serviceLoader;
	}

	/**
	 * 应用还没实例化前, 调用插件的注册
	 * 会读取数据库的缓存信息, 对需要加载的插件进行注册, 调用了注册的插件实例会被换成在list中(registerPlugin)
	 * 1. 如果发现数据库中的缓存数据有, ServiceLoader 没有load的插件会删除数据库里的缓存数据
	 * 2. 如果ServiceLoader加载的插件, 数据库里没有, 就会插入新的数据
	 */
	public static void callRegister() {
		if (loader == null) {
            return;
        }
		Connection conn = SqluckyAppDB.getConn();

		try {
			List<PluginInfoPO> pluginInfoList = PoDao.select(conn, new PluginInfoPO());
			List<PluginInfoPO> deleteInfoList = new ArrayList<>();
			for (var po : pluginInfoList) {
				String code = po.getPluginCode();
				SqluckyPluginDelegate spd = findDelegateByCode(code);
				if (spd != null) {
					var loadStatus = po.getReloadStatus();
					if (loadStatus == 1) {
						spd.register();
						registerPlugin.add(spd);
					}

				} else { // 如果 数据库里的插件信息和加载的数据不同, 删除数据库的这条数据
					deleteInfoList.add(po);
				}
			}
			if (!deleteInfoList.isEmpty()) {
				for (var delPo : deleteInfoList) {
					PoDao.delete(conn, delPo);
				}
			}


			for (SqluckyPluginDelegate plugin : loader) {
				String pluginName = plugin.pluginName();
				String pluginCode = plugin.pluginCode();
				String pluginDescribe = plugin.pluginDescribe();
				String version = plugin.version();
				PluginInfoPO ppo = new PluginInfoPO();
				ppo.setPluginCode(pluginCode);
				var ls = PoDao.select(conn, ppo);
				// 不存在插入
				if (ls == null || ls.isEmpty()) {
					// 数据库中不存在就新建一条数据
					ppo.setPluginName(pluginName);
					ppo.setPluginCode(pluginCode);
					ppo.setPluginDescribe(pluginDescribe);
					ppo.setVersion(version);
					ppo.setDownloadStatus(1);
					ppo.setReloadStatus(1);

					ppo.setCreatedTime(new Date());
					PoDao.insert(conn, ppo);

					plugin.register();
					registerPlugin.add(plugin);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			SqluckyAppDB.closeConn(conn);
		}


	}

	/**
	 * 根据plugin_code 找到ServiceLoader加载的插件实例
	 * @param code
	 * @return
	 */
	public static SqluckyPluginDelegate findDelegateByCode(String code){
		for(SqluckyPluginDelegate plugin: loader ) {
			String pluginCode = plugin.pluginCode();
			if(code.equals(pluginCode)){
				return plugin;
			}
		}
		return null;
	}
	
	// 应用实例化完成后, 但还没显示前
	public static void callLoad() {
		if (registerPlugin.isEmpty()) {
            return;
        }
		for (SqluckyPluginDelegate plugin : registerPlugin) {
			try {
				plugin.load();
			}catch (Exception e){
				e.printStackTrace();
			}

		}
	}
	// 应用显示后
	public static void callShowed(){
		if (registerPlugin.isEmpty()) {
            return;
        }
		Platform.runLater(()->{
			for(SqluckyPluginDelegate plugin: registerPlugin ) {
				try {
					plugin.showed();
				}catch (Exception e){
					e.printStackTrace();
				}
			}
		});

	}

}
