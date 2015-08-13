package com.tianxing.wln.ytkdemo;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.tianxing.wln.ytkdemo.view.BrushView;

/**
 * 草稿纸界面
 */
public class PaintActivity extends Activity implements OnClickListener, BrushView.OnPaintListener {

	private Button lastBtn;
	private Button nextBtn;
	BrushView bView;
    Activity context;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		setContentView(R.layout.paint_layout);

        context = this;

		lastBtn = (Button) findViewById(R.id.last_btn);
		lastBtn.setVisibility(View.GONE);
		nextBtn = (Button) findViewById(R.id.next_btn);
		nextBtn.setVisibility(View.GONE);
		lastBtn.setOnClickListener(this);
		nextBtn.setOnClickListener(this);
        nextBtn.setClickable(false);
        bView = (BrushView) findViewById(R.id.brush_view);
        Button clearBtn = (Button) findViewById(R.id.clear_btn);
		clearBtn.setVisibility(View.GONE);
		clearBtn.setOnClickListener(this);

	}

	@Override
	protected void onPause() {
		super.onPause();
		finish();
	}

    @Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.last_btn:
			nextBtn.setClickable(true);
			if (!bView.lastPath()) {
				v.setClickable(false);
                lastBtn.setClickable(false);
			}

			break;
		case R.id.next_btn:
			lastBtn.setClickable(true);
			if (!bView.nextPath()) {
				v.setClickable(false);
			}
			break;
			case R.id.clear_btn:
                nextBtn.setClickable(false);
                lastBtn.setClickable(false);
				bView.clearPath();
				break;
		}
	}

    @Override
    public void onPaint() {
        lastBtn.setClickable(true);
    }
}
