package ru.nsu.ccfit.chernovskaya.parser;

import com.google.devtools.common.options.Option;
import com.google.devtools.common.options.OptionsBase;

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
            help = "The port.",
            category = "startup",
            defaultValue = "8089"
    )
    public int port;

    @Option(
            name = "adress",
            abbrev = 'a',
            help = "The group multicast address",
            category = "startup",
            defaultValue = "127.0.0.0"
    )
    public String address;
}
