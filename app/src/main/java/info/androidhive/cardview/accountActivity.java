package info.androidhive.cardview;

import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.List;

import info.androidhive.cardview.helper.SessionManager;

public class accountActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    protected ViewPager viewPager;
    protected TabLayout tabLayout;
    private SessionManager session;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FirebaseMessaging.getInstance().subscribeToTopic("pushNotifications");
        session = new SessionManager(getApplicationContext());

        /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View hView =  navigationView.getHeaderView(0);

        de.hdodenhof.circleimageview.CircleImageView imageView = hView.findViewById(R.id.imageView);
        TextView eText = hView.findViewById(R.id.EtextView);
        TextView dnTxt = hView.findViewById(R.id.dnTxt);

        if(FirebaseAuth.getInstance().getCurrentUser() != null){
            eText.setText(FirebaseAuth.getInstance().getCurrentUser().getEmail());
            dnTxt.setText(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
            try {
                Glide.with(accountActivity.this).load(FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl()).into(imageView);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        tabLayout = findViewById(R.id.tab2);
        viewPager = findViewById(R.id.viewpager1);
        setupViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new walletFragment(), "Wallet");
        adapter.addFragment(new walHisFragment(), "History");
        viewPager.setAdapter(adapter);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.account, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(accountActivity.this,SettingsActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.Home) {
            // Handle the camera action
            Intent intent = new Intent(accountActivity.this, MainActivity.class);
            //intent.putExtra("Address",getAddress(location.getLatitude(),location.getLongitude()));
            startActivity(intent);

        } else if (id == R.id.Order) {
            // Handle the camera action
            Intent intent = new Intent(accountActivity.this, TransActivity.class);
            //intent.putExtra("Address",getAddress(location.getLatitude(),location.getLongitude()));
            startActivity(intent);

        }
        else if (id == R.id.Detail) {
            // Handle the camera action
            Intent intent = new Intent(accountActivity.this, ProfileActivity.class);
            //intent.putExtra("Address",getAddress(location.getLatitude(),location.getLongitude()));
            startActivity(intent);
        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {
            logout.setLogoutCode(2);
            DialogFragment exit = new exit();
            exit.show(getFragmentManager(),"Exit");
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }



    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    public void schedule() {
        DialogFragment newFragment = new schedule();
        newFragment.show(getFragmentManager(),"Schedule Delivery");
    }



    public void fund() {
        /*DialogFragment newFragment = new BlankFragment();
        newFragment.show(getFragmentManager(),"FUND WALLET");*/
        pstackFragment fragmentA = new pstackFragment();
        if(fragmentA != null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.Alayout, fragmentA, "PAYSTACK").commit();

        }
    }
    public void rfra() {
        /*DialogFragment newFragment = new BlankFragment();
        newFragment.show(getFragmentManager(),"FUND WALLET");*/
        pstackFragment fragmentA = (pstackFragment)getSupportFragmentManager().findFragmentByTag("PAYSTACK");
        if(fragmentA != null && fragmentA.isVisible()) {
            getSupportFragmentManager().beginTransaction()
                    .remove(fragmentA).commit();
        }
    }

    public void restart(String msg){

        /*Toast.makeText(this,msg,Toast.LENGTH_LONG).show();
        int id = viewPager.getCurrentItem();

        Fragment w = (walletFragment)getSupportFragmentManager().findFragmentByTag("android:switcher:"+R.id.viewpager1+":"+id);
        w.getWallet(FirebaseAuth.getInstance().getCurrentUser().getEmail());*/
    }

    public void logout(){
        AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    public void onComplete(@NonNull Task<Void> task) {
                        // user is now signed out
                        session.setLogin(false);
                        startActivity(new Intent(accountActivity.this, LoginActivity.class));
                        Toast.makeText(accountActivity.this,"logout",Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public class action implements View.OnClickListener{
        @Override
        public void onClick(View v) {

            // Code to undo the user's last action

        }
    }

}
