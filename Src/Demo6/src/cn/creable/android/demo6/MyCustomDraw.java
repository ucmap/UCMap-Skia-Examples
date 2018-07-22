package cn.creable.android.demo6;

import java.util.Vector;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import cn.creable.gridgis.controls.ICustomDraw;
import cn.creable.gridgis.controls.MapControl;
import cn.creable.gridgis.display.IDisplayTransformation;
import cn.creable.gridgis.geometry.Point;
import cn.creable.gridgis.util.Image;

public class MyCustomDraw implements ICustomDraw {
	

	private MapControl mapControl;
	private Vector<POI> poiList=new Vector<POI>();
	private Vector<Area> areaList=new Vector<Area>();
	private Paint paint;
	private Point pt=new Point();
	
	public MyCustomDraw(MapControl mapControl)
	{
		this.mapControl=mapControl;
		paint=new Paint();
		paint.setTextSize(18);
		paint.setFlags(Paint.ANTI_ALIAS_FLAG);
	}
	
	public Vector<POI> getPOIList()
	{
		return poiList;
	}
	
	public void addPOI(POI poi)
	{
		this.poiList.addElement(poi);
	}
	
	public void addArea(Area area)
	{
		this.areaList.addElement(area);
	}

	@Override
	public void draw(Canvas g) {
		IDisplayTransformation dt=mapControl.getDisplay().getDisplayTransformation();
		int size=poiList.size();
		for (int i=0;i<size;++i)
		{
			POI poi=poiList.elementAt(i);
			dt.fromMapPoint(poi.x, poi.y, pt);
			poi.img.draw(g, (float)pt.getX()-poi.img.getWidth()/2, (float)pt.getY()-poi.img.getWidth()/2, null);
			int x0=poi.img.getWidth()/3;
			int y0=-3;
			paint.setColor(0xFFFF0000);//ºìÉ«
			paint.setStyle(Paint.Style.FILL);
			g.drawText(poi.text, (float)(x0+pt.getX()), (float)(pt.getY()-y0), paint);
		}
		
		size=areaList.size();
		for (int i=0;i<size;++i)
		{
			Area area=areaList.elementAt(i);
			int count=area.points.length/2;
			if (count>1)
			{
				Path path=new Path();
				dt.fromMapPoint(area.points[0], area.points[1], pt);
				path.moveTo((float)pt.getX(),(float)pt.getY());
				for (int i1=1;i1<count;++i1)
				{
					dt.fromMapPoint(area.points[i1*2], area.points[i1*2+1], pt);
					path.lineTo((float)pt.getX(),(float)pt.getY());
				}
				path.close();
				paint.setColor(area.fillColor);
				paint.setStyle(Paint.Style.FILL);
				g.drawPath(path, paint);
				paint.setColor(area.lineColor);
				paint.setStyle(Paint.Style.STROKE);
				g.drawPath(path, paint);
			}
		}
	}

}
