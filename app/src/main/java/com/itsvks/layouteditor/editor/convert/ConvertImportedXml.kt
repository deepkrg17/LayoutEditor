package com.itsvks.layouteditor.editor.convert;

import android.content.Context;
import com.itsvks.layouteditor.utils.FileUtil;
import java.io.File;
import java.io.StringReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.json.JSONObject;

public class ConvertImportedXml {
  private final String TAG = this.getClass().getSimpleName();

  private final String xml;

  public ConvertImportedXml(String xml) {
    this.xml = xml;
  }

  public String getXmlConverted(Context context) {
    String convertedXml = xml;

    if (isWellFormed(xml)) {
      String pattern = "<([a-zA-Z0-9]+\\.)*([a-zA-Z0-9]+)";

      Matcher matcher = Pattern.compile(pattern).matcher(xml);
      try {
        while (matcher.find()) {
          String fullTag = matcher.group(0).replace("<", "");
          String widgetName = matcher.group(2);

          JSONObject classes =
              new JSONObject(FileUtil.readFromAsset("widgetclasses.json", context));

          String widgetClass = classes.getString(widgetName);
          convertedXml = convertedXml.replace("<" + fullTag, "<" + widgetClass);
          convertedXml = convertedXml.replace("</" + fullTag, "</" + widgetClass);
        }
      } catch (Exception e) {
        e.printStackTrace();
        return null;
      }
    } else {
      return null;
    }
    return convertedXml;
  }

  private boolean isWellFormed(String xml) {
    try {
      DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
      DocumentBuilder builder = factory.newDocumentBuilder();
      InputSource source = new InputSource(new StringReader(xml));
      builder.parse(source);
      return true;
    } catch (Exception e) {
      return false;
    }
  }

  public boolean isLayoutFile(String filePath) {
    File file = new File(filePath);
    if (!file.exists() || !file.isFile()) {
      return false;
    }

    String[] layoutTags = {
      "Button",
      "ImageButton",
      "ChipGroup",
      "Chip",
      "CheckBox",
      "RadioGroup",
      "RadioButton",
      "ToggleButton",
      "Switch",
      "FloatingActionButton",
      "Spinner",
      "ScrollView",
      "HorizontalScrollView",
      "NestedScrollView",
      "ViewPager",
      "CardView",
      "AppBarLayout",
      "BottomAppBar",
      "Toolbar",
      "MaterialToolbar",
      "TabItem",
      "RelativeLayout",
      "LinearLayout",
      "FrameLayout",
      "TableLayout",
      "TableRow",
      "Space",
      "GridLayout",
      "TabHost",
      "GridView",
      "TextView",
      "AutoCompleteTextView",
      "MultiAutoCompleteTextView",
      "CheckedTextView",
      "View",
      "ImageView",
      "WebView",
      "VideoView",
      "TextClock",
      "ProgressBar",
      "SeekBar",
      "RatingBar",
      "TextureView",
      "SurfaceView",
      "EditText",
      "DatePicker",
      "TimePicker",
      "Chronometer",
      "ViewFlipper",
      "ListView",
      "GridView",
      "SearchView",
      "RatingBar",
      "NumberPicker",
      "SeekBar",
      "ProgressBar",
      "QuickContactBadge",
      "Switch",
      "TextSwitcher",
      "ImageSwitcher",
      "AdapterViewFlipper",
      "AnalogClock",
      "DigitalClock",
      "ConstraintLayout"
    };

    try {
      DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
      DocumentBuilder builder = factory.newDocumentBuilder();
      Document document = builder.parse(file);
      String rootTag = document.getDocumentElement().getTagName();
      if (rootTag.contains(".")) rootTag = rootTag.substring(rootTag.lastIndexOf(".") + 1);

      for (String layoutTag : layoutTags) {
        if (rootTag.equals(layoutTag)) {
          return true;
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return false;
  }
}
