package ru.nsu.ccfit.chernovskaya.multicast;

import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.Level;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.net.*;

@Log4j2
public class MulticastSender implements Runnable {

    private static final long TIME_OUT = 3000;

    private final InetAddress groupAddress;
    private final int groupPort;

    public MulticastSender(int groupPort, InetAddress groupAddress) {
        this.groupPort = groupPort;
        this.groupAddress = groupAddress;
    }

    @Override
    public void run() {
        try (DatagramSocket socket = new DatagramSocket()) {
            while (true) {
                try {
                    String message = "password" + " " + ManagementFactory.getRuntimeMXBean().getPid();
                    byte[] buf = message.getBytes();

                    DatagramPacket packet = new DatagramPacket(buf, buf.length, groupAddress, groupPort);
                    log.info("Thread sent the message: " + message);
                    socket.send(packet);

                    Thread.sleep(TIME_OUT);
                } catch (IOException | InterruptedException e) {
                    socket.close();
                }
            }
        } catch (IOException e) {
            log.atLevel(Level.ERROR).log(e.getMessage());
        }
    }
}
