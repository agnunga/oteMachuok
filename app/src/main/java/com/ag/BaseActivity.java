package com.ag;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Parcelable;
import android.support.annotation.DrawableRes;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telecom.PhoneAccountHandle;
import android.telecom.TelecomManager;
import android.util.Log;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.ag.data.Chat;
import com.ag.utilis.TelephonyInfo;

import java.util.List;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by Ag.
 */

public class BaseActivity extends AppCompatActivity {
    Toolbar toolbar;
    TextView title;
    public static String TAG = "BASE_ACT";
    private ProgressDialog mProgressDialog;
    private static final int PERMISSION_REQUEST_CODE = 1;
    private int simselected;

    public final void changeTitle(int toolbarId, String titlePage) {
        toolbar = (Toolbar) findViewById(toolbarId);
        setSupportActionBar(toolbar);

        title = (TextView) toolbar.findViewById(R.id.tv_title);
        title.setText(titlePage);
        getSupportActionBar().setTitle("");
    }

    public final void setupToolbar(int toolbarId, String titlePage) {
        toolbar = (Toolbar) findViewById(toolbarId);
        setSupportActionBar(toolbar);

        title = (TextView) toolbar.findViewById(R.id.tv_title);
        title.setText(titlePage);

        getSupportActionBar().setTitle("");
    }

    public void setupToolbarWithUpNav(int toolbarId, String titlePage, @DrawableRes int res) {
        toolbar = (Toolbar) findViewById(toolbarId);
        setSupportActionBar(toolbar);

        title = (TextView) toolbar.findViewById(R.id.tv_title);
        title.setText(titlePage);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(res);
        getSupportActionBar().setTitle("");
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    protected void showProgress(String msg) {
        if (mProgressDialog != null && mProgressDialog.isShowing())
            dismissProgress();

        mProgressDialog = ProgressDialog.show(this, getResources().getString(R.string.app_name), msg);
    }

    protected void dismissProgress() {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
            mProgressDialog = null;
        }
    }

    protected void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }

    protected void showAlert(String msg) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getResources().getString(R.string.app_name))
                .setMessage(msg)
                .setCancelable(false)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                }).create().show();
    }

    public void makeCall(String s, boolean isUssd) {
        if (isUssd)
            s = s + Uri.encode("#");
        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + s));
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            requestForCallPermission();
        } else {
            startActivity(intent);
        }
    }

    public void requestForCallPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CALL_PHONE)) {

        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    makeCall("100", false);
                }
                break;
        }
    }

    public ProgressDialog showProgressDialog(Context context, String message) {
        final ProgressDialog progressDialog = new ProgressDialog(context, R.style.AppTheme);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage(message);
        progressDialog.show();
        Window window = progressDialog.getWindow();
        window.setLayout(ActionBar.LayoutParams.WRAP_CONTENT, ActionBar.LayoutParams.WRAP_CONTENT);
        return progressDialog;
    }

    public void dismissProgressDialog(ProgressDialog progressDialog) {
        progressDialog.dismiss();
    }

    public void goToConversation(Context context, Chat c) {
        Intent i = new Intent(context, ConversationActivity.class);
        if (c != null) {
            //Log.d("SimpleSMS", c.toString());
            i.putExtra("thread_id", c.getThreadId());
            i.putExtra("name", c.getContact().getName());
            i.putExtra("number", c.getContact().getNumber());
            i.putExtra("contact_id", c.getContact().getId());
        }
        startActivity(i);
    }

    @TargetApi(Build.VERSION_CODES.M)
    public void selectSim(String number) {
        String simSlotName[] = {
                "extra_asus_dial_use_dualsim",
                "com.android.phone.extra.slot",
                "slot",
                "simslot",
                "sim_slot",
                "subscription",
                "Subscription",
                "phone",
                "com.android.phone.DialingMode",
                "simSlot",
                "slot_id",
                "simId",
                "simnum",
                "phone_type",
                "slotId",
                "slotIdx"
        };

        TelecomManager telecomManager = (TelecomManager) this.getSystemService(Context.TELECOM_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        List<PhoneAccountHandle> phoneAccountHandleList = telecomManager.getCallCapablePhoneAccounts();


        Intent intent = new Intent(Intent.ACTION_CALL).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setData(Uri.parse("tel:" + number));
        intent.putExtra("com.android.phone.force.slot", true);
        intent.putExtra("Cdma_Supp", true);
        if (simselected== 0) {   //0 for sim1
            for (String s : simSlotName)
                intent.putExtra(s, 0); //0 or 1 according to sim.......

            if (phoneAccountHandleList != null && phoneAccountHandleList.size() > 0)
                intent.putExtra("android.telecom.extra.PHONE_ACCOUNT_HANDLE", phoneAccountHandleList.get(0));

        } else {      //1 for sim2
            for (String s : simSlotName)
                intent.putExtra(s, 1); //0 or 1 according to sim.......

            if (phoneAccountHandleList != null && phoneAccountHandleList.size() > 1)
                intent.putExtra("android.telecom.extra.PHONE_ACCOUNT_HANDLE", phoneAccountHandleList.get(1));

        }
        startActivity(intent);
    }

}
