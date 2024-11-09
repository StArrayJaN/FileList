package starray.android.filelist;

import android.app.AlertDialog;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@SuppressWarnings("all")
public class FileListAdapter extends RecyclerView.Adapter<FileListAdapter.ViewHolder> {

	private List<FileInfo> files;
	private Context context;
	private FileInfo currentDir;
	private FileItemClickListener fileItemClickListener;

	private String newFileName = "";

	/**
	 * 构造函数
	 * @param context 上下文
	 * @param files 文件信息列表
	 */
	public FileListAdapter(Context context, List<FileInfo> files) {
		this.context = context;
		setCurrentDir(files.get(0).getFile().getParent());
	}

	/** 从位置获取{@link FileInfo}实例
	 *
	 * @param position 位置
	 * @return {@link FileInfo}实例
	 */
	public FileInfo getItem(int position) {
		return files.get(position);
	}

	public interface FileItemClickListener {
		void onFileItemClick(File file);
	}

	/**
	 * 设置文件点击监听器
	 * @param fileItemClickListener 文件点击监听器
	 */
	public void setFileItemClickListener(FileItemClickListener fileItemClickListener) {
		this.fileItemClickListener = fileItemClickListener;
	}

	@Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.file_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final FileInfo selectedFile = files.get(position);
		holder.fileName.setText(selectedFile.getName());
        if (selectedFile.getFile().isDirectory()) {
			holder.fileIcon.setImageResource(R.drawable.folder);
		} else {
			switch (Extensions.getFileType(selectedFile.getName())) {
				case IMAGE:
					holder.fileIcon.setImageBitmap(BitmapFactory.decodeFile(selectedFile.getFile().getAbsolutePath()));
					break;
				case TEXT:
					holder.fileIcon.setImageResource(R.drawable.file_type_txt);
					break;
				case UNKNOWN:
					holder.fileIcon.setImageResource(R.drawable.file_type_unknown);
					break;
				case JAVA:
					holder.fileIcon.setImageResource(R.drawable.file_type_java);
					break;
				case APK:
					PackageInfo pi = context.getPackageManager().getPackageArchiveInfo(selectedFile.getFile().getAbsolutePath(), 0);
					pi.applicationInfo.sourceDir = selectedFile.getFile().getAbsolutePath();
					pi.applicationInfo.publicSourceDir = selectedFile.getFile().getAbsolutePath();
					holder.fileIcon.setImageDrawable(pi.applicationInfo.loadIcon(context.getPackageManager()));
					break;
				case C:
					holder.fileIcon.setImageResource(R.drawable.file_type_c);
					break;
				case CPP:
		            holder.fileIcon.setImageResource(R.drawable.file_type_cpp);
					break;
				default:
					holder.fileIcon.setImageResource(R.drawable.file_type_unknown);
			}
		}

		long lastModifiedTime = selectedFile.getFile().lastModified();
        Date lastModifiedDate = new Date(lastModifiedTime);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String formattedDate = dateFormat.format(lastModifiedDate);

		String permission = "";
		if (selectedFile.getFile().canRead()) {
			permission = permission + "r";
		} else {
			permission = permission + "-";
		}
		if (selectedFile.getFile().canWrite()) {
			permission = permission + "w";
		} else {
			permission = permission + "-";
		}
		if (selectedFile.getFile().canExecute()) {
			permission = permission + "x";
		} else {
			permission = permission + "-";
		}

