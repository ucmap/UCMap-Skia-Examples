package cn.creable.demo8;

import java.util.ArrayList;
import java.util.LinkedList;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.drawable.BitmapDrawable;
import cn.creable.demo8.Util.LocationPoint;
import cn.creable.gridgis.controls.App;
import cn.creable.gridgis.controls.ICustomDraw;
import cn.creable.gridgis.controls.MapControl;
import cn.creable.gridgis.display.Display;
import cn.creable.gridgis.display.IDisplayTransformation;
import cn.creable.gridgis.display.LineSymbol;
import cn.creable.gridgis.geometry.IEnvelope;
import cn.creable.gridgis.geometry.LineString;
import cn.creable.gridgis.geometry.Point;

/**
 * 这个类实现绘制gps点以及轨迹的功能
 *
 */
public class MyCustomDraw implements ICustomDraw {
	
	private LineString historyLine;//轨迹线
	private LineSymbol ls;//线样式1
	private LineSymbol ls2;//线样式2
	private LocationPoint curLoc;//当前gps点
	
	private MapControl mapControl;
	
	private Point pt=new Point();
	
	private Bitmap gpsImage;//gps点图片符号
	
	private boolean needRefresh=false;//是否需要刷新地图
	
	public MyCustomDraw(MapControl mapControl)
	{
		this.mapControl=mapControl;
		gpsImage=((BitmapDrawable)App.getInstance().getResources().getDrawable(R.drawable.gps)).getBitmap();
		ls=new LineSymbol(3,0xFFE049B2);
		ls2=new LineSymbol(5,0xFF49E0B2);
		
		curLoc=Util.getInstance().getCurrentLocation();
	}
	
	/**
	 * 设置轨迹
	 * @param locPoints gps点数组，也就是轨迹
	 */
	public void setLocaitonPoints(ArrayList<LocationPoint> locPoints)
	{
		if (locPoints==null)
			historyLine=null;
		else
		{
			//根据LocationPoint数组构建一个LineString对象
			int count=locPoints.size();
			if (count<2) return;
			double[] points=new double[count*2];
			for (int i=0;i<count;++i)
			{
				points[i*2]=locPoints.get(i).lon;
				points[i*2+1]=locPoints.get(i).lat;
			}
			historyLine=new LineString(points);
		}
	}
	
	/**
	 * 设置当前gps点
	 * @param curLoc 当前gps点
	 */
	public void setCurrentLocation(LocationPoint curLoc)
	{
		this.curLoc=curLoc;
		if (curLoc==null) return;
		if (mapControl.pointerStatus==2 || mapControl.getRefreshManager().isThreadRunning()==true)
			return;//当mapControl有地图工具处于拖动模式，或者刷新线程没有执行完毕时，直接return
		IEnvelope env=mapControl.getExtent();
		//如果gps处于当前显示的地图范围内，则立即画出gps点
		if (env.getXMin()<=curLoc.lon && curLoc.lon<=env.getXMax() &&
				env.getYMin()<=curLoc.lat &&curLoc.lat<=env.getYMax())
		{
			if (needRefresh)
			{//如果需要刷新则调用refresh
				mapControl.refresh();
				needRefresh=false;
			}
			else
			{//如果不需要刷新则调用repaint，repaint的速度比refresh快的多，因为repaint不需要重新绘制地图，仅仅是将新的gps点绘制一下而已
				mapControl.repaint();
			}
		}
	}

	@Override
	public void draw(Canvas g) {
		if (curLoc!=null)
		{
			mapControl.getDisplay().getDisplayTransformation().fromMapPoint(curLoc.lon, curLoc.lat, pt);
			g.drawBitmap(gpsImage, (float)(pt.getX()-gpsImage.getWidth()/2), (float)(pt.getY()-gpsImage.getHeight()/2), null);
			if (mapControl.smoothMode==true && mapControl.pointerStatus==0)
				needRefresh=true;//当smoothMode=true，且按下触屏被触发的draw时，下一次gps位置的更新需要调用refresh，因为PanTool在移动地图时，会调用customDraw的draw函数，将内容画在地图缓存上，而gps点是移动的，要显示出移动的效果，则必须调用一次refresh将上次残留的gps点清除
		}
		if (historyLine!=null)
		{
			Display display=(Display)mapControl.getDisplay();
			Canvas oldG=display.getCanvas();//获取display中的canvas
			display.setCanvas(g);//替换display中的canvas
			display.DrawPolyline(historyLine, ls2);//画出轨迹线
			//display.DrawPolyline(historyLine, ls);//画出轨迹线
			display.setCanvas(oldG);//还原display中的canvas
		}
	}

}
