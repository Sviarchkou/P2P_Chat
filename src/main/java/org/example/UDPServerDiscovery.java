package org.example;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;

public class UDPServerDiscovery implements Runnable {
    private final DatagramSocket socket;

    public UDPServerDiscovery(DatagramSocket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        while(!socket.isClosed()) {
            try {
                byte[] receivingDataBuffer = new byte[1024];
                byte[] sendingDataBuffer = new byte[1024];

                DatagramPacket inputPacket = new DatagramPacket(receivingDataBuffer, receivingDataBuffer.length);

                //System.out.println("Listen to port for requests...");
                socket.receive(inputPacket);

                InetAddress senderAddress = inputPacket.getAddress();
                int senderPort = inputPacket.getPort();
                Socket clientSocket = new Socket(senderAddress, 5000);
                //new Thread(new Client(clientSocket)).start();
                new Thread(new ClientListener(clientSocket)).start();
                // MessageHandler.sendMessages(clientSocket);
                System.out.println("\u001B[36m" + "Server: Connect with " + senderAddress + "\u001B[0m");

                DatagramPacket outputPacket = new DatagramPacket(sendingDataBuffer, sendingDataBuffer.length, senderAddress, senderPort);
                socket.send(outputPacket);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}