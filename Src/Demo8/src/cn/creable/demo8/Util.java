package cn.creable.demo8;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;

import cn.creable.gridgis.controls.App;
import cn.creable.gridgis.controls.MapControl;
import cn.creable.gridgis.geodatabase.IFeatureClass;
import cn.creable.gridgis.geometry.Arithmetic;
import cn.creable.gridgis.geometry.Envelope;
import cn.creable.gridgis.geometry.IEnvelope;
import cn.creable.gridgis.geometry.Point;
import cn.creable.gridgis.gridMap.IMap;
import cn.creable.gridgis.mapLayer.ILayer;
import cn.creable.gridgis.shapefile.FixedShapefileLayer;
import cn.creable.gridgis.shapefile.ShapefileLayer;
import cn.creable.ucmap.OpenSourceMapLayer;
import cn.creable.ucmap.RasterLayer;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.media.SoundPool;
import android.os.Bundle;

/**
 * 这是一个工具类，实现了gps的管理，一些地图相关函数，以及方位角、距离计算等等
 *
 */
public class Util implements LocationListener{
	
	private static Util instance;
	
	private LocationManager locationManager;
	private LocationProvider gpsProvider;
	
	private boolean isGPSOpened=false;
	
	private SQLiteDatabase db;
	
	private MapControl mapControl;
	private long time=0;
	private long interval=0;
	
