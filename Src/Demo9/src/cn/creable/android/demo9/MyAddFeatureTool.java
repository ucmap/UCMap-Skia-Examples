package cn.creable.android.demo9;

import java.util.Vector;

import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import cn.creable.gridgis.controls.IMapTool;
import cn.creable.gridgis.controls.MapControl;
import cn.creable.gridgis.geometry.GeometryType;
import cn.creable.gridgis.geometry.IEnvelope;
import cn.creable.gridgis.geometry.IPoint;
import cn.creable.gridgis.geometry.Point;

public class MyAddFeatureTool implements IMapTool {
	
	private MapControl mapControl;
	private Activity act;
	private int state;
	
	private double angle;
	
	private boolean isLeft;
	
	private Paint paint;
	
	private int bType;
	private Point refPoint=new Point();
	
	private Vector<IPoint> pts=new Vector<IPoint>();
	
	public MyAddFeatureTool(MapControl mapControl,Activity act)
	{
		this.mapControl=mapControl;
		this.act=act;
		paint=new Paint();
		reset();
	}

	@Override
	public void pointerDragged(int x, int y, int x2, int y2) {
		// TODO Auto-generated method stub

	}

	@Override
	public void pointerPressed(int x, int y, int x2, int y2) {
		// TODO Auto-generated method stub

	}
	
	public void reset()
	{
		pts.removeAllElements();
		state=0;
		isLeft=true;
		bType=0;
	}
	
	public IPoint[] getPoints(int geometryType)
	{
		if (geometryType==GeometryType.Polygon)
		{
			if (pts.size()<3) return null;
			IPoint pt1=pts.elementAt(0);
			IPoint pt2=pts.elementAt(pts.size()-1);
			if (pt1.getX()==pt2.getX() && pt1.getY()==pt2.getY())
			{
				IPoint[] result=new IPoint[pts.size()];
				pts.copyInto(result);
				return result;
			}
			else
			{
				IPoint[] result=new IPoint[pts.size()+1];
				pts.copyInto(result);
				result[pts.size()]=new Point(pts.elementAt(0).getX(),pts.elementAt(0).getY());
				return result;
			}
		}
		else
		{
			if (pts.size()<2) return null;
			IPoint[] result=new IPoint[pts.size()];
			pts.copyInto(result);
			return result;
		}
	}

	@Override
	public void pointerReleased(int x, int y, int x2, int y2) {
		IPoint result=new Point();
		mapControl.getDisplay().getDisplayTransformation().toMapPoint(x, y, result);
		if (state==0)
		{
			pts.addElement(result);
			++state;
			mapControl.repaint();
		}
		else if (state==1)
		{
			if (bType==0)
			{
				pts.addElement(result);
				++state;
				mapControl.slideAnimation(result.getX(), result.getY());
				
				IPoint prev=pts.elementAt(0);
				angle=calc(prev.getY(),prev.getX(),result.getY(),result.getX());
			}
			else if (bType==1)
			{
				refPoint.setX(result.getX());
				refPoint.setY(result.getY());
				IPoint prev=pts.elementAt(0);
				angle=calc(prev.getY(),prev.getX(),result.getY(),result.getX());
				showInputDialog3(new String[]{"距离"},new String[]{"100"});
			}
		}
		else
		{
			showInputDialog(new String[]{"角度","距离"},new String[]{"90","100"});
		}
	}

	@Override
	public void action() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean keyPressed(int keyCode) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void draw(Canvas g) {
		int size=pts.size();
		IPoint pt=new Point();
		if (size>1)
		{
			mapControl.getDisplay().getDisplayTransformation().fromMapPoint(pts.elementAt(0), pt);
			Path path=new Path();
			path.moveTo((float)pt.getX(),(float)pt.getY());
			for (int i=1;i<size;++i)
			{
				mapControl.getDisplay().getDisplayTransformation().fromMapPoint(pts.elementAt(i), pt);
				path.lineTo((float)pt.getX(),(float)pt.getY());
			}
			paint.setColor(0xFFFF0000);
			paint.setStyle(Paint.Style.STROKE);
			paint.setStrokeWidth(3);
			g.drawPath(path, paint);
		}
		paint.setColor(0xFF00FF00);
		paint.setStyle(Paint.Style.FILL);
		paint.setStrokeWidth(1);
		for (int i=0;i<size;++i)
		{
			mapControl.getDisplay().getDisplayTransformation().fromMapPoint(pts.elementAt(i), pt);
			g.drawCircle((float)pt.getX(),(float)pt.getY(), 4, paint);
		}
		
		if (bType==1)
		{
			paint.setColor(0xFF0000FF);
			paint.setStyle(Paint.Style.FILL);
			paint.setStrokeWidth(1);
			mapControl.getDisplay().getDisplayTransformation().fromMapPoint(refPoint, pt);
			g.drawCircle((float)pt.getX(),(float)pt.getY(), 4, paint);
		}
		pt=null;
	}
	
	private LinearLayout.LayoutParams LP_FF = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);     
    private LinearLayout.LayoutParams LP_FW = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);     
    private LinearLayout.LayoutParams LP_WW = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT); 
	
    private Vector<EditText> ets=new Vector<EditText>();
    private RadioGroup rg;
    private RadioButton rb1,rb2;
    
	private void showInputDialog(String[] fields,String[] values)
    {
    	ScrollView sv   = new ScrollView(act);     
    	sv.setLayoutParams( LP_FF );
    	
    	LinearLayout layout = new LinearLayout(act);
    	layout.setOrientation( LinearLayout.VERTICAL );
    	sv.addView( layout ); 
    	
    	TextView tv;
    	EditText et;
    	RelativeLayout.LayoutParams lp=new RelativeLayout.LayoutParams(400,LayoutParams.WRAP_CONTENT);
    	//lp.setMargins(70, 0, 0, 0);
    	
    	rg=new RadioGroup(act);
    	rb1=new RadioButton(act);
    	rb1.setText("左角");
    	rg.addView(rb1);
    	rb2=new RadioButton(act);
    	rb2.setText("右角");
    	rg.addView(rb2);
    	if (isLeft) 
    		rb1.setChecked(true);
    	else
    		rb2.setChecked(true);
    	layout.addView(rg);
    	
    	int count=fields.length;
    	ets.clear();
    	for (int i=0;i<count;++i)
    	{
	    	tv = new TextView(act);
	    	tv.setText(fields[i]);
	    	tv.setTextAppearance(act, android.R.style.TextAppearance_Medium);
	    	layout.addView( tv );
	    	et=new EditText(act);
	    	et.setLayoutParams(lp);
	    	et.setSingleLine(true);
	    	et.setTextAppearance(act, android.R.style.TextAppearance_Medium);
	    	et.setTextColor(0xFF000000);
	    	if (values!=null) et.setText(values[i]);
	    	layout.addView(et);
	    	ets.addElement(et);
    	}

    	AlertDialog.Builder builder = new AlertDialog.Builder(act);
    	builder.setView(sv).setTitle("输入").setIcon(R.drawable.ic_launcher)
    	.setPositiveButton("确定", new DialogInterface.OnClickListener(){

			@Override
			public void onClick(DialogInterface dialog, int which) {
				int count=ets.size();
				String[] values=new String[count];
				for (int i=0;i<count;++i)
				{
					values[i]=ets.elementAt(i).getText().toString();
				}
				
				double d=Double.parseDouble(ets.elementAt(1).getText().toString());
				isLeft=rg.getCheckedRadioButtonId()==rb1.getId();
				if (isLeft)
					angle=angle+(Double.parseDouble(ets.elementAt(0).getText().toString())-180)*Math.PI/180;
				else
					angle=angle-(Double.parseDouble(ets.elementAt(0).getText().toString())-180)*Math.PI/180;
				IPoint prev=pts.elementAt(state-1);
				IPoint cur=new Point(prev.getX()+d*Math.sin(angle),prev.getY()+d*Math.cos(angle));
				pts.addElement(cur);
				++state;
				mapControl.slideAnimation(cur.getX(),cur.getY());
			}
    		
    	})
    	.setNegativeButton("取消", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				
			}
		});
    	builder.create().show();
    }
	
	public void inputBPoint(int type)
	{
		if (type==0)
			showInputDialog2(new String[]{"方位角","距离"},new String[]{"90","100"});
		else
			bType=type;
	}
	
	private void showInputDialog2(String[] fields,String[] values)
    {
    	ScrollView sv   = new ScrollView(act);     
    	sv.setLayoutParams( LP_FF );
    	
    	LinearLayout layout = new LinearLayout(act);
    	layout.setOrientation( LinearLayout.VERTICAL );
    	sv.addView( layout ); 
    	
    	TextView tv;
    	EditText et;
    	RelativeLayout.LayoutParams lp=new RelativeLayout.LayoutParams(400,LayoutParams.WRAP_CONTENT);
    	//lp.setMargins(70, 0, 0, 0);
    	
    	int count=fields.length;
    	ets.clear();
    	for (int i=0;i<count;++i)
    	{
	    	tv = new TextView(act);
	    	tv.setText(fields[i]);
	    	tv.setTextAppearance(act, android.R.style.TextAppearance_Medium);
	    	layout.addView( tv );
	    	et=new EditText(act);
	    	et.setLayoutParams(lp);
	    	et.setSingleLine(true);
	    	et.setTextAppearance(act, android.R.style.TextAppearance_Medium);
	    	et.setTextColor(0xFF000000);
	    	if (values!=null) et.setText(values[i]);
	    	layout.addView(et);
	    	ets.addElement(et);
    	}

    	AlertDialog.Builder builder = new AlertDialog.Builder(act);
    	builder.setView(sv).setTitle("输入").setIcon(R.drawable.ic_launcher)
    	.setPositiveButton("确定", new DialogInterface.OnClickListener(){

			@Override
			public void onClick(DialogInterface dialog, int which) {
				int count=ets.size();
				String[] values=new String[count];
				for (int i=0;i<count;++i)
				{
					values[i]=ets.elementAt(i).getText().toString();
				}
				
				double d=Double.parseDouble(ets.elementAt(1).getText().toString());
				angle=Double.parseDouble(ets.elementAt(0).getText().toString())*Math.PI/180;
				IPoint prev=pts.elementAt(state-1);
				IPoint cur=new Point(prev.getX()+d*Math.sin(angle),prev.getY()+d*Math.cos(angle));
				pts.addElement(cur);
				++state;
				mapControl.slideAnimation(cur.getX(),cur.getY());
			}
    		
    	})
    	.setNegativeButton("取消", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				
			}
		});
    	builder.create().show();
    }
	
	private void showInputDialog3(String[] fields,String[] values)
    {
    	ScrollView sv   = new ScrollView(act);     
    	sv.setLayoutParams( LP_FF );
    	
    	LinearLayout layout = new LinearLayout(act);
    	layout.setOrientation( LinearLayout.VERTICAL );
    	sv.addView( layout ); 
    	
    	TextView tv;
    	EditText et;
    	RelativeLayout.LayoutParams lp=new RelativeLayout.LayoutParams(400,LayoutParams.WRAP_CONTENT);
    	//lp.setMargins(70, 0, 0, 0);
    	
    	int count=fields.length;
    	ets.clear();
    	for (int i=0;i<count;++i)
    	{
	    	tv = new TextView(act);
	    	tv.setText(fields[i]);
	    	tv.setTextAppearance(act, android.R.style.TextAppearance_Medium);
	    	layout.addView( tv );
	    	et=new EditText(act);
	    	et.setLayoutParams(lp);
	    	et.setSingleLine(true);
	    	et.setTextAppearance(act, android.R.style.TextAppearance_Medium);
	    	et.setTextColor(0xFF000000);
	    	if (values!=null) et.setText(values[i]);
	    	layout.addView(et);
	    	ets.addElement(et);
    	}

    	AlertDialog.Builder builder = new AlertDialog.Builder(act);
    	builder.setView(sv).setTitle("输入").setIcon(R.drawable.ic_launcher)
    	.setPositiveButton("确定", new DialogInterface.OnClickListener(){

			@Override
			public void onClick(DialogInterface dialog, int which) {
				int count=ets.size();
				String[] values=new String[count];
				for (int i=0;i<count;++i)
				{
					values[i]=ets.elementAt(i).getText().toString();
				}
				
				double d=Double.parseDouble(ets.elementAt(0).getText().toString());
				IPoint prev=pts.elementAt(state-1);
				IPoint cur=new Point(prev.getX()+d*Math.sin(angle),prev.getY()+d*Math.cos(angle));
				pts.addElement(cur);
				++state;
				mapControl.slideAnimation(cur.getX(),cur.getY());
			}
    		
    	})
    	.setNegativeButton("取消", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				
			}
		});
    	builder.create().show();
    }
	
	private double calc(double Xa,double Ya,double Xp,double Yp)
	{
		double a;
		double dX=Xp-Xa;
		double dY=Yp-Ya;
		if (dX==0 && dY>0)
			return Math.PI/2;//a=π/2; returnα;
		else if (dX==0 && dY<0)
			return Math.PI*1.5;//α=3π/2; returnα;
		else if(dX<0 && dY==0)
			return Math.PI;//α=π; returnα;
		else if(dX>0 && dY==0)
			return 0;//α=0; returnα;
		a=Math.atan(dY /dX);
		if(dX>0 && dY>0)
			return a;//α=θ; returnα;
		else if(dX>0 && dY<0)
			return a+Math.PI*2;//α=θ+2π; returnα;
		else if(dX<0)
			return a+Math.PI;//α=θ+π; returnα;
		return -1;
	}

}
