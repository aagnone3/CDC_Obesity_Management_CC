package edu.gatech.johndoe.carecoordinator;

import android.Manifest;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;

import com.firebase.client.Firebase;

import edu.gatech.johndoe.carecoordinator.patient_fragments.PatientAdapter;
import edu.gatech.johndoe.carecoordinator.util.Utility;


public class MainActivity extends AppCompatActivity implements OnFragmentInteractionListener, NavigationView.OnNavigationItemSelectedListener {

    public static boolean isInExpandedMode;

    private Menu mOptionsMenu;
    private CommunityListFragment currentFragment;
    private int currentNavigationItemId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set Context for Firebase
        Firebase.setAndroidContext(this);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CALL_PHONE)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CALL_PHONE}, 0);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (findViewById(R.id.expanded_layout) != null) {
            // expanded layout mode
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_locked);
            drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_OPEN);
            drawer.setScrimColor(0);
            isInExpandedMode = true;
        } else {
            // collapsed (navigation drawer) layout mode
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_unlocked);
            drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                    this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
            drawer.addDrawerListener(toggle);
            toggle.syncState();
            isInExpandedMode = false;
        }

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        if (savedInstanceState == null) {
            onNavigationItemSelected(navigationView.getMenu().getItem(0).setChecked(true));
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            if (currentFragment != null) {
                currentFragment.filterList(query);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        mOptionsMenu = menu;
        MenuItem searchMenuItem = menu.findItem(R.id.search);

        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) searchMenuItem.getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));

//        searchMenuItem.setVisible(mViewPager.getCurrentItem() == 3);
//        menu.findItem(R.id.sort).setVisible(mViewPager.getCurrentItem() == 3);

        MenuItemCompat.setOnActionExpandListener(searchMenuItem, new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                if (currentFragment != null) {
                    currentFragment.filterList(((SearchView) item.getActionView()).getQuery());
                }
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                if (currentFragment != null) {
                    currentFragment.filterList("");
                }
                return true;
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                return true;
            case R.id.menuSortName:
                sortCommunityList(CommunityAdapter.SortType.NAME);
                return true;
            case R.id.menuSortPopularity:
                sortCommunityList(CommunityAdapter.SortType.POPULARITY);
                return true;
            case R.id.menuSortDistance:
                sortCommunityList(CommunityAdapter.SortType.DISTANCE);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSummaryFragmentInteraction(Uri uri) {
        // TODO
    }

    @Override
    public void onReferralFragmentInteraction(Uri uri) {
        // TODO
    }

    @Override
    public void onPatientFragmentInteraction(Uri uri) {
        // TODO
    }

    @Override
    public void onCommunityFragmentInteraction(CommunityListFragment fragment) {
        currentFragment = fragment;
    }

    @Override
    public void onCommunityDetailFragmentInteraction(Uri uri) {
        // TODO
    }

    @Override
    public void onShouldUpdateDetail(Object content) {
        if (content instanceof Community) {
            Fragment detailFragment = CommunityDetailFragment.newInstance((Community) content);
            getSupportFragmentManager().beginTransaction().replace(R.id.detailFragmentContainer, detailFragment, "detail").commit();
        }
    }

    private void sortCommunityList(CommunityAdapter.SortType type) {
        if (currentFragment != null) {
            currentFragment.sortList(type);
        }
    }



    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == currentNavigationItemId) {
            return true;
        }

        ContentListFragment contentListFragment = (ContentListFragment) getSupportFragmentManager().findFragmentById(R.id.contentListFragment);

        if (id == R.id.nav_referrals) {
            contentListFragment.setAdapter(new CommunityAdapter(Utility.getCommunities()), ContentListFragment.ContentType.Referral); // FIXME: replace with the referral adapter/data
        } else if (id == R.id.nav_patients) {
            contentListFragment.setAdapter(new PatientAdapter(Utility.getPatients()), ContentListFragment.ContentType.Patient);
        } else if (id == R.id.nav_communities) {
            contentListFragment.setAdapter(new CommunityAdapter(Utility.getCommunities()), ContentListFragment.ContentType.Community);
        } else if (id == R.id.nav_account) {
            // TODO
        } else if (id == R.id.nav_settings) {
            // TODO
        } else {
            return false;
        }

        if (isInExpandedMode) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.detailFragmentContainer, new UnselectedFragment(), "detail");
            transaction.commit();
        } else {
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_unlocked);
            drawer.closeDrawer(GravityCompat.START);
        }

        currentNavigationItemId = id;

        return true;
    }
}
