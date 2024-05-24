package ru.stanley.messenger.Utils;

import org.bouncycastle.crypto.engines.GOST3412_2015Engine;
import org.bouncycastle.crypto.modes.G3413CBCBlockCipher;
import org.bouncycastle.crypto.modes.G3413CTRBlockCipher;
import org.bouncycastle.crypto.paddings.PaddedBufferedBlockCipher;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

public class GOSTEncryptor {
    private static final int BLOCK_SIZE = 16;
    private final PaddedBufferedBlockCipher cipher;
    private final KeyParameter key;
    private final byte[] iv;

    public GOSTEncryptor(SecretKey secretKey) {
        GOST3412_2015Engine engine = new GOST3412_2015Engine();
        G3413CBCBlockCipher ctrCipher = new G3413CBCBlockCipher(engine);
        cipher = new PaddedBufferedBlockCipher(ctrCipher);
        key = new KeyParameter(secretKey.getEncoded());
        iv = generateIV();
    }

    public String encrypt(String plaintext) throws Exception {
        byte[] plaintextBytes = plaintext.getBytes(StandardCharsets.UTF_8);
        byte[] ciphertext = encrypt(plaintextBytes);
        return Base64.getEncoder().encodeToString(ciphertext);
    }

    public String decrypt(String ciphertextBase64) throws Exception {
        byte[] ciphertext = Base64.getDecoder().decode(ciphertextBase64);
        byte[] decryptedBytes = decrypt(ciphertext);
        return new String(decryptedBytes, StandardCharsets.UTF_8).trim();
    }

    private byte[] encrypt(byte[] plaintext) throws Exception {
        byte[] ciphertext = new byte[cipher.getOutputSize(plaintext.length)];

        cipher.init(true, new ParametersWithIV(key, iv));
        int len = cipher.processBytes(plaintext, 0, plaintext.length, ciphertext, 0);
        cipher.doFinal(ciphertext, len);

        return concatenate(iv, ciphertext);
    }

    private byte[] decrypt(byte[] ciphertext) throws Exception {
        byte[] iv = new byte[BLOCK_SIZE];
        System.arraycopy(ciphertext, 0, iv, 0, iv.length);
        byte[] actualCiphertext = new byte[ciphertext.length - iv.length];
        System.arraycopy(ciphertext, iv.length, actualCiphertext, 0, actualCiphertext.length);

        cipher.init(false, new ParametersWithIV(key, iv));
        byte[] decrypted = new byte[cipher.getOutputSize(actualCiphertext.length)];
        int len = cipher.processBytes(actualCiphertext, 0, actualCiphertext.length, decrypted, 0);
        cipher.doFinal(decrypted, len);

        return decrypted;
    }

    private byte[] generateIV() {
        byte[] iv = new byte[BLOCK_SIZE];
        new SecureRandom().nextBytes(iv);
        return iv;
    }

    private byte[] concatenate(byte[] iv, byte[] ciphertext) {
        byte[] result = new byte[iv.length + ciphertext.length];
        System.arraycopy(iv, 0, result, 0, iv.length);
        System.arraycopy(ciphertext, 0, result, iv.length, ciphertext.length);
        return result;
    }

}
