package cn.creable.android.demo;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class MyActivity extends Activity {
	
	private String[] fields,values;
	
	private int[] textViews={R.id.textView1,R.id.textView2,R.id.textView3,R.id.textView4,R.id.textView5,
			R.id.textView6,R.id.textView7,R.id.textView8,R.id.textView9,R.id.textView10};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		fields=this.getIntent().getExtras().getStringArray("fields");
		values=this.getIntent().getExtras().getStringArray("values");
		this.setContentView(R.layout.act_my);
		
		for (int i=0;i<fields.length;++i)
		{
			TextView tv1=(TextView)this.findViewById(textViews[i]);
			tv1.setText(fields[i]+":"+values[i]);
			if (i==9) break;
		}
		
	}

}
