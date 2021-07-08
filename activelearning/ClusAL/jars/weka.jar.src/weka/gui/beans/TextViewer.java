package weka.gui.beans;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeListener;
import java.beans.VetoableChangeListener;
import java.beans.beancontext.BeanContext;
import java.beans.beancontext.BeanContextChild;
import java.beans.beancontext.BeanContextChildSupport;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.Vector;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import weka.gui.ResultHistoryPanel;

public class TextViewer extends JPanel implements TextListener, DataSourceListener, TrainingSetListener, TestSetListener, Visible, UserRequestAcceptor, Serializable, BeanContextChild {
  protected BeanVisual m_visual;
  
  private transient JFrame m_resultsFrame = null;
  
  private transient JTextArea m_outText = new JTextArea(20, 80);
  
  protected transient ResultHistoryPanel m_history = new ResultHistoryPanel(this.m_outText);
  
  protected boolean m_design;
  
  protected transient BeanContext m_beanContext = null;
  
  protected BeanContextChildSupport m_bcSupport = new BeanContextChildSupport(this);
  
  public TextViewer() {
    appearanceFinal();
  }
  
  protected void appearanceDesign() {
    setUpResultHistory();
    removeAll();
    this.m_visual = new BeanVisual("TextViewer", "weka/gui/beans/icons/DefaultText.gif", "weka/gui/beans/icons/DefaultText_animated.gif");
    setLayout(new BorderLayout());
    add(this.m_visual, "Center");
  }
  
  protected void appearanceFinal() {
    removeAll();
    setLayout(new BorderLayout());
    setUpFinal();
  }
  
  protected void setUpFinal() {
    setUpResultHistory();
    JPanel jPanel = new JPanel();
    jPanel.setLayout(new BorderLayout());
    JScrollPane jScrollPane = new JScrollPane(this.m_outText);
    jScrollPane.setBorder(BorderFactory.createTitledBorder("Text"));
    jPanel.add(jScrollPane, "Center");
    jPanel.add((Component)this.m_history, "West");
    add(jPanel, "Center");
  }
  
  public String globalInfo() {
    return "General purpose text display.";
  }
  
  private void setUpResultHistory() {
    if (this.m_outText == null) {
      this.m_outText = new JTextArea(20, 80);
      this.m_history = new ResultHistoryPanel(this.m_outText);
    } 
    this.m_outText.setEditable(false);
    this.m_outText.setFont(new Font("Monospaced", 0, 12));
    this.m_outText.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
    this.m_history.setBorder(BorderFactory.createTitledBorder("Result list"));
  }
  
  public synchronized void acceptDataSet(DataSetEvent paramDataSetEvent) {
    TextEvent textEvent = new TextEvent(paramDataSetEvent.getSource(), paramDataSetEvent.getDataSet().toString(), paramDataSetEvent.getDataSet().relationName());
    acceptText(textEvent);
  }
  
  public synchronized void acceptTrainingSet(TrainingSetEvent paramTrainingSetEvent) {
    TextEvent textEvent = new TextEvent(paramTrainingSetEvent.getSource(), paramTrainingSetEvent.getTrainingSet().toString(), paramTrainingSetEvent.getTrainingSet().relationName());
    acceptText(textEvent);
  }
  
  public synchronized void acceptTestSet(TestSetEvent paramTestSetEvent) {
    TextEvent textEvent = new TextEvent(paramTestSetEvent.getSource(), paramTestSetEvent.getTestSet().toString(), paramTestSetEvent.getTestSet().relationName());
    acceptText(textEvent);
  }
  
  public synchronized void acceptText(TextEvent paramTextEvent) {
    if (this.m_outText == null)
      setUpResultHistory(); 
    StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append(paramTextEvent.getText());
    String str1 = (new SimpleDateFormat("HH:mm:ss - ")).format(new Date());
    str1 = str1 + paramTextEvent.getTextTitle();
    System.err.println(str1);
    if (str1.length() > 30)
      str1 = str1.substring(0, 30); 
    byte b = 2;
    String str2 = new String(str1);
    while (this.m_history.getNamedBuffer(str1) != null) {
      str1 = new String(str2 + "" + b);
      b++;
    } 
    this.m_history.addResult(str1, stringBuffer);
    this.m_history.setSingle(str1);
  }
  
