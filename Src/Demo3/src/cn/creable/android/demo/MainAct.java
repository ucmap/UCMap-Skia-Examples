package cn.creable.android.demo;

import java.io.IOException;
import java.io.InputStream;

import cn.creable.cosmetic.Cosmetic;
import cn.creable.cosmetic.CosmeticAddTool;
import cn.creable.cosmetic.CosmeticDeleteTool;
import cn.creable.cosmetic.CosmeticEditTool;
import cn.creable.cosmetic.CosmeticItem;
import cn.creable.cosmetic.CosmeticItemRect;
import cn.creable.cosmetic.CosmeticItemSvg;
import cn.creable.cosmetic.CosmeticItemText;
import cn.creable.cosmetic.CosmeticLayer;
import cn.creable.cosmetic.CosmeticTransformTool;
import cn.creable.gridgis.controls.IMapViewListener;
import cn.creable.gridgis.controls.MapControl;
import cn.creable.gridgis.controls.MapView;
import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ZoomControls;

public class MainAct extends Activity {
	MapView mapView;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
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
					String path=Environment.getExternalStorageDirectory().getPath();
					mapControl.loadMap(path+"/bj2/map.ini", (byte)0);
					mapControl.setPanTool();
				}
			}
        	
        });
    }
    
    private Cosmetic createSvgCosmetic(int rawid)
	{
		Cosmetic c=new Cosmetic(0,true,0,null);//新建一个Cosmetic
		InputStream is=getResources().openRawResource(rawid);
		CosmeticItemSvg ci=new CosmeticItemSvg(0,0,is,1,1,0,0xFF000000,0xFFFF0000);//新建一个SVG格式的CosmeticItem
		ci.setPosition(-ci.getSVGImage().getWidth()/2, -ci.getSVGImage().getHeight()/2);//将这个CosmeticItemSvg的定位点设置到SVG文件的中间
		c.addCosmeticItem(ci);//加入Cosmetic
		try {
			is.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return c;
	}
    
    private Cosmetic createSplitSvgCosmetic(int rawid)
    {
    	InputStream is=getResources().openRawResource(rawid);
		CosmeticItem[] items=CosmeticItemSvg.split(is, 0xFF000000,0xFF0000FF);
		try {
			is.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Cosmetic c=new Cosmetic(0,true,0,null);//新建一个Cosmetic
		for (int i=1;i<items.length;++i)
		{
			c.addCosmeticItem(items[i]);
		}
		CosmeticItemRect rect=(CosmeticItemRect)items[0];
		c.move(-rect.getWidth()/2, -rect.getHeight()/2);
		return c;
    }
    
    private Cosmetic createTextCosmetic(String text,int color,int size)
    {
    	Cosmetic c=new Cosmetic(0,true,0,null);//新建一个Cosmetic
    	CosmeticItemText ci=new CosmeticItemText(0,0,text,color,size);
    	ci.setPosition(-(float)ci.getEnvelope().getWidth()/2, (float)ci.getEnvelope().getHeight()/2);
    	c.addCosmeticItem(ci);//加入Cosmetic
    	return c;
    }
    
    @Override
	public boolean onCreateOptionsMenu(Menu menu) {
    	menu.add(0,100,0,"添加装饰1");
		menu.add(0,101,0,"添加装饰2");
		menu.add(0,102,0,"添加装饰3");
		menu.add(0,103,0,"添加装饰4");
		menu.add(0,104,0,"添加装饰5");
		menu.add(0,107,0,"添加文字装饰1");
		menu.add(0,108,0,"添加文字装饰2");
		menu.add(0,105,0,"装饰变形");
		menu.add(0,119,0,"添加可编辑装饰1");
		menu.add(0,117,0,"修改装饰");
		menu.add(0,106,0,"删除装饰");
		menu.add(0,118,0,"清空装饰");
		menu.add(0,110,0,"添加装饰1不缩放");
		menu.add(0,111,0,"添加装饰2不缩放");
		menu.add(0,112,0,"添加装饰3不缩放");
		menu.add(0,113,0,"添加装饰4不缩放");
		menu.add(0,114,0,"添加装饰5不缩放");
		menu.add(0,115,0,"添加文字装饰1不缩放");
		menu.add(0,116,0,"添加文字装饰2不缩放");
		menu.add(0, 5, 0, "退出");
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId())
		{
		case 100://添加装饰1
		{
			CosmeticAddTool tool=new CosmeticAddTool(mapView.getMapControl(),createSvgCosmetic(R.raw.point1),true);
			mapView.getMapControl().setCurrentTool(tool);
			break;
		}
		case 101://添加装饰2
		{
			CosmeticAddTool tool=new CosmeticAddTool(mapView.getMapControl(),createSvgCosmetic(R.raw.point2),true);
			mapView.getMapControl().setCurrentTool(tool);
			break;
		}
		case 102://添加装饰3
		{
			CosmeticAddTool tool=new CosmeticAddTool(mapView.getMapControl(),createSvgCosmetic(R.raw.point3),true);
			mapView.getMapControl().setCurrentTool(tool);
			break;
		}
		case 103://添加装饰4
		{
			CosmeticAddTool tool=new CosmeticAddTool(mapView.getMapControl(),createSvgCosmetic(R.raw.point4),true);
			mapView.getMapControl().setCurrentTool(tool);
			break;
		}
		case 104://添加装饰5
		{
			CosmeticAddTool tool=new CosmeticAddTool(mapView.getMapControl(),createSvgCosmetic(R.raw.point5),true);
			mapView.getMapControl().setCurrentTool(tool);
			break;
		}
		case 105://装饰变形
		{
			mapView.getMapControl().setCurrentTool(new CosmeticTransformTool(mapView.getMapControl()));
			break;
		}
		case 106://删除装饰
		{
			mapView.getMapControl().setCurrentTool(new CosmeticDeleteTool(mapView.getMapControl()));
			break;
		}
		case 107://添加文字装饰1
		{
			CosmeticAddTool tool=new CosmeticAddTool(mapView.getMapControl(),createTextCosmetic("文字1",0xFFFF0000,20),true);
			mapView.getMapControl().setCurrentTool(tool);
			break;
		}
		case 108://添加文字装饰2
		{
			CosmeticAddTool tool=new CosmeticAddTool(mapView.getMapControl(),createTextCosmetic("文字2",0xFF0000FF,15),true);
			mapView.getMapControl().setCurrentTool(tool);
			break;
		}
		case 110://添加装饰1不缩放
		{
			CosmeticLayer layer=CosmeticLayer.getCosmeticLayer();
			CosmeticAddTool tool=new CosmeticAddTool(mapView.getMapControl(),createSvgCosmetic(R.raw.point1),false);
			mapView.getMapControl().setCurrentTool(tool);
			break;
		}
		case 111://添加装饰2不缩放
		{
			CosmeticAddTool tool=new CosmeticAddTool(mapView.getMapControl(),createSvgCosmetic(R.raw.point2),false);
			mapView.getMapControl().setCurrentTool(tool);
			break;
		}
		case 112://添加装饰3不缩放
		{
			CosmeticAddTool tool=new CosmeticAddTool(mapView.getMapControl(),createSvgCosmetic(R.raw.point3),false);
			mapView.getMapControl().setCurrentTool(tool);
			break;
		}
		case 113://添加装饰4不缩放
		{
			CosmeticAddTool tool=new CosmeticAddTool(mapView.getMapControl(),createSvgCosmetic(R.raw.point4),false);
			mapView.getMapControl().setCurrentTool(tool);
			break;
		}
		case 114://添加装饰5不缩放
		{
			CosmeticAddTool tool=new CosmeticAddTool(mapView.getMapControl(),createSvgCosmetic(R.raw.point5),false);
			mapView.getMapControl().setCurrentTool(tool);
			break;
		}
		case 115://添加文字装饰1
		{
			CosmeticAddTool tool=new CosmeticAddTool(mapView.getMapControl(),createTextCosmetic("文字1",0xFFFF0000,20),false);
			mapView.getMapControl().setCurrentTool(tool);
			break;
		}
		case 116://添加文字装饰2
		{
			CosmeticAddTool tool=new CosmeticAddTool(mapView.getMapControl(),createTextCosmetic("文字2",0xFF0000FF,15),false);
			mapView.getMapControl().setCurrentTool(tool);
			break;
		}
		case 117://修改装饰
		{
			//这里注意，svg装饰是无法修改的，只有将svg装饰打散添加进装饰图层，这样才能修改
			CosmeticEditTool tool=new CosmeticEditTool(mapView.getMapControl());
			tool.selector.setOffset(0, 80);
			mapView.getMapControl().setCurrentTool(tool);
			break;
		}
		case 118://清空装饰
		{
			CosmeticLayer cl=CosmeticLayer.getCosmeticLayer();
			cl.clear();
			mapView.getMapControl().refresh();
			break;
		}
		case 119://添加可编辑装饰1
		{
			CosmeticAddTool tool=new CosmeticAddTool(mapView.getMapControl(),createSplitSvgCosmetic(R.raw.point1),true);
			mapView.getMapControl().setCurrentTool(tool);
			break;
		}
		case 5:	//退出
			System.exit(0);
			break;
		}
		return super.onOptionsItemSelected(item);
	}
}