package cn.creable.android.demo7;

import cn.creable.gridgis.controls.App;
import cn.creable.gridgis.controls.IMapViewListener;
import cn.creable.gridgis.controls.MapControl;
import cn.creable.gridgis.controls.MapView;
import cn.creable.ucmap.MapLoader;
import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.graphics.Color;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ZoomControls;

public class MainActivity2 extends Activity {
	
	MapView mapView;
	Activity act;
	PathAnalysisTool pathTool;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		act=this;
		//设置放大缩小按钮事件
  		ZoomControls zc=(ZoomControls)findViewById(R.id.zoomControls);
  		zc.setOnZoomInClickListener(new View.OnClickListener() {
  			
  			@Override
  			public void onClick(View v) {
  				mapView.getMapControl().setZoomInTool();
  				mapView.getMapControl().getCurrentTool().action();
  				mapView.getMapControl().setPanTool();
  			}
  		});
  		zc.setOnZoomOutClickListener(new View.OnClickListener() {
  			
  			@Override
  			public void onClick(View v) {
  				mapView.getMapControl().setZoomOutTool();
  				mapView.getMapControl().getCurrentTool().action();
  				mapView.getMapControl().setPanTool();
  			}
  		});
        
        mapView=(MapView)findViewById(R.id.mapView);
        mapView.setListener(new IMapViewListener() {

			@Override
			public void onSizeChanged(int w, int h, int oldw, int oldh) {
				MapControl mapControl=mapView.getMapControl();
				//mapControl.showScaleBar(8, getResources().getDisplayMetrics().xdpi/2.54f, 10, mapControl.getHeight()-10, Color.BLACK,Color.RED,3,20);
				//if (mapControl.getMap()==null)
//				{
//					String path=Environment.getExternalStorageDirectory().getPath();
//					mapControl.loadMap(path+"/bj2/map.ini", (byte)0);
//					mapControl.setPanTool();
//				}
			}
        	
        });
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0,1,0,"路径分析");
		menu.add(0, 100, 0, "退出");
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId())
		{
		case 1:
			if (pathTool==null)
			{
				pathTool=new PathAnalysisTool(mapView.getMapControl(),act);
				mapView.getMapControl().addCustomDraw(pathTool);
			}
			mapView.getMapControl().setCurrentTool(pathTool);
			pathTool.clearPath();
			mapView.getMapControl().refresh();
			break;
		case 100:
			System.exit(0);
			break;
		}
		return super.onOptionsItemSelected(item);
	}

}
