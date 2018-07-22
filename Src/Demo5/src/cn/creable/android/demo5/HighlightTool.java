package cn.creable.android.demo5;

import java.util.Vector;

import android.graphics.Canvas;
import cn.creable.gridgis.controls.ICustomDraw2;
import cn.creable.gridgis.controls.IMapTool;
import cn.creable.gridgis.controls.MapControl;
import cn.creable.gridgis.display.Display;
import cn.creable.gridgis.display.LineSymbol;
import cn.creable.gridgis.geodatabase.IFeature;
import cn.creable.gridgis.geometry.GeometryType;
import cn.creable.gridgis.geometry.IGeometry;
import cn.creable.gridgis.geometry.LineString;
import cn.creable.gridgis.geometry.MultiPolygon;
import cn.creable.gridgis.geometry.Polygon;
import cn.creable.gridgis.shapefile.Selector;

public class HighlightTool implements IMapTool, ICustomDraw2 {
	
	private MapControl mapControl;
	public Selector selector;
	private Vector<IGeometry> geos;
	private LineSymbol ls;
	private Display display;
	
	public HighlightTool(MapControl mapControl)
	{
		this.mapControl=mapControl;
		this.display=(Display) mapControl.getDisplay();
		selector=new Selector(mapControl);
		selector.setMode(2);//让选择器可以选中不可编辑图层和可编辑图层
		geos=new Vector<IGeometry>();
		ls=new LineSymbol(4,0x80DA251D);
	}

	@Override
	public void drawOnMapCache(Canvas g) {
		IMapTool tool=mapControl.getCurrentTool();
		if (tool!=this)
		{
			drawHighlight(g);
		}
	}

	@Override
	public void drawOnScreen(Canvas g) {
		IMapTool tool=mapControl.getCurrentTool();
		if (tool==this)
		{
			drawHighlight(g);
		}
	}

	@Override
	public void action() {
		// TODO Auto-generated method stub

	}

	@Override
	public void draw(Canvas g) {
		selector.draw(g);
	}

	@Override
	public boolean keyPressed(int arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void pointerDragged(int x, int y, int x2, int y2) {
		selector.pointerDragged(x, y, x2, y2);//直接将触屏事件发送给选择器
	}

	@Override
	public void pointerPressed(int x, int y, int x2, int y2) {
		selector.pointerPressed(x, y, x2, y2);//直接将触屏事件发送给选择器
	}

	@Override
	public void pointerReleased(int x, int y, int x2, int y2) {
		selector.pointerReleased(x, y, x2, y2);//直接将触屏事件发送给选择器
		IFeature ft=selector.getSelectedFeature();
		if (ft.getShape().getGeometryType()!=GeometryType.Polygon && ft.getShape().getGeometryType()!=GeometryType.MultiPolygon) return;//如果被选中的要素的几何对象不是Polygon也不是MultiPolygon，则不处理
		geos.addElement(ft.getShape());
		mapControl.repaint();
	}
	
	public void drawHighlight(Canvas g)
	{
		int size=geos.size();
		Canvas oldG=display.getCanvas();
		display.setCanvas(g);//将display使用的画布设置为g
		for (int i=0;i<size;++i)
		{
			IGeometry geo=geos.get(i);
			switch (geo.getGeometryType())
			{
			case GeometryType.Polygon:
				display.DrawPolyline((LineString)((Polygon)geo).getExteriorRing(),ls);//在画布上绘制多边形的边线
				break;
			case GeometryType.MultiPolygon://绘制多面
				MultiPolygon mp=(MultiPolygon)geo;
				for (int j=0;j<mp.getNumGeometries();++j)
				{//循环绘制多面中的所有面
					Polygon pg=(Polygon)mp.getGeometry(j);
					display.DrawPolyline((LineString)pg.getExteriorRing(), ls);//在画布上绘制多边形的边线
				}
				break;
			}
		}
		display.setCanvas(oldG);
	}
	
	public void addGeometry(IGeometry geo)
	{
		geos.addElement(geo);
	}
	
	public void clear()
	{
		geos.clear();
	}
}