		holder.fileTime.setText(formattedDate + " " + permission);
		holder.itemView.setOnClickListener(v -> {
			if (fileItemClickListener != null) {
				fileItemClickListener.onFileItemClick(selectedFile.getFile());
			}
            if (selectedFile.getFile().getParentFile() == null) {
                return;
            }
            if (selectedFile.getFile().isDirectory()) {
                currentDir = selectedFile;
                List<File> newFiles = new ArrayList<>();
                newFiles.add(selectedFile.getFile().getParentFile());
				Log.i("FileListAdapter", "add:" + selectedFile.getFile().getAbsolutePath());
                File[] newDir = getFiles(selectedFile.getFile().getAbsolutePath());
				newFiles.addAll(Arrays.asList(newDir));
                setCurrentDir(currentDir.getFile().getAbsolutePath());
            }
        });
		holder.itemView.setOnLongClickListener(v -> {
			PopupMenu popupMenu = new PopupMenu(context, v);
			popupMenu.inflate(R.menu.file_operation_menu);
			Menu menu = popupMenu.getMenu();
			menu.findItem(R.id.create_file).setVisible(selectedFile.getFile().isDirectory());
			menu.findItem(R.id.create_folder).setVisible(selectedFile.getFile().isDirectory());
			popupMenu.setOnMenuItemClickListener(item -> {
				try {
					if (item.getItemId() == R.id.delete) {
						if (selectedFile.getFile().isDirectory()) {
							Files.walkFileTree(selectedFile.getFile().toPath(), new SimpleFileVisitor<Path>() {
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
						} else {
							selectedFile.getFile().delete();
						}
					} else if (item.getItemId() == R.id.create_folder) {
						File newFolder = new File(selectedFile.getFile(), "New Folder");
						newFolder.mkdirs();
					} else if (item.getItemId() == R.id.create_file) {
						File newFile = new File(selectedFile.getFile(), "New File.txt");
						if (!newFile.getParentFile().exists()) {
							newFile.getParentFile().mkdirs();
						}
						newFile.createNewFile();
					}
					refresh();
				} catch (IOException e) {

				}
				return true;
			});
			popupMenu.show();
            return true;
        });
    }

	/**
	 * 检查是否为无法访问的目录("/storage/emulated/0", "/storage/emulated", "/sdcard", "/storage", "/")
	 *
	 * @param file {@link File}实例
	 */
	public static boolean isRoot(File file) {
		String[] rootDirs = {"/storage/emulated/0", "/storage/emulated", "/sdcard", "/storage", "/"};
		for (String rootDir : rootDirs) {
			if (file.getAbsolutePath().equals(rootDir)) return true;
		}
		return false;
	}

	/** 刷新当前目录 */
	public void refresh(){
		setCurrentDir(getCurrentDir().getAbsolutePath());
	}

	/** 获取新文件名 */
	private void getNewName(String title) {
		AlertDialog dialog = new AlertDialog.Builder(context).create();
		dialog.setTitle(title);
		EditText editText = new EditText(context);
		dialog.addContentView(editText, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
		dialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK", (dialog1, which) -> {
			newFileName = editText.getText().toString();
		});
		dialog.show();
	}


	static File[] sortFileList(File[] files) {
		Arrays.sort(files, (o1, o2) -> {
            int typeCompare = o1.isFile() ? (o2.isFile() ? 0 : 1)
                    : (o2.isFile() ? -1 : 0);
            if (typeCompare != 0) {
                return typeCompare;
            } else {
                String name1 = o1.getName().toUpperCase();
                String name2 = o2.getName().toUpperCase();
                return name1.compareTo(name2);
            }
        });
		return files;
	}

	@Override
    public int getItemCount() {
        return files.size();
    }

	/** 获取当前目录 */
	public File getCurrentDir() {
		Log.i("FileListAdapter", "getCurrentDir:" + currentDir);
		return currentDir.getFile();
	}

	/**
	 * 获取并排序文件列表
	 * 顺序为:文件夹(.A-.z(名字开头为.的文件夹))，文件夹(A-z),文件(.A-.z(名字开头为.的文件))，文件(A-z)
	 * @param path 文件夹路径
	 * @return {@code File[]} 排序后的文件组
	 */
	static File[] getFiles(String path) {
		File[] files = new File(path).listFiles();
		return sortFileList(files);
	}

	/**
	 * 设置当前目录
	 *
	 * @param path 文件夹路径
	 */
	public void setCurrentDir(String path) {
		currentDir = FileInfo.formFile(new File(path));
		Log.i("FileListAdapter", "setCurrentDir:" + currentDir);
		File[] files = getFiles(path);
		this.files = Arrays.asList(files).stream().map(FileInfo::formFile).collect((Collectors.toList()));
		if (!isRoot(new File(path))) {
			Log.i("FileListAdapter", "add:" + new File(path).getParentFile().getAbsolutePath());
			File parent = new File(path).getParentFile();
			this.files.add(0,new FileInfo("..",parent.getAbsolutePath()));
		}
		notifyDataSetChanged();
	}

	static class ViewHolder extends RecyclerView.ViewHolder {

        TextView fileName;
		ImageView fileIcon;
		TextView fileTime;

        public ViewHolder(View itemView) {
            super(itemView);
            fileName = itemView.findViewById(R.id.file_list_name);
            fileIcon = itemView.findViewById(R.id.file_list_image);
			fileTime = itemView.findViewById(R.id.file_list_time);
        }
    }

}
