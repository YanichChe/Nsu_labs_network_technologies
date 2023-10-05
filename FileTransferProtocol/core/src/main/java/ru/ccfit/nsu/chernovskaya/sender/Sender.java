package ru.ccfit.nsu.chernovskaya.sender;


import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.experimental.UtilityClass;
import lombok.extern.log4j.Log4j2;
import ru.ccfit.nsu.chernovskaya.exceptions.SessionException;
import ru.ccfit.nsu.chernovskaya.message.Message;

import java.io.DataOutputStream;
import java.io.IOException;

@Log4j2
public class Sender implements SenderInterface{
    @Override
    public void sendMessage(Message message, DataOutputStream outputStream) throws SessionException {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            String json = objectMapper.writeValueAsString(message);
            log.debug("Sending data ... {}", json);
            outputStream.writeUTF(message.getClass().getName() + "\n" + json);
        } catch (IOException e) {
            log.error("JSON mapping error" + e.getMessage());

            throw new SessionException("Message format is not valid");
        }
    }
}
