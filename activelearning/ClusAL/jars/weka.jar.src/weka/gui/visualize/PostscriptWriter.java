package weka.gui.visualize;

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

public class PostscriptWriter extends JComponentWriter {
  public PostscriptWriter() {
    super(null);
  }
  
  public PostscriptWriter(JComponent paramJComponent) {
    super(paramJComponent);
  }
  
  public PostscriptWriter(JComponent paramJComponent, File paramFile) {
    super(paramJComponent, paramFile);
  }
  
  public String getDescription() {
    return "Postscript-File (EPS)";
  }
  
  public String getExtension() {
    return ".eps";
  }
  
  public static void toOutput(JComponent paramJComponent, File paramFile) throws Exception {
    PostscriptWriter postscriptWriter = new PostscriptWriter(paramJComponent, paramFile);
    postscriptWriter.toOutput();
  }
  
  public void toOutput() throws Exception {
    super.toOutput();
    BufferedOutputStream bufferedOutputStream = null;
    try {
      bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(getFile()));
      PostscriptGraphics postscriptGraphics = new PostscriptGraphics(getComponent().getHeight(), getComponent().getWidth(), bufferedOutputStream);
      postscriptGraphics.setFont(getComponent().getFont());
      postscriptGraphics.scale(getXScale(), getYScale());
      getComponent().printAll(postscriptGraphics);
      postscriptGraphics.finished();
    } catch (Exception exception) {
      System.err.println(exception);
    } finally {
      if (bufferedOutputStream != null)
        try {
          bufferedOutputStream.close();
        } catch (Exception exception) {} 
    } 
  }
  
  public static void main(String[] paramArrayOfString) throws Exception {
    System.out.println("building TreeVisualizer...");
    TreeBuild treeBuild = new TreeBuild();
    PlaceNode2 placeNode2 = new PlaceNode2();
    Node node = treeBuild.create(new StringReader("digraph atree { top [label=\"the top\"] a [label=\"the first node\"] b [label=\"the second nodes\"] c [label=\"comes off of first\"] top->a top->b b->c }"));
    TreeVisualizer treeVisualizer = new TreeVisualizer(null, node, (NodePlace)placeNode2);
    treeVisualizer.setSize(800, 600);
    String str = System.getProperty("java.io.tmpdir") + "test.eps";
    System.out.println("outputting to '" + str + "'...");
    toOutput((JComponent)treeVisualizer, new File(str));
    System.out.println("done!");
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\gui\visualize\PostscriptWriter.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */