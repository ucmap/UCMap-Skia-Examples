package cn.creable.android.demo6;

import android.graphics.Canvas;
import cn.creable.gridgis.controls.IMapTool;
import cn.creable.gridgis.controls.IMapTool2;
import cn.creable.gridgis.controls.MapControl;
import cn.creable.gridgis.controls.PanTool;

public class MergedTool implements IMapTool, IMapTool2 {
	
	private PanTool panTool;
	private ClickTool clickTool;
	private int x,y;
	private int tol=5;
	private boolean flag=false;
	
	public MergedTool(MapControl mapControl,MyCustomDraw mcd)
	{
		this.panTool=new PanTool(mapControl);
		this.clickTool=new ClickTool(mapControl,mcd);
	}

	@Override
	public int getLongPressTime() {
		return clickTool.getLongPressTime();
	}

	@Override
	public int getLongPressTolerance() {
		return clickTool.getLongPressTolerance();
	}

	@Override
	public void onLongPressed() {
		clickTool.onLongPressed();
	}

	@Override
	public void onRemove() {
		// TODO Auto-generated method stub

	}

	@Override
	public void action() {
		// TODO Auto-generated method stub

	}

	@Override
	public void draw(Canvas g) {
		if (flag==true)
			panTool.draw(g);
	}

	@Override
	public boolean keyPressed(int arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void pointerDragged(int x, int y, int x2, int y2) {
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
//				infoTool.pointerPressed(this.x, this.y, -1, -1);
			}
		}

		if (flag==true)
			panTool.pointerDragged(x, y, x2, y2);
		else
			clickTool.pointerDragged(x, y, x2, y2);
	}

	@Override
	public void pointerPressed(int x, int y, int x2, int y2) {
		this.x=x;
		this.y=y;
		clickTool.pointerPressed(x, y, x2, y2);
	}

	@Override
	public void pointerReleased(int x, int y, int x2, int y2) {
		if (flag==true)
			panTool.pointerReleased(x, y, x2, y2);
		else
		{
			clickTool.pointerReleased(x, y, x2, y2);
		}
		flag=false;
	}

}
