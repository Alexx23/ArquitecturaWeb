package web.practicafinal.utils;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 *
 * @author Alex
 */
public class CypherUtils {
    
    // Obtener un nuevo vector de inicialización
    public static IvParameterSpec generateIv() {
        byte[] iv = new byte[16];
        new SecureRandom().nextBytes(iv);
        return new IvParameterSpec(iv);
    }
    
    // Cifrar String
    public static String encrypt(String string, String key) throws Exception {
        Cipher cypher = Cipher.getInstance("AES/CBC/PKCS5Padding");

        SecretKeySpec secureKey = new SecretKeySpec(key.getBytes(), "AES");

        // Generar un IV aleatorio
        SecureRandom random = new SecureRandom();
        byte[] iv = new byte[cypher.getBlockSize()];
        random.nextBytes(iv);

        IvParameterSpec ivParameter = new IvParameterSpec(iv);

        cypher.init(Cipher.ENCRYPT_MODE, secureKey, ivParameter);

        byte[] resultadoCifrado = cypher.doFinal(string.getBytes("UTF-8"));

        // Concatenar IV y resultado cifrado
        byte[] finalResult = new byte[ivParameter.getIV().length + resultadoCifrado.length];
        System.arraycopy(ivParameter.getIV(), 0, finalResult, 0, ivParameter.getIV().length);
        System.arraycopy(resultadoCifrado, 0, finalResult, ivParameter.getIV().length, resultadoCifrado.length);

        String base64 = Base64.getEncoder().encodeToString(finalResult);
        
        return base64;
    }
    
    // Descifrar String
    public static String decrypt(String encrytedBase64, String key) throws Exception {
        Cipher cypher = Cipher.getInstance("AES/CBC/PKCS5Padding");

        SecretKeySpec secureKey = new SecretKeySpec(key.getBytes(), "AES");
        
        // Decodificar la cadena Base64
        byte[] encyptedString = Base64.getDecoder().decode(encrytedBase64);

        // Obtener IV desde la cadena cifrada
        byte[] iv = new byte[cypher.getBlockSize()];
        System.arraycopy(encyptedString, 0, iv, 0, cypher.getBlockSize());

        IvParameterSpec ivParameter = new IvParameterSpec(iv);

        cypher.init(Cipher.DECRYPT_MODE, secureKey, ivParameter);

        byte[] result = cypher.doFinal(encyptedString, cypher.getBlockSize(), encyptedString.length - cypher.getBlockSize());

        return new String(result, "UTF-8");
    }
    
}
