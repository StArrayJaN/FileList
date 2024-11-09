package starray.android.filelist;

import android.util.Log;

import java.io.FileInputStream;
import java.io.IOException;

public class Extensions {

    public static boolean isCPPLanguage(String filename) {
        return switch (getExtension(filename)) {
            case ".cc", ".cpp", ".cxx" -> true;
            default -> false;
        };
    }

    public static boolean isCLanguage(String filename) {
        return getExtension(filename).equals("c");
    }

    public static boolean isJavaLanguage(String filename) {
        return getExtension(filename).equals("java");
    }

    public static boolean isCAndCPPHeader(String filename) {
        return switch (getExtension(filename)) {
            case "h", "hpp" -> true;
            default -> false;
        };
    }

    public static boolean isELFBinary(String filename) {
        try (FileInputStream fis = new FileInputStream(filename)) {
            byte[] buffer = new byte[4];
            fis.read(buffer);
            // Check for the ELF magic number: 0x7F followed by "ELF" in ASCII
            if (buffer[0] == (byte) 0x7F && buffer[1] == 'E' && buffer[2] == 'L' && buffer[3] == 'F') {
                return true;
            }
        } catch (IOException e) {
            return false;
        }
        return false;
    }

    public static boolean isTextFile(String filename) {
        if (isCAndCPPHeader(filename) || isCLanguage(filename) ||
                isCPPLanguage(filename) || isJavaLanguage(filename)) {
            return true;
        }
        return switch (getExtension(filename)) {
            case "txt", "md", "json", "xml",
                 "log", "cs", "sh", "bash" -> true;
            default ->false;
        };
    }

    public static boolean isImageFile(String filename) {
        return switch (getExtension(filename)) {
            case "png", "jpg", "jpeg", "gif", "bmp", "tiff" -> true;
            default -> false;
        };
    }

    public static boolean isAudioFile(String filename) {
        return switch (getExtension(filename)) {
            case "mp3", "wav", "flac", "aac", "ogg" -> true;
            default -> false;
        };
    }

    public static boolean isVideoFile(String filename) {
        return switch (getExtension(filename)) {
            case "mp4", "avi", "mov", "mkv", "flv", "3gp" -> true;
            default -> false;
        };
    }

    public static boolean isCompressedFile(String filename) {
        return switch (getExtension(filename)) {
            case "zip", "rar", "7z", "gz", "bz2", "xz", "tar" -> true;
            default -> false;
        };
    }

    public static boolean isApk(String filename) {
        return getExtension(filename).equals("apk");
    }

    /**
     * 获取文件扩展名(.txt,.java,.cpp等)
     *
     * @param filename 文件名
     * @return {@link String} 扩展名
     */
    public static String getExtension(String filename) {
        String[] fileNames = filename.split("\\.");
        Log.i(Extensions.class.getName(),"input:" + filename + ",return:" + fileNames[fileNames.length - 1]);
        return fileNames[fileNames.length - 1];
    }

    public static boolean isNTBinary(String filename) {
        return filename.endsWith(".exe") || filename.endsWith(".dll");
    }

    /**
     * 获取文件类型
     *
     * @param filename 文件名
     * @return {@link FileType} 返回值描述
     */
    public static FileType getFileType(String filename) {
        if (isCLanguage(filename)) {
            return FileType.C;
        }
        if (isCPPLanguage(filename)) {
            return FileType.CPP;
        }
        if (isJavaLanguage(filename)) {
            return FileType.JAVA;
        }
        if (isTextFile(filename)) {
            return FileType.TEXT;
        }
        if (isImageFile(filename)) {
            return FileType.IMAGE;
        }
        if (isCompressedFile(filename)) {
            return FileType.COMPRESSED;
        }
        if (isApk(filename)) {
            return FileType.APK;
        }
        if (isCAndCPPHeader(filename)) {
            return FileType.C_AND_CPP_HEADER;
        }
        if (isNTBinary(filename)) {
            return FileType.NT_BINARY;
        }
        if (isELFBinary(filename)) {
            return FileType.ELF_BINARY;
        }
        if (isAudioFile(filename)) {
            return FileType.AUDIO;
        }
        if (isVideoFile(filename)) {
            return FileType.VIDEO;
        }
        return FileType.UNKNOWN;
    }

    /**
     * 文件类型枚举
     */
    protected enum FileType {
        TEXT, IMAGE, COMPRESSED, APK,
        C, CPP, JAVA, C_AND_CPP_HEADER,
        NT_BINARY, ELF_BINARY, VIDEO, AUDIO, UNKNOWN
    }
}
