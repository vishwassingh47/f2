package com.example.sahil.f2.GokuFrags;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.sahil.f2.Classes.Page;
import com.example.sahil.f2.MainActivity;
import com.example.sahil.f2.R;

/**
 * Created by Acer on 06-08-2017.
 */


public class rootFrame2 extends Fragment
{
    private final int pageIndex=2;
    private final int frameId=R.id.root_frameLayout2;
    private final int frameLayoutId=R.layout.layoutof_rootframe2;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        final Page page=MainActivity.pageList.get(pageIndex);
        final String pageName=page.getName();
        final String currentPagePath=MainActivity.pageList.get(pageIndex).getCurrentPath();

		/* Inflate the layout for this fragment */
        View view = inflater.inflate(frameLayoutId, container, false);
        FragmentTransaction transaction = getFragmentManager().beginTransaction();

		/*
		 * When this container fragment is created, we fill it with our first
		 * "real" fragment
		 */

        Log.e("ROOT2:",pageName+"--"+currentPagePath);

        if (pageName.equals("Home"))
        {
            transaction.replace(frameId, new pager0());
        }
        if(pageName.equals("AddNew"))
        {
            transaction.replace(frameId, new AddNewFragment());
        }
        if(page.getPageId()==-5)
        {
            if(currentPagePath.equals("Gallery1"))
            {
                transaction.replace(frameId, new image_gallery_fragment1());
            }
            if(currentPagePath.equals("Gallery2"))
            {
                transaction.replace(frameId, new image_gallery_fragment2());
            }

        }
        if(page.getPageId()==696969)
        {
            transaction.replace(frameId, new appManager());
        }
        if (page.getPageId()==12345)
        {
            if (currentPagePath.equals("SearchResult"))
            {
                transaction.replace(frameId, new search_fragment());
            }
            else
            {
                //transaction.replace(frameId,new storagePager(),"search1Fragment");
                transaction.replace(frameId, new storagePager());
            }
        }

        if(page.getPageId()==11)
        {
            transaction.replace(frameId,new storageAnalyser());
        }
        if(page.getPageId()==15)
        {
            if (currentPagePath.equals("FtpClient"))
            {
                transaction.replace(frameId, new ftpLoginPager());
            }
            else
            {
                //transaction.replace(frameId,new storagePager(),"search1Fragment");
                transaction.replace(frameId, new storagePager());
            }
        }
        if(page.getPageId()==16)
        {
            transaction.replace(frameId, new ftpServerPager());
        }

        transaction.commit();

        return view;

    }
}