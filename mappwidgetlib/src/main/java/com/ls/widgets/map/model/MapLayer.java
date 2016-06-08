/*************************************************************************
 * Copyright (c) 2015 Lemberg Solutions
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 **************************************************************************/

package com.ls.widgets.map.model;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Drawable.Callback;
import android.os.Looper;
import android.util.Log;

import com.ls.widgets.map.MapWidget;
import com.ls.widgets.map.config.OfflineMapConfig;
import com.ls.widgets.map.interfaces.Layer;

import java.util.ArrayList;

/* 地图图层 */
public class MapLayer implements Layer, Callback {
    private long layerId;
    private boolean isVisible;

    private ArrayList<MapObject> drawables;
    private ArrayList<MapObject> touchables;
    private ArrayList<MapLine> lines;

    protected MapWidget parent;

    public MapWidget getParent() {
        return parent;
    }

    public void setParent(MapWidget parent) {
        this.parent = parent;
    }

    public MapLayer(long theLayerId, MapWidget parent) {
        drawables = new ArrayList<>();
        touchables = new ArrayList<>();

        //Visible by default
        isVisible = true;

        this.layerId = theLayerId;
        this.parent = parent;
    }


    @Override
    public void addMapObject(MapObject object) {
        if (object == null) {
            throw new IllegalArgumentException();
        }

        if (Looper.myLooper() == null) {
            throw new RuntimeException("addMapObject should be called from UI thread.");
        }

        object.setParent(this);

        object.setScale(parent.getScale());

        drawables.add(object);

        if (object.isTouchable()) {
            touchables.add(object);
        }

        parent.invalidate(object.getBounds());
    }


    @Override
    public MapObject getMapObject(Object id) {
        if (id == null) {
            throw new IllegalArgumentException();
        }

        // TODO: Re-factor this as this is extremely not efficient
        for (MapObject drawable : drawables) {
            if (drawable.getId().equals(id)) {
                return drawable;
            }
        }

        return null;
    }


    @Override
    public MapObject getMapObjectByIndex(int index) {
        return drawables.get(index);
    }


    @Override
    public int getMapObjectCount() {
        return drawables != null ? drawables.size() : 0;
    }


    @Override
    public void removeMapObject(Object id) {
        if (id == null) {
            throw new IllegalArgumentException();
        }

        if (Looper.myLooper() == null) {
            throw new RuntimeException("removeMapObject should be called from UI thread.");
        }

        MapObject objectToDelete = null;
        for (MapObject mapObject : drawables) {
            if (mapObject.getId().equals(id)) {
                objectToDelete = mapObject;
                break;
            }
        }

        if (objectToDelete != null) {
            drawables.remove(objectToDelete);
            touchables.remove(objectToDelete);

            Rect b = objectToDelete.getDrawable().getBounds();
            parent.postInvalidate(b.left, b.top, b.right, b.bottom);
        }
    }


//	public ArrayList<Object> getTouched(int normX, int normY)
//	{
//		ArrayList<Object> result = new ArrayList<Object>();
//		
//		for (MapObject touchable:touchables) {
//			if (touchable.isTouched(normX, normY)) {
//				result.add(touchable.getId());
//			}
//		}
//		
//		return result;
//	}
//	

    /**
     * Returns Ids of map object that were touched. Intended for internal use
     * @param touchRect
     * @return
     */
    public ArrayList<Object> getTouched(Rect touchRect) {
        ArrayList<Object> result = new ArrayList<Object>();

        for (MapObject touchable : touchables) {
            if (touchable.isTouched(touchRect)) {
                result.add(touchable.getId());
            }
        }

        return result;
    }


    public boolean isVisible() {
        return isVisible;
    }


    public void setVisible(boolean visible) {
        isVisible = visible;
    }

    @Override
    public void addMapLine(MapLine mapLine) {
        if (mapLine == null) {
            throw new IllegalArgumentException();
        }

        if (Looper.myLooper() == null) {
            throw new RuntimeException("addMapObject should be called from UI thread.");
        }

        mapLine.setLayer(this);
        if (lines == null)
            lines = new ArrayList<>();

        lines.add(mapLine);

        parent.invalidate();
    }

    @Override
    public void removeMapLine(MapLine mapLine) {
        if (mapLine == null) {
            throw new IllegalArgumentException();
        }

        if (Looper.myLooper() == null) {
            throw new RuntimeException("addMapObject should be called from UI thread.");
        }

        lines.remove(mapLine);

        parent.invalidate();
    }

    @Override
    public void removeMapLineById(int lineId) {
        removeMapLine(getMapLine(lineId));
    }

    @Override
    public MapLine getMapLine(int id) {
        for (MapLine line : lines) {
            if (line.getLineId() == id)
                return line;
        }
        return null;
    }

    @Override
    public MapLine getMapLineByTag(String tag) {
        for (MapLine line : lines) {
            if (line.getTag().equals(tag))
                return line;
        }
        return null;
    }

    @Override
    public int getMapLineCount() {
        if (lines != null)
            return lines.size();
        return 0;
    }

    @Override
    public void clearAllLines() {
        if (lines != null)
            lines.clear();
        parent.invalidate();
    }


    public void setScale(float scale) {
        int size = drawables.size();

        for (int i = 0; i < size; ++i) {
            MapObject drawable = drawables.get(i);
            drawable.setScale(scale);
        }
    }


    // Draw逻辑
    public void draw(Canvas canvas, Rect drawingRect) {
        if (!isVisible)
            return;

        for (int i = 0, size = drawables.size(); i < size; ++i) {
            MapObject object = drawables.get(i);

            if (Rect.intersects(object.getBounds(), drawingRect)) {
                object.draw(canvas);
            }
        }

        // 绘制图像
        if (lines != null && (!lines.isEmpty())) {
            for (MapLine line : lines)
                line.draw(canvas, parent.getScale());
        }
    }


    public void clearAll() {
        if (Looper.myLooper() == null) {
            throw new RuntimeException("clearAll should be called from UI thread.");
        }

        drawables.clear();
        touchables.clear();
        drawables = null;
        touchables = null;
        drawables = new ArrayList<>();
        touchables = new ArrayList<>();
    }


    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }

        if (!(o instanceof MapLayer)) {
            return false;
        }

        MapLayer inMapLayer = (MapLayer) o;

        return inMapLayer.layerId == this.layerId;
    }


    public int hashCode() {
        return (int) (layerId ^ (layerId >>> 32));
    }


    // Package access methods

    OfflineMapConfig getConfig() {
        return parent.getConfig();
    }


    void invalidate(MapObject object) {
        Rect bounds = object.getBounds();
        parent.postInvalidate(bounds.left, bounds.top, bounds.right, bounds.bottom);
    }


    public void scheduleDrawable(Drawable who, Runnable what, long when) {
        parent.scheduleDrawable(who, what, when);
    }


    public void unscheduleDrawable(Drawable who, Runnable what) {
        parent.unscheduleDrawable(who, what);
    }


    public void invalidateDrawable(Drawable who) {
        Rect bounds = who.getBounds();
        parent.postInvalidate(bounds.left, bounds.top, bounds.right, bounds.bottom);
    }


    public long getId() {
        return layerId;
    }
}
