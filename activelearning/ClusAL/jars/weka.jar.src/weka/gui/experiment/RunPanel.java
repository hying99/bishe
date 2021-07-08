package weka.gui.experiment;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.io.Serializable;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import weka.core.SerializedObject;
import weka.core.Utils;
import weka.experiment.Experiment;
import weka.experiment.RemoteExperiment;
import weka.experiment.RemoteExperimentListener;
import weka.gui.LogPanel;

public class RunPanel extends JPanel implements ActionListener {
  protected static final String NOT_RUNNING = "Not running";
  
  protected JButton m_StartBut = new JButton("Start");
  
  protected JButton m_StopBut = new JButton("Stop");
  
  protected LogPanel m_Log = new LogPanel();
  
  protected Experiment m_Exp;
  
  protected Thread m_RunThread = null;
  
  protected ResultsPanel m_ResultsPanel = null;
  
  public void setResultsPanel(ResultsPanel paramResultsPanel) {
    this.m_ResultsPanel = paramResultsPanel;
  }
  
  public RunPanel() {
    this.m_StartBut.addActionListener(this);
    this.m_StopBut.addActionListener(this);
    this.m_StartBut.setEnabled(false);
    this.m_StopBut.setEnabled(false);
    this.m_Log.statusMessage("Not running");
    JPanel jPanel = new JPanel();
    GridBagLayout gridBagLayout = new GridBagLayout();
    GridBagConstraints gridBagConstraints = new GridBagConstraints();
    jPanel.setBorder(BorderFactory.createEmptyBorder(10, 5, 10, 5));
    jPanel.setLayout(gridBagLayout);
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.weightx = 5.0D;
    gridBagConstraints.fill = 2;
    gridBagConstraints.gridwidth = 1;
    gridBagConstraints.gridheight = 1;
    gridBagConstraints.insets = new Insets(0, 2, 0, 2);
    jPanel.add(this.m_StartBut, gridBagConstraints);
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.weightx = 5.0D;
    gridBagConstraints.gridwidth = 1;
    gridBagConstraints.gridheight = 1;
    jPanel.add(this.m_StopBut, gridBagConstraints);
    setLayout(new BorderLayout());
    add(jPanel, "North");
    add((Component)this.m_Log, "Center");
  }
  
  public RunPanel(Experiment paramExperiment) {
    this();
    setExperiment(paramExperiment);
  }
  
  public void setExperiment(Experiment paramExperiment) {
    this.m_Exp = paramExperiment;
    this.m_StartBut.setEnabled((this.m_RunThread == null));
    this.m_StopBut.setEnabled((this.m_RunThread != null));
  }
  
  public void actionPerformed(ActionEvent paramActionEvent) {
    if (paramActionEvent.getSource() == this.m_StartBut) {
      if (this.m_RunThread == null)
        try {
          this.m_RunThread = new ExperimentRunner(this, this.m_Exp);
          this.m_RunThread.setPriority(1);
          this.m_RunThread.start();
        } catch (Exception exception) {
          exception.printStackTrace();
          logMessage("Problem creating experiment copy to run: " + exception.getMessage());
        }  
    } else if (paramActionEvent.getSource() == this.m_StopBut) {
      this.m_StopBut.setEnabled(false);
      logMessage("User aborting experiment. ");
      if (this.m_Exp instanceof RemoteExperiment)
        logMessage("Waiting for remote tasks to complete..."); 
      ((ExperimentRunner)this.m_RunThread).abortExperiment();
      this.m_RunThread = null;
    } 
  }
  
  protected void logMessage(String paramString) {
    this.m_Log.logMessage(paramString);
  }
  
  protected void statusMessage(String paramString) {
    this.m_Log.statusMessage(paramString);
  }
  
