package com.example.sahil.f2.Utilities;

import com.example.sahil.f2.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by hit4man47 on 1/10/2018.
 */

public class ExtensionUtil
{
    private List<String> imageExtensions= Arrays.asList(".jpg",".png",".jpeg",".bmp",".gif",".svg");//IMMUTABLE--CANNOT BE CHANGED
    private List<String> videoExtensions= Arrays.asList(".mp4",".mkv",".mwv",".3gp",".3gpp",".avi",".flv",".m4v",".mov",".mpg",".vob");
    private List<String> audioExtensions= Arrays.asList(".mp3",".wav",".m4a",".wma",".raw",".mid",".m3u",".mpa");

    private int lastIndex;

    private boolean isImage(String extension)
    {
        return imageExtensions.contains(extension);
    }

    private boolean isVideo(String extension)
    {
        return videoExtensions.contains(extension);
    }

    private boolean isAudio(String extension)
    {
        return audioExtensions.contains(extension);
    }


    public int getExtensionId(String name)
    {
        String extension;
        lastIndex=name.lastIndexOf('.');
        if(lastIndex<0)
        {
            return 0;
        }
        else
        {
            extension=name.substring(lastIndex).toLowerCase();
        }

        if(extension.equals(".apk"))
        {
            return 4;
        }

        if(isImage(extension))
        {
            return 1;
        }
        if(isVideo(extension))
        {
            return 2;
        }
        if(isAudio(extension))
        {
            return 3;
        }



        return 0;
    }

    public String getExtension(String name)
    {
        String extension;
        lastIndex=name.lastIndexOf('.');
        if(lastIndex<0)
        {
            return null;
        }
        else
        {
            extension=name.substring(lastIndex).toLowerCase();
            return extension;
        }
    }

    public int getKnownIcons(String name)
    {

        String extension;
        lastIndex=name.lastIndexOf('.');
        if(lastIndex<0)
        {
            return R.mipmap.unknown;
        }
        else
        {
            extension=name.substring(lastIndex).toLowerCase();
        }


        if(imageExtensions.contains(extension))
        {
            return R.mipmap.image;
        }
        if(videoExtensions.contains(extension))
        {
            return R.mipmap.video;
        }
        if(audioExtensions.contains(extension))
        {
            return R.mipmap.music;
        }


        switch(extension)
        {
            case ".doc":
                return R.mipmap.word;
            case ".docx":
                return R.mipmap.word;
            case ".log":
                return R.mipmap.log;
            case ".rtf":
                return R.mipmap.rtf;
            case ".txt":
                return R.mipmap.text;
            case ".csv":
                return R.mipmap.csv;
            case ".vcf":
                return R.mipmap.vcf;
            case ".ppt":
                return R.mipmap.ppt;
            case ".pptx":
                return R.mipmap.ppt;
            case ".tar":
                return R.mipmap.tar;
            case ".xml":
                return R.mipmap.code;
            case ".ogg":
                return R.mipmap.ogg;
            case ".srt":
                return R.mipmap.srt;
            case ".pdf":
                return R.mipmap.pdf;
            case ".xlr":
                return R.mipmap.excel;
            case ".xls":
                return R.mipmap.excel;
            case ".xlsx":
                return R.mipmap.excel;
            case ".db":
                return R.mipmap.db;
            case ".dbf":
                return R.mipmap.db;
            case ".sql":
                return R.mipmap.sql;
            case ".apk":
                return R.mipmap.unknown;
            case ".bat":
                return R.mipmap.bat;
            case ".com":
                return R.mipmap.cmd;
            case ".exe":
                return R.mipmap.exe;
            case ".jar":
                return R.mipmap.java;
            case ".html":
                return R.mipmap.html;
            case ".js":
                return R.mipmap.js;
            case ".htm":
                return R.mipmap.html;
            case ".css":
                return R.mipmap.code;
            case ".jsp":
                return R.mipmap.code;
            case ".php":
                return R.mipmap.code;
            case ".c":
                return R.mipmap.c;
            case ".cpp":
                return R.mipmap.cpp;
            case ".java":
                return R.mipmap.java;
            case ".dll":
                return R.mipmap.dll;
            case ".ini":
                return R.mipmap.c;
            case ".7z":
                return R.mipmap.rar;
            case ".rar":
                return R.mipmap.rar;
            case ".zip":
                return R.mipmap.rar;
            case ".bin":
                return R.mipmap.iso;
            case ".iso":
                return R.mipmap.iso;
            case ".vcd":
                return R.mipmap.iso;
            case ".class":
                return R.mipmap.clas;
            case ".cs":
                return R.mipmap.c_sharp;
            case ".h":
                return R.mipmap.h;
            case ".pl":
                return R.mipmap.code;
            case ".py":
                return R.mipmap.python;
            case ".sh":
                return R.mipmap.code;
            case ".vb":
                return R.mipmap.code;
            case ".tmp":
                return R.mipmap.tmp;
            case ".torrent":
                return R.mipmap.torrent;
            case ".cmd":
                return R.mipmap.cmd;
            case ".lib":
                return R.mipmap.c;
            case ".o":
                return R.mipmap.object;
            case ".obj":
                return R.mipmap.object;
            default:
                return R.mipmap.unknown;
        }

    }


}
