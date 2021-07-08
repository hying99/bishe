package weka.gui.visualize;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Vector;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;
import weka.core.RTSI;
import weka.gui.ExtensionFileFilter;

public class PrintableComponent implements PrintableHandler {
  protected JComponent m_Component;
  
  protected static JFileChooser m_FileChooserPanel = null;
  
  protected String m_SaveDialogTitle = "Save as...";
  
  protected double m_xScale = 1.0D;
  
  protected double m_yScale = 1.0D;
  
  private static final boolean DEBUG = false;
  
  public PrintableComponent(JComponent paramJComponent) {
    this.m_Component = paramJComponent;
    getComponent().addMouseListener(new PrintMouseListener(this, this));
    getComponent().setToolTipText("Click left mouse button while holding <alt> and <shift> to display a save dialog.");
    initFileChooser();
  }
  
  public JComponent getComponent() {
    return this.m_Component;
  }
  
  protected void initFileChooser() {
    if (m_FileChooserPanel != null)
      return; 
    m_FileChooserPanel = new JFileChooser();
    m_FileChooserPanel.resetChoosableFileFilters();
    m_FileChooserPanel.setAcceptAllFileFilterUsed(false);
    Vector vector = RTSI.find(JComponentWriter.class.getPackage().getName(), JComponentWriter.class.getName());
    Collections.sort(vector);
    for (byte b = 0; b < vector.size(); b++) {
      try {
        Class clazz = Class.forName(vector.get(b).toString());
        JComponentWriter jComponentWriter = (JComponentWriter)clazz.newInstance();
        m_FileChooserPanel.addChoosableFileFilter((FileFilter)new JComponentWriterFileFilter(this, jComponentWriter.getExtension(), jComponentWriter.getDescription(), jComponentWriter));
      } catch (Exception exception) {
        System.err.println((new StringBuffer()).append(vector.get(b)).append(": ").append(exception).toString());
      } 
    } 
    if ((m_FileChooserPanel.getChoosableFileFilters()).length > 0)
      m_FileChooserPanel.setFileFilter(m_FileChooserPanel.getChoosableFileFilters()[0]); 
  }
  
  public Hashtable getWriters() {
    Hashtable hashtable = new Hashtable();
    for (byte b = 0; b < (m_FileChooserPanel.getChoosableFileFilters()).length; b++) {
      JComponentWriter jComponentWriter = ((JComponentWriterFileFilter)m_FileChooserPanel.getChoosableFileFilters()[b]).getWriter();
      hashtable.put(jComponentWriter.getDescription(), jComponentWriter);
    } 
    return hashtable;
  }
  
  public JComponentWriter getWriter(String paramString) {
    return (JComponentWriter)getWriters().get(paramString);
  }
  
  public void setSaveDialogTitle(String paramString) {
    this.m_SaveDialogTitle = paramString;
  }
  
  public String getSaveDialogTitle() {
    return this.m_SaveDialogTitle;
  }
  
  public void setScale(double paramDouble1, double paramDouble2) {
    this.m_xScale = paramDouble1;
    this.m_yScale = paramDouble2;
  }
  
  public double getXScale() {
    return this.m_xScale;
  }
  
  public double getYScale() {
    return this.m_xScale;
  }
  
