package web.practicafinal.models.helpers;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import web.practicafinal.utils.CustomLogger;
import web.practicafinal.utils.CypherUtils;

/**
 *
 * @author Alex
 */
public class CardHelper {
    
    private static String cardKey = "";
    
    public static void loadCardKey() {
        
        // Para cargar una clave, esta debe estar almacenada en un lugar externo
        // No es recomendable usar una string constante o recuperarla desde un fichero
        // Lo correcto es recuperarla desde un servicio externo y que la clave solo exista en memoria
        // De esta manera si un atacante penetra en el sistema, será prácticamente imposible que la pueda ver
        
        // En este caso se usa una String constante, pero no es lo correcto
        // Debe estar en formato Base64
        cardKey = "UcWn0viVu7SXliHLF3rB7dX5vkFYep2a";
    }
    
    public static String encryptCvv(String cvv) {
        try {
            return CypherUtils.encrypt(cvv, cardKey);
        } catch (Exception ex) {
            CustomLogger.errorThrow(CardHelper.class.getName(), ex);
        }
        return "";
    }
    
    public static String decryptCvv(String encyptedCvv) {
        try {
            return CypherUtils.decrypt(encyptedCvv, cardKey);
        } catch (Exception ex) {
            CustomLogger.errorThrow(CardHelper.class.getName(), ex);
        }
        return "";
    }
    
}
