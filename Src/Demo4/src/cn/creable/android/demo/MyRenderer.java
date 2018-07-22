package cn.creable.android.demo;

import java.util.Hashtable;

import cn.creable.gridgis.display.FeatureRenderer;
import cn.creable.gridgis.display.FillSymbol;
import cn.creable.gridgis.display.ISymbol;
import cn.creable.gridgis.display.LineSymbol;
import cn.creable.gridgis.geodatabase.IFeature;
import cn.creable.gridgis.shapefile.IShapefileLayer;
import cn.creable.gridgis.shapefile.ShapefileLayer;

public class MyRenderer extends FeatureRenderer {
	
	private Hashtable<String,ISymbol> syms;
	private IShapefileLayer layer;
	
	public MyRenderer(IShapefileLayer layer)
	{
		super();
		this.layer=layer;
		syms=new Hashtable<String,ISymbol>();
	}

	@Override
	public ISymbol getSymbolByFeature(IFeature ft) {
		String[] values=ft.getValues();
		if (values==null) 
		{
			layer.loadFeatureAttribute(ft);//如果要素的属性没有加载，那么这里加载它
			values=ft.getValues();
		}
		int val=Integer.parseInt(values[1]);//这里将第二个属性值转换为整型
		//下面根据val的值进行判断采用哪种样式来绘图
		if (110101<=val && val<=110105)
		{
			ISymbol result=syms.get("110101_110105");	//这里首先检查hashtable里是否已经有创建好的样式，如果有则直接返回
			if (result==null) 
			{
				result=new FillSymbol(0xFF00FF00,new LineSymbol(2,0xFF000000));
				syms.put("110101_110105", result);
			}
			return result;
		}
		else if (110106<=val && val<=110110)
		{
			ISymbol result=syms.get("110106_110110");	//这里首先检查hashtable里是否已经有创建好的样式，如果有则直接返回
			if (result==null) 
			{
				result=new FillSymbol(0xFFFFFF00,new LineSymbol(2,0xFF000000));
				syms.put("110106_110110", result);
			}
			return result;
		}
		else if (110111<=val && val<=110115)
		{
			ISymbol result=syms.get("110111_110115");	//这里首先检查hashtable里是否已经有创建好的样式，如果有则直接返回
			if (result==null) 
			{
				result=new FillSymbol(0xFF00FFFF,new LineSymbol(2,0xFF000000));
				syms.put("110111_110115", result);
			}
			return result;
		}
		else if (110116<=val && val<=110230)
		{
			ISymbol result=syms.get("110116_110230");	//这里首先检查hashtable里是否已经有创建好的样式，如果有则直接返回
			if (result==null) 
			{
				result=new FillSymbol(0xFF0000FF,new LineSymbol(2,0xFF000000));
				syms.put("110116_110230", result);
			}
			return result;
		}
		return null;
	}

}
