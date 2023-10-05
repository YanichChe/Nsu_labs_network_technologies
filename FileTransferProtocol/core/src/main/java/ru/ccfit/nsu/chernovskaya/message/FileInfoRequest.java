package ru.ccfit.nsu.chernovskaya.message;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class FileInfoRequest extends Message{
    private String fileName;
    private long fileSize;
}
