package nhl.containing.managmentinterface;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import nhl.containing.managmentinterface.navigationdrawer.*;

/**
 * Main activity for the app
 */
public class MainActivity extends ActionBarActivity implements ContainersFragment.OnFragmentInteractionListener
{
    //navigation drawer
    ListView mDrawerList;
    RelativeLayout mDrawerPane;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    ArrayList<NavItem> mNavItems = new ArrayList<NavItem>();
    //end navigation drawer

    private Menu menu;
    private volatile Fragment fragment;
    private volatile int refreshTime = 0;
    private AutoRefreshRunnable autorefreshRunnable;
    private ExecutorService executer = Executors.newSingleThreadExecutor();

    /**
     * Creates the Activity
     * @param savedInstanceState used for saved data (on resume)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar)findViewById(R.id.tool_bar);
        if(toolbar == null)
            this.finishAffinity();
        setSupportActionBar(toolbar);
        setupNavDrawer(toolbar);
        setupHomeFragment();
    }

    /**
     * Setup the nav drawer
     * @param toolbar toolbar
     */
    private void setupNavDrawer(Toolbar toolbar)
    {
        mNavItems.add(new NavItem("Per Category", "Numbers per category", R.drawable.ic_home_black));
        mNavItems.add(new NavItem("Graph2", "Unknown", R.drawable.ic_poll_black));
        mNavItems.add(new NavItem("Graph3", "Unknown", R.drawable.ic_poll_black));
        mNavItems.add(new NavItem("Graph4", "Unknown", R.drawable.ic_poll_black));
        mNavItems.add(new NavItem("Containers","List with actual container stats",R.drawable.ic_list_black));
        mDrawerLayout = (DrawerLayout)findViewById(R.id.drawerLayout);
        mDrawerPane = (RelativeLayout)findViewById(R.id.drawerPane);
        mDrawerList = (ListView)findViewById(R.id.navList);
        DrawerListAdapter adapter = new DrawerListAdapter(this,mNavItems);
        mDrawerList.setAdapter(adapter);
        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectItemFromDrawer(position);
            }
        });

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,toolbar, R.string.drawer_open, R.string.drawer_close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                completeRefresh.run();
                invalidateOptionsMenu();
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);

                invalidateOptionsMenu();
            }
        };
        mDrawerLayout.post(new Runnable() {
            @Override
            public void run() {
                mDrawerToggle.syncState();
            }
        });

        mDrawerLayout.setDrawerListener(mDrawerToggle);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    /**
     * Setup the home fragment
     */
    private void setupHomeFragment()
    {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        GraphFragment gf = new GraphFragment();
        fragment = gf;
        ft.replace(R.id.frame, gf);
        ft.commit();
    }

    /**
     * Creates the Menu
     * @param menu menu
     * @return true
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        this.menu = menu;
        return true;
    }

    /**
     * Prepares the Menu
     * @param menu menu
     * @return boolean
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu)
    {
        boolean drawerOpen = mDrawerLayout.isDrawerOpen(GravityCompat.START);
        menu.findItem(R.id.action_refresh).setVisible(!drawerOpen);
        menu.findItem(R.id.action_refresh_time).setVisible(!drawerOpen);
        menu.findItem(R.id.action_legend).setVisible(!drawerOpen);
        menu.findItem(R.id.action_legend).setVisible(fragment instanceof GraphFragment);
        return super.onPrepareOptionsMenu(menu);
    }

    /**
     * Triggered when an item from the menu is selected
     * @param item the item that is selected
     * @return boolean
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id)
        {
            case R.id.action_settings:

                break;
            case R.id.action_refresh:
                refresh();
                break;
            case R.id.menu_refresh_0:
                refreshTime = 0;
                if(autorefreshRunnable != null)
                    autorefreshRunnable.stop();
                if(!executer.isShutdown())
                    executer.shutdown();
                autorefreshRunnable = null;
                break;
            case R.id.menu_refresh_5:
                refreshTime = 5;
                if(autorefreshRunnable == null)
                {
                    autorefreshRunnable = new AutoRefreshRunnable();
                    executer.submit(autorefreshRunnable);
                }
                break;
            case R.id.menu_refresh_10:
                refreshTime = 10;
                if(autorefreshRunnable == null)
                {
                    autorefreshRunnable = new AutoRefreshRunnable();
                    executer.submit(autorefreshRunnable);
                }
                break;
            case R.id.menu_refresh_20:
                refreshTime = 20;
                if(autorefreshRunnable != null)
                {
                    autorefreshRunnable = new AutoRefreshRunnable();
                    executer.submit(autorefreshRunnable);
                }
                break;
            case R.id.menu_refresh_30:
                refreshTime = 30;
                if(autorefreshRunnable != null)
                {
                    autorefreshRunnable = new AutoRefreshRunnable();
                    executer.submit(autorefreshRunnable);
                }
                break;
            case R.id.action_legend:
                showLegend();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Shows a legend for the graph
     */
    private void showLegend()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this,R.style.AppCompatAlertDialogStyle);
        builder.setTitle("Legend");
        builder.setMessage("Tra = Train\nTru = Truck\nSea = Seaship\nInl = Inlineship\nSto = Storage\nAGV = Automatic Guided Vehicles\nRem = Remaining");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    /**
     * complete the refresh
     */
    private Runnable completeRefresh = new Runnable() {
        @Override
        public void run() {
            MenuItem ri =  menu.findItem(R.id.action_refresh);
            if(ri.getActionView() != null)
            {
                ri.getActionView().clearAnimation();
                ri.setActionView(null);
            }
        }
    };

    /**
     * refresh the graph
     */
    private void refresh()
    {
        LayoutInflater inflater = (LayoutInflater) getApplication().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ImageView iv = (ImageView) inflater.inflate(R.layout.rotaterefresh, null);
        Animation rotate = AnimationUtils.loadAnimation(getApplication(),R.anim.rotate);
        rotate.setRepeatCount(Animation.INFINITE);
        iv.startAnimation(rotate);
        menu.findItem(R.id.action_refresh).setActionView(iv);
        new Thread(new Runnable() {
            @Override
            public void run() {
                if(fragment!= null && fragment instanceof GraphFragment)
                {
                    GraphFragment gf = (GraphFragment)fragment;
                    try{
                        Thread.sleep(2000);
                    }
                    catch (Exception e){}
                    gf.setData();
                }
                runOnUiThread(completeRefresh);
            }
        }).start();
    }

    /**
     * Runnable for refreshing
     */
    private Runnable refreshRunnable = new Runnable() {
        @Override
        public void run() {
            refresh();
        }
    };

    /**
     * Select item from the navigation drawer
     * @param position the position of the item
     */
    private void selectItemFromDrawer(int position)
    {
        Fragment f;
        switch (position)
        {
            case 0:
                f = getSupportFragmentManager().findFragmentByTag("Graph_one");
                if(f == null || !f.isVisible())
                {
                    if(autorefreshRunnable != null)
                        autorefreshRunnable.stop();
                    if(executer.isShutdown())
                        executer.shutdown();
                    autorefreshRunnable = null;
                    GraphFragment gf = new GraphFragment();
                    fragment = gf;
                    Bundle b = new Bundle();
                    b.putInt("graphID",position);
                    gf.setArguments(b);
                    getSupportFragmentManager().beginTransaction().replace(R.id.frame,gf,"Graph_one").commit();
                }
                mDrawerLayout.closeDrawers();
                break;
            case 1:
                f = getSupportFragmentManager().findFragmentByTag("Graph_two");
                if(f == null || !f.isVisible())
                {
                    if(autorefreshRunnable != null)
                        autorefreshRunnable.stop();
                    if(executer.isShutdown())
                        executer.shutdown();
                    autorefreshRunnable = null;
                    GraphFragment gf = new GraphFragment();
                    fragment = gf;
                    Bundle b = new Bundle();
                    b.putInt("graphID",position);
                    gf.setArguments(b);
                    getSupportFragmentManager().beginTransaction().replace(R.id.frame,gf,"Graph_two").commit();
                }
                mDrawerLayout.closeDrawers();
                break;
            case 2:
                f = getSupportFragmentManager().findFragmentByTag("Graph_three");
                if(f == null || !f.isVisible())
                {
                    if(autorefreshRunnable != null)
                        autorefreshRunnable.stop();
                    if(executer.isShutdown())
                        executer.shutdown();
                    autorefreshRunnable = null;
                    GraphFragment gf = new GraphFragment();
                    fragment = gf;
                    Bundle b = new Bundle();
                    b.putInt("graphID",position);
                    gf.setArguments(b);
                    getSupportFragmentManager().beginTransaction().replace(R.id.frame,gf,"Graph_three").commit();
                }
                mDrawerLayout.closeDrawers();
                break;
            case 3:
                f = getSupportFragmentManager().findFragmentByTag("Graph_four");
                if(f == null || !f.isVisible())
                {
                    if(autorefreshRunnable != null)
                        autorefreshRunnable.stop();
                    if(executer.isShutdown())
                        executer.shutdown();
                    autorefreshRunnable = null;
                    GraphFragment gf = new GraphFragment();
                    fragment = gf;
                    Bundle b = new Bundle();
                    b.putInt("graphID",position);
                    gf.setArguments(b);
                    getSupportFragmentManager().beginTransaction().replace(R.id.frame,gf,"Graph_four").commit();
                }
                mDrawerLayout.closeDrawers();
                break;
            case 4:
                f = getSupportFragmentManager().findFragmentByTag("Container_list");
                if(f == null || !f.isVisible())
                {
                    if(autorefreshRunnable != null)
                        autorefreshRunnable.stop();
                    if(executer.isShutdown())
                        executer.shutdown();
                    autorefreshRunnable = null;
                    ContainersFragment cf = new ContainersFragment();
                    fragment = cf;
                    getSupportFragmentManager().beginTransaction().replace(R.id.frame,cf,"Container_list").commit();
                }
                mDrawerLayout.closeDrawers();
                break;

        }
    }

    @Override
    public void onFragmentInteraction(String id) {

    }

    /**
     * Runnable for autorefreshing datat
     */
    private class AutoRefreshRunnable implements Runnable
    {
        private volatile boolean isRunning = true;

        @Override
        public void run() {
            while(isRunning){
                doJob();
            }
        }

        private void doJob()
        {
            if(refreshTime == 0)
                return;
            runOnUiThread(refreshRunnable);
            try{
                Thread.sleep(refreshTime * 1000);
            }catch (Exception e){}
        }

        public void stop()
        {
            isRunning = false;
        }
    }
}