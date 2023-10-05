package ru.ccfit.nsu.chernovskaya.receiver;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.experimental.UtilityClass;
import lombok.extern.log4j.Log4j2;
import ru.ccfit.nsu.chernovskaya.exceptions.IncorrectHeader;
import ru.ccfit.nsu.chernovskaya.exceptions.IncorrectJSONStructure;
import ru.ccfit.nsu.chernovskaya.message.Message;

import java.io.DataInputStream;
import java.io.IOException;

@Log4j2
public class Receiver implements ReceiverInterface{
    @Override
    public <T extends Message> T receiveMessage(DataInputStream inputStream)
            throws IncorrectHeader, IncorrectJSONStructure, IOException {

        String[] receivedMessage = inputStream.readUTF().split("\n");
        String className = receivedMessage[0];
        String json = receivedMessage[1];

        log.debug("Received: {}", receivedMessage[1]);

        Message message;
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            message = objectMapper.readValue(json, Message.class);
            //noinspection unchecked
            return (T) objectMapper.convertValue(message, Class.forName(className));
        } catch (JsonProcessingException | IllegalArgumentException e) {
            log.error("JSON mapping error" + e.getMessage());
            throw new IncorrectJSONStructure("Incorrect json structure" + e.getMessage());
        } catch (ClassNotFoundException e) {
            log.error("Message header not exist error: " + className);
            throw new IncorrectHeader("Not such class name:" + className);
        }
    }
}
