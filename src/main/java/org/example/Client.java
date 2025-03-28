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
    private BufferedReader reader;
    private BufferedWriter writer;

    public Client(Socket socket, String username) {
        this.socket = socket;
        this.username = username;
        try {
            writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            Runtime.getRuntime().addShutdownHook(new Thread()
            {
                @Override
                public void run()
                {
                    String exitMessage = "\u001B[33m" + username + " has left the chat" + "\u001B[0m";
                    try {
                        writer.write(exitMessage);
                        writer.newLine();
                        writer.flush();
                    } catch (IOException e) {
                        System.out.println("Error in sanding final message");
                    }
                }
            });
        } catch (IOException e) {
            System.out.println(e);
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        System.out.println("To left the chat enter '<<exit>>'");
        sendMessages();
        listenToMessages();
    }

    private void sendMessages() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String entranceMessage = "\u001B[33m" + username + " has entered the chat" + "\u001B[0m";
                    writer.write(entranceMessage);
                    writer.newLine();
                    writer.flush();
                    //MessageHandler.messages.add(entranceMessage);
                    Scanner scanner = new Scanner(System.in);
                    while (socket.isConnected()) {
                        String message = "";
                        try {
                            message = scanner.nextLine();
                            if (message.equals("<<exit>>"))
                                System.exit(0);
                        } catch (Exception e) {
                            continue;
                        }
                        String outputString = username + "(" + socket.getInetAddress() + "): " + message;
                        writer.write(outputString);
                        writer.newLine();
                        writer.flush();
                        //MessageHandler.messages.add(outputString);
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
}
