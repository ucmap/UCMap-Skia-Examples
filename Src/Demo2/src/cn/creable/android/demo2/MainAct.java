package cn.creable.android.demo2;

import java.util.Vector;

import cn.creable.gridgis.controls.App;
import cn.creable.gridgis.controls.CustomDrawGeometrySelector;
import cn.creable.gridgis.controls.ICustomDraw;
import cn.creable.gridgis.controls.ICustomDrawDataCenter;
import cn.creable.gridgis.controls.MapView;
import cn.creable.gridgis.geometry.IEnvelope;
import cn.creable.gridgis.geometry.IPoint;
import cn.creable.gridgis.geometry.Point;
import cn.creable.ucmap.ILocalSearchListener;
import cn.creable.ucmap.IPathSearchListener;
import cn.creable.ucmap.MapLoader;
import cn.creable.ucmap.OpenSourceMapLayer;
import cn.creable.ucmap.OpenSourceMapLayer.Path;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class MainAct extends Activity {
	MapView mapView;
	ProgressDialog dlg;
	Activity act;
	PathTool pathTool;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        pathTool=null;
        act=this;
        mapView=(MapView)findViewById(R.id.mapView);
    }
    
    @Override
	public boolean onCreateOptionsMenu(Menu menu) {
		//menu.add(0, 0, 0, "装入地图");
		menu.add(0, 1, 0, "放大地图");
		menu.add(0, 2, 0, "缩小地图");
		menu.add(0, 3, 0, "平移地图");
		menu.add(0,12,0,"点击查询坐标");
		menu.add(0,10,0,"用代码控制视图");
		menu.add(0, 11, 0, "查看gps点");
		menu.add(0, 4, 0, "关键字查询");
		menu.add(0, 5, 0, "路径查询");
		menu.add(0, 6, 0, "切换地图数据源");
		menu.add(0, 7, 0, "切换地图模式");
		menu.add(0, 8, 0, "退出");
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId())
		{
		case 0:	//装入地图
			MapLoader.loadMapXML(mapView.getMapControl(), "/sdcard/OpenSourceMap.xml");
			mapView.getMapControl().setCustomDraw(new GPSCustomDraw(mapView.getMapControl()));
			mapView.getMapControl().setPanTool();
			break;
		case 1:	//放大地图
			mapView.getMapControl().setZoomInTool();
			break;
		case 2:	//缩小地图
			mapView.getMapControl().setZoomOutTool();
			break;
		case 3:	//平移地图
			mapView.getMapControl().setPanTool();
			break;
		case 10://用代码视图控制
		{
			mapView.getMapControl().getDisplay().getDisplayTransformation().setZoom(10);//直接设置显示比例尺，实现放大到想要的比例，如果仅仅需要居中，可以注释掉这一行
			//以下代码实现以(117,30)为中心居中
			OpenSourceMapLayer oslayer=(OpenSourceMapLayer)mapView.getMapControl().getMap().getLayer(0);
			Point pt=oslayer.fromLonLat(117,30);
			Point offset=oslayer.getOffset(117,30);
			pt.setX(pt.getX()+offset.getX());
			pt.setY(pt.getY()+offset.getY());
			IEnvelope env=mapView.getMapControl().getExtent();//TODO:注意，如果你需要移动中心点时并setZoom，getExtent必须在setZoom之后调用才行
			env.centerAt(pt);
			mapView.getMapControl().refresh(env);
			break;
		}
		case 4:	//关键字查询
			if (mapView.getMapControl().getMap().getLayerCount()>0)
			{
				OpenSourceMapLayer oslayer=(OpenSourceMapLayer)mapView.getMapControl().getMap().getLayer(0);
				oslayer.setLocalSearchListener(new ILocalSearchListener(){

					@Override
					public void localSearchFinished(Vector pois) {
						if (pois==null) return;
						int size=pois.size();
						StringBuilder sb=new StringBuilder();
						for (int i=0;i<size;++i)
						{
							cn.creable.ucmap.OpenSourceMapLayer.POI poi=(cn.creable.ucmap.OpenSourceMapLayer.POI)pois.get(i);
							sb.append(poi.title);
							sb.append("\n");
						}
						sb.deleteCharAt(sb.length()-1);
						//利用Toast显示查询到的信息点的名字
						Bundle b=new Bundle();
						b.putString("string",sb.toString());
						Message msg=new Message();
						msg.what=1;
						msg.setData(b);
						handler.sendMessage(msg);
						sb=null;
					}
					
				});
				oslayer.localSearch("南京,中山", 0);
				//显示等待界面
				dlg = new ProgressDialog(act);   
				dlg.setProgressStyle(ProgressDialog.STYLE_SPINNER);   
				dlg.setTitle("提示");   
				dlg.setMessage("正在进行关键字查询，请稍后");   
				dlg.setIcon(R.drawable.icon);   
				dlg.setIndeterminate(false);   
				dlg.setCancelable(true);
				dlg.setButton("取消 ", new DialogInterface.OnClickListener(){

					@Override
					public void onClick(DialogInterface dialog, int which) {
						OpenSourceMapLayer oslayer=(OpenSourceMapLayer)mapView.getMapControl().getMap().getLayer(0);
						oslayer.cancel();
					}
					
				});
				dlg.show();   
			}
			break;
		case 5:	//路径查询
			if (mapView.getMapControl().getMap().getLayerCount()>0)
			{
				OpenSourceMapLayer oslayer=(OpenSourceMapLayer)mapView.getMapControl().getMap().getLayer(0);
				if (pathTool==null)
				{
					pathTool=new PathTool(mapView.getMapControl(),oslayer,act);
					mapView.getMapControl().addCustomDraw(pathTool);
				}
				mapView.getMapControl().setCurrentTool(pathTool);
				mapView.getMapControl().refresh();
//				oslayer.setPathSearchListener(new IPathSearchListener(){
//
//					@Override
//					public void pathSearchFinished(OpenSourceMapLayer$Path path) {
//						if (path==null) return;
//						int size=path.markArray.length;
//						StringBuilder sb=new StringBuilder();
//						sb.append("路径名称:");
//						sb.append(path.name);
//						sb.append("\n");
//						for (int i=0;i<size;++i)
//						{
//							sb.append("拐点名称:");
//							sb.append(path.markArray[i].name);
//							sb.append("\n");
//						}
//						sb.deleteCharAt(sb.length()-1);
//						//利用Toast显示查询到的路径名以及拐点名
//						Bundle b=new Bundle();
//						b.putString("string",sb.toString());
//						Message msg=new Message();
//						msg.what=1;
//						msg.setData(b);
//						handler.sendMessage(msg);
//						sb=null;
//					}
//					
//				});
//				oslayer.getPath(118.855484f,32.055214f,118.733574f,32.087738f,false);
//				//显示等待界面
//				dlg = new ProgressDialog(act);   
//				dlg.setProgressStyle(ProgressDialog.STYLE_SPINNER);   
//				dlg.setTitle("提示");   
//				dlg.setMessage("正在进行路径查询，请稍后");   
//				dlg.setIcon(R.drawable.icon);   
//				dlg.setIndeterminate(false);   
//				dlg.setCancelable(true);
//				dlg.setButton("取消 ", new DialogInterface.OnClickListener(){
//
//					@Override
//					public void onClick(DialogInterface dialog, int which) {
//						OpenSourceMapLayer oslayer=(OpenSourceMapLayer)mapView.getMapControl().getMap().getLayer(0);
//						oslayer.cancel();
//					}
//					
//				});
//				dlg.show();   
			}
			break;
		case 6:	//切换地图数据源
			if (mapView.getMapControl().getMap().getLayerCount()>0)
			{
				OpenSourceMapLayer oslayer=(OpenSourceMapLayer)mapView.getMapControl().getMap().getLayer(0);
				switch (oslayer.getMapMode())
				{
				case GOOGLE:
					oslayer.setMapMode(1);break;
				case BING:
					oslayer.setMapMode(0);break;
				}
				mapView.getMapControl().refresh();
			}
			break;
		case 7:	//切换地图模式
			if (mapView.getMapControl().getMap().getLayerCount()>0)
			{
				OpenSourceMapLayer oslayer=(OpenSourceMapLayer)mapView.getMapControl().getMap().getLayer(0);
				switch (oslayer.getMode())
				{
				case 0:
					oslayer.setMode(1);
					break;
				case 1:
					oslayer.setMode(0);
					break;
				}
				mapView.getMapControl().refresh();
			}
			break;
		case 8:	//退出
			System.exit(0);
			break;
		case 11:
			ICustomDraw draw=mapView.getMapControl().getCustomDraw();
			if (draw!=null && draw instanceof ICustomDrawDataCenter)
			{
				CustomDrawGeometrySelector s=new CustomDrawGeometrySelector(mapView.getMapControl(),(ICustomDrawDataCenter)draw);
				s.setOffset(0, 60);
				mapView.getMapControl().setCurrentTool(s);
			}
			break;
		case 12:
			mapView.getMapControl().setCurrentTool(new PointTool(mapView.getMapControl()));
			break;
		}
		return super.onOptionsItemSelected(item);
	}
	
	
	@Override
	protected void onResume() {
		if (mapView!=null && mapView.getMapControl()!=null) mapView.getMapControl().refresh(mapView);
		super.onResume();
	}
	
	private Handler handler=new Handler(){
		public void handleMessage(Message msg) 
		{    
            switch (msg.what) 
            {    
            case 1:
            	//关闭等待界面
            	dlg.cancel();
            	dlg=null;
            	//利用Toast显示查询的结果
            	Toast.makeText(App.getInstance(), msg.getData().getString("string"), Toast.LENGTH_SHORT).show();
            	break;
            }
		}
	};
}