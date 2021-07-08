package weka.gui;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.Point;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.Serializable;
import java.util.Hashtable;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JViewport;
import javax.swing.ListSelectionModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.JTextComponent;
import weka.gui.visualize.PrintableComponent;

public class ResultHistoryPanel extends JPanel {
  protected JTextComponent m_SingleText;
  
  protected String m_SingleName;
  
  protected DefaultListModel m_Model = new DefaultListModel();
  
  protected JList m_List = new JList(this.m_Model);
  
  protected Hashtable m_Results = new Hashtable();
  
  protected Hashtable m_FramedOutput = new Hashtable();
  
  protected Hashtable m_Objs = new Hashtable();
  
  protected boolean m_HandleRightClicks = true;
  
  protected PrintableComponent m_Printer = null;
  
  public ResultHistoryPanel(JTextComponent paramJTextComponent) {
    this.m_SingleText = paramJTextComponent;
    if (paramJTextComponent != null)
      this.m_Printer = new PrintableComponent(this.m_SingleText); 
    this.m_List.addMouseListener(new RMouseAdapter(this) {
          private final ResultHistoryPanel this$0;
          
          public void mouseClicked(MouseEvent param1MouseEvent) {
            if ((param1MouseEvent.getModifiers() & 0x10) == 16) {
              if ((param1MouseEvent.getModifiers() & 0x40) == 0 && (param1MouseEvent.getModifiers() & 0x80) == 0) {
                int i = this.this$0.m_List.locationToIndex(param1MouseEvent.getPoint());
                if (i != -1 && this.this$0.m_SingleText != null)
                  this.this$0.setSingle(this.this$0.m_Model.elementAt(i)); 
              } 
            } else if (this.this$0.m_HandleRightClicks) {
              int i = this.this$0.m_List.locationToIndex(param1MouseEvent.getPoint());
              if (i != -1) {
                String str = this.this$0.m_Model.elementAt(i);
                this.this$0.openFrame(str);
              } 
            } 
          }
        });
    this.m_List.addKeyListener(new RKeyAdapter(this) {
          private final ResultHistoryPanel this$0;
          
          public void keyReleased(KeyEvent param1KeyEvent) {
            if (param1KeyEvent.getKeyCode() == 127) {
              int i = this.this$0.m_List.getSelectedIndex();
              if (i != -1)
                this.this$0.removeResult(this.this$0.m_Model.elementAt(i)); 
            } 
          }
        });
    this.m_List.getSelectionModel().addListSelectionListener(new ListSelectionListener(this) {
          private final ResultHistoryPanel this$0;
          
          public void valueChanged(ListSelectionEvent param1ListSelectionEvent) {
            if (!param1ListSelectionEvent.getValueIsAdjusting()) {
              ListSelectionModel listSelectionModel = (ListSelectionModel)param1ListSelectionEvent.getSource();
              for (int i = param1ListSelectionEvent.getFirstIndex(); i <= param1ListSelectionEvent.getLastIndex(); i++) {
                if (listSelectionModel.isSelectedIndex(i)) {
                  if (i != -1 && this.this$0.m_SingleText != null)
                    this.this$0.setSingle(this.this$0.m_Model.elementAt(i)); 
                  break;
                } 
              } 
            } 
          }
        });
    setLayout(new BorderLayout());
    JScrollPane jScrollPane = new JScrollPane(this.m_List);
    jScrollPane.getViewport().addChangeListener(new ChangeListener(this) {
          private int lastHeight;
          
          private final ResultHistoryPanel this$0;
          
          public void stateChanged(ChangeEvent param1ChangeEvent) {
            JViewport jViewport = (JViewport)param1ChangeEvent.getSource();
            int i = (jViewport.getViewSize()).height;
            if (i != this.lastHeight) {
              this.lastHeight = i;
              int j = i - (jViewport.getExtentSize()).height;
              jViewport.setViewPosition(new Point(0, j));
            } 
          }
        });
    add(jScrollPane, "Center");
  }
  
  public void addResult(String paramString, StringBuffer paramStringBuffer) {
    this.m_Model.addElement(paramString);
    this.m_Results.put(paramString, paramStringBuffer);
  }
  
  public void removeResult(String paramString) {
    StringBuffer stringBuffer = (StringBuffer)this.m_Results.get(paramString);
    JTextComponent jTextComponent = (JTextComponent)this.m_FramedOutput.get(paramString);
    if (stringBuffer != null) {
      this.m_Results.remove(paramString);
      this.m_Model.removeElement(paramString);
    } 
  }
  
  public void addObject(String paramString, Object paramObject) {
    this.m_Objs.put(paramString, paramObject);
  }
  
  public Object getNamedObject(String paramString) {
    null = null;
    return this.m_Objs.get(paramString);
  }
  
  public Object getSelectedObject() {
    Object object = null;
    int i = this.m_List.getSelectedIndex();
    if (i != -1) {
      String str = this.m_Model.elementAt(i);
      object = this.m_Objs.get(str);
    } 
    return object;
  }
  
  public StringBuffer getNamedBuffer(String paramString) {
    null = null;
    return (StringBuffer)this.m_Results.get(paramString);
  }
  
  public StringBuffer getSelectedBuffer() {
    StringBuffer stringBuffer = null;
    int i = this.m_List.getSelectedIndex();
    if (i != -1) {
      String str = this.m_Model.elementAt(i);
      stringBuffer = (StringBuffer)this.m_Results.get(str);
    } 
    return stringBuffer;
  }
  
  public String getSelectedName() {
    int i = this.m_List.getSelectedIndex();
    return (i != -1) ? this.m_Model.elementAt(i) : null;
  }
  
  public String getNameAtIndex(int paramInt) {
    return (paramInt != -1) ? this.m_Model.elementAt(paramInt) : null;
  }
  
  public void setSingle(String paramString) {
    StringBuffer stringBuffer = (StringBuffer)this.m_Results.get(paramString);
    if (stringBuffer != null) {
      this.m_SingleName = paramString;
      this.m_SingleText.setText(stringBuffer.toString());
      this.m_List.setSelectedValue(paramString, true);
    } 
  }
  
  public void openFrame(String paramString) {
    StringBuffer stringBuffer = (StringBuffer)this.m_Results.get(paramString);
    JTextComponent jTextComponent = (JTextComponent)this.m_FramedOutput.get(paramString);
    if (stringBuffer != null && jTextComponent == null) {
      JTextArea jTextArea = new JTextArea();
      jTextArea.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
      jTextArea.setFont(new Font("Monospaced", 0, 12));
      jTextArea.setEditable(false);
      jTextArea.setText(stringBuffer.toString());
      this.m_FramedOutput.put(paramString, jTextArea);
      JFrame jFrame = new JFrame(paramString);
      jFrame.addWindowListener(new WindowAdapter(this, jFrame) {
            private final JFrame val$jf;
            
            private final ResultHistoryPanel this$0;
            
            public void windowClosing(WindowEvent param1WindowEvent) {
              this.this$0.m_FramedOutput.remove(this.val$jf.getTitle());
              this.val$jf.dispose();
            }
          });
      jFrame.getContentPane().setLayout(new BorderLayout());
      jFrame.getContentPane().add(new JScrollPane(jTextArea), "Center");
      jFrame.pack();
      jFrame.setSize(450, 350);
      jFrame.setVisible(true);
    } 
  }
  
  public void updateResult(String paramString) {
    StringBuffer stringBuffer = (StringBuffer)this.m_Results.get(paramString);
    if (stringBuffer == null)
      return; 
    if (this.m_SingleName == paramString)
      this.m_SingleText.setText(stringBuffer.toString()); 
    JTextComponent jTextComponent = (JTextComponent)this.m_FramedOutput.get(paramString);
    if (jTextComponent != null)
      jTextComponent.setText(stringBuffer.toString()); 
  }
  
  public ListSelectionModel getSelectionModel() {
    return this.m_List.getSelectionModel();
  }
  
  public JList getList() {
    return this.m_List;
  }
  
  public void setHandleRightClicks(boolean paramBoolean) {
    this.m_HandleRightClicks = paramBoolean;
  }
  
  public static void main(String[] paramArrayOfString) {
    try {
      JFrame jFrame = new JFrame("Weka Explorer: Classifier");
      jFrame.getContentPane().setLayout(new BorderLayout());
      ResultHistoryPanel resultHistoryPanel = new ResultHistoryPanel(null);
      resultHistoryPanel.addResult("blah", new StringBuffer("Nothing to see here"));
      resultHistoryPanel.addResult("blah1", new StringBuffer("Nothing to see here1"));
      resultHistoryPanel.addResult("blah2", new StringBuffer("Nothing to see here2"));
      resultHistoryPanel.addResult("blah3", new StringBuffer("Nothing to see here3"));
      jFrame.getContentPane().add(resultHistoryPanel, "Center");
      jFrame.addWindowListener(new WindowAdapter(jFrame) {
            private final JFrame val$jf;
            
            public void windowClosing(WindowEvent param1WindowEvent) {
              this.val$jf.dispose();
              System.exit(0);
            }
          });
      jFrame.pack();
      jFrame.setVisible(true);
    } catch (Exception exception) {
      exception.printStackTrace();
      System.err.println(exception.getMessage());
    } 
  }
  
  public static class RKeyAdapter extends KeyAdapter implements Serializable {}
  
  public static class RMouseAdapter extends MouseAdapter implements Serializable {}
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\gui\ResultHistoryPanel.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */