package weka.gui.visualize;

import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGEncodeParam;
import com.sun.image.codec.jpeg.JPEGImageEncoder;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.StringReader;
import javax.swing.JComponent;
import weka.gui.treevisualizer.Node;
import weka.gui.treevisualizer.NodePlace;
import weka.gui.treevisualizer.PlaceNode2;
import weka.gui.treevisualizer.TreeBuild;
import weka.gui.treevisualizer.TreeVisualizer;

public class JPEGWriter extends JComponentWriter {
  private float quality;
  
  private Color background;
  
  public JPEGWriter() {}
  
  public JPEGWriter(JComponent paramJComponent) {
    super(paramJComponent);
  }
  
  public JPEGWriter(JComponent paramJComponent, File paramFile) {
    super(paramJComponent, paramFile);
    this.quality = 1.0F;
    this.background = Color.WHITE;
  }
  
  public void initialize() {
    super.initialize();
    this.quality = 1.0F;
    this.background = Color.WHITE;
    setScalingEnabled(false);
  }
  
  public String getDescription() {
    return "JPEG-Image";
  }
  
  public String getExtension() {
    return ".jpg";
  }
  
  public Color getBackground() {
    return this.background;
  }
  
  public void setBackground(Color paramColor) {
    this.background = paramColor;
  }
  
  public float getQuality() {
    return this.quality;
  }
  
  public void setQuality(float paramFloat) {
    this.quality = paramFloat;
  }
  
  public static void toOutput(JComponent paramJComponent, File paramFile) throws Exception {
    JPEGWriter jPEGWriter = new JPEGWriter(paramJComponent, paramFile);
    jPEGWriter.toOutput();
  }
  
  public void toOutput() throws Exception {
    BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(getFile()));
    BufferedImage bufferedImage = new BufferedImage(getComponent().getWidth(), getComponent().getHeight(), 1);
    Graphics graphics = bufferedImage.getGraphics();
    graphics.setPaintMode();
    graphics.setColor(getBackground());
    if (graphics instanceof Graphics2D)
      ((Graphics2D)graphics).scale(getXScale(), getYScale()); 
    graphics.fillRect(0, 0, getComponent().getWidth(), getComponent().getHeight());
    getComponent().paint(graphics);
    JPEGImageEncoder jPEGImageEncoder = JPEGCodec.createJPEGEncoder(bufferedOutputStream);
    JPEGEncodeParam jPEGEncodeParam = jPEGImageEncoder.getDefaultJPEGEncodeParam(bufferedImage);
    jPEGEncodeParam.setQuality(getQuality(), false);
    jPEGImageEncoder.setJPEGEncodeParam(jPEGEncodeParam);
    jPEGImageEncoder.encode(bufferedImage);
    bufferedOutputStream.flush();
    bufferedOutputStream.close();
  }
  
  public static void main(String[] paramArrayOfString) throws Exception {
    System.out.println("building TreeVisualizer...");
    TreeBuild treeBuild = new TreeBuild();
    PlaceNode2 placeNode2 = new PlaceNode2();
    Node node = treeBuild.create(new StringReader("digraph atree { top [label=\"the top\"] a [label=\"the first node\"] b [label=\"the second nodes\"] c [label=\"comes off of first\"] top->a top->b b->c }"));
    TreeVisualizer treeVisualizer = new TreeVisualizer(null, node, (NodePlace)placeNode2);
    treeVisualizer.setSize(800, 600);
    String str = System.getProperty("java.io.tmpdir") + "test.jpg";
    System.out.println("outputting to '" + str + "'...");
    toOutput((JComponent)treeVisualizer, new File(str));
    System.out.println("done!");
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\gui\visualize\JPEGWriter.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */