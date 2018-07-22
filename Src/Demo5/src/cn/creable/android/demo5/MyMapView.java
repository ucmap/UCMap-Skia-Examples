package cn.creable.android.demo5;
import android.content.Context;
import android.os.Environment;
import android.util.AttributeSet;
import cn.creable.gridgis.controls.MapControl;
import cn.creable.gridgis.controls.MapView;


public class MyMapView extends MapView {
	
	private boolean isLoaded;

	public MyMapView(Context context, AttributeSet attrs) {
		super(context, attrs);
		isLoaded=false;
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		if (this.isInEditMode()) return;
		MapControl mapControl=getMapControl();
		if (isLoaded==false)
		{
			String path=Environment.getExternalStorageDirectory().getPath();
			mapControl.loadMap(path+"/bj/map.ini", (byte)0);
			mapControl.setPanTool(false,2);
			isLoaded=true;
		}
	}

}
