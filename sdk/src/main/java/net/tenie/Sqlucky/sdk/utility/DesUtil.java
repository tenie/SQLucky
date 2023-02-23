package net.tenie.Sqlucky.sdk.utility;


import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.SecureRandom;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
 

//import sun.misc.BASE64Decoder;
//import sun.misc.BASE64Encoder;

/**
 * DES加密 解密算法
 * @author zhangdi
 *
 */
public class DesUtil {

    private final static String DES = "DES";
    private final static String ENCODE = "UTF-8";
    private final static String defaultKey = "sqluckydefaultKeyval";

    /**
     * 使用 默认key 加密
     * @param data 待加密数据
     * @return
     * @throws Exception
     */
    public static String encrypt(String data) {
        byte[] bt;
        String strs = "";
		try {
			bt = encrypt(data.getBytes(ENCODE), defaultKey.getBytes(ENCODE));
		    strs = Base64.getEncoder().encodeToString(bt );
		} catch (Exception e) {
			e.printStackTrace();
		}
       
        return strs;
    }

    /**
     * 使用 默认key 解密
     * @param data 待解密数据
     * @return
     * @throws IOException
     * @throws Exception
     */
    public static String decrypt(String data) {
    	String rs = "";
        if (data == null)
            return rs;
        byte[] buf =  Base64.getDecoder().decode(data);
        
        byte[] bt;
		try {
			bt = decrypt(buf, defaultKey.getBytes(ENCODE));
			rs =  new String(bt, ENCODE);
		} catch (Exception e) {
			e.printStackTrace();
		}  
		return rs;
    }

    /**
     * Description 根据键值进行加密
     * @param data 待加密数据
     * @param key 密钥
     * @return
     * @throws Exception
     */
    public static String encrypt(String data, String key)   {
    	if(key == null || "".equals(key)) {
    		key = defaultKey;
    	}
    	String strs = "";
        byte[] bt;
		try {
			bt = encrypt(data.getBytes(ENCODE), key.getBytes(ENCODE));
		    strs = Base64.getEncoder().encodeToString(bt );
		} catch (Exception e) {
			e.printStackTrace();
		}
       
        return strs;
    }

    /**
     * 根据键值进行解密
     * @param data 待解密数据
     * @param key    密钥
     * @return
     * @throws IOException
     * @throws Exception
     */
    public static String decrypt(String data, String key) throws IOException,
            Exception {
        if (data == null)
            return null;
        if(key == null || "".equals(key)) {
    		key = defaultKey;
    	}
        byte[] buf =  Base64.getDecoder().decode(data);
        byte[] bt = decrypt(buf, key.getBytes(ENCODE));
        return new String(bt, ENCODE);
    }

    /**
     * Description 根据键值进行加密
     * 
     * @param data
     * @param key
     *            加密键byte数组
     * @return
     * @throws Exception
     */
    private static byte[] encrypt(byte[] data, byte[] key) throws Exception {
        // 生成一个可信任的随机数源
        SecureRandom sr = new SecureRandom();
        // 从原始密钥数据创建DESKeySpec对象
        DESKeySpec dks = new DESKeySpec(key);
        // 创建一个密钥工厂，然后用它把DESKeySpec转换成SecretKey对象
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(DES);
        SecretKey securekey = keyFactory.generateSecret(dks);
        // Cipher对象实际完成加密操作
        Cipher cipher = Cipher.getInstance(DES);
        // 用密钥初始化Cipher对象
        cipher.init(Cipher.ENCRYPT_MODE, securekey, sr);
        return cipher.doFinal(data);
    }

    /**
     * Description 根据键值进行解密
     * 
     * @param data
     * @param key 加密键byte数组
     * @return
     * @throws Exception
     */
    private static byte[] decrypt(byte[] data, byte[] key) throws Exception {
        // 生成一个可信任的随机数源
        SecureRandom sr = new SecureRandom();

        // 从原始密钥数据创建DESKeySpec对象
        DESKeySpec dks = new DESKeySpec(key);

        // 创建一个密钥工厂，然后用它把DESKeySpec转换成SecretKey对象
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(DES);
        SecretKey securekey = keyFactory.generateSecret(dks);

        // Cipher对象实际完成解密操作
        Cipher cipher = Cipher.getInstance(DES);

        // 用密钥初始化Cipher对象
        cipher.init(Cipher.DECRYPT_MODE, securekey, sr);

        return cipher.doFinal(data);
    }
    
    public static void main(String[] args){
        String data = "我是需要被加密的数据";
        // 这里的key长度必须大于8位
        String key ="12345678";
        System.out.println("初始数据===>"+data);
        try {
            System.out.println();
            String eStr = encrypt(data, key);
            System.out.println(">>>>>>>>>>根据预定秘钥加密后：" + eStr);
            String dStr =  decrypt(eStr, key);
            System.out.println(">>>>>>>>>>根据预定秘钥解密后：" + dStr);

            System.out.println("==========================================");
            String encrypt = encrypt(data);
            System.out.println(">>>>>>>>>>默认秘钥加密后："+encrypt);
            System.out.println(">>>>>>>>>>默认秘钥解密后："+decrypt(encrypt));
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
}