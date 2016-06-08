package hust.mallguide.guides;

import android.content.Context;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

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
 * Created by admin on 2016/6/7.
 */
public class MapFragment extends Fragment {

    private View view;

    public static final String MAP_PATH = "map4";

    public static final int MAP_ID = 1;
    private static final long LAYER_ID = 5;

    private Layer layer;
    private MapWidget mapWidget;

    private int curr_mapId = 0;
    private int curr_lineId = 0;

    private WifiManager wifiManager;

    private DaoUtils daoUtils;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.map_layout, container, false);

        init(savedInstanceState);
        return view;
    }

    private void init(Bundle savedInstanceState) {
        daoUtils = DaoUtils.getInstance(getActivity());

        final int initialZoomLevel = 11;

        mapWidget = new MapWidget(savedInstanceState, getActivity(), MAP_PATH, initialZoomLevel);
        mapWidget.setId(MAP_ID);

        RelativeLayout layout = (RelativeLayout) view.findViewById(R.id.mainLayout);
        layout.addView(mapWidget);

        mapWidget.getConfig().setFlingEnabled(true);
        mapWidget.getConfig().setPinchZoomEnabled(true);
        mapWidget.getConfig().setMapCenteringEnabled(true);

        mapWidget.setMaxZoomLevel(12);
        mapWidget.setUseSoftwareZoom(true);
        mapWidget.setZoomButtonsVisible(true);
        mapWidget.setSaveEnabled(true);
//        mapWidget.setBackgroundColor(Color.GREEN);
        mapWidget.setBackgroundResource(R.color.color_bg);

        // 设置长按响应
        mapWidget.setOnLongClickListener(new View.OnLongClickListener() {

            @Override
            public boolean onLongClick(View v) {
                ToastUtils.show(getActivity(), "Long press works!");
                return true;
            }
        });


        // 重写onTouch响应
        mapWidget.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View arg0, MotionEvent arg1) {
                return false;
            }
        });

        // 设置双击响应
        mapWidget.setOnDoubleTapListener(new OnMapDoubleTapListener() {
            @Override
            public boolean onDoubleTap(MapWidget v, MapTouchedEvent event) {
                onDoubleClick(v, event);
                return true;
            }
        });

        // 添加地图事件监听Listener
        mapWidget.addMapEventsListener(new MapEventsListener() {
            @Override
            public void onPreZoomOut() {

            }

            @Override
            public void onPreZoomIn() {

            }

            @Override
            public void onPostZoomOut() {
            }

            @Override
            public void onPostZoomIn() {

            }
        });

        // 初始化界面行的节点
        initObjects();
        addLine();
    }


    // 双击相应函数
    private void onDoubleClick(MapWidget v, MapTouchedEvent event) {
        int pic_x = event.getMapX();
        int pic_y = event.getMapY();
        addIndex(curr_mapId++, pic_x, pic_y);
//        addWifiInfos(pic_x, pic_y);

    }

    // 获取当前位置的所有WIFI信息
    private void addWifiInfos(int pic_x, int pic_y) {
        // 获取每个位置的所有WIFI信息
        wifiManager = (WifiManager) getActivity().getSystemService(Context.WIFI_SERVICE);

        wifiManager.startScan();
        List<ScanResult> list = wifiManager.getScanResults();

        List<WifiRecord> records = new ArrayList<>();

        if (list != null) {
            LogUtils.i("list size : " + list.size());
            if (list != null) {
                for (ScanResult scanResult : list) {
                    // 计算信号强度
                    int nSigLevel = WifiManager.calculateSignalLevel(scanResult.level, 100);

                    // 存储成WifiRecord对象
                    records.add(new WifiRecord(WifiRecord._ID++, scanResult.SSID, scanResult.BSSID,
                            scanResult.level, nSigLevel, pic_x, pic_y, 7));

                    LogUtils.d("SSID:" + scanResult.SSID + "   BSSID:" + scanResult.BSSID + "    强度:"
                            + scanResult.level + "----" + nSigLevel);
                }
            }

        } else {
            LogUtils.d("list is null");
        }

        // 添加到数据库中存储
        DaoSession daoSession = daoUtils.getDaoSession();
        WifiRecordDao dao = daoSession.getWifiRecordDao();
        dao.insertInTx(records);

//        // 已连接信息
//        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
//        int nWSig = WifiManager.calculateSignalLevel(wifiInfo.getRssi(), 100);
//        LogUtils.w("new SSID : " + wifiInfo.getSSID() + "   signal strength : " + wifiInfo.getRssi() + "   强度:" + nWSig);
    }


    private void initObjects() {
        List<Point> datas = new ArrayList<>();
        datas.add(new Point(1348, 794));
        datas.add(new Point(742 , 678));
//        datas.add(new int[] {1044, 420});
//        datas.add(new int[] {1352, 240});
//        datas.add(new int[] {2184, 1090});
//        datas.add(new int[] {2260, 660});
//        datas.add(new int[] {2510, 1061});
//        datas.add(new int[] {1764, 893});

        for (Point data : datas) {
            addIndex(curr_mapId++, data);
        }
    }

    // 添加Line连线
    private void addLine(MapLine line) {
        if (layer == null)
            // 创建图层
            layer = mapWidget.createLayer(LAYER_ID);
        layer.addMapLine(line);
    }

    private void addLine() {
        List<Point> datas = new ArrayList<>();
        datas.add(new Point(1348, 794));
        datas.add(new Point(742 , 678));

        addLine(new MapLine(curr_lineId++, datas));
    }

    // 添加位置节点
    private void addIndex(int mapId, int x, int y) {
        Drawable icon = getResources().getDrawable(R.mipmap.index);
        addIndex(mapId, x, y, icon);
    }

    private void addIndex (int mapId, int x, int y, Drawable icon) {
        addIndex(mapId, new Point(x, y), icon);
    }

    private void addIndex (int mapId, Point point) {
        Drawable icon = getResources().getDrawable(R.mipmap.index);
        addIndex(mapId, point, icon);
    }

    private void addIndex (int mapId, Point point, Drawable icon) {
        if (layer == null)
            // 创建图层
            layer = mapWidget.createLayer(LAYER_ID);
        // adding object to layer
        layer.addMapObject(new MapObject(mapId, icon, point,
                PivotFactory.createPivotPoint(icon, PivotFactory.PivotPosition.PIVOT_CENTER), true, true));
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        MapWidget map = (MapWidget) getActivity().findViewById(MAP_ID);
        map.saveState(outState);
    }
}
