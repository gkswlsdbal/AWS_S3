package greed.util;

import greed.application.exceptions.CustomException;
import greed.application.exceptions.code.ImageErrorCode;

public class FileUtils {

    public static String getFileExtension(String filename) throws CustomException {
        if (filename == null || !filename.contains(".")) {
            throw new CustomException(ImageErrorCode.INVALID_FILE_EXTENSION);
        }
        return filename.substring(filename.lastIndexOf('.') + 1).toLowerCase();
    }

}
