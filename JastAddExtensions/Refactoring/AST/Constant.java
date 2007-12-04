
package AST;
import java.util.HashSet;import java.util.LinkedHashSet;import java.io.FileNotFoundException;import java.io.File;import java.util.*;import beaver.*;import java.util.ArrayList;import java.util.zip.*;import java.io.*;import changes.*;import main.FileRange;

  public class Constant extends java.lang.Object {
    // Declared in ConstantExpression.jrag at line 3
    static class ConstantInt extends Constant {
      private int value;
      public ConstantInt(int i) { this.value = i; }
      int intValue() { return value; }
      long longValue() { return value; }
      float floatValue() { return value; }
      double doubleValue() { return value; }
      String stringValue() { return new Integer(value).toString(); }
      Literal buildLiteral() { return new IntegerLiteral(stringValue()); }
    }

    // Declared in ConstantExpression.jrag at line 13
    static class ConstantLong extends Constant {
      private long value;
      public ConstantLong(long l) { this.value = l; }
      int intValue() { return (int)value; }
      long longValue() { return value; }
      float floatValue() { return value; }
      double doubleValue() { return value; }
      String stringValue() { return new Long(value).toString(); }
      Literal buildLiteral() { return new LongLiteral(stringValue()); }
    }

    // Declared in ConstantExpression.jrag at line 23
    static class ConstantFloat extends Constant {
      private float value;
      public ConstantFloat(float f) { this.value = f; }
      int intValue() { return (int)value; }
      long longValue() { return (long)value; }
      float floatValue() { return value; }
      double doubleValue() { return value; }
      String stringValue() { return new Float(value).toString(); }
      Literal buildLiteral() { return new FloatingPointLiteral(stringValue()); }
    }

    // Declared in ConstantExpression.jrag at line 33
    static class ConstantDouble extends Constant {
      private double value;
      public ConstantDouble(double d) { this.value = d; }
      int intValue() { return (int)value; }
      long longValue() { return (long)value; }
      float floatValue() { return (float)value; }
      double doubleValue() { return value; }
      String stringValue() { return new Double(value).toString(); }
      Literal buildLiteral() { return new DoubleLiteral(stringValue()); }
    }

    // Declared in ConstantExpression.jrag at line 43
    static class ConstantChar extends Constant {
      private char value;
      public ConstantChar(char c) { this.value = c; }
      int intValue() { return value; }
      long longValue() { return value; }
      float floatValue() { return value; }
      double doubleValue() { return value; }
      String stringValue() { return new Character(value).toString(); }
      Literal buildLiteral() { return new CharacterLiteral(stringValue()); }
    }

    // Declared in ConstantExpression.jrag at line 53
    static class ConstantBoolean extends Constant {
      private boolean value;
      public ConstantBoolean(boolean b) { this.value = b; }
      boolean booleanValue() { return value; }
      String stringValue() { return new Boolean(value).toString(); }
      Literal buildLiteral() { return new BooleanLiteral(stringValue()); }
    }

    // Declared in ConstantExpression.jrag at line 60
    static class ConstantString extends Constant {
      private String value;
      public ConstantString(String s) { this.value = s; }
      String stringValue() { return value; }
      Literal buildLiteral() { return new StringLiteral(stringValue()); }
    }

    // Declared in ConstantExpression.jrag at line 67

    int intValue() { throw new UnsupportedOperationException(); }

    // Declared in ConstantExpression.jrag at line 68
    long longValue() { throw new UnsupportedOperationException(); }

    // Declared in ConstantExpression.jrag at line 69
    float floatValue() { throw new UnsupportedOperationException(); }

    // Declared in ConstantExpression.jrag at line 70
    double doubleValue() { throw new UnsupportedOperationException(); }

    // Declared in ConstantExpression.jrag at line 71
    boolean booleanValue() { throw new UnsupportedOperationException(getClass().getName()); }

    // Declared in ConstantExpression.jrag at line 72
    String stringValue() { throw new UnsupportedOperationException(); }

    // Declared in ConstantExpression.jrag at line 73
    Literal buildLiteral() { throw new UnsupportedOperationException(); }

    // Declared in ConstantExpression.jrag at line 75
      
    protected Constant() {
    }

    // Declared in ConstantExpression.jrag at line 78
    
    public boolean error = false;

    // Declared in ConstantExpression.jrag at line 80

    static Constant create(int i) { return new ConstantInt(i); }

    // Declared in ConstantExpression.jrag at line 81
    static Constant create(long l) { return new ConstantLong(l); }

    // Declared in ConstantExpression.jrag at line 82
    static Constant create(float f) { return new ConstantFloat(f); }

    // Declared in ConstantExpression.jrag at line 83
    static Constant create(double d) { return new ConstantDouble(d); }

    // Declared in ConstantExpression.jrag at line 84
    static Constant create(boolean b) { return new ConstantBoolean(b); }

    // Declared in ConstantExpression.jrag at line 85
    static Constant create(char c) { return new ConstantChar(c); }

    // Declared in ConstantExpression.jrag at line 86
    static Constant create(String s) { return new ConstantString(s); }


}
