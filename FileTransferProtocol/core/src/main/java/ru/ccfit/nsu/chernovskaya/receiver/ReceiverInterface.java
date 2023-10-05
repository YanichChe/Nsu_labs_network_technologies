package ru.ccfit.nsu.chernovskaya.receiver;

import ru.ccfit.nsu.chernovskaya.exceptions.IncorrectHeader;
import ru.ccfit.nsu.chernovskaya.exceptions.IncorrectJSONStructure;
import ru.ccfit.nsu.chernovskaya.message.Message;

import java.io.DataInputStream;
import java.io.IOException;

public interface ReceiverInterface {
    /**
     * Получение сообщения из потока приема сообщений.
     *
     * @param inputStream поток приема сообщений.
     * @param <T>         класс-тип получаемого сообщения, хранится в заголовке полученного сообщения
     * @return сообщение без заголовка
     * @throws IncorrectHeader        если не существует данного типа сообщений
     * @throws IncorrectJSONStructure ошибка при невозможности конвертации json в java класс
     * @throws IOException            возможная ошибка при чтения из потока данных
     */
    <T extends Message> T receiveMessage(DataInputStream inputStream)
            throws IncorrectHeader, IncorrectJSONStructure, IOException;
}