	/**
	 * 播放声音
	 */
	public void playSound()
	{
		final MediaPlayer mp = new MediaPlayer();
		mp.reset();   
		try {
			mp.setDataSource(App.getInstance(),RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM ));
			mp.prepare();
			mp.start();
			
			new Thread()
			{
				@Override
				public void run() {
					try {
						Thread.sleep(5000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					mp.stop();
				}
			}.start();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void setMapControl(MapControl mapControl)
	{
		this.mapControl=mapControl;
	}
	
	class LocationPoint
	{
		public long time;
		public double lon,lat;
		public LocationPoint()
		{
			
		}
		public LocationPoint(double lon,double lat,long time)
		{
			this.time=time;
			this.lon=lon;
			this.lat=lat;
		}
	}
	
	private LocationPoint curLocation;
	
	private Util()
	{
		curLocation=null;
	}
	
	public static Util getInstance()
	{
		if (instance==null)
			instance=new Util();
		return instance;
	}
	
	/**
	 * 根据图层名获取图层引用
	 * @param mapControl
	 * @param layerName
	 * @return
	 */
	public static ILayer getLayerByName(MapControl mapControl,String layerName)
	{
		IMap map=mapControl.getMap();
		int layerCount=map.getLayerCount();
		ILayer layer;
		for (int i=0;i<layerCount;++i)
		{
			layer=map.getLayer(i);
			if (layer.getName().equalsIgnoreCase(layerName))
			{
				return layer;
			}
		}
		return null;
	}
	
	/**
	 * 根据字段名获取字段序号
	 * @param fc
	 * @param fieldName
	 * @return
	 */
	public static int getFieldIdByName(IFeatureClass fc,String fieldName)
	{
		return fc.findField(fieldName);
	}
	
	/**
	 * 将地图以给定的点为中心显示
	 * @param mapControl
	 * @param x
	 * @param y
	 */
	public static void centerMapByPoints(MapControl mapControl,double x,double y)
	{
		IEnvelope env=mapControl.getExtent();
		Point center=new Point(x,y);
		env.centerAt(center);
		mapControl.refresh(env);
		center=null;
	}
	
	/**
	 * 获取全图范围
	 * @param map
	 * @return
	 */
	public static IEnvelope getMapFullExtent(IMap map)
	{
		int count=map.getLayerCount();
		if (count>0)
		{
			IEnvelope env=Envelope.createEnvelope(Double.MAX_VALUE, -Double.MAX_VALUE, Double.MAX_VALUE, -Double.MAX_VALUE);
			for (int i=0;i<count;++i)
			{
				ILayer layer=map.getLayer(i);
				if (layer instanceof OpenSourceMapLayer)
					return null;
				if (layer instanceof ShapefileLayer)
				{
					env.union(((ShapefileLayer)layer).getFullExtent());
				}
				else if (layer instanceof FixedShapefileLayer)
				{
					env.union(((FixedShapefileLayer)layer).getFullExtent());
				}
				else if (layer instanceof RasterLayer)
				{
					env.union(((RasterLayer)layer).getFullExtent());
				}
			}
			return env;
		}
		return null;
	}
	
	/**
	 * 复制文件
	 * @param from
	 * @param to
	 * @return
	 */
	public static boolean copy(String from, String to) {
		try {
			String toPath = to.substring(0, to.lastIndexOf(File.separatorChar)); // 提取文件路径
			File f = new File(toPath); // 建立文件目录路
			if (!f.exists())
				f.mkdirs();

			BufferedInputStream bin = new BufferedInputStream(
					new FileInputStream(from));
			BufferedOutputStream bout = new BufferedOutputStream(
					new FileOutputStream(to));
			int c;
			while ((c = bin.read()) != -1)
				// 复制
				bout.write(c);
			bin.close();
			bout.close();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * 开启gps
	 * @param context
	 * @param interval 刷新间隔，单位为毫秒
	 * @param minDistance 最小距离
	 */
	public void openGPS(Context context,int interval,float minDistance)
	{
		closeGPS();
		this.interval=interval;
		this.locationManager=(LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
		this.gpsProvider=this.locationManager.getProvider(LocationManager.GPS_PROVIDER);
		this.locationManager.requestLocationUpdates(gpsProvider.getName(), interval, minDistance, this);
		isGPSOpened=true;
		//打开数据库，如果没有则会创建数据库和必要的表
		db=context.openOrCreateDatabase("database.db", Context.MODE_PRIVATE,null);
		db.execSQL("CREATE TABLE IF NOT EXISTS [location_history] ([lon] DOUBLE, [lat] DOUBLE, [time] INT64, CONSTRAINT [sqlite_autoindex_location_history_1] PRIMARY KEY ([time]));");
	}
	
	/**
	 * 关闭gps
	 */
	public void closeGPS()
	{
		if (isGPSOpened==true)
		{
			locationManager.removeUpdates(this);
			//db.close();
		}
		isGPSOpened=false;
	}
	
	/**
	 * 获取当前gps位置
	 * @return
	 */
	public LocationPoint getCurrentLocation()
	{
		return curLocation;
	}
	
	/**
	 * 获取gps历史轨迹
	 * @param startTime 开始时间  GregorianCalendar.getTimeInMillis返回的值
	 * @param endTime 结束时间  GregorianCalendar.getTimeInMillis返回的值
	 * @return
	 */
	public ArrayList<LocationPoint> getLocationHistory(long startTime,long endTime)
	{
		//构建sql语句
		String sql=String.format("select lon,lat,time from location_history where time>%d and time<%d",startTime,endTime);
		Cursor cursor=db.rawQuery(sql, null);
		if (cursor!=null)
		{//将结果存入数组
			ArrayList<LocationPoint> lps=new ArrayList<LocationPoint>();
			while (cursor.moveToNext())
			{
				lps.add(new LocationPoint(cursor.getDouble(0),cursor.getDouble(1),cursor.getLong(2)));
			}
			if (lps.isEmpty()) return null;
			return lps;
		}
		return null;
	}
	
	/**
	 * 计算方位角
	 * @param x
	 * @param y
	 * @param x2
	 * @param y2
	 * @return
	 */
	public double getAngle(double x,double y,double x2,double y2)
	{
		double dx=y2-y;
		double dy=x2-x;
		if (dx==0)
		{
			if (dy>0) return 90;
			else return 270;
		}
		double a=Math.atan(Math.abs(dy/dx))/Math.PI*180;
		if (dx>0 && dy>=0)
		{
			
		}
		else if (dx<0 && dy>=0)
		{
			a=180-a;
		}
		else if (dx<0 && dy<0)
		{
			a=180+a;
		}
		else if (dx>0 && dy<0)
		{
			a=360-a;
		}
		return a;
	}
	
//	/**
//	 * 计算给定的一点与当前gps位置形成的方位角和距离
//	 * @param lon
//	 * @param lat
//	 * @param distance
//	 * @return
//	 */
//	public double getAngleAndDistance(double lon,double lat,Double distance)
//	{
//		LocationPoint curLocation=getCurrentLocation();
//		if (curLocation==null) return -1;
//		double dx=lat-curLocation.lat;
//		double dy=lon-curLocation.lon;
//		distance=Math.sqrt(dx*dx+dy*dy);
//		return getAngle(curLocation.lon,curLocation.lat,lon,lat);
//	}
	
	public double getDistance(double x,double y,double x2,double y2)
	{
		byte type=0;
		if (mapControl.getMap().getMapUnits()==1)
			type=1;
		return Arithmetic.Distance(new Point(x,y), new Point(x2,y2), type);
	}

	@Override
	public void onLocationChanged(Location loc) {
		if (time!=0 && (loc.getTime()-time)<interval)
			return;//如果没有达到刷新间隔，则抛弃这个位置信息
		time=loc.getTime();
		if (curLocation==null) 
		{
			curLocation=new LocationPoint();
			playSound();
		}
		if (curLocation.time==time)
		{//数据库中将time做为主键，不允许出现2个相同的时间，所以这里抛弃相同时间的位置信息
			System.out.println("重复时间获取的位置被抛弃");
			return;
		}
		//修改当前位置curLocation的值，以存储当前位置信息
		curLocation.lon=loc.getLongitude();
		curLocation.lat=loc.getLatitude();
		curLocation.time=time;
		//向数据库添加位置信息
		ContentValues cv = new ContentValues();
		cv.put("lon", curLocation.lon);
		cv.put("lat", curLocation.lat);
		cv.put("time", curLocation.time);
		db.insert("location_history", null, cv);
		cv=null;
		
		if (mapControl!=null)
		{//将最新的位置信息设置进MyCustomDraw
			MyCustomDraw mcd=(MyCustomDraw)mapControl.getCustomDraw();
			mcd.setCurrentLocation(curLocation);
		}
	}

	@Override
	public void onProviderDisabled(String loc) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderEnabled(String loc) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
		// TODO Auto-generated method stub
		
	}

}
