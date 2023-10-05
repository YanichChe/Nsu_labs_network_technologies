package ru.ccfit.nsu.chernovskaya;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import ru.ccfit.nsu.chernovskaya.exceptions.IncorrectHeader;
import ru.ccfit.nsu.chernovskaya.exceptions.IncorrectJSONStructure;
import ru.ccfit.nsu.chernovskaya.message.*;
import ru.ccfit.nsu.chernovskaya.receiver.ReceiverInterface;
import ru.ccfit.nsu.chernovskaya.sender.SenderInterface;
import ru.ccfit.nsu.chernovskaya.session_info.SessionInfo;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.util.Objects;

import ru.ccfit.nsu.chernovskaya.exceptions.SessionException;

/**
 * Обработчик клиента.
 */
@Log4j2
@RequiredArgsConstructor
public class ClientHandler implements Runnable {

    private final Socket socket;
    private final String directoryName;

    private final ReceiverInterface receiverInterface;
    private final SenderInterface senderInterface;

    /**
     * Обмен сообщениями с клиентами.
     * Алгоритм протокола:
     * 1. Получает сообщение информацию об пересылаемом файле.
     * 2. Отправляет сообщение об успехе получения сообщения об информации о файле.
     * 3. Получает сообщение о начале транспортировки файла.
     * 4. Запускаетт поток, в котором осуществляется передача файлов.
     * 5. Проверяет на индентичность отправляемый и полученный файл.
     * 6. В случае индентичности, отправляет сообщение об успехе передаче файла.
     *
     */
    @Override
    public void run() {

        SessionInfo sessionInfo = new SessionInfo();

        try (socket;
             DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
             DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream())) {

            try {
                log.info("Starting ClientHandler ...");
                FileInfoRequest fileInfoRequest = receiverInterface.receiveMessage(dataInputStream);
                String fileName = chooseFileName(fileInfoRequest);
                log.info("File name: " + fileName);

                sessionInfo.setFileName(fileName);
                sessionInfo.setFileSize(fileInfoRequest.getFileSize());

                senderInterface.sendMessage(new SuccessResponse(), dataOutputStream);

                StartTransferRequest startTransferRequest = receiverInterface.receiveMessage(dataInputStream);
                log.info("get start transfer request");
                Thread transferConnection = new Thread(new ServerTransferConnection(
                        socket.getInetAddress(), startTransferRequest.getPort(), sessionInfo));
                transferConnection.start();

                transferConnection.join();
                EndTransferRequest endTransferRequest = receiverInterface.receiveMessage(dataInputStream);

                checkTransferredFile(sessionInfo, endTransferRequest.getTotalHashSum());
                senderInterface.sendMessage(new SuccessResponse(), dataOutputStream);
                transferConnection.join();
                log.info("{} transferred successfully", sessionInfo.getFileName());
            } catch (IncorrectHeader | IncorrectJSONStructure | SessionException e) {
                log.error("Session error", e);
                senderInterface.sendMessage(new ErrorResponse(e.getMessage()), dataOutputStream);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        } catch (IOException | SessionException e) {
            log.error("Control socket error", e);
        }
    }

    /**
     * Изменение имени файла в случае, если в директории такой уже существует.
     *
     * @param fileInfoRequest информация об изначальном файле
     * @return новое имя файла
     * @throws IOException возможная ошибка при создании нового файла
     */
    private String chooseFileName(@NonNull FileInfoRequest fileInfoRequest) throws IOException {

        File file = new File(directoryName, fileInfoRequest.getFileName());
        String fileName = fileInfoRequest.getFileName();
        int i = 0;
        while (!file.createNewFile()) {
            file = new File(directoryName, fileName + (i++));
        }

        return fileName;
    }

    /**
     * Проверка индентичности оригинального и полученного файла.
     *
     * @param sessionInfo информация о данной сессии
     * @param fileHashSum результат хэш суммы полученного файла
     * @throws IOException
     */
    private void checkTransferredFile(@NonNull SessionInfo sessionInfo, @NonNull String fileHashSum)
            throws IOException {
        if (sessionInfo.getFileSize() != sessionInfo.getCurrentBytes()
                || !Objects.equals(sessionInfo.getFileHashSum(), fileHashSum))
            throw new IOException("Original and transferred files are not the same\n" +
                    " count: " + sessionInfo.getFileHashSum()
            + ", get: " + fileHashSum);
    }
}
