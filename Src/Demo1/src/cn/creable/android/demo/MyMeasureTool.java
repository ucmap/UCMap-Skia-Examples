package cn.creable.android.demo;

import android.graphics.Canvas;
import cn.creable.gridgis.controls.IInfoToolListener;
import cn.creable.gridgis.controls.IMapTool;
import cn.creable.gridgis.controls.IMapTool2;
import cn.creable.gridgis.controls.InfoTool;
import cn.creable.gridgis.controls.MapControl;
import cn.creable.gridgis.controls.MeasureTool;
import cn.creable.gridgis.controls.PanTool;

public class MyMeasureTool implements IMapTool,IMapTool2 {
	
	public PanTool panTool;
	public MeasureTool2 mTool;
	public int x,y;
	public int tol=5;
	private boolean flag=false;
	private MapControl mapControl;
	private float offsetx,offsety;
	
	private boolean hasTwo=false;
	
	public MyMeasureTool(MapControl mapControl,int type)
	{
		this.mapControl=mapControl;
		panTool=new PanTool(mapControl);
		mTool=new MeasureTool2(mapControl,type);
		mTool.selector.openMultipleSelection(0);
	}

	@Override
	public void pointerDragged(int x, int y, int x2, int y2) {
		this.offsetx=x-this.x;
		offsety=y-this.y;
		if (flag==false)
		{
			if (Math.abs(x-this.x)>tol || Math.abs(y-this.y)>tol)
			{
				flag=true;
				panTool.pointerPressed(this.x, this.y, -1, -1);
			}
			else
			{
				flag=false;
				mTool.pointerPressed(this.x, this.y, -1, -1);
			}
		}

		if (flag==true)
			panTool.pointerDragged(x, y, x2, y2);
		if (x2!=-1)
			hasTwo=true;
//		else
//			infoTool.pointerDragged(x, y, x2, y2);
	}

	@Override
	public void pointerPressed(int x, int y, int x2, int y2) {
		this.x=x;
		this.y=y;
		hasTwo=false;
	}

	@Override
	public void pointerReleased(int x, int y, int x2, int y2) {
		offsetx=0;
		offsety=0;
		hasTwo=false;
		if (flag==true)
			panTool.pointerReleased(x, y, x2, y2);
		else
		{
			mTool.pointerPressed(this.x, this.y, x2, y2);
			mTool.pointerDragged(x, y, x2, y2);
			mTool.pointerReleased(x, y, x2, y2);
		}
		flag=false;
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
		if (flag==true)
			panTool.draw(g);
		//else
		if (!hasTwo)
			mTool.draw(g,offsetx,offsety);
	}

	@Override
	public int getLongPressTime() {
		return mTool.getLongPressTime();
	}

	@Override
	public int getLongPressTolerance() {
		return mTool.getLongPressTolerance();
	}

	@Override
	public void onLongPressed() {
		mTool.onLongPressed();
	}

	@Override
	public void onRemove() {
		mTool.onRemove();
	}

}
