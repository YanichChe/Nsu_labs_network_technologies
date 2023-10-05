package ru.ccfit.nsu.chernovskaya.message;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class StartTransferRequest extends Message {
    private int port;
}

