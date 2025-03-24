package org.example;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Scanner;

public class Client implements Runnable {
    private final Socket socket;
    private String username;
    //private BufferedReader reader;
    private BufferedWriter writer;

    public Client(Socket socket, String username) {
        this.socket = socket;
        this.username = username;
        try {
            writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            // reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            System.out.println(e);
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        sendMessages();
        // listenToMessages();
    }

    private void sendMessages() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    writer.write(username + " has entered the chat");
                    writer.newLine();
                    writer.flush();

                    Scanner scanner = new Scanner(System.in);
                    while (socket.isConnected()) {
                        String message = scanner.nextLine();
                        writer.write(username + "(" + socket.getInetAddress() + "): " + message);
                        writer.newLine();
                        writer.flush();
                    }
                } catch (IOException e) {
                    closeEverything();
                }
            }
        }).start();
    }

    private void closeEverything() {
        try {
            writer.close();
            socket.close();
        } catch (IOException e) {

        }
    }

    /*

    private void listenToMessages() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (socket.isConnected()) {
                    try {
                        String message = reader.readLine();
                        System.out.println(message);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }
*/
}
