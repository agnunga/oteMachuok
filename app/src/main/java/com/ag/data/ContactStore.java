package com.ag.data;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract.PhoneLookup;
import android.util.Log;

import com.ag.BaseActivity;
import com.ag.Messola;
import com.ag.R;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.HashMap;

//import android.provider.Contacts.People;

public class ContactStore {
    private static Context sContext;
    private static ContentResolver sResolver;
    private static HashMap<Long, Contact> sContacts;

    private static final String CACHE_FILE_NAME = "ContactsCache.txt";
    private static final String TAG = "ContactStore";

    static {
        sContext = Messola.getContext();
        sResolver = sContext.getContentResolver();
        sContacts = new HashMap<Long, Contact>(20);
    }

    public static Contact getByNumber(String number) {
        for(Contact c : sContacts.values()) {
            if(c.getNumber().equals(number))
                return c;
        }

        String name = resolveNumber(number);
        Contact c = new Contact(-1, name, number, R.drawable.userpic);
        // TODO: Figure out the recipient ID mess.
        return c;
    }

    public static Contact getByName(String name) {
        for(Contact c : sContacts.values()) {
            if(c.getName().equals(name))
                return c;
        }
        return null;
    }

    public static Contact getByRecipientId(long recipientId) {
        if(sContacts.containsKey(recipientId))
            return sContacts.get(recipientId);

        String number = resolveIdToName(recipientId);
        String name = resolveNumber(number);

        Contact c = new Contact(recipientId, name, number, R.drawable.userpic);

        if(name != null)
            sContacts.put(c.getId(), c);
        return c;
    }

    public static void importCache() {
        Log.d(TAG, "Importing cache file!");
        FileInputStream fs = null;
        try {
            fs = sContext.openFileInput(CACHE_FILE_NAME);
        }
        catch (FileNotFoundException e) {
            Log.d(TAG, "Contacts Cache file does not exist!");
            return;
        }
        InputStreamReader ifs = new InputStreamReader(fs);
        BufferedReader reader = new BufferedReader(ifs);

        while(true) {
            String s = null;
            try {
                s = reader.readLine();
                if(s == null)
                    return;
            }
            catch (IOException e) {
                Log.d(TAG, "IOException occured while reading the" +
                        " ContactsCache!");
                return;
            }

            Contact c = Contact.parseCached(s);
            sContacts.put(c.getId(), c);
        }
    }

    public static void exportCache() {
        FileOutputStream fs = null;
        try {
            fs = sContext.openFileOutput(CACHE_FILE_NAME, Context.MODE_PRIVATE);
        }
        catch (FileNotFoundException e) {
            Log.d(TAG, "Unable to open the Cache file for writing!");
            return;
        }
        PrintWriter writer = new PrintWriter(fs);

        for(Contact c : sContacts.values()) {
            writer.println(c);
        }
        writer.flush();
        writer.close();
    }

    private static String resolveIdToName(long id) {
        Cursor addrCursor = sResolver.query(Uri.parse("content://mms-sms/canonical-address/" + id),
                null,
                null,
                null,
                null
        );
        addrCursor.moveToFirst();
        String addrCursorStr = addrCursor.getString(0);
        addrCursor.close();
        return addrCursorStr;
    }

    private static String resolveNumber(String address) {
        if(address == null)
            return null;

        address = address.replace("-", "").replace("+", "");
        if(address.charAt(0) == '1')
            address = address.substring(1);

        String address_key = "";
        for(int i = address.length() - 1; i >= 0; i--) {
            address_key += address.charAt(i);
        }

        Uri lookup = Uri.withAppendedPath(PhoneLookup.CONTENT_FILTER_URI,
                Uri.encode(address));
        Cursor c = sResolver.query(lookup,
                new String[] { PhoneLookup.DISPLAY_NAME, PhoneLookup.NUMBER },
                null,
                null,
                null
        );

        if(c == null || c.getCount() == 0) {
            c.close();
            return address;
        }

        c.moveToFirst();
        String name = c.getString(0);
        c.close();

        return name;
    }
}
