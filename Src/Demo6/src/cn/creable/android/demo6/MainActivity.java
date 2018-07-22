package cn.creable.android.demo6;

import cn.creable.gridgis.controls.App;
import cn.creable.gridgis.controls.IMapViewListener;
import cn.creable.gridgis.controls.MapControl;
import cn.creable.gridgis.controls.MapView;
import cn.creable.gridgis.util.Image;
import cn.creable.ucmap.ComparisonOperator;
import cn.creable.ucmap.GetFeatureResult;
import cn.creable.ucmap.MapLoader;
import cn.creable.ucmap.QueryLayerParam;
import cn.creable.ucmap.QueryParam;
import cn.creable.ucmap.WMSLayer;
import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ZoomControls;

public class MainActivity extends Activity {
	
	MapView mapView;
	MyCustomDraw mcd;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		//设置放大缩小按钮事件
  		ZoomControls zc=(ZoomControls)findViewById(R.id.zoomControls);
  		zc.setOnZoomInClickListener(new View.OnClickListener() {
  			
  			@Override
  			public void onClick(View v) {
  				mapView.getMapControl().setZoomInTool();
  				mapView.getMapControl().getCurrentTool().action();
  				//这里直接将融合工具设置为当前地图工具
  				mapView.getMapControl().setCurrentTool(new MergedTool(mapView.getMapControl(),mcd));
  			}
  		});
  		zc.setOnZoomOutClickListener(new View.OnClickListener() {
  			
  			@Override
  			public void onClick(View v) {
  				mapView.getMapControl().setZoomOutTool();
  				mapView.getMapControl().getCurrentTool().action();
  				//这里直接将融合工具设置为当前地图工具
  				mapView.getMapControl().setCurrentTool(new MergedTool(mapView.getMapControl(),mcd));
  			}
  		});
        
        mapView=(MapView)findViewById(R.id.mapView);
        mapView.setListener(new IMapViewListener() {

			@Override
			public void onSizeChanged(int w, int h, int oldw, int oldh) {
				MapControl mapControl=mapView.getMapControl();
				mapControl.drawOnScreen=true;//开启直接绘制到屏幕上模式
				mapControl.showScaleBar(8, getResources().getDisplayMetrics().xdpi/2.54f, 10, mapControl.getHeight()-10, Color.BLACK,Color.RED,3,20);
				if (mapControl.getMap()==null)
				{
					String path=Environment.getExternalStorageDirectory().getPath();
					boolean b=MapLoader.loadMapXML(mapControl, path+"/tiger-ny.xml");
					mapControl.getMap().setMapUnits(1);//设置为经纬度坐标
					mcd=new MyCustomDraw(mapControl);
					mapControl.setCustomDraw(mcd);
					//这里直接将融合工具设置为当前地图工具
					mapControl.setCurrentTool(new MergedTool(mapControl,mcd));
				}
			}
        	
        });
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0,1,0,"画圆");
		menu.add(0,2,0,"画多边形");
		menu.add(0,3,0,"画矩形");
		menu.add(0,4,0,"传入图标和文字");
		menu.add(0,5,0,"传入多边形");
		//menu.add(0,6,0,"点击传入的点");
		menu.add(0, 100, 0, "退出");
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId())
		{
		case 1:
//			QueryParam params=new QueryParam();
//			//params.setMaxFeatures(1);
//			QueryLayerParam param=params.createAndAddQueryLayerParam("彭水地图:XZQ");
//			param.AttFilterInsert("行政区名称", ComparisonOperator.NotEqual, "123", null);
//			WMSLayer layer=(WMSLayer)mapView.getMapControl().getMap().getLayer(0);
//			GetFeatureResult gfr=layer.getFeature(params);
//			System.out.println(gfr);
			DrawTool tool=new DrawTool(mapView.getMapControl(),0);
			tool.selector.setOffset(0, 80);
			mapView.getMapControl().setCurrentTool(tool);
			break;
		case 2:
			DrawTool tool1=new DrawTool(mapView.getMapControl(),1);
			tool1.selector.setOffset(0, 80);
			mapView.getMapControl().setCurrentTool(tool1);
			break;
		case 3:
			DrawTool tool11=new DrawTool(mapView.getMapControl(),2);
			tool11.selector.setOffset(0, 80);
			mapView.getMapControl().setCurrentTool(tool11);
			break;
		case 4:
			POI poi=new POI();
			poi.x=-73.981840;
			poi.y=40.764927;
			poi.text="测试测试";
			BitmapDrawable bmpDraw=(BitmapDrawable)App.getInstance().getResources().getDrawable(R.drawable.gps);
			poi.img=new Image(bmpDraw.getBitmap());
			MyCustomDraw mcd=(MyCustomDraw)mapView.getMapControl().getCustomDraw();
			mcd.addPOI(poi);
			mapView.getMapControl().repaint();
			break;
		case 5:
			Area area=new Area();
			area.points=new double[]{-73.983471,40.777716,-73.973,40.789474,-73.965103,40.780033,-73.965361,40.770678};
			area.fillColor=0x80FC7F43;
			area.lineColor=0xFFFC7F43;
			MyCustomDraw mcd1=(MyCustomDraw)mapView.getMapControl().getCustomDraw();
			mcd1.addArea(area);
			mapView.getMapControl().repaint();
			break;
//		case 6:
//			if (mapView.getMapControl().getCustomDraw()!=null && 
//					mapView.getMapControl().getCustomDraw() instanceof MyCustomDraw)
//			{
//				ClickTool tool2=new ClickTool(mapView.getMapControl(),(MyCustomDraw)mapView.getMapControl().getCustomDraw());
//				mapView.getMapControl().setCurrentTool(tool2);
//			}
//			break;
		case 100:
			System.exit(0);
			break;
		}
		return super.onOptionsItemSelected(item);
	}

}
