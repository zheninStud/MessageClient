package ru.stanley.messenger.Utils;

import javax.crypto.KeyAgreement;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class DHUtil {

    public static KeyPair initDH() throws NoSuchAlgorithmException, NoSuchProviderException {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("DH", "BC");
        keyPairGenerator.initialize(2048);
        return keyPairGenerator.generateKeyPair();
    }

    public static SecretKey generateSharedSecret(PrivateKey privateKey, PublicKey otherClientPublicKey) throws NoSuchAlgorithmException, InvalidKeyException, NoSuchProviderException {
        KeyAgreement keyAgreement = KeyAgreement.getInstance("DH", "BC");
        keyAgreement.init(privateKey);
        keyAgreement.doPhase(otherClientPublicKey, true);
        byte[] sharedSecretBytes = keyAgreement.generateSecret();
        return new SecretKeySpec(sharedSecretBytes, "Kuznyechik");
    }

    public static PublicKey convertBytesToPublicKey(String key) throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchProviderException {
        byte[] keyBytes = Base64.getDecoder().decode(key);
        KeyFactory keyFactory = KeyFactory.getInstance("DH", "BC");
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
        return keyFactory.generatePublic(keySpec);
    }

    public static PrivateKey convertBytesToPrivateKey(String key) throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchProviderException {
        byte[] keyBytes = Base64.getDecoder().decode(key);
        KeyFactory keyFactory = KeyFactory.getInstance("DH", "BC");
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
        return keyFactory.generatePrivate(keySpec);
    }

    public static SecretKey convertStringToSecretKey(String keyString) {
        byte[] keyBytes = Base64.getDecoder().decode(keyString);
        return new SecretKeySpec(keyBytes, "Kuznyechik");
    }

    public static String keyToString(java.security.Key key) {
        return Base64.getEncoder().encodeToString(key.getEncoded());
    }

}
