package com.example.sahil.f2.Cache;

import com.example.sahil.f2.Classes.SerializablePacket;
import com.example.sahil.f2.Classes.WiFiSendData;

import java.net.Socket;

/**
 * Created by hit4man47 on 9/10/2017.
 */

public class superCache
{
    public static boolean pager0OpeningFirstTime=true;
    public static void clear()
    {
        pager0OpeningFirstTime=true;
    }
    public static WiFiSendData wiFiSendData1 =null;
    public static WiFiSendData wiFiSendData2 =null;
    public static WiFiSendData wiFiSendData3=null;
    public static WiFiSendData wiFiSendData4=null;
    public static Socket receiverSocket1=null;
    public static Socket receiverSocket2=null;
}


