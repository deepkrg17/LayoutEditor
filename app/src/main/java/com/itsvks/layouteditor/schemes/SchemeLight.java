package com.itsvks.layouteditor.schemes;

import io.github.rosemoe.sora.widget.schemes.EditorColorScheme;

public class SchemeLight extends EditorColorScheme {
  @Override
  public void applyDefault() {
    super.applyDefault();
    setColor(ANNOTATION, 0xffbbb529);
    setColor(FUNCTION_NAME, 0xff111111);
    setColor(IDENTIFIER_NAME, 0xff111111);
    setColor(IDENTIFIER_VAR, 0xff464646);
    setColor(LITERAL, 0xff6a8759);
    setColor(OPERATOR, 0xff111111);
    setColor(COMMENT, 0xff808080);
    setColor(KEYWORD, 0xffcc7832);
    setColor(WHOLE_BACKGROUND, 0xffffffff);
    setColor(COMPLETION_WND_BACKGROUND, 0xffffffff);
    setColor(COMPLETION_WND_CORNER, 0xff999999);
    setColor(TEXT_NORMAL, 0xff111111);
    setColor(LINE_NUMBER_BACKGROUND, 0xffefefef);
    setColor(LINE_NUMBER, 0xff999999);
    setColor(LINE_NUMBER_CURRENT, 0xff999999);
    setColor(LINE_DIVIDER, 0xff999999);
    setColor(SCROLL_BAR_THUMB, 0xffa6a6a6);
    setColor(SCROLL_BAR_THUMB_PRESSED, 0xff565656);
    setColor(SELECTED_TEXT_BACKGROUND, 0xff3676b8);
    setColor(MATCHED_TEXT_BACKGROUND, 0xff32593d);
    setColor(CURRENT_LINE, 0xffffffcc);
    setColor(SELECTION_INSERT, 0xff111111);
    setColor(SELECTION_HANDLE, 0xff111111);
    setColor(BLOCK_LINE, 0xffcccccc);
    setColor(BLOCK_LINE_CURRENT, 0xddcccccc);
    setColor(NON_PRINTABLE_CHAR, 0xffdddddd);
    setColor(TEXT_SELECTED, 0xff111111);
    setColor(HIGHLIGHTED_DELIMITERS_FOREGROUND, 0xff111111);
  }
}
