package ru.ccfit.nsu.chernovskaya;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import ru.ccfit.nsu.chernovskaya.receiver.Receiver;
import ru.ccfit.nsu.chernovskaya.receiver.ReceiverInterface;
import ru.ccfit.nsu.chernovskaya.sender.Sender;
import ru.ccfit.nsu.chernovskaya.sender.SenderInterface;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Log4j2
@RequiredArgsConstructor
public class Server implements Runnable {
    private static final String CONFIG_FILE = "../src/main/resources/server.config";
    private final int port;

    private final ReceiverInterface receiverInterface;
    private final SenderInterface senderInterface;

    /**
     *  В бесконечном цикле ожидает подключение клиента. При подключении клиента
     *  запускает поток @ClientHandler для обработки сообщений с помощью ThreadPool.
     */
    @Override
    public void run() {

        Properties properties = new Properties();
        try (FileInputStream fis = new FileInputStream(CONFIG_FILE)) {
            properties.load(fis);
        } catch (IOException ex) {
            log.error("Error with read config file " + ex.getMessage());
        }

        int THREAD_COUNT = Integer.parseInt(properties.getProperty("THREAD.COUNT"));
        int BACKLOG= Integer.parseInt(properties.getProperty("BACKLOG"));
        String DIRECTORY =  properties.getProperty("DIRECTORY");

        ExecutorService clientThreadPool = Executors.newFixedThreadPool(THREAD_COUNT);

        if (new File(properties.getProperty("DIRECTORY")).mkdir()) {
            log.debug("Upload directory is created");
        } else {
            log.debug("Upload directory already exists");
        }

        try (ServerSocket serverSocket = new ServerSocket(port, BACKLOG)) {
            log.info("Server port: {}, address: {}", port, serverSocket.getInetAddress().getHostAddress());

            while (true) {
                Socket clientSocket = serverSocket.accept();
                log.info("New connection with {}:{}",
                        clientSocket.getInetAddress().getHostAddress(),
                        clientSocket.getPort());

                clientThreadPool.execute(new ClientHandler(clientSocket, DIRECTORY,
                        receiverInterface, senderInterface));
            }
        } catch (IOException e) {
            log.error("Server socket error", e);
        }
    }
}