package ru.nsu.ccfit.chernovskaya;

import com.google.devtools.common.options.OptionsParser;
import lombok.extern.log4j.Log4j2;
import ru.nsu.ccfit.chernovskaya.multicast.MulticastManager;
import ru.nsu.ccfit.chernovskaya.parser.ConsoleArguments;
import ru.nsu.ccfit.chernovskaya.parser.ConsoleArgumentsData;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Collections;

@Log4j2
public class Main {

    public static void main(String[] args) {
        OptionsParser parser = OptionsParser.newOptionsParser(ConsoleArgumentsData.class);
        parser.parseAndExitUponError(args);

        ConsoleArguments options = new ConsoleArguments(parser.getOptions(ConsoleArgumentsData.class));

        int groupPort;
        InetAddress groupAddress;

        try {
            groupPort = options.getPort();
            groupAddress = options.getAddress();

        } catch (UnknownHostException e) {
            printUsage(parser);
            return;
        }

        if (options.getPort() < 0 || !groupAddress.isMulticastAddress()) {
            printUsage(parser);
            return;
        }
        log.info("Starting server at" + groupAddress + " groupPor = "+ groupPort);
        MulticastManager multicastManager = new MulticastManager(groupPort, groupAddress);
        multicastManager.manage();

    }

    private static void printUsage(OptionsParser parser) {
        System.out.println("Usage: java -jar server.jar OPTIONS");
        System.out.println(parser.describeOptions(Collections.<String, String>emptyMap(), OptionsParser.HelpVerbosity.LONG));
    }
}
