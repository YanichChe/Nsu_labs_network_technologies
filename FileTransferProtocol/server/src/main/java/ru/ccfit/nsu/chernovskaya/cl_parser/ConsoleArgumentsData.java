package ru.ccfit.nsu.chernovskaya.cl_parser;

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
            help = "The port of the server",
            category = "startup",
            defaultValue = "0"
    )
    public int port;

}
