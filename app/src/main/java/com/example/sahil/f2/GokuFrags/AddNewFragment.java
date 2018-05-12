package com.example.sahil.f2.GokuFrags;


import android.os.Bundle;

import android.support.v4.app.Fragment;

import android.view.LayoutInflater;

import android.view.View;
import android.view.ViewGroup;
import com.example.sahil.f2.R;


/**
 * Created by hit4man47 on 9/5/2017.
 */

public class AddNewFragment extends Fragment
{

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup contaimer, Bundle saved)
    {

        return inflater.inflate(R.layout.layoutof_addnewtab,contaimer,false);
    }


    @Override
    public void onStart()
    {
        super.onStart();

        //new folder is opened


    }

}