  public void saveComponent() {
    // Byte code:
    //   0: getstatic weka/gui/visualize/PrintableComponent.m_FileChooserPanel : Ljavax/swing/JFileChooser;
    //   3: aload_0
    //   4: invokevirtual getSaveDialogTitle : ()Ljava/lang/String;
    //   7: invokevirtual setDialogTitle : (Ljava/lang/String;)V
    //   10: getstatic weka/gui/visualize/PrintableComponent.m_FileChooserPanel : Ljavax/swing/JFileChooser;
    //   13: aload_0
    //   14: invokevirtual getComponent : ()Ljavax/swing/JComponent;
    //   17: invokevirtual showSaveDialog : (Ljava/awt/Component;)I
    //   20: istore_1
    //   21: iload_1
    //   22: ifeq -> 26
    //   25: return
    //   26: getstatic weka/gui/visualize/PrintableComponent.m_FileChooserPanel : Ljavax/swing/JFileChooser;
    //   29: invokevirtual getSelectedFile : ()Ljava/io/File;
    //   32: ifnull -> 10
    //   35: getstatic weka/gui/visualize/PrintableComponent.m_FileChooserPanel : Ljavax/swing/JFileChooser;
    //   38: invokevirtual getFileFilter : ()Ljavax/swing/filechooser/FileFilter;
    //   41: checkcast weka/gui/visualize/PrintableComponent$JComponentWriterFileFilter
    //   44: astore #4
    //   46: getstatic weka/gui/visualize/PrintableComponent.m_FileChooserPanel : Ljavax/swing/JFileChooser;
    //   49: invokevirtual getSelectedFile : ()Ljava/io/File;
    //   52: astore_3
    //   53: aload #4
    //   55: invokevirtual getWriter : ()Lweka/gui/visualize/JComponentWriter;
    //   58: astore_2
    //   59: aload_3
    //   60: invokevirtual getAbsolutePath : ()Ljava/lang/String;
    //   63: invokevirtual toLowerCase : ()Ljava/lang/String;
    //   66: aload_2
    //   67: invokevirtual getExtension : ()Ljava/lang/String;
    //   70: invokevirtual toLowerCase : ()Ljava/lang/String;
    //   73: invokevirtual endsWith : (Ljava/lang/String;)Z
    //   76: ifne -> 111
    //   79: new java/io/File
    //   82: dup
    //   83: new java/lang/StringBuffer
    //   86: dup
    //   87: invokespecial <init> : ()V
    //   90: aload_3
    //   91: invokevirtual getAbsolutePath : ()Ljava/lang/String;
    //   94: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuffer;
    //   97: aload_2
    //   98: invokevirtual getExtension : ()Ljava/lang/String;
    //   101: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuffer;
    //   104: invokevirtual toString : ()Ljava/lang/String;
    //   107: invokespecial <init> : (Ljava/lang/String;)V
    //   110: astore_3
    //   111: aload_2
    //   112: aload_0
    //   113: invokevirtual getComponent : ()Ljavax/swing/JComponent;
    //   116: invokevirtual setComponent : (Ljavax/swing/JComponent;)V
    //   119: aload_2
    //   120: aload_3
    //   121: invokevirtual setFile : (Ljava/io/File;)V
    //   124: aload_2
    //   125: aload_0
    //   126: invokevirtual getXScale : ()D
    //   129: aload_0
    //   130: invokevirtual getYScale : ()D
    //   133: invokevirtual setScale : (DD)V
    //   136: aload_2
    //   137: invokevirtual toOutput : ()V
    //   140: goto -> 150
    //   143: astore #5
    //   145: aload #5
    //   147: invokevirtual printStackTrace : ()V
    //   150: return
    // Exception table:
    //   from	to	target	type
    //   35	140	143	java/lang/Exception
  }
  
  private class PrintMouseListener extends MouseAdapter {
    private PrintableComponent m_Component;
    
    private final PrintableComponent this$0;
    
    PrintMouseListener(PrintableComponent this$0, PrintableComponent param1PrintableComponent1) {
      this.this$0 = this$0;
      this.m_Component = param1PrintableComponent1;
    }
    
    public void mouseClicked(MouseEvent param1MouseEvent) {
      int i = param1MouseEvent.getModifiers();
      if ((i & 0x1) == 1 && (i & 0x8) == 8 && (i & 0x10) == 16) {
        param1MouseEvent.consume();
        this.m_Component.saveComponent();
      } 
    }
  }
  
  protected class JComponentWriterFileFilter extends ExtensionFileFilter {
    private JComponentWriter m_Writer;
    
    private final PrintableComponent this$0;
    
    public JComponentWriterFileFilter(PrintableComponent this$0, String param1String1, String param1String2, JComponentWriter param1JComponentWriter) {
      super(param1String1, param1String2);
      this.this$0 = this$0;
      this.m_Writer = param1JComponentWriter;
    }
    
    public JComponentWriter getWriter() {
      return this.m_Writer;
    }
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\gui\visualize\PrintableComponent.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */