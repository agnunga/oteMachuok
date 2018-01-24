package com.ag;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.annotation.DrawableRes;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.ag.data.Conversation;
import com.ag.data.ConversationStore;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by Ag.
 */

public class BaseActivity extends AppCompatActivity {
    Toolbar toolbar;
    TextView title;
    public static String TAG = "Chata";
    private static Context instance;
    private ProgressDialog mProgressDialog;
    private static final int PERMISSION_REQUEST_CODE = 1;

    public static ConversationStore getConversationStore() {
        // TODO: Implement it this way.
        return null;
    }

    public final void changeTitle(int toolbarId, String titlePage){
        toolbar = (Toolbar) findViewById(toolbarId);
        setSupportActionBar(toolbar);

        title = (TextView) toolbar.findViewById(R.id.tv_title);
        title.setText(titlePage);
        getSupportActionBar().setTitle("");
    }
    public final void setupToolbar(int toolbarId, String titlePage){
        toolbar = (Toolbar) findViewById(toolbarId);
        setSupportActionBar(toolbar);

        title = (TextView) toolbar.findViewById(R.id.tv_title);
        title.setText(titlePage);

        getSupportActionBar().setTitle("");
    }
    public void setupToolbarWithUpNav(int toolbarId, String titlePage, @DrawableRes int res){
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

    public void sendMessage(Conversation mConv, String message) {
        ContentValues values = new ContentValues(7);
        values.put("address", mConv.getContact().getNumber());
        values.put("read", false);
        values.put("subject", "");
        values.put("body", message);
        values.put("thread_id", mConv.getThreadId());
        values.put("type", 2);

        Uri uri = Uri.parse("content://sms/outbox");
        getContentResolver().insert(uri, values);

        Toast.makeText(Messola.getContext(), "Sending message: " + message, Toast.LENGTH_SHORT).show();
        // TODO: Handle undelivered messages, etc.
        try {
            SmsManager.getDefault().sendTextMessage(mConv.getContact().getNumber(),
                    null,
                    message.toString(),
                    null,
                    null);
            Log.d(TAG,"Message Sent");
            showToast("Message Sent");
        }catch (Exception e){
            Log.d(TAG, "Message Sending Failed, Error :  " + e.getMessage());
            showToast("Sending SMS Failed");
        }
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


    public void makeCall(String s, boolean isUssd){
        if(isUssd)
            s = s + Uri.encode("#");
        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + s));
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED){
            requestForCallPermission();
        } else {
            startActivity(intent);
        }
    }

    public void requestForCallPermission(){
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.CALL_PHONE)){
        }
        else{
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.CALL_PHONE},PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults)
    {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    makeCall("100", false);
                }
                break;
        }
    }

    public ProgressDialog showProgressDialog(Context context, String message){
        final ProgressDialog progressDialog = new ProgressDialog(context,
                R.style.AppTheme);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage(message);
        progressDialog.show();
        Window window = progressDialog.getWindow();
        window.setLayout(ActionBar.LayoutParams.WRAP_CONTENT, ActionBar.LayoutParams.WRAP_CONTENT);
        return  progressDialog;
    }

    public void dismissProgressDialog(ProgressDialog progressDialog){
        progressDialog.dismiss();
    }

    public void startComposing(Context context, Conversation c) {
        Intent i = new Intent(context, ConversationActivity.class);
        if(c != null) {
            Log.d("SimpleSMS", c.toString());
            i.putExtra("thread_id", c.getThreadId());
            i.putExtra("name", c.getContact().getName());
            i.putExtra("number", c.getContact().getNumber());
            i.putExtra("contact_id", c.getContact().getId());
        }
        startActivity(i);
    }
}
