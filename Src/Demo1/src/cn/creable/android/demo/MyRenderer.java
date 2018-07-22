package cn.creable.android.demo;

import cn.creable.gridgis.display.ISymbol;
import cn.creable.gridgis.display.UniqueValueRenderer;
import cn.creable.gridgis.geodatabase.IFeature;
import cn.creable.gridgis.shapefile.IShapefileLayer;

public class MyRenderer extends UniqueValueRenderer {
	
	public int fieldIndex;
	public String key;
	private IShapefileLayer layer;

	public MyRenderer(ISymbol arg0, IShapefileLayer layer) {
		super(arg0, layer);
		this.layer=layer;
	}

	@Override
	public ISymbol getSymbolByFeature(IFeature ft) {
		String[] values=ft.getValues();
		if (values==null)
		{
			layer.loadFeatureAttribute(ft);
			values=ft.getValues();
		}
		if (!ft.getValue(fieldIndex).equalsIgnoreCase(key)) return null;
		return super.getSymbolByFeature(ft);
	}

}
