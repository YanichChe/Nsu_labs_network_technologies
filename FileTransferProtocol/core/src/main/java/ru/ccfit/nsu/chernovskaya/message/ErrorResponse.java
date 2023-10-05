package ru.ccfit.nsu.chernovskaya.message;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class ErrorResponse extends Message{
    private String errorMessage;
}
