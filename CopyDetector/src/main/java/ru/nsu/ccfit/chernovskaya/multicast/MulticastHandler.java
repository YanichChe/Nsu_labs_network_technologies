package ru.nsu.ccfit.chernovskaya.multicast;

import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.Level;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Log4j2
public class MulticastHandler implements Runnable {

    private static final long DELIVERY_TIME = 7000;

    private final Set<MemberData> membersCopy = new HashSet<>();
    private final Map<MemberData, Long> timeOfReceived = new HashMap<>();

    public MulticastHandler() {
    }

    private void deleteMembers() {
        for (MemberData memberData : membersCopy) {
            if ((System.currentTimeMillis() - timeOfReceived.get(memberData)) > DELIVERY_TIME) {
                membersCopy.remove(memberData);
                timeOfReceived.remove(memberData);
                log.info("Delete old member: " + memberData.toString());
            }
        }

    }

    public void addNewMember(MemberData memberData) {
        if (membersCopy.contains(memberData))
            log.info("Update member: " + memberData.toString());
        else
            log.info("New member: " + memberData.toString());

        membersCopy.add(memberData);
        timeOfReceived.put(memberData, System.currentTimeMillis() + DELIVERY_TIME);
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
