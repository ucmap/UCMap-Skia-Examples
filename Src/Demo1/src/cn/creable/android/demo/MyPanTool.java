package cn.creable.android.demo;

import android.graphics.Canvas;
import cn.creable.gridgis.controls.IInfoToolListener;
import cn.creable.gridgis.controls.IMapTool;
import cn.creable.gridgis.controls.InfoTool;
import cn.creable.gridgis.controls.MapControl;
import cn.creable.gridgis.controls.PanTool;

public class MyPanTool implements IMapTool {
	
	public PanTool panTool;
	public InfoTool infoTool;
	public int x,y;
	public int tol=5;
	private boolean flag=false;
	private MapControl mapControl;
	
	public MyPanTool(MapControl mapControl,IInfoToolListener itListener)
	{
		panTool=new PanTool(mapControl);
		infoTool=new InfoTool(mapControl,itListener);
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
//		else
//			infoTool.pointerDragged(x, y, x2, y2);
	}

	@Override
	public void pointerPressed(int x, int y, int x2, int y2) {
		this.x=x;
		this.y=y;
	}

	@Override
	public void pointerReleased(int x, int y, int x2, int y2) {
		
		if (flag==true)
			panTool.pointerReleased(x, y, x2, y2);
		else
		{
			infoTool.pointerPressed(this.x, this.y, x2, y2);
			infoTool.pointerReleased(x, y, x2, y2);
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
//		else
//			infoTool.draw(g);
	}

}
