package com.example.sahil.f2;

/**
 * Created by sahil on 14-07-2017.
 */

public  class clipboard
{


/*

 try {
                DropboxAPI.Entry entry = DropBoxConnection.mDBApi.metadata("/yoyo (1)", 1000, null, false, null);
                InputStream inputStream =DropBoxConnection.mDBApi.getFileStream("/yoyo (1)",entry.rev);
                Log.e("total size",entry.bytes+"...");
                response= DropBoxConnection.mDBApi.putFile("/123/yo",inputStream, entry.bytes,null,true, new ProgressListener()
                {
                    @Override
                    public long progressInterval()
                    {
                        // Update the progress bar every half-second or so
                        return 1000;
                    }

                    @Override
                    public void onProgress(long bytes, long total) {
                        Log.e("haha", bytes + "...."+total);
                    }
                });





 */



    /*
    public void showDropBoxUpDialog()
    {
        dropbox.progress=0;

        paster.timeinsec=1;


        textwhatToDopd.setText("UPLOADING TO DROPBOX........");
        texttopd.setText("TO:"+dropbox.DROP_BOX_PATH);
        textfrompd.setText("FROM: "+paster.fromParentPath);
        textspeedpd.setText("calculating");

        dialog1.show();

        h=new Handler();
        r=new Runnable()
        {
            @Override
            public void run()
            {
                Log.e("RUNNER",paster.timeinsec+"......................");
                if(tempimageIterator==tempImagesId.length-1)
                {
                    tempimageIterator=-1;
                }
                imagepd.setImageResource(tempImagesId[++tempimageIterator]);

                textnamepd.setText("NAME: "+dropbox.currentName);
                textsizepd.setText("TOTAL SIZE: "+dropbox.totalSizeInWords);
                textnumberpd.setText(dropbox.currentNumber+"/"+dropbox.totalNumber);
                textpercentpd.setText(dropbox.progress+" %");
                pb.setProgress(dropbox.progress);

                textspeedpd.setText(sizeinwords(dropbox.uploadedSize/paster.timeinsec)+"/sec");


                if(dropbox.progress==100||!isMyServiceRunning(DropBoxUpService.class))
                {
                        imagepd.setImageResource(R.mipmap.zeromusic);
                        button2pd.setText("OPEN");
                        textpercentpd.setText("100 %");
                        pb.setProgress(100);
                        Toast.makeText(MainActivity.this, "Uploading Successful", Toast.LENGTH_SHORT).show();

                    h.removeCallbacks(r);
                }
                else
                {
                    paster.timeinsec++;
                    h.postDelayed(r,1000);
                }

            }
        };
        h.postDelayed(r,1000);
    }
    */


