package com.tianxing.wln.ytkdemo;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.tianxing.wln.ytkdemo.view.DraftView;

/**
 * 草稿纸界面
 */
public class DraftActivity extends Activity implements OnClickListener, DraftView.OnPaintListener {

    private Button clearBtn;
    private Button lastBtn;
    private Button nextBtn;
    DraftView bView;
    Activity context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final ActionBar actionBar = getActionBar();
        if (null != actionBar) {
            actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
            actionBar.setCustomView(R.layout.draft_titlebar);
        }
        setContentView(R.layout.activity_draft);

        View customView = actionBar.getCustomView();
        customView.findViewById(R.id.close_btn).setOnClickListener(this);
        context = this;

        lastBtn = (Button) customView.findViewById(R.id.last_btn);
        nextBtn = (Button) findViewById(R.id.next_btn);
        lastBtn.setOnClickListener(this);
        nextBtn.setOnClickListener(this);
        lastBtn.setEnabled(false);
        nextBtn.setEnabled(false);
        bView = (DraftView) findViewById(R.id.brush_view);
        clearBtn = (Button) findViewById(R.id.clear_btn);
        clearBtn.setEnabled(false);
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
            case R.id.close_btn:
                finish();
                break;
            case R.id.last_btn:
                boolean b = bView.lastPath();
                v.setEnabled(b);
                clearBtn.setEnabled(b);
                if (!nextBtn.isEnabled()) {
                    nextBtn.setEnabled(true);
                }
                break;
            case R.id.next_btn:
                v.setEnabled(bView.nextPath());
                if (!lastBtn.isEnabled()) {
                    lastBtn.setEnabled(true);
                }
                if (!clearBtn.isEnabled()) {
                    clearBtn.setEnabled(true);
                }
                break;
            case R.id.clear_btn:
                bView.clearPath();
                if (nextBtn.isEnabled()) {
                    nextBtn.setEnabled(false);
                }
                if (lastBtn.isEnabled()) {
                    lastBtn.setEnabled(false);
                }
                if (clearBtn.isEnabled()) {
                    clearBtn.setEnabled(false);
                }

                break;
        }
    }

    @Override
    public void onPaint() {
        if (!lastBtn.isEnabled()) {
            lastBtn.setEnabled(true);
        }
        if (!clearBtn.isEnabled()) {
            clearBtn.setEnabled(true);
        }
    }
}
