package cn.creable.android.demo6;

import java.util.Vector;

import cn.creable.gridgis.controls.App;
import cn.creable.gridgis.controls.IMapTool;
import cn.creable.gridgis.controls.IMapTool2;
import cn.creable.gridgis.controls.IMeasureToolListener;
import cn.creable.gridgis.controls.MapControl;
import cn.creable.gridgis.display.IDisplayTransformation;
import cn.creable.gridgis.geometry.Arithmetic;
import cn.creable.gridgis.geometry.IEnvelope;
import cn.creable.gridgis.geometry.IPoint;
import cn.creable.gridgis.geometry.Point;
import cn.creable.gridgis.shapefile.Selector;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.widget.Toast;

public class DrawTool implements IMapTool,IMapTool2 {
	
	protected MapControl mapControl;
	protected int type;
	public Selector selector;
	protected boolean bDrag;
	protected Vector<IPoint> pts;
	protected Paint paint;
	
	protected int fillColor=0x80FC7F43;
	protected int lineColor=0xFFFC7F43;
	
	private int longPressTolerance=8;
	private float x,y;
	private boolean flag;
	
	/**
	 * 构造函数
	 * @param mapControl 地图控件的引用
	 * @param type 种类，0表示画圆，1表示画多边形，2表示画矩形
	 */
	public DrawTool(MapControl mapControl,int type)
    {
        this.mapControl=mapControl;
        this.type=type;
        selector=new Selector(mapControl);
        selector.setMode(1);
        pts=new Vector<IPoint>();
        paint=new Paint();
        paint.setStrokeWidth(3);
        paint.setTextSize(16);
        paint.setFlags(Paint.ANTI_ALIAS_FLAG);
    }
	
	public void setType(int type)
	{
		this.type=type;
	}

	@Override
	public void action() {
		// TODO Auto-generated method stub

	}

	@Override
	public void draw(Canvas g) {
        IDisplayTransformation trans=mapControl.getDisplay().getDisplayTransformation();
        int size=pts.size();
        Point result=new Point();
        if (size>1 || (size>0 && bDrag))
        {
        	if (type==0)
        	{//画圆
        		paint.setStrokeWidth(3);
        		paint.setColor(lineColor);
	            paint.setStyle(Paint.Style.STROKE);
        		trans.fromMapPoint(pts.get(0),result);
        		Point pt=new Point();
        		if (size>1) trans.fromMapPoint(pts.get(1), pt);
        		if (bDrag && flag)
        		{
        			pt.setX(selector.getX());
        			pt.setY(selector.getY());
        			--size;
        		}
        		g.drawCircle((float)result.getX(), (float)result.getY(), (float)Arithmetic.Distance(pt, result), paint);
        		paint.setStyle(Paint.Style.FILL);
            	paint.setColor(fillColor);
            	g.drawCircle((float)result.getX(), (float)result.getY(), (float)Arithmetic.Distance(pt, result), paint);
        	}
        	else if (type==1)
        	{//画多边形
	        	paint.setStrokeWidth(3);
	            Path path = new Path();
	            Point start=new Point();
	            trans.fromMapPoint(pts.get(0),start);
	            path.moveTo((float)start.getX(), (float)start.getY());
	            for (int i=1;i<size;++i)
	            {
	                trans.fromMapPoint(pts.get(i), result);
	                path.lineTo((float)result.getX(), (float)result.getY());
	            }
	            if (bDrag && flag)
	            	path.lineTo(selector.getX(), selector.getY());
	            if (type==1)
	            	path.lineTo((float)start.getX(), (float)start.getY());
	            
	            
	            paint.setColor(lineColor);
	            paint.setStyle(Paint.Style.STROKE);
	            g.drawPath(path, paint);
	            if (type==1 && (size>2 || (size>1 && bDrag)))
	            {
	            	paint.setStyle(Paint.Style.FILL);
	            	paint.setColor(fillColor);
	            	g.drawPath(path, paint);
	            }
        	}
        	else if (type==2)
        	{//画矩形
        		paint.setStrokeWidth(3);
        		paint.setColor(lineColor);
	            paint.setStyle(Paint.Style.STROKE);
        		trans.fromMapPoint(pts.get(0),result);
        		Point pt=new Point();
        		if (size>1) trans.fromMapPoint(pts.get(1), pt);
        		if (bDrag && flag)
        		{
        			pt.setX(selector.getX());
        			pt.setY(selector.getY());
        			--size;
        		}
        		g.drawRect((float)result.getX(), (float)result.getY(), (float)pt.getX(), (float)pt.getY(), paint);
        		paint.setStyle(Paint.Style.FILL);
            	paint.setColor(fillColor);
            	g.drawRect((float)result.getX(), (float)result.getY(), (float)pt.getX(), (float)pt.getY(), paint);
        	}
                
        }
        for (int i=0;i<size;++i)
        {
        	paint.setStrokeWidth(3);
            trans.fromMapPoint(pts.get(i), result);
            paint.setColor(0xFFFFFFFF);
            paint.setStyle(Paint.Style.FILL);
            g.drawCircle((float)result.getX(),(float)result.getY(), 4, paint);
            paint.setColor(0xFFFF0000);
            paint.setStyle(Paint.Style.STROKE);
            g.drawCircle((float)result.getX(),(float)result.getY(), 4, paint);
            paint.setStrokeWidth(1);
        }
        if (bDrag) selector.draw(g);
	}

