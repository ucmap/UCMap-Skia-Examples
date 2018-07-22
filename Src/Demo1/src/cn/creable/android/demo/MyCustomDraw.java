package cn.creable.android.demo;

import java.util.Vector;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.os.Message;
import android.widget.Toast;
import cn.creable.gridgis.controls.App;
import cn.creable.gridgis.controls.ICustomDraw;
import cn.creable.gridgis.controls.ICustomDrawDataCenter;
import cn.creable.gridgis.controls.MapControl;
import cn.creable.gridgis.display.Display;
import cn.creable.gridgis.geodatabase.Feature;
import cn.creable.gridgis.geodatabase.FeatureClass;
import cn.creable.gridgis.geodatabase.IFeature;
import cn.creable.gridgis.geometry.GeometryType;
import cn.creable.gridgis.geometry.IEnvelope;
import cn.creable.gridgis.geometry.IGeometry;
import cn.creable.gridgis.geometry.Point;
import cn.creable.gridgis.util.Image;

public class MyCustomDraw implements ICustomDraw,ICustomDrawDataCenter {
	
	private MapControl mapControl;
	private Point[] pts;
	private String[] text;
	private Image img;
	private Paint paint;
	private Activity act;
	
	private FeatureClass fc;
	
	public MyCustomDraw(MapControl mapControl,Point[] pts,String[] text,Activity act)
	{
		this.mapControl=mapControl;
		this.pts=pts;
		this.text=text;
		
		fc=new FeatureClass(new String[]{"text"},GeometryType.Point);
		Vector<IFeature> fts=new Vector<IFeature>();
		for (int i=0;i<pts.length;++i)
		{
			IFeature ft=new Feature(pts[i],new String[]{text[i]});
			fts.addElement(ft);
		}
		fc.setFeatures(fts);
		
		//读取图片
		BitmapDrawable bmpDraw=(BitmapDrawable)App.getInstance().getResources().getDrawable(R.drawable.gps);
		img=new Image(bmpDraw.getBitmap());
		
		paint=new Paint();
		paint.setTextSize(16);
		paint.setColor(0xFFFF0000);
		this.act=act;
		//move();
		//mapControl.setCustomDrawMode(true);
	}
	
	public void move()
	{
		new Thread(new Runnable() {
			@Override
			public void run() {
				while (true)
				{
					try {
						Thread.sleep(1000);//这里让线程睡眠1秒
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					pts[0].setX(pts[0].getX()+0.0004);
					IEnvelope env=mapControl.getExtent();
					env.centerAt(pts[0]);
					mapControl.refresh(env);
				}
			}
			}).start();
//		new Thread(new Runnable() {
//		@Override
//		public void run() {
//			while (true)
//			{
//				try {
//					Thread.sleep(1000);//这里让线程睡眠1秒
//				} catch (InterruptedException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//				pts[0].setX(pts[0].getX()+0.0004);
//				mapControl.repaint();
//			}
//		}
//		}).start();
//		
//		new Thread(new Runnable() {
//			@Override
//			public void run() {
//				while (true)
//				{
//					try {
//						Thread.sleep(1000);//这里让线程睡眠1秒
//					} catch (InterruptedException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//					pts[1].setX(pts[1].getX()-0.0004);
//					mapControl.repaint();
//				}
//			}
//			}).start();
//		
//		new Thread(new Runnable() {
//			@Override
//			public void run() {
//				while (true)
//				{
//					try {
//						Thread.sleep(1000);//这里让线程睡眠1秒
//					} catch (InterruptedException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//					pts[2].setY(pts[2].getY()+0.0004);
//					mapControl.repaint();
//				}
//			}
//			}).start();
//		
//		new Thread(new Runnable() {
//			@Override
//			public void run() {
//				while (true)
//				{
//					try {
//						Thread.sleep(1000);//这里让线程睡眠1秒
//					} catch (InterruptedException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//					pts[3].setY(pts[3].getY()-0.0004);
//					mapControl.repaint();
//				}
//			}
//			}).start();
	}

	@Override
	public void draw(Canvas g) {
		if (pts==null) return;
		System.out.println("--------------draw");
		int length=pts.length;
		Point pt=new Point();
		
//		for (int i=0;i<length;++i)
//		{
//			mapControl.getDisplay().getDisplayTransformation().fromMapPoint(pts[i], pt);//将地图坐标转换为屏幕坐标
//			img.draw(g, (int)pt.getX()-img.getWidth()/2, (int)pt.getY()-img.getWidth()/2, null);//将图片画在屏幕上
//			int x0=img.getWidth()/3;
//			int y0=-3;
//			g.drawText(text[i], (float)(x0+pt.getX()), (float)(pt.getY()-y0), paint);
//		}
		
		Display d=(Display)mapControl.getDisplay();
		Canvas oldG=d.getCanvas();
		d.setCanvas(g);
		for (int i=0;i<length;++i)
		{
			d.DrawImagePoint(pts[i], img);
		}
		d.setCanvas(oldG);
		
		//d.setLabelLocation(2);
		d.setLabelColor(0xFF000000);
		//d.setLabelSize(13);
		d.labelFeatureClass(g, fc, 0, img.getWidth(), img.getWidth()/4);
		//d.setLabelLocation(0);
	}

	@Override
	public IGeometry getGeometry(int index) {
		return pts[index];
	}

	@Override
	public int getGeometryNum() {
		return pts.length;
	}

	@Override
	public void onGeometrySelected(int index, IGeometry geo) {
		//Toast.makeText(App.getInstance().getApplicationContext(), text[index], Toast.LENGTH_LONG).show();
		Builder builder1 = new AlertDialog.Builder(act);
		builder1.setTitle("信息");
		builder1.setMessage(text[index]);
		builder1.setCancelable(true);
		builder1.setPositiveButton("OK", null);
		builder1.create().show();
	}

}
