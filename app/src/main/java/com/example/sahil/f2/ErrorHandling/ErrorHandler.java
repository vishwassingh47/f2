package com.example.sahil.f2.ErrorHandling;

/**
 * Created by hit4man47 on 10/20/2017.
 */

public class ErrorHandler
{
    public static String getErrorName(int errorCode)
    {
        switch (errorCode)
        {
            case 0:
                return "Low Space Error";

            case 1:
                return "ReadAccessDenied Error";//if the file does not exist, is a directory rather than a regular file, or for some other reason cannot be opened for reading.

            case 2:
                return "WriteAccessDenied Error";

            case 3:
                return "SD Card Permission Not Granted";

            case 4:
                return "I/O Error";

            case 5:
                return "NullPointerException";

            case 6:
                return "IndexOutOfBoundsException";

            case 7:
                return "Directory cannot be Created";

            case 8:
                return "MalformedURLException";

            case 9:
                return "TimeOut Error";

            case 10:
                return "UnknownServiceException";// if the protocol does not support input.

            case 11:
                return "IllegalStateException";

            case 12:
                return "DropBox Error";

            case 13:
                return "Google Drive Error";

            case 14:
                return "Failed to ReMount";

            case 15:
                return "Command Failed";

            case 16:
                return "RootDenied Exception";
            case 17:
                return "cannot move parent folder inside its subDirectory";
            case 18:
                return "Please Reconnect to the FTP Server First";
            case 19:
                return "Failed to connect to the FTP Server";
            case 20:
                return "CopyStreamException";
            case 21:
                return "Failed to modify the server";

            case 22:
                return "Failed To Resume";
            default:
                return "unknown error";

        }
    }


}
