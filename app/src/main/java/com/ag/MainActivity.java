package com.ag;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.ag.utilis.TelephonyInfo;

public class MainActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener {
    TextView chats;
    NavigationView navigationView, navigationViewBottom;
    DrawerLayout drawer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (ActivityCompat.checkSelfPermission(Messola.getContext(), Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        setContentView(R.layout.activity_main);

        System.out.println("DUAL SIM MANENOS!!!");

        TelephonyInfo.dualSimManenos();

        setupToolbar(R.id.toolbar, "Messages");

        FragmentTransaction ft;

        Intent i = getIntent();
        if(i.hasExtra("frgToLoad")) {
            System.out.println("HAS FRAGMENT TO LOAD.....");
            int intentFragment = i.getExtras().getInt("frgToLoad");
            switch (intentFragment) {
                case 1: {
                    FragmentHome fragmentHome = new FragmentHome();
                    ft = getSupportFragmentManager().beginTransaction();
                    ft.replace(R.id.frameLayout, fragmentHome).commit();
                    break;
                }
            }
        }

        FragmentHome fragmentHome = new FragmentHome();
        ft = getSupportFragmentManager().beginTransaction();
        ft.add(R.id.frameLayout, fragmentHome).commit();

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        final ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        navigationViewBottom = (NavigationView) findViewById(R.id.nav_view_bottom);
        navigationViewBottom.setNavigationItemSelectedListener(this);


        chats =(TextView) MenuItemCompat.getActionView(navigationView.getMenu().
                findItem(R.id.nav_chats));
        initializeCountDrawer();

        try {

            // Start service
            Intent svc = new Intent(this, ResponderService.class);
            startService(svc);
        }
        catch (Exception e) {
            Log.e("onCreate", "service creation problem", e);
        }


    }

    private void initializeCountDrawer(){
        chats.setGravity(Gravity.CENTER);
        chats.setTypeface(null, Typeface.BOLD);
        chats.setTextColor(getResources().getColor(R.color.colorAccent));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            chats.setTextAppearance(R.style.LightNav);
            chats.setTextColor(getResources().getColor(R.color.colorAccent));
        }
        chats.setText("99+");
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
            finish();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
/*
        Log.i("OLOO", "\n\n\n\n===Selected "+item.getItemId()+
                "=== home = " + android.R.id.home +
                "=== menu_edit " + R.menu.menu_edit +
                "=== R.id.new_chat " + R.id.new_chat +
                "=== R.menu.menu_add " + R.menu.menu_add +
                "\n\n\n\n\n");
*/

        switch (item.getItemId()) {
            case android.R.id.home: {
                Log.i(TAG, "\n\n\n\n===home===\n\n\n\n\n");
                drawer.openDrawer(GravityCompat.START);  // OPEN DRAWER
                return true;
            }case R.id.new_chat : {
                Log.i(TAG, "\n\n\n\n===Create new messo===\n\n\n\n\n");
//                Intent i = new Intent(Messola.getContext(), ComposeMessageActivity.class);
                Intent i = new Intent(Messola.getContext(), ConversationActivity.class);
                startActivity(i);
            }/*case R.menu.menu_edit : {
                Log.i(TAG, "\n\n\n\n===Create new messo===\n\n\n\n\n");
                Intent i = new Intent(Messola.getContext(), ComposeMessageActivity.class);
                startActivity(i);
            }case R.menu.menu_add : {
                Log.i(TAG, "\n\n\n\n===Create new messo===\n\n\n\n\n");
                Intent i = new Intent(Messola.getContext(), ComposeMessageActivity.class);
                startActivity(i);
            }*/
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        FragmentTransaction ft;
        int id = item.getItemId();

        if (id == R.id.nav_contacts) {
            FragmentContacts fragmentContacts = new FragmentContacts();
            ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.frameLayout, fragmentContacts).addToBackStack(null).commit();
        } else if (id == R.id.nav_chats) {
            FragmentHome fragmentHome = new FragmentHome();
            ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.frameLayout, fragmentHome).commit();
        } else if (id == R.id.nav_trash) {
        } else if (id == R.id.nav_settings) {
        } else if (id == R.id.nav_logout) {
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }

}
