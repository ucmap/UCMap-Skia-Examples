package cn.creable.android.demo7;

import cn.creable.gridgis.controls.App;
import cn.creable.gridgis.controls.IMapViewListener;
import cn.creable.gridgis.controls.MapControl;
import cn.creable.gridgis.controls.MapView;
import cn.creable.gridgis.mapLayer.ILayer;
import cn.creable.gridgis.shapefile.IShapefileLayer;
import cn.creable.ucmap.MapLoader;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ZoomControls;

public class MainActivity extends Activity {
	
	MapView mapView;
	Activity act;
	PathAnalysisTool pathTool;
	
	String mapPath;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		act=this;
		mapPath=Environment.getExternalStorageDirectory().getPath()+"/lujiang";
		//设置放大缩小按钮事件
  		ZoomControls zc=(ZoomControls)findViewById(R.id.zoomControls);
  		zc.setOnZoomInClickListener(new View.OnClickListener() {
  			
  			@Override
  			public void onClick(View v) {
  				mapView.getMapControl().setZoomInTool();
  				mapView.getMapControl().getCurrentTool().action();
  				mapView.getMapControl().setPanTool(true);
  			}
  		});
  		zc.setOnZoomOutClickListener(new View.OnClickListener() {
  			
  			@Override
  			public void onClick(View v) {
  				mapView.getMapControl().setZoomOutTool();
  				mapView.getMapControl().getCurrentTool().action();
  				mapView.getMapControl().setPanTool(true);
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
					mapControl.loadMap(mapPath+"/map.ini", (byte)0);
					mapControl.setPanTool(true);
				}
			}
        	
        });
        
        Button btn1=(Button)findViewById(R.id.button1);
        btn1.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String path=Environment.getExternalStorageDirectory().getPath();
				MapControl mapControl=mapView.getMapControl();
				mapControl.loadMap(path+"/bj2/map.ini", (byte)0);
				mapControl.setPanTool();
				Intent intent=new Intent(App.getInstance(), MainActivity2.class);
				startActivity(intent); 
			}
        	
        });
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0,1,0,"路径分析");
		menu.add(0, 2, 0, "更新地图");
		menu.add(0, 3, 0, "更新DT_ROAD_polyline图层");
		menu.add(0, 100, 0, "退出");
		return true;
	}
	
	private ProgressDialog m_pDialog;
	
	private Handler handler = new Handler() {

		public void handleMessage(Message msg) {
			switch (msg.what) {
				case 1:
					if (msg.arg1==1)
					{
						final int mapVersion=msg.arg2;
					new AlertDialog.Builder(act)
							.setTitle("地图有更新，确认更新吗？")
							.setIcon(android.R.drawable.ic_dialog_info)
							.setPositiveButton("确定",
									new DialogInterface.OnClickListener() {

										@Override
										public void onClick(
												DialogInterface dialog,
												int which) {
											
											m_pDialog=null;
									        // 创建ProgressDialog对象   
									        m_pDialog = new ProgressDialog(act);   
									          
									        // 设置进度条风格，风格为圆形   
									        m_pDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);   
									          
									        // 设置ProgressDialog 标题   
									        m_pDialog.setTitle("提示");   
									          
									        // 设置ProgressDialog 提示信息   
									        m_pDialog.setMessage("正在下载地图");   
									          
									        // 设置ProgressDialog 标题图标   
									        m_pDialog.setIcon(R.drawable.ic_launcher);   
									          
									        // 设置ProgressDialog 进度条进度   
									        m_pDialog.setProgress(100);   
									          
									        // 设置ProgressDialog 的进度条是否不明确   
									        m_pDialog.setIndeterminate(false);   
									          
									        // 设置ProgressDialog 是否可以按退回按键取消   
									        m_pDialog.setCancelable(false);   
									          
									        // 让ProgressDialog显示   
									        m_pDialog.show();   
											
									        mapView.getMapControl().closeMap();//更新地图前，先关闭地图
											MapUpdater mu = new MapUpdater(mapVersion);
											mu.update(mapPath,handler);
										}
									})
							.setNegativeButton("返回",
									new DialogInterface.OnClickListener() {

										@Override
										public void onClick(
												DialogInterface dialog,
												int which) {

										}
									}).show();
						
					}
					else if (msg.arg1==0)
					{
						AlertDialog.Builder builder1 = new AlertDialog.Builder(act);
    					builder1.setTitle("信息");
    					builder1.setMessage("您的地图是最新的");
    					builder1.setCancelable(true);
    					builder1.setPositiveButton("OK", null);
    					builder1.create().show();
					}
					break;
				case 2:
					m_pDialog.setProgress((int)((double)msg.arg1/msg.arg2*100));
					break;
				case 3:
					m_pDialog.setProgress(100);
					m_pDialog.setMessage("正在删除旧地图");
					break;
				case 4:
					m_pDialog.setMessage("正在解压缩新地图");
					break;
				case 5:
					mapView.getMapControl().loadMap(mapPath+"/map.ini", (byte)0);
					m_pDialog.setMessage("地图更新完成");
					m_pDialog.setCancelable(true);//完成之后，用户可以按下back键返回地图
					//m_pDialog.cancel();
					break;
				case 6:
					if (msg.arg1>0)
					{
						m_pDialog.setProgress(100);
						mapView.getMapControl().loadMap(mapPath+"/map.ini", (byte)0);
						m_pDialog.setMessage("图层更新完成");
						m_pDialog.setCancelable(true);//完成之后，用户可以按下back键返回地图
					}
					else
					{
						m_pDialog.setMessage("图层更新失败，请检查是否含有图层名.dat和图层名.bin文件");
						m_pDialog.setCancelable(true);//完成之后，用户可以按下back键返回地图
					}
					break;
			}
		};
	};
	
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
		case 2:
			MapUpdater mu=new MapUpdater(0);
			mu.check(mapPath,handler);
			break;
		case 3:
		{
			m_pDialog=null;
	        // 创建ProgressDialog对象   
	        m_pDialog = new ProgressDialog(act);   
	          
	        // 设置进度条风格，风格为圆形   
	        m_pDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);   
	          
	        // 设置ProgressDialog 标题   
	        m_pDialog.setTitle("提示");   
	          
	        // 设置ProgressDialog 提示信息   
	        m_pDialog.setMessage("正在更新图层");   
	          
	        // 设置ProgressDialog 标题图标   
	        m_pDialog.setIcon(R.drawable.ic_launcher);   
	          
	        // 设置ProgressDialog 进度条进度   
	        m_pDialog.setProgress(100);   
	          
	        // 设置ProgressDialog 的进度条是否不明确   
	        m_pDialog.setIndeterminate(true);   
	          
	        // 设置ProgressDialog 是否可以按退回按键取消   
	        m_pDialog.setCancelable(false);   
	          
	        // 让ProgressDialog显示   
	        m_pDialog.show();   
			new Thread() {
				@Override
				public void run() {
					ILayer layer=mapView.getMapControl().getMap().getLayer("DT_ROAD_polyline");
					int ret=0;
					if (layer instanceof IShapefileLayer)
						ret=((IShapefileLayer)layer).update();
					Message msg=new Message();
					msg.what=6;
					msg.arg1=ret;
					handler.sendMessage(msg);
				}
			}.start();
			break;
		}
		case 100:
			System.exit(0);
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onResume() {
		if (mapView!=null && mapView.getMapControl()!=null) 
		{
			MapControl mapControl=mapView.getMapControl();
			String path=Environment.getExternalStorageDirectory().getPath();
			mapControl.loadMap(path+"/lujiang/map.ini", (byte)0);
			mapControl.setPanTool();
			mapView.getMapControl().refresh(mapView);
		}
		super.onResume();
	}

}