    /*
     public void pqr()
    {
        paster.timeinsec=1;

        uploadCache1.totalsizetoupload=0;
        uploadCache1.progress=0;
        uploadCache1.isUploadError=false;
        uploadCache1.uploadedsize=0;
        uploadCache1.uploadErrorCode=0;
        uploadCache1.currentFileName="calculating...";
        uploadCache1.currentFileNumber=0;


        final int totalItemsToUpload=paster.pastelistname.size();

        for(int i=0;i<totalItemsToUpload;i++)
        {
            uploadCache1.totalsizetoupload+= paster.pastesizelistLong.get(i);
        }


        if(uploadCache1.totalsizetoupload>=new File(paster.toParentPath +"/").getUsableSpace())
        {
            final Dialog dialog0 = new Dialog(this);
            dialog0.setContentView(R.layout.layoutof_low_space);
            dialog0.setTitle("Low space Dialog");
            TextView lowspace_clear=(TextView) dialog0.findViewById(R.id.lowspace_clear);
            TextView lowspace_available=(TextView) dialog0.findViewById(R.id.lowspace_available);
            TextView lowspace_required=(TextView) dialog0.findViewById(R.id.lowspace_required);
            Button lowspace_button=(Button)dialog0.findViewById(R.id.lowspace_button);

            lowspace_button.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    dialog0.cancel();
                }
            });

            long spaceAvailable=new File(paster.toParentPath +"/").getUsableSpace();

            lowspace_clear.setText("TRY CLEARING "+ sizeinwords(downloadCache1.totalsizetodownload-spaceAvailable)+" MORE");
            lowspace_available.setText("AVAILABLE SPACE :"+sizeinwords(spaceAvailable));
            lowspace_required.setText("REQUIRED SPACE :"+sizeinwords(downloadCache1.totalsizetodownload));

            dialog0.show();

            Toast.makeText(MainActivity.this, "DOWNLOADING FAILED,TRY CLEARING SOME SPACE", Toast.LENGTH_LONG).show();

            paster.pastelistname.clear();
            paster.pastelistpath.clear();
            paster.pastesizelistLong.clear();
            paster.pasteIsFolder.clear();

            return;
        }

        final Dialog dialog1 = new Dialog(this);
        dialog1.setContentView(R.layout.layoutof_copy1);
        dialog1.setTitle("copy 1");
        final TextView copy1_name=(TextView) dialog1.findViewById(R.id.copy1_name);
        final TextView copy1_from=(TextView) dialog1.findViewById(R.id.copy1_from);
        final TextView copy1_to=(TextView) dialog1.findViewById(R.id.copy1_to);
        final TextView copy1_size=(TextView) dialog1.findViewById(R.id.copy1_size);
        final TextView copy1_itemsprogress=(TextView) dialog1.findViewById(R.id.copy1_itemsProgress);
        final  TextView copy1_sizeprogress=(TextView) dialog1.findViewById(R.id.copy1_sizeProgress);
        final  TextView copy1_percent=(TextView) dialog1.findViewById(R.id.copy1_percent);
        final TextView copy1_speed=(TextView) dialog1.findViewById(R.id.copy1_speed);

        final ImageView copy1_logo=(ImageView)dialog1.findViewById(R.id.copy1_logo);
        final ImageView copy1_fromlogo=(ImageView)dialog1.findViewById(R.id.copy1_from_logo);
        final ImageView copy1_tologo=(ImageView)dialog1.findViewById(R.id.copy1_to_logo);

        final  ProgressBar copy1_progressBar=(ProgressBar)dialog1.findViewById(R.id.copy1_progressBar);

        final  Button copy1_cancel=(Button)dialog1.findViewById(R.id.copy1_cancel);
        final  Button copy1_hide=(Button)dialog1.findViewById(R.id.copy1_hide);



        //to be declared only once
        String relativePath="";
        if(paster.fromStorageCode==1)
        {
            relativePath=paster.fromParentPath.replaceFirst(Storage_Directories[0],"");
            copy1_fromlogo.setImageResource(R.mipmap.internal);
        }
        if(paster.fromStorageCode==2)
        {
            relativePath=paster.fromParentPath.replaceFirst(Storage_Directories[1],"");
            copy1_fromlogo.setImageResource(R.mipmap.sdcard);
        }

        copy1_from.setText(relativePath);

        relativePath="";
        if(paster.toStorageCode==1)
        {
            relativePath=paster.toParentPath.replaceFirst(Storage_Directories[0],"");
            copy1_tologo.setImageResource(R.mipmap.internal);
        }
        if(paster.toStorageCode==2)
        {
            relativePath=paster.toParentPath.replaceFirst(Storage_Directories[1],"");
            copy1_tologo.setImageResource(R.mipmap.sdcard);
        }
        copy1_to.setText(relativePath);
        copy1_size.setText(sizeinwords(downloadCache1.totalsizetodownload));



        copy1_name.setText(downloadCache1.currentFileName);
        copy1_itemsprogress.setText(downloadCache1.currentFileNumber+"/"+totalItemsToDelete +" items");
        copy1_sizeprogress.setText(sizeinwords(downloadCache1.downloadedsize)+"/"+sizeinwords(downloadCache1.totalsizetodownload));
        copy1_percent.setText(downloadCache1.progress+" %");
        copy1_speed.setText("calculating..");

        copy1_progressBar.setProgress(0);

        dialog1.show();
        copy1_counter=0;
        h=new Handler();
        copy1_oldcopiedsize=0;
        r=new Runnable()
        {
            @Override
            public void run()
            {
                Log.e("RUNNER",paster.timeinsec+"......................");
                ++copy1_counter;
                if(copy1_counter%2==0)
                {
                    copy1_logo.setVisibility(View.INVISIBLE);
                }
                else
                {
                    copy1_logo.setVisibility(View.VISIBLE);
                }

                copy1_name.setText(downloadCache1.currentFileName);
                copy1_itemsprogress.setText(downloadCache1.currentFileNumber+"/"+totalItemsToDelete +" items");
                copy1_sizeprogress.setText(sizeinwords(downloadCache1.downloadedsize)+"/"+sizeinwords(downloadCache1.totalsizetodownload));
                copy1_percent.setText(downloadCache1.progress+" %");
                copy1_progressBar.setProgress((int)downloadCache1.progress);
                copy1_speed.setText(sizeinwords(downloadCache1.downloadedsize-copy1_oldcopiedsize)+"/sec");

                copy1_oldcopiedsize=downloadCache1.downloadedsize;

                paster.timeinsec++;

                if((int)downloadCache1.progress==100||!isMyServiceRunning(DownloadService1.class)||downloadCache1.isDownloadError)
                {
                    if(downloadCache1.isDownloadError)
                    {
                        Toast.makeText(MainActivity.this, "ERROR....", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        copy1_logo.setVisibility(View.VISIBLE);
                        copy1_speed.setText("Copying Done.....");
                        copy1_speed.setTextColor(Color.parseColor("#4caf50"));
                        Toast.makeText(MainActivity.this, "Copying Successful", Toast.LENGTH_SHORT).show();
                        //  refreshfiles();
                        //GETTING ATTENTION FOR LAST NEW FILE COPIED
                        //listView.setSelection(nameList.indexOf(CopyService1.currentFileName));
                    }


                    h.removeCallbacks(r);
                }
                else
                {
                    h.postDelayed(r,1000);
                }

            }
        };
        h.postDelayed(r,1000);

        Intent ix= new Intent(MainActivity.this, DownloadService1.class);

        startService(ix);
        Log.e("1111","service called");




    }

     */




