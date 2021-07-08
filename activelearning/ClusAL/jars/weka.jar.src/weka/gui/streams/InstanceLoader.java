package weka.gui.streams;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.Serializable;
import java.util.Vector;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;
import weka.core.Instance;
import weka.core.Instances;

public class InstanceLoader extends JPanel implements Serializable, ActionListener, InstanceProducer {
  private Vector m_Listeners;
  
  private Thread m_LoaderThread;
  
  private Instance m_OutputInstance;
  
  private Instances m_OutputInstances;
  
  private boolean m_Debug;
  
  private JButton m_StartBut;
  
  private JTextField m_FileNameTex;
  
  public InstanceLoader() {
    setLayout(new BorderLayout());
    this.m_StartBut = new JButton("Start");
    this.m_StartBut.setBackground(Color.green);
    add("West", this.m_StartBut);
    this.m_StartBut.addActionListener(this);
    this.m_FileNameTex = new JTextField("/home/trigg/datasets/UCI/iris.arff");
    add("Center", this.m_FileNameTex);
    this.m_Listeners = new Vector();
  }
  
  public void setDebug(boolean paramBoolean) {
    this.m_Debug = paramBoolean;
  }
  
  public boolean getDebug() {
    return this.m_Debug;
  }
  
  public void setArffFile(String paramString) {
    this.m_FileNameTex.setText(paramString);
  }
  
  public String getArffFile() {
    return this.m_FileNameTex.getText();
  }
  
  public synchronized void addInstanceListener(InstanceListener paramInstanceListener) {
    this.m_Listeners.addElement(paramInstanceListener);
  }
  
  public synchronized void removeInstanceListener(InstanceListener paramInstanceListener) {
    this.m_Listeners.removeElement(paramInstanceListener);
  }
  
  protected void notifyInstanceProduced(InstanceEvent paramInstanceEvent) {
    Vector vector;
    if (this.m_Debug)
      System.err.println("InstanceLoader::notifyInstanceProduced()"); 
    synchronized (this) {
      vector = (Vector)this.m_Listeners.clone();
    } 
    if (vector.size() > 0) {
      for (byte b = 0; b < vector.size(); b++)
        ((InstanceListener)vector.elementAt(b)).instanceProduced(paramInstanceEvent); 
      if (paramInstanceEvent.getID() == 2)
        this.m_OutputInstance = null; 
    } 
  }
  
  public Instances outputFormat() throws Exception {
    if (this.m_OutputInstances == null)
      throw new Exception("No output format defined."); 
    return new Instances(this.m_OutputInstances, 0);
  }
  
  public Instance outputPeek() throws Exception {
    return (this.m_OutputInstances == null || this.m_OutputInstance == null) ? null : (Instance)this.m_OutputInstance.copy();
  }
  
  public void actionPerformed(ActionEvent paramActionEvent) {
    Object object = paramActionEvent.getSource();
    if (object == this.m_StartBut)
      if (this.m_LoaderThread == null) {
        this.m_LoaderThread = new LoadThread(this, this);
        this.m_LoaderThread.setPriority(1);
        this.m_LoaderThread.start();
      } else {
        this.m_LoaderThread = null;
      }  
  }
  
  private class LoadThread extends Thread {
    private InstanceProducer m_IP;
    
    private final InstanceLoader this$0;
    
    public LoadThread(InstanceLoader this$0, InstanceProducer param1InstanceProducer) {
      this.this$0 = this$0;
      this.m_IP = param1InstanceProducer;
    }
    
    public void run() {
      try {
        this.this$0.m_StartBut.setText("Stop");
        this.this$0.m_StartBut.setBackground(Color.red);
        if (this.this$0.m_Debug)
          System.err.println("InstanceLoader::LoadThread::run()"); 
        BufferedReader bufferedReader = new BufferedReader(new FileReader(this.this$0.m_FileNameTex.getText()));
        this.this$0.m_OutputInstances = new Instances(bufferedReader, 1);
        if (this.this$0.m_Debug)
          System.err.println("InstanceLoader::LoadThread::run() - Instances opened from: " + this.this$0.m_FileNameTex.getText()); 
        InstanceEvent instanceEvent = new InstanceEvent(this.m_IP, 1);
        this.this$0.notifyInstanceProduced(instanceEvent);
        while (this.this$0.m_OutputInstances.readInstance(bufferedReader)) {
          if (this.this$0.m_LoaderThread != this)
            return; 
          if (this.this$0.m_Debug)
            System.err.println("InstanceLoader::LoadThread::run() - read instance"); 
          this.this$0.m_OutputInstance = this.this$0.m_OutputInstances.instance(0);
          this.this$0.m_OutputInstances.delete(0);
          instanceEvent = new InstanceEvent(this.m_IP, 2);
          this.this$0.notifyInstanceProduced(instanceEvent);
        } 
        instanceEvent = new InstanceEvent(this.m_IP, 3);
        this.this$0.notifyInstanceProduced(instanceEvent);
      } catch (Exception exception) {
        System.err.println(exception.getMessage());
      } finally {
        this.this$0.m_LoaderThread = null;
        this.this$0.m_StartBut.setText("Start");
        this.this$0.m_StartBut.setBackground(Color.green);
      } 
    }
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\gui\streams\InstanceLoader.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */