package com.ag.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.ContactsContract;
import android.provider.ContactsContract.PhoneLookup;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.widget.ImageView;

import com.ag.FragmentContacts;
import com.ag.Messola;
import com.ag.R;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class ContactStore extends AsyncTask<Void, Void, List<Contact>>{
    private static Context sContext = Messola.getContext();
    private static ContentResolver sResolver = sContext.getContentResolver();
    private static List<Contact> sContacts = new ArrayList<>();

    FragmentContacts fragmentContacts;

    private static final String CACHE_FILE_NAME = "ContactsCache.txt";
    private static final String TAG = "ContactStore";

    private ContentResolver cr ;
    private Cursor cur;

    /*@Override
    protected List<Contact> doInBackground(Void... voids) {
        return null;
    }

    @Override
    protected void onPostExecute(List<Contact> contacts) {
        super.onPostExecute(contacts);
        fragmentContacts.setList(getContactList());
    }
*/

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(List<Contact> contacts) {
        super.onPostExecute(contacts);
        fragmentContacts.setList(contacts);
    }

    @Override
    protected List<Contact> doInBackground(Void... voids) {
        return getContactList();
    }

    public ContactStore(FragmentContacts fragmentContacts){
        this.fragmentContacts = fragmentContacts;
    }

    public ContactStore() {
        cr = Messola.getContext().getContentResolver();
        cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                null, null, null, "upper("+ContactsContract.Contacts.DISPLAY_NAME + ") ASC");

    }

    public List<Contact> getContactList() {
        update();
        return sContacts;
    }

    public void update() {
        if ((cur != null ? cur.getCount() : 0) > 0) {
            while (cur != null && cur.moveToNext()) {
                Long id = cur.getLong(cur.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                String thumbNailUri = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.PHOTO_THUMBNAIL_URI));
                String photoUri = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.PHOTO_URI));
                String number = "";

                if (cur.getInt(cur.getColumnIndex( ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
                    sContacts.add(new Contact(id, name, "", loadImage(id)));
                }
            }
        }
        if(cur!=null){
            //cur.close();
        }
    }

    public Bitmap loadImage(long id){
        Bitmap bitmap2 = retrieveContactPhoto(id+"");
        if(bitmap2 != null)
            return   bitmap2;

        Bitmap bitmap = BitmapFactory.decodeResource(sContext.getResources(), R.drawable.userpic);
        return bitmap;
    }

    public Set<String> findNumbersById(Long id){

        Set<String> numbers = new HashSet<>();
        Cursor pCur = cr.query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                null,
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                new String[]{id.toString()}, null);

        while (pCur.moveToNext()) {
            numbers.add(pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)));
        }
        Log.i(TAG, "\n\n" + id + " ===> unique numbers : " + numbers.size() + "\n\n");
        pCur.close();

        return numbers;
    }

    /*@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_PICK_CONTACTS && resultCode == RESULT_OK) {
            Log.d(TAG, "Response: " + data.toString());
            uriContact = data.getData();

            retrieveContactName();
            retrieveContactNumber();
            retrieveContactPhoto();

        }
    }*/

    private Bitmap retrieveContactPhoto(String contactID) {
        Bitmap photo = null;
        try {
            InputStream inputStream = ContactsContract.Contacts.openContactPhotoInputStream(cr,
                    ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, new Long(contactID)));
            if (inputStream != null) {
                photo = BitmapFactory.decodeStream(inputStream);
                /* ImageView imageView = (ImageView) findViewById(R.id.img_contact);
                imageView.setImageBitmap(photo);*/
            }

            if(inputStream != null)
                inputStream.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return photo;

    }

    /*private String retrieveContactNumber(String contactID) {
        String contactNumber = null;
        // getting contacts ID
        Cursor cursorID = cr.query(uriContact,
                new String[]{ContactsContract.Contacts._ID},
                null, null, null);

        if (cursorID.moveToFirst()) {
            contactID = cursorID.getString(cursorID.getColumnIndex(ContactsContract.Contacts._ID));
        }
        cursorID.close();
        Log.d(TAG, "Contact ID: " + contactID);
        // Using the contact ID now we will get contact phone number
        Cursor cursorPhone = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER},

                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ? AND " +
                        ContactsContract.CommonDataKinds.Phone.TYPE + " = " +
                        ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE,

                new String[]{contactID},
                null);

        if (cursorPhone.moveToFirst()) {
            contactNumber = cursorPhone.getString(cursorPhone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
        }
        cursorPhone.close();
        Log.d(TAG, "Contact Phone Number: " + contactNumber);
        return contactNumber;
    }

    private String retrieveContactName() {
        String contactName = null;
        // querying contact data store
        Cursor cursor = cr.query(uriContact, null, null, null, null);
        if (cursor.moveToFirst()) {
            // DISPLAY_NAME = The display name for the contact.
            // HAS_PHONE_NUMBER =   An indicator of whether this contact has at least one phone number.
            contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
        }
        cursor.close();
        Log.d(TAG, "Contact Name: " + contactName);
        return contactName;
    }*/

    public Contact getByNumber(String number) {
        for(Contact c : sContacts) {
            if(c.getNumber().equals(number))
                return c;
        }

        String name = resolveNumber(number);
        Contact c = new Contact(-1L, name, number, loadImage(-1L));
        // TODO: Figure out the recipient ID mess.
        return c;
    }

    public static Contact getByName(String name) {
        for(Contact c : sContacts) {
            if(c.getName().equals(name))
                return c;
        }
        return null;
    }

    public Contact getByRecipientId(long recipientId) {
        for(Contact c : sContacts) {
            if (c.getId().equals(recipientId))
                return c;
        }

        String number = resolveIdToName(recipientId);
        String name = resolveNumber(number);

        Contact c = new Contact(recipientId, name, number, loadImage(recipientId));

        if(name != null)
            c.setId(c.getId());
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
            c.setId(c.getId());
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

        for(Contact c : sContacts) {
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

        /*address = address.replace("-", "").replace("+", "");
        if(address.charAt(0) == '1')
           address = address.substring(1); */

        String address_key = "";
        for(int i = address.length() - 1; i >= 0; i--) {
            address_key += address.charAt(i);
        }

        Uri lookup = Uri.withAppendedPath(PhoneLookup.CONTENT_FILTER_URI, Uri.encode(address));
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