    //@Override
   /* public void onResume()
    {
        super.onResume();
      //Log.e(rootFolder.getAbsolutePath(),"onResume()..................");


        thisActivityId=++paster.maxActivityId;


        fakeHandler=new Handler();
        fakeRunnable=new Runnable()
        {
            @Override
            public void run()
            {
                Log.e("FAKE RUNNER",rootFolder.getName()+"**"+thisActivityId+"**"+paster.timeinsec+"+++++++++++++++");

                if(paster.alreadyExist==1 && thisActivityId==paster.maxActivityId)
                {
                    paster.alreadyExistWhatNext=-5;//just to stop the timer
                    paster.alreadyExist=2;
                    textfilepathod.setText("DESTINATION FILEPATH: --");
                    dialog2.setCancelable(false);
                    dialog2.show();
                }



                    if(!isMyServiceRunning(CopyService1.class) || thisActivityId!=paster.maxActivityId)
                    {
                        Log.e("FAKE RUNNER stopped",rootFolder.getName()+"**"+thisActivityId+"**"+"+++++++++_++++++++");
                        fakeHandler.removeCallbacks(fakeRunnable);
                    }
                    else
                    {
                        fakeHandler.postDelayed(fakeRunnable,1000);
                    }
            }
        };

        if(isMyServiceRunning(CopyService1.class))
        {
            Log.e("Fake RUNNER started",rootFolder.getName()+"+++++++++++++++++++++++");
            fakeHandler.postDelayed(fakeRunnable,1000);
        }

    }

*/





    /*

     File file = new File("/sdcard/111111111111111111111.pdf");

                FileOutputStream outputStream = new FileOutputStream(file);
                DropboxAPI.DropboxFileInfo info = mDBApi.getFile("/mydicccdc", null, outputStream,new ProgressListener() {

                @Override
                    public long progressInterval()
                    {
                        // Update the progress bar every half-second or so
                        return 1000;
                    }

                    @Override
                    public void onProgress(long bytes, long total) {
                        Log.e("haha", bytes + "...."+total);
                    }

     */






    /*


     */



    /*
    if(paster.toParentPath.contains(Storage_Directories[0]))
                        {
                            //REFRESH FRAGMENT 1

                            frag1=(pager1)getSupportFragmentManager().findFragmentById(R.id.root_frameLayout1);
                            frag1.refresher();

                            String lastnameCopied=pastelistname.get(pastelistname.size()-1);
                            String[] smallPaths = lastnameCopied.split("\\/");//may be the recursive file
                            int x=frag1.nameList.indexOf(smallPaths[0]);

                            frag1.listView.setSelection(x);

                        }
                        if(paster.toParentPath.contains(Storage_Directories[1]))
                        {
                            //REFRESH FRAGMENT 2

                            frag2=(pager2)getSupportFragmentManager().findFragmentById(R.id.root_frameLayout2);
                            frag2.refresher();

                            String lastnameCopied=pastelistname.get(pastelistname.size()-1);
                            String[] smallPaths = lastnameCopied.split("\\/");//may be the recursive file
                            int x=frag2.nameList.indexOf(smallPaths[0]);

                            frag2.listView.setSelection(x);
                        }



     */


    /*

     for(String item:deletePaths)
                            {

                            }

                            shouldDelete_Yes=true;
                            shouldDelete_No=true;


     */

    /*

     public void Yes()
        {

            Log.e("button INSIDE","......");

            dialog3.hide();

            total_size2.setText("Total Size: "+helpingBot.sizeinwords(deletesize));
            no_of_items2.setText("Total Item: "+(items));

            dialog4.show();


            Handler handler=new Handler();
            Runnable runnable=null;
            runnable=new Runnable()
            {
                @Override
                public void run()
                {

                    for (String item:deletePaths)
                    {
                        File fdelete = new File(item);

                        if (fdelete.exists())   //TRYING TO DELETE
                        {
                            if(item.contains(Storage_Directories[0]))    //DELETE RECURSIVELY
                            {
                                if(deleteRecursive(fdelete))
                                {

                                }
                                else
                                {
                                    errorno=1;
                                    deleteError=true;

                                }
                            }
                            else
                                if(item.contains(Storage_Directories[1]))   //DELETE DOCUMENT FILE
                                {
                                    Log.e("deleting in sd card","....");
                                    if(deleteDocumentFile(fdelete))
                                    {
                                        Log.e("deleting in sd card","done...");
                                    }
                                    else
                                    {
                                        errorno=2;
                                    deleteError=true;
                                    }
                                }
                                else
                                {
                                    errorno=3;
                                    deleteError=true;
                                }
                        }
                        else
                        {
                            errorno=4;
                            deleteError=true;
                        }
                    }

                    if(deleteError)
                        Toast.makeText(context, "Deletion Failed,ERROR: "+errorno,Toast.LENGTH_LONG).show();
                    else
                        Toast.makeText(context, "Deleted Succesfully", Toast.LENGTH_SHORT).show();

                    dialog4.hide();

                    if(deletePaths.get(0).contains(Storage_Directories[0]))
                    {
                        frag1=(pager1)getSupportFragmentManager().findFragmentById(R.id.root_frameLayout1);
                        int x=frag1.listView.getFirstVisiblePosition();
                        frag1.refresher();
                        frag1.listView.setSelection(x);

                    }
                    if(deletePaths.get(0).contains(Storage_Directories[1]))
                    {
                        frag2=(pager2)getSupportFragmentManager().findFragmentById(R.id.root_frameLayout2);
                        int x=frag2.listView.getFirstVisiblePosition();
                        frag2.refresher();
                        frag2.listView.setSelection(x);
                    }
                   // refreshfiles();

                }
            };
            handler.postDelayed(runnable,1000);

        }

        public void No ()
        {
            dialog3.hide();

        }


     */






    /*

     rootFolder=getRootFolder();
                    paster.toParentPath=rootFolder.getAbsolutePath();
                    int tempo = paster.pastelistname.size();
                    for (int j = 0; j < tempo; j++)
                    {

                        File to = new File(paster.toParentPath + "/" + paster.pastelistname.get(j));
                        File tempRootFolder=new File(paster.toParentPath);
                        File from = new File(paster.pastelistpath.get(j));
                        boolean x = from.renameTo(to);
                        Log.e("cutttt???",x+"");
                        files=tempRootFolder.listFiles();
                        nameList.add(paster.pastelistname.get(j));
                        checkedlist.add(false);
                        if (!x)
                        {
                            //error in  moving
                            Toast.makeText(MainActivity.this, "Can't Move Selected,CHECK FILE PERMISSIONS", Toast.LENGTH_LONG).show();
                           // refreshfiles();
                            paster.pastelistname.clear();
                            paster.pastelistpath.clear();
                            paster.what=100;//no more pasting
                            return;
                        }
                    }
                   // refreshfiles();

                    //for getting the attention of last moved file at the top of list view
                    //listView.setSelection(nameList.indexOf(paster.pastelistname.get(tempo-1)));
                    Toast.makeText(MainActivity.this, "Move Successful", Toast.LENGTH_SHORT).show();
                    paster.pastelistname.clear();
                    paster.pastelistpath.clear();
                    paster.pastesizelist.clear();
                    paster.what=100;//no more pasting
                    return;


     */





