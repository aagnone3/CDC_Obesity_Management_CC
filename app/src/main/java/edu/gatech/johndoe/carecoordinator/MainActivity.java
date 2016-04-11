package edu.gatech.johndoe.carecoordinator;

import android.Manifest;
import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
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

import java.io.InputStream;
import java.util.Arrays;

import edu.gatech.johndoe.carecoordinator.care_plan.UI.CarePlanDetailFragment;
import edu.gatech.johndoe.carecoordinator.care_plan.UI.CarePlanListAdapter;
import edu.gatech.johndoe.carecoordinator.community.Community;
import edu.gatech.johndoe.carecoordinator.community.UI.CommunityAdapter;
import edu.gatech.johndoe.carecoordinator.community.UI.CommunityDetailFragment;
import edu.gatech.johndoe.carecoordinator.community.UI.CommunityListFragment;
import edu.gatech.johndoe.carecoordinator.patient.*;
import edu.gatech.johndoe.carecoordinator.care_plan.CarePlan;
import edu.gatech.johndoe.carecoordinator.patient.UI.PatientAdapter;
import edu.gatech.johndoe.carecoordinator.patient.UI.PatientDetailFragment;
import edu.gatech.johndoe.carecoordinator.util.Utility;


public class MainActivity extends AppCompatActivity implements OnFragmentInteractionListener, NavigationView.OnNavigationItemSelectedListener {

    public static boolean isInExpandedMode;
    public static GoogleSignInAccount currentUserAccount;
    public static final String TAG = "MainActivity";
    public static final long UPDATE_INTERVAL = 60 * 10 * 1000;  // 10 minutes

    private Menu mOptionsMenu;
    private CommunityListFragment currentFragment;
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
            new ProfilePictureTask((ImageView) headerView.findViewById(R.id.coordinatorPicture)).execute(currentUserAccount.getPhotoUrl());
        }
        ((TextView) headerView.findViewById(R.id.coordinatorName)).setText(currentUserAccount.getDisplayName());
        ((TextView) headerView.findViewById(R.id.coordinatorEmail)).setText(currentUserAccount.getEmail());

        if (savedInstanceState == null) {
            onNavigationItemSelected(navigationView.getMenu().getItem(0).setChecked(true));
            updateTime();
            Utility.getAllCarePlans();
            Utility.getAllPatients();
            Utility.getAllCommunities();
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
        MenuItem refreshMenuItem = menu.findItem(R.id.refresh);
        MenuItem searchMenuItem = menu.findItem(R.id.search);
        MenuItem filterMenuItem = menu.findItem(R.id.filter);

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
        } else if (content instanceof Patient) {
            Patient p = (Patient) content;
            Fragment detailFragment = PatientDetailFragment.newInstance(p,  Utility.getAllRelatedReferrals(p.getReferralList()));
            getSupportFragmentManager().beginTransaction().replace(R.id.detailFragmentContainer, detailFragment, "detail").commit();
        } else if (content instanceof CarePlan) {
            Fragment detailFragment = CarePlanDetailFragment.newInstance((CarePlan) content);
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
        updateEachTab(getNavID(id));
        if (id == R.id.nav_care_plans) {
            contentListFragment.setAdapter(new CarePlanListAdapter(Utility.carePlan_list), ContentListFragment.ContentType.Referral); // FIXME: replace with the referral adapter/data
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
        Utility.fhirUpdate();
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

    private class ProfilePictureTask extends AsyncTask<Uri, Void, Bitmap> {
        private ImageView imageView;

        public ProfilePictureTask(ImageView bmImage) {
            this.imageView = bmImage;
        }

        protected Bitmap doInBackground(Uri... uris) {
            Bitmap picture = null;

            try {
                InputStream in = new java.net.URL(uris[0].toString()).openStream();
                picture = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("MainActivity", "Error while downloading profile picture: " + e.getMessage());
            }

            return picture;
        }

        protected void onPostExecute(Bitmap result) {
            imageView.setImageBitmap(result);
        }
    }
}
