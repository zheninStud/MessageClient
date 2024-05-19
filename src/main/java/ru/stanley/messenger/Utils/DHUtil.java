package ru.stanley.messenger.Utils;

import javax.crypto.KeyAgreement;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

public class DHUtil {

    public static KeyPair initDH() throws NoSuchAlgorithmException, NoSuchProviderException {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("DH", "BC");
        keyPairGenerator.initialize(2048);
        return keyPairGenerator.generateKeyPair();
    }

    public static PublicKey setOtherClientPublicKey(byte[] keyBytes) throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchProviderException {
        KeyFactory keyFactory = KeyFactory.getInstance("DH", "BC");
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
        return keyFactory.generatePublic(keySpec);
    }

    public static SecretKey generateSharedSecret(KeyPair keyPair, PublicKey otherClientPublicKey) throws NoSuchAlgorithmException, InvalidKeyException, NoSuchProviderException {
        KeyAgreement keyAgreement = KeyAgreement.getInstance("DH", "BC");
        keyAgreement.init(keyPair.getPrivate());
        keyAgreement.doPhase(otherClientPublicKey, true);
        byte[] sharedSecretBytes = keyAgreement.generateSecret();
        return new SecretKeySpec(sharedSecretBytes, "Kuznyechik");
    }

    public static PublicKey convertBytesToPublicKey(byte[] keyBytes) throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchProviderException {
        KeyFactory keyFactory = KeyFactory.getInstance("DH", "BC");
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
        return keyFactory.generatePublic(keySpec);
    }

    public static PrivateKey convertBytesToPrivateKey(byte[] keyBytes) throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchProviderException {
        KeyFactory keyFactory = KeyFactory.getInstance("DH", "BC");
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
        return keyFactory.generatePrivate(keySpec);
    }

}
