package com.sate7.wlj.developerreader.sate7gems.map;

import com.baidu.mapapi.map.Overlay;

import java.util.ArrayList;

public class PolylineData {
    //所有的折点
    private ArrayList<Overlay> pointsList;
    //折线
    private Overlay polyline;

    public ArrayList<Overlay> getPointsList() {
        return pointsList;
    }


    public Overlay getPolyline() {
        return polyline;
    }


    public PolylineData(ArrayList<Overlay> pointsList, Overlay polyline) {
        this.pointsList = pointsList;
        this.polyline = polyline;
    }

    public PolylineData(ArrayList<Overlay> pointsList) {
        this.pointsList = pointsList;
    }

    public void remove() {
        if (polyline != null) {
            polyline.remove();
            polyline = null;
        }

        if (pointsList != null) {
            for (Overlay overlay : pointsList) {
                overlay.remove();
            }
            pointsList.clear();
        }
    }

}
