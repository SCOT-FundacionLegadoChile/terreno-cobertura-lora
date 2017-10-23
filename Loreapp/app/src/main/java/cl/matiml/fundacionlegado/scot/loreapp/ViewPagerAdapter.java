package cl.matiml.fundacionlegado.scot.loreapp;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class ViewPagerAdapter extends FragmentPagerAdapter
{
//  FragmentPagerAdapter
//    This is best when navigating between sibling screens representing a fixed, small number of pages.
//  FragmentStatePagerAdapter
//    This is best for paging across a collection of objects for which the number of pages is undetermined.
//    It destroys fragments as the user navigates to other pages, minimizing memory usage.

    private static int NUM_ITEMS = 3;
    //private MisAlertasInterfaces.MisAlertasTabListener misAlertasTabListener;

    public ViewPagerAdapter(FragmentManager fm) {//MisAlertasInterfaces.MisAlertasTabListener alertasTabListener, FragmentManager fm) {
        super(fm);
        //misAlertasTabListener = alertasTabListener;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                MonitorFragment monitorFragment = new MonitorFragment();
                return monitorFragment;
            case 1:
                PruebasFragment pruebasFragment = new PruebasFragment();
                return pruebasFragment;
            case 2:
                ConfigFragment configFragment = new ConfigFragment();
                return configFragment;
            default:
                return null;
        }
    }

    //@Overridee
    //public CharSequence getPageTitle(int position)
    //{
    //    return "Page" + position;
    //}

    @Override
    public int getCount() {
        return NUM_ITEMS;
    }
}
