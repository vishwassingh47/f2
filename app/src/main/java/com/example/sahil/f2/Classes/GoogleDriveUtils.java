package com.example.sahil.f2.Classes;

import com.example.sahil.f2.Classes.DropBox.GoogleDriveConnection;
import com.google.api.services.drive.model.File;

/**
 * Created by hit4man47 on 10/29/2017.
 */

public class GoogleDriveUtils
{
    public static String getNameFromId(final String id)
    {
        Thread thread=new Thread()
        {
            @Override
            public void run()
            {
                try
                {
                    File folder= GoogleDriveConnection.m_service_client.files().get(id).setFields("name").execute();
                }
                catch (Exception e)
                {

                }

            }
        };

        return null;
    }
}
