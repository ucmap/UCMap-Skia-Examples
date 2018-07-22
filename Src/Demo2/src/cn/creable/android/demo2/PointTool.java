package cn.creable.android.demo2;

import android.graphics.Canvas;
import android.widget.Toast;
import cn.creable.gridgis.controls.App;
import cn.creable.gridgis.controls.IMapTool;
import cn.creable.gridgis.controls.MapControl;
import cn.creable.gridgis.geometry.IPoint;
import cn.creable.gridgis.geometry.Point;
import cn.creable.ucmap.OpenSourceMapLayer;

public class PointTool implements IMapTool {
	
	public MapControl mapControl;
	
	public PointTool(MapControl mapControl)
	{
		this.mapControl=mapControl;
	}

	@Override
	public void action() {
		// TODO Auto-generated method stub

	}

	@Override
	public void draw(Canvas arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean keyPressed(int arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void pointerDragged(int arg0, int arg1, int arg2, int arg3) {
		// TODO Auto-generated method stub

	}

	@Override
	public void pointerPressed(int arg0, int arg1, int arg2, int arg3) {
		// TODO Auto-generated method stub

	}

	@Override
	public void pointerReleased(int x, int y, int arg2, int arg3) {
		OpenSourceMapLayer olayer=(OpenSourceMapLayer)mapControl.getMap().getLayer(0);
		IPoint result=new Point();
		mapControl.getDisplay().getDisplayTransformation().toMapPoint(x, y, result);
		IPoint degree=olayer.toLonLat((int)result.getX(), (int)result.getY());
		IPoint offset=olayer.getOffset(degree.getX(), degree.getY());
		degree=olayer.toLonLat((int)(result.getX()-offset.getX()), (int)(result.getY()-offset.getY()));
		Toast.makeText(App.getInstance(), String.format("%d\t\t\t\t\t%d\n%f\t%f", x,y,degree.getX(),degree.getY()), Toast.LENGTH_SHORT).show();
		
//		Point pt=olayer.fromLonLat(degree.getX(), degree.getY());
//		offset=olayer.getOffset(degree.getX(), degree.getY());
//		pt.setX(pt.getX()+offset.getX());
//		pt.setY(pt.getY()+offset.getY());
//		mapControl.getDisplay().getDisplayTransformation().fromMapPoint(pt, result);
//		Toast.makeText(App.getInstance(), String.format("%d\t\t\t\t\t%d\n%f\t%f", x,y,result.getX(),result.getY()), Toast.LENGTH_SHORT).show();
	}

}