    /*

    case R.id.menu_cut:

                       // Log.w("ass","cut");
                        paster.what=1;
                        for (String itemo : selectedNameList)
                        {
                            paster.pastelistpath.add(rootFolder.getAbsolutePath()+"/"+itemo);
                            paster.pastelistname.add(itemo);
                            paster.pastesizelist.add(sizeList.get(nameList.indexOf(itemo)));
                            paster.pastesizelistLong.add(sizeListLong.get(nameList.indexOf(itemo)));
                            checkedlist.set(checkedlist.indexOf(true),false);

                        }

                        mode.finish();
                        return true;


                    case R.id.menu_copy:
                        paster.what=2;
                        for (String itemo : selectedNameList)
                        {
                            File x=new File(rootFolder.getAbsolutePath()+"/"+itemo);
                            if(x.isDirectory())
                            {
                                tempParentPath=x.getAbsolutePath();
                                tempParentName=itemo;

                                Recursive(x);
                                //getting data from reversed lists

                                for(int i=paster.reverselistname.size()-1;i>=0;i--)
                                {
                                    paster.pastelistname.add(paster.reverselistname.get(i));
                                    paster.pastelistpath.add(paster.reverselistpath.get(i));
                                    paster.pastesizelist.add(paster.reversesizelist.get(i));
                                    paster.pastesizelistLong.add(paster.reversesizelistLong.get(i));
                                }
                                paster.reverselistname.clear();
                                paster.reverselistpath.clear();
                                paster.reversesizelist.clear();
                                paster.reversesizelistLong.clear();


                                checkedlist.set(checkedlist.indexOf(true), false);

                            }
                            else
                             {
                                paster.pastelistpath.add(rootFolder.getAbsolutePath() + "/" + itemo);
                                paster.pastelistname.add(itemo);
                                paster.pastesizelist.add(sizeList.get(nameList.indexOf(itemo)));
                                paster.pastesizelistLong.add(sizeListLong.get(nameList.indexOf(itemo)));
                                checkedlist.set(checkedlist.indexOf(true), false);
                            }

                        }

                        mode.finish();
                        return true;

     */





































/*
if(paster.what==2)
                {






                    intent=new Intent(this,mycopyservice.class);
                    tempo = paster.pastelistname.size();

                    joker=0;

                        Log.w("joker loop started",joker+"");

                        dialog1 = new Dialog(this);
                        dialog1.setContentView(R.layout.custom_dialog2);
                        dialog1.setTitle("Custom Dialog");










                        dialog1.show();


                        tempimageIterator=0;
                    starter=true;

                        handler=new Handler();

                        runnable=new Runnable()
                        {
                            @Override
                            public void run()
                            {

                                tempFileForSize=new File(paster.pastelistpath.get(joker));
                                tempFileSize=tempFileForSize.length();
                                tempFileSize=tempFileSize/1024;
                                tempFileSize=tempFileSize/1024;


                                //checking for space errors
                                File tempSpace=new File(rootFolder.getAbsolutePath() + "/");


                                //checking for space error only one time for file
                                if(((tempSpace.getUsableSpace()/1048576)<tempFileSize)&& (starter))
                                {
                                    //error true

                                    Log.d("SPACE",tempSpace.getUsableSpace()/1048576+"jhsguhcjshjxj");

                                    Toast.makeText(MainActivity.this, "COPYING FAILED,TRY CLEARING SOME SPACE", Toast.LENGTH_LONG).show();

                                    //SHOWING APPROPRIATE DIALOG
                                    textnamepd.setText("NAME: "+paster.pastelistname.get(joker));
                                    textsizepd.setText("SIZE: "+paster.pastesizelist.get(joker));
                                    textfrompd.setText("FROM: "+paster.pastelistpath.get(joker));
                                    texttopd.setText("TO :"+rootFolder.getAbsolutePath() + "/" + paster.pastelistname.get(joker));
                                    pb.setMax(100);
                                    imagepd.setImageResource(R.mipmap.txt);

                                    textpercentpd.setText("ERROR!!! LOW SPACE");
                                    textpercentpd.setTextColor(Color.parseColor("#f44336"));

                                    textspeedpd.setText("TRY CLEARING "+ (tempFileSize-tempSpace.getUsableSpace() /1048576)+" MB MORE");
                                    textspeedpd.setTextColor(Color.parseColor("#f44336"));

                                    button2pd.setClickable(false);

                                    //NO MORE COPYING
                                    paster.pastelistname.clear();
                                    paster.pastelistpath.clear();
                                    paster.pastesizelist.clear();
                                    paster.what=200;
                                    mycopyservice.prog=0;
                                    oldprog=0;


                                    //CLOSING THE HANDLER
                                    handler.removeCallbacks(runnable);


                                }
                                else
                                {
                                    //ENOUGH SPACE IS THERE TO COPY ----PREPARING FOR COPYING

                                    Log.d("SPACE",tempSpace.getUsableSpace()/1048576+"");

                                    download1=paster.pastelistpath.get(joker);
                                    download2=rootFolder.getAbsolutePath() + "/" + paster.pastelistname.get(joker);

                                    intent.putExtra("1",download1);
                                    intent.putExtra("2",download2);

                                    textnamepd.setText("NAME: "+paster.pastelistname.get(joker));
                                    textsizepd.setText("SIZE: "+paster.pastesizelist.get(joker));
                                    textfrompd.setText("FROM: "+paster.pastelistpath.get(joker));
                                    texttopd.setText("TO :"+rootFolder.getAbsolutePath() + "/" + paster.pastelistname.get(joker));


                                    pb.setMax(100);

                                    if(starter)
                                    {
                                        //STARTING THE SERVICE
                                        startService(intent);
                                        starter=false;
                                    }

                                    //CHANGING BACKGROUND ICONS
                                    if(tempimageIterator==tempImagesId.length-1)
                                    {
                                        tempimageIterator=-1;
                                    }
                                    imagepd.setImageResource(tempImagesId[++tempimageIterator]);

                                    //SETTING PROGRESS BAR PROGRESS
                                    pb.setProgress(mycopyservice.prog);
                                    textpercentpd.setText(mycopyservice.prog+" %");
                                    //SETTING SPEED
                                    speed=(mycopyservice.prog-oldprog)*(int)tempFileSize/100;
                                    textspeedpd.setText(speed+" MB/sec");
                                    oldprog=mycopyservice.prog;


                                    if(mycopyservice.prog==100)
                                    {
                                        //STOPPING OR STARTING ANOTHER SERVICE
                                        stopService(intent);
                                        if(joker+1<tempo)
                                        {
                                            //STARING NEXT SERVICE TO COPY THE NEXT FILE

                                            //REFRESHING THE BACKGROUND LIST VIEW
                                            nameList.add(paster.pastelistname.get(joker));
                                            checkedlist.add(false);
                                            refreshfiles();

                                            //PREPARATION FOR STARTING NEXT SERVICE
                                            ++joker;
                                            starter=true;
                                            mycopyservice.prog=0;
                                            oldprog=0;


                                            handler.postDelayed(runnable,800);

                                        }
                                        else
                                        {
                                            //ENDING THE COPYING TASK
                                            mycopyservice.prog=0;
                                            oldprog=0;
                                            //dialog1.cancel();

                                            //COPING SUCCESSFUL
                                            Toast.makeText(MainActivity.this, "Copying Successful", Toast.LENGTH_SHORT).show();

                                            imagepd.setImageResource(R.mipmap.pdf);
                                            button2pd.setText("OPEN");
                                            //REFRESHING THE BACKGROUND LIST VIEW
                                            nameList.add(paster.pastelistname.get(joker));
                                            checkedlist.add(false);
                                            refreshfiles();
                                            //GETTING ATTENTION FOR LAST NEW FILE COPIED
                                            listView.setSelection(nameList.indexOf(paster.pastelistname.get(tempo-1)));


                                            //NO MORE COPYING
                                            paster.pastelistname.clear();
                                            paster.pastelistpath.clear();
                                            paster.pastesizelist.clear();
                                            paster.what=200;
                                            mycopyservice.prog=0;
                                            oldprog=0;
                                            starter=true;

                                            //CLOSING THE HANDLER
                                            handler.removeCallbacks(runnable);

                                        }
                                    }
                                    else
                                    {
                                        //PROGRESS BAR IS NOT YET FILLED ,KEEP FILLING IT
                                        handler.postDelayed(runnable,800);
                                    }


                                }


                            }
                        };

                        handler.postDelayed(runnable,800);


                    }



 */







/*
actionBar=getSupportActionBar();
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM|ActionBar.DISPLAY_SHOW_HOME|ActionBar.DISPLAY_HOME_AS_UP);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setCustomView(R.layout.custom_action_bar_layout);
        actionbarView=actionBar.getCustomView();
         Toolbar toolbar=(Toolbar)actionBar.getCustomView().getParent();
        toolbar.setContentInsetsAbsolute(0, 0);
        toolbar.getContentInsetEnd();
        toolbar.setPadding(0, 0, 0, 0);

 */

/*

actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setIcon(R.drawable.my_icon_delete);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setTitle("allah hu");

        // Hide the action bar title
        actionBar.setDisplayShowTitleEnabled(false);

        // Enabling Spinner dropdown navigation
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);

        // Spinner title navigation data
        navSpinner = new ArrayList<SpinnerNavItem>();
        navSpinner.add(new SpinnerNavItem("Local", R.drawable.my_icon_delete));
        navSpinner.add(new SpinnerNavItem("My Places", R.drawable.my_icon_delete));
        navSpinner.add(new SpinnerNavItem("Checkins", R.drawable.my_icon_delete));
        navSpinner.add(new SpinnerNavItem("Latitude", R.drawable.my_icon_delete));

        // title drop down adapter
        adapter = new TitleNavigationAdapter(getApplicationContext(), navSpinner);
        actionBar.setListNavigationCallbacks(adapter,this);
 */




/*
 spinner demo version
        ArrayList<String> contactlist= new ArrayList<String>();
        contactlist.add("Gabe");
        contactlist.add("Mark");
        contactlist.add("Bill");
        contactlist.add("Steve");
        ArrayAdapter adapter2 = new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, contactlist);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
 */































