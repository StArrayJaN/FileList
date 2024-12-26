package starray.android.filelist;

import androidx.annotation.NonNull;

import java.io.File;

public class FileItemInfo {

    /**
     * 文件名
     */
    private String name;
    /**
     * 文件绝对路径
     */
    private final String path;
    /**
     * {@link File}文件实例
     */
    private final File file;

    private boolean allowLongPress;

    private boolean isSelected;

    /**
     * 从文件获取{@link FileItemInfo}实例
     *
     * @param file {@link File}
     * @return {@link FileItemInfo} 获取到的{@link FileItemInfo}实例
     */
    public static FileItemInfo formFile(File file) {
        return new FileItemInfo(file.getName(), file.getAbsolutePath(), true);
    }

    public FileItemInfo(String name, String path, boolean allowLongPress) {
        this.name = name;
        this.path = path;
        this.file = new File(path);
        this.allowLongPress = allowLongPress;
    }

    public FileItemInfo getParent() {
        return new FileItemInfo(file.getParentFile().getName(), file.getParentFile().getAbsolutePath(), true);
    }

    public void setAllowLongPress(boolean allowLongPress) {
        this.allowLongPress = allowLongPress;
    }

    public boolean isAllowLongPress() {
        return allowLongPress;
    }

    /** 获取文件名 */
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /** 获取文件绝对路径 */
    public String getPath() {
        return path;
    }

    /** 获取{@link File}文件实例 */
    public File getFile() {
        return file;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof FileItemInfo) {
            return this.path.equals(((FileItemInfo) o).getPath());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return file.hashCode();
    }

    @NonNull
    @Override
    public String toString() {
        return "FileInfo{" +
                "name='" + name + '\'' +
                ", path='" + path + '\'' +
                ", allowLongPress=" + allowLongPress +
                '}';
    }
}
