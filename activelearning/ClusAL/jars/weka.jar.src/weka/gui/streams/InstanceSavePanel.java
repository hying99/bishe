package weka.gui.streams;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Label;
import java.awt.Panel;
import java.awt.TextField;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.Serializable;
import weka.core.Instance;
import weka.core.Instances;

public class InstanceSavePanel extends Panel implements Serializable, InstanceListener {
  private Label count_Lab;
  
  private int m_Count;
  
  private TextField arffFile_Tex;
  
  private boolean b_Debug;
  
  private PrintWriter outputWriter;
  
  public void input(Instance paramInstance) throws Exception {
    if (this.b_Debug)
      System.err.println("InstanceSavePanel::input(" + paramInstance + ")"); 
    this.m_Count++;
    this.count_Lab.setText("" + this.m_Count + " instances");
    if (this.outputWriter != null)
      this.outputWriter.println(paramInstance.toString()); 
  }
  
  public void inputFormat(Instances paramInstances) {
    if (this.b_Debug)
      System.err.println("InstanceSavePanel::inputFormat()\n" + paramInstances.toString()); 
    this.m_Count = 0;
    this.count_Lab.setText("" + this.m_Count + " instances");
    try {
      this.outputWriter = new PrintWriter(new FileOutputStream(this.arffFile_Tex.getText()));
      this.outputWriter.println(paramInstances.toString());
      if (this.b_Debug)
        System.err.println("InstanceSavePanel::inputFormat() - written header"); 
    } catch (Exception exception) {
      this.outputWriter = null;
      System.err.println("InstanceSavePanel::inputFormat(): " + exception.getMessage());
    } 
  }
  
  public void batchFinished() {
    if (this.b_Debug)
      System.err.println("InstanceSavePanel::batchFinished()"); 
    if (this.outputWriter != null)
      this.outputWriter.close(); 
  }
  
  public InstanceSavePanel() {
    setLayout(new BorderLayout());
    this.arffFile_Tex = new TextField("arffoutput.arff");
    add("Center", this.arffFile_Tex);
    this.count_Lab = new Label("0 instances");
    add("East", this.count_Lab);
    setBackground(Color.lightGray);
  }
  
  public void setDebug(boolean paramBoolean) {
    this.b_Debug = paramBoolean;
  }
  
  public boolean getDebug() {
    return this.b_Debug;
  }
  
  public void setArffFile(String paramString) {
    this.arffFile_Tex.setText(paramString);
  }
  
  public String getArffFile() {
    return this.arffFile_Tex.getText();
  }
  
  public void instanceProduced(InstanceEvent paramInstanceEvent) {
    Object object = paramInstanceEvent.getSource();
    if (object instanceof InstanceProducer) {
      try {
        InstanceProducer instanceProducer = (InstanceProducer)object;
        switch (paramInstanceEvent.getID()) {
          case 1:
            inputFormat(instanceProducer.outputFormat());
            return;
          case 2:
            input(instanceProducer.outputPeek());
            return;
          case 3:
            batchFinished();
            return;
        } 
        System.err.println("InstanceSavePanel::instanceProduced() - unknown event type");
      } catch (Exception exception) {
        System.err.println(exception.getMessage());
      } 
    } else {
      System.err.println("InstanceSavePanel::instanceProduced() - Unknown source object type");
    } 
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\gui\streams\InstanceSavePanel.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */