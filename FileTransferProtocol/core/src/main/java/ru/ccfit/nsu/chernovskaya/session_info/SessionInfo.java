package ru.ccfit.nsu.chernovskaya.session_info;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class SessionInfo {
    private String fileName = null;
    private long fileSize = 0;
    private String fileHashSum = null;
    private long currentBytes = 0;
}
