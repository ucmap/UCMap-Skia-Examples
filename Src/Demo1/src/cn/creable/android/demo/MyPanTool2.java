package cn.creable.android.demo;

import cn.creable.gridgis.controls.MapControl;
import cn.creable.gridgis.controls.PanTool;
import cn.creable.gridgis.geometry.IEnvelope;

public class MyPanTool2 extends PanTool {
	
	private boolean flag=true;
	
	private IEnvelope maxExtent;

	@Override
	public void pointerDragged(int arg0, int arg1, int arg2, int arg3) {
		if (flag) super.pointerDragged(arg0, arg1, arg2, arg3);
	}

	@Override
	public void pointerPressed(int arg0, int arg1, int arg2, int arg3) {
		
		super.pointerPressed(arg0, arg1, arg2, arg3);
	}

	@Override
	public void pointerReleased(int arg0, int arg1, int arg2, int arg3) {
		if (flag) super.pointerReleased(arg0, arg1, arg2, arg3);
	}

	public MyPanTool2(MapControl mapControl,IEnvelope maxExtent) {
		super(mapControl);
		this.maxExtent=maxExtent;
	}

}
