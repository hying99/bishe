package weka.gui;

import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

public class DatabaseConnectionDialog extends JDialog {
  protected JTextField m_DbaseURLText;
  
  protected JLabel m_DbaseURLLab;
  
  protected JTextField m_UserNameText;
  
  protected JLabel m_UserNameLab;
  
  protected JPasswordField m_PasswordText;
  
  protected JLabel m_PasswordLab;
  
  protected int m_returnValue;
  
  public DatabaseConnectionDialog(Frame paramFrame) {
    super(paramFrame, "Database Connection Parameters", true);
    DbConnectionDialog("", "");
  }
  
  public DatabaseConnectionDialog(Frame paramFrame, String paramString1, String paramString2) {
    super(paramFrame, "Database Connection Parameters", true);
    DbConnectionDialog(paramString1, paramString2);
  }
  
  public String getURL() {
    return this.m_DbaseURLText.getText();
  }
  
  public String getUsername() {
    return this.m_UserNameText.getText();
  }
  
  public String getPassword() {
    return new String(this.m_PasswordText.getPassword());
  }
  
  public int getReturnValue() {
    return this.m_returnValue;
  }
  
  public void DbConnectionDialog(String paramString1, String paramString2) {
    JPanel jPanel1 = new JPanel();
    jPanel1.setLayout(new GridLayout(4, 1));
    this.m_DbaseURLText = new JTextField(paramString1, 50);
    this.m_DbaseURLLab = new JLabel(" Database URL:", 2);
    this.m_DbaseURLLab.setFont(new Font("Monospaced", 0, 12));
    this.m_UserNameText = new JTextField(paramString2, 25);
    this.m_UserNameLab = new JLabel(" Username:    ", 2);
    this.m_UserNameLab.setFont(new Font("Monospaced", 0, 12));
    this.m_PasswordText = new JPasswordField(25);
    this.m_PasswordLab = new JLabel(" Password:    ", 2);
    this.m_PasswordLab.setFont(new Font("Monospaced", 0, 12));
    JPanel jPanel2 = new JPanel();
    jPanel2.setLayout(new FlowLayout(0));
    jPanel2.add(this.m_DbaseURLLab);
    jPanel2.add(this.m_DbaseURLText);
    jPanel1.add(jPanel2);
    JPanel jPanel3 = new JPanel();
    jPanel3.setLayout(new FlowLayout(0));
    jPanel3.add(this.m_UserNameLab);
    jPanel3.add(this.m_UserNameText);
    jPanel1.add(jPanel3);
    JPanel jPanel4 = new JPanel();
    jPanel4.setLayout(new FlowLayout(0));
    jPanel4.add(this.m_PasswordLab);
    jPanel4.add(this.m_PasswordText);
    jPanel1.add(jPanel4);
    JPanel jPanel5 = new JPanel();
    jPanel5.setLayout(new FlowLayout());
    JButton jButton1;
    jPanel5.add(jButton1 = new JButton("OK"));
    JButton jButton2;
    jPanel5.add(jButton2 = new JButton("Cancel"));
    jButton1.addActionListener(new ActionListener(this) {
          private final DatabaseConnectionDialog this$0;
          
          public void actionPerformed(ActionEvent param1ActionEvent) {
            this.this$0.m_returnValue = 0;
            this.this$0.dispose();
          }
        });
    jButton2.addActionListener(new ActionListener(this) {
          private final DatabaseConnectionDialog this$0;
          
          public void actionPerformed(ActionEvent param1ActionEvent) {
            this.this$0.m_returnValue = -1;
            this.this$0.dispose();
          }
        });
    jPanel1.add(jPanel5);
    getContentPane().add(jPanel1, "Center");
    pack();
    setResizable(false);
  }
  
  public static void main(String[] paramArrayOfString) {
    DatabaseConnectionDialog databaseConnectionDialog = new DatabaseConnectionDialog(null, "URL", "username");
    databaseConnectionDialog.setVisible(true);
    System.out.println(databaseConnectionDialog.getReturnValue() + ":" + databaseConnectionDialog.getUsername() + ":" + databaseConnectionDialog.getPassword() + ":" + databaseConnectionDialog.getURL());
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\gui\DatabaseConnectionDialog.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */