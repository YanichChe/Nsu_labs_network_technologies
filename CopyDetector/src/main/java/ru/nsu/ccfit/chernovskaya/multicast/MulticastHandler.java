package ru.nsu.ccfit.chernovskaya.multicast;

import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.Level;

import java.util.*;

@Log4j2
public class MulticastHandler implements Runnable {

    private static final long DELIVERY_TIME = 7000;

    private final Set<MemberData> membersCopy = new HashSet<>();
    private final Map<MemberData, Long> timeOfReceived = new HashMap<>();

    private final Object monitor = new Object();

    public MulticastHandler() {
    }

    private void deleteMembers() {
        synchronized (monitor) {
            Iterator<MemberData> iterator = membersCopy.iterator();
            while (iterator.hasNext()) {
                MemberData element = iterator.next();
                if ((System.currentTimeMillis() - timeOfReceived.get(element)) > DELIVERY_TIME) {
                    iterator.remove();
                    timeOfReceived.remove(element);
                    log.info("Delete old member: " + element.toString());
                }
            }
        }
    }

    public void addNewMember(MemberData memberData) {
        synchronized (monitor) {
            if (membersCopy.contains(memberData))
                log.info("Update member: " + memberData.toString());
            else
                log.info("New member: " + memberData.toString());

            membersCopy.add(memberData);
            timeOfReceived.put(memberData, System.currentTimeMillis() + DELIVERY_TIME);
        }
    }

    private void printMembers() {
        for (MemberData memberData : membersCopy) {
            System.out.println(
                    "ip = " + memberData.inetAddress() +
                    " port = " + memberData.port() +
                    " pid = " + memberData.pid());
        }
    }

    @Override
    public void run() {
        while (true) {
            deleteMembers();
            try {
                Thread.sleep(DELIVERY_TIME);
            }
            catch (InterruptedException e) {
                log.atLevel(Level.ERROR).log(e.getMessage());
            }
        }
    }
}
