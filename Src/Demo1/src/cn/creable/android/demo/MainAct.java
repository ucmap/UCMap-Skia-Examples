package cn.creable.android.demo;

import cn.creable.arrow.ARROW_STYLE;
import cn.creable.arrow.ArrowTool;
import cn.creable.gridgis.controls.App;
import cn.creable.gridgis.controls.CustomDrawGeometrySelector;
import cn.creable.gridgis.controls.ICustomDraw;
import cn.creable.gridgis.controls.ICustomDrawDataCenter;
import cn.creable.gridgis.controls.IInfoToolListener;
import cn.creable.gridgis.controls.IMapTool;
import cn.creable.gridgis.controls.MapControl;
import cn.creable.gridgis.controls.MapView;
import cn.creable.gridgis.controls.PanTool;
import cn.creable.gridgis.display.FillSymbol;
import cn.creable.gridgis.display.ISymbol;
import cn.creable.gridgis.display.UniqueValueRenderer;
import cn.creable.gridgis.geodatabase.DataProvider;
import cn.creable.gridgis.geodatabase.IFeature;
import cn.creable.gridgis.geometry.IEnvelope;
import cn.creable.gridgis.geometry.Point;
import cn.creable.gridgis.mapLayer.IFeatureLayer;
import cn.creable.gridgis.mapLayer.ILayer;
import cn.creable.gridgis.shapefile.IEditTool;
import cn.creable.gridgis.shapefile.IShapefileLayer;
import cn.creable.gridgis.shapefile.ShapefileLayer;
import cn.creable.ucmap.MapLoader;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.telephony.TelephonyManager;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

public class MainAct extends Activity {
	
	static  
	{    
		System.loadLibrary( "UCMAP" );
	}
	MapView mapView;
	
	int type=0;
	
	private ArrowTool arrowTool;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        setContentView(R.layout.main);
        App.getInstance();
        mapView=(MapView)findViewById(R.id.mapView);
        
        Button btnm=(Button)findViewById(R.id.menu);
        btnm.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Runtime runtime = Runtime.getRuntime();

				try {
					runtime.exec("input keyevent " + KeyEvent.KEYCODE_MENU);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
        	
        });
        
        Button btn=(Button)findViewById(R.id.button1);
        btn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				//这里调用slideAnimation函数，将当前视图的中心滑动到116.368392,39.92476
				//mapView.getMapControl().getDisplay().getDisplayTransformation().setZoom(0.0000349015f);
				//mapView.getMapControl().refreshSync();
				mapView.getMapControl().slideAnimation(116.368392,39.92476);
			}
        	
        });
    }
    
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0,99,0,"加载google地图");
		menu.add(0,98,0,"加载bj地图");
		//menu.add(0,100,0,"切换Activity");
		menu.add(0, 0, 0, "绘制单箭头");
		menu.add(0,10,0,"绘制双箭头");
		menu.add(0,4,0,"补偿漫游");
		menu.add(0,5,0,"平滑漫游");
		menu.add(0,20,0,"改变标注样式");
		menu.add(0,21,0,"恢复标注样式");
		menu.add(0,22,0,"让某一层透明");
		menu.add(0,23,0,"集成地图工具");
		menu.add(0,28,0,"集成地图工具2");
		menu.add(0,24,0,"画自定义点");
		menu.add(0,25,0,"清空自定义点");
		menu.add(0,26,0,"点选自定义点");
		menu.add(0,27,0,"查询地图坐标工具");
		menu.add(0,28,0,"自定义选择工具");
		menu.add(0, 1, 0, "退出");
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId())
		{
		case 99:
//			ILayer layer=mapView.getMapControl().getMap().getLayer("管点");
//			UniqueValueRenderer ur=(UniqueValueRenderer) ((IFeatureLayer)layer).getRenderer();
//			String[] keys=ur.getKeys();
//			MyRenderer mr=new MyRenderer(ur.getSymbolByFeature(null),(IShapefileLayer) layer);
//			mr.setFieldIndex(ur.getFieldIndex());
//			for (int i=0;i<keys.length;++i)
//				mr.setSymbol(keys[i], ur.getSymbol(keys[i]));
//			((IFeatureLayer)layer).setRenderer(mr);
//			mr.fieldIndex=5;//GXXL字段序号
//			mr.key="天然气";//关键字
//			mapView.getMapControl().refresh();
			
			if (arrowTool!=null)
			{
				mapView.getMapControl().removeCustomDraw(arrowTool);
				arrowTool=null;
			}
			type=1;
			MapLoader.loadMapXML(mapView.getMapControl(), "/sdcard/OpenSourceMap.xml",null,0,0.00001f,false);
			//mapView.getMapControl().getRefreshManager().endLayerId=-1;
			mapView.getMapControl().setPanTool();
			break;
		case 98:
			if (arrowTool!=null)
			{
				mapView.getMapControl().removeCustomDraw(arrowTool);
				arrowTool=null;
			}
			type=0;
			mapView.getMapControl().loadMap("/sdcard/bj2/map.ini", (byte)0);
			mapView.getMapControl().setPanTool();
			break;
//		case 100:
//			Intent intent=new Intent(this,MainAct.class);
//			startActivity(intent);
//			break;
		case 0:
			if (arrowTool==null)
			{
				arrowTool=new ArrowTool(mapView.getMapControl());
				arrowTool.setColor(0xC0FF0000, 0xC00000FF);
				mapView.getMapControl().addCustomDraw(arrowTool);
			}
			arrowTool.setArrowStyle(ARROW_STYLE.SINGLE_HEAD_OPEN_TAIL);
			mapView.getMapControl().setCurrentTool(arrowTool);
			break;
		case 10:
			if (arrowTool==null)
			{
				arrowTool=new ArrowTool(mapView.getMapControl());
				arrowTool.setColor(0xC0FF0000, 0xC00000FF);
				mapView.getMapControl().addCustomDraw(arrowTool);
			}
			arrowTool.setArrowStyle(ARROW_STYLE.MULTI_HEAD);
			mapView.getMapControl().setCurrentTool(arrowTool);
			break;
		case 4:
			mapView.getMapControl().setPanTool(false,0);
			break;
		case 5:
			mapView.getMapControl().setPanTool(true,0);//参数为true说明开启平滑漫游模式
			break;
		case 20:
			mapView.getMapControl().addLabelStyle(15, 20, 0xFFFF0000);
			mapView.getMapControl().refresh();
			break;
		case 21:
			mapView.getMapControl().removeLabelStyle(15);
			mapView.getMapControl().refresh();
			break;
		case 22:
			IFeatureLayer fl=(IFeatureLayer)mapView.getMapControl().getMap().getLayer(5);//获取第6个图层
			ISymbol sym=fl.getRenderer().getSymbolByFeature(null);//获取这个图层采用的符号样式
			if (sym instanceof FillSymbol)//如果他是FillSymbol
			{
				FillSymbol fs=(FillSymbol)sym;//将符号转换为FillSymbol
				fs.setColor(0x00000000);//设置颜色，颜色格式为AARRGGBB
			}
			mapView.getMapControl().refresh();//刷新地图，看到效果
			break;
		case 23:
			//如果想让某些图层不能点中，那就调用这些图层的setSelectable(false);将他们设置为不可选择
			MyPanTool tool=new MyPanTool(mapView.getMapControl(),new IInfoToolListener(){

				@Override
				public void notify(MapControl mapControl, IFeatureLayer flayer,
						IFeature ft, String[] fields, String[] values) {
					mapControl.flashFeatures(flayer, ft.getOid());//闪烁被选中的地图要素
					//显示地图要素的属性
					StringBuilder sb=new StringBuilder();
					for (int i=0;i<fields.length;++i)
					{
						sb.append(fields[i]);
						sb.append(":");
						sb.append(values[i]);
						sb.append("\n");
					}
					sb.deleteCharAt(sb.length()-1);
					//利用Toast显示地图要素的属性
					Toast.makeText(App.getInstance(), sb.toString(), Toast.LENGTH_SHORT).show();
					sb=null;
				}
				
			});
			mapView.getMapControl().setCurrentTool(tool);
			break;
		case 24://画自定义点
			if (type!=0) break;
			//构造四个经纬度点
			Point[] pts=new Point[4];
			pts[0]=new Point(116.33,39.9);//(119.178771,36.743880);//
			pts[1]=new Point(116.38,39.95);
			pts[2]=new Point(116.35,39.89);
			pts[3]=new Point(116.36,39.92);
			String[] text=new String[4];
			text[0]="吉A111111";
			text[1]="吉A222222";
			text[2]="吉A333333";
			text[3]="吉A444444";
			
			MyCustomDraw mcd=new MyCustomDraw(mapView.getMapControl(),pts,text,this);//创建CustomDraw对象，并将需要绘制的点传入
			mapView.getMapControl().setCustomDraw(mcd);//将CustomDraw对象置为当前
			mapView.getMapControl().repaint();//立即重绘地图，显示自定义对象
			break;
		case 25://清空自定义点
			if (type!=0) break;
			mapView.getMapControl().setCustomDraw(null);//清空CustomDraw对象
			mapView.getMapControl().refresh();//重绘地图
			break;
		case 26://点选自定义点
			ICustomDraw draw=mapView.getMapControl().getCustomDraw();
			if (draw!=null && draw instanceof ICustomDrawDataCenter)
			{
				CustomDrawGeometrySelector s=new CustomDrawGeometrySelector(mapView.getMapControl(),(ICustomDrawDataCenter)draw);
				s.setOffset(0, 80);
				mapView.getMapControl().setCurrentTool(s);
			}
			break;
		case 27://查询地图坐标工具
			mapView.getMapControl().setCurrentTool(new PointTool(mapView.getMapControl()));
			break;
		case 28:
//			MyMeasureTool mt=new MyMeasureTool(mapView.getMapControl(),0);
//			mapView.getMapControl().setCurrentTool(mt);
			MySelector ms=new MySelector(mapView.getMapControl());
			mapView.getMapControl().setCurrentTool(ms);
			break;
		case 1:	//退出
			System.exit(0);
			break;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			IMapTool mapTool=mapView.getMapControl().getCurrentTool();
			if (mapTool!=null && mapTool instanceof ArrowTool)
			{
				if (((ArrowTool)mapView.getMapControl().getCurrentTool()).undo()==true)
					return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}
	
	@Override
	protected void onResume() {
		if (mapView!=null && mapView.getMapControl()!=null) mapView.getMapControl().refresh(mapView);
		super.onResume();
	}
}