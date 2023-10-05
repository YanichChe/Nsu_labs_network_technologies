package ru.ccfit.nsu.chernovskaya;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import ru.ccfit.nsu.chernovskaya.exceptions.IncorrectHeader;
import ru.ccfit.nsu.chernovskaya.exceptions.IncorrectJSONStructure;
import ru.ccfit.nsu.chernovskaya.exceptions.SessionException;
import ru.ccfit.nsu.chernovskaya.message.*;
import ru.ccfit.nsu.chernovskaya.receiver.ReceiverInterface;
import ru.ccfit.nsu.chernovskaya.sender.SenderInterface;
import ru.ccfit.nsu.chernovskaya.session_info.SessionInfo;

/**
 * Класс который принимает и отправляет сообщения серверу.
 */
@Log4j2
@RequiredArgsConstructor
public class Client implements Runnable {

    private final InetAddress address;
    private final int port;
    private final File file;

    private final SenderInterface senderInterface;
    private final ReceiverInterface receiverInterface;

    private final int clientPort;

    /**
     * Обрабатывает сообщения от сервера, к которому подлючен.
     * Алгоритм протокола:
     * 1. Отправляет сообщение о полной информации о пересылаемом файле.
     * 2. Ожидает сообщение об успехе получения полной информации о файле.
     * 3. Отправляет сообщение о старте транспортировки файла.
     * 4. Транспортирует файл.
     * 5. Отправляет сообщение о завершении отправки сообщения.
     * 6. Ожидает сообщение о успехе передаче файла.
     */
    @Override
    public void run() {
        try (Socket socket = new Socket(address, port);
             DataInputStream inputStream = new DataInputStream(socket.getInputStream());
             DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream())) {

            log.info("Start connection: {}:{}", address.getHostAddress(), port);

            // Session info setter
            SessionInfo sessionInfo = new SessionInfo();
            sessionInfo.setFileName(file.getName());
            sessionInfo.setFileSize(file.length());
            //

            senderInterface.sendMessage(new FileInfoRequest(file.getName(), file.length()), outputStream);
            waitReceiveSuccessResponse(inputStream);

            senderInterface.sendMessage(new StartTransferRequest(clientPort), outputStream);
            transferFile(sessionInfo);
            senderInterface.sendMessage(new EndTransferRequest(sessionInfo.getFileHashSum()), outputStream);

            waitReceiveSuccessResponse(inputStream);
            log.info("{} transferred successfully", file.getAbsolutePath());
        } catch (SessionException | IncorrectJSONStructure | IncorrectHeader e) {
            log.error("Session error", e);
        } catch (IOException e) {
            log.error("Socket error", e);
        }
    }

    /**
     * Инициализирует транспортировку файла на сервер. В цикле ожидает подключение сервера, которому подключался,
     * блокирует остальные подключения. Создает новый поток для отправки файла на сервер и ожидает его завершения.
     *
     * @param sessionInfo информация о файле.
     * @throws SocketException
     */
    private void transferFile(@NonNull SessionInfo sessionInfo) throws SocketException {
        try (ServerSocket serverSocket = new ServerSocket(clientPort)) {
            Socket transferSocket;
            do {
                transferSocket = serverSocket.accept();
                log.debug(transferSocket.getInetAddress());
            } while (!transferSocket.getInetAddress().equals(address));
            log.info("Created connection with {}:{} to transfer {}",
                    transferSocket.getInetAddress().getHostAddress(), transferSocket.getPort(),
                    file.getAbsolutePath());

            Thread transferThread = new Thread(new ClientTransferConnection(transferSocket, file, sessionInfo));
            transferThread.start();
            try {
                transferThread.join();
            } catch (InterruptedException e) {
                log.error("Transfer thread error", e);
            }
        } catch (IOException e) {
            log.error(e.getMessage());
            throw new SocketException(e.getMessage());
        }
    }

    /**
     * Ожидает успешного ответа от сервера. В противном случае создаюся определенные исключения.
     *
     * @param inputStream поток отправки сообещний.
     * @throws IOException
     * @throws SessionException
     * @throws IncorrectJSONStructure
     * @throws IncorrectHeader
     */
    private void waitReceiveSuccessResponse(@NonNull DataInputStream inputStream)
            throws IOException, SessionException, IncorrectJSONStructure, IncorrectHeader {
        Message message = receiverInterface.receiveMessage(inputStream);
        if (message instanceof ErrorResponse) {
            throw new SessionException(((ErrorResponse) message).getErrorMessage());
        } else if (!(message instanceof SuccessResponse)) {
            throw new SessionException("Internal server error");
        }
    }
}
