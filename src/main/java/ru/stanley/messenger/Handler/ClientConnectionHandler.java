package ru.stanley.messenger.Handler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientConnectionHandler {

    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private Thread receiveThread;
    private boolean running;

    public void connect(String serverAddress, int port) {
        try {
            socket = new Socket(serverAddress, port);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            System.out.println("Connected to the server");

            running = true;
            receiveThread = new Thread(this::receiveMessages);
            receiveThread.start();
        } catch (IOException e) {
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

    public void sendMessage(String message) {
        out.println(message);
    }

    public void receiveMessages() {
        try {
            String serverResponse;
            while (running && (serverResponse = in.readLine()) != null) {
                System.out.println("Received message from server: " + serverResponse);

                // Дополнительная обработка
            }
        } catch (IOException e) {
            if (running) {
                e.printStackTrace();
            }
        }
    }


}

