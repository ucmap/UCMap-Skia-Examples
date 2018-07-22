package cn.creable.android.demo2;

import java.util.Timer;
import java.util.TimerTask;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.widget.Toast;
import cn.creable.gridgis.controls.App;
import cn.creable.gridgis.controls.ICustomDraw;
import cn.creable.gridgis.controls.ICustomDrawDataCenter;
import cn.creable.gridgis.controls.MapControl;
import cn.creable.gridgis.display.IDisplayTransformation;
import cn.creable.gridgis.geometry.IEnvelope;
import cn.creable.gridgis.geometry.IGeometry;
import cn.creable.gridgis.geometry.Point;
import cn.creable.gridgis.util.Image;
import cn.creable.ucmap.LBS;
import cn.creable.ucmap.OpenSourceMapLayer;

public class GPSCustomDraw implements ICustomDraw,LocationListener,ICustomDrawDataCenter {
	
	private MapControl mapControl;
	public double lon,lat;
	public double acc;//是范围，只有基站定位的数据才有这个值
	public double x,y;
	private Image gps;
//	private Image gps1;
//	private boolean flag;
	private Paint paint;
	private IDisplayTransformation dt;
//	private MyTimerTask timer;
	
	private LBS lbs;
	
//	private class MyTimerTask extends TimerTask
//	{
//
//		@Override
//		public void run() {
//			if (lon!=0 && lat!=0 && mapControl.noCustomDraw==false)
//				mapControl.repaint();
//		}
//		
//	}
	
	/**
	 * 根据给定的中心点和半径，在地图上画一个圆
	 * @param layer google图层
	 * @param dt 转换坐标对象
	 * @param g Canvas对象
	 * @param paint paint对象
	 * @param x 中心点，单位是度
	 * @param y 中心点
	 * @param radius 半径，单位是米
	 */
	private void drawCircle(OpenSourceMapLayer layer,IDisplayTransformation dt,Canvas g,Paint paint,double x,double y,float radius)
	{
		if (layer==null)
		{//不含有google图层时的处理
			double dis=0;
			float radius1=0;
			if (mapControl.getMap().getMapUnits()==1)//如果地图采用度为单位
			{
				dis=radius*180/(6370693.4856530580439461631130889*Math.PI);//将以米为单位的距离转换为以度为单位的距离
			}
			else//如果地图不采用度为单位
				dis=radius;
			radius1=dt.TransformMeasures((float)dis, false);//将以地图上距离转换为屏幕上距离
			
			Point result=new Point();
			Point pt=new Point(x,y);
			dt.fromMapPoint(pt, result);//将中心点转换为屏幕上的点
			
			paint.setStyle(Paint.Style.FILL);
			paint.setColor(0x2E0087FF);
			g.drawCircle((float)result.getX(), (float)result.getY(), radius1, paint);
			paint.setStyle(Paint.Style.STROKE);
			paint.setColor(0xBF84B6D6);
			g.drawCircle((float)result.getX(), (float)result.getY(), radius1, paint);
		}
		else
		{//含有google图层时的处理
			double dis=radius*180/(6370693.4856530580439461631130889*Math.PI);//将以米为单位的距离转换为以度为单位的距离
			Point pt1=layer.fromLonLat(x, y);
			Point pt2=layer.fromLonLat(x+dis, y);
			double dis2=Math.abs(pt2.getX()-pt1.getX());//将以度为单位的距离转换为google坐标上的距离
			float radius1=dt.TransformMeasures((float)dis2, false);//将以google坐标为单位的距离转换为屏幕上距离
			
			Point result=new Point();
			Point pt=layer.fromLonLat(x, y);
			Point offset=layer.getOffset(x, y);
			pt.setX(pt.getX()+offset.getX());
			pt.setY(pt.getY()+offset.getY());
			dt.fromMapPoint(pt, result);//将中心点转换为屏幕上的点
			
			paint.setStyle(Paint.Style.FILL);
			paint.setColor(0x2E0087FF);
			g.drawCircle((float)result.getX(), (float)result.getY(), radius1, paint);
			paint.setStyle(Paint.Style.STROKE);
			paint.setColor(0xBF84B6D6);
			g.drawCircle((float)result.getX(), (float)result.getY(), radius1, paint);
		}
	}
	
