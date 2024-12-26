package starray.android.filelist;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class FileListView extends RecyclerView {

    private FileListAdapter adapter;
    private boolean init = false;
    ItemTouchHelper.Callback callback;
    ItemTouchHelper itemTouchHelper;


    public FileListView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

    }

    public FileListView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public FileListView(@NonNull Context context) {
        super(context);
    }

    /**
     * 初始化文件列表
     * @param path 文件夹路径
     */
    public void init(String path){
        if (init) throw new IllegalStateException("Already initialized!");
        File[] files = Objects.requireNonNull(FileListAdapter.sortFileList(new File(path).listFiles()));
        List<File> fileList = new ArrayList<>(Arrays.asList(files));
        List<FileItemInfo> fileInfoList = fileList.stream()
                .map(FileItemInfo::formFile)
                .collect(Collectors.toList());
        adapter = new FileListAdapter(this.getContext(),fileInfoList);
        setAdapter(adapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this.getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        setLayoutManager(layoutManager);
        callback = new ItemTouchHelperCallback(adapter);
        itemTouchHelper = new ItemTouchHelper(callback);
        itemTouchHelper.attachToRecyclerView(this);
        init = true;
        Log.i(getClass().getSimpleName(), Objects.toString(init));
    }

    /** 刷新文件列表 */
    public void refresh(){
        adapter.refresh();
    }

    /** 获取文件列表适配器 */
    @Nullable
    @Override
    public FileListAdapter getAdapter() {
        return adapter;
    }
}
