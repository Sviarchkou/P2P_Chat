package org.example;

import java.io.*;
import java.net.Socket;

public class ClientListener implements Runnable {

    private final Socket socket;
    private BufferedReader reader;

    public ClientListener(Socket socket) {
        this.socket = socket;
        try {
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            System.out.println("Can not get reader from " + socket.getInetAddress());
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        while (!socket.isClosed() && socket.isConnected()) {
            try {
                String message = reader.readLine();
                System.out.println(message);
            } catch (IOException e) {
                closeEverything();
            }
        }
    }

    private void closeEverything(){
        try {
            reader.close();
            socket.close();
        } catch (IOException e) {

        }
    }
}
