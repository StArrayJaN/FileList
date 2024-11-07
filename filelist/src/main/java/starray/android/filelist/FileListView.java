package starray.android.filelist;

import android.content.Context;
import android.os.Environment;
import android.util.AttributeSet;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class FileListView extends RecyclerView {
    FileListAdapter adapter;
    boolean init = false;
    public FileListView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public FileListView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public FileListView(@NonNull Context context) {
        super(context);
    }

    public void init(String path){
        if (init) throw new IllegalStateException("Already initialized!");
        File[] files = Objects.requireNonNull(FileListAdapter.sortFileList(new File(path).listFiles()));
        List<File> fileList = new ArrayList<>(Arrays.asList(files));
        List<FileInfo> fileInfoList = fileList.stream()
                .map(FileInfo::formFile)
                .collect(Collectors.toList());
        adapter = new FileListAdapter(this.getContext(),fileInfoList);
        setAdapter(adapter);
        setLayoutManager(new LinearLayoutManager(this.getContext()));
        init = true;
        Log.i(getClass().getSimpleName(), Objects.toString(init));
    }

    public void refresh(){
        adapter.refresh();
    }

    @Nullable
    @Override
    public FileListAdapter getAdapter() {
        return adapter;
    }
}
