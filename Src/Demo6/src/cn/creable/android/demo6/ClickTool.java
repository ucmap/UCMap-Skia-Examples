package cn.creable.android.demo6;

import java.util.Vector;

import android.graphics.Canvas;
import android.widget.Toast;
import cn.creable.gridgis.controls.App;
import cn.creable.gridgis.controls.IMapTool;
import cn.creable.gridgis.controls.IMapTool2;
import cn.creable.gridgis.controls.MapControl;
import cn.creable.gridgis.display.IDisplayTransformation;
import cn.creable.gridgis.geometry.Arithmetic;
import cn.creable.gridgis.geometry.Point;

public class ClickTool implements IMapTool, IMapTool2 {
	
	private MapControl mapControl;
	private MyCustomDraw mcd;
	private int x,y;
	private Point pt=new Point();
	
	public ClickTool(MapControl mapControl,MyCustomDraw mcd)
	{
		this.mapControl=mapControl;
		this.mcd=mcd;
	}

	@Override
	public int getLongPressTime() {
		return 1000;
	}

	@Override
	public int getLongPressTolerance() {
		return 5;
	}

	@Override
	public void onLongPressed() {
		Vector<POI> poiList=mcd.getPOIList();
		if (poiList!=null && poiList.size()>0)
		{
			IDisplayTransformation dt=mapControl.getDisplay().getDisplayTransformation();
			int size=poiList.size();
			POI poi;
			for (int i=0;i<size;++i)
			{
				poi=poiList.get(i);
				dt.fromMapPoint(poi.x, poi.y, pt);
				double xmin=pt.getX()-poi.img.getWidth()/2;
				double xmax=pt.getX()+poi.img.getWidth()/2;
				double ymin=pt.getY()-poi.img.getHeight()/2;
				double ymax=pt.getY()+poi.img.getHeight()/2;
				if (xmin<x && x<xmax && ymin<y && y<ymax)
				{//找到被点中的POI
					Toast.makeText(App.getInstance(), poi.text, Toast.LENGTH_SHORT).show();
					break;
				}
			}
		}
	}

	@Override
	public void onRemove() {
		// TODO Auto-generated method stub

	}

	@Override
	public void action() {
		// TODO Auto-generated method stub

	}

	@Override
	public void draw(Canvas g) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean keyPressed(int code) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void pointerDragged(int x, int y, int x2, int y2) {
		// TODO Auto-generated method stub

	}

	@Override
	public void pointerPressed(int x, int y, int x2, int y2) {
		this.x=x;
		this.y=y;
	}

	@Override
	public void pointerReleased(int x, int y, int x2, int y2) {
		Vector<POI> poiList=mcd.getPOIList();
		if (poiList!=null && poiList.size()>0)
		{
			IDisplayTransformation dt=mapControl.getDisplay().getDisplayTransformation();
			int size=poiList.size();
			POI poi;
			for (int i=0;i<size;++i)
			{
				poi=poiList.get(i);
				dt.fromMapPoint(poi.x, poi.y, pt);
				double xmin=pt.getX()-poi.img.getWidth()/2;
				double xmax=pt.getX()+poi.img.getWidth()/2;
				double ymin=pt.getY()-poi.img.getHeight()/2;
				double ymax=pt.getY()+poi.img.getHeight()/2;
				if (xmin<x && x<xmax && ymin<y && y<ymax)
				{//找到被点中的POI
					Toast.makeText(App.getInstance(), poi.text, Toast.LENGTH_SHORT).show();
					break;
				}
			}
		}
	}

}
