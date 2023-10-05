package ru.ccfit.nsu.chernovskaya.cl_parser;

import com.google.devtools.common.options.Option;
import com.google.devtools.common.options.OptionsBase;

import java.net.InetAddress;

public class ConsoleArgumentsData extends OptionsBase {
    public ConsoleArgumentsData() {
    }

    @Option(
            name = "help",
            abbrev = 'h',
            help = "Prints usage info.",
            defaultValue = "true"
    )
    public boolean help;

    @Option(
            name = "port",
            abbrev = 'p',
            help = "The port of the server",
            category = "startup",
            defaultValue = "0"
    )
    public int port;

    @Option(
            name = "client port",
            abbrev = 'c',
            help = "The port of the client",
            category = "startup",
            defaultValue = "0"
    )
    public int clientPort;

    @Option(
            name = "address",
            abbrev = 'a',
            help = "The address of the server",
            category = "startup",
            defaultValue = "0"
    )
    public String address;

    @Option(
            name = "filePath",
            abbrev = 'f',
            help = "The full file path",
            category = "startup",
            defaultValue = "0"
    )
    public String filePath;

}
