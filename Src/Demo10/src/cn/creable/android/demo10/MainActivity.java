package cn.creable.android.demo10;

import cn.creable.gridgis.controls.IMapViewListener;
import cn.creable.gridgis.controls.MapControl;
import cn.creable.gridgis.controls.MapView;
import cn.creable.gridgis.controls.MapView2;
import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.graphics.Color;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends Activity {
	
	private MapView mapView;
	private MapView2 mapView2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		mapView=(MapView)findViewById(R.id.mapView);
        mapView.setListener(new IMapViewListener() {

			@Override
			public void onSizeChanged(int w, int h, int oldw, int oldh) {
				MapControl mapControl=mapView.getMapControl();
				mapControl.showScaleBar(8, getResources().getDisplayMetrics().xdpi/2.54f, 10, mapControl.getHeight()-10, Color.BLACK,Color.RED,3,20);
				if (mapControl.getMap()==null)
				{
					mapControl.loadMap(Environment.getExternalStorageDirectory().getPath()+"/bj2/map.ini", (byte)0);
					mapControl.setPanTool();
				}
			}
        	
        });
        
        mapView2=(MapView2)findViewById(R.id.mapView2);
        mapView2.setListener(new IMapViewListener() {

			@Override
			public void onSizeChanged(int w, int h, int oldw, int oldh) {
				MapControl mapControl=mapView2.getMapControl();
				mapControl.showScaleBar(8, getResources().getDisplayMetrics().xdpi/2.54f, 10, mapControl.getHeight()-10, Color.BLACK,Color.RED,3,20);
				if (mapControl.getMap()==null)
				{
					mapControl.loadMap(Environment.getExternalStorageDirectory().getPath()+"/lujiang/map.ini", (byte)0);
					mapControl.setPanTool();
				}
			}
        	
        });
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0,1,0,"πÿ±’”“±ﬂµÿÕº");
		menu.add(0,2,0,"œ‘ æ”“±ﬂµÿÕº");
		menu.add(0, 100, 0, "ÕÀ≥ˆ");
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId())
		{
		case 1:
			//mapView.getMapControl().loadMap(Environment.getExternalStorageDirectory().getPath()+"/lujiang/map.ini", (byte)0);
			//mapView2.getMapControl().closeMap();
			setContentView(R.layout.activity_main2);
			break;
		case 2:
			setContentView(R.layout.activity_main);
			break;
		case 100:
			System.exit(0);
			break;
		}
		return super.onOptionsItemSelected(item);
	}

}
