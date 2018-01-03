package in.hoptec.smartconnect.adapters;

/**
 * Created by shivesh on 29/12/17.
 */
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

/**
 * Created by shivesh on 29/12/17.
 */

public class HelpPagerAdapter extends FragmentPagerAdapter {
    private List<Fragment> fragments;
    public HelpPagerAdapter(FragmentManager fm, List<Fragment> fragments) {
        super(fm);
        this.fragments = fragments;
    }
    @Override
    public Fragment getItem(int position) {
        return this.fragments.get(position);
    }
    @Override
    public int getCount() {
        return this.fragments.size();
    }
}