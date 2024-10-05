package greed.application.exceptions.code;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ImageErrorCode implements ErrorCode {

    FILE_SIZE_EXCEED("I001", "File size exceeds the 5MB limit."),
    INVALID_FILE_EXTENSION("I002", "Invalid file extension");

    private final String code;
    private final String message;
}
