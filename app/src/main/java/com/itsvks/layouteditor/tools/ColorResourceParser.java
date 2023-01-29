package com.itsvks.layouteditor.tools;

import android.widget.TextView;
import com.itsvks.layouteditor.adapters.models.ColorItem;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

public class ColorResourceParser {

  private List<ColorItem> colorList;

  public ColorResourceParser(InputStream stream) {
    colorList = new ArrayList<>();
    parseXML(stream);
  }

  private void parseXML(InputStream stream) {
    String colorName = "";
    String colorValue = "";
    try {
      XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
      factory.setNamespaceAware(true);
      XmlPullParser xpp = factory.newPullParser();

      xpp.setInput(stream, null);

      int eventType = xpp.getEventType();
      while (eventType != XmlPullParser.END_DOCUMENT) {
        if (eventType == XmlPullParser.START_TAG) {
          String tagName = xpp.getName();
          if (tagName.equalsIgnoreCase("color")) {
            colorName = xpp.getAttributeValue(null, "name");
          }
        } else if (eventType == XmlPullParser.TEXT) {
          colorValue = xpp.getText();
        } else if (eventType == XmlPullParser.END_TAG) {
          String tagName = xpp.getName();
          if (tagName.equalsIgnoreCase("color")) {
            colorList.add(new ColorItem(colorName, colorValue));
          }
        }
        eventType = xpp.next();
      }
      // createTextView(textView);
    } catch (XmlPullParserException | IOException e) {
      e.printStackTrace();
    }
  }

  public void createTextView(TextView textView) {
    StringBuilder builder = new StringBuilder();
    for (ColorItem colorItem : colorList) {
      builder.append(colorItem.colorName).append(" = ").append(colorItem.colorValue).append("\n");
    }
    textView.setText(builder.toString());
  }

  public List<ColorItem> getColorList() {
    return this.colorList;
  }

  public void setColorList(List<ColorItem> colorList) {
    this.colorList = colorList;
  }
}
