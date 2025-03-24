package org.example;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class Server implements Runnable {

    private final ServerSocket serverSocket;
    private ArrayList<BufferedReader> readers = new ArrayList<>();
    private static ArrayList<BufferedWriter> writers;

    public Server(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    @Override
    public void run() {
        System.out.println("Server is running...");

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

        public ClientHandler(Socket socket) {
            try {
                this.socket = socket;
                writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                clientHandlers.add(this);
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
            for (ClientHandler clientHandler : clientHandlers) {
                try{
                     if(!clientHandler.equals(this)) {
                        clientHandler.writer.write(message);
                        clientHandler.writer.newLine();
                        clientHandler.writer.flush();
                     }
                }catch (IOException e) {
                    removeClientHandler(clientHandler);
                    closeEverything(clientHandler);
                }
            }
        }

        private void closeEverything(ClientHandler clientHandler){
            try {
                clientHandler.writer.close();
                clientHandler.reader.close();
                clientHandler.socket.close();
            } catch (IOException e) {

            }
        }

        private void removeClientHandler(ClientHandler clientHandler){
            clientHandlers.remove(clientHandler);
            closeEverything(clientHandler);
        }
    }


    /*
    private static class ClientHandler implements Runnable {
        private Socket socket;
        private BufferedWriter writer;


        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
                String message;
                while(socket.isConnected()) {
                    message = reader.readLine();
                    if (message != null)
                        System.out.println(message);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
*/

}
