package hust.mallguide.services;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;

import java.util.ArrayList;
import java.util.List;

import hust.mallguide.R;

/**
 * Created by admin on 2016/3/24.
 */
public class TabThreeFragment extends Fragment implements View.OnClickListener{
    View view;

    ImageView myImageView;
    ListView myListView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.tabthree_layout, container, false);
        myImageView = (ImageView) view.findViewById(R.id.myImageView);
        myImageView.requestFocus();

        myListView = (ListView) view.findViewById(R.id.myListView);


        List<String> list = new ArrayList<>();
        for (int i = 0; i < 100; i++)
            list.add("jdhfjkhfskfh==" + i);
        myListView.setAdapter(new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, list));

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }


    @Override
    public void onClick(View v) {

    }

}
