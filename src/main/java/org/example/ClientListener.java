package org.example;

import java.io.*;
import java.net.Socket;

public class ClientListener implements Runnable {

    private final Socket socket;
    private BufferedReader reader;
    private Object obj = new Object();

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
//                if (message.startsWith("/<<previous messages>>/")){
//                    if (MessageHandler.historyIsReceived)
//                        continue;
//                    synchronized (obj) {
//                        if (MessageHandler.historyIsReceived)
//                            continue;
//                        String[] messages = message.substring("/<<previous messages>>/".length()).split("\\|");
//                        for (String m : messages) {
//                            if (m.equals(" ") || m.isEmpty())
//                                continue;
//                            System.out.println(m);
//                            MessageHandler.messages.add(m);
//                        }
//                        MessageHandler.historyIsReceived = true;
//                        continue;
//                    }
//                }
                System.out.println(message);
                //
                // MessageHandler.messages.add(message);
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
