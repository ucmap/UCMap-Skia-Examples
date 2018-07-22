package cn.creable.android.demo9;

import cn.creable.gridgis.controls.IMapTool;
import cn.creable.gridgis.controls.IMapViewListener;
import cn.creable.gridgis.controls.MapControl;
import cn.creable.gridgis.controls.MapView;
import cn.creable.gridgis.geometry.GeometryType;
import cn.creable.gridgis.geometry.IPoint;
import cn.creable.gridgis.geometry.LineString;
import cn.creable.gridgis.geometry.LinearRing;
import cn.creable.gridgis.geometry.Polygon;
import cn.creable.gridgis.shapefile.ShapefileLayer;
import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ZoomControls;
import android.graphics.Color;

public class MainActivity extends Activity {
	
	Activity act;
	
	MapView mapView;

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
				mapControl.showScaleBar(8, getResources().getDisplayMetrics().xdpi/2.54f, 10, mapControl.getHeight()-10, Color.BLACK,Color.RED,3,20);
				if (mapControl.getMap()==null)
				{
					mapControl.loadMap(Environment.getExternalStorageDirectory().getPath()+"/demoMap/map.ini", (byte)0);
					mapControl.setPanTool();
				}
			}
        	
        });
        
        Button btn1=(Button)findViewById(R.id.button1);
        btn1.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				MyAddFeatureTool tool=new MyAddFeatureTool(mapView.getMapControl(),act);
				mapView.getMapControl().setCurrentTool(tool);
			}
        	
        });
        
        Button btn2=(Button)findViewById(R.id.button2);
        btn2.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				IMapTool mtool=mapView.getMapControl().getCurrentTool();
				if (mtool instanceof MyAddFeatureTool)
				{
					MyAddFeatureTool tool=(MyAddFeatureTool)mtool;
					tool.inputBPoint(0);
				}
			}
        	
        });
        
        Button btn3=(Button)findViewById(R.id.button3);
        btn3.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				IMapTool mtool=mapView.getMapControl().getCurrentTool();
				if (mtool instanceof MyAddFeatureTool)
				{
					MyAddFeatureTool tool=(MyAddFeatureTool)mtool;
					tool.inputBPoint(1);
				}
			}
        	
        });
        
        Button btn4=(Button)findViewById(R.id.button4);
        btn4.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				IMapTool mtool=mapView.getMapControl().getCurrentTool();
				if (mtool instanceof MyAddFeatureTool)
				{
					ShapefileLayer layer=(ShapefileLayer)mapView.getMapControl().getMap().getLayer("耕地");
					MyAddFeatureTool tool=(MyAddFeatureTool)mtool;
					IPoint[] pts=tool.getPoints(GeometryType.Polygon);
					Polygon pg=new Polygon(new LinearRing(pts,pts.length));
					pg.recalcEnvelope();
					layer.addFeature(pg, null);
					mapView.getMapControl().refresh();
					tool.reset();
				}
			}
        	
        });
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, 100, 0, "退出");
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId())
		{
		case 100:
			System.exit(0);
			break;
		}
		return super.onOptionsItemSelected(item);
	}

}
