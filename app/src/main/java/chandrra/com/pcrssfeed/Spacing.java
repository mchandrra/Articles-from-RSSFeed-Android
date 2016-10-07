package chandrra.com.pcrssfeed;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by smallipeddi on 10/7/16.
 * To define space constraints.
 */


public class Spacing extends RecyclerView.ItemDecoration {
    int space;
    public Spacing(int space) {
        this.space = space;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        if (parent.getChildLayoutPosition(view) > 0) {
            outRect.left = space;
            outRect.bottom = space;
            outRect.right = space;
        } else {
            outRect.bottom = space;
        }
    }
}
