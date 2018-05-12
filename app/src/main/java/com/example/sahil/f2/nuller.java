package com.example.sahil.f2;

/**
 * Created by hit4man47 on 8/20/2017.
 */

public class nuller
{


    

















    /**
     * Mount filesystem associated with path for writable access (rw)
     * Since we don't have the root of filesystem to remount, we need to parse output of
     * # mount command.
     * @param path the path on which action to perform
     * @return String the root of mount point that was ro, and mounted to rw; null otherwise

    private String mountFileSystemRW(String path)
    {
        Log.e("Mounting","--");
        String command = "mount";
        ArrayList<String> output = runShellCommand(command);
        String mountPoint = "", types = null;
        for (String line : output)
        {
            String[] words = line.split(" ");
            Log.e("words:",words[1]+"--");
            if (path.contains(words[1]))
            {
                // current found point is bigger than last one, hence not a conflicting one
                // we're finding the best match, this omits for eg. / and /sys when we're actually
                // looking for /system
                if (words[1].length() > mountPoint.length()) {
                    mountPoint = words[1];
                    types = words[3];
                }
            }
        }


        if (!mountPoint.equals("") && types!=null)
        {
            Log.e("--","--1");
            // we have the mountpoint, check for mount options if already rw
            if (types.contains("rw"))
            {
                Log.e("--","--2");
                // already a rw filesystem return
                return null;
            } else if (types.contains("ro"))
            {
                Log.e("--","--3");
                // read-only file system, remount as rw
                String mountCommand = "mount -o rw,remount " + mountPoint;
                ArrayList<String> mountOutput =runShellCommand(mountCommand);

                if (mountOutput.size()!=0) {
                    // command failed, and we got a reason echo'ed
                    Log.e("--","--4");
                    return null;
                } else return mountPoint;
            }
        }
        return null;
    }


    private  void mountFileSystemRO(String path)
    {
        Log.e("UnMounting AMAZE","---");
        String command = "umount -r \"" + path + "\"";
        runShellCommand(command);
    }


    private void ummount(String mountPoint)
    {
        Log.e("UnMounting special","---");
        String command="mount -o ro,remount " + mountPoint;
        runShellCommand(command);

    }
    */
}
