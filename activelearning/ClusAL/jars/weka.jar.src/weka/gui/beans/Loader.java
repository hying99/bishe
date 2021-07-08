package weka.gui.beans;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.beancontext.BeanContext;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Vector;
import javax.swing.JButton;
import javax.swing.JFrame;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ArffLoader;
import weka.core.converters.FileSourcedConverter;

public class Loader extends AbstractDataSource implements UserRequestAcceptor, WekaWrapper, EventConstraints {
  private transient Instances m_dataSet;
  
  private transient Instances m_dataFormat;
  
  protected String m_globalInfo;
  
  private LoadThread m_ioThread;
  
  private weka.core.converters.Loader m_Loader = (weka.core.converters.Loader)new ArffLoader();
  
  private InstanceEvent m_ie = new InstanceEvent(this);
  
  private int m_instanceEventTargets = 0;
  
  private int m_dataSetEventTargets = 0;
  
  private boolean m_dbSet = false;
  
  public String globalInfo() {
    return this.m_globalInfo;
  }
  
  public Loader() {
    setLoader(this.m_Loader);
    appearanceFinal();
  }
  
  public void setDB(boolean paramBoolean) {
    this.m_dbSet = paramBoolean;
  }
  
  protected void appearanceFinal() {
    removeAll();
    setLayout(new BorderLayout());
    JButton jButton = new JButton("Start...");
    add(jButton, "Center");
    jButton.addActionListener(new ActionListener(this) {
          private final Loader this$0;
          
          public void actionPerformed(ActionEvent param1ActionEvent) {
            this.this$0.startLoading();
          }
        });
  }
  
  protected void appearanceDesign() {
    removeAll();
    setLayout(new BorderLayout());
    add(this.m_visual, "Center");
  }
  
  public void setBeanContext(BeanContext paramBeanContext) {
    super.setBeanContext(paramBeanContext);
    if (this.m_design) {
      appearanceDesign();
    } else {
      appearanceFinal();
    } 
  }
  
  public void setLoader(weka.core.converters.Loader paramLoader) {
    boolean bool = true;
    if (paramLoader.getClass().getName().compareTo(this.m_Loader.getClass().getName()) == 0)
      bool = false; 
    this.m_Loader = paramLoader;
    String str = paramLoader.getClass().toString();
    str = str.substring(str.lastIndexOf('.') + 1, str.length());
    if (bool && !this.m_visual.loadIcons("weka/gui/beans/icons/" + str + ".gif", "weka/gui/beans/icons/" + str + "_animated.gif"))
      useDefaultVisual(); 
    this.m_visual.setText(str);
    if (!(paramLoader instanceof weka.core.converters.DatabaseLoader))
      try {
        this.m_dataFormat = this.m_Loader.getStructure();
        System.err.println("Notifying listeners of instance structure avail. (Loader).");
        notifyStructureAvailable(this.m_dataFormat);
      } catch (Exception exception) {} 
    this.m_globalInfo = KnowledgeFlow.getGlobalInfo(this.m_Loader);
  }
  
  public weka.core.converters.Loader getLoader() {
    return this.m_Loader;
  }
  
  public void setWrappedAlgorithm(Object paramObject) {
    if (!(paramObject instanceof weka.core.converters.Loader))
      throw new IllegalArgumentException(paramObject.getClass() + " : incorrect " + "type of algorithm (Loader)"); 
    setLoader((weka.core.converters.Loader)paramObject);
  }
  
  public Object getWrappedAlgorithm() {
    return getLoader();
  }
  
  protected void notifyStructureAvailable(Instances paramInstances) {
    if (this.m_dataSetEventTargets > 0 && paramInstances != null) {
      DataSetEvent dataSetEvent = new DataSetEvent(this, paramInstances);
      notifyDataSetLoaded(dataSetEvent);
    } else if (this.m_instanceEventTargets > 0 && paramInstances != null) {
      this.m_ie.setStructure(paramInstances);
      notifyInstanceLoaded(this.m_ie);
    } 
  }
  
  protected void notifyDataSetLoaded(DataSetEvent paramDataSetEvent) {
    Vector vector;
    synchronized (this) {
      vector = (Vector)this.m_listeners.clone();
    } 
    if (vector.size() > 0) {
      for (byte b = 0; b < vector.size(); b++)
        ((DataSourceListener)vector.elementAt(b)).acceptDataSet(paramDataSetEvent); 
      this.m_dataSet = null;
    } 
  }
  
  protected void notifyInstanceLoaded(InstanceEvent paramInstanceEvent) {
    Vector vector;
    synchronized (this) {
      vector = (Vector)this.m_listeners.clone();
    } 
    if (vector.size() > 0) {
      for (byte b = 0; b < vector.size(); b++)
        ((InstanceListener)vector.elementAt(b)).acceptInstance(paramInstanceEvent); 
      this.m_dataSet = null;
    } 
  }
  
