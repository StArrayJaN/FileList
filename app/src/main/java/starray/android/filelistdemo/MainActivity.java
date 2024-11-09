package starray.android.filelistdemo;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import starray.android.filelist.FileListAdapter;
import starray.android.filelist.FileListView;

public class MainActivity extends AppCompatActivity {
    FileListView fileListView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        fileListView = findViewById(R.id.fileListView);
        Permission.checkPermission(this);
        if (Permission.isPermissionGranted(this) )fileListView.init("/storage/emulated/0");

        if (fileListView.getAdapter() != null) {
            fileListView.getAdapter().setFileItemClickListener(file -> {
                Toast.makeText(this, file.getAbsolutePath(), Toast.LENGTH_SHORT).show();
            });
        }

        getOnBackPressedDispatcher().addCallback(new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (FileListAdapter.isRoot(fileListView.getAdapter().getCurrentDir())) {
                    finish();
                } else {
                    fileListView.getAdapter().setCurrentDir(fileListView.getAdapter().getCurrentDir().getParent());
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == Permission.REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PERMISSION_GRANTED) {
                fileListView.init("/storage/emulated/0");
            }
        }
    }
}