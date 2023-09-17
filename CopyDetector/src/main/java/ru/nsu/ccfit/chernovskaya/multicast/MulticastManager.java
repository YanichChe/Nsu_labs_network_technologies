package ru.nsu.ccfit.chernovskaya.multicast;

import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.Level;

import java.net.InetAddress;

@Log4j2
public class MulticastManager {
    private final int groupPort;
    private final InetAddress groupAddress;

    public MulticastManager(int groupPort, InetAddress groupAddress) {
        this.groupPort = groupPort;
        this.groupAddress = groupAddress;
    }

    public void manage() {
        MulticastSender multicastSender = new MulticastSender(groupPort, groupAddress);
        MulticastHandler multicastHandler = new MulticastHandler();
        MulticastReceiver multicastReceiver = new MulticastReceiver(groupPort, groupAddress, multicastHandler);

        Thread sender = new Thread(multicastSender);
        Thread receiver = new Thread(multicastReceiver);
        Thread handler = new Thread(multicastHandler);

        sender.start();
        receiver.start();
        handler.start();

        try {
            sender.join();
            receiver.join();
            handler.join();
        }
        catch (InterruptedException e) {
            log.atLevel(Level.ERROR).log(e.getMessage());
        }
    }
}