  public void setVisual(BeanVisual paramBeanVisual) {
    this.m_visual = paramBeanVisual;
  }
  
  public BeanVisual getVisual() {
    return this.m_visual;
  }
  
  public void useDefaultVisual() {
    this.m_visual.loadIcons("weka/gui/beans/icons/DefaultText.gif", "weka/gui/beans/icons/DefaultText_animated.gif");
  }
  
  public void showResults() {
    if (this.m_resultsFrame == null) {
      if (this.m_outText == null)
        setUpResultHistory(); 
      this.m_resultsFrame = new JFrame("Text Viewer");
      this.m_resultsFrame.getContentPane().setLayout(new BorderLayout());
      JScrollPane jScrollPane = new JScrollPane(this.m_outText);
      jScrollPane.setBorder(BorderFactory.createTitledBorder("Text"));
      this.m_resultsFrame.getContentPane().add(jScrollPane, "Center");
      this.m_resultsFrame.getContentPane().add((Component)this.m_history, "West");
      this.m_resultsFrame.addWindowListener(new WindowAdapter(this) {
            private final TextViewer this$0;
            
            public void windowClosing(WindowEvent param1WindowEvent) {
              this.this$0.m_resultsFrame.dispose();
              this.this$0.m_resultsFrame = null;
            }
          });
      this.m_resultsFrame.pack();
      this.m_resultsFrame.setVisible(true);
    } else {
      this.m_resultsFrame.toFront();
    } 
  }
  
  public Enumeration enumerateRequests() {
    Vector vector = new Vector(0);
    vector.addElement("Show results");
    vector.addElement("Clear results");
    return vector.elements();
  }
  
  public void performRequest(String paramString) {
    if (paramString.compareTo("Show results") == 0) {
      showResults();
    } else if (paramString.compareTo("Clear results") == 0) {
      this.m_outText.setText("");
    } else {
      throw new IllegalArgumentException(paramString + " not supported (TextViewer)");
    } 
  }
  
  public void addPropertyChangeListener(String paramString, PropertyChangeListener paramPropertyChangeListener) {
    this.m_bcSupport.addPropertyChangeListener(paramString, paramPropertyChangeListener);
  }
  
  public void removePropertyChangeListener(String paramString, PropertyChangeListener paramPropertyChangeListener) {
    this.m_bcSupport.removePropertyChangeListener(paramString, paramPropertyChangeListener);
  }
  
  public void addVetoableChangeListener(String paramString, VetoableChangeListener paramVetoableChangeListener) {
    this.m_bcSupport.addVetoableChangeListener(paramString, paramVetoableChangeListener);
  }
  
  public void removeVetoableChangeListener(String paramString, VetoableChangeListener paramVetoableChangeListener) {
    this.m_bcSupport.removeVetoableChangeListener(paramString, paramVetoableChangeListener);
  }
  
  public void setBeanContext(BeanContext paramBeanContext) {
    this.m_beanContext = paramBeanContext;
    this.m_design = this.m_beanContext.isDesignTime();
    if (this.m_design) {
      appearanceDesign();
    } else {
      appearanceFinal();
    } 
  }
  
  public BeanContext getBeanContext() {
    return this.m_beanContext;
  }
  
  public static void main(String[] paramArrayOfString) {
    try {
      JFrame jFrame = new JFrame();
      jFrame.getContentPane().setLayout(new BorderLayout());
      TextViewer textViewer = new TextViewer();
      textViewer.acceptText(new TextEvent(textViewer, "Here is some test text from the main method of this class.", "The Title"));
      jFrame.getContentPane().add(textViewer, "Center");
      jFrame.addWindowListener(new WindowAdapter(jFrame) {
            private final JFrame val$jf;
            
            public void windowClosing(WindowEvent param1WindowEvent) {
              this.val$jf.dispose();
              System.exit(0);
            }
          });
      jFrame.setSize(800, 600);
      jFrame.setVisible(true);
    } catch (Exception exception) {
      exception.printStackTrace();
    } 
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\gui\beans\TextViewer.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */