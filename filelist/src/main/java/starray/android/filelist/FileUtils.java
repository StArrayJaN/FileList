package starray.android.filelist;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

public class FileUtils {

    public static void deleteFolder(File file) throws IOException {
        if (file.isDirectory()) {
            Files.walkFileTree(file.toPath(), new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    file.toFile().delete();
                    return super.visitFile(file, attrs);
                }

                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                    dir.toFile().delete();
                    return super.postVisitDirectory(dir, exc);
                }
            });
        }
    }

    public static void deleteFile(File file) throws IOException {
        if (file.isFile()) {
            file.delete();
        }
    }

    public static void delete(File file) throws IOException {
        if (file.isDirectory()) {
            deleteFolder(file);
        } else {
            deleteFile(file);
        }
    }

    public static void delete(List<File> files) throws IOException {
        for (File file:files) {
            delete(file);
        }
    }
    public static List<File> getAllSubDir(String dir) throws IOException {
        final List<File> files = new ArrayList<>();
        Path path = Paths.get(dir);
        FileVisitor<Path> fileVisitor = new FileVisitor<Path>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
                files.add(dir.toFile());
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFileFailed(Path file, IOException exc) {
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) {

                return FileVisitResult.CONTINUE;
            }
        };
        Files.walkFileTree(path, fileVisitor);
        return files;
    }
    public static List<File> getAllFile(File file) throws IOException {
        final List<File> files = new ArrayList<>();
        Path path = file.toPath();
        FileVisitor<Path> fileVisitor = new FileVisitor<Path>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
                files.add(dir.toFile());
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                files.add(file.toFile());
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFileFailed(Path file, IOException exc) {
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) {

                return FileVisitResult.CONTINUE;
            }
        };
        Files.walkFileTree(path, fileVisitor);
        return files;
    }
}
