package starray.android.filelist;

import android.os.Environment;

import androidx.annotation.NonNull;

import java.io.File;

public class FileInfo {
    private final String name;
    private final String path;
    private final File file;

    public static FileInfo formFile(File file) {
        return new FileInfo(file.getName(), file.getAbsolutePath());
    }

    public FileInfo(String name, String path) {
        this.name = name;
        this.path = path;
        this.file = new File(path);
    }

    public String getName() {
        return name;
    }

    public String getPath() {
        return path;
    }

    public File getFile() {
        return file;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof FileInfo) {
            return this.name.equals(((FileInfo) o).getName()) && this.path.equals(((FileInfo) o).getPath());
        }
        return false;
    }

    @NonNull
    @Override
    public String toString() {
        return "FileInfo{" +
                "name='" + name + '\'' +
                ", path='" + path + '\'' +
                '}';
    }
}