                            /*
                        new DownloadTask(MainActivity.this,download1,download2).execute("dhjdhz");

                        */

                        /*




                                try
                                {
                                        InputStream in = new FileInputStream(download1);
                                        OutputStream out = new FileOutputStream(download2);
                                        File from = new File(download1);
                                        long fileLength = from.length();
                                        fileLength = fileLength / 1024;
                                        fileLength = fileLength / 1024;

                                        byte[] buf = new byte[1024];


                                        int len;
                                        long total = 0;
                                        long temp = 0;
                                        long old=0;
                                        while ((len = in.read(buf)) > 0)
                                        {
                                            total += len;
                                            temp = total / 1024;
                                            temp = temp / 1024;


                                           mBuilder.setProgress(100,(int) temp * 100 / (int) fileLength,false);


                                            if(temp-old>10)
                                            {
                                                mNotifyManager.notify(9891,mBuilder.build());
                                                old=temp;
                                            }

                                            //publishProgress((int) temp * 100 / (int) fileLength);
                                            out.write(buf, 0, len);
                                        }
                                    Log.i("COPY", "Copy file successful.");
                                    Toast.makeText(MainActivity.this, "File downloaded", Toast.LENGTH_SHORT).show();

                                        in.close();
                                        out.close();
                                    mBuilder.setSmallIcon(R.drawable.ic_action_location_found)
                                            .setContentTitle("Copying...")
                                            .setContentText("Complete");
                                    mNotifyManager.notify(2,mBuilder.build());

                                }
                                catch (Exception e1)
                                {
                                    e1.printStackTrace();
                                }


                        try
                        {
                            InputStream in = new FileInputStream(paster.pastelistpath.get(joker));
                            OutputStream out = new FileOutputStream(rootFolder.getAbsolutePath() + "/" + paster.pastelistname.get(joker));

                            // Copy the bits from instream to outstream
                            byte[] buf = new byte[1024*5];
                            int len;
                            while ((len = in.read(buf)) > 0) {
                                out.write(buf, 0, len);
                            }

                            in.close();
                            out.close();



                        }
                        catch (Exception E)
                        {
                            Log.i("COPY", "Copy file failed!!!!!!!!!.");
                        }
                */




}
