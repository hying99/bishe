package weka.gui.streams;

import java.awt.Color;
import java.io.Serializable;
import javax.swing.JLabel;
import javax.swing.JPanel;
import weka.core.Instance;
import weka.core.Instances;

public class InstanceCounter extends JPanel implements Serializable, InstanceListener {
  private JLabel m_Count_Lab = new JLabel("no instances");
  
  private int m_Count = 0;
  
  private boolean m_Debug;
  
  public void input(Instance paramInstance) throws Exception {
    if (this.m_Debug)
      System.err.println("InstanceCounter::input(" + paramInstance + ")"); 
    this.m_Count++;
    this.m_Count_Lab.setText("" + this.m_Count + " instances");
    repaint();
  }
  
  public void inputFormat(Instances paramInstances) {
    if (this.m_Debug)
      System.err.println("InstanceCounter::inputFormat()"); 
    Instances instances = new Instances(paramInstances, 0);
    this.m_Count = 0;
    this.m_Count_Lab.setText("" + this.m_Count + " instances");
  }
  
  public void setDebug(boolean paramBoolean) {
    this.m_Debug = paramBoolean;
  }
  
  public boolean getDebug() {
    return this.m_Debug;
  }
  
  public InstanceCounter() {
    add(this.m_Count_Lab);
    setBackground(Color.lightGray);
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
            if (this.m_Debug)
              System.err.println("InstanceCounter::instanceProduced() - End of instance batch"); 
            return;
        } 
        System.err.println("InstanceCounter::instanceProduced() - unknown event type");
      } catch (Exception exception) {
        System.err.println(exception.getMessage());
      } 
    } else {
      System.err.println("InstanceCounter::instanceProduced() - Unknown source object type");
    } 
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\gui\streams\InstanceCounter.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */