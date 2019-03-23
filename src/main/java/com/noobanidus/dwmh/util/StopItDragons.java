package com.noobanidus.dwmh.util;

import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.message.MessageFactory;

public class StopItDragons extends Logger {
    /**
     * The constructor.
     *
     * @param context        The LoggerContext this Logger is associated with.
     * @param name           The name of the Logger.
     * @param messageFactory The message factory.
     */
    protected StopItDragons(LoggerContext context, String name, MessageFactory messageFactory) {
        super(context, name, messageFactory);
    }

    public static StopItDragons stopIt(org.apache.logging.log4j.Logger log) {
        return new StopItDragons(((Logger) log).getContext(), log.getName(), log.getMessageFactory());
    }

    @Override
    public void info(String message) {
    }
}
