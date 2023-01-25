package com.itsvks.layouteditor.xml.editor.language.xml;

/**
 * Tokens for XML
 *
 * @author Vivek
 */
@SuppressWarnings("SpellCheckingInspection")
public enum Tokens {
  WHITESPACE,
  NEWLINE,
  UNKNOWN,
  EOF,

  COMMENT,
  CDATA,
  DOCTYPE,

  CLOSE_TAG,
  OPEN_TAG,
  SLASH,
  EQUALS,
  DOUBLE_QUOTE,
  SINGLE_QUOTE,

  ATTRIBUTE_VALUE,
  ATTRIBUTE_NAME,
  ELEMENT_NAME,
  PROCESSING_INSTRUCTION,
  EXCLAMATION_MARK,
  QUESTION_MARK
}
