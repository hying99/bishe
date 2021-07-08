package weka.gui.experiment;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Array;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import weka.core.FastVector;
import weka.experiment.Experiment;
import weka.experiment.PropertyNode;
import weka.gui.GenericArrayEditor;
import weka.gui.PropertySelectorDialog;

public class GeneratorPropertyIteratorPanel extends JPanel implements ActionListener {
  protected JButton m_ConfigureBut = new JButton("Select property...");
  
  protected JComboBox m_StatusBox = new JComboBox();
  
  protected GenericArrayEditor m_ArrayEditor = new GenericArrayEditor();
  
  protected Experiment m_Exp;
  
  protected FastVector m_Listeners = new FastVector();
  
  public GeneratorPropertyIteratorPanel() {
    String[] arrayOfString = { "Disabled", "Enabled" };
    DefaultComboBoxModel defaultComboBoxModel = new DefaultComboBoxModel(arrayOfString);
    this.m_StatusBox.setModel(defaultComboBoxModel);
    this.m_StatusBox.setSelectedIndex(0);
    this.m_StatusBox.addActionListener(this);
    this.m_StatusBox.setEnabled(false);
    this.m_ConfigureBut.setEnabled(false);
    this.m_ConfigureBut.addActionListener(this);
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
    jPanel.add(this.m_StatusBox, gridBagConstraints);
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.weightx = 5.0D;
    gridBagConstraints.gridwidth = 1;
    gridBagConstraints.gridheight = 1;
    jPanel.add(this.m_ConfigureBut, gridBagConstraints);
    jPanel.setMaximumSize(new Dimension((jPanel.getMaximumSize()).width, (jPanel.getMinimumSize()).height));
    setBorder(BorderFactory.createTitledBorder("Generator properties"));
    setLayout(new BorderLayout());
    add(jPanel, "North");
    this.m_ArrayEditor.setBorder(BorderFactory.createEtchedBorder());
    this.m_ArrayEditor.addPropertyChangeListener(new PropertyChangeListener(this) {
          private final GeneratorPropertyIteratorPanel this$0;
          
          public void propertyChange(PropertyChangeEvent param1PropertyChangeEvent) {
            System.err.println("Updating experiment property iterator array");
            this.this$0.m_Exp.setPropertyArray(this.this$0.m_ArrayEditor.getValue());
          }
        });
    add((Component)this.m_ArrayEditor, "Center");
  }
  
  public GeneratorPropertyIteratorPanel(Experiment paramExperiment) {
    this();
    setExperiment(paramExperiment);
  }
  
  public boolean getEditorActive() {
    return !(this.m_StatusBox.getSelectedIndex() == 0);
  }
  
  public void setExperiment(Experiment paramExperiment) {
    this.m_Exp = paramExperiment;
    this.m_StatusBox.setEnabled(true);
    this.m_ArrayEditor.setValue(this.m_Exp.getPropertyArray());
    if (this.m_Exp.getPropertyArray() == null) {
      this.m_StatusBox.setSelectedIndex(0);
      this.m_ConfigureBut.setEnabled(false);
    } else {
      this.m_StatusBox.setSelectedIndex(this.m_Exp.getUsePropertyIterator() ? 1 : 0);
      this.m_ConfigureBut.setEnabled(this.m_Exp.getUsePropertyIterator());
    } 
    validate();
  }
  
  protected int selectProperty() {
    PropertySelectorDialog propertySelectorDialog = new PropertySelectorDialog(null, this.m_Exp.getResultProducer());
    propertySelectorDialog.setLocationRelativeTo(this);
    int i = propertySelectorDialog.showDialog();
    if (i == 0) {
      System.err.println("Property Selected");
      PropertyNode[] arrayOfPropertyNode = propertySelectorDialog.getPath();
      Object object = (arrayOfPropertyNode[arrayOfPropertyNode.length - 1]).value;
      PropertyDescriptor propertyDescriptor = (arrayOfPropertyNode[arrayOfPropertyNode.length - 1]).property;
      Class clazz = propertyDescriptor.getPropertyType();
      this.m_Exp.setPropertyPath(arrayOfPropertyNode);
      this.m_Exp.setPropertyArray(Array.newInstance(clazz, 1));
      Array.set(this.m_Exp.getPropertyArray(), 0, object);
      this.m_ArrayEditor.setValue(this.m_Exp.getPropertyArray());
      this.m_ArrayEditor.repaint();
      System.err.println("Set new array to array editor");
    } else {
      System.err.println("Cancelled");
    } 
    return i;
  }
  
  public void actionPerformed(ActionEvent paramActionEvent) {
    if (paramActionEvent.getSource() == this.m_ConfigureBut) {
      selectProperty();
    } else if (paramActionEvent.getSource() == this.m_StatusBox) {
      for (byte b = 0; b < this.m_Listeners.size(); b++) {
        ActionListener actionListener = (ActionListener)this.m_Listeners.elementAt(b);
        actionListener.actionPerformed(new ActionEvent(this, 1001, "Editor status change"));
      } 
      if (this.m_StatusBox.getSelectedIndex() == 0) {
        this.m_Exp.setUsePropertyIterator(false);
        this.m_ConfigureBut.setEnabled(false);
        this.m_ArrayEditor.setEnabled(false);
        this.m_ArrayEditor.setValue(null);
        validate();
      } else {
        if (this.m_Exp.getPropertyArray() == null)
          selectProperty(); 
        if (this.m_Exp.getPropertyArray() == null) {
          this.m_StatusBox.setSelectedIndex(0);
        } else {
          this.m_Exp.setUsePropertyIterator(true);
          this.m_ConfigureBut.setEnabled(true);
          this.m_ArrayEditor.setEnabled(true);
        } 
        validate();
      } 
    } 
  }
  
  public void addActionListener(ActionListener paramActionListener) {
    this.m_Listeners.addElement(paramActionListener);
  }
  
  public static void main(String[] paramArrayOfString) {
    try {
      JFrame jFrame = new JFrame("Generator Property Iterator");
      jFrame.getContentPane().setLayout(new BorderLayout());
      GeneratorPropertyIteratorPanel generatorPropertyIteratorPanel = new GeneratorPropertyIteratorPanel();
      jFrame.getContentPane().add(generatorPropertyIteratorPanel, "Center");
      jFrame.addWindowListener(new WindowAdapter(jFrame) {
            private final JFrame val$jf;
            
            public void windowClosing(WindowEvent param1WindowEvent) {
              this.val$jf.dispose();
              System.exit(0);
            }
          });
      jFrame.pack();
      jFrame.setVisible(true);
      System.err.println("Short nap");
      Thread.currentThread();
      Thread.sleep(3000L);
      System.err.println("Done");
      generatorPropertyIteratorPanel.setExperiment(new Experiment());
    } catch (Exception exception) {
      exception.printStackTrace();
      System.err.println(exception.getMessage());
    } 
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\gui\experiment\GeneratorPropertyIteratorPanel.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */