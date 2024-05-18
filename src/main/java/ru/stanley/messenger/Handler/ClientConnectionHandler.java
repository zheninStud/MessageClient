package ru.stanley.messenger.Handler;

import javafx.application.Platform;
import ru.stanley.messenger.Controllers.AuthController;
import ru.stanley.messenger.Controllers.RegistryController;
import ru.stanley.messenger.Models.Message;
import ru.stanley.messenger.Models.User;
import ru.stanley.messenger.Utils.ControllerRegistry;
import ru.stanley.messenger.Utils.WindowsOpener;

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
    private AuthController authController;
    private RegistryController registryController;

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

    public void sendMessage(Message message) {
        out.println(message.toJSON());
    }

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
                User currentUser = new User(message.getData().getString("userId"), message.getData().getString("userName"),
                        message.getData().getString("email"), message.getData().getString("phone"));

                authController = (AuthController) ControllerRegistry.getController("AuthController");
                if (authController != null) {
                    Platform.runLater(() -> authController.openMainForm(currentUser));
                }
                break;
            case "AUTH_FAIL":
                authController = (AuthController) ControllerRegistry.getController("AuthController");
                if (authController != null) {
                    Platform.runLater(() -> authController.clearText());
                }
                break;
            case "REGISTER_SUCCESS":
                registryController = (RegistryController) ControllerRegistry.getController("RegistryController");
                if (registryController != null) {
                    Platform.runLater(() -> registryController.openAuthForm());
                }
                break;
            case "REGISTER_FAIL":
                registryController = (RegistryController) ControllerRegistry.getController("RegistryController");
                if (registryController != null) {
                    Platform.runLater(() -> registryController.checkAlert(message.getData().getString("alert")));
                }
                break;
            case "SET_SALT":
                String salt = message.getData().getString("salt");
                authController = (AuthController) ControllerRegistry.getController("AuthController");
                if (authController != null) {
                    Platform.runLater(() -> authController.authMessage(salt));
                }


        }
    }
}

