/*     */ package org.apache.commons.math.complex;
/*     */ 
/*     */ import java.io.Serializable;
/*     */ import java.text.FieldPosition;
/*     */ import java.text.Format;
/*     */ import java.text.NumberFormat;
/*     */ import java.text.ParseException;
/*     */ import java.text.ParsePosition;
/*     */ import java.util.Locale;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class ComplexFormat
/*     */   extends Format
/*     */   implements Serializable
/*     */ {
/*     */   static final long serialVersionUID = -6337346779577272306L;
/*     */   private static final String DEFAULT_IMAGINARY_CHARACTER = "i";
/*     */   private String imaginaryCharacter;
/*     */   private NumberFormat imaginaryFormat;
/*     */   private NumberFormat realFormat;
/*     */   
/*     */   public ComplexFormat() {
/*  57 */     this("i", getDefaultNumberFormat());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ComplexFormat(NumberFormat format) {
/*  66 */     this("i", format);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ComplexFormat(NumberFormat realFormat, NumberFormat imaginaryFormat) {
/*  77 */     this("i", realFormat, imaginaryFormat);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ComplexFormat(String imaginaryCharacter) {
/*  86 */     this(imaginaryCharacter, getDefaultNumberFormat());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ComplexFormat(String imaginaryCharacter, NumberFormat format) {
/*  96 */     this(imaginaryCharacter, format, (NumberFormat)format.clone());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ComplexFormat(String imaginaryCharacter, NumberFormat realFormat, NumberFormat imaginaryFormat) {
/* 110 */     setImaginaryCharacter(imaginaryCharacter);
/* 111 */     setImaginaryFormat(imaginaryFormat);
/* 112 */     setRealFormat(realFormat);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String formatComplex(Complex c) {
/* 123 */     return getInstance().format(c);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public StringBuffer format(Complex complex, StringBuffer toAppendTo, FieldPosition pos) {
/* 138 */     pos.setBeginIndex(0);
/* 139 */     pos.setEndIndex(0);
/*     */ 
/*     */     
/* 142 */     double re = complex.getReal();
/* 143 */     formatDouble(re, getRealFormat(), toAppendTo, pos);
/*     */ 
/*     */     
/* 146 */     double im = complex.getImaginary();
/* 147 */     if (im < 0.0D) {
/* 148 */       toAppendTo.append(" - ");
/* 149 */       formatDouble(-im, getImaginaryFormat(), toAppendTo, pos);
/* 150 */       toAppendTo.append(getImaginaryCharacter());
/* 151 */     } else if (im > 0.0D || Double.isNaN(im)) {
/* 152 */       toAppendTo.append(" + ");
/* 153 */       formatDouble(im, getImaginaryFormat(), toAppendTo, pos);
/* 154 */       toAppendTo.append(getImaginaryCharacter());
/*     */     } 
/*     */     
/* 157 */     return toAppendTo;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public StringBuffer format(Object obj, StringBuffer toAppendTo, FieldPosition pos) {
/* 176 */     StringBuffer ret = null;
/*     */     
/* 178 */     if (obj instanceof Complex) {
/* 179 */       ret = format((Complex)obj, toAppendTo, pos);
/* 180 */     } else if (obj instanceof Number) {
/* 181 */       ret = format(new Complex(((Number)obj).doubleValue(), 0.0D), toAppendTo, pos);
/*     */     } else {
/*     */       
/* 184 */       throw new IllegalArgumentException("Cannot format given Object as a Date");
/*     */     } 
/*     */ 
/*     */     
/* 188 */     return ret;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private StringBuffer formatDouble(double value, NumberFormat format, StringBuffer toAppendTo, FieldPosition pos) {
/* 210 */     if (Double.isNaN(value) || Double.isInfinite(value)) {
/* 211 */       toAppendTo.append('(');
/* 212 */       toAppendTo.append(value);
/* 213 */       toAppendTo.append(')');
/*     */     } else {
/* 215 */       getRealFormat().format(value, toAppendTo, pos);
/*     */     } 
/* 217 */     return toAppendTo;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Locale[] getAvailableLocales() {
/* 226 */     return NumberFormat.getAvailableLocales();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static NumberFormat getDefaultNumberFormat() {
/* 236 */     return getDefaultNumberFormat(Locale.getDefault());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static NumberFormat getDefaultNumberFormat(Locale locale) {
/* 247 */     NumberFormat nf = NumberFormat.getInstance(locale);
/* 248 */     nf.setMaximumFractionDigits(2);
/* 249 */     return nf;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getImaginaryCharacter() {
/* 257 */     return this.imaginaryCharacter;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public NumberFormat getImaginaryFormat() {
/* 265 */     return this.imaginaryFormat;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static ComplexFormat getInstance() {
/* 273 */     return getInstance(Locale.getDefault());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static ComplexFormat getInstance(Locale locale) {
/* 282 */     NumberFormat f = getDefaultNumberFormat(locale);
/* 283 */     return new ComplexFormat(f);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public NumberFormat getRealFormat() {
/* 291 */     return this.realFormat;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Complex parse(String source) throws ParseException {
/* 303 */     ParsePosition parsePosition = new ParsePosition(0);
/* 304 */     Complex result = parse(source, parsePosition);
/* 305 */     if (parsePosition.getIndex() == 0) {
/* 306 */       throw new ParseException("Unparseable complex number: \"" + source + "\"", parsePosition.getErrorIndex());
/*     */     }
/*     */     
/* 309 */     return result;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Complex parse(String source, ParsePosition pos) {
/* 320 */     int initialIndex = pos.getIndex();
/*     */ 
/*     */     
/* 323 */     parseAndIgnoreWhitespace(source, pos);
/*     */ 
/*     */     
/* 326 */     Number re = parseNumber(source, getRealFormat(), pos);
/* 327 */     if (re == null) {
/*     */ 
/*     */ 
/*     */       
/* 331 */       pos.setIndex(initialIndex);
/* 332 */       return null;
/*     */     } 
/*     */ 
/*     */     
/* 336 */     int startIndex = pos.getIndex();
/* 337 */     char c = parseNextCharacter(source, pos);
/* 338 */     int sign = 0;
/* 339 */     switch (c) {
/*     */ 
/*     */       
/*     */       case '\000':
/* 343 */         return new Complex(re.doubleValue(), 0.0D);
/*     */       case '-':
/* 345 */         sign = -1;
/*     */         break;
/*     */       case '+':
/* 348 */         sign = 1;
/*     */         break;
/*     */ 
/*     */ 
/*     */       
/*     */       default:
/* 354 */         pos.setIndex(initialIndex);
/* 355 */         pos.setErrorIndex(startIndex);
/* 356 */         return null;
/*     */     } 
/*     */ 
/*     */     
/* 360 */     parseAndIgnoreWhitespace(source, pos);
/*     */ 
/*     */     
/* 363 */     Number im = parseNumber(source, getRealFormat(), pos);
/* 364 */     if (im == null) {
/*     */ 
/*     */ 
/*     */       
/* 368 */       pos.setIndex(initialIndex);
/* 369 */       return null;
/*     */     } 
/*     */ 
/*     */     
/* 373 */     int n = getImaginaryCharacter().length();
/* 374 */     startIndex = pos.getIndex();
/* 375 */     int endIndex = startIndex + n;
/* 376 */     if (source.substring(startIndex, endIndex).compareTo(getImaginaryCharacter()) != 0) {
/*     */ 
/*     */ 
/*     */       
/* 380 */       pos.setIndex(initialIndex);
/* 381 */       pos.setErrorIndex(startIndex);
/* 382 */       return null;
/*     */     } 
/* 384 */     pos.setIndex(endIndex);
/*     */     
/* 386 */     return new Complex(re.doubleValue(), im.doubleValue() * sign);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void parseAndIgnoreWhitespace(String source, ParsePosition pos) {
/* 397 */     parseNextCharacter(source, pos);
/* 398 */     pos.setIndex(pos.getIndex() - 1);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private char parseNextCharacter(String source, ParsePosition pos) {
/* 409 */     int index = pos.getIndex();
/* 410 */     int n = source.length();
/* 411 */     char ret = Character.MIN_VALUE;
/*     */     
/* 413 */     if (index < n) {
/*     */       char c;
/*     */       do {
/* 416 */         c = source.charAt(index++);
/* 417 */       } while (Character.isWhitespace(c) && index < n);
/* 418 */       pos.setIndex(index);
/*     */       
/* 420 */       if (index < n) {
/* 421 */         ret = c;
/*     */       }
/*     */     } 
/*     */     
/* 425 */     return ret;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private Number parseNumber(String source, double value, ParsePosition pos) {
/* 438 */     Number ret = null;
/*     */     
/* 440 */     StringBuffer sb = new StringBuffer();
/* 441 */     sb.append('(');
/* 442 */     sb.append(value);
/* 443 */     sb.append(')');
/*     */     
/* 445 */     int n = sb.length();
/* 446 */     int startIndex = pos.getIndex();
/* 447 */     int endIndex = startIndex + n;
/* 448 */     if (endIndex < source.length() && 
/* 449 */       source.substring(startIndex, endIndex).compareTo(sb.toString()) == 0) {
/* 450 */       ret = new Double(value);
/* 451 */       pos.setIndex(endIndex);
/*     */     } 
/*     */ 
/*     */     
/* 455 */     return ret;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private Number parseNumber(String source, NumberFormat format, ParsePosition pos) {
/* 469 */     int startIndex = pos.getIndex();
/* 470 */     Number number = getRealFormat().parse(source, pos);
/* 471 */     int endIndex = pos.getIndex();
/*     */ 
/*     */     
/* 474 */     if (startIndex == endIndex) {
/*     */       
/* 476 */       double[] special = { Double.NaN, Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY };
/* 477 */       for (int i = 0; i < special.length; i++) {
/* 478 */         number = parseNumber(source, special[i], pos);
/* 479 */         if (number != null) {
/*     */           break;
/*     */         }
/*     */       } 
/*     */     } 
/*     */     
/* 485 */     return number;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Object parseObject(String source, ParsePosition pos) {
/* 497 */     return parse(source, pos);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setImaginaryCharacter(String imaginaryCharacter) {
/* 506 */     if (imaginaryCharacter == null || imaginaryCharacter.length() == 0) {
/* 507 */       throw new IllegalArgumentException("imaginaryCharacter must be a non-empty string.");
/*     */     }
/*     */     
/* 510 */     this.imaginaryCharacter = imaginaryCharacter;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setImaginaryFormat(NumberFormat imaginaryFormat) {
/* 520 */     if (imaginaryFormat == null) {
/* 521 */       throw new IllegalArgumentException("imaginaryFormat can not be null.");
/*     */     }
/*     */     
/* 524 */     this.imaginaryFormat = imaginaryFormat;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setRealFormat(NumberFormat realFormat) {
/* 534 */     if (realFormat == null) {
/* 535 */       throw new IllegalArgumentException("realFormat can not be null.");
/*     */     }
/*     */     
/* 538 */     this.realFormat = realFormat;
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\commons-math-1.0.jar!\org\apache\commons\math\complex\ComplexFormat.class
 * Java compiler version: 1 (45.3)
 * JD-Core Version:       1.1.3
 */