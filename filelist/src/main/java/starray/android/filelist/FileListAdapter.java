package starray.android.filelist;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@SuppressWarnings("all")
public class FileListAdapter extends RecyclerView.Adapter<FileListAdapter.ViewHolder> {

	private List<FileItemInfo> files = new ArrayList<>();
	private Context context;
	private Activity activityContext;
	private FileItemInfo currentDir;
	private String lastDir;
	private FileItemClickListener fileItemClickListener;
	private RecyclerView parentView;

	private List<FileItemInfo> selectedFiles = new ArrayList<>();
	private String newFileName = "";
	private boolean isDestroyed = false;
	private boolean isMultiSelectMode = false;

	/**
	 * 构造函数
	 * @param context 上下文
	 * @param files 文件信息列表
	 */
	public FileListAdapter(Context context, List<FileItemInfo> files) {
		this.context = context;
		if (context instanceof Activity) {
			this.activityContext = (Activity) context;
		}
		setCurrentDir(files.get(0).getParent());

	}

	protected void setParentView(RecyclerView view) {
		parentView = view;
	}

	/** 从位置获取{@link FileItemInfo}实例
	 *
	 * @param position 位置
	 * @return {@link FileItemInfo}实例
	 */
	public FileItemInfo getItem(int position) {
		return files.get(position);
	}

	public interface FileItemClickListener {
		void onFileItemClick(FileItemInfo file);
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
		final FileItemInfo selectedFile = files.get(position);
		holder.fileName.setText(selectedFile.getName());
		if (selectedFile.getFile().isDirectory()) {
			holder.fileIcon.setImageResource(R.drawable.folder);
		} else {
			switch (Extensions.getFileType(selectedFile.getName())) {
				case IMAGE:
					if (activityContext == null) {
						Bitmap bitmap = BitmapFactory.decodeFile(selectedFile.getFile().getAbsolutePath());
						holder.fileIcon.setImageBitmap(bitmap);
					} else {
						new Thread(() -> {
							Bitmap bitmap = BitmapFactory.decodeFile(selectedFile.getFile().getAbsolutePath());
							activityContext.runOnUiThread(() -> holder.fileIcon.setImageBitmap(bitmap));
						}).start();
					}
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
					if (activityContext == null) {
						PackageInfo pi = context.getPackageManager().getPackageArchiveInfo(selectedFile.getFile().getAbsolutePath(), 0);
						pi.applicationInfo.sourceDir = selectedFile.getFile().getAbsolutePath();
						pi.applicationInfo.publicSourceDir = selectedFile.getFile().getAbsolutePath();
						Drawable drawable = pi.applicationInfo.loadIcon(context.getPackageManager());
						holder.fileIcon.setImageDrawable(drawable);
					} else {
						new Thread(() -> {
							PackageInfo pi = context.getPackageManager().getPackageArchiveInfo(selectedFile.getFile().getAbsolutePath(), 0);
							pi.applicationInfo.sourceDir = selectedFile.getFile().getAbsolutePath();
							pi.applicationInfo.publicSourceDir = selectedFile.getFile().getAbsolutePath();
							Drawable drawable = pi.applicationInfo.loadIcon(context.getPackageManager());
							activityContext.runOnUiThread(() -> holder.fileIcon.setImageDrawable(drawable));
						}).start();
					}
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
				fileItemClickListener.onFileItemClick(selectedFile);
			}
			if (selectedFile.getFile().getParentFile() == null) {
				return;
			}

			if (isMultiSelectMode) {
				if (selectedFile.isSelected() || !selectedFile.getFile().exists()) {
					selectedFile.setSelected(false);
					holder.itemView.setBackgroundColor(Color.TRANSPARENT);
					selectedFiles.remove(selectedFile);

				} else {
					selectedFile.setSelected(true);
					holder.itemView.setBackgroundColor(Color.BLUE);
					selectedFiles.add(selectedFile);
				}
				return;
			}
			if (selectedFile.getFile().isDirectory()) {
				setCurrentDir(selectedFile);
			}
		});

		if (!holder.fileName.getText().equals("..")) {
			holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
				@Override
				public boolean onLongClick(View view) {
					PopupMenu popupMenu = new PopupMenu(context, view);
					popupMenu.inflate(R.menu.file_operation_menu);
					Menu menu = popupMenu.getMenu();
					menu.findItem(R.id.create_file).setVisible(selectedFile.getFile().isDirectory());
					menu.findItem(R.id.create_folder).setVisible(selectedFile.getFile().isDirectory());
					popupMenu.setOnMenuItemClickListener(item -> {
						try {
							if (item.getItemId() == R.id.delete) {
								FileUtils.delete(selectedFile.getFile());
								if (!selectedFiles.isEmpty())
									FileUtils.delete(selectedFiles.stream().map(FileItemInfo::getFile).collect(Collectors.toList()));
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
						} catch (IOException ignored) {
						}
						return false;
					});
					popupMenu.show();
					return false;
				}
			});
		}
		/** if block ↑ is false but execute????????
		 * if(!holder.fileName.getText().equals(".."))
		 * ?????????????????????????
		 */
		if (holder.fileName.getText().equals("..")) {
			holder.itemView.setOnLongClickListener(null);
		}
    }

	private void cleanNotExistFiles() {
		for (FileItemInfo fileItemInfo : selectedFiles) {
			if (!fileItemInfo.getFile().exists()) {
				selectedFiles.remove(fileItemInfo);
			}
		}
		if (selectedFiles.isEmpty()) isMultiSelectMode = false;
	}
	/**
	 * 检查是否为无法访问的目录("/storage/emulated/0", "/storage/emulated", "/sdcard", "/storage", "/")
	 *
	 * @param file {@link File}
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
		setCurrentDir(FileItemInfo.formFile(getCurrentDir()));
	}

	public void destory() {
		if (isDestroyed) {
			throw new IllegalStateException("FileListAdapter has been destoryed");
		} else {
			isDestroyed = true;
		}
		files.clear();
		selectedFiles.clear();
		notifyDataSetChanged();
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
	public void setCurrentDir(FileItemInfo fileItemInfo) {
		currentDir = fileItemInfo;
		currentDir.setAllowLongPress(false);
		this.files.clear();
		File[] files = getFiles(fileItemInfo.getPath());
		if (!isRoot(new File(fileItemInfo.getPath()))) {
			Log.i("FileListAdapter", "add:" + new File(fileItemInfo.getPath()).getParentFile().getAbsolutePath());
			File parent = new File(fileItemInfo.getPath()).getParentFile();
			this.files.add(new FileItemInfo("..",parent.getAbsolutePath(), false));
		}
		var fileList = Arrays
				.asList(files)
				.stream()
				.map(FileItemInfo::formFile)
				.collect((Collectors.toList()));
		this.files.addAll(fileList);
		notifyDataSetChanged();
	}

	public void onItemMove(int fromPosition, int toPosition) {
		//Collections.swap(fileList, fromPosition, toPosition);
		isMultiSelectMode = true;
	}

	interface ItemTouchHelperViewHolder {
		void onItemSelected(int position);
		void onItemScroll(int position);
	}

	class ViewHolder extends RecyclerView.ViewHolder implements ItemTouchHelperViewHolder {

        TextView fileName;
		ImageView fileIcon;
		TextView fileTime;


        public ViewHolder(View itemView) {
            super(itemView);
            fileName = itemView.findViewById(R.id.file_list_name);
            fileIcon = itemView.findViewById(R.id.file_list_image);
			fileTime = itemView.findViewById(R.id.file_list_time);
        }

		@Override
		public void onItemSelected(int position) {
			itemView.setBackgroundColor(Color.BLUE);
			itemView.setBackgroundColor(Color.BLUE);
			if (!isMultiSelectMode) {
				isMultiSelectMode = true;
			}
			files.get(position).setSelected(true);
		}

		@Override
		public void onItemScroll(int position) {
			itemView.setBackgroundColor(Color.BLUE);
			if (!isMultiSelectMode) {
				isMultiSelectMode = true;
			}
			files.get(position).setSelected(true);
		}
	}

}
