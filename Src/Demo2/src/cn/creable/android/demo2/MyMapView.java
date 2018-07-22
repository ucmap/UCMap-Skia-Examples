package cn.creable.android.demo2;

import android.content.Context;
import android.graphics.Color;
import android.os.Environment;
import android.util.AttributeSet;
import cn.creable.gridgis.controls.MapControl;
import cn.creable.gridgis.controls.MapView;
import cn.creable.ucmap.MapLoader;
import cn.creable.ucmap.OpenSourceMapLayer;

public class MyMapView extends MapView {
	
	public MyMapView(Context context, AttributeSet attrs) {
		super(context, attrs);

	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		if (this.isInEditMode()) return;
		MapControl mapControl=getMapControl();
		
		//以下代码会在地图窗口的左下角显示一个比例尺符号，具体参数说明请参考doc
		//getResources().getDisplayMetrics().xdpi 获取了屏幕的横向dpi，乘以2.54可以得到1厘米有多少个像素点
		mapControl.showScaleBar(8, getResources().getDisplayMetrics().xdpi/2.54f, 10, mapControl.getHeight()-10, Color.BLACK,Color.RED,3,20);
		if (mapControl.getMap()==null)
		{
			//mapControl.drawOnScreen=true;
			String path=Environment.getExternalStorageDirectory().getPath();
			MapLoader.loadMapXML(mapControl, path+"/OpenSourceMap.xml");
			//MapLoader.loadMapXML(mapControl,Environment.getExternalStorageDirectory().getPath()+"/cache/OpenSourceMapWithLocal.xml",null,1,0.00001f,true);
			//mapControl.setCustomDraw(new GPSCustomDraw(mapControl));
			mapControl.setPanTool();
			//((OpenSourceMapLayer)mapControl.getMap().getLayer(0)).openZoomInMode();
			//mapControl.refresh();
		}
	}
}