  public static void main(String[] paramArrayOfString) {
    try {
      Experiment experiment;
      boolean bool = Utils.getFlag('l', paramArrayOfString);
      String str = Utils.getOption('f', paramArrayOfString);
      if (bool && str.length() == 0)
        throw new Exception("A filename must be given with the -f option"); 
      RemoteExperiment remoteExperiment = null;
      if (bool) {
        FileInputStream fileInputStream = new FileInputStream(str);
        ObjectInputStream objectInputStream = new ObjectInputStream(new BufferedInputStream(fileInputStream));
        Object object = objectInputStream.readObject();
        if (object instanceof RemoteExperiment) {
          remoteExperiment = (RemoteExperiment)object;
        } else {
          experiment = (Experiment)object;
        } 
        objectInputStream.close();
      } else {
        experiment = new Experiment();
      } 
      System.err.println("Initial Experiment:\n" + experiment.toString());
      JFrame jFrame = new JFrame("Run Weka Experiment");
      jFrame.getContentPane().setLayout(new BorderLayout());
      RunPanel runPanel = new RunPanel(experiment);
      jFrame.getContentPane().add(runPanel, "Center");
      jFrame.addWindowListener(new WindowAdapter(runPanel, jFrame) {
            private final RunPanel val$sp;
            
            private final JFrame val$jf;
            
            public void windowClosing(WindowEvent param1WindowEvent) {
              System.err.println("\nExperiment Configuration\n" + this.val$sp.m_Exp.toString());
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
  
  class ExperimentRunner extends Thread implements Serializable {
    Experiment m_ExpCopy;
    
    private final RunPanel this$0;
    
    public ExperimentRunner(RunPanel this$0, Experiment param1Experiment) throws Exception {
      this.this$0 = this$0;
      if (param1Experiment == null) {
        System.err.println("Null experiment!!!");
      } else {
        System.err.println("Running experiment: " + param1Experiment.toString());
      } 
      System.err.println("Writing experiment copy");
      SerializedObject serializedObject = new SerializedObject(param1Experiment);
      System.err.println("Reading experiment copy");
      this.m_ExpCopy = (Experiment)serializedObject.getObject();
      System.err.println("Made experiment copy");
    }
    
    public void abortExperiment() {
      if (this.m_ExpCopy instanceof RemoteExperiment) {
        ((RemoteExperiment)this.m_ExpCopy).abortExperiment();
        this.this$0.m_StopBut.setEnabled(false);
      } 
    }
    
    public void run() {
      this.this$0.m_StartBut.setEnabled(false);
      this.this$0.m_StopBut.setEnabled(true);
      if (this.this$0.m_ResultsPanel != null)
        this.this$0.m_ResultsPanel.setExperiment(null); 
      try {
        if (this.m_ExpCopy instanceof RemoteExperiment) {
          System.err.println("Adding a listener");
          ((RemoteExperiment)this.m_ExpCopy).addRemoteExperimentListener((RemoteExperimentListener)new Object(this));
        } 
        this.this$0.logMessage("Started");
        this.this$0.statusMessage("Initializing...");
        this.m_ExpCopy.initialize();
        byte b = 0;
        if (!(this.m_ExpCopy instanceof RemoteExperiment)) {
          this.this$0.statusMessage("Iterating...");
          while (this.this$0.m_RunThread != null && this.m_ExpCopy.hasMoreIterations()) {
            try {
              String str1 = "Iteration:";
              if (this.m_ExpCopy.getUsePropertyIterator()) {
                int i = this.m_ExpCopy.getCurrentPropertyNumber();
                String str3 = this.m_ExpCopy.getPropertyArray().getClass().getComponentType().getName();
                int j = str3.lastIndexOf('.');
                if (j != -1)
                  str3 = str3.substring(j + 1); 
                String str4 = " " + str3 + "=" + (i + 1) + ":" + this.m_ExpCopy.getPropertyArrayValue(i).getClass().getName();
                str1 = str1 + str4;
              } 
              String str2 = ((File)this.m_ExpCopy.getDatasets().elementAt(this.m_ExpCopy.getCurrentDatasetNumber())).getName();
              str1 = str1 + " Dataset=" + str2 + " Run=" + this.m_ExpCopy.getCurrentRunNumber();
              this.this$0.statusMessage(str1);
              this.m_ExpCopy.nextIteration();
            } catch (Exception exception) {
              b++;
              this.this$0.logMessage(exception.getMessage());
              exception.printStackTrace();
              boolean bool = false;
              if (bool) {
                this.m_ExpCopy.advanceCounters();
                continue;
              } 
              this.this$0.m_RunThread = null;
            } 
          } 
          this.this$0.statusMessage("Postprocessing...");
          this.m_ExpCopy.postProcess();
          if (this.this$0.m_RunThread == null) {
            this.this$0.logMessage("Interrupted");
          } else {
            this.this$0.logMessage("Finished");
          } 
          if (b == 1) {
            this.this$0.logMessage("There was " + b + " error");
          } else {
            this.this$0.logMessage("There were " + b + " errors");
          } 
          this.this$0.statusMessage("Not running");
        } else {
          this.this$0.statusMessage("Remote experiment running...");
          ((RemoteExperiment)this.m_ExpCopy).runExperiment();
        } 
      } catch (Exception exception) {
        exception.printStackTrace();
        System.err.println(exception.getMessage());
        this.this$0.statusMessage(exception.getMessage());
      } finally {
        if (this.this$0.m_ResultsPanel != null)
          this.this$0.m_ResultsPanel.setExperiment(this.m_ExpCopy); 
        if (!(this.m_ExpCopy instanceof RemoteExperiment)) {
          this.this$0.m_RunThread = null;
          this.this$0.m_StartBut.setEnabled(true);
          this.this$0.m_StopBut.setEnabled(false);
          System.err.println("Done...");
        } 
      } 
    }
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\gui\experiment\RunPanel.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */