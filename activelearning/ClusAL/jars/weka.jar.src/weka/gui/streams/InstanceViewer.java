package weka.gui.streams;

import java.awt.BorderLayout;
import java.io.Serializable;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import weka.core.Instance;
import weka.core.Instances;

public class InstanceViewer extends JPanel implements Serializable, InstanceListener {
  private JTextArea m_OutputTex;
  
  private boolean m_Debug;
  
  private boolean m_Clear;
  
  private String m_UpdateString;
  
  private void updateOutput() {
    this.m_OutputTex.append(this.m_UpdateString);
    this.m_UpdateString = "";
  }
  
  private void clearOutput() {
    this.m_UpdateString = "";
    this.m_OutputTex.setText("");
  }
  
  public void inputFormat(Instances paramInstances) {
    if (this.m_Debug)
      System.err.println("InstanceViewer::inputFormat()\n" + paramInstances.toString()); 
    if (this.m_Clear)
      clearOutput(); 
    this.m_UpdateString += paramInstances.toString();
    updateOutput();
  }
  
  public void input(Instance paramInstance) throws Exception {
    if (this.m_Debug)
      System.err.println("InstanceViewer::input(" + paramInstance + ")"); 
    this.m_UpdateString += paramInstance.toString() + "\n";
    updateOutput();
  }
  
  public void batchFinished() {
    updateOutput();
    if (this.m_Debug)
      System.err.println("InstanceViewer::batchFinished()"); 
  }
  
  public InstanceViewer() {
    setLayout(new BorderLayout());
    this.m_UpdateString = "";
    setClearEachDataset(true);
    this.m_OutputTex = new JTextArea(10, 20);
    this.m_OutputTex.setEditable(false);
    add("Center", new JScrollPane(this.m_OutputTex));
  }
  
  public void setClearEachDataset(boolean paramBoolean) {
    this.m_Clear = paramBoolean;
  }
  
  public boolean getClearEachDataset() {
    return this.m_Clear;
  }
  
  public void setDebug(boolean paramBoolean) {
    this.m_Debug = paramBoolean;
  }
  
  public boolean getDebug() {
    return this.m_Debug;
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
        System.err.println("InstanceViewer::instanceProduced() - unknown event type");
      } catch (Exception exception) {
        System.err.println(exception.getMessage());
      } 
    } else {
      System.err.println("InstanceViewer::instanceProduced() - Unknown source object type");
    } 
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\gui\streams\InstanceViewer.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */