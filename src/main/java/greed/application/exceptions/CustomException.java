package greed.application.exceptions;

import greed.application.exceptions.code.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CustomException extends Exception {
    private final ErrorCode errorCode;
}
