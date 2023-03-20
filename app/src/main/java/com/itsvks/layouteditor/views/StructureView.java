package com.itsvks.layouteditor.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.webkit.WebView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.CheckBox;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.MultiAutoCompleteTextView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.SearchView;
import android.widget.SeekBar;
import android.widget.Space;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TabHost;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextClock;
import android.widget.TextView;

import android.widget.ToggleButton;
import android.widget.VideoView;
import androidx.appcompat.widget.LinearLayoutCompat;

import androidx.appcompat.widget.TooltipCompat;
import androidx.cardview.widget.CardView;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.color.MaterialColors;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.itsvks.layouteditor.databinding.LayoutStructureViewItemBinding;
import com.itsvks.layouteditor.managers.IdManager;
import java.util.HashMap;
import com.itsvks.layouteditor.R;
import java.util.Map;

public class StructureView extends LinearLayoutCompat implements View.OnClickListener {
  private LayoutInflater inflater;
  private Paint paint;
  private int pointRadius;
  private Map<TextView, View> textViewMap = new HashMap<>();
  private Map<View, TextView> viewTextMap = new HashMap<>();
  private OnItemClickListener listener;
  private int primaryColor;

  public static Map<String, Integer> imgMap = new HashMap<>();

  static {
    imgMap.put("_unknown", R.mipmap.ic_palette_unknown_view);
    imgMap.put(TextView.class.getSimpleName(), R.mipmap.ic_palette_text_view);
    imgMap.put(EditText.class.getSimpleName(), R.mipmap.ic_palette_edit_text);
    imgMap.put(Button.class.getSimpleName(), R.mipmap.ic_palette_button);
    imgMap.put(ImageButton.class.getSimpleName(), R.mipmap.ic_palette_image_button);
    imgMap.put(ImageView.class.getSimpleName(), R.mipmap.ic_palette_image_view);
    imgMap.put(VideoView.class.getSimpleName(), R.mipmap.ic_palette_video_view);
    imgMap.put(
        AutoCompleteTextView.class.getSimpleName(), R.mipmap.ic_palette_auto_complete_text_view);
    imgMap.put(
        MultiAutoCompleteTextView.class.getSimpleName(),
        R.mipmap.ic_palette_multi_auto_complete_text_view);
    imgMap.put(CheckedTextView.class.getSimpleName(), R.mipmap.ic_palette_checked_text_view);
    imgMap.put(CheckBox.class.getSimpleName(), R.mipmap.ic_palette_check_box);
    imgMap.put(RadioButton.class.getSimpleName(), R.mipmap.ic_palette_radio_button);
    imgMap.put(RadioGroup.class.getSimpleName(), R.mipmap.ic_palette_radio_group);
    imgMap.put(ToggleButton.class.getSimpleName(), R.mipmap.ic_palette_toggle_button);
    imgMap.put(Switch.class.getSimpleName(), R.mipmap.ic_palette_switch);
    imgMap.put(View.class.getSimpleName(), R.mipmap.ic_palette_view);
    imgMap.put(WebView.class.getSimpleName(), R.mipmap.ic_palette_web_view);
    imgMap.put(CalendarView.class.getSimpleName(), R.mipmap.ic_palette_calendar_view);
    imgMap.put(ProgressBar.class.getSimpleName(), R.mipmap.ic_palette_progress_bar);
    imgMap.put(
        ProgressBar.class.getSimpleName() + "horizontal",
        R.mipmap.ic_palette_progress_bar_horizontal);
    imgMap.put(SeekBar.class.getSimpleName(), R.mipmap.ic_palette_seek_bar);
    imgMap.put(RatingBar.class.getSimpleName(), R.mipmap.ic_palette_rating_bar);
    imgMap.put(TextureView.class.getSimpleName(), R.mipmap.ic_palette_texture_view);
    imgMap.put(SurfaceView.class.getSimpleName(), R.mipmap.ic_palette_surface_view);
    imgMap.put(SearchView.class.getSimpleName(), R.mipmap.ic_palette_search_view);
    imgMap.put(
        LinearLayout.class.getSimpleName() + "horizontal", R.mipmap.ic_palette_linear_layout_horz);
    imgMap.put(
        LinearLayout.class.getSimpleName() + "vertical", R.mipmap.ic_palette_linear_layout_vert);
    imgMap.put(FrameLayout.class.getSimpleName(), R.mipmap.ic_palette_frame_layout);
    imgMap.put(TableLayout.class.getSimpleName(), R.mipmap.ic_palette_table_layout);
    imgMap.put(TableRow.class.getSimpleName(), R.mipmap.ic_palette_table_row);
    imgMap.put(Space.class.getSimpleName(), R.mipmap.ic_palette_space);
    imgMap.put(Spinner.class.getSimpleName(), R.mipmap.ic_palette_spinner);
    imgMap.put(ScrollView.class.getSimpleName(), R.mipmap.ic_palette_scroll_view);
    imgMap.put(
        HorizontalScrollView.class.getSimpleName(), R.mipmap.ic_palette_horizontal_scroll_view);
    imgMap.put(ViewStub.class.getSimpleName(), R.mipmap.ic_palette_view_stub);
    imgMap.put("include", R.mipmap.ic_palette_include);
    imgMap.put(GridLayout.class.getSimpleName(), R.mipmap.ic_palette_grid_layout);
    imgMap.put(GridView.class.getSimpleName(), R.mipmap.ic_palette_grid_view);
    imgMap.put(RecyclerView.class.getSimpleName(), R.mipmap.ic_palette_recycler_view);
    imgMap.put(ListView.class.getSimpleName(), R.mipmap.ic_palette_list_view);
    imgMap.put(TabHost.class.getSimpleName(), R.mipmap.ic_palette_tab_host);
    imgMap.put(RelativeLayout.class.getSimpleName(), R.mipmap.ic_palette_relative_layout);
    imgMap.put(Chip.class.getSimpleName(), R.mipmap.ic_palette_chip);
    imgMap.put(ChipGroup.class.getSimpleName(), R.mipmap.ic_palette_chip_group);
    imgMap.put(
        FloatingActionButton.class.getSimpleName(), R.mipmap.ic_palette_floating_action_button);
    imgMap.put(NestedScrollView.class.getSimpleName(), R.mipmap.ic_palette_nested_scroll_view);
    imgMap.put(ViewPager.class.getSimpleName(), R.mipmap.ic_palette_view_pager);
    imgMap.put(ViewPager2.class.getSimpleName(), R.mipmap.ic_palette_view_pager);
    imgMap.put(CardView.class.getSimpleName(), R.mipmap.ic_palette_card_view);
    imgMap.put(TextClock.class.getSimpleName(), R.mipmap.ic_palette_text_clock);
    imgMap.put(AppBarLayout.class.getSimpleName(), R.mipmap.ic_palette_app_bar_layout);
    imgMap.put(NavigationView.class.getSimpleName(), R.mipmap.ic_palette_navigation_view);
    imgMap.put(
        BottomNavigationView.class.getSimpleName(), R.mipmap.ic_palette_bottom_navigation_view);
  }

  /**
   * This is the constructor of the StructureView class which takes context and attributeSet as
   * parameters. It creates a new Paint object, sets its color and anti-alias. It also sets the
   * orientation of this view to VERTICAL and sets the default OnItemClickListener.
   */
  public StructureView(Context context, AttributeSet attrs) {
    super(context, attrs);
    inflater = LayoutInflater.from(context);

    paint = new Paint();
    primaryColor = MaterialColors.getColor(this, com.google.android.material.R.attr.colorPrimary);
    paint.setColor(primaryColor);
    paint.setAntiAlias(true);
    paint.setStrokeWidth(getDip(1));

    pointRadius = getDip(3);

    setOrientation(VERTICAL);
    listener =
        new OnItemClickListener() {
          @Override
          public void onItemClick(View view) {}
        };
  }

  /** This method clears all Views and HashMaps stored in this view. */
  public void clear() {
    removeAllViews();
    textViewMap.clear();
    viewTextMap.clear();
  }

  /**
   * This method sets a View to this view. It clears all the stored views and hashmaps, and then
   * calls the peek() method to peek into the View.
   */
  public void setView(View view) {
    textViewMap.clear();
    viewTextMap.clear();
    removeAllViews();
    peek(view, 1);
  }

  /**
   * This method recursively calls itself to add TextViews for each View inside the ViewGroup. It
   * also stores the TextViews and Views in their respective hashmaps.
   */
  private void peek(View view, int depth) {
    int nextDepth = depth;
    LayoutStructureViewItemBinding binding =
        LayoutStructureViewItemBinding.inflate(inflater, null, false);
    TextView viewName = binding.viewName;
    TextView viewId = binding.viewId;
    ImageView icon = binding.icon;
    if (view.getId() == -1 || IdManager.getIdMap().get(view) == null) {
      viewId.setVisibility(View.GONE);
      viewName.setTranslationY(0);
      viewId.setTranslationY(0);
      TooltipCompat.setTooltipText(
          binding.mainView, view.getClass().getSuperclass().getSimpleName());
    } else {
      viewName.setTranslationY(getDip(-7));
      viewId.setTranslationY(getDip(-3));
      viewId.setVisibility(View.VISIBLE);
      viewId.setText(IdManager.getIdMap().get(view));
      TooltipCompat.setTooltipText(binding.mainView, IdManager.getIdMap().get(view));
    }
    if (view instanceof LinearLayout && !(view instanceof RadioGroup)) {
      String orientation =
          ((LinearLayout) view).getOrientation() == HORIZONTAL ? "horizontal" : "vertical";
      int imgResId = imgMap.get(LinearLayout.class.getSimpleName() + orientation);

      icon.setImageResource(imgResId);
      viewName.setText(LinearLayout.class.getSimpleName() + " (" + orientation + ")");
    } else {
      String viewSimpleName = view.getClass().getSuperclass().getSimpleName();
      Integer imageResource = imgMap.get(viewSimpleName);
      if (imageResource == null) {
        imageResource = imgMap.get("_unknown");
      }
      icon.setImageResource(imageResource);
      viewName.setText(viewSimpleName);
    }

    binding.mainView.setOnClickListener(this);

    addView(binding.getRoot());

    LinearLayoutCompat.LayoutParams params =
        (LinearLayoutCompat.LayoutParams) binding.getRoot().getLayoutParams();
    params.leftMargin = depth * getDip(15);

    textViewMap.put(viewName, view);
    viewTextMap.put(view, viewName);

    if (view instanceof ViewGroup) {
      ViewGroup group = (ViewGroup) view;
      if (!(group instanceof CalendarView)
          && !(group instanceof SearchView)
          && !(group instanceof NavigationView)
          && !(group instanceof BottomNavigationView)
          && !(group instanceof TabLayout)) {
        nextDepth++;

        for (int i = 0; i < group.getChildCount(); i++) {
          View child = group.getChildAt(i);
          peek(child, nextDepth);
        }
      }
    }
  }

  /** This method is called to draw rectangles, lines, and circles for each TextView in the view. */
  @Override
  protected void dispatchDraw(Canvas canvas) {
    super.dispatchDraw(canvas);

    for (TextView text : textViewMap.keySet()) {
      View view = textViewMap.get(text);
      ViewGroup parent = (ViewGroup) text.getParent().getParent();

      if (view instanceof ViewGroup && ((ViewGroup) view).getChildCount() > 0) {
        float x = parent.getX();
        float y = parent.getY() + parent.getHeight() / 2;

        ViewGroup group = (ViewGroup) view;
        if (!(group instanceof CalendarView)
            && !(group instanceof SearchView)
            && !(group instanceof NavigationView)
            && !(group instanceof BottomNavigationView)
            && !(group instanceof TabLayout)) {
          canvas.drawRect(
              x - pointRadius, y - pointRadius, x + pointRadius, y + pointRadius, paint);
          for (int i = 0; i < group.getChildCount(); i++) {
            TextView current = viewTextMap.get(group.getChildAt(i));
            ViewGroup currentParent = (ViewGroup) current.getParent().getParent();
            canvas.drawLine(
                parent.getX(),
                parent.getY() + parent.getHeight() / 2,
                parent.getX(),
                currentParent.getY() + currentParent.getHeight() / 2,
                paint);
            canvas.drawLine(
                parent.getX(),
                currentParent.getY() + currentParent.getHeight() / 2,
                currentParent.getX(),
                currentParent.getY() + currentParent.getHeight() / 2,
                paint);
          }
        } else {
          canvas.drawCircle(
              parent.getX(), parent.getY() + parent.getHeight() / 2, pointRadius, paint);
        }
      } else {
        canvas.drawCircle(
            parent.getX(), parent.getY() + parent.getHeight() / 2, pointRadius, paint);
      }
    }
  }

  /**
   * This method is called when a TextView is clicked, and it calls the OnItemClickListener's
   * onItemClick method.
   */
  @Override
  public void onClick(View v) {
    if (listener != null && v instanceof ViewGroup) {
      for (int i = 0; i < ((ViewGroup) v).getChildCount(); i++) {
        View child = ((ViewGroup) v).getChildAt(i);
        if (child.getId() == R.id.view_name)
          listener.onItemClick(textViewMap.get((TextView) child));
      }
    }
  }

  /** This method is used to convert the input into the equivalent dip value. */
  private int getDip(int input) {
    return (int)
        TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, input, getContext().getResources().getDisplayMetrics());
  }

  /** This method sets the OnItemClickListener for this view. */
  public void setOnItemClickListener(OnItemClickListener listener) {
    this.listener = listener;
  }

  /**
   * This is an abstract OnItemClickListener class which contains an abstract onItemClick method.
   */
  public abstract static class OnItemClickListener {
    public abstract void onItemClick(View view);
  }
}
