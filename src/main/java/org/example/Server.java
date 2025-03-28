package org.example;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server implements Runnable {

    private final ServerSocket serverSocket;

    public Server(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    @Override
    public void run() {
        System.out.println("\u001B[32m" + "Server is running..." + "\u001B[0m");
        while (!serverSocket.isClosed()) {
            Socket socket = null;
            try {
                socket = serverSocket.accept();
                // System.out.println("new user entered the chat");
                new Thread(new ClientHandler(socket)).start();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }


    private static class ClientHandler implements Runnable {
        private Socket socket;
        private BufferedWriter writer;
        private BufferedReader reader;
        static ArrayList<ClientHandler> clientHandlers = new ArrayList<>();

        private final Object obj = new Object();

        public ClientHandler(Socket socket) {
            try {
                this.socket = socket;
                writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                clientHandlers.add(this);
                // MessageHandler.sendMessages(socket);
            } catch (IOException e) {

            }
        }

        @Override
        public void run() {
            String message;

            while (!socket.isClosed() && socket.isConnected()) {
                try {
                    message = reader.readLine();
                    if (message != null)
                        broadcastMessage(message);
                } catch (IOException e) {
                    removeClientHandler(this);
                    closeEverything(this);
                }
            }
        }

        private void broadcastMessage(String message) {
            /*if (message.startsWith("/<<previous messages>>/")) {
                if (MessageHandler.historyIsReceived)
                    return;
                synchronized (obj) {
                    if (MessageHandler.historyIsReceived)
                        return;
                    String[] messages = message.substring("/<<previous messages>>/".length()).split("\\|");
                    for (String m : messages) {
                        if (m.equals(" ") || m.isEmpty())
                            continue;
                        try {
                            writer.write(message);
                            writer.newLine();
                            writer.flush();
                            MessageHandler.messages.add(message);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    MessageHandler.historyIsReceived = true;
                }
                return;
            }*/
            for (ClientHandler clientHandler : clientHandlers) {
                try {
                    if (!clientHandler.equals(this)) {
                        clientHandler.writer.write(message);
                        clientHandler.writer.newLine();
                        clientHandler.writer.flush();
                    }
                } catch (IOException e) {
                    removeClientHandler(clientHandler);
                    closeEverything(clientHandler);
                }
            }
        }

        private void closeEverything(ClientHandler clientHandler) {
            try {
                clientHandler.writer.close();
                clientHandler.reader.close();
                clientHandler.socket.close();
            } catch (IOException e) {

            }
        }

        private void removeClientHandler(ClientHandler clientHandler) {
            clientHandlers.remove(clientHandler);
            closeEverything(clientHandler);
        }
    }


}
