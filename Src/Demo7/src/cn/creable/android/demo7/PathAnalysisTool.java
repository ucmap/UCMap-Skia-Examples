package cn.creable.android.demo7;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.drawable.BitmapDrawable;
import cn.creable.gridgis.controls.App;
import cn.creable.gridgis.controls.ICustomDraw2;
import cn.creable.gridgis.controls.IMapTool;
import cn.creable.gridgis.controls.MapControl;
import cn.creable.gridgis.display.IDisplayTransformation;
import cn.creable.gridgis.geometry.IPoint;
import cn.creable.gridgis.geometry.Point;
import cn.creable.gridgis.util.Image;
import cn.creable.topology.Navigate;

public class PathAnalysisTool implements IMapTool,ICustomDraw2 {
	
	private MapControl mapControl;
	private Activity act;
	private Point pt1,pt2;
	private int state;
	private Paint paint;
	private cn.creable.topology.Path _path;
	private ProgressDialog dlg;
	private Image startImg,endImg;
	
	public PathAnalysisTool(MapControl mapControl,Activity act)
	{
		this.mapControl=mapControl;
		this.act=act;
		pt1=new Point();
		pt2=new Point();
		state=0;
		paint=new Paint();
		
		_path=null;
		
		BitmapDrawable bmpDraw=(BitmapDrawable)App.getInstance().getResources().getDrawable(R.drawable.start);   
		startImg=new Image(bmpDraw.getBitmap());
		
		bmpDraw=(BitmapDrawable)App.getInstance().getResources().getDrawable(R.drawable.end);   
		endImg=new Image(bmpDraw.getBitmap());
	}
	
	public void clearPath()
	{
		_path=null;
		state=0;
	}

	@Override
	public void action() {
		// TODO Auto-generated method stub

	}

	@Override
	public void draw(Canvas g) {
		
	}
	
	private void drawPath(Canvas g)
	{
		if (state>0)
		{
			Point pt=new Point();
			mapControl.getDisplay().getDisplayTransformation().fromMapPoint(pt1, pt);
			startImg.draw(g, (int)pt.getX()-startImg.getWidth()/2, (int)pt.getY()-startImg.getHeight()/2, null);
			pt=null;
		}
		if (state>1)
		{
			Point pt=new Point();
			mapControl.getDisplay().getDisplayTransformation().fromMapPoint(pt2, pt);
			endImg.draw(g, (int)pt.getX()-endImg.getWidth()/2, (int)pt.getY()-endImg.getHeight()/2, null);
			pt=null;
		}
		if (_path!=null)
		{
			IDisplayTransformation trans=mapControl.getDisplay().getDisplayTransformation();
			paint.setStrokeWidth(6);
			paint.setColor(0xF07BB9C9);
			Point point=new Point();
			int ptCount;
			IPoint pt;
			Path path=new Path();
			mapControl.getDisplay().getDisplayTransformation().fromMapPoint(pt1, point);
			path.moveTo((float)point.getX(), (float)point.getY());
			for (int k=0;k<_path.arcCount;++k)
			{
				ptCount=_path.arcs[k].ptsCount;
				pt=_path.arcs[k].pts[0];
				trans.fromMapPoint(pt, point);
				if (k==0)
					path.lineTo((float)point.getX(), (float)point.getY());
				else
					path.moveTo((float)point.getX(), (float)point.getY());
				--ptCount;
				for (int i=0;i<ptCount;++i)
				{
					pt=_path.arcs[k].pts[i+1];
					trans.fromMapPoint(pt, point);
					path.lineTo((float)point.getX(), (float)point.getY());
				}
			}
			mapControl.getDisplay().getDisplayTransformation().fromMapPoint(pt2, point);
			path.lineTo((float)point.getX(), (float)point.getY());
			paint.setStyle(Paint.Style.STROKE);
			g.drawPath(path, paint);
			paint.setStyle(Paint.Style.FILL);
			
//			Point tp=new Point();
//			for (int i=0;i<this._path.arcCount;++i)
//			{
//				trans.fromMapPoint(_path.arcs[i].pts[0],tp);
//
//				if (i==(this._path.arcCount-1))	//绘制结束符号
//				{
//					paint.setColor(0xFF191BF5);
//					
//				}
//				else if (i==0)	//绘制开始符号
//				{
//					paint.setColor(0xFFFF0000);
//				}
//				else
//				{
//					paint.setColor(0xFF00C2E8);
//				}
//				g.drawCircle((float)tp.getX(), (float)tp.getY(), 8, paint);
//			}
			
			mapControl.getDisplay().getDisplayTransformation().fromMapPoint(pt1, point);
			startImg.draw(g, (int)point.getX()-startImg.getWidth()/2, (int)point.getY()-startImg.getHeight()/2, null);
			mapControl.getDisplay().getDisplayTransformation().fromMapPoint(pt2, point);
			endImg.draw(g, (int)point.getX()-endImg.getWidth()/2, (int)point.getY()-endImg.getHeight()/2, null);
			point=null;
		}
	}

	@Override
	public boolean keyPressed(int arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void pointerDragged(int x, int y, int x2, int y2) {
		// TODO Auto-generated method stub

	}

	@Override
	public void pointerPressed(int x, int y, int x2, int y2) {
		// TODO Auto-generated method stub

	}

	@Override
	public void pointerReleased(int x, int y, int x2, int y2) {
		switch (state)
		{
		case 0:
			mapControl.getDisplay().getDisplayTransformation().toMapPoint(x, y, pt1);
			state=1;
			_path=null;
			mapControl.repaint();
			break;
		case 1:
			mapControl.getDisplay().getDisplayTransformation().toMapPoint(x, y, pt2);
			state=2;
			mapControl.repaint();
//			break;
//		case 2:
			_path=null;
			state=0;
			
			new Thread(new Runnable() {
				@Override
				public void run() {
					long time=System.currentTimeMillis();
					_path=Navigate.getPath((float)pt1.getX(), (float)pt1.getY(), (float)pt2.getX(), (float)pt2.getY(), 1);
					System.out.println("time="+(System.currentTimeMillis()-time));
					dlg.cancel();
					mapControl.repaint();
				}
			}).start();
			
			//显示等待界面
			dlg = new ProgressDialog(act);   
			dlg.setProgressStyle(ProgressDialog.STYLE_SPINNER);   
			dlg.setTitle("提示");   
			dlg.setMessage("正在进行路径查询，请稍后");   
			dlg.setIcon(R.drawable.ic_launcher);   
			dlg.setIndeterminate(false);   
			dlg.setCancelable(false);
			dlg.show();  
			break;
		default:
			state=0;
			_path=null;
			mapControl.repaint();
			break;
		}
	}

	@Override
	public void drawOnMapCache(Canvas g) {
		IMapTool tool=mapControl.getCurrentTool();
		if (tool!=this)
		{
			drawPath(g);
		}
	}

	@Override
	public void drawOnScreen(Canvas g) {
		IMapTool tool=mapControl.getCurrentTool();
		if (tool==this)
		{
			drawPath(g);
		}
	}

}
