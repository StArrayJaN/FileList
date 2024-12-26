package starray.android.filelist;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.ItemTouchHelper;
public class ItemTouchHelperCallback extends ItemTouchHelper.Callback {

    private final FileListAdapter adapter;

    public ItemTouchHelperCallback(FileListAdapter adapter) {
        this.adapter = adapter;
    }

    @Override
    public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        int dragFlags = ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
        int swipeFlags = 0;
        return makeMovementFlags(dragFlags, swipeFlags);
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        FileListAdapter.ViewHolder viewHolder1 = (FileListAdapter.ViewHolder)viewHolder;
        viewHolder1.onItemScroll(viewHolder.getAdapterPosition());
        return true;
    }

    @Override
    public boolean isLongPressDragEnabled() {
        return true;
    }

    @Override
    public void onSelectedChanged(@Nullable RecyclerView.ViewHolder viewHolder, int actionState) {
        FileListAdapter.ViewHolder viewHolder1 = (FileListAdapter.ViewHolder)viewHolder;
        if (viewHolder1 != null) {
            viewHolder1.onItemSelected(viewHolder.getBindingAdapterPosition());
        }
        super.onSelectedChanged(viewHolder, actionState);
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        FileListAdapter.ViewHolder viewHolder1 = (FileListAdapter.ViewHolder)viewHolder;
        viewHolder1.onItemSelected(viewHolder.getBindingAdapterPosition());
    }
}

