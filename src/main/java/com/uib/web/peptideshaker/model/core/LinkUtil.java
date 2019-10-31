package com.uib.web.peptideshaker.model.core;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Base64;
import java.util.Date;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

/**
 *
 * @author y-mok
 */
public class LinkUtil {

    private Random rand = new Random((new Date()).getTime());
    private static final String UNICODE_FORMAT = "UTF8";
    public static final String DESEDE_ENCRYPTION_SCHEME = "DESede";
    private KeySpec ks;
    private SecretKeyFactory skf;
    private Cipher cipher;
    byte[] arrayBytes;
    private String myEncryptionKey;
    private String myEncryptionScheme;
    SecretKey key;
    private final BASE64Decoder decoder = new BASE64Decoder();
    private final BASE64Encoder encoder = new BASE64Encoder();

    ;

    public LinkUtil() {
        try {
            myEncryptionKey = "ThisIsSpartaThisIsSparta";
            myEncryptionScheme = DESEDE_ENCRYPTION_SCHEME;
            arrayBytes = myEncryptionKey.getBytes(UNICODE_FORMAT);
            ks = new DESedeKeySpec(arrayBytes);
            try {
                skf = SecretKeyFactory.getInstance(myEncryptionScheme);
            } catch (NoSuchAlgorithmException ex) {
                Logger.getLogger(LinkUtil.class.getName()).log(Level.SEVERE, null, ex);
            }
            cipher = Cipher.getInstance(myEncryptionScheme);
            try {
                key = skf.generateSecret(ks);
            } catch (InvalidKeySpecException ex) {
                Logger.getLogger(LinkUtil.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (Exception ex) {
            Logger.getLogger(LinkUtil.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public String encrypt(String str) {

        str = inencrypt(str);

        byte[] salt = new byte[8];

        rand.nextBytes(salt);

        return encoder.encode(salt) + encoder.encode(str.getBytes());
    }

    public String decrypt(String encstr) {

        if (encstr.length() > 12) {

            String cipher = encstr.substring(12);

            try {

                return indecrypt(new String(decoder.decodeBuffer(cipher)));

            } catch (IOException e) {

                //  throw new InvalidImplementationException(
                //Fail
            }

        }

        return null;
    }

    public String inencrypt(String unencryptedString) {
        String encryptedString = null;
        try {
            cipher.init(Cipher.ENCRYPT_MODE, key);
            byte[] plainText = unencryptedString.getBytes(UNICODE_FORMAT);
            byte[] encryptedText = cipher.doFinal(plainText);
            encryptedString = new String(Base64.getEncoder().encode(encryptedText));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return encryptedString;
    }

    public String indecrypt(String encryptedString) {
        String decryptedText = null;
        try {
            cipher.init(Cipher.DECRYPT_MODE, key);
            byte[] encryptedText = Base64.getDecoder().decode(encryptedString);
            byte[] plainText = cipher.doFinal(encryptedText);
            decryptedText = new String(plainText);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return decryptedText;
    }
}