	@Override
	public boolean keyPressed(int keyCode) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void pointerDragged(int x, int y, int x2, int y2) {
		if (Math.abs(x-this.x)>this.longPressTolerance || Math.abs(y-this.y)>this.longPressTolerance)
			flag=true;
		selector.pointerDragged(x, y, x2, y2);
	}

	@Override
	public void pointerPressed(int x, int y, int x2, int y2) {
		bDrag=true;
        selector.pointerPressed(x, y, x2, y2);
        this.x=x;
        this.y=y;
        flag=false;
	}

	@Override
	public void pointerReleased(int x, int y, int x2, int y2) {
		bDrag=false;
		flag=false;
        selector.pointerReleased(x, y, x2, y2);
        IDisplayTransformation dt=mapControl.getDisplay().getDisplayTransformation();
        
        if (type==1)
        {
	        Point pt=new Point(selector.getX(),selector.getY());
	        Point result=new Point();
	        dt.toMapPoint(pt, result);
	        pts.add(result);
        }
        else
        {
        	Point pt=new Point(selector.getX(),selector.getY());
	        Point result=new Point();
	        dt.toMapPoint(pt, result);
        	if (pts.size()<2)
        		pts.add(result);
        	else
        		pts.setElementAt(result,1);
        }
        mapControl.repaint();
	}
	
	public int getType()
	{
		return type;
	}
	
	public Vector<IPoint> getResult()
    {
        return pts;
    }
    
    public void submit()
    {
        pts.clear();
    }

	@Override
	public void onRemove() {
	}

	@Override
	public int getLongPressTolerance() {
		return longPressTolerance;
	}

	@Override
	public int getLongPressTime() {
		return 1000;
	}

	@Override
	public void onLongPressed() {
		bDrag=false;
		selector.reset();
		
		if (pts!=null && pts.size()>1)
		switch (type)
		{
		case 0:
			Toast.makeText(App.getInstance(), String.format("圆心坐标x=%f,y=%f,半径r=%f", pts.elementAt(0).getX(),pts.elementAt(0).getY(),Arithmetic.Distance(pts.elementAt(0), pts.elementAt(1))), Toast.LENGTH_SHORT).show();
			break;
		case 1:
			StringBuilder sb=new StringBuilder();
			sb.append("折线坐标如下:\n");
			for (int i=0;i<pts.size();++i)
			{
				sb.append(String.format("%f,%f\n",pts.elementAt(i).getX(),pts.elementAt(i).getY()));
			}
			Toast.makeText(App.getInstance(), sb.toString(), Toast.LENGTH_SHORT).show();
			break;
		case 2:
			StringBuilder sb1=new StringBuilder();
			sb1.append("矩形坐标如下:\n");
			double x1=pts.elementAt(0).getX();
			double y1=pts.elementAt(0).getY();
			double x2=pts.elementAt(1).getX();
			double y2=pts.elementAt(1).getY();
			sb1.append(String.format("%f,%f\n",x1,y1));
			sb1.append(String.format("%f,%f\n",x1,y2));
			sb1.append(String.format("%f,%f\n",x2,y2));
			sb1.append(String.format("%f,%f\n",x2,y1));
			Toast.makeText(App.getInstance(), sb1.toString(), Toast.LENGTH_SHORT).show();
			break;
		}
		
		submit();
		
	}
	
	/**
	 * 后退一步
	 * @return 是否后退成功
	 */
	public boolean undo()
	{
		if (pts.size()>0)
		{
			pts.remove(pts.size()-1);
			mapControl.repaint();
			return true;
		}
		return false;
	}

}
