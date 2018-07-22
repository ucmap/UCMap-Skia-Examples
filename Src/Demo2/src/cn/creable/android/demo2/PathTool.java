package cn.creable.android.demo2;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Bundle;
import android.os.Message;
import android.widget.Toast;
import cn.creable.gridgis.controls.ICustomDraw2;
import cn.creable.gridgis.controls.IMapTool;
import cn.creable.gridgis.controls.MapControl;
import cn.creable.gridgis.display.IDisplayTransformation;
import cn.creable.gridgis.geometry.IPoint;
import cn.creable.gridgis.geometry.Point;
import cn.creable.ucmap.IPathSearchListener;
import cn.creable.ucmap.OpenSourceMapLayer;

public class PathTool implements IMapTool,IPathSearchListener,ICustomDraw2 {
	
	private MapControl mapControl;
	private OpenSourceMapLayer layer;
	private Activity act;
	private Point pt1,pt2;
	private double lon1,lat1,lon2,lat2;
	private int state;
	private Paint paint;
	private OpenSourceMapLayer.Path _path;
	private ProgressDialog dlg;
	
	public PathTool(MapControl mapControl,OpenSourceMapLayer layer,Activity act)
	{
		this.mapControl=mapControl;
		this.layer=layer;
		this.act=act;
		pt1=new Point();
		pt2=new Point();
		state=0;
		paint=new Paint();
		
		_path=null;
		
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
			paint.setColor(0xFFFF0000);
			paint.setStrokeWidth(1);
			g.drawLine((int)pt.getX()-10, (int)pt.getY(), (int)pt.getX()+10, (int)pt.getY(),paint);
			g.drawLine((int)pt.getX(), (int)pt.getY()-10, (int)pt.getX(), (int)pt.getY()+10, paint);
			pt=null;
		}
		if (state>1)
		{
			Point pt=new Point();
			mapControl.getDisplay().getDisplayTransformation().fromMapPoint(pt2, pt);
			paint.setColor(0xFF0000FF);
			g.drawLine((int)pt.getX()-10, (int)pt.getY(), (int)pt.getX()+10, (int)pt.getY(),paint);
			g.drawLine((int)pt.getX(), (int)pt.getY()-10, (int)pt.getX(), (int)pt.getY()+10, paint);
			pt=null;
		}
		if (_path!=null)
		{
			IDisplayTransformation trans=mapControl.getDisplay().getDisplayTransformation();
			paint.setStrokeWidth(6);
			paint.setColor(0xF0C97BB9);
			Point pt1=new Point();
			int ptCount;
			IPoint pt;
			Point pppp=null;
			Path path=new Path();
			for (int k=0;k<_path.lines.length;++k)
			{
				ptCount=_path.lines[k].getNumPoints();
				pt=_path.lines[k].getPoint(0);
				pppp=layer.fromLonLat(pt.getX(),pt.getY());
				trans.fromMapPoint(pppp, pt1);
				path.moveTo((float)pt1.getX(), (float)pt1.getY());
				--ptCount;
				for (int i=0;i<ptCount;++i)
				{
					pt=_path.lines[k].getPoint(i+1);
					pppp=layer.fromLonLat(pt.getX(),pt.getY());
					trans.fromMapPoint(pppp, pt1);
					path.lineTo((float)pt1.getX(), (float)pt1.getY());
				}
			}
			pt1=null;
			paint.setStyle(Paint.Style.STROKE);
			g.drawPath(path, paint);
			paint.setStyle(Paint.Style.FILL);

			Point tp=new Point();
			for (int i=0;i<this._path.markArray.length;++i)
			{
				pppp=layer.fromLonLat(_path.markArray[i].point.getX(),_path.markArray[i].point.getY());
				trans.fromMapPoint(pppp,tp);

				if (i==(_path.markArray.length-1))	//绘制结束符号
				{
					paint.setColor(0xFF191BF5);
					
				}
				else if (i==0)	//绘制开始符号
				{
					paint.setColor(0xFFFF0000);
				}
				else
				{
					paint.setColor(0xFF00C2E8);
				}
				g.drawCircle((float)tp.getX(), (float)tp.getY(), 4, paint);
			}
			
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
			Point pt=layer.toLonLat((int)pt1.getX(), (int)pt1.getY());
			lon1=pt.getX();
			lat1=pt.getY();
			state=1;
			mapControl.repaint();
			break;
		case 1:
			mapControl.getDisplay().getDisplayTransformation().toMapPoint(x, y, pt2);
			pt=layer.toLonLat((int)pt2.getX(), (int)pt2.getY());
			lon2=pt.getX();
			lat2=pt.getY();
			state=2;
			mapControl.repaint();
			break;
		case 2:
			_path=null;
			state=0;
			layer.setPathSearchListener(this);
			layer.getPath((float)lon1,(float)lat1,(float)lon2,(float)lat2,false,false);
			//layer.getPath(119.84994f,33.526226f,117.7076f,30.508738f,false);
			//显示等待界面
			dlg = new ProgressDialog(act);   
			dlg.setProgressStyle(ProgressDialog.STYLE_SPINNER);   
			dlg.setTitle("提示");   
			dlg.setMessage("正在进行路径查询，请稍后");   
			dlg.setIcon(R.drawable.icon);   
			dlg.setIndeterminate(false);   
			dlg.setCancelable(true);
			dlg.setButton("取消 ", new DialogInterface.OnClickListener(){

				@Override
				public void onClick(DialogInterface dialog, int which) {
					layer.cancel();
				}
				
			});
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
	public void pathSearchFinished(OpenSourceMapLayer.Path path) {
		
		dlg.cancel();
		dlg=null;
		if (path==null || path.lines==null) return;
		_path=path;
		mapControl.repaint();
	}

	@Override
	public void drawOnMapCache(Canvas g) {
		IMapTool tool=mapControl.getCurrentTool();
		if (tool!=this)
			drawPath(g);
	}

	@Override
	public void drawOnScreen(Canvas g) {
		IMapTool tool=mapControl.getCurrentTool();
		if (tool==this)
			drawPath(g);
	}

}
