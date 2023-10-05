package ru.ccfit.nsu.chernovskaya.sender;

import ru.ccfit.nsu.chernovskaya.exceptions.SessionException;
import ru.ccfit.nsu.chernovskaya.message.Message;

import java.io.DataOutputStream;

public interface SenderInterface {
    /**
     * Отправление сообщения в поток отправки сообщений.
     *
     * @param message           сообщение
     * @param outputStream      выходной поток
     */
    void sendMessage(Message message, DataOutputStream outputStream) throws SessionException;
}
