package cn.creable.android.demo5;

import java.util.Vector;

import cn.creable.gridgis.controls.FingerPaintTool;
import cn.creable.gridgis.controls.MapView;
import cn.creable.gridgis.geodatabase.IFeature;
import cn.creable.gridgis.geometry.Arithmetic;
import cn.creable.gridgis.geometry.Envelope;
import cn.creable.gridgis.geometry.GeometryType;
import cn.creable.gridgis.geometry.IEnvelope;
import cn.creable.gridgis.geometry.IGeometry;
import cn.creable.gridgis.geometry.MultiLineString;
import cn.creable.gridgis.mapLayer.IFeatureLayer;
import cn.creable.gridgis.mapLayer.ILayer;
import cn.creable.gridgis.shapefile.AddFeatureTool;
import cn.creable.gridgis.shapefile.FixedShapefileLayer;
import cn.creable.gridgis.shapefile.IEditListener;
import cn.creable.gridgis.shapefile.IEditTool;
import cn.creable.gridgis.shapefile.ISpatialAnalysisToolListener;
import cn.creable.gridgis.shapefile.ShapefileLayer;
import cn.creable.gridgis.shapefile.SpatialAnalysisTool;
import cn.creable.so.SpatialOperator;
import cn.creable.ucmap.OpenSourceMapLayer;
import cn.creable.ucmap.RasterLayer;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

