package org.example;

import java.io.IOException;
import java.net.*;
import java.util.Scanner;

public class Main {
    public static void main(String[] args){

        Scanner scanner = new Scanner(System.in);

        System.out.println("Enter your IP");
        String ip = scanner.nextLine();
        InetAddress address = null;
        try {
            address = InetAddress.getByName(ip);
        }
        catch (IOException e) {
            System.out.println("Address is incorrect");
            return;
        }

        System.out.println("Enter your name");
        String username = scanner.nextLine();

        try {
            ServerSocket serverSocket = new ServerSocket(5000,50, address);
            Server server = new Server(serverSocket);
            Thread serverThread = new Thread(server);
            serverThread.start();

            UDPClientDiscovery clientDiscovery = new UDPClientDiscovery(new DatagramSocket(8888, address));
            clientDiscovery.perform();

            DatagramSocket datagramSocket = new DatagramSocket(8888, address);
            Thread udpServerThread = new Thread(new UDPServerDiscovery(datagramSocket));
            udpServerThread.start();

            Socket socket = new Socket(address, 5000);
            Client client = new Client(socket, username);
            Thread clientThread = new Thread(client);
            clientThread.start();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}