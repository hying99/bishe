package weka.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import weka.core.Instances;
import weka.gui.arffviewer.ArffPanel;

public class ViewerDialog extends JDialog implements ChangeListener {
  public static final int APPROVE_OPTION = 0;
  
  public static final int CANCEL_OPTION = 1;
  
  protected int m_Result = 1;
  
  protected JButton m_OkButton = new JButton("OK");
  
  protected JButton m_CancelButton = new JButton("Cancel");
  
  protected JButton m_UndoButton = new JButton("Undo");
  
  protected ArffPanel m_ArffPanel = new ArffPanel();
  
  public ViewerDialog(Frame paramFrame) {
    super(paramFrame, true);
    createDialog();
  }
  
  protected void createDialog() {
    setTitle("Viewer");
    getContentPane().setLayout(new BorderLayout());
    this.m_ArffPanel.addChangeListener(this);
    getContentPane().add((Component)this.m_ArffPanel, "Center");
    JPanel jPanel = new JPanel(new FlowLayout(2));
    getContentPane().add(jPanel, "South");
    this.m_UndoButton.addActionListener(new ActionListener(this) {
          private final ViewerDialog this$0;
          
          public void actionPerformed(ActionEvent param1ActionEvent) {
            this.this$0.undo();
          }
        });
    getContentPane().add(jPanel, "South");
    this.m_CancelButton.addActionListener(new ActionListener(this) {
          private final ViewerDialog this$0;
          
          public void actionPerformed(ActionEvent param1ActionEvent) {
            this.this$0.m_Result = 1;
            this.this$0.setVisible(false);
          }
        });
    this.m_OkButton.addActionListener(new ActionListener(this) {
          private final ViewerDialog this$0;
          
          public void actionPerformed(ActionEvent param1ActionEvent) {
            this.this$0.m_Result = 0;
            this.this$0.setVisible(false);
          }
        });
    jPanel.add(this.m_UndoButton);
    jPanel.add(this.m_OkButton);
    jPanel.add(this.m_CancelButton);
    pack();
  }
  
  public void setInstances(Instances paramInstances) {
    this.m_ArffPanel.setInstances(new Instances(paramInstances));
  }
  
  public Instances getInstances() {
    return this.m_ArffPanel.getInstances();
  }
  
  protected void setButtons() {
    this.m_OkButton.setEnabled(true);
    this.m_CancelButton.setEnabled(true);
    this.m_UndoButton.setEnabled(this.m_ArffPanel.canUndo());
  }
  
  private void undo() {
    this.m_ArffPanel.undo();
  }
  
  public void stateChanged(ChangeEvent paramChangeEvent) {
    setButtons();
  }
  
  public int showDialog() {
    this.m_Result = 1;
    setVisible(true);
    setButtons();
    return this.m_Result;
  }
  
  public int showDialog(Instances paramInstances) {
    setInstances(paramInstances);
    return showDialog();
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\gui\ViewerDialog.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */