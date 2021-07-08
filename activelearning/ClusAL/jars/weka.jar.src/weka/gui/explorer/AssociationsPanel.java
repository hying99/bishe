package weka.gui.explorer;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyEditor;
import java.beans.PropertyEditorManager;
import java.io.BufferedReader;
import java.io.FileReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JViewport;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import weka.associations.Apriori;
import weka.associations.Associator;
import weka.core.Instances;
import weka.core.OptionHandler;
import weka.core.SelectedTag;
import weka.core.Utils;
import weka.filters.Filter;
import weka.gui.GenericObjectEditor;
import weka.gui.LogPanel;
import weka.gui.Logger;
import weka.gui.PropertyPanel;
import weka.gui.ResultHistoryPanel;
import weka.gui.SaveBuffer;
import weka.gui.SelectedTagEditor;
import weka.gui.SysErrLog;
import weka.gui.TaskLogger;

public class AssociationsPanel extends JPanel {
  protected GenericObjectEditor m_AssociatorEditor = new GenericObjectEditor();
  
  protected PropertyPanel m_CEPanel = new PropertyPanel((PropertyEditor)this.m_AssociatorEditor);
  
  protected JTextArea m_OutText = new JTextArea(20, 40);
  
  protected Logger m_Log = (Logger)new SysErrLog();
  
  protected SaveBuffer m_SaveOut = new SaveBuffer(this.m_Log, this);
  
  protected ResultHistoryPanel m_History = new ResultHistoryPanel(this.m_OutText);
  
  protected JButton m_StartBut = new JButton("Start");
  
  protected JButton m_StopBut = new JButton("Stop");
  
  protected Instances m_Instances;
  
  protected Instances m_TestInstances;
  
  protected Thread m_RunThread;
  
  public AssociationsPanel() {
    this.m_OutText.setEditable(false);
    this.m_OutText.setFont(new Font("Monospaced", 0, 12));
    this.m_OutText.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
    this.m_OutText.addMouseListener(new MouseAdapter(this) {
          private final AssociationsPanel this$0;
          
          public void mouseClicked(MouseEvent param1MouseEvent) {
            if ((param1MouseEvent.getModifiers() & 0x10) != 16)
              this.this$0.m_OutText.selectAll(); 
          }
        });
    this.m_History.setBorder(BorderFactory.createTitledBorder("Result list (right-click for options)"));
    this.m_History.setHandleRightClicks(false);
    this.m_History.getList().addMouseListener(new MouseAdapter(this) {
          private final AssociationsPanel this$0;
          
          public void mouseClicked(MouseEvent param1MouseEvent) {
            if ((param1MouseEvent.getModifiers() & 0x10) != 16 || param1MouseEvent.isAltDown()) {
              int i = this.this$0.m_History.getList().locationToIndex(param1MouseEvent.getPoint());
              if (i != -1) {
                String str = this.this$0.m_History.getNameAtIndex(i);
                this.this$0.historyRightClickPopup(str, param1MouseEvent.getX(), param1MouseEvent.getY());
              } else {
                this.this$0.historyRightClickPopup((String)null, param1MouseEvent.getX(), param1MouseEvent.getY());
              } 
            } 
          }
        });
    this.m_AssociatorEditor.setClassType(Associator.class);
    this.m_AssociatorEditor.setValue(new Apriori());
    this.m_AssociatorEditor.addPropertyChangeListener(new PropertyChangeListener(this) {
          private final AssociationsPanel this$0;
          
          public void propertyChange(PropertyChangeEvent param1PropertyChangeEvent) {
            this.this$0.repaint();
          }
        });
    this.m_StartBut.setToolTipText("Starts the associator");
    this.m_StopBut.setToolTipText("Stops the associator");
    this.m_StartBut.setEnabled(false);
    this.m_StopBut.setEnabled(false);
    this.m_StartBut.addActionListener(new ActionListener(this) {
          private final AssociationsPanel this$0;
          
          public void actionPerformed(ActionEvent param1ActionEvent) {
            this.this$0.startAssociator();
          }
        });
    this.m_StopBut.addActionListener(new ActionListener(this) {
          private final AssociationsPanel this$0;
          
          public void actionPerformed(ActionEvent param1ActionEvent) {
            this.this$0.stopAssociator();
          }
        });
    JPanel jPanel1 = new JPanel();
    jPanel1.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder("Associator"), BorderFactory.createEmptyBorder(0, 5, 5, 5)));
    jPanel1.setLayout(new BorderLayout());
    jPanel1.add((Component)this.m_CEPanel, "North");
    JPanel jPanel2 = new JPanel();
    jPanel2.setLayout(new GridLayout(1, 2));
    JPanel jPanel3 = new JPanel();
    jPanel3.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
    jPanel3.setLayout(new GridLayout(1, 2, 5, 5));
    jPanel3.add(this.m_StartBut);
    jPanel3.add(this.m_StopBut);
    jPanel2.add(jPanel3);
    JPanel jPanel4 = new JPanel();
    jPanel4.setBorder(BorderFactory.createTitledBorder("Associator output"));
    jPanel4.setLayout(new BorderLayout());
    JScrollPane jScrollPane = new JScrollPane(this.m_OutText);
    jPanel4.add(jScrollPane, "Center");
    jScrollPane.getViewport().addChangeListener(new ChangeListener(this) {
          private int lastHeight;
          
          private final AssociationsPanel this$0;
          
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
    GridBagLayout gridBagLayout = new GridBagLayout();
    GridBagConstraints gridBagConstraints = new GridBagConstraints();
    JPanel jPanel5 = new JPanel();
    gridBagLayout = new GridBagLayout();
    jPanel5.setLayout(gridBagLayout);
    gridBagConstraints = new GridBagConstraints();
    gridBagConstraints.anchor = 11;
    gridBagConstraints.fill = 2;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.gridx = 0;
    gridBagLayout.setConstraints(jPanel2, gridBagConstraints);
    jPanel5.add(jPanel2);
    gridBagConstraints = new GridBagConstraints();
    gridBagConstraints.fill = 1;
    gridBagConstraints.gridy = 2;
    gridBagConstraints.gridx = 0;
    gridBagConstraints.weightx = 0.0D;
    gridBagLayout.setConstraints((Component)this.m_History, gridBagConstraints);
    jPanel5.add((Component)this.m_History);
    gridBagConstraints = new GridBagConstraints();
    gridBagConstraints.fill = 1;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridheight = 3;
    gridBagConstraints.weightx = 100.0D;
    gridBagConstraints.weighty = 100.0D;
    gridBagLayout.setConstraints(jPanel4, gridBagConstraints);
    jPanel5.add(jPanel4);
    setLayout(new BorderLayout());
    add(jPanel1, "North");
    add(jPanel5, "Center");
  }
  
  public void setLog(Logger paramLogger) {
    this.m_Log = paramLogger;
  }
  
  public void setInstances(Instances paramInstances) {
    this.m_Instances = paramInstances;
    String[] arrayOfString = new String[this.m_Instances.numAttributes()];
    for (byte b = 0; b < arrayOfString.length; b++) {
      String str = "";
      switch (this.m_Instances.attribute(b).type()) {
        case 1:
          str = "(Nom) ";
          break;
        case 0:
          str = "(Num) ";
          break;
        case 2:
          str = "(Str) ";
          break;
        default:
          str = "(???) ";
          break;
      } 
      arrayOfString[b] = str + this.m_Instances.attribute(b).name();
    } 
    this.m_StartBut.setEnabled((this.m_RunThread == null));
    this.m_StopBut.setEnabled((this.m_RunThread != null));
  }
  
  protected void startAssociator() {
    if (this.m_RunThread == null) {
      this.m_StartBut.setEnabled(false);
      this.m_StopBut.setEnabled(true);
      this.m_RunThread = new Thread(this) {
          private final AssociationsPanel this$0;
          
          public void run() {
            this.this$0.m_Log.statusMessage("Setting up...");
            Instances instances = new Instances(this.this$0.m_Instances);
            Associator associator = (Associator)this.this$0.m_AssociatorEditor.getValue();
            StringBuffer stringBuffer = new StringBuffer();
            String str1 = (new SimpleDateFormat("HH:mm:ss - ")).format(new Date());
            String str2 = associator.getClass().getName();
            if (str2.startsWith("weka.associations.")) {
              str1 = str1 + str2.substring("weka.associations.".length());
            } else {
              str1 = str1 + str2;
            } 
            try {
              this.this$0.m_Log.logMessage("Started " + str2);
              if (this.this$0.m_Log instanceof TaskLogger)
                ((TaskLogger)this.this$0.m_Log).taskStarted(); 
              stringBuffer.append("=== Run information ===\n\n");
              stringBuffer.append("Scheme:       " + str2);
              if (associator instanceof OptionHandler) {
                String[] arrayOfString = ((OptionHandler)associator).getOptions();
                stringBuffer.append(" " + Utils.joinOptions(arrayOfString));
              } 
              stringBuffer.append("\n");
              stringBuffer.append("Relation:     " + instances.relationName() + '\n');
              stringBuffer.append("Instances:    " + instances.numInstances() + '\n');
              stringBuffer.append("Attributes:   " + instances.numAttributes() + '\n');
              if (instances.numAttributes() < 100) {
                for (byte b = 0; b < instances.numAttributes(); b++)
                  stringBuffer.append("              " + instances.attribute(b).name() + '\n'); 
              } else {
                stringBuffer.append("              [list of attributes omitted]\n");
              } 
              this.this$0.m_History.addResult(str1, stringBuffer);
              this.this$0.m_History.setSingle(str1);
              this.this$0.m_Log.statusMessage("Building model on training data...");
              associator.buildAssociations(instances);
              stringBuffer.append("=== Associator model (full training set) ===\n\n");
              stringBuffer.append(associator.toString() + '\n');
              this.this$0.m_History.updateResult(str1);
              this.this$0.m_Log.logMessage("Finished " + str2);
              this.this$0.m_Log.statusMessage("OK");
            } catch (Exception exception) {
              this.this$0.m_Log.logMessage(exception.getMessage());
              this.this$0.m_Log.statusMessage("See error log");
            } finally {
              if (isInterrupted()) {
                this.this$0.m_Log.logMessage("Interrupted " + str2);
                this.this$0.m_Log.statusMessage("See error log");
              } 
              this.this$0.m_RunThread = null;
              this.this$0.m_StartBut.setEnabled(true);
              this.this$0.m_StopBut.setEnabled(false);
              if (this.this$0.m_Log instanceof TaskLogger)
                ((TaskLogger)this.this$0.m_Log).taskFinished(); 
            } 
          }
        };
      this.m_RunThread.setPriority(1);
      this.m_RunThread.start();
    } 
  }
  
  protected void stopAssociator() {
    if (this.m_RunThread != null) {
      this.m_RunThread.interrupt();
      this.m_RunThread.stop();
    } 
  }
  
  protected void saveBuffer(String paramString) {
    StringBuffer stringBuffer = this.m_History.getNamedBuffer(paramString);
    if (stringBuffer != null && this.m_SaveOut.save(stringBuffer))
      this.m_Log.logMessage("Save successful."); 
  }
  
  protected void historyRightClickPopup(String paramString, int paramInt1, int paramInt2) {
    String str = paramString;
    JPopupMenu jPopupMenu = new JPopupMenu();
    JMenuItem jMenuItem1 = new JMenuItem("View in main window");
    if (str != null) {
      jMenuItem1.addActionListener(new ActionListener(this, str) {
            private final String val$selectedName;
            
            private final AssociationsPanel this$0;
            
            public void actionPerformed(ActionEvent param1ActionEvent) {
              this.this$0.m_History.setSingle(this.val$selectedName);
            }
          });
    } else {
      jMenuItem1.setEnabled(false);
    } 
    jPopupMenu.add(jMenuItem1);
    JMenuItem jMenuItem2 = new JMenuItem("View in separate window");
    if (str != null) {
      jMenuItem2.addActionListener(new ActionListener(this, str) {
            private final String val$selectedName;
            
            private final AssociationsPanel this$0;
            
            public void actionPerformed(ActionEvent param1ActionEvent) {
              this.this$0.m_History.openFrame(this.val$selectedName);
            }
          });
    } else {
      jMenuItem2.setEnabled(false);
    } 
    jPopupMenu.add(jMenuItem2);
    JMenuItem jMenuItem3 = new JMenuItem("Save result buffer");
    if (str != null) {
      jMenuItem3.addActionListener(new ActionListener(this, str) {
            private final String val$selectedName;
            
            private final AssociationsPanel this$0;
            
            public void actionPerformed(ActionEvent param1ActionEvent) {
              this.this$0.saveBuffer(this.val$selectedName);
            }
          });
    } else {
      jMenuItem3.setEnabled(false);
    } 
    jPopupMenu.add(jMenuItem3);
    jPopupMenu.show(this.m_History.getList(), paramInt1, paramInt2);
  }
  
  public static void main(String[] paramArrayOfString) {
    try {
      JFrame jFrame = new JFrame("Weka Explorer: Associator");
      jFrame.getContentPane().setLayout(new BorderLayout());
      AssociationsPanel associationsPanel = new AssociationsPanel();
      jFrame.getContentPane().add(associationsPanel, "Center");
      LogPanel logPanel = new LogPanel();
      associationsPanel.setLog((Logger)logPanel);
      jFrame.getContentPane().add((Component)logPanel, "South");
      jFrame.addWindowListener(new WindowAdapter(jFrame) {
            private final JFrame val$jf;
            
            public void windowClosing(WindowEvent param1WindowEvent) {
              this.val$jf.dispose();
              System.exit(0);
            }
          });
      jFrame.pack();
      jFrame.setVisible(true);
      if (paramArrayOfString.length == 1) {
        System.err.println("Loading instances from " + paramArrayOfString[0]);
        BufferedReader bufferedReader = new BufferedReader(new FileReader(paramArrayOfString[0]));
        Instances instances = new Instances(bufferedReader);
        associationsPanel.setInstances(instances);
      } 
    } catch (Exception exception) {
      exception.printStackTrace();
      System.err.println(exception.getMessage());
    } 
  }
  
  static {
    PropertyEditorManager.registerEditor(SelectedTag.class, SelectedTagEditor.class);
    PropertyEditorManager.registerEditor(Filter.class, GenericObjectEditor.class);
    PropertyEditorManager.registerEditor(Associator.class, GenericObjectEditor.class);
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\gui\explorer\AssociationsPanel.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */