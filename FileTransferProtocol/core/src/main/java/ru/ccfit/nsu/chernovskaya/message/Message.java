package ru.ccfit.nsu.chernovskaya.message;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = FileInfoRequest.class, name = "FileInfoRequest"),
        @JsonSubTypes.Type(value = StartTransferRequest.class, name = "StartTransferRequest"),
        @JsonSubTypes.Type(value = EndTransferRequest.class, name = "EndTransferRequest"),
        @JsonSubTypes.Type(value = SuccessResponse.class, name = "SuccessResponse"),
        @JsonSubTypes.Type(value = ErrorResponse.class, name = "ErrorResponse")
})
public abstract class Message {}
