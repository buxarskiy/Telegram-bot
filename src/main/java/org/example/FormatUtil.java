package org.example;

import java.util.logging.Formatter;
import java.util.logging.LogRecord;

public class
FormatUtil extends Formatter {
    @Override
    public String format(LogRecord record) {
        return record.getMessage();
    }
}