public class MainAct extends Activity implements IEditListener{
	MapView mapView;
	MainAct act;
	private Vector<ShapefileLayer> layers;
	private IEditTool editTool;
    private IFeature ft;
    private Vector<EditText> ets=new Vector<EditText>();
    private HighlightTool hTool;
    
   
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        Button btn=(Button)findViewById(R.id.menu);
        btn.setOnClickListener(new View.OnClickListener() {

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
        
        act=this;
        layers=new Vector<ShapefileLayer>();
        mapView=(MapView)findViewById(R.id.mapView);
        
        ImageView zoomin=(ImageView)findViewById(R.id.ToolButton1);
        zoomin.setOnTouchListener(new View.OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction()!=MotionEvent.ACTION_UP) return true;
				mapView.getMapControl().setZoomInTool();
				mapView.getMapControl().getCurrentTool().action();
				mapView.getMapControl().setPanTool(false,2);
				return true;
			}
		});
        
        ImageView zoomout=(ImageView)findViewById(R.id.ToolButton2);
        zoomout.setOnTouchListener(new View.OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction()!=MotionEvent.ACTION_UP) return true;
				mapView.getMapControl().setZoomOutTool();
				mapView.getMapControl().getCurrentTool().action();
				mapView.getMapControl().setPanTool(false,2);
				return true;
			}
		});
    }
    
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, 0, 0, "画圆");
		menu.add(0, 1, 0, "画矩形");
		menu.add(0, 10,0, "地图浏览");
		menu.add(0, 2, 0, "手绘");
		menu.add(0, 3, 0, "删除最后一个手绘");
		menu.add(0, 4, 0, "清空手绘");
		menu.add(0, 5, 0, "求交集");
		menu.add(0, 6, 0, "求并集");
		menu.add(0, 7, 0, "求差集");
		menu.add(0, 8, 0, "用面裁切面");
		menu.add(0, 12,0, "用线裁切线");
		menu.add(0, 13,0, "裁切");
		menu.add(0, 9, 0, "删除要素");
		menu.add(0, 14,0, "高亮多边形边线");
		menu.add(0, 11, 0, "显示全图");
		menu.add(0, 100, 0, "退出");
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId())
		{
		case 0://画圆，注意：如果选择了点图层，那是画不了圆的
		{
			AlertDialog.Builder builder = new AlertDialog.Builder(act);
			builder.setTitle("请选择图层:");
			layers.clear();
			Vector<String> strs=new Vector<String>();
			int count=mapView.getMapControl().getMap().getLayerCount();
			for (int i=0;i<count;++i)
			{
				ILayer layer=mapView.getMapControl().getMap().getLayer(i);
				if (layer instanceof ShapefileLayer)
				{
					strs.addElement(layer.getName());
					layers.addElement((ShapefileLayer)layer);
				}
			}
			String[] layerNames=new String[strs.size()];
			strs.copyInto(layerNames);
			strs=null;
			builder.setSingleChoiceItems(layerNames, -1,new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.cancel();	
					AddFeatureTool tool=new AddFeatureTool(mapView.getMapControl(),layers.elementAt(which));
					editTool=tool;
					tool.openSnap();//这里开启捕捉
					tool.setOffset(0, 60);
					tool.setType(1);//这里将type设置为1，表示画圆
					editTool.setListener(act);
					mapView.getMapControl().setCurrentTool(tool);
				}
			});
			AlertDialog dialog=builder.create();
			dialog.show();
			break;
		}
		case 1://画矩形，注意：如果选择了点图层，那是画不了矩形的
		{
			AlertDialog.Builder builder = new AlertDialog.Builder(act);
			builder.setTitle("请选择图层:");
			layers.clear();
			Vector<String> strs=new Vector<String>();
			int count=mapView.getMapControl().getMap().getLayerCount();
			for (int i=0;i<count;++i)
			{
				ILayer layer=mapView.getMapControl().getMap().getLayer(i);
				if (layer instanceof ShapefileLayer)
				{
					strs.addElement(layer.getName());
					layers.addElement((ShapefileLayer)layer);
				}
			}
			String[] layerNames=new String[strs.size()];
			strs.copyInto(layerNames);
			strs=null;
			builder.setSingleChoiceItems(layerNames, -1,new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.cancel();	
					AddFeatureTool tool=new AddFeatureTool(mapView.getMapControl(),layers.elementAt(which));
					editTool=tool;
					tool.openSnap();//这里开启捕捉
					tool.setOffset(0, 60);
					tool.setType(2);//这里将type设置为2，表示画矩形
					editTool.setListener(act);
					mapView.getMapControl().setCurrentTool(tool);
				}
			});
			AlertDialog dialog=builder.create();
			dialog.show();
			break;
		}
		case 10://地图浏览
			mapView.getMapControl().setPanTool();
			break;
		case 2://手绘
			mapView.getMapControl().setCurrentTool(FingerPaintTool.getInstance(mapView.getMapControl()));
			break;
		case 3://删除最后一个手绘
			if (FingerPaintTool.getInstance(mapView.getMapControl()).removeLast())
				mapView.getMapControl().refresh();
			break;
		case 4://清空手绘
			if (FingerPaintTool.getInstance(mapView.getMapControl()).clear())
				mapView.getMapControl().refresh();
			break;
		case 5://求交集
		{
			SpatialAnalysisTool tool=new SpatialAnalysisTool(mapView.getMapControl(),new ISpatialAnalysisToolListener(){

				@Override
				public IGeometry onSubmit(IFeatureLayer layer1,
						IFeature ft1, IFeatureLayer layer2, IFeature ft2) {
					SpatialOperator so=new SpatialOperator();
					IGeometry geo=so.intersection(ft1.getShape(), ft2.getShape());//这里求2个地图要素的交集
					return geo;
				}
				
			});
			tool.selector.setOffset(0, 80);
			mapView.getMapControl().setCurrentTool(tool);
			break;
		}
		case 6://求并集
		{
			SpatialAnalysisTool tool=new SpatialAnalysisTool(mapView.getMapControl(),new ISpatialAnalysisToolListener(){

				@Override
				public IGeometry onSubmit(IFeatureLayer layer1,
						IFeature ft1, IFeatureLayer layer2, IFeature ft2) {
					SpatialOperator so=new SpatialOperator();
					IGeometry geo=so.union(ft1.getShape(), ft2.getShape());//这里求2个地图要素的并集
					return geo;
				}
				
			});
			tool.selector.setOffset(0, 80);
			mapView.getMapControl().setCurrentTool(tool);
			break;
		}
		case 7://求差集
		{
			SpatialAnalysisTool tool=new SpatialAnalysisTool(mapView.getMapControl(),new ISpatialAnalysisToolListener(){

				@Override
				public IGeometry onSubmit(IFeatureLayer layer1,
						IFeature ft1, IFeatureLayer layer2, IFeature ft2) {
					SpatialOperator so=new SpatialOperator();
					IGeometry geo=so.difference(ft1.getShape(), ft2.getShape());//这里求2个地图要素的差集
					return geo;
				}
				
			});
			tool.selector.setOffset(0, 80);
			mapView.getMapControl().setCurrentTool(tool);
			break;
		}
		case 8://用面裁切面
		{
			SpatialAnalysisTool tool=new SpatialAnalysisTool(mapView.getMapControl(),new ISpatialAnalysisToolListener(){

				@Override
				public IGeometry onSubmit(IFeatureLayer layer1,
						IFeature ft1, IFeatureLayer layer2, IFeature ft2) {
					if (ft1.getShape().getGeometryType()!=GeometryType.Polygon ||
						ft2.getShape().getGeometryType()!=GeometryType.Polygon)
						return null;//如果被选中的2个要素，有一个不是Polygon，则不处理
					if (!(layer1 instanceof ShapefileLayer))
						return null;//如果第一个图层不是可编辑图层，则不处理
					SpatialOperator so=new SpatialOperator();
					IGeometry geo=so.intersection(ft1.getShape(), ft2.getShape());//这里求2个地图要素的交集
					if (geo==null) return null;
					IGeometry geo2=so.difference(ft1.getShape(), ft2.getShape());//这里求2个地图要素的差集
					//下面生成空间分析接口生成的2个地图要素，并删除ft1
					String[] values=new String[ft1.getValues().length];
					System.arraycopy(ft1.getValues(), 0, values, 0, ft1.getValues().length);//这里将ft1的属性复制给结果要素
					String[] values2=new String[ft1.getValues().length];
					System.arraycopy(ft1.getValues(), 0, values2, 0, ft1.getValues().length);//这里将ft1的属性复制给结果要素
					ShapefileLayer sLayer=(ShapefileLayer)layer1;
					//ShapefileLayer.beginAddUndo();
					ShapefileLayer.beginEdit();//开始批量编辑，如果需要同时调用好几次编辑函数，调用beginEdit会快很多
					sLayer.addFeature(geo,values);//添加一个要素到图层1
					sLayer.addFeature(geo2, values2);//添加另一个要素的图层2
					sLayer.deleteFeature(ft1);//删除ft1
					ShapefileLayer.endEdit();//结束批量编辑
					//ShapefileLayer.endAddUndo();
					mapView.getMapControl().refresh();
					return null;
				}
				
			});
			tool.selector.setOffset(0, 80);
			mapView.getMapControl().setCurrentTool(tool);
			
			break;
		}
		case 12://用线裁切线
		{
			SpatialAnalysisTool tool=new SpatialAnalysisTool(mapView.getMapControl(),new ISpatialAnalysisToolListener(){

				@Override
				public IGeometry onSubmit(IFeatureLayer layer1,
						IFeature ft1, IFeatureLayer layer2, IFeature ft2) {
					if (ft1.getShape().getGeometryType()!=GeometryType.LineString ||
						ft2.getShape().getGeometryType()!=GeometryType.LineString)
						return null;//如果被选中的2个要素，有一个不是LineString，则不处理
					if (!(layer1 instanceof ShapefileLayer))
						return null;//如果第一个图层不是可编辑图层，则不处理
					SpatialOperator so=new SpatialOperator();
					IGeometry geo=so.difference(ft1.getShape(), ft2.getShape());//这里求2个地图要素的差集
					if (geo==null) return null;
					if (geo.getGeometryType()==GeometryType.MultiLineString)
					{
						//下面将裁切得到的多线，每一个都存入图层1，并删除ft1
						ShapefileLayer sLayer=(ShapefileLayer)layer1;
						MultiLineString mls=(MultiLineString)geo;
						int count=mls.getNumGeometries();
						//ShapefileLayer.beginAddUndo();
						ShapefileLayer.beginEdit();//开始批量编辑，如果需要同时调用好几次编辑函数，调用beginEdit会快很多
						for (int i=0;i<count;++i)
						{
							String[] values=new String[ft1.getValues().length];
							System.arraycopy(ft1.getValues(), 0, values, 0, ft1.getValues().length);//这里将ft1的属性复制给结果要素
							sLayer.addFeature(mls.getGeometry(i), values);//将多线的每一个成员都以单独的地图要素身份添加进图层1
						}
						sLayer.deleteFeature(ft1);//删除ft1
						ShapefileLayer.endEdit();//结束批量编辑
						//ShapefileLayer.endAddUndo();
						mapView.getMapControl().refresh();
					}
					
					return null;
				}
				
			});
			tool.selector.setOffset(0, 80);
			mapView.getMapControl().setCurrentTool(tool);
			break;
		}
		case 13://裁切
		{
			SpatialAnalysisTool tool = new SpatialAnalysisTool(mapView.getMapControl(), new ISpatialAnalysisToolListener() {
				@Override
				public IGeometry onSubmit(IFeatureLayer layer1,
						IFeature ft1, IFeatureLayer layer2, IFeature ft2) {
					if (!(layer1 instanceof ShapefileLayer))
						return null;//如果第一个图层不是可编辑图层，则不处理
					IGeometry[] geo=Arithmetic.cut(ft1.getShape(), ft2.getShape());
					if (geo!=null)
					{
						ShapefileLayer sLayer=(ShapefileLayer)layer1;
						int count=geo.length;
						//ShapefileLayer.beginAddUndo();
						ShapefileLayer.beginEdit();//开始批量编辑，如果需要同时调用好几次编辑函数，调用beginEdit会快很多
						for (int i=0;i<count;++i)
						{
							String[] values=new String[ft1.getValues().length];
							System.arraycopy(ft1.getValues(), 0, values, 0, ft1.getValues().length);//这里将ft1的属性复制给结果要素
							sLayer.addFeature(geo[i], values);//将geo的每一个成员都以单独的地图要素身份添加进图层1
						}
						sLayer.deleteFeature(ft1);//删除ft1
						ShapefileLayer.endEdit();//结束批量编辑
						//ShapefileLayer.endAddUndo();
						mapView.getMapControl().refresh();
					}
					return null;
				}
			});
			tool.selector.setOffset(0, 80);
			mapView.getMapControl().setCurrentTool(tool);
			break;
		}
		case 9://删除要素
		{
			MyDeleteFeatureTool tool=new MyDeleteFeatureTool(mapView.getMapControl(),act);
			tool.selector.setOffset(0, 80);
			mapView.getMapControl().setCurrentTool(tool);
			break;
		}
		case 11://显示全图
		{
			int count=mapView.getMapControl().getMap().getLayerCount();
			if (count>0)
			{
				IEnvelope env=mapView.getMapControl().getFullExtent();
				mapView.getMapControl().adjustEnvelope2(env);
				mapView.getMapControl().refresh(env);
			}
			break;
		}
		case 14://高亮多边形边线
		{
			if (hTool==null)
			{
				hTool=new HighlightTool(mapView.getMapControl());
				hTool.selector.setOffset(0, 80);
				mapView.getMapControl().addCustomDraw(hTool);
			}
			mapView.getMapControl().setCurrentTool(hTool);
			hTool.clear();
			mapView.getMapControl().refresh();
			break;
		}
		case 100://退出
			System.exit(0);break;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {

		}
		return super.onKeyDown(keyCode, event);
	}
	
	private LinearLayout.LayoutParams LP_FF = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
	
	private void showModifyDialog(String[] fields,String[] values)
    {
    	ScrollView sv   = new ScrollView(this);     
    	sv.setLayoutParams( LP_FF );
    	
    	LinearLayout layout = new LinearLayout(this);
    	layout.setOrientation( LinearLayout.VERTICAL );
    	sv.addView( layout ); 
    	
    	TextView tv;
    	EditText et;
    	RelativeLayout.LayoutParams lp=new RelativeLayout.LayoutParams(400,LayoutParams.WRAP_CONTENT);
    	//lp.setMargins(70, 0, 0, 0);
    	
    	int count=fields.length;
    	ets.clear();
    	for (int i=0;i<count;++i)
    	{
	    	tv = new TextView(act);
	    	tv.setText(fields[i]);
	    	tv.setTextAppearance(act, android.R.style.TextAppearance_Medium);
	    	layout.addView( tv );
	    	et=new EditText(act);
	    	et.setLayoutParams(lp);
	    	et.setSingleLine(true);
	    	et.setTextAppearance(act, android.R.style.TextAppearance_Medium);
	    	et.setTextColor(0xFF000000);
	    	if (values!=null) et.setText(values[i]);
	    	layout.addView(et);
	    	ets.addElement(et);
    	}

    	AlertDialog.Builder builder = new AlertDialog.Builder(act);
    	builder.setView(sv).setTitle("修改属性").setIcon(R.drawable.icon)
    	.setPositiveButton("确定", new DialogInterface.OnClickListener(){

			@Override
			public void onClick(DialogInterface dialog, int which) {
				int count=ets.size();
				String[] values=new String[count];
				for (int i=0;i<count;++i)
				{
					values[i]=ets.elementAt(i).getText().toString();
				}
				ft.setValues(values);
				editTool.confirm();
			}
    		
    	})
    	.setNegativeButton("取消", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				editTool.cancel();
			}
		});
    	builder.create().show();
    }

	@Override
	public void onAddFeature(IFeature ft, ILayer layer) {
		this.ft=ft;
		IFeatureLayer flayer=(IFeatureLayer)layer;
		this.showModifyDialog(flayer.getFeatureClass().getFields(), ft.getValues());
	}

	@Override
	public void onDeleteFeature(IFeature ft, ILayer layer) {
		
	}

	@Override
	public void onUpdateFeature(IFeature ft, ILayer layer) {
		
	}
	
	@Override
	protected void onResume() {
		if (mapView!=null && mapView.getMapControl()!=null) mapView.getMapControl().refresh();
		super.onResume();
	}
}