package weka.gui.visualize;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Paint;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.Toolkit;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ColorModel;
import java.awt.image.ImageObserver;
import java.awt.image.PixelGrabber;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.RenderableImage;
import java.io.OutputStream;
import java.io.PrintStream;
import java.text.AttributedCharacterIterator;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.Map;

public class PostscriptGraphics extends Graphics2D {
  protected Rectangle m_extent;
  
  protected PrintStream m_printstream;
  
  protected GraphicsState m_psGraphicsState;
  
  protected GraphicsState m_localGraphicsState;
  
  protected static final boolean DEBUG = false;
  
  protected static Hashtable m_PSFontReplacement = new Hashtable();
  
  public PostscriptGraphics(int paramInt1, int paramInt2, OutputStream paramOutputStream) {
    this.m_extent = new Rectangle(0, 0, paramInt2, paramInt1);
    this.m_printstream = new PrintStream(paramOutputStream);
    this.m_localGraphicsState = new GraphicsState(this);
    this.m_psGraphicsState = new GraphicsState(this);
    Header();
  }
  
  PostscriptGraphics(PostscriptGraphics paramPostscriptGraphics) {
    this.m_extent = new Rectangle(paramPostscriptGraphics.m_extent);
    this.m_printstream = paramPostscriptGraphics.m_printstream;
    this.m_localGraphicsState = new GraphicsState(this, paramPostscriptGraphics.m_localGraphicsState);
    this.m_psGraphicsState = paramPostscriptGraphics.m_psGraphicsState;
  }
  
  public void finished() {
    this.m_printstream.flush();
  }
  
  private void Header() {
    this.m_printstream.println("%!PS-Adobe-3.0 EPSF-3.0");
    this.m_printstream.println("%%BoundingBox: 0 0 " + xScale(this.m_extent.width) + " " + yScale(this.m_extent.height));
    this.m_printstream.println("%%CreationDate: " + Calendar.getInstance().getTime());
    this.m_printstream.println("/Oval { % x y w h filled");
    this.m_printstream.println("gsave");
    this.m_printstream.println("/filled exch def /h exch def /w exch def /y exch def /x exch def");
    this.m_printstream.println("x w 2 div add y h 2 div sub translate");
    this.m_printstream.println("1 h w div scale");
    this.m_printstream.println("filled {0 0 moveto} if");
    this.m_printstream.println("0 0 w 2 div 0 360 arc");
    this.m_printstream.println("filled {closepath fill} {stroke} ifelse grestore} bind def");
    this.m_printstream.println("/Rect { % x y w h filled");
    this.m_printstream.println("/filled exch def /h exch def /w exch def /y exch def /x exch def");
    this.m_printstream.println("newpath ");
    this.m_printstream.println("x y moveto");
    this.m_printstream.println("w 0 rlineto");
    this.m_printstream.println("0 h neg rlineto");
    this.m_printstream.println("w neg 0 rlineto");
    this.m_printstream.println("closepath");
    this.m_printstream.println("filled {fill} {stroke} ifelse} bind def");
    this.m_printstream.println("%%BeginProlog\n%%EndProlog");
    this.m_printstream.println("%%Page 1 1");
    setFont(null);
    setColor(null);
    setStroke(null);
  }
  
  public static void addPSFontReplacement(String paramString1, String paramString2) {
    m_PSFontReplacement.put(paramString1, paramString2);
  }
  
  private int yTransform(int paramInt) {
    return this.m_extent.height - this.m_localGraphicsState.getYOffset() + paramInt;
  }
  
  private int xTransform(int paramInt) {
    return this.m_localGraphicsState.getXOffset() + paramInt;
  }
  
  private int doScale(int paramInt, double paramDouble) {
    return (int)StrictMath.round(paramInt * paramDouble);
  }
  
  private int xScale(int paramInt) {
    return doScale(paramInt, this.m_localGraphicsState.getXScale());
  }
  
  private int yScale(int paramInt) {
    return doScale(paramInt, this.m_localGraphicsState.getYScale());
  }
  
  private void setStateToLocal() {
    setColor(getColor());
    setFont(getFont());
    setStroke(getStroke());
  }
  
  private String toHex(int paramInt) {
    String str = Integer.toHexString(paramInt);
    if (str.length() < 2)
      str = "0" + str; 
    return str;
  }
  
  public void clearRect(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    setStateToLocal();
    Color color = getColor();
    setColor(Color.white);
    this.m_printstream.println(xTransform(xScale(paramInt1)) + " " + yTransform(yScale(paramInt2)) + " " + xScale(paramInt3) + " " + yScale(paramInt4) + " true Rect");
    setColor(color);
  }
  
  public void clipRect(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {}
  
  public void copyArea(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6) {}
  
  public Graphics create() {
    return new PostscriptGraphics(this);
  }
  
  public void dispose() {}
  
  public void draw3DRect(int paramInt1, int paramInt2, int paramInt3, int paramInt4, boolean paramBoolean) {
    drawRect(paramInt1, paramInt2, paramInt3, paramInt4);
  }
  
  public void drawArc(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6) {}
  
  public void drawBytes(byte[] paramArrayOfbyte, int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    drawString(new String(paramArrayOfbyte, paramInt1, paramInt2), paramInt3, paramInt4);
  }
  
  public void drawChars(char[] paramArrayOfchar, int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    drawString(new String(paramArrayOfchar, paramInt1, paramInt2), paramInt3, paramInt4);
  }
  
  public boolean drawImage(Image paramImage, int paramInt1, int paramInt2, Color paramColor, ImageObserver paramImageObserver) {
    return drawImage(paramImage, paramInt1, paramInt2, paramImage.getWidth(paramImageObserver), paramImage.getHeight(paramImageObserver), paramColor, paramImageObserver);
  }
  
  public boolean drawImage(Image paramImage, int paramInt1, int paramInt2, ImageObserver paramImageObserver) {
    return drawImage(paramImage, paramInt1, paramInt2, Color.WHITE, paramImageObserver);
  }
  
  public boolean drawImage(Image paramImage, int paramInt1, int paramInt2, int paramInt3, int paramInt4, Color paramColor, ImageObserver paramImageObserver) {
    try {
      int[] arrayOfInt = new int[paramInt3 * paramInt4];
      PixelGrabber pixelGrabber = new PixelGrabber(paramImage, 0, 0, paramInt3, paramInt4, arrayOfInt, 0, paramInt3);
      pixelGrabber.grabPixels();
      ColorModel colorModel = ColorModel.getRGBdefault();
      this.m_printstream.println("gsave");
      this.m_printstream.println(xTransform(xScale(paramInt1)) + " " + (yTransform(yScale(paramInt2)) - yScale(paramInt4)) + " translate");
      this.m_printstream.println(xScale(paramInt3) + " " + yScale(paramInt4) + " scale");
      this.m_printstream.println(paramInt3 + " " + paramInt4 + " " + "8" + " [" + paramInt3 + " 0 0 " + -paramInt4 + " 0 " + paramInt4 + "]");
      this.m_printstream.println("{<");
      for (byte b = 0; b < paramInt4; b++) {
        for (byte b1 = 0; b1 < paramInt3; b1++) {
          int i = b * paramInt3 + b1;
          this.m_printstream.print(toHex(colorModel.getRed(arrayOfInt[i])));
          this.m_printstream.print(toHex(colorModel.getGreen(arrayOfInt[i])));
          this.m_printstream.print(toHex(colorModel.getBlue(arrayOfInt[i])));
        } 
        this.m_printstream.println();
      } 
      this.m_printstream.println(">}");
      this.m_printstream.println("false 3 colorimage");
      this.m_printstream.println("grestore");
      return true;
    } catch (Exception exception) {
      exception.printStackTrace();
      return false;
    } 
  }
  
  public boolean drawImage(Image paramImage, int paramInt1, int paramInt2, int paramInt3, int paramInt4, ImageObserver paramImageObserver) {
    return drawImage(paramImage, paramInt1, paramInt2, paramInt3, paramInt4, Color.WHITE, paramImageObserver);
  }
  
  public boolean drawImage(Image paramImage, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7, int paramInt8, Color paramColor, ImageObserver paramImageObserver) {
    return false;
  }
  
  public boolean drawImage(Image paramImage, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7, int paramInt8, ImageObserver paramImageObserver) {
    return drawImage(paramImage, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6, paramInt7, paramInt8, Color.WHITE, paramImageObserver);
  }
  
  public void drawLine(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    setStateToLocal();
    this.m_printstream.println(xTransform(xScale(paramInt1)) + " " + yTransform(yScale(paramInt2)) + " moveto " + xTransform(xScale(paramInt3)) + " " + yTransform(yScale(paramInt4)) + " lineto stroke");
  }
  
  public void drawOval(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    setStateToLocal();
    this.m_printstream.println(xTransform(xScale(paramInt1)) + " " + yTransform(yScale(paramInt2)) + " " + xScale(paramInt3) + " " + yScale(paramInt4) + " false Oval");
  }
  
  public void drawPolygon(int[] paramArrayOfint1, int[] paramArrayOfint2, int paramInt) {}
  
  public void drawPolyline(int[] paramArrayOfint1, int[] paramArrayOfint2, int paramInt) {}
  
  public void drawRect(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    setStateToLocal();
    this.m_printstream.println(xTransform(xScale(paramInt1)) + " " + yTransform(yScale(paramInt2)) + " " + xScale(paramInt3) + " " + yScale(paramInt4) + " false Rect");
  }
  
  public void drawRoundRect(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6) {}
  
  public void drawString(AttributedCharacterIterator paramAttributedCharacterIterator, int paramInt1, int paramInt2) {}
  
  public void drawString(String paramString, int paramInt1, int paramInt2) {
    setStateToLocal();
    this.m_printstream.println(xTransform(xScale(paramInt1)) + " " + yTransform(yScale(paramInt2)) + " moveto" + " (" + paramString + ") show stroke");
  }
  
  public void fill3DRect(int paramInt1, int paramInt2, int paramInt3, int paramInt4, boolean paramBoolean) {
    fillRect(paramInt1, paramInt2, paramInt3, paramInt4);
  }
  
  public void fillArc(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6) {}
  
  public void fillOval(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    setStateToLocal();
    this.m_printstream.println(xTransform(xScale(paramInt1)) + " " + yTransform(yScale(paramInt2)) + " " + xScale(paramInt3) + " " + yScale(paramInt4) + " true Oval");
  }
  
  public void fillPolygon(int[] paramArrayOfint1, int[] paramArrayOfint2, int paramInt) {}
  
  public void fillPolygon(Polygon paramPolygon) {}
  
  public void fillRect(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    if (paramInt3 == this.m_extent.width && paramInt4 == this.m_extent.height) {
      clearRect(paramInt1, paramInt2, paramInt3, paramInt4);
    } else {
      setStateToLocal();
      this.m_printstream.println(xTransform(xScale(paramInt1)) + " " + yTransform(yScale(paramInt2)) + " " + xScale(paramInt3) + " " + yScale(paramInt4) + " true Rect");
    } 
  }
  
  public void fillRoundRect(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6) {}
  
  public void finalize() {}
  
  public Shape getClip() {
    return null;
  }
  
  public Rectangle getClipBounds() {
    return new Rectangle(0, 0, this.m_extent.width, this.m_extent.height);
  }
  
  public Rectangle getClipBounds(Rectangle paramRectangle) {
    paramRectangle.setBounds(0, 0, this.m_extent.width, this.m_extent.height);
    return paramRectangle;
  }
  
  public Rectangle getClipRect() {
    return null;
  }
  
  public Color getColor() {
    return this.m_localGraphicsState.getColor();
  }
  
  public Font getFont() {
    return this.m_localGraphicsState.getFont();
  }
  
  public FontMetrics getFontMetrics(Font paramFont) {
    return Toolkit.getDefaultToolkit().getFontMetrics(paramFont);
  }
  
  public void setClip(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {}
  
  public void setClip(Shape paramShape) {}
  
  public void setColor(Color paramColor) {
    if (paramColor != null) {
      this.m_localGraphicsState.setColor(paramColor);
      if (this.m_psGraphicsState.getColor().equals(paramColor))
        return; 
      this.m_psGraphicsState.setColor(paramColor);
    } else {
      this.m_localGraphicsState.setColor(Color.black);
      this.m_psGraphicsState.setColor(getColor());
    } 
    this.m_printstream.print(getColor().getRed() / 255.0D);
    this.m_printstream.print(" ");
    this.m_printstream.print(getColor().getGreen() / 255.0D);
    this.m_printstream.print(" ");
    this.m_printstream.print(getColor().getBlue() / 255.0D);
    this.m_printstream.println(" setrgbcolor");
  }
  
  private static String replacePSFont(String paramString) {
    String str = paramString;
    if (m_PSFontReplacement.containsKey(paramString))
      str = m_PSFontReplacement.get(paramString).toString(); 
    return str;
  }
  
  public void setFont(Font paramFont) {
    if (paramFont != null) {
      this.m_localGraphicsState.setFont(paramFont);
      if (paramFont.getName().equals(this.m_psGraphicsState.getFont().getName()) && this.m_psGraphicsState.getFont().getStyle() == paramFont.getStyle() && this.m_psGraphicsState.getFont().getSize() == yScale(paramFont.getSize()))
        return; 
      this.m_psGraphicsState.setFont(new Font(paramFont.getName(), paramFont.getStyle(), yScale(getFont().getSize())));
    } else {
      this.m_localGraphicsState.setFont(new Font("Courier", 0, 11));
      this.m_psGraphicsState.setFont(getFont());
    } 
    this.m_printstream.println("/(" + replacePSFont(getFont().getPSName()) + ")" + " findfont");
    this.m_printstream.println(yScale(getFont().getSize()) + " scalefont setfont");
  }
  
  public void setPaintMode() {}
  
  public void setXORMode(Color paramColor) {}
  
  public void translate(int paramInt1, int paramInt2) {
    this.m_localGraphicsState.setXOffset(this.m_localGraphicsState.getXOffset() + xScale(paramInt1));
    this.m_localGraphicsState.setYOffset(this.m_localGraphicsState.getYOffset() + yScale(paramInt2));
    this.m_psGraphicsState.setXOffset(this.m_psGraphicsState.getXOffset() + xScale(paramInt1));
    this.m_psGraphicsState.setYOffset(this.m_psGraphicsState.getYOffset() + yScale(paramInt2));
  }
  
  public FontRenderContext getFontRenderContext() {
    return new FontRenderContext(null, true, true);
  }
  
  public void clip(Shape paramShape) {}
  
  public Stroke getStroke() {
    return this.m_localGraphicsState.getStroke();
  }
  
  public Color getBackground() {
    return Color.white;
  }
  
  public void setBackground(Color paramColor) {}
  
  public Composite getComposite() {
    return AlphaComposite.getInstance(2);
  }
  
  public Paint getPaint() {
    return new Color(getColor().getRed(), getColor().getGreen(), getColor().getBlue());
  }
  
  public AffineTransform getTransform() {
    return new AffineTransform();
  }
  
  public void setTransform(AffineTransform paramAffineTransform) {}
  
  public void transform(AffineTransform paramAffineTransform) {}
  
  public void shear(double paramDouble1, double paramDouble2) {}
  
  public void scale(double paramDouble1, double paramDouble2) {
    this.m_localGraphicsState.setXScale(paramDouble1);
    this.m_localGraphicsState.setYScale(paramDouble2);
  }
  
  public void rotate(double paramDouble1, double paramDouble2, double paramDouble3) {}
  
  public void rotate(double paramDouble) {}
  
  public void translate(double paramDouble1, double paramDouble2) {}
  
  public RenderingHints getRenderingHints() {
    return new RenderingHints(null);
  }
  
  public void addRenderingHints(Map paramMap) {}
  
  public void setRenderingHints(Map paramMap) {}
  
  public Object getRenderingHint(RenderingHints.Key paramKey) {
    return null;
  }
  
  public void setRenderingHint(RenderingHints.Key paramKey, Object paramObject) {}
  
  public void setStroke(Stroke paramStroke) {
    if (paramStroke != null) {
      this.m_localGraphicsState.setStroke(paramStroke);
      if (paramStroke.equals(this.m_psGraphicsState.getStroke()))
        return; 
      this.m_psGraphicsState.setStroke(paramStroke);
    } else {
      this.m_localGraphicsState.setStroke(new BasicStroke());
      this.m_psGraphicsState.setStroke(getStroke());
    } 
  }
  
  public void setPaint(Paint paramPaint) {}
  
  public void setComposite(Composite paramComposite) {}
  
  public GraphicsConfiguration getDeviceConfiguration() {
    GraphicsEnvironment graphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment();
    GraphicsDevice graphicsDevice = graphicsEnvironment.getDefaultScreenDevice();
    return graphicsDevice.getDefaultConfiguration();
  }
  
  public boolean hit(Rectangle paramRectangle, Shape paramShape, boolean paramBoolean) {
    return false;
  }
  
  public void fill(Shape paramShape) {}
  
  public void drawGlyphVector(GlyphVector paramGlyphVector, float paramFloat1, float paramFloat2) {}
  
  public void drawString(AttributedCharacterIterator paramAttributedCharacterIterator, float paramFloat1, float paramFloat2) {}
  
  public void drawString(String paramString, float paramFloat1, float paramFloat2) {
    drawString(paramString, (int)paramFloat1, (int)paramFloat2);
  }
  
  public void drawRenderableImage(RenderableImage paramRenderableImage, AffineTransform paramAffineTransform) {}
  
  public void drawRenderedImage(RenderedImage paramRenderedImage, AffineTransform paramAffineTransform) {}
  
  public void drawImage(BufferedImage paramBufferedImage, BufferedImageOp paramBufferedImageOp, int paramInt1, int paramInt2) {}
  
  public boolean drawImage(Image paramImage, AffineTransform paramAffineTransform, ImageObserver paramImageObserver) {
    return false;
  }
  
  public void draw(Shape paramShape) {}
  
  static {
    m_PSFontReplacement.put("SansSerif.plain", "Helvetica.plain");
    m_PSFontReplacement.put("Dialog.plain", "Helvetica.plain");
    m_PSFontReplacement.put("Microsoft Sans Serif", "Helvetica.plain");
    m_PSFontReplacement.put("MicrosoftSansSerif", "Helvetica.plain");
  }
  
  private class GraphicsState {
    protected Color m_currentColor;
    
    protected Font m_currentFont;
    
    protected Stroke m_currentStroke;
    
    protected int m_xOffset;
    
    protected int m_yOffset;
    
    protected double m_xScale;
    
    protected double m_yScale;
    
    private final PostscriptGraphics this$0;
    
    GraphicsState(PostscriptGraphics this$0) {
      this.this$0 = this$0;
      this.m_currentColor = Color.white;
      this.m_currentFont = new Font("Courier", 0, 11);
      this.m_currentStroke = new BasicStroke();
      this.m_xOffset = 0;
      this.m_yOffset = 0;
      this.m_xScale = 1.0D;
      this.m_yScale = 1.0D;
    }
    
    GraphicsState(PostscriptGraphics this$0, GraphicsState param1GraphicsState) {
      this.this$0 = this$0;
      this.m_currentColor = param1GraphicsState.m_currentColor;
      this.m_currentFont = param1GraphicsState.m_currentFont;
      this.m_currentStroke = param1GraphicsState.m_currentStroke;
      this.m_xOffset = param1GraphicsState.m_xOffset;
      this.m_yOffset = param1GraphicsState.m_yOffset;
      this.m_xScale = param1GraphicsState.m_xScale;
      this.m_yScale = param1GraphicsState.m_yScale;
    }
    
    protected Stroke getStroke() {
      return this.m_currentStroke;
    }
    
    protected void setStroke(Stroke param1Stroke) {
      this.m_currentStroke = param1Stroke;
    }
    
    protected Font getFont() {
      return this.m_currentFont;
    }
    
    protected void setFont(Font param1Font) {
      this.m_currentFont = param1Font;
    }
    
    protected Color getColor() {
      return this.m_currentColor;
    }
    
    protected void setColor(Color param1Color) {
      this.m_currentColor = param1Color;
    }
    
    protected void setXOffset(int param1Int) {
      this.m_xOffset = param1Int;
    }
    
    protected void setYOffset(int param1Int) {
      this.m_yOffset = param1Int;
    }
    
    protected int getXOffset() {
      return this.m_xOffset;
    }
    
    protected int getYOffset() {
      return this.m_yOffset;
    }
    
    protected void setXScale(double param1Double) {
      this.m_xScale = param1Double;
    }
    
    protected void setYScale(double param1Double) {
      this.m_yScale = param1Double;
    }
    
    protected double getXScale() {
      return this.m_xScale;
    }
    
    protected double getYScale() {
      return this.m_yScale;
    }
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\gui\visualize\PostscriptGraphics.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */