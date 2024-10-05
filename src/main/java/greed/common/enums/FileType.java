package greed.common.enums;

import greed.application.exceptions.CustomException;
import greed.application.exceptions.code.ImageErrorCode;

import java.util.Arrays;
import java.util.List;

public enum FileType {

    IMAGE(Arrays.asList("jpeg", "jpg", "png", "gif"));

    private final List<String> extensions;

    FileType(List<String> permissions) {
        this.extensions = permissions;
    }

    public static void validate(FileType type, String fileExtension) throws CustomException {
        type.extensions.stream().filter(ext -> ext.equalsIgnoreCase(fileExtension))
                .findAny()
                .orElseThrow(() -> new CustomException(ImageErrorCode.INVALID_FILE_EXTENSION));
    }


}
