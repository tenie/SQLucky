package net.tenie.lib.tools;

import java.security.NoSuchAlgorithmException;
import java.security.Security;

import javax.crypto.Cipher;

public class UnlimitedCryptoPoliciesCheck {
	
	public static String getInfo() throws NoSuchAlgorithmException {
        // Security.setProperty("crypto.policy", "limited"); // uncomment to switch to limited crypto policies
		String val = "Check for unlimited crypto policies \n";
	    val += "Java version: " + Runtime.version() +"\n";
        //Security.setProperty("crypto.policy", "limited"); // muss ganz am anfang gesetzt werden !
	    val += "restricted cryptography: " + restrictedCryptography() + " Notice: 'false' means unlimited policies \n"; // false mean unlimited crypto
	    val += "Security properties: " + Security.getProperty("crypto.policy")+"\n";
        int maxKeyLen = Cipher.getMaxAllowedKeyLength("AES");
        val += "Max AES key length = " + maxKeyLen+"\n";
        
        return val;
    }

	
	  public static boolean restrictedCryptography() {
	        try {
	            return Cipher.getMaxAllowedKeyLength("AES/CBC/PKCS5Padding") < Integer.MAX_VALUE;
	        } catch (final NoSuchAlgorithmException e) {
	            throw new IllegalStateException("The transform \"AES/CBC/PKCS5Padding\" is not available (the availability of this algorithm is mandatory for Java SE implementations)", e);
	        }
	    }
}
