package ru.nsu.ccfit.chernovskaya.multicast;

import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.Level;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

@Log4j2
public class MulticastReceiver implements Runnable {

    private final byte[] buf = new byte[256];
    private final InetAddress groupAddress;
    private final MulticastHandler multicastHandler;

    private final int port;

    public MulticastReceiver(int port, InetAddress groupAddress, MulticastHandler multicastHandler) {
        this.port = port;
        this.groupAddress = groupAddress;
        this.multicastHandler = multicastHandler;
    }

    @Override
    public void run() {
        try (MulticastSocket socket = new MulticastSocket(port)) {
            socket.joinGroup(groupAddress);

            while (true) {
                DatagramPacket packet = new DatagramPacket(buf, buf.length);
                socket.receive(packet);
                String message = new String(packet.getData(), 0, packet.getLength());

                log.info("Thread got the message: " +  message);

                String[] parts = message.split(" ");
                if (parts.length == 2 && parts[0].equals("password")) {
                    MemberData memberData = new MemberData(packet.getAddress(), packet.getPort(), parts[1]);
                    multicastHandler.addNewMember(memberData);
                }
            }

        } catch (IOException e) {
            log.atLevel(Level.ERROR).log(e.getMessage());
        }
    }
}
