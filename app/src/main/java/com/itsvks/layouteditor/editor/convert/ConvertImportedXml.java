package com.itsvks.layouteditor.editor.convert;

import android.content.Context;
import com.itsvks.layouteditor.utils.FileUtil;
import java.io.StringReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.xml.sax.InputSource;
import org.json.JSONObject;

public class ConvertImportedXml {
  private String xml;

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
          if (widgetClass != null) {

            convertedXml = convertedXml.replace("<" + fullTag, "<" + widgetClass);
            convertedXml = convertedXml.replace("</" + fullTag, "</" + widgetClass);
          } else {
            convertedXml = convertedXml.replace(fullTag, "");
          }
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
}
