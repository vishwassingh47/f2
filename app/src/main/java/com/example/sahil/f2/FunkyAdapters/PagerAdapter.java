package com.example.sahil.f2.FunkyAdapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.sahil.f2.GokuFrags.rootFrame0;
import com.example.sahil.f2.GokuFrags.rootFrame1;
import com.example.sahil.f2.GokuFrags.rootFrame2;
import com.example.sahil.f2.GokuFrags.rootFrame3;
import com.example.sahil.f2.GokuFrags.rootFrame4;
import com.example.sahil.f2.GokuFrags.rootFrame5;

/**
 * Created by Acer on 06-08-2017.
 */


public class PagerAdapter extends FragmentPagerAdapter
{

    private int totalViews=0;

    public PagerAdapter(FragmentManager fm)
    {
        super(fm);
    }


    @Override
    public Fragment getItem(int index)
    {
        if(index== 0)
        {
            return new rootFrame0();
        }
        if(index== 1)
        {
            return new rootFrame1();
        }
        if(index== 2)
        {
            return new rootFrame2();
        }
        if(index== 3)
        {
            return new rootFrame3();
        }
        if(index== 4)
        {
            return new rootFrame4();
        }
        if(index== 5)
        {
            return new rootFrame5();
        }

        return null;
    }

    @Override
    public int getCount()
    {
        return totalViews;
    }

    @Override
    public  CharSequence getPageTitle(int pos)
    {
        return null;
    }


    public void setCount(int x)
    {
        totalViews = x;
    }


}
