package org.example;

import java.io.IOException;
import java.net.*;

public class UDPClientDiscovery {

    private InetAddress address = InetAddress.getByName("255.255.255.255");
    private final int PORT = 8888;
    private DatagramSocket socket;

    public UDPClientDiscovery(DatagramSocket datagramSocket) throws SocketException, UnknownHostException {
        socket = datagramSocket;
        socket.setBroadcast(true);
        socket.setSoTimeout(3000);
    }

    public void perform() {
        try {
            DatagramPacket sendingPacket = new DatagramPacket(new byte[1024], 1024, address, PORT);
            System.out.println("Send request to find servers...");
            socket.send(sendingPacket);

            long send_time = System.currentTimeMillis();

            while (send_time - System.currentTimeMillis() < 3000) {
                DatagramPacket receivingPacket = new DatagramPacket(new byte[1024], 1024);
                socket.receive(receivingPacket);
                InetAddress addr = socket.getLocalAddress();
                if (receivingPacket.getAddress().equals(addr))
                    continue;
                try {
                    Socket remote = new Socket(receivingPacket.getAddress(), 5000);
                    new Thread(new ClientListener(remote)).start();
                    System.out.println("Client: Connect with " + receivingPacket.getAddress());
                } catch (SocketException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            System.out.println("Time out exited");
        } finally {
            socket.close();
        }
    }
}