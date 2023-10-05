package ru.ccfit.nsu.chernovskaya;

import com.google.devtools.common.options.OptionsParser;
import ru.ccfit.nsu.chernovskaya.cl_parser.ConsoleArguments;
import ru.ccfit.nsu.chernovskaya.cl_parser.ConsoleArgumentsData;
import ru.ccfit.nsu.chernovskaya.receiver.Receiver;
import ru.ccfit.nsu.chernovskaya.sender.Sender;

public class ServerApplication {
    public static void main(String[] args) {

        OptionsParser parser = OptionsParser.newOptionsParser(ConsoleArgumentsData.class);
        parser.parseAndExitUponError(args);

        ConsoleArguments options = new ConsoleArguments(parser.getOptions(ConsoleArgumentsData.class));

        Thread server = new Thread(new Server(options.getPort(), new Receiver(), new Sender()));
        server.start();
    }
}