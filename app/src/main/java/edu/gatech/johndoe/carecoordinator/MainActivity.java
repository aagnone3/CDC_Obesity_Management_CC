package edu.gatech.johndoe.carecoordinator;

import android.Manifest;
import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.design.widget.NavigationView;
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
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;

import com.firebase.client.Firebase;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

import java.util.Arrays;

import edu.gatech.johndoe.carecoordinator.care_plan.CarePlan;
import edu.gatech.johndoe.carecoordinator.care_plan.UI.CarePlanDetailFragment;
import edu.gatech.johndoe.carecoordinator.care_plan.UI.CarePlanListAdapter;
import edu.gatech.johndoe.carecoordinator.community.Community;
import edu.gatech.johndoe.carecoordinator.community.UI.CommunityAdapter;
import edu.gatech.johndoe.carecoordinator.community.UI.CommunityDetailFragment;
import edu.gatech.johndoe.carecoordinator.patient.Patient;
import edu.gatech.johndoe.carecoordinator.patient.UI.PatientAdapter;
import edu.gatech.johndoe.carecoordinator.patient.UI.PatientDetailFragment;
import edu.gatech.johndoe.carecoordinator.util.Utility;


public class MainActivity extends AppCompatActivity implements OnFragmentInteractionListener, NavigationView.OnNavigationItemSelectedListener {

    public static boolean isInExpandedMode;
    public static GoogleSignInAccount currentUserAccount;
    public static final String TAG = "MainActivity";
    public static final long UPDATE_INTERVAL = 60 * 10 * 1000;  // 10 minutes

    private SearchView searchView;
    private MenuItem searchMenuItem;
    private Menu mOptionsMenu;
    private ContentListFragment currentFragment;
    public static int currentNavigationItemId;
    private Intent intent;
    private NavigationView navigationView;
    private long[] lastUpdateTime = new long[3];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Store intent
        intent = getIntent();
        // Use the intent to populate current user information
        handleIntent(intent);
        // Set content view to main activity layout
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

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View headerView = navigationView.getHeaderView(0);
        if (currentUserAccount.getPhotoUrl() != null) {
            new Utility.ImageDownloadTask((ImageView) headerView.findViewById(R.id.coordinatorPicture)).execute(currentUserAccount.getPhotoUrl().toString());
        }
        ((TextView) headerView.findViewById(R.id.coordinatorName)).setText(currentUserAccount.getDisplayName());
        ((TextView) headerView.findViewById(R.id.coordinatorEmail)).setText(currentUserAccount.getEmail());

        if (savedInstanceState == null) {
            onNavigationItemSelected(navigationView.getMenu().getItem(0).setChecked(true));
            updateTime();
            Utility.getAllCarePlans();
            Utility.getAllPatients();
            Utility.getAllCommunities();
            Utility.getSubCarePlans();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        handleIntent(intent);
    }

    /**
     * Handles the intent that was recently received
     * @param intent Recently received intent
     */
    private void handleIntent(Intent intent) {
        // Update information for the current user
        currentUserAccount = (GoogleSignInAccount) intent.getExtras().get("userAccount");

        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            ContentListFragment currentFragment = (ContentListFragment) getSupportFragmentManager().findFragmentById(R.id.contentListFragment);
            if (currentFragment != null) {
                currentFragment.searchList(query);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        mOptionsMenu = menu;
        searchMenuItem = menu.findItem(R.id.search);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) searchMenuItem.getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        MenuItemCompat.setOnActionExpandListener(searchMenuItem, new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                ContentListFragment currentFragment = (ContentListFragment) getSupportFragmentManager().findFragmentById(R.id.contentListFragment);
                if (currentFragment != null) {
                    currentFragment.searchList(((SearchView) item.getActionView()).getQuery());
                }
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                ContentListFragment currentFragment = (ContentListFragment) getSupportFragmentManager().findFragmentById(R.id.contentListFragment);
                if (currentFragment != null) {
                    currentFragment.searchList("");
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
            case R.id.refresh:
                loadData();
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
            case R.id.filter:
                final ContentListFragment contentListFragment = (ContentListFragment) getSupportFragmentManager().findFragmentById(R.id.contentListFragment);
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Filter Communities");
                builder.setMultiChoiceItems(R.array.filterOptions, contentListFragment.getCommunityFilters(), null)
                        .setPositiveButton("Filter", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                SparseBooleanArray checked = ((AlertDialog) dialog).getListView().getCheckedItemPositions();
                                contentListFragment.filterList(checked);
                            }
                        }).setNegativeButton("Cancel", null).create();

                builder.show();

                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onShouldUpdateDetail(Object content) {
        Fragment detailFragment = null;

        if (content instanceof Community) {
            detailFragment = CommunityDetailFragment.newInstance((Community) content);
        } else if (content instanceof Patient) {
            Patient p = (Patient) content;
            detailFragment = PatientDetailFragment.newInstance(p, Utility.getAllRelatedReferrals(p.getReferralList()));
        } else if (content instanceof CarePlan) {
            detailFragment = CarePlanDetailFragment.newInstance((CarePlan) content);
        }

        if (detailFragment != null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.detailFragmentContainer, detailFragment, "detail").commit();
        }
    }

    private void sortCommunityList(CommunityAdapter.SortType type) {
        ContentListFragment contentListFragment = (ContentListFragment) getSupportFragmentManager().findFragmentById(R.id.contentListFragment);
        if (contentListFragment != null) {
            contentListFragment.sortList(type);
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == currentNavigationItemId) {
            return true;
        }

        if (mOptionsMenu != null) {
            mOptionsMenu.findItem(R.id.sort).setVisible(id == R.id.nav_communities);
            mOptionsMenu.findItem(R.id.filter).setVisible(id == R.id.nav_communities);
            mOptionsMenu.findItem(R.id.search).setVisible(id == R.id.nav_communities || id == R.id.nav_patients).collapseActionView();
            searchView.setQuery("", false);
        }

        ContentListFragment contentListFragment = (ContentListFragment) getSupportFragmentManager().findFragmentById(R.id.contentListFragment);
        updateEachTab(getNavID(id));
        if (id == R.id.nav_care_plans) {
            contentListFragment.setAdapter(new CarePlanListAdapter(Utility.carePlan_list), ContentListFragment.ContentType.Referral);
        } else if (id == R.id.nav_patients) {
            contentListFragment.setAdapter(new PatientAdapter(Utility.patient_list), ContentListFragment.ContentType.Patient);
        } else if (id == R.id.nav_communities) {
            contentListFragment.setAdapter(new CommunityAdapter(Utility.community_list), ContentListFragment.ContentType.Community);
        } else if (id == R.id.nav_account) {
            // TODO
        } else if (id == R.id.nav_settings) {
            // TODO
        } else if (id == R.id.sign_out_button_main) {
            Intent logoutIntent = new Intent(this, LoginActivity.class);
            logoutIntent.putExtra("SIGN_OUT", true);
            startActivity(logoutIntent);
            //setResult(1);
            finish();
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
            contentListFragment.closeDetailView();
        }

        currentNavigationItemId = id;

        return true;
    }

    public void loadData() {
        updateTime();
        Utility.update(getApplicationContext(),
                       (ContentListFragment) getSupportFragmentManager().findFragmentById(R.id.contentListFragment),
                       getSupportFragmentManager().beginTransaction(),
                       isInExpandedMode,
                       getNavID(currentNavigationItemId));
    }

    public void updateEachTab(int id) {
        if (id >= 0 && System.currentTimeMillis() - lastUpdateTime[id] >= UPDATE_INTERVAL) {
            switch (id) {
                case 0:
                    Utility.updateCarePlans(getApplicationContext(),
                                           (ContentListFragment) getSupportFragmentManager().findFragmentById(R.id.contentListFragment),
                                           getSupportFragmentManager().beginTransaction(),
                                           isInExpandedMode,
                                           true,
                                           false);
                    break;
                case 1:
                    Utility.updatePatients(getApplicationContext(),
                                          (ContentListFragment) getSupportFragmentManager().findFragmentById(R.id.contentListFragment),
                                          getSupportFragmentManager().beginTransaction(),
                                          isInExpandedMode,
                                          true,
                                          false);
                    break;
                case 2:
                    Utility.updateCommunityResources(getApplicationContext(),
                                            (ContentListFragment) getSupportFragmentManager().findFragmentById(R.id.contentListFragment),
                                            getSupportFragmentManager().beginTransaction(),
                                            isInExpandedMode,
                                            true,
                                            false);
                    break;

            }
            lastUpdateTime[id] = System.currentTimeMillis();
        }
    }

    public void updateTime() {
        for (int i = 0; i < lastUpdateTime.length; i++) {
            lastUpdateTime[i]  = System.currentTimeMillis();
        }
        Log.i(TAG, "Last updated time updated. (" + Arrays.toString(lastUpdateTime) + ")");
    }

    public int getNavID(int id) {
        switch (id) {
            case R.id.nav_care_plans:
                return 0;
            case R.id.nav_patients:
                return 1;
            case R.id.nav_communities:
                return 2;
        }
        return -1;
    }
}
