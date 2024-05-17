package ru.stanley.messenger.Handler;

import ru.stanley.messenger.Models.Message;

import javax.net.ssl.*;
import java.io.*;
import java.security.*;
import java.security.cert.CertificateException;


public class ClientConnectionHandler {

    private SSLSocket socket;
    private PrintWriter out;
    private BufferedReader in;
    private Thread receiveThread;
    private boolean running;

    public void connect(String serverAddress, int port) {
        try {
            SSLContext sslContext = SSLContext.getInstance("TLS");
            KeyStore keyStore = KeyStore.getInstance("JKS");
            char[] password = "testserver".toCharArray();
            keyStore.load(new FileInputStream("server_keystore.jks"), password);
            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init(keyStore);

            sslContext.init(null, trustManagerFactory.getTrustManagers(), new SecureRandom());

            SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();
            socket = (SSLSocket) sslSocketFactory.createSocket(serverAddress, port);

            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            System.out.println("Connected to the server");

            running = true;
            receiveThread = new Thread(this::receiveMessages);
            receiveThread.start();
        } catch (IOException | NoSuchAlgorithmException | KeyManagementException | KeyStoreException | CertificateException   e) {
            e.printStackTrace();
        }
    }

    public void disconnect() {
        try {
            running = false;
            receiveThread.join();
            if (socket != null && !socket.isClosed()) {
                socket.close();
                System.out.println("Disconnected from the server");
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    // Метод для отправки объекта Message
    public void sendMessage(Message message) {
        out.println(message.toJSON());
    }

    // Метод для приема сообщений от сервера
    public void receiveMessages() {
        try {
            String serverResponse;
            while (running && (serverResponse = in.readLine()) != null) {
                System.out.println("Received message from server: " + serverResponse);

                Message message = Message.fromJSON(serverResponse);

                handleMessage(message);
            }
        } catch (IOException e) {
            if (running) {
                e.printStackTrace();
            }
        }
    }

    // Метод для обработки сообщений от сервера
    private void handleMessage(Message message) {
        String messageType = message.getType();

        switch (messageType) {
            case "AUTH_SUCCESS":
                // Обработка успешной авторизации
                break;
            case "AUTH_FAIL":
                // Обработка неудачной авторизации
                break;
            case "REGISTER_SUCCESS":

                break;
            case "REGISTER_FAIL":

                break;

        }
    }
}

