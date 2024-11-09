package starray.android.filelist;

import androidx.annotation.NonNull;

import java.io.File;

public class FileInfo {

    /**
     * 文件名
     */
    private final String name;
    /**
     * 文件绝对路径
     */
    private final String path;
    /**
     * {@link File}文件实例
     */
    private final File file;

    /**
     * 从文件获取{@link FileInfo}实例
     *
     * @param file {@link File}
     * @return {@link FileInfo} 获取到的{@link FileInfo}实例
     */
    public static FileInfo formFile(File file) {
        return new FileInfo(file.getName(), file.getAbsolutePath());
    }

    public FileInfo(String name, String path) {
        this.name = name;
        this.path = path;
        this.file = new File(path);
    }

    /** 获取文件名 */
    public String getName() {
        return name;
    }

    /** 获取文件绝对路径 */
    public String getPath() {
        return path;
    }

    /** 获取{@link File}文件实例 */
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
