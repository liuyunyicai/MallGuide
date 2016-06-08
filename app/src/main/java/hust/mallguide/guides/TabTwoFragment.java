package hust.mallguide.guides;

import android.content.Context;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ls.widgets.map.MapWidget;
import com.ls.widgets.map.events.MapTouchedEvent;
import com.ls.widgets.map.interfaces.Layer;
import com.ls.widgets.map.interfaces.MapEventsListener;
import com.ls.widgets.map.interfaces.OnMapDoubleTapListener;
import com.ls.widgets.map.model.MapLine;
import com.ls.widgets.map.model.MapObject;
import com.ls.widgets.map.utils.PivotFactory;

import java.util.ArrayList;
import java.util.List;

import hust.mallguide.R;
import hust.mallguide.utils.LogUtils;
import hust.mallguide.utils.ToastUtils;
import hust.mallguide.utils.dao_utils.DaoUtils;
import mydb.DaoSession;
import mydb.WifiRecord;
import mydb.WifiRecordDao;

/**
 * Created by admin on 2016/3/24.
 */
public class TabTwoFragment extends Fragment {
    private View view;
    private MapFragment mapFragment;
    private FragmentManager fragmentManager;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.tabtwo_layout, container, false);
        fragmentManager = getActivity().getSupportFragmentManager();

        mapFragment = new MapFragment();

        fragmentManager.beginTransaction().replace(R.id.mapContent, mapFragment).commit();

        return view;
    }



}
