package com.itsvks.layouteditor.adapters;

import android.graphics.drawable.Drawable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import java.util.ArrayList;
import java.util.List;

public class PagerAdapter {
  private Adapter adapter;

  private ViewPager2 pager;
  private TabLayout layout;

  private List<Fragment> fragmentList = new ArrayList<>();
  private List<CharSequence> fragmentTitleList = new ArrayList<>();
  private List<Drawable> fragmentIconList = new ArrayList<>();

  private class Adapter extends FragmentStateAdapter {
    public Adapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
      super(fragmentManager, lifecycle);
    }

    @Override
    public int getItemCount() {
      return fragmentList.size();
    }

    @Override
    public Fragment createFragment(int position) {
      return fragmentList.get(position);
    }
  }

  public PagerAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
    adapter = new Adapter(fragmentManager, lifecycle);
  }

  public void setup(ViewPager2 pager, TabLayout layout) {
    this.pager = pager;
    this.layout = layout;
  }

  public void addFragmentToAdapter(Fragment fragment, CharSequence title) {
    fragmentList.add(fragment);
    fragmentTitleList.add(title);
  }

  public void addFragmentToAdapter(Fragment fragment, CharSequence title, Drawable icon) {
    fragmentList.add(fragment);
    fragmentTitleList.add(title);
    fragmentIconList.add(icon);
  }
    
  public int getFragmentsCount() {
    return fragmentList.size();
  }
    
  public Fragment getFragmentAt(int position) {
    if (fragmentList.get(position) == null) return fragmentList.get(0);
    return fragmentList.get(position);
  }
    
  public CharSequence getFragmentTitleAt(int position) {
    if (fragmentTitleList.get(position) == null) return fragmentTitleList.get(0);
    return fragmentTitleList.get(position);
  }

  public Drawable getFragmentIconAt(int position) {
    if (fragmentIconList.get(position) == null) return fragmentIconList.get(0);
    return fragmentIconList.get(position);
  }

  public Fragment getFragmentWithTitle(CharSequence title) {
    for (int i = 0; i < getFragmentsCount(); i++) {
      if (title == getFragmentTitleAt(i)) {
        return getFragmentAt(i);
      }
    }
    return fragmentList.get(0);
  }

  public int getFragmentPosition(Fragment fragment) {
    for (int i = 0; i < getFragmentsCount(); i++) {
      if (fragment == fragmentList.get(i)) {
        return i;
      }
    }
    return 0;
  }

  public void setupPager(int orientation) {
    pager.setOrientation(orientation);
    pager.setAdapter(adapter);
  }

  public void setupMediatorWithIcon() {
    TabLayoutMediator mediator = new TabLayoutMediator(layout, pager, new TabLayoutMediator.TabConfigurationStrategy() {
      @Override
      public void onConfigureTab(TabLayout.Tab tab, int position) {
        tab.setText(getFragmentTitleAt(position));
        tab.setIcon(getFragmentIconAt(position));
      }
    });
    mediator.attach();
  }

  public void setupMediatorWithoutIcon() {
    TabLayoutMediator mediator = new TabLayoutMediator(layout, pager, new TabLayoutMediator.TabConfigurationStrategy() {
      @Override
      public void onConfigureTab(TabLayout.Tab tab, int position) {
        tab.setText(getFragmentTitleAt(position));
      }
    });
    mediator.attach();
  }
}
