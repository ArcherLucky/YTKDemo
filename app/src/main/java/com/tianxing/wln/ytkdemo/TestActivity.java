package com.tianxing.wln.ytkdemo;

import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import com.tianxing.wln.ytkdemo.fragment.SingleChoiceFragment;
import com.tianxing.wln.ytkdemo.view.ObservableScrollView;
import com.tianxing.wln.ytkdemo.view.SlidingFinishLayout;

public class TestActivity extends FragmentActivity implements SingleChoiceFragment.OnFragmentInteractionListener {

    ViewPager viewPager;
	ObservableScrollView titleScrollView;
	SlidingFinishLayout slideLayout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_abslistview);
		
		titleScrollView = (ObservableScrollView) findViewById(R.id.scroll_title);

		slideLayout = (SlidingFinishLayout) findViewById(R.id.sildingFinishLayout);

		slideLayout.setBackgroundColor(Color.GREEN);

		viewPager = (ViewPager) findViewById(R.id.frame_main_viewpage);

        viewPager.setOffscreenPageLimit(20);

		viewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {

            @Override
            public Fragment getItem(int position) {
                return new SingleChoiceFragment();
            }

            @Override
            public int getCount() {
                return 20;
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
//                super.destroyItem(container, position, object);
                FragmentTransaction mTransaction = getSupportFragmentManager().beginTransaction();
                if (object instanceof Fragment) {
                    mTransaction.remove((Fragment) object);
                }
                mTransaction.commit();

            }
        });

		TextView tv = (TextView) findViewById(R.id.xxx);

		// touchView要设置到ListView上面
		slideLayout.setTitleView(titleScrollView);
		slideLayout.setTouchView(tv);
		slideLayout.setContentView(viewPager);

	}

	// Press the back button in mobile phone
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		overridePendingTransition(0, R.anim.base_slide_right_out);
	}

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
