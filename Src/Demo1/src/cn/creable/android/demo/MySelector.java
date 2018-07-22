package cn.creable.android.demo;

import android.widget.Toast;
import cn.creable.gridgis.controls.App;
import cn.creable.gridgis.controls.MapControl;
import cn.creable.gridgis.geodatabase.IFeature;
import cn.creable.gridgis.geometry.GeometryType;
import cn.creable.gridgis.geometry.LineString;
import cn.creable.gridgis.geometry.Polygon;
import cn.creable.gridgis.shapefile.Selector;

public class MySelector extends Selector {

	public MySelector(MapControl mapControl) {
		super(mapControl);
		// TODO Auto-generated constructor stub
		this.setMode(2);
	}

	@Override
	public void pointerReleased(int x, int y, int x2, int y2) {
		// TODO Auto-generated method stub
		super.pointerReleased(x, y, x2, y2);
		IFeature feature=this.getSelectedFeature();
		if (feature!=null && feature.getShape().getGeometryType()==GeometryType.Polygon)
		{
			Polygon pg=(Polygon)feature.getShape();
			LineString line=(LineString)pg.getExteriorRing();
			Toast.makeText(App.getInstance(), String.format("面积=%f,周长=%f", pg.getArea(),line.getLength()), Toast.LENGTH_SHORT).show();
			mapControl.flashFeature(this.getSelectedLayer(), this.getSelectedFeature());
		}
	}

}
