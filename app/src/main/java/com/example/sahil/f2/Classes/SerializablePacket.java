package com.example.sahil.f2.Classes;

import java.io.Serializable;
import java.util.ArrayList;

public class SerializablePacket implements Serializable
{
    public String senderIP,senderDeviceName,receiverIP,receiverDeviceName;
    public long totalSizeToDownload=0;
    public ArrayList<String> pathList=new ArrayList<>();
    public ArrayList<String> nameList=new ArrayList<>();
    public ArrayList<Long> sizeLongList=new ArrayList<>();
    public ArrayList<Boolean> isFolderList=new ArrayList<>();
}
