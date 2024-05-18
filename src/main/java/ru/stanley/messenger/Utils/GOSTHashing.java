package ru.stanley.messenger.Utils;

import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.digests.GOST3411_2012_256Digest;
import org.bouncycastle.util.encoders.Hex;
import org.json.JSONObject;
import ru.stanley.messenger.Handler.ClientConnectionHandler;
import ru.stanley.messenger.Messenger;

import java.security.SecureRandom;

public class GOSTHashing {

    private static final ClientConnectionHandler clientConnectionHandler = Messenger.getClientConnectionHandler();

    public static void requestGenerateSalt(String username) {
        MessageType messageType = MessageType.GET_SALT;
        JSONObject jsonMessage = messageType.createJsonObject();

        jsonMessage.getJSONObject("data").put("username", username);

        clientConnectionHandler.sendMessage(messageType.createMessage(jsonMessage));
    }

    public static byte[] generateSalt(int length) {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[length];
        random.nextBytes(salt);
        return salt;
    }

    public static byte[] computeHashWithSalt(String password, byte[] salt) {
        Digest digest = new GOST3411_2012_256Digest();
        byte[] passwordBytes = password.getBytes();
        byte[] combined = new byte[passwordBytes.length + salt.length];

        System.arraycopy(passwordBytes, 0, combined, 0, passwordBytes.length);
        System.arraycopy(salt, 0, combined, passwordBytes.length, salt.length);

        digest.update(combined, 0, combined.length);
        byte[] hash = new byte[digest.getDigestSize()];
        digest.doFinal(hash, 0);
        return hash;
    }

    public static String encodeSaltAndHash(byte[] salt, byte[] hash) {
        byte[] combined = new byte[salt.length + hash.length];
        System.arraycopy(salt, 0, combined, 0, salt.length);
        System.arraycopy(hash, 0, combined, salt.length, hash.length);
        return Hex.toHexString(combined);
    }
}
