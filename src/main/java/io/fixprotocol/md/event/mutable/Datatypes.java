package io.fixprotocol.md.event.mutable;

import java.text.NumberFormat;
import java.text.ParsePosition;
import java.util.function.Predicate;

class Datatypes {

  static final String DEFAULT_FALSE = "N";
  static final String DEFAULT_TRUE = "Y";

  protected Predicate<String> isBoolean =
      t -> t.equalsIgnoreCase(getTrueValue()) || t.equalsIgnoreCase(getFalseValue());

  protected Predicate<String> isChar = t -> t.length() == 1 && !Character.isWhitespace(t.charAt(0));

  protected Predicate<String> isNumber = t -> {
    final NumberFormat numberFormat = getNumberFormat();
    ParsePosition parsePosition = new ParsePosition(0);
    numberFormat.parse(t, parsePosition);
    int index = parsePosition.getIndex();
    if (index == 0 || index < t.length()) {
      return false;
    } else {
      return true;
    }
  };

  private final String falseValue;
  private final NumberFormat numberFormat;
  private final String trueValue;

  /**
   * Constructor with default formats of current Locale
   */
  public Datatypes() {
    this(NumberFormat.getInstance(), DEFAULT_TRUE, DEFAULT_FALSE);
  }

  /**
   * Constructor
   * @param numberFormat formatter based on Locale
   * @param trueValue String value of true Boolean
   * @param falseValue String value of false Boolean
   */
  public Datatypes(NumberFormat numberFormat, String trueValue, String falseValue) {
    this.falseValue = falseValue;
    this.numberFormat = numberFormat;
    this.trueValue = trueValue;
  }

  /**
   * Infers datatype of a String
   * @param str String to parse
   * @return Inferred datatype Class, defaults to {@code String.class}
   */
  public Class<?> getDatatype(String str) {
    String s = str.strip();
    if (isNumber.test(s)) {
      return Number.class;
    }
    if (isBoolean.test(s)) {
      return Boolean.class;
    }
    if (isChar.test(s)) {
      return Character.class;
    }
    return String.class;
  }

  public String getFalseValue() {
    return falseValue;
  }

  public String getTrueValue() {
    return trueValue;
  }

  private NumberFormat getNumberFormat() {
    return numberFormat;
  }
}
