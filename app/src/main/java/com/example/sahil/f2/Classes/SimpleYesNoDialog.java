package com.example.sahil.f2.Classes;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.example.sahil.f2.MainActivity;
import com.example.sahil.f2.R;

/**
 * Created by hit4man47 on 2/3/2018.
 */

public abstract class SimpleYesNoDialog
{

    abstract public void yesClicked();

    abstract public void noClicked();

    public void showDialog(Context mainActivityObject, String tit, String msg, String btn_yes, String btn_no)
    {
        final Dialog dialog = new Dialog(mainActivityObject);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layoutof_dialog13);
        dialog.setCanceledOnTouchOutside(false);

        Button cancel=(Button) dialog.findViewById(R.id.dialog13_cancel);
        Button ok=(Button) dialog.findViewById(R.id.dialog13_ok);
        TextView message=(TextView) dialog.findViewById(R.id.dialog13_message);
        TextView title=(TextView) dialog.findViewById(R.id.dialog13_title);
        title.setText(tit);
        message.setText(msg);
        ok.setText(btn_yes);
        cancel.setText(btn_no);


        cancel.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                dialog.cancel();
                noClicked();
            }
        });
        ok.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                dialog.cancel();
                yesClicked();
            }
        });

        dialog.show();
    }

}
