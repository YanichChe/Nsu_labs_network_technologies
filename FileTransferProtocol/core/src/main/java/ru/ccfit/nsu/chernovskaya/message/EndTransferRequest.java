package ru.ccfit.nsu.chernovskaya.message;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class EndTransferRequest extends Message{
    private String totalHashSum;
}
