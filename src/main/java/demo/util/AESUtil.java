package demo.util;

import org.apache.commons.codec.binary.Hex;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.security.NoSuchAlgorithmException;

/**
 * 提供AES加解密操作
 * 版本号不要以明文存放在文件中，以防被人为修改
 * 也不需要太过复杂的加密，仅仅只是用来保证不要被简单的修改
 */
public class AESUtil {

    private static Key getKkey() throws NoSuchAlgorithmException {
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(256);
        byte[] byteKey = "1234567890123456".getBytes();

        Key key = new SecretKeySpec(byteKey,"AES");
        return key;
    }

    /**
     * 加密字符串
     * @param str
     * @return
     */
    public static String encode(String str) {
        try {
            Key key = getKkey();
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, key);
            byte[] result = cipher.doFinal(str.getBytes());
            return Hex.encodeHexString(result);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 解密字符串
     * @param str
     * @return
     */
    public static String decode(String str){
        try {
            Key key = getKkey();
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, key);

            byte[] result = Hex.decodeHex(str);
            result = cipher.doFinal(result);
            return new String(result);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
}
