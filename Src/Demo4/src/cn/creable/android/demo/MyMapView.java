package cn.creable.android.demo;
import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Color;
import android.os.Environment;
import android.util.AttributeSet;
import android.view.MotionEvent;
import cn.creable.gridgis.controls.App;
import cn.creable.gridgis.controls.MapControl;
import cn.creable.gridgis.controls.MapView;
import cn.creable.gridgis.display.IDisplayListener;
import cn.creable.gridgis.display.IDisplayTransformation;
import cn.creable.gridgis.shapefile.ShapefileLayer;

public class MyMapView extends MapView implements IDisplayListener {

	public MyMapView(Context context, AttributeSet attrs) {
		super(context, attrs);

	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		if (this.isInEditMode()) return;
		MapControl mapControl=getMapControl();
		if (mapControl!=null)
		{//如果地图控件不为null，则需要等待刷线地图线程执行完毕
			try{
				while (mapControl.getRefreshManager().isThreadRunning())
					Thread.sleep(200);
			}catch(Exception ex){ex.printStackTrace();}
		}
		super.onSizeChanged(w, h, oldw, oldh);
		
		mapControl=getMapControl();
		//以下代码会在地图窗口的左下角显示一个比例尺符号，具体参数说明请参考doc
		//getResources().getDisplayMetrics().xdpi 获取了屏幕的横向dpi，乘以2.54可以得到1厘米有多少个像素点
		mapControl.showScaleBar(8, getResources().getDisplayMetrics().xdpi/2.54f, 10, mapControl.getHeight()-10, Color.BLACK,Color.RED,3,20);
		if (mapControl.getMap()==null)
		{
			mapControl.setDisplayListener(this);
			
			String path=Environment.getExternalStorageDirectory().getPath();
			mapControl.loadMap(path+"/bj/map.ini", (byte)0);
			
			mapControl.setPanTool();
			
			//开启undo redo功能，并设置最多可以undo 100次
			ShapefileLayer.openUndoRedo(100);
		}
	}

	@Override
	public void onDisplayNotify(IDisplayTransformation dt, long costTime) {
		System.out.println(costTime);
	}

	@Override
	public boolean onTouchEvent(MotionEvent arg0) {
		if (MainAct.myt!=null)
		{
			MainAct.myt.cancel();
			MainAct.myt=null;
		}
		return super.onTouchEvent(arg0);
	}

}
