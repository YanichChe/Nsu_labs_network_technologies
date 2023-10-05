package ru.ccfit.nsu.chernovskaya;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import ru.ccfit.nsu.chernovskaya.session_info.SessionInfo;
import ru.ccfit.nsu.chernovskaya.util.BytesToHex;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Properties;

/**
 * Поток транспортировки файла на сервер.
 */
@Log4j2
@RequiredArgsConstructor
public class ClientTransferConnection implements Runnable {

    private static final String CONFIG_FILE = "../src/main/resources/client_connection.config";

    private final Socket transferSocket;
    private final File file;
    private final SessionInfo sessionInfo;

    /**
     * Отправляет файл на сервер, шифруя алгоритмом, указанном в конфигурационном файле.
     * После завершения устанавливает поле fileHashSum в классе @SessionInf.
     */
    @Override
    public void run() {

        log.info("Starting sending...");

        Properties properties = new Properties();

        try (FileInputStream fis = new FileInputStream(CONFIG_FILE)) {
            properties.load(fis);
        } catch (IOException ex) {
            log.error("Error with read config file");
        }

        String ALGORITHM = properties.getProperty("ALGORITHM");
        int BUFFER_SIZE = Integer.parseInt(properties.getProperty("BUFFER.SIZE"));

        MessageDigest md;
        try {
            md = MessageDigest.getInstance(ALGORITHM);
        } catch (NoSuchAlgorithmException e) {
            log.error("Algorithm error, not such algorithm: " + ALGORITHM);
            return;
        }

        // Send file
        try (transferSocket; OutputStream outputStream = transferSocket.getOutputStream();
             FileInputStream fileInputStream = new FileInputStream(file)) {

            int readBytes;
            byte[] buffer = new byte[BUFFER_SIZE];
            while ((readBytes = fileInputStream.read(buffer)) > 0) {
                //log.debug("Sending data: {}", new String(buffer));
                outputStream.write(buffer, 0, readBytes);
                md.update(buffer, 0, readBytes);
            }
        } catch (IOException e) {
            log.error("Transfer socket error", e);
        }

        sessionInfo.setFileHashSum(BytesToHex.convertToString(md));
    }
}
