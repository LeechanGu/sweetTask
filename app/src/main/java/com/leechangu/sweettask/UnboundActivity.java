package com.leechangu.sweettask;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by CharlesGao on 15-11-25.
 */
public class UnboundActivity extends Activity {

    TextView tv_show_partner_username;
    Button bt_unbind;
    String partnerUsername;
    AlertDialog alertDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_unbound);

        init();

        partnerUsername = UserMngRepository.getPartnerUsername();
        tv_show_partner_username.setText("You now bind with "+partnerUsername+" !");

        // Unbind partner
        bt_unbind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(UnboundActivity.this);
                builder.setMessage(getResources().getString(R.string.unbind_partner_alertdialog));
                builder.setPositiveButton(getResources().getString(R.string.confirm), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        UserMngRepository.unbindPartner();
                        Toast.makeText(UnboundActivity.this,
                                "Unbind successfully!", Toast.LENGTH_LONG).show();
                        alertDialog.dismiss();
                        ProgressDialog progressDialog = new ProgressDialog(UnboundActivity.this);
                        progressDialog.setMessage("Sending Cancellation...");
                        progressDialog.show();
                        // TODO : Send notification


                        finish();
                    }
                });
                builder.setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        alertDialog.dismiss();
                    }
                });
                alertDialog = builder.create();
                alertDialog.show();

            }
        });



    }



    public void init(){
        tv_show_partner_username = (TextView)findViewById(R.id.tv_show_partner_name);
        bt_unbind = (Button)findViewById(R.id.bt_unbind_partner);
    }

}
