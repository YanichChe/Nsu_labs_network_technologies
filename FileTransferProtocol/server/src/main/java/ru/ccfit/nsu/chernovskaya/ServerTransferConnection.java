package ru.ccfit.nsu.chernovskaya;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import ru.ccfit.nsu.chernovskaya.session_info.SessionInfo;
import ru.ccfit.nsu.chernovskaya.util.BytesToHex;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Properties;

import static java.lang.Thread.sleep;

@Log4j2
@RequiredArgsConstructor
public class ServerTransferConnection implements Runnable {

    private static final String CONFIG_FILE = "../src/main/resources/transfer_connection.config";

    private final InetAddress address;
    private final Integer port;
    private final SessionInfo sessionInfo;

    private boolean stop = false;

    /**
     *
     */
    @Override
    public void run() {

        log.info("Transferring ....");
        Properties properties = new Properties();

        try (FileInputStream fis = new FileInputStream(CONFIG_FILE)) {
            properties.load(fis);
        } catch (IOException ex) {
            log.error("Error with read config file");
        }

        String DIRECTORY = properties.getProperty("DIRECTORY");
        String ALGORITHM = properties.getProperty("ALGORITHM");
        int BUFFER_SIZE = Integer.parseInt(properties.getProperty("BUFFER.SIZE"));
        int TIMEOUT = Integer.parseInt(properties.getProperty("TIMEOUT"));

        MessageDigest md;
        File file;
        Thread progressBar;

        try {
            progressBar = new Thread(new ProgressBar(TIMEOUT));
            progressBar.start();

            file = new File(DIRECTORY, sessionInfo.getFileName());
            md = MessageDigest.getInstance(ALGORITHM);

        } catch (NoSuchAlgorithmException e) {
            log.error("Algorithm error, not such algorithm: " + ALGORITHM);
            return;
        }

        try (Socket transferSocket = new Socket(address, port);
             InputStream clientInputStream = transferSocket.getInputStream();
             FileOutputStream fileOutputStream = new FileOutputStream(file)) {

            log.info("Connection with {}:{}", address.getHostAddress(), port);

            byte[] buffer = new byte[BUFFER_SIZE];
            int receivedBytes;
            while ((receivedBytes = clientInputStream.read(buffer)) > 0) {
                fileOutputStream.write(buffer, 0, receivedBytes);
                md.update(buffer, 0, receivedBytes);
                sessionInfo.setCurrentBytes(sessionInfo.getCurrentBytes() + receivedBytes);
            }

        } catch (IOException e) {
            log.error("Transfer file error" + e.getMessage());
            progressBar.interrupt();
            return;
        }
        progressBar.interrupt();
        stop = progressBar.isInterrupted();
        sessionInfo.setFileHashSum(BytesToHex.convertToString(md));
    }

    /**
     * Поток отвечающий за вывод скорости передачи файла в консоль.
     */
    private class ProgressBar implements Runnable {

        private final long startTime = System.currentTimeMillis();
        private long previousTime = System.currentTimeMillis();
        private long previousReceivedBytes = 0;

        private final int TIMEOUT;

        public ProgressBar(int timeout) {
            TIMEOUT = timeout;
        }

        /**
         *  Выводит скорость передачи файла раз в TIMEOUT.
         */
        @Override
        public void run() {

            while (!stop) {
                float instantSpeed = (float) (sessionInfo.getCurrentBytes() - previousReceivedBytes) * 1000 /
                        (System.currentTimeMillis() - previousTime);
                float averageSpeed = (float) sessionInfo.getCurrentBytes() * 1000 /
                        (System.currentTimeMillis() - startTime);

                if ((System.currentTimeMillis() - startTime) != 0) log.info("{}: {}, {} bytes/sec", sessionInfo.getFileName(),
                        instantSpeed, averageSpeed);

                previousTime = System.currentTimeMillis();
                previousReceivedBytes = sessionInfo.getCurrentBytes();
                try {
                    sleep(TIMEOUT);
                } catch (InterruptedException e) {
                    log.info(e.getMessage());
                }
            }
        }
    }
}