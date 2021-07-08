package weka.gui.arffviewer;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Vector;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JTabbedPane;
import javax.swing.KeyStroke;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileFilter;
import weka.core.Instances;
import weka.core.converters.AbstractSaver;
import weka.core.converters.ArffSaver;
import weka.core.converters.CSVSaver;
import weka.gui.ComponentHelper;
import weka.gui.ExtensionFileFilter;
import weka.gui.JTableHelper;
import weka.gui.ListSelectorDialog;

public class ArffViewer extends JFrame implements ActionListener, ChangeListener, WindowListener {
  private static final int DEFAULT_WIDTH = -1;
  
  private static final int DEFAULT_HEIGHT = -1;
  
  private static final int DEFAULT_LEFT = -1;
  
  private static final int DEFAULT_TOP = -1;
  
  private static final int WIDTH = 800;
  
  private static final int HEIGHT = 600;
  
  private JTabbedPane tabbedPane;
  
  private JMenuBar menuBar;
  
  private JMenu menuFile;
  
  private JMenuItem menuFileOpen;
  
  private JMenuItem menuFileSave;
  
  private JMenuItem menuFileSaveAs;
  
  private JMenuItem menuFileClose;
  
  private JMenuItem menuFileProperties;
  
  private JMenuItem menuFileExit;
  
  private JMenu menuEdit;
  
  private JMenuItem menuEditUndo;
  
  private JMenuItem menuEditCopy;
  
  private JMenuItem menuEditSearch;
  
  private JMenuItem menuEditClearSearch;
  
  private JMenuItem menuEditDeleteAttribute;
  
  private JMenuItem menuEditDeleteAttributes;
  
  private JMenuItem menuEditDeleteInstance;
  
  private JMenuItem menuEditDeleteInstances;
  
  private JMenuItem menuEditSortInstances;
  
  private JMenu menuView;
  
  private JMenuItem menuViewAttributes;
  
  private JMenuItem menuViewValues;
  
  private FileChooser fileChooser;
  
  private ExtensionFileFilter arffFilter;
  
  private ExtensionFileFilter csvFilter;
  
  private String frameTitle = "ARFF-Viewer";
  
  private boolean confirmExit;
  
  private int width;
  
  private int height;
  
  private int top;
  
  private int left;
  
  public ArffViewer() {
    super("ARFF-Viewer");
    createFrame();
  }
  
  protected void createFrame() {
    setIconImage(ComponentHelper.getImage("weka_icon.gif"));
    setSize(800, 600);
    setCenteredLocation();
    setDefaultCloseOperation(2);
    removeWindowListener(this);
    addWindowListener(this);
    setConfirmExit(false);
    getContentPane().setLayout(new BorderLayout());
    this.arffFilter = new ExtensionFileFilter("arff", "ARFF-Files");
    this.csvFilter = new ExtensionFileFilter("csv", "CSV-File");
    this.fileChooser = new FileChooser();
    this.fileChooser.setMultiSelectionEnabled(true);
    this.fileChooser.addChoosableFileFilter((FileFilter)this.arffFilter);
    this.fileChooser.addChoosableFileFilter((FileFilter)this.csvFilter);
    this.fileChooser.setFileFilter((FileFilter)this.arffFilter);
    this.menuBar = new JMenuBar();
    setJMenuBar(this.menuBar);
    this.menuFile = new JMenu("File");
    this.menuFileOpen = new JMenuItem("Open...", ComponentHelper.getImageIcon("open.gif"));
    this.menuFileOpen.setAccelerator(KeyStroke.getKeyStroke(79, 2));
    this.menuFileOpen.addActionListener(this);
    this.menuFileSave = new JMenuItem("Save", ComponentHelper.getImageIcon("save.gif"));
    this.menuFileSave.setAccelerator(KeyStroke.getKeyStroke(83, 2));
    this.menuFileSave.addActionListener(this);
    this.menuFileSaveAs = new JMenuItem("Save as...", ComponentHelper.getImageIcon("empty.gif"));
    this.menuFileSaveAs.setAccelerator(KeyStroke.getKeyStroke(83, 3));
    this.menuFileSaveAs.addActionListener(this);
    this.menuFileClose = new JMenuItem("Close", ComponentHelper.getImageIcon("empty.gif"));
    this.menuFileClose.setAccelerator(KeyStroke.getKeyStroke(87, 2));
    this.menuFileClose.addActionListener(this);
    this.menuFileProperties = new JMenuItem("Properties", ComponentHelper.getImageIcon("empty.gif"));
    this.menuFileProperties.setAccelerator(KeyStroke.getKeyStroke(10, 2));
    this.menuFileProperties.addActionListener(this);
    this.menuFileExit = new JMenuItem("Exit", ComponentHelper.getImageIcon("forward.gif"));
    this.menuFileExit.setAccelerator(KeyStroke.getKeyStroke(88, 8));
    this.menuFileExit.addActionListener(this);
    this.menuFile.add(this.menuFileOpen);
    this.menuFile.add(this.menuFileSave);
    this.menuFile.add(this.menuFileSaveAs);
    this.menuFile.add(this.menuFileClose);
    this.menuFile.addSeparator();
    this.menuFile.add(this.menuFileProperties);
    this.menuFile.addSeparator();
    this.menuFile.add(this.menuFileExit);
    this.menuBar.add(this.menuFile);
    this.menuEdit = new JMenu("Edit");
    this.menuEditUndo = new JMenuItem("Undo", ComponentHelper.getImageIcon("undo.gif"));
    this.menuEditUndo.setAccelerator(KeyStroke.getKeyStroke(90, 2));
    this.menuEditUndo.addActionListener(this);
    this.menuEditCopy = new JMenuItem("Copy", ComponentHelper.getImageIcon("copy.gif"));
    this.menuEditCopy.setAccelerator(KeyStroke.getKeyStroke(155, 2));
    this.menuEditCopy.addActionListener(this);
    this.menuEditSearch = new JMenuItem("Search...", ComponentHelper.getImageIcon("find.gif"));
    this.menuEditSearch.setAccelerator(KeyStroke.getKeyStroke(70, 2));
    this.menuEditSearch.addActionListener(this);
    this.menuEditClearSearch = new JMenuItem("Clear search", ComponentHelper.getImageIcon("empty.gif"));
    this.menuEditClearSearch.setAccelerator(KeyStroke.getKeyStroke(70, 3));
    this.menuEditClearSearch.addActionListener(this);
    this.menuEditDeleteAttribute = new JMenuItem("Delete attribute", ComponentHelper.getImageIcon("empty.gif"));
    this.menuEditDeleteAttribute.addActionListener(this);
    this.menuEditDeleteAttributes = new JMenuItem("Delete attributes", ComponentHelper.getImageIcon("empty.gif"));
    this.menuEditDeleteAttributes.addActionListener(this);
    this.menuEditDeleteInstance = new JMenuItem("Delete instance", ComponentHelper.getImageIcon("empty.gif"));
    this.menuEditDeleteInstance.addActionListener(this);
    this.menuEditDeleteInstances = new JMenuItem("Delete instances", ComponentHelper.getImageIcon("empty.gif"));
    this.menuEditDeleteInstances.addActionListener(this);
    this.menuEditSortInstances = new JMenuItem("Sort data (ascending)", ComponentHelper.getImageIcon("sort.gif"));
    this.menuEditSortInstances.addActionListener(this);
    this.menuEdit.add(this.menuEditUndo);
    this.menuEdit.addSeparator();
    this.menuEdit.add(this.menuEditCopy);
    this.menuEdit.addSeparator();
    this.menuEdit.add(this.menuEditSearch);
    this.menuEdit.add(this.menuEditClearSearch);
    this.menuEdit.addSeparator();
    this.menuEdit.add(this.menuEditDeleteAttribute);
    this.menuEdit.add(this.menuEditDeleteAttributes);
    this.menuEdit.addSeparator();
    this.menuEdit.add(this.menuEditDeleteInstance);
    this.menuEdit.add(this.menuEditDeleteInstances);
    this.menuEdit.add(this.menuEditSortInstances);
    this.menuBar.add(this.menuEdit);
    this.menuView = new JMenu("View");
    this.menuViewAttributes = new JMenuItem("Attributes...", ComponentHelper.getImageIcon("objects.gif"));
    this.menuViewAttributes.setAccelerator(KeyStroke.getKeyStroke(65, 3));
    this.menuViewAttributes.addActionListener(this);
    this.menuViewValues = new JMenuItem("Values...", ComponentHelper.getImageIcon("properties.gif"));
    this.menuViewValues.setAccelerator(KeyStroke.getKeyStroke(86, 3));
    this.menuViewValues.addActionListener(this);
    this.menuView.add(this.menuViewAttributes);
    this.menuView.add(this.menuViewValues);
    this.menuBar.add(this.menuView);
    this.tabbedPane = new JTabbedPane();
    this.tabbedPane.addChangeListener(this);
    getContentPane().add(this.tabbedPane, "Center");
    setMenu();
  }
  
  protected void resizeFrame() {
    if (this.width != -1)
      setSize(this.width, (getSize()).height); 
    if (this.height != -1)
      setSize((getSize()).width, this.height); 
    setCenteredLocation();
    if (this.top != -1)
      setLocation((getLocation()).x, this.top); 
    if (this.left != -1)
      setLocation(this.left, (getLocation()).y); 
  }
  
  protected int getCenteredLeft() {
    int i = (getBounds()).width;
    int j = ((getGraphicsConfiguration().getBounds()).width - i) / 2;
    if (j < 0)
      j = 0; 
    return j;
  }
  
  protected int getCenteredTop() {
    int i = (getBounds()).height;
    int j = ((getGraphicsConfiguration().getBounds()).height - i) / 2;
    if (j < 0)
      j = 0; 
    return j;
  }
  
  public void setCenteredLocation() {
    setLocation(getCenteredLeft(), getCenteredTop());
  }
  
  public void setConfirmExit(boolean paramBoolean) {
    this.confirmExit = paramBoolean;
  }
  
  public boolean getConfirmExit() {
    return this.confirmExit;
  }
  
  public void refresh() {
    validate();
    repaint();
  }
  
  protected void setFrameTitle() {
    if (getCurrentFilename().equals("")) {
      setTitle(this.frameTitle);
    } else {
      setTitle(this.frameTitle + " - " + getCurrentFilename());
    } 
  }
  
  private void setMenu() {
    boolean bool1 = (getCurrentPanel() != null) ? true : false;
    boolean bool2 = (bool1 && getCurrentPanel().isChanged()) ? true : false;
    boolean bool3 = (bool1 && getCurrentPanel().canUndo()) ? true : false;
    this.menuFileOpen.setEnabled(true);
    this.menuFileSave.setEnabled(bool2);
    this.menuFileSaveAs.setEnabled(bool1);
    this.menuFileClose.setEnabled(bool1);
    this.menuFileProperties.setEnabled(bool1);
    this.menuFileExit.setEnabled(true);
    this.menuEditUndo.setEnabled(bool3);
    this.menuEditCopy.setEnabled(bool1);
    this.menuEditSearch.setEnabled(bool1);
    this.menuEditClearSearch.setEnabled(bool1);
    this.menuEditDeleteAttribute.setEnabled(bool1);
    this.menuEditDeleteAttributes.setEnabled(bool1);
    this.menuEditDeleteInstance.setEnabled(bool1);
    this.menuEditDeleteInstances.setEnabled(bool1);
    this.menuEditSortInstances.setEnabled(bool1);
    this.menuViewAttributes.setEnabled(bool1);
    this.menuViewValues.setEnabled(bool1);
  }
  
  private void setTabTitle(JComponent paramJComponent) {
    if (!(paramJComponent instanceof ArffPanel))
      return; 
    int i = this.tabbedPane.indexOfComponent(paramJComponent);
    if (i == -1)
      return; 
    this.tabbedPane.setTitleAt(i, ((ArffPanel)paramJComponent).getTitle());
    setFrameTitle();
  }
  
  public int getPanelCount() {
    return this.tabbedPane.getTabCount();
  }
  
  public ArffPanel getPanel(int paramInt) {
    return (paramInt >= 0 && paramInt < getPanelCount()) ? (ArffPanel)this.tabbedPane.getComponentAt(paramInt) : null;
  }
  
  public int getCurrentIndex() {
    return this.tabbedPane.getSelectedIndex();
  }
  
  public ArffPanel getCurrentPanel() {
    return getPanel(getCurrentIndex());
  }
  
  public boolean isPanelSelected() {
    return (getCurrentPanel() != null);
  }
  
  public String getFilename(int paramInt) {
    String str = "";
    ArffPanel arffPanel = getPanel(paramInt);
    if (arffPanel != null)
      str = arffPanel.getFilename(); 
    return str;
  }
  
  public String getCurrentFilename() {
    return getFilename(getCurrentIndex());
  }
  
  public void setFilename(int paramInt, String paramString) {
    ArffPanel arffPanel = getPanel(paramInt);
    if (arffPanel != null) {
      arffPanel.setFilename(paramString);
      setTabTitle(arffPanel);
    } 
  }
  
  public void setCurrentFilename(String paramString) {
    setFilename(getCurrentIndex(), paramString);
  }
  
  private boolean saveChanges() {
    return saveChanges(true);
  }
  
  private boolean saveChanges(boolean paramBoolean) {
    if (!isPanelSelected())
      return true; 
    boolean bool = !getCurrentPanel().isChanged() ? true : false;
    if (getCurrentPanel().isChanged()) {
      byte b;
      try {
        if (paramBoolean) {
          b = ComponentHelper.showMessageBox(this, "Changed", "The file is not saved - Do you want to save it?", 1, 3);
        } else {
          b = ComponentHelper.showMessageBox(this, "Changed", "The file is not saved - Do you want to save it?", 0, 3);
        } 
      } catch (Exception exception) {
        b = 2;
      } 
      switch (b) {
        case 0:
          saveFile();
          bool = !getCurrentPanel().isChanged() ? true : false;
          break;
        case 1:
          bool = true;
          break;
        case 2:
          bool = false;
          break;
      } 
    } 
    return bool;
  }
  
  private void loadFile() {
    int i = this.fileChooser.showOpenDialog(this);
    if (i != 0)
      return; 
    setCursor(Cursor.getPredefinedCursor(3));
    for (byte b = 0; b < (this.fileChooser.getSelectedFiles()).length; b++) {
      String str = this.fileChooser.getSelectedFiles()[b].getAbsolutePath();
      ArffPanel arffPanel = new ArffPanel(str);
      arffPanel.addChangeListener(this);
      this.tabbedPane.addTab(arffPanel.getTitle(), arffPanel);
      this.tabbedPane.setSelectedIndex(this.tabbedPane.getTabCount() - 1);
    } 
    setCursor(Cursor.getPredefinedCursor(0));
  }
  
  private void saveFile() {
    ArffPanel arffPanel = getCurrentPanel();
    if (arffPanel == null)
      return; 
    String str = arffPanel.getFilename();
    if (str.equals("Instances")) {
      saveFileAs();
    } else {
      AbstractSaver abstractSaver;
      if (this.fileChooser.getFileFilter() == this.arffFilter) {
        abstractSaver = (AbstractSaver)new ArffSaver();
      } else if (this.fileChooser.getFileFilter() == this.csvFilter) {
        abstractSaver = (AbstractSaver)new CSVSaver();
      } else {
        abstractSaver = null;
      } 
      if (abstractSaver != null)
        try {
          abstractSaver.setRetrieval(1);
          abstractSaver.setInstances(arffPanel.getInstances());
          abstractSaver.setFile(this.fileChooser.getSelectedFile());
          abstractSaver.setDestination(this.fileChooser.getSelectedFile());
          abstractSaver.writeBatch();
          arffPanel.setChanged(false);
          setCurrentFilename(str);
        } catch (Exception exception) {
          exception.printStackTrace();
        }  
    } 
  }
  
  private void saveFileAs() {
    ArffPanel arffPanel = getCurrentPanel();
    if (arffPanel == null) {
      System.out.println("nothing selected!");
      return;
    } 
    if (!getCurrentFilename().equals(""))
      try {
        this.fileChooser.setSelectedFile(new File(getCurrentFilename()));
      } catch (Exception exception) {} 
    int i = this.fileChooser.showSaveDialog(this);
    if (i != 0)
      return; 
    arffPanel.setChanged(false);
    setCurrentFilename(this.fileChooser.getSelectedFile().getAbsolutePath());
    saveFile();
  }
  
  private void closeFile() {
    closeFile(true);
  }
  
  private void closeFile(boolean paramBoolean) {
    if (getCurrentIndex() == -1)
      return; 
    if (!saveChanges(paramBoolean))
      return; 
    this.tabbedPane.removeTabAt(getCurrentIndex());
    setFrameTitle();
    System.gc();
  }
  
  private void showProperties() {
    ArffPanel arffPanel = getCurrentPanel();
    if (arffPanel == null)
      return; 
    Instances instances = arffPanel.getInstances();
    if (instances == null)
      return; 
    if (instances.classIndex() < 0)
      instances.setClassIndex(instances.numAttributes() - 1); 
    Vector vector = new Vector();
    vector.add("Filename: " + arffPanel.getFilename());
    vector.add("Relation name: " + instances.relationName());
    vector.add("# of instances: " + instances.numInstances());
    vector.add("# of attributes: " + instances.numAttributes());
    vector.add("Class attribute: " + instances.classAttribute().name());
    vector.add("# of class labels: " + instances.numClasses());
    ListSelectorDialog listSelectorDialog = new ListSelectorDialog(this, new JList(vector));
    listSelectorDialog.showDialog();
  }
  
  private void close() {
    windowClosing((WindowEvent)null);
  }
  
  private void undo() {
    if (!isPanelSelected())
      return; 
    getCurrentPanel().undo();
  }
  
  private void copyContent() {
    if (!isPanelSelected())
      return; 
    getCurrentPanel().copyContent();
  }
  
  private void search() {
    if (!isPanelSelected())
      return; 
    getCurrentPanel().search();
  }
  
  private void clearSearch() {
    if (!isPanelSelected())
      return; 
    getCurrentPanel().clearSearch();
  }
  
  private void deleteAttribute(boolean paramBoolean) {
    if (!isPanelSelected())
      return; 
    if (paramBoolean) {
      getCurrentPanel().deleteAttributes();
    } else {
      getCurrentPanel().deleteAttribute();
    } 
  }
  
  private void deleteInstance(boolean paramBoolean) {
    if (!isPanelSelected())
      return; 
    if (paramBoolean) {
      getCurrentPanel().deleteInstances();
    } else {
      getCurrentPanel().deleteInstance();
    } 
  }
  
  private void sortInstances() {
    if (!isPanelSelected())
      return; 
    getCurrentPanel().sortInstances();
  }
  
  private String showAttributes() {
    if (!isPanelSelected())
      return null; 
    JList jList = new JList(getCurrentPanel().getAttributes());
    ListSelectorDialog listSelectorDialog = new ListSelectorDialog(this, jList);
    int i = listSelectorDialog.showDialog();
    if (i == 0) {
      ArffTableSorter arffTableSorter = (ArffTableSorter)getCurrentPanel().getTable().getModel();
      String str = jList.getSelectedValue().toString();
      int j = arffTableSorter.getAttributeColumn(str);
      JTableHelper.scrollToVisible(getCurrentPanel().getTable(), 0, j);
      getCurrentPanel().getTable().setSelectedColumn(j);
      return str;
    } 
    return null;
  }
  
  private void showValues() {
    String str = showAttributes();
    if (str == null)
      return; 
    ArffTable arffTable = getCurrentPanel().getTable();
    ArffTableSorter arffTableSorter = (ArffTableSorter)arffTable.getModel();
    byte b1 = -1;
    byte b;
    for (b = 0; b < arffTable.getColumnCount(); b++) {
      if (arffTable.getPlainColumnName(b).equals(str)) {
        b1 = b;
        break;
      } 
    } 
    if (b1 == -1)
      return; 
    HashSet hashSet = new HashSet();
    Vector vector = new Vector();
    for (b = 0; b < arffTableSorter.getRowCount(); b++)
      hashSet.add(arffTableSorter.getValueAt(b, b1).toString()); 
    if (hashSet.isEmpty())
      return; 
    Iterator iterator = hashSet.iterator();
    while (iterator.hasNext())
      vector.add(iterator.next()); 
    Collections.sort(vector);
    ListSelectorDialog listSelectorDialog = new ListSelectorDialog(this, new JList(vector));
    listSelectorDialog.showDialog();
  }
  
  public void actionPerformed(ActionEvent paramActionEvent) {
    Object object = paramActionEvent.getSource();
    if (object == this.menuFileOpen) {
      loadFile();
    } else if (object == this.menuFileSave) {
      saveFile();
    } else if (object == this.menuFileSaveAs) {
      saveFileAs();
    } else if (object == this.menuFileClose) {
      closeFile();
    } else if (object == this.menuFileProperties) {
      showProperties();
    } else if (object == this.menuFileExit) {
      close();
    } else if (object == this.menuEditUndo) {
      undo();
    } else if (object == this.menuEditCopy) {
      copyContent();
    } else if (object == this.menuEditSearch) {
      search();
    } else if (object == this.menuEditClearSearch) {
      clearSearch();
    } else if (object == this.menuEditDeleteAttribute) {
      deleteAttribute(false);
    } else if (object == this.menuEditDeleteAttributes) {
      deleteAttribute(true);
    } else if (object == this.menuEditDeleteInstance) {
      deleteInstance(false);
    } else if (object == this.menuEditDeleteInstances) {
      deleteInstance(true);
    } else if (object == this.menuEditSortInstances) {
      sortInstances();
    } else if (object == this.menuViewAttributes) {
      showAttributes();
    } else if (object == this.menuViewValues) {
      showValues();
    } 
    setMenu();
  }
  
  public void stateChanged(ChangeEvent paramChangeEvent) {
    setFrameTitle();
    setMenu();
    if (paramChangeEvent.getSource() instanceof JComponent)
      setTabTitle((JComponent)paramChangeEvent.getSource()); 
  }
  
  public void windowActivated(WindowEvent paramWindowEvent) {}
  
  public void windowClosed(WindowEvent paramWindowEvent) {}
  
  public void windowClosing(WindowEvent paramWindowEvent) {
    while (this.tabbedPane.getTabCount() > 0)
      closeFile(false); 
    if (this.confirmExit) {
      int i = ComponentHelper.showMessageBox(this, "Quit - " + getTitle(), "Do you really want to quit?", 0, 3);
      if (i == 0)
        dispose(); 
    } else {
      dispose();
    } 
  }
  
  public void windowDeactivated(WindowEvent paramWindowEvent) {}
  
  public void windowDeiconified(WindowEvent paramWindowEvent) {}
  
  public void windowIconified(WindowEvent paramWindowEvent) {}
  
  public void windowOpened(WindowEvent paramWindowEvent) {}
  
  public String toString() {
    return getClass().getName();
  }
  
  public static void main(String[] paramArrayOfString) throws Exception {
    (new ArffViewer()).show();
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\gui\arffviewer\ArffViewer.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */