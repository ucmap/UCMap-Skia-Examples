package cn.creable.android.demo;
import java.io.File;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Environment;
import android.util.AttributeSet;
import cn.creable.gridgis.controls.App;
import cn.creable.gridgis.controls.MapControl;
import cn.creable.gridgis.controls.MapView;
import cn.creable.gridgis.display.IDisplayListener;
import cn.creable.gridgis.display.IDisplayTransformation;
import cn.creable.gridgis.mapLayer.ILayer;
import cn.creable.ucmap.MapLoader;
import cn.creable.ucmap.WMSLayer;

public class MyMapView extends MapView implements IDisplayListener {
	
	ProgressDialog mDialog;
	Context context;

	public MyMapView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context=context;
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		//if (w<h) return;
		super.onSizeChanged(w, h, oldw, oldh);
		if (this.isInEditMode()) return;
		MapControl mapControl=getMapControl();
		//mapControl.setBackgroundBitmap(BitmapFactory.decodeFile("/sdcard/test.png"),true);
		//以下代码会在地图窗口的左下角显示一个比例尺符号，具体参数说明请参考doc
		//getResources().getDisplayMetrics().xdpi 获取了屏幕的横向dpi，乘以2.54可以得到1厘米有多少个像素点
		mapControl.showScaleBar(8, getResources().getDisplayMetrics().xdpi/2.54f, 10, mapControl.getHeight()-10, Color.BLACK,Color.RED,3,20);
		//以下代码会在地图窗口左上角显示一个指北针
		mapControl.showCompass(0, 0, ((BitmapDrawable)App.getInstance().getResources().getDrawable(R.drawable.compass)).getBitmap());
		if (mapControl.getMap()==null)
		{
			//mapControl.drawOnScreen=true;
			mapControl.setDisplayListener(this);//设置视图变换监听器
			String path=Environment.getExternalStorageDirectory().getPath();
			//MapLoader.loadMapXML(mapControl, path+"/wf.xml");
			//((WMSLayer)mapControl.getMap().getLayer(0)).setOfflineMode(true);
			//mapControl.setPanTool();
			//mapControl.loadMap(path+"/ucdata-all/map.ini","&30sCs&78vPCiEH0");
			//mapControl.loadMap(path+"/bj2/map.ini","11");
			mapControl.loadMap(path+"/bj2/map.ini", (byte)0);
			//MapLoader.loadMapXML(mapControl, Environment.getExternalStorageDirectory().getPath()+"/DLTB.xml");
			//mapControl.loadMap(path+"/WeiFangGZ/wfgzmap/wfgzmap/map.ini", (byte)0);
//			boolean f=MapLoader.loadMapXML(mapControl, Environment.getExternalStorageDirectory().getPath()+"/tianditu.xml");
////		((DisplayTransformation)mapControl.getDisplay().getDisplayTransformation()).minZoom=7.2e-8f;
////		((DisplayTransformation)mapControl.getDisplay().getDisplayTransformation()).fixZoomLevel=false;
//			ILayer layer=null;
//			for (int i=0;i<mapControl.getMap().getLayerCount();++i)
//			{
//				if (mapControl.getMap().getLayer(i) instanceof WMSLayer)
//				{
//					layer=mapControl.getMap().getLayer(i);
//					layer.setVisible(true);
//					break;
//				}
//			}
//			mapControl.getMap().moveLayer(layer, mapControl.getMap().getLayerCount()-1);
//			mapControl.refresh();
			//mapControl.refresh();
			mapControl.setPanTool(false,0);//第二个参数是2表示开启双指旋转功能
			
			//ILayer layer=mapControl.getMap().getLayer("QuXianJie");
			//layer.setVisible(false);
			
//			mapControl.refresh();
//			
//			mDialog = new ProgressDialog(context);   
//	        mDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);   
//	        mDialog.setTitle("提示");   
//	        mDialog.setMessage("正在加载地图，请您稍等");   
//	        mDialog.setIcon(R.drawable.icon);   
//	        mDialog.setProgress(100);   
//	        mDialog.setIndeterminate(false);   
//	        mDialog.setCancelable(false);  
//	        mDialog.show();
		}
	}

	@Override
	public void onDisplayNotify(IDisplayTransformation dt, long costTime) {
		System.out.println(costTime);
//		if (mDialog!=null)
//		{//因为onSizeChanged里new了这个对话框，这里关闭他，并且进行一些对于地图控件的初始化工作
//			mDialog.cancel();
//			mDialog=null;
//			getMapControl().setPanTool();//将pan工具置为当前工具，用户即可缩放、浏览地图
//		}
	}

}
