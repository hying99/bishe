package weka.gui.beans;

import java.awt.BorderLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JFrame;
import weka.core.Instances;
import weka.core.converters.ArffSaver;
import weka.core.converters.DatabaseSaver;

public class Saver extends AbstractDataSink implements WekaWrapper {
  private Instances m_dataSet;
  
  private Instances m_structure;
  
  protected String m_globalInfo;
  
  private transient SaveBatchThread m_ioThread;
  
  private weka.core.converters.Saver m_Saver = (weka.core.converters.Saver)new ArffSaver();
  
  private String m_fileName;
  
  private boolean m_isDBSaver;
  
  private int m_count;
  
  private synchronized void block(boolean paramBoolean) {
    if (paramBoolean) {
      try {
        if (this.m_ioThread.isAlive())
          wait(); 
      } catch (InterruptedException interruptedException) {}
    } else {
      notifyAll();
    } 
  }
  
  public String globalInfo() {
    return this.m_globalInfo;
  }
  
  public Saver() {
    setSaver(this.m_Saver);
    this.m_fileName = "";
    this.m_dataSet = null;
    this.m_count = 0;
  }
  
  public void setSaver(weka.core.converters.Saver paramSaver) {
    boolean bool = true;
    if (paramSaver.getClass().getName().compareTo(this.m_Saver.getClass().getName()) == 0)
      bool = false; 
    this.m_Saver = paramSaver;
    String str = paramSaver.getClass().toString();
    str = str.substring(str.lastIndexOf('.') + 1, str.length());
    if (bool && !this.m_visual.loadIcons("weka/gui/beans/icons/" + str + ".gif", "weka/gui/beans/icons/" + str + "_animated.gif"))
      useDefaultVisual(); 
    this.m_visual.setText(str);
    this.m_globalInfo = KnowledgeFlow.getGlobalInfo(this.m_Saver);
    if (this.m_Saver instanceof weka.core.converters.DatabaseConverter) {
      this.m_isDBSaver = true;
    } else {
      this.m_isDBSaver = false;
    } 
  }
  
  public synchronized void acceptDataSet(DataSetEvent paramDataSetEvent) {
    this.m_fileName = paramDataSetEvent.getDataSet().relationName();
    this.m_dataSet = paramDataSetEvent.getDataSet();
    if (paramDataSetEvent.isStructureOnly() && this.m_isDBSaver && ((DatabaseSaver)this.m_Saver).getRelationForTableName())
      ((DatabaseSaver)this.m_Saver).setTableName(this.m_fileName); 
    if (!paramDataSetEvent.isStructureOnly()) {
      if (!this.m_isDBSaver)
        try {
          this.m_Saver.setDirAndPrefix(this.m_fileName, "");
        } catch (Exception exception) {
          System.out.println(exception);
        }  
      saveBatch();
      System.out.println("...relation " + this.m_fileName + " saved.");
    } 
  }
  
  public synchronized void acceptTestSet(TestSetEvent paramTestSetEvent) {
    this.m_fileName = paramTestSetEvent.getTestSet().relationName();
    this.m_dataSet = paramTestSetEvent.getTestSet();
    if (paramTestSetEvent.isStructureOnly() && this.m_isDBSaver && ((DatabaseSaver)this.m_Saver).getRelationForTableName())
      ((DatabaseSaver)this.m_Saver).setTableName(this.m_fileName); 
    if (!paramTestSetEvent.isStructureOnly()) {
      if (!this.m_isDBSaver) {
        try {
          this.m_Saver.setDirAndPrefix(this.m_fileName, "_test_" + paramTestSetEvent.getSetNumber() + "_of_" + paramTestSetEvent.getMaxSetNumber());
        } catch (Exception exception) {
          System.out.println(exception);
        } 
      } else {
        String str = ((DatabaseSaver)this.m_Saver).getTableName();
        str = str.replaceFirst("_[tT][eE][sS][tT]_[0-9]+_[oO][fF]_[0-9]+", "");
        ((DatabaseSaver)this.m_Saver).setTableName(str + "_test_" + paramTestSetEvent.getSetNumber() + "_of_" + paramTestSetEvent.getMaxSetNumber());
      } 
      saveBatch();
      System.out.println("... test set " + paramTestSetEvent.getSetNumber() + " of " + paramTestSetEvent.getMaxSetNumber() + " for relation " + this.m_fileName + " saved.");
    } 
  }
  
  public synchronized void acceptTrainingSet(TrainingSetEvent paramTrainingSetEvent) {
    this.m_fileName = paramTrainingSetEvent.getTrainingSet().relationName();
    this.m_dataSet = paramTrainingSetEvent.getTrainingSet();
    if (paramTrainingSetEvent.isStructureOnly() && this.m_isDBSaver && ((DatabaseSaver)this.m_Saver).getRelationForTableName())
      ((DatabaseSaver)this.m_Saver).setTableName(this.m_fileName); 
    if (!paramTrainingSetEvent.isStructureOnly()) {
      if (!this.m_isDBSaver) {
        try {
          this.m_Saver.setDirAndPrefix(this.m_fileName, "_training_" + paramTrainingSetEvent.getSetNumber() + "_of_" + paramTrainingSetEvent.getMaxSetNumber());
        } catch (Exception exception) {
          System.out.println(exception);
        } 
      } else {
        String str = ((DatabaseSaver)this.m_Saver).getTableName();
        str = str.replaceFirst("_[tT][rR][aA][iI][nN][iI][nN][gG]_[0-9]+_[oO][fF]_[0-9]+", "");
        ((DatabaseSaver)this.m_Saver).setTableName(str + "_training_" + paramTrainingSetEvent.getSetNumber() + "_of_" + paramTrainingSetEvent.getMaxSetNumber());
      } 
      saveBatch();
      System.out.println("... training set " + paramTrainingSetEvent.getSetNumber() + " of " + paramTrainingSetEvent.getMaxSetNumber() + " for relation " + this.m_fileName + " saved.");
    } 
  }
  
  public synchronized void saveBatch() {
    this.m_Saver.setRetrieval(1);
    this.m_visual.setText(this.m_fileName);
    this.m_ioThread = new SaveBatchThread(this, this);
    this.m_ioThread.setPriority(1);
    this.m_ioThread.start();
    block(true);
  }
  
  public synchronized void acceptInstance(InstanceEvent paramInstanceEvent) {
    if (paramInstanceEvent.getStatus() == 0) {
      this.m_Saver.setRetrieval(2);
      this.m_structure = paramInstanceEvent.getStructure();
      this.m_fileName = this.m_structure.relationName();
      this.m_Saver.setInstances(this.m_structure);
      if (this.m_isDBSaver && ((DatabaseSaver)this.m_Saver).getRelationForTableName())
        ((DatabaseSaver)this.m_Saver).setTableName(this.m_fileName); 
    } 
    if (paramInstanceEvent.getStatus() == 1) {
      this.m_visual.setAnimated();
      if (this.m_count == 0) {
        if (!this.m_isDBSaver)
          try {
            this.m_Saver.setDirAndPrefix(this.m_fileName, "");
          } catch (Exception exception) {
            System.out.println(exception);
            this.m_visual.setStatic();
          }  
        this.m_count++;
      } 
      try {
        this.m_visual.setText(this.m_fileName);
        this.m_Saver.writeIncremental(paramInstanceEvent.getInstance());
      } catch (Exception exception) {
        this.m_visual.setStatic();
        System.err.println("Instance " + paramInstanceEvent.getInstance() + " could not been saved");
        exception.printStackTrace();
      } 
    } 
    if (paramInstanceEvent.getStatus() == 2)
      try {
        this.m_Saver.writeIncremental(paramInstanceEvent.getInstance());
        this.m_Saver.writeIncremental(null);
        this.m_visual.setStatic();
        System.out.println("...relation " + this.m_fileName + " saved.");
        this.m_count = 0;
      } catch (Exception exception) {
        this.m_visual.setStatic();
        System.err.println("File could not have been closed.");
        exception.printStackTrace();
      }  
  }
  
  public weka.core.converters.Saver getSaver() {
    return this.m_Saver;
  }
  
  public void setWrappedAlgorithm(Object paramObject) {
    if (!(paramObject instanceof weka.core.converters.Saver))
      throw new IllegalArgumentException(paramObject.getClass() + " : incorrect " + "type of algorithm (Loader)"); 
    setSaver((weka.core.converters.Saver)paramObject);
  }
  
  public Object getWrappedAlgorithm() {
    return getSaver();
  }
  
  public void stop() {}
  
  public static void main(String[] paramArrayOfString) {
    try {
      JFrame jFrame = new JFrame();
      jFrame.getContentPane().setLayout(new BorderLayout());
      Saver saver = new Saver();
      jFrame.getContentPane().add(saver, "Center");
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
  
  private class SaveBatchThread extends Thread {
    private DataSink m_DS;
    
    private final Saver this$0;
    
    public SaveBatchThread(Saver this$0, DataSink param1DataSink) {
      this.this$0 = this$0;
      this.m_DS = param1DataSink;
    }
    
    public void run() {
      try {
        this.this$0.m_visual.setAnimated();
        this.this$0.m_Saver.setInstances(this.this$0.m_dataSet);
        this.this$0.m_Saver.writeBatch();
      } catch (Exception exception) {
        exception.printStackTrace();
      } finally {
        this.this$0.block(false);
        this.this$0.m_visual.setStatic();
      } 
    }
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\gui\beans\Saver.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */