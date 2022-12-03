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
import com.itsvks.layouteditor.fragments.ui.AboutFragment;
import com.itsvks.layouteditor.fragments.ui.HomeFragment;
import com.itsvks.layouteditor.fragments.ui.PreferencesFragment;
import com.itsvks.layouteditor.utils.Constants;

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

        setContentView(binding.getRoot());
        setSupportActionBar(binding.topAppBar);

        drawerLayout = binding.drawer;
        navigationView = binding.navigationView;

        contentView = binding.content;
        switch (prefs.getString("fragment", "home")) {
            case "preferences":
                goToPreference();
                navigationView.setCheckedItem(R.id.nav_preference);
                break;
            case "about":
                goToAbout();
                navigationView.setCheckedItem(R.id.nav_about);
                break;

            case "home":
                goToHome();
                navigationView.setCheckedItem(R.id.nav_home);
                break;

            default:
                goToHome();
                navigationView.setCheckedItem(R.id.nav_home);
        }

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

                    switch (id) {
                        case R.id.nav_home:
                            drawerLayout.closeDrawer(GravityCompat.START);
                            goToHome();
                            navigationView.setCheckedItem(R.id.nav_home);
                            return true;
                        case R.id.nav_preference:
                            drawerLayout.closeDrawer(GravityCompat.START);
                            goToPreference();
                            navigationView.setCheckedItem(R.id.nav_preference);
                            return true;
                        case R.id.nav_about:
                            drawerLayout.closeDrawer(GravityCompat.START);
                            goToAbout();
                            navigationView.setCheckedItem(R.id.nav_about);
                            return true;
                        case R.id.nav_licence:
                            startActivity(new Intent(this, OssLicensesMenuActivity.class));
                            return true;
                        case R.id.nav_github:
                            openUrl(Constants.GITHUB_URL);
                            return true;
                        case R.id.nav_share:
                            var shareIntent = new ShareCompat.IntentBuilder(this);
                            shareIntent.setType("text/plain");
                            shareIntent.setChooserTitle(getString(R.string.app_name));
                            shareIntent.setText(
                                    getString(R.string.share_description, Constants.GITHUB_URL));
                            shareIntent.startChooser();
                            return true;
                        default:
                            return false;
                    }
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

            switch (prefs.getString("fragment", "home")) {
                case "preferences":
                case "about":
                    goToHome();
                    navigationView.setCheckedItem(R.id.nav_home);
                    break;

                case "home":
                    finishAffinity();
                    break;

                default:
                    goToHome();
                    navigationView.setCheckedItem(R.id.nav_home);
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
        if (actionBarDrawerToggle.onOptionsItemSelected(item)) return true;

        return false;
    }

    @Override
    public void onConfigurationChanged(Configuration config) {
        super.onConfigurationChanged(config);
        goToHome();
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

    private void goToHome() {
        replaceFragment((Fragment) new HomeFragment());
        getSupportActionBar().setTitle(R.string.projects);
        prefs.edit().putString("fragment", "home").apply();
    }

    private void goToPreference() {
        replaceFragment((Fragment) new PreferencesFragment());
        getSupportActionBar().setTitle(R.string.title_preference);
        prefs.edit().putString("fragment", "preferences").apply();
    }

    private void goToAbout() {
        replaceFragment((Fragment) new AboutFragment());
        getSupportActionBar().setTitle(R.string.title_about);
        prefs.edit().putString("fragment", "about").apply();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}
