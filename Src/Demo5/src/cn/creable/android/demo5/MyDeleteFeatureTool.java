package cn.creable.android.demo5;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.os.Handler;
import android.os.Message;
import cn.creable.gridgis.controls.IMapTool;
import cn.creable.gridgis.controls.MapControl;
import cn.creable.gridgis.geodatabase.IFeature;
import cn.creable.gridgis.shapefile.Selector;
import cn.creable.gridgis.shapefile.ShapefileLayer;

/**
 * 这是一个实现删除地图要素的地图工具例子，有别于sdk自带的DeleteFeatureTool
 * 本地图工具在选中地图要素，1.5秒之后，会自动弹出确认对话框，不需要长按，也不需要点击按钮
 *
 */
public class MyDeleteFeatureTool implements IMapTool {
	
	private MapControl mapControl;
	public Selector selector;//selector是一个用于实现选择的地图工具，这里集成他来帮助我们实现对于地图要素的选择
	
	private IFeature ft;
	private ShapefileLayer layer;
	private Activity act;

	public MyDeleteFeatureTool(MapControl mapControl,Activity act) {
		this.mapControl=mapControl;
		this.act=act;
		selector=new Selector(mapControl);
		selector.reset();//重置选择器
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
		//一旦选择器的pointerReleased执行完毕之后，我们就可以使用下面2行代码来检查用户到底有没有选中地图要素
		if (ft!=null) return;//这个判断防止还没弹出对话框时，用户又点击地图的情况
		this.ft=selector.getSelectedFeature();
		this.layer=(ShapefileLayer)selector.getSelectedLayer();//这里直接将图层应用强制转换为ShapefileLayer，是因为选择器默认情况下只能选择可编辑图层(ShapefileLayer)
		if (this.ft!=null && this.layer!=null)
		{
			mapControl.flashFeature(layer, ft);//删除被选中的地图要素
			//下面利用一个线程来实现地图要素闪烁1.5秒之后，立即弹出是否删除要素的对话框
			new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					Thread.sleep(1500);//这里让线程睡眠1.5秒
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				Message msg=new Message();
				msg.what=1;
				mhandler.sendMessage(msg);//这里发送消息给handler
			}
			}).start();
		}
	}
	
	private Handler mhandler=new Handler()
	{
		public void handleMessage(Message msg) 
		{    
            switch (msg.what) 
            {    
            case 1:
            	action();
            	break;
            }
		}
	};

	@Override
	public void action() {
		if (ft!=null)
		{
			mapControl.flashFeature(null, null);//flashFeature传入2个NULL，就会停止闪烁
			//下面显示一个确认对话框
			AlertDialog.Builder builder = new AlertDialog.Builder(act);
	    	builder.setTitle("确认删除吗？").setIcon(R.drawable.icon)
	    	.setPositiveButton("确定", new DialogInterface.OnClickListener(){

				@Override
				public void onClick(DialogInterface dialog, int which) {
					//用户点了确认，则调用图层的deleteFeature删除要素
					if (layer.deleteFeature(ft))
						mapControl.refresh();
					ft=null;
				}
	    		
	    	})
	    	.setNegativeButton("取消", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					//用户取消删除
					ft=null;
				}
			});
	    	builder.create().show();
			
		}
	}

	@Override
	public boolean keyPressed(int keyCode) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void draw(Canvas g) {
		selector.draw(g);//这里调用选择器的draw函数，以显示放大镜效果
	}

}
