package weka.gui.experiment;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeListener;
import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import weka.experiment.Experiment;

public class SetupModePanel extends JPanel {
  protected JRadioButton m_SimpleSetupRBut = new JRadioButton("Simple");
  
  protected JRadioButton m_AdvancedSetupRBut = new JRadioButton("Advanced");
  
  protected SimpleSetupPanel m_simplePanel = new SimpleSetupPanel();
  
  protected SetupPanel m_advancedPanel = new SetupPanel();
  
  public SetupModePanel() {
    this.m_simplePanel.setModePanel(this);
    this.m_SimpleSetupRBut.addActionListener(new ActionListener(this) {
          private final SetupModePanel this$0;
          
          public void actionPerformed(ActionEvent param1ActionEvent) {
            this.this$0.switchToSimple((Experiment)null);
          }
        });
    this.m_AdvancedSetupRBut.addActionListener(new ActionListener(this) {
          private final SetupModePanel this$0;
          
          public void actionPerformed(ActionEvent param1ActionEvent) {
            this.this$0.switchToAdvanced((Experiment)null);
          }
        });
    ButtonGroup buttonGroup = new ButtonGroup();
    buttonGroup.add(this.m_SimpleSetupRBut);
    buttonGroup.add(this.m_AdvancedSetupRBut);
    this.m_SimpleSetupRBut.setSelected(true);
    JPanel jPanel1 = new JPanel();
    jPanel1.setLayout(new GridLayout(1, 0));
    jPanel1.add(this.m_SimpleSetupRBut);
    jPanel1.add(this.m_AdvancedSetupRBut);
    JPanel jPanel2 = new JPanel();
    jPanel2.setLayout(new GridLayout(1, 0));
    jPanel2.add(new JLabel("Experiment Configuration Mode:"));
    jPanel2.add(jPanel1);
    setLayout(new BorderLayout());
    add(jPanel2, "North");
    add(this.m_simplePanel, "Center");
  }
  
  public void switchToAdvanced(Experiment paramExperiment) {
    if (paramExperiment == null)
      paramExperiment = this.m_simplePanel.getExperiment(); 
    if (paramExperiment != null) {
      this.m_AdvancedSetupRBut.setSelected(true);
      this.m_advancedPanel.setExperiment(paramExperiment);
    } 
    remove(this.m_simplePanel);
    this.m_simplePanel.removeNotesFrame();
    add(this.m_advancedPanel, "Center");
    validate();
    repaint();
  }
  
  public void switchToSimple(Experiment paramExperiment) {
    if (paramExperiment == null)
      paramExperiment = this.m_advancedPanel.getExperiment(); 
    if (paramExperiment != null && !this.m_simplePanel.setExperiment(paramExperiment)) {
      this.m_AdvancedSetupRBut.setSelected(true);
      switchToAdvanced(paramExperiment);
    } else {
      remove(this.m_advancedPanel);
      this.m_advancedPanel.removeNotesFrame();
      add(this.m_simplePanel, "Center");
      validate();
      repaint();
    } 
  }
  
  public void addPropertyChangeListener(PropertyChangeListener paramPropertyChangeListener) {
    this.m_simplePanel.addPropertyChangeListener(paramPropertyChangeListener);
    this.m_advancedPanel.addPropertyChangeListener(paramPropertyChangeListener);
  }
  
  public Experiment getExperiment() {
    return this.m_SimpleSetupRBut.isSelected() ? this.m_simplePanel.getExperiment() : this.m_advancedPanel.getExperiment();
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\gui\experiment\SetupModePanel.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */