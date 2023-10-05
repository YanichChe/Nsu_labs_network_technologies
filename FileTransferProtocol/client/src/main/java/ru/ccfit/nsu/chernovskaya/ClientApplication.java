package ru.ccfit.nsu.chernovskaya;

import com.google.devtools.common.options.OptionsParser;
import lombok.extern.log4j.Log4j2;
import ru.ccfit.nsu.chernovskaya.cl_parser.ConsoleArguments;
import ru.ccfit.nsu.chernovskaya.cl_parser.ConsoleArgumentsData;
import ru.ccfit.nsu.chernovskaya.receiver.Receiver;
import ru.ccfit.nsu.chernovskaya.sender.Sender;

import java.io.File;
import java.net.UnknownHostException;

@Log4j2
public class ClientApplication {
    public static void main(String[] args) throws UnknownHostException {
        OptionsParser parser = OptionsParser.newOptionsParser(ConsoleArgumentsData.class);
        parser.parseAndExitUponError(args);

        ConsoleArguments options = new ConsoleArguments(parser.getOptions(ConsoleArgumentsData.class));

        File file = new File(options.getFilePath());
        if (!file.exists()) {
            log.error("Not such file :  {}", options.getFilePath());
            return;
        }
        Thread clientThread = new Thread(new Client(options.getAddress(),
                                            options.getPort(), file,
                                            new Sender(),
                                            new Receiver(),
                                            options.getClientPort()));
        clientThread.start();
    }
}
