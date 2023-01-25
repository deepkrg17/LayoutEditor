package com.itsvks.layouteditor.activities;

import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.content.res.ResourcesCompat;
import com.blankj.utilcode.util.ClipboardUtils;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.itsvks.layouteditor.BaseActivity;
import com.itsvks.layouteditor.R;
import com.itsvks.layouteditor.databinding.ActivityShowXMLBinding;
import com.itsvks.layouteditor.managers.PreferencesManager;
import com.itsvks.layouteditor.utils.SBUtils;
import io.github.rosemoe.sora.langs.textmate.TextMateColorScheme;
import io.github.rosemoe.sora.langs.textmate.TextMateLanguage;
import io.github.rosemoe.sora.langs.textmate.registry.FileProviderRegistry;
import io.github.rosemoe.sora.langs.textmate.registry.GrammarRegistry;
import io.github.rosemoe.sora.langs.textmate.registry.ThemeRegistry;
import io.github.rosemoe.sora.langs.textmate.registry.model.ThemeModel;
import io.github.rosemoe.sora.langs.textmate.registry.provider.AssetsFileResolver;
import io.github.rosemoe.sora.widget.SymbolInputView;
import org.eclipse.tm4e.core.registry.IThemeSource;

public class ShowXMLActivity extends BaseActivity {

  public static final String EXTRA_KEY_XML = "xml";
  private int lastUiMode = Configuration.UI_MODE_NIGHT_NO; // default to light theme
  private SharedPreferences prefs;

  private ActivityShowXMLBinding binding;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    binding = ActivityShowXMLBinding.inflate(getLayoutInflater());
    prefs = PreferencesManager.getPrefs();

    setContentView(binding.getRoot());
    setSupportActionBar(binding.topAppBar);
    getSupportActionBar().setTitle(R.string.xml_preview);
    binding.symbolInput.bindEditor(binding.editor);
    setupSymbols(binding.symbolInput);
    // switchThemeIfRequired(this, binding.editor);

    binding.topAppBar.setNavigationOnClickListener(
        v -> {
          super.onBackPressed();
        });

    binding.editor.setText(getIntent().getStringExtra(EXTRA_KEY_XML));
    binding.editor.setTypefaceText(jetBrainsMono());
    binding.editor.setTypefaceLineNumber(jetBrainsMono());
    binding.editor.setEditable(false);
    // binding.editor.setColorScheme(new SchemeLight());
    try {
      loadDefaultThemes();
      ThemeRegistry.getInstance().setTheme(prefs.getString("scheme", "darcula"));
      loadDefaultLanguages();

      ensureTextmateTheme();

      var editor = binding.editor;
      var language = TextMateLanguage.create("text.xml", true);
      editor.setEditorLanguage(language);
    } catch (Exception e) {
      e.printStackTrace();
    }

    binding.result.setTextIsSelectable(true);    
    binding.fab.setOnClickListener(
        v -> {
          ClipboardUtils.copyText(binding.editor.getText().toString());
          SBUtils.make(binding.getRoot(), getString(R.string.copied))
              .setAnchorView(binding.fab)
              .setSlideAnimation()
              .show();
        });

    binding.editor.setOnScrollChangeListener(
        (v, x, y, oldX, oldY) -> {
          if (y > oldY + 20 && binding.fab.isExtended()) {
            binding.fab.shrink();
          }
          if (y < oldY - 20 && !binding.fab.isExtended()) {
            binding.fab.extend();
          }
          if (y == 0) {
            binding.fab.extend();
          }
        });
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    binding = null;
  }

  private void loadDefaultThemes() throws Exception {
    // add assets file provider
    FileProviderRegistry.getInstance()
        .addFileProvider(new AssetsFileResolver(getApplicationContext().getAssets()));

    String[] themes = new String[] {"darcula", "abyss", "quietlight", "solarized_drak"};
    ThemeRegistry themeRegistry = ThemeRegistry.getInstance();
    for (String name : themes) {
      String path = "editor/textmate/" + name + ".json";
      themeRegistry.loadTheme(
          new ThemeModel(
              IThemeSource.fromInputStream(
                  FileProviderRegistry.getInstance().tryGetInputStream(path), path, null),
              name));
    }

    themeRegistry.setTheme("darcula");
  }

  private void loadDefaultLanguages() {
    GrammarRegistry.getInstance().loadGrammars("editor/textmate/languages.json");
  }

  private void ensureTextmateTheme() throws Exception {
    var editor = binding.editor;
    var editorColorScheme = editor.getColorScheme();
    if (!(editorColorScheme instanceof TextMateColorScheme)) {
      editorColorScheme = TextMateColorScheme.create(ThemeRegistry.getInstance());
      editor.setColorScheme(editorColorScheme);
    }
  }

  private void setupSymbols(SymbolInputView symbolInput) {
    symbolInput.addSymbols(
        new String[] {
          "➜", "<", ">", "/", "=", "\"", ":", "@", "+", "(", ")", ";", ",", ".", "?", "|", "\\",
          "&", "[", "]", "{", "}", "_", "-"
        },
        new String[] {
          "\t", "<>", ">", "/", "=", "\"\"", ":", "@", "+", "()", ")", ";", ",", ".", "?", "|",
          "\\", "&", "[]", "]", "{}", "}", "_", "-"
        });
    symbolInput.forEachButton(
        (b) -> {
          b.setTypeface(jetBrainsMono());
        });
  }

  private Typeface jetBrainsMono() {
    return ResourcesCompat.getFont(this, R.font.jetbrains_mono_regular);
  }

  @Override
  public void onConfigurationChanged(Configuration newConfig) {
    super.onConfigurationChanged(newConfig);
    if (newConfig.keyboardHidden == Configuration.KEYBOARDHIDDEN_NO) {
      // Keyboard is now visible
      binding.fab.hide();
    } else if (newConfig.keyboardHidden == Configuration.KEYBOARDHIDDEN_YES) {
      // Keyboard is now hidden
      binding.fab.show();
    }
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    var editorThemeMenu = menu.add(getString(R.string.color_scheme));
    editorThemeMenu.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
    editorThemeMenu.setIcon(AppCompatResources.getDrawable(this, R.drawable.palette));
    editorThemeMenu.setContentDescription(getString(R.string.color_scheme));
    return super.onCreateOptionsMenu(menu);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    if (item.getTitle().equals(getString(R.string.color_scheme))) {
      new MaterialAlertDialogBuilder(this)
          .setTitle(getString(R.string.select) + " " + getString(R.string.color_scheme))
          .setAdapter(
              new ArrayAdapter<String>(
                  this,
                  android.R.layout.simple_list_item_1,
                  new String[] {"Abyss", "Darcula", "QuietLight", "Solarized Drak"}),
              (d, w) -> {
                try {
                  ensureTextmateTheme();
                  switch (w) {
                    case 0:
                      ThemeRegistry.getInstance().setTheme("abyss");
                      break;
                    case 1:
                      ThemeRegistry.getInstance().setTheme("darcula");
                      break;
                    case 2:
                      ThemeRegistry.getInstance().setTheme("quietlight");
                      break;
                    case 3:
                      ThemeRegistry.getInstance().setTheme("solarized_drak");
                      break;
                  }
                } catch (Exception e) {
                  e.printStackTrace();
                }
              })
          .setNegativeButton(getString(R.string.cancel), (d, w) -> d.dismiss())
          .show();
      return true;
    }
    return false;
  }
}
