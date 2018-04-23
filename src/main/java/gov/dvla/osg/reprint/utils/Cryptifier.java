package gov.dvla.osg.reprint.utils;

import java.security.Key;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * A utility class that encrypts or decrypts a file.
 */
public class Cryptifier {

	static final Logger LOGGER = LogManager.getLogger();
	
    private static final String ALGORITHM = "AES";
    // uses a single key for encryption and decryption
    private static final String KEY = "JKDFJKJSVSNSDHFE";

    /**
     * @param inputBytes message to encrypt
     * @return encrypted message
     */
    public static byte[] encrypt(byte[] inputBytes) {
        return doCrypto(inputBytes, Cipher.ENCRYPT_MODE);
    }

    /**
     * @param inputBytes message to decrypt
     * @return decrypted message as plain text
     */
    public static byte[] decrypt(byte[] inputBytes) {
        return doCrypto(inputBytes, Cipher.DECRYPT_MODE);
    }

    /**
     * @param inputBytes message to transform
     * @param encryptMode type of transformation needed
     * @return transformed message
     */
    private static byte[] doCrypto(byte[] inputBytes, int encryptMode) {

        try {
            Key secretKey = new SecretKeySpec(KEY.getBytes(), ALGORITHM);
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            byte[] iv = new byte[cipher.getBlockSize()];
            IvParameterSpec ivParams = new IvParameterSpec(iv);
            cipher.init(encryptMode, secretKey, ivParams);
            
            return cipher.doFinal(inputBytes);

        } catch (Exception e) {
        	// as cipher is run before gui loads no message will be shown to the user
        	LOGGER.fatal(ExceptionUtils.getStackTrace(e));
            return null;
        }
    }
}