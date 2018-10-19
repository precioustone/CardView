package info.androidhive.cardview;


import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class CylinderFragment extends Fragment{

    private RecyclerView rView;
    private Button next;

    private AlbumsAdapter adapter;
    private List<Album> albumList;
    private OnnextButtonClickedListener mCallback;

    public CylinderFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_cylinder, container, false);
        rView = (RecyclerView) view.findViewById(R.id.recycler_view);

        albumList = new ArrayList<>();
        adapter = new AlbumsAdapter(getActivity(), albumList);

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getContext(), 2);
        rView.setLayoutManager(mLayoutManager);
        rView.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(10), true));
        rView.setItemAnimator(new DefaultItemAnimator());
        rView.setAdapter(adapter);
        next = view.findViewById(R.id.cyl_next);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallback.nextButtonClicked("order");
            }
        });
        prepareAlbums();

        return view;
    }


    /**
     * Adding few albums for testing
     */
     private void prepareAlbums() {
         int[] covers = new int[]{R.drawable.three,
                 R.drawable.five,
                 R.drawable.seven,
                 R.drawable.ten,
                 R.drawable.twel,
                 R.drawable.twen,
                 R.drawable.fif};

         Album a = new Album("3kg",covers[0]);
         albumList.add(a);

         a = new Album("5kg",covers[1]);
         albumList.add(a);

         a = new Album("7kg", covers[2]);
         albumList.add(a);

         a = new Album("10kg", covers[3]);
         albumList.add(a);

         a = new Album("12.5kg", covers[4]);
         albumList.add(a);

         a = new Album("25kg", covers[5]);
         albumList.add(a);

         a = new Album("50kg", covers[6]);
         albumList.add(a);



         adapter.notifyDataSetChanged();
     }

    /**
     * RecyclerView item decoration - give equal margin around grid item
     */
     public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

         private int spanCount;
         private int spacing;
         private boolean includeEdge;

         public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
             this.spanCount = spanCount;
             this.spacing = spacing;
             this.includeEdge = includeEdge;
         }

         @Override
         public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
             int position = parent.getChildAdapterPosition(view); // item position
             int column = position % spanCount; // item column

             if (includeEdge) {
                 outRect.left = spacing - column * spacing / spanCount; // spacing - column * ((1f / spanCount) * spacing)
                 outRect.right = (column + 1) * spacing / spanCount; // (column + 1) * ((1f / spanCount) * spacing)

                 if (position < spanCount) { // top edge
                 outRect.top = spacing;
                 }
                 outRect.bottom = spacing; // item bottom
            } else {
                 outRect.left = column * spacing / spanCount; // column * ((1f / spanCount) * spacing)
                 outRect.right = spacing - (column + 1) * spacing / spanCount; // spacing - (column + 1) * ((1f /    spanCount) * spacing)
                 if (position >= spanCount) {
                 outRect.top = spacing; // item top
                 }
             }
         }
     }

     /**
      * Converting dp to pixel
      */
     private int dpToPx(int dp) {
     Resources r = getResources();
     return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
     }


    @Override
    public void onAttach(Activity activity){
         super.onAttach(activity);


        try {
            mCallback = (OnnextButtonClickedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnHeadlineSelectedListener");
        }
     }

}