	public GPSCustomDraw(MapControl mapControl)
	{
		this.mapControl=mapControl;
		dt=mapControl.getDisplay().getDisplayTransformation();
		paint=new Paint();
		paint.setAntiAlias(true);
		BitmapDrawable bmpDraw=(BitmapDrawable)App.getInstance().getResources().getDrawable(R.drawable.gps);
		gps=new Image(bmpDraw.getBitmap());
//		bmpDraw=(BitmapDrawable)App.getInstance().getResources().getDrawable(R.drawable.gps1);
//		gps1=new Image(bmpDraw.getBitmap());
//		Timer myTimer = new Timer();
//		timer=new MyTimerTask();
//		myTimer.schedule(timer, 500, 500);
		
		lbs=new LBS(App.getInstance());
		lbs.openGPS(1000, 0.01f, this);
		lbs.getPositionByNetwork(this);
		
	}
	
	public void close()
	{
		lbs.closeGPS(this);
		x=0;
		y=0;
		mapControl.repaint();
	}

	@Override
	public void draw(Canvas g) {
		if (x!=0 && y!=0)
		{
			Point pt=new Point(x,y);
			Point result=new Point();
			dt=mapControl.getDisplay().getDisplayTransformation();
			dt.fromMapPoint(pt, result);//将图上坐标转换为屏幕坐标
			gps.draw(g, (int)result.getX()-gps.getWidth()/2, (int)result.getY()-gps.getWidth()/2, null);
			OpenSourceMapLayer oslayer=null;
			if (mapControl.getMap().getLayerCount()>0 && mapControl.getMap().getLayer(0) instanceof OpenSourceMapLayer)
				oslayer=(OpenSourceMapLayer)mapControl.getMap().getLayer(0);
			
			if (acc>0) drawCircle(oslayer,dt,g,paint,lon,lat,(float)acc);
			pt=null;
			result=null;
		}
	}

	@Override
	public void onLocationChanged(Location location) {
		lon=location.getLongitude();
		lat=location.getLatitude();
		acc=location.getAccuracy();
		//将经纬度转换为图上坐标
		if (mapControl.getMap().getLayerCount()>0 && mapControl.getMap().getLayer(0) instanceof OpenSourceMapLayer)
		{
			OpenSourceMapLayer oslayer=(OpenSourceMapLayer)mapControl.getMap().getLayer(0);
			Point offset=oslayer.getOffset(lon, lat);
			Point pt=oslayer.fromLonLat(lon, lat);
			x=pt.getX()+offset.getX();
			y=pt.getY()+offset.getY();
		}
		else
		{
			x=lon;
			y=lat;
		}
		IEnvelope env=mapControl.getExtent();
		if (env.getXMin()>x || env.getYMax()<x || env.getYMin()>y || env.getYMax()<y)
		{
			Point pt=new Point(x,y);
			env.centerAt(pt);
			mapControl.refresh(env);
		}
		else
			mapControl.repaint();
	}

	@Override
	public void onProviderDisabled(String arg0) {

	}

	@Override
	public void onProviderEnabled(String arg0) {

	}

	@Override
	public void onStatusChanged(String arg0, int arg1, Bundle arg2) {

	}

	@Override
	public IGeometry getGeometry(int index) {
		return new Point(x,y);
	}

	@Override
	public int getGeometryNum() {
		return 1;
	}

	@Override
	public void onGeometrySelected(int index, IGeometry geometry) {
		Toast.makeText(App.getInstance().getApplicationContext(), index+"   "+geometry, 100).show();
	}

}
