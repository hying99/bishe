package weka.gui.experiment;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

public class OutputFormatDialog extends JDialog {
  public static final int APPROVE_OPTION = 0;
  
  public static final int CANCEL_OPTION = 1;
  
  protected int m_Result = 1;
  
  protected static final String[] m_OutputFormats = new String[] { "Plain Text", "LaTeX", "CSV" };
  
  protected JComboBox m_OutputFormatComboBox = new JComboBox(m_OutputFormats);
  
  protected JSpinner m_MeanPrecSpinner = new JSpinner();
  
  protected JSpinner m_StdDevPrecSpinner = new JSpinner();
  
  protected JButton m_OkButton = new JButton("OK");
  
  protected JButton m_CancelButton = new JButton("Cancel");
  
  protected boolean m_latexOutput = false;
  
  protected boolean m_csvOutput = false;
  
  protected int m_MeanPrec = 2;
  
  protected int m_StdDevPrec = 2;
  
  public OutputFormatDialog(Frame paramFrame) {
    super(paramFrame, "Output Format...", true);
    createDialog();
  }
  
  protected void createDialog() {
    getContentPane().setLayout(new BorderLayout());
    JPanel jPanel = new JPanel(new GridLayout(3, 2));
    getContentPane().add(jPanel, "Center");
    SpinnerNumberModel spinnerNumberModel = (SpinnerNumberModel)this.m_MeanPrecSpinner.getModel();
    spinnerNumberModel.setMaximum(new Integer(20));
    spinnerNumberModel.setMinimum(new Integer(0));
    spinnerNumberModel = (SpinnerNumberModel)this.m_StdDevPrecSpinner.getModel();
    spinnerNumberModel.setMaximum(new Integer(20));
    spinnerNumberModel.setMinimum(new Integer(0));
    jPanel.add(new JLabel("Mean Precision"));
    jPanel.add(this.m_MeanPrecSpinner);
    jPanel.add(new JLabel("StdDev. Precision"));
    jPanel.add(this.m_StdDevPrecSpinner);
    jPanel.add(new JLabel("Output Format"));
    jPanel.add(this.m_OutputFormatComboBox);
    jPanel = new JPanel(new FlowLayout(2));
    getContentPane().add(jPanel, "South");
    this.m_CancelButton.addActionListener(new ActionListener(this) {
          private final OutputFormatDialog this$0;
          
          public void actionPerformed(ActionEvent param1ActionEvent) {
            this.this$0.m_Result = 1;
            this.this$0.setVisible(false);
          }
        });
    this.m_OkButton.addActionListener(new ActionListener(this) {
          private final OutputFormatDialog this$0;
          
          public void actionPerformed(ActionEvent param1ActionEvent) {
            this.this$0.getData();
            this.this$0.m_Result = 0;
            this.this$0.setVisible(false);
          }
        });
    jPanel.add(this.m_OkButton);
    jPanel.add(this.m_CancelButton);
    pack();
  }
  
  private void setData() {
    this.m_MeanPrecSpinner.setValue(new Integer(this.m_MeanPrec));
    this.m_StdDevPrecSpinner.setValue(new Integer(this.m_StdDevPrec));
    if (getProduceLatex()) {
      this.m_OutputFormatComboBox.setSelectedIndex(1);
    } else if (getProduceCSV()) {
      this.m_OutputFormatComboBox.setSelectedIndex(2);
    } else {
      this.m_OutputFormatComboBox.setSelectedIndex(0);
    } 
  }
  
  private void getData() {
    this.m_MeanPrec = Integer.parseInt(this.m_MeanPrecSpinner.getValue().toString());
    this.m_StdDevPrec = Integer.parseInt(this.m_StdDevPrecSpinner.getValue().toString());
    setProduceLatex((this.m_OutputFormatComboBox.getSelectedIndex() == 1));
    setProduceCSV((this.m_OutputFormatComboBox.getSelectedIndex() == 2));
  }
  
  public void setMeanPrec(int paramInt) {
    this.m_MeanPrec = paramInt;
  }
  
  public int getMeanPrec() {
    return this.m_MeanPrec;
  }
  
  public void setStdDevPrec(int paramInt) {
    this.m_StdDevPrec = paramInt;
  }
  
  public int getStdDevPrec() {
    return this.m_StdDevPrec;
  }
  
  public void setProduceLatex(boolean paramBoolean) {
    this.m_latexOutput = paramBoolean;
    if (this.m_latexOutput)
      setProduceCSV(false); 
  }
  
  public boolean getProduceLatex() {
    return this.m_latexOutput;
  }
  
  public void setProduceCSV(boolean paramBoolean) {
    this.m_csvOutput = paramBoolean;
    if (this.m_csvOutput)
      setProduceLatex(false); 
  }
  
  public boolean getProduceCSV() {
    return this.m_csvOutput;
  }
  
  public int getResult() {
    return this.m_Result;
  }
  
  public int showDialog() {
    this.m_Result = 1;
    setData();
    setVisible(true);
    return this.m_Result;
  }
  
  public static void main(String[] paramArrayOfString) {
    OutputFormatDialog outputFormatDialog = new OutputFormatDialog(null);
    if (outputFormatDialog.showDialog() == 0) {
      System.out.println("Accepted");
    } else {
      System.out.println("Aborted");
    } 
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\gui\experiment\OutputFormatDialog.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */