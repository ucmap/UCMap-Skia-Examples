package cn.creable.android.demo;

import android.graphics.Canvas;
import android.widget.Toast;
import cn.creable.gridgis.controls.App;
import cn.creable.gridgis.controls.IMapTool;
import cn.creable.gridgis.controls.MapControl;
import cn.creable.gridgis.geometry.Point;
import cn.creable.gridgis.shapefile.Selector;

public class PointTool implements IMapTool {
	
	public MapControl mapControl;
	
	public Selector selector;
	private boolean isSelecting;
	
	public PointTool(MapControl mapControl)
	{
		this.mapControl=mapControl;
		selector=new Selector(mapControl);
		selector.setMode(1);
		selector.setOffset(0, 100);
		isSelecting=true;
	}

	@Override
	public void action() {
		// TODO Auto-generated method stub

	}

	@Override
	public void draw(Canvas g) {
		if (isSelecting)
			selector.draw(g);
	}

	@Override
	public boolean keyPressed(int arg0) {
		if (isSelecting) selector.keyPressed(arg0);
		return false;
	}

	@Override
	public void pointerDragged(int x, int y,int x2,int y2) {
		if (isSelecting) selector.pointerDragged(x, y, x2, y2);
	}

	@Override
	public void pointerPressed(int x, int y,int x2,int y2) {
		if (isSelecting) selector.pointerPressed(x, y, x2, y2);
	}

	@Override
	public void pointerReleased(int x, int y,int x2,int y2) {
		if (isSelecting)
		{
			selector.pointerReleased(x, y, x2, y2);
			x=selector.getX();
			y=selector.getY();
			selector.reset();
		}
		Point pt=new Point();
		mapControl.getDisplay().getDisplayTransformation().toMapPoint(new Point(x,y), pt);
		Toast.makeText(App.getInstance(), String.format("µØÍ¼×ø±êx=%f,y=%f", pt.getX(),pt.getY()), Toast.LENGTH_SHORT).show();
		
		Point[] pts=new Point[1];
		pts[0]=pt;
		String[] ts=new String[1];
		ts[0]="²âÊÔ";
		MyCustomDraw mcd=new MyCustomDraw(mapControl,pts,ts,null);
		mapControl.setCustomDraw(mcd);
		mapControl.refresh();
	}

}
