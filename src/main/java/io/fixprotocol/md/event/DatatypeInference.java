package io.fixprotocol.md.event;

import java.text.NumberFormat;
import java.text.ParsePosition;
import java.util.function.Predicate;

class DatatypeInference {

  static final String DEFAULT_FALSE = "N";
  static final String DEFAULT_TRUE = "Y";

  protected Predicate<String> isBoolean =
      t -> t.equalsIgnoreCase(getTrueValue()) || t.equalsIgnoreCase(getFalseValue());

  protected Predicate<String> isChar = t -> t.length() == 1 && !Character.isWhitespace(t.charAt(0));

  protected Predicate<String> isNumber = t -> {
    final NumberFormat numberFormat = getNumberFormat();
    final ParsePosition parsePosition = new ParsePosition(0);
    numberFormat.parse(t, parsePosition);
    final int index = parsePosition.getIndex();
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
  public DatatypeInference() {
    this(NumberFormat.getInstance(), DEFAULT_TRUE, DEFAULT_FALSE);
  }

  /**
   * Constructor
   *
   * @param numberFormat formatter based on Locale
   * @param trueValue String value of true Boolean
   * @param falseValue String value of false Boolean
   */
  public DatatypeInference(NumberFormat numberFormat, String trueValue, String falseValue) {
    this.falseValue = falseValue;
    this.numberFormat = numberFormat;
    this.trueValue = trueValue;
  }

  public String getFalseValue() {
    return falseValue;
  }

  public String getTrueValue() {
    return trueValue;
  }

  /**
   * Infers datatype of a String
   *
   * @param str String to parse
   * @return Inferred datatype Class, defaults to {@code String.class}
   */
  public Class<?> inferDatatype(String str) {
    final String s = str.strip();
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

  private NumberFormat getNumberFormat() {
    return numberFormat;
  }
}