  public void startLoading() {
    if (this.m_ioThread == null) {
      this.m_ioThread = new LoadThread(this, this);
      this.m_ioThread.setPriority(1);
      this.m_ioThread.start();
    } else {
      this.m_ioThread = null;
    } 
  }
  
  public Enumeration enumerateRequests() {
    Vector vector = new Vector(0);
    boolean bool = true;
    if (this.m_ioThread == null) {
      if (this.m_Loader instanceof FileSourcedConverter && !((FileSourcedConverter)this.m_Loader).retrieveFile().isFile())
        bool = false; 
      String str = "Start loading";
      if (!bool)
        str = "$" + str; 
      vector.addElement(str);
    } 
    return vector.elements();
  }
  
  public void performRequest(String paramString) {
    if (paramString.compareTo("Start loading") == 0) {
      startLoading();
    } else {
      throw new IllegalArgumentException(paramString + " not supported (Loader)");
    } 
  }
  
  public boolean eventGeneratable(String paramString) {
    if (paramString.compareTo("instance") == 0) {
      if (!(this.m_Loader instanceof weka.core.converters.IncrementalConverter))
        return false; 
      if (this.m_dataSetEventTargets > 0)
        return false; 
    } 
    if (paramString.compareTo("dataSet") == 0) {
      if (!(this.m_Loader instanceof weka.core.converters.BatchConverter))
        return false; 
      if (this.m_instanceEventTargets > 0)
        return false; 
    } 
    return true;
  }
  
  public synchronized void addDataSourceListener(DataSourceListener paramDataSourceListener) {
    super.addDataSourceListener(paramDataSourceListener);
    this.m_dataSetEventTargets++;
    try {
      if (this.m_dbSet) {
        this.m_dataFormat = this.m_Loader.getStructure();
        this.m_dbSet = false;
      } 
    } catch (Exception exception) {}
    notifyStructureAvailable(this.m_dataFormat);
  }
  
  public synchronized void removeDataSourceListener(DataSourceListener paramDataSourceListener) {
    super.removeDataSourceListener(paramDataSourceListener);
    this.m_dataSetEventTargets--;
  }
  
  public synchronized void addInstanceListener(InstanceListener paramInstanceListener) {
    super.addInstanceListener(paramInstanceListener);
    this.m_instanceEventTargets++;
    try {
      if (this.m_dbSet) {
        this.m_dataFormat = this.m_Loader.getStructure();
        this.m_dbSet = false;
      } 
    } catch (Exception exception) {}
    notifyStructureAvailable(this.m_dataFormat);
  }
  
  public synchronized void removeInstanceListener(InstanceListener paramInstanceListener) {
    super.removeInstanceListener(paramInstanceListener);
    this.m_instanceEventTargets--;
  }
  
  public static void main(String[] paramArrayOfString) {
    try {
      JFrame jFrame = new JFrame();
      jFrame.getContentPane().setLayout(new BorderLayout());
      Loader loader = new Loader();
      jFrame.getContentPane().add(loader, "Center");
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
  
  private class LoadThread extends Thread {
    private DataSource m_DP;
    
    private final Loader this$0;
    
    public LoadThread(Loader this$0, DataSource param1DataSource) {
      this.this$0 = this$0;
      this.m_DP = param1DataSource;
    }
    
    public void run() {
      try {
        this.this$0.m_visual.setAnimated();
        boolean bool = true;
        if (this.this$0.m_dataSetEventTargets > 0)
          bool = false; 
        if (bool) {
          Instance instance = null;
          Instances instances = null;
          try {
            System.err.println("NOTIFYING STRUCTURE AVAIL");
            instances = this.this$0.m_Loader.getStructure();
            this.this$0.notifyStructureAvailable(instances);
          } catch (IOException iOException) {
            iOException.printStackTrace();
          } 
          try {
            instance = this.this$0.m_Loader.getNextInstance();
          } catch (IOException iOException) {
            iOException.printStackTrace();
          } 
          for (byte b = 0; instance != null; b++) {
            instance.setDataset(instances);
            this.this$0.m_ie.setStatus(1);
            this.this$0.m_ie.setInstance(instance);
            instance = this.this$0.m_Loader.getNextInstance();
            if (instance == null)
              this.this$0.m_ie.setStatus(2); 
            this.this$0.notifyInstanceLoaded(this.this$0.m_ie);
          } 
          this.this$0.m_visual.setStatic();
        } else {
          this.this$0.m_dataSet = this.this$0.m_Loader.getDataSet();
          this.this$0.m_visual.setStatic();
          this.this$0.m_visual.setText(this.this$0.m_dataSet.relationName());
          this.this$0.notifyDataSetLoaded(new DataSetEvent(this.m_DP, this.this$0.m_dataSet));
        } 
      } catch (Exception exception) {
        exception.printStackTrace();
      } finally {
        this.this$0.m_ioThread = null;
        this.this$0.m_visual.setStatic();
      } 
    }
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\gui\beans\Loader.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */