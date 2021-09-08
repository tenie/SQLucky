package net.tenie.fx.utility;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
 /*   @author tenie */
/**
 * language翻译 
 */
public final class ResourceBundleUtil
{
    /**
     * language 资源
     */
    private static final ResourceBundle resource;
 
    static
    {
       
        resource = ResourceBundle.getBundle("language.message", Locale.ENGLISH);
    }
 
    /**
     * 私有构造函数
     */
    private ResourceBundleUtil()
    {
 
    }
 
    /**
     * Gets a string from the ResourceBundles.
     * <br> Convenience method to save casting.
     *
     * @param key the key of the properties.
     * @return the value of the property. Return the key if the value is not found.
     */
    public static String getStringValue(String key)
    {
        try
        {
            return resource.getString(key);
        }
        catch (MissingResourceException e)
        {
            return "";
        }
    }
 
    /**
     * Gets the integer from the properties.
     *
     * @param key the key of the property.
     *
     * @return the value of the key. return -1 if the value is not found.
     */
    public static Integer getIntegerValue(String key)
    {
        try
        {
            return Integer.valueOf(resource.getString(key));
        }
        catch (MissingResourceException e)
        {
            return -1;
        }
    }
 
  
}
