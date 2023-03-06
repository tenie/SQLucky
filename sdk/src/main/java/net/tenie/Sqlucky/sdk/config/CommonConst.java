package net.tenie.Sqlucky.sdk.config;

import java.util.HashMap;
import java.util.Map;

public class CommonConst {
	public final static String TYPE_TABLE = "TABLE";
	public final static String TYPE_VIEW  = "VIEW";
	
	public final static String THEME_DARK    = "DARK";
	public final static String THEME_LIGHT   = "LIGHT";
	public final static String THEME_YELLOW  = "YELLOW";
	
	
	public static Map<String, Integer> PROCEDURE_TYPE = new HashMap<>();
	
	/**
	 * 系统判断代码来自 javafx.base模块的 PlatformUtil 
	 */
    private static final String os = System.getProperty("os.name");
    private static final String version = System.getProperty("os.version");
    // a property used to denote a non-default impl for this host
    private static String javafxPlatform;
    private static final boolean ANDROID = "android".equals(javafxPlatform) || "Dalvik".equals(System.getProperty("java.vm.name"));
    private static final boolean WINDOWS = os.startsWith("Windows");
    private static final boolean WINDOWS_VISTA_OR_LATER = WINDOWS && versionNumberGreaterThanOrEqualTo(6.0f);
    private static final boolean WINDOWS_7_OR_LATER = WINDOWS && versionNumberGreaterThanOrEqualTo(6.1f);
    private static final boolean MAC = os.startsWith("Mac");
    private static final boolean LINUX = os.startsWith("Linux") && !ANDROID;
    private static final boolean SOLARIS = os.startsWith("SunOS");
    private static final boolean IOS = os.startsWith("iOS");
    private static final boolean STATIC_BUILD = "Substrate VM".equals(System.getProperty("java.vm.name"));

    private static boolean versionNumberGreaterThanOrEqualTo(float value) {
        try {
            return Float.parseFloat(version) >= value;
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Returns true if the operating system is a form of Windows.
     */
    public static boolean isWindows(){
        return WINDOWS;
    }

    /**
     * Returns true if the operating system is at least Windows Vista(v6.0).
     */
    public static boolean isWinVistaOrLater(){
        return WINDOWS_VISTA_OR_LATER;
    }

    /**
     * Returns true if the operating system is at least Windows 7(v6.1).
     */
    public static boolean isWin7OrLater(){
        return WINDOWS_7_OR_LATER;
    }

    /**
     * Returns true if the operating system is a form of Mac OS.
     */
    public static boolean isMac(){
        return MAC;
    }

    /**
     * Returns true if the operating system is a form of Linux.
     */
    public static boolean isLinux(){
        return LINUX;
    }

     
//	static {
//		PROCEDURE_TYPE.put("String", java.sql.Types.VARCHAR);
//		PROCEDURE_TYPE.put("Integer", java.sql.Types.BIGINT);
//		PROCEDURE_TYPE.put("Decimal", java.sql.Types.DOUBLE);
//		PROCEDURE_TYPE.put("Time",  java.sql.Types.TIMESTAMP);
//	}
}
