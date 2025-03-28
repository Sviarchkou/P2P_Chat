package org.example;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class MessageHandler {

    public static volatile boolean historyIsReceived = false;
    public static List<String> messages = Collections.synchronizedList(new ArrayList<>());

    public static void sendMessages(Socket socket) throws IOException {
        BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        StringBuilder sb = new StringBuilder("/<<previous messages>>/");
        for (String str : messages) {
            sb.append(str).append("|");
        }
        try{
            out.write(sb.toString());
            out.newLine();
            out.flush();
        }catch (IOException e){
            System.out.println("Cannot send messages");
        }
    }
}
