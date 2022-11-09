package com.itsvks.layouteditor.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.FitWindowsFrameLayout;
import androidx.core.app.ShareCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.preference.PreferenceManager;

import com.google.android.gms.oss.licenses.OssLicensesMenuActivity;
import com.google.android.material.navigation.NavigationView;
import com.itsvks.layouteditor.BaseActivity;
import com.itsvks.layouteditor.R;
import com.itsvks.layouteditor.databinding.ActivityHomeBinding;
import com.itsvks.layouteditor.fragments.ui.about.AboutFragment;
import com.itsvks.layouteditor.fragments.ui.home.HomeFragment;
import com.itsvks.layouteditor.fragments.ui.preferences.PreferencesFragment;

public class HomeActivity extends BaseActivity {

    private static final float END_SCALE = 0.7f;

    private ActivityHomeBinding binding;

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private FitWindowsFrameLayout contentView;

    private ActionBarDrawerToggle actionBarDrawerToggle;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        prefs.edit().putString("fragment", "home").apply();

        setContentView(binding.getRoot());
        setSupportActionBar(binding.topAppBar);

        contentView = binding.content;

        try {
            switch (prefs.getString("fragment", "home")) {
                case "home":
                    {
                        replaceFragment((Fragment) HomeFragment.class.newInstance());
                        getSupportActionBar().setTitle(R.string.projects);
                        break;
                    }
                case "preferences":
                    {
                        replaceFragment((Fragment) PreferencesFragment.class.newInstance());
                        getSupportActionBar().setTitle(R.string.preference);
                        break;
                    }
                case "about":
                    {
                        replaceFragment((Fragment) AboutFragment.class.newInstance());
                        getSupportActionBar().setTitle(R.string.about);
                        break;
                    }
                default:
                    {
                        replaceFragment((Fragment) HomeFragment.class.newInstance());
                        getSupportActionBar().setTitle(R.string.projects);
                    }
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }

        drawerLayout = binding.drawer;
        navigationView = binding.navigationView;

        navigationDrawer();

        actionBarDrawerToggle =
                new ActionBarDrawerToggle(
                        this,
                        drawerLayout,
                        binding.topAppBar,
                        R.string.navigation_drawer_open,
                        R.string.navigation_drawer_close);

        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
    }

    private void navigationDrawer() {
        navigationView.bringToFront();
        navigationView.setNavigationItemSelectedListener(
                item -> {
                    var id = item.getItemId();

                    if (id == R.id.nav_home) {
                        try {
                            replaceFragment((Fragment) HomeFragment.class.newInstance());
                            getSupportActionBar().setTitle(R.string.projects);
                            prefs.edit().putString("fragment", "home").apply();
                            drawerLayout.closeDrawer(GravityCompat.START);
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        } catch (InstantiationException e) {
                            e.printStackTrace();
                        }
                        return true;
                    } else if (id == R.id.nav_preference) {
                        try {
                            replaceFragment((Fragment) PreferencesFragment.class.newInstance());
                            getSupportActionBar().setTitle(R.string.title_preference);
                            prefs.edit().putString("fragment", "preferences").apply();
                            drawerLayout.closeDrawer(GravityCompat.START);
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        } catch (InstantiationException e) {
                            e.printStackTrace();
                        }
                        return true;
                    } else if (id == R.id.nav_about) {
                        try {
                            replaceFragment((Fragment) AboutFragment.class.newInstance());
                            getSupportActionBar().setTitle(R.string.title_about);
                            prefs.edit().putString("fragment", "about").apply();
                            drawerLayout.closeDrawer(GravityCompat.START);
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        } catch (InstantiationException e) {
                            e.printStackTrace();
                        }
                        return true;
                    } else if (id == R.id.nav_licence) {
                        startActivity(new Intent(this, OssLicensesMenuActivity.class));
                        return true;
                    } else if (id == R.id.nav_github) {
                        openUrl("https://github.com/itsvks19/LayoutEditor");
                        return true;
                    } else if (id == R.id.nav_share) {
                        var shareIntent = new ShareCompat.IntentBuilder(this);
                        shareIntent.setType("text/plain");
                        shareIntent.setChooserTitle(getString(R.string.app_name));
                        shareIntent.setText(
                                getString(R.string.share_description)
                                        + "\n\nDownload from here: https://github.com/itsvks19/LayoutEditor/");
                        shareIntent.startChooser();
                        return true;
                    } else return false;
                });
        navigationView.setCheckedItem(R.id.nav_home);

        animateNavigationDrawer();
    }

    private void animateNavigationDrawer() {
        drawerLayout.addDrawerListener(
                new DrawerLayout.SimpleDrawerListener() {

                    @Override
                    public void onDrawerSlide(View drawerView, float slideOffset) {

                        final float diffScaledOffset = slideOffset * (1 - END_SCALE);
                        final float offsetScale = 1 - diffScaledOffset;
                        contentView.setScaleX(offsetScale);
                        contentView.setScaleY(offsetScale);

                        final float xOffset = drawerView.getWidth() * slideOffset;
                        final float xOffsetDiff = contentView.getWidth() * diffScaledOffset / 2;
                        final float xTranslation = xOffset - xOffsetDiff;
                        contentView.setTranslationX(xTranslation);
                    }
                });
    }

    @Override
    public void onBackPressed() {
        if (!drawerLayout.isDrawerVisible(GravityCompat.START)) {
            try {
                switch (prefs.getString("fragment", "home")) {
                    case "preferences":
                        {
                            replaceFragment((Fragment) HomeFragment.class.newInstance());
                            prefs.edit().putString("fragment", "home").apply();
                            getSupportActionBar().setTitle(R.string.projects);
                            navigationView.setCheckedItem(R.id.nav_home);
                            break;
                        }
                    case "about":
                        {
                            replaceFragment((Fragment) HomeFragment.class.newInstance());
                            prefs.edit().putString("fragment", "home").apply();
                            getSupportActionBar().setTitle(R.string.projects);
                            navigationView.setCheckedItem(R.id.nav_home);
                            break;
                        }
                    case "home":
                        {
                            finishAffinity();
                            break;
                        }
                    default:
                        {
                            replaceFragment((Fragment) HomeFragment.class.newInstance());
                            prefs.edit().putString("fragment", "home").apply();
                            getSupportActionBar().setTitle(R.string.projects);
                            navigationView.setCheckedItem(R.id.nav_home);
                        }
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            }
        } else if (drawerLayout.isDrawerVisible(GravityCompat.START))
            drawerLayout.closeDrawer(GravityCompat.START);
        else super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        var id = item.getItemId();
        if (id == android.R.id.home) {
            drawerLayout.openDrawer(GravityCompat.START);
            return true;
        }
        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConfigurationChanged(Configuration config) {
        super.onConfigurationChanged(config);
        actionBarDrawerToggle.onConfigurationChanged(config);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        actionBarDrawerToggle.syncState();
    }

    private void replaceFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .replace(R.id.main_fragment, fragment)
                .commit();
    }
}
