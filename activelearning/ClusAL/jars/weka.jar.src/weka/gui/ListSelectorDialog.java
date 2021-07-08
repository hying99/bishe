package weka.gui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Box;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JScrollPane;

public class ListSelectorDialog extends JDialog {
  protected JButton m_SelectBut = new JButton("Select");
  
  protected JButton m_CancelBut = new JButton("Cancel");
  
  protected JList m_List;
  
  protected int m_Result;
  
  public static final int APPROVE_OPTION = 0;
  
  public static final int CANCEL_OPTION = 1;
  
  public ListSelectorDialog(Frame paramFrame, JList paramJList) {
    super(paramFrame, "Select items", true);
    this.m_List = paramJList;
    this.m_CancelBut.addActionListener(new ActionListener(this) {
          private final ListSelectorDialog this$0;
          
          public void actionPerformed(ActionEvent param1ActionEvent) {
            this.this$0.m_Result = 1;
            this.this$0.setVisible(false);
          }
        });
    this.m_SelectBut.addActionListener(new ActionListener(this) {
          private final ListSelectorDialog this$0;
          
          public void actionPerformed(ActionEvent param1ActionEvent) {
            this.this$0.m_Result = 0;
            this.this$0.setVisible(false);
          }
        });
    Container container = getContentPane();
    container.setLayout(new BorderLayout());
    Box box = new Box(0);
    box.add(this.m_SelectBut);
    box.add(Box.createHorizontalStrut(10));
    box.add(this.m_CancelBut);
    container.add(box, "South");
    container.add(new JScrollPane(this.m_List), "Center");
    pack();
  }
  
  public int showDialog() {
    this.m_Result = 1;
    int[] arrayOfInt = this.m_List.getSelectedIndices();
    setVisible(true);
    if (this.m_Result == 1)
      this.m_List.setSelectedIndices(arrayOfInt); 
    return this.m_Result;
  }
  
  public static void main(String[] paramArrayOfString) {
    try {
      DefaultListModel defaultListModel = new DefaultListModel();
      defaultListModel.addElement("one");
      defaultListModel.addElement("two");
      defaultListModel.addElement("three");
      defaultListModel.addElement("four");
      defaultListModel.addElement("five");
      JList jList = new JList(defaultListModel);
      ListSelectorDialog listSelectorDialog = new ListSelectorDialog(null, jList);
      int i = listSelectorDialog.showDialog();
      if (i == 0) {
        System.err.println("Fields Selected");
        int[] arrayOfInt = jList.getSelectedIndices();
        for (byte b = 0; b < arrayOfInt.length; b++)
          System.err.println("" + arrayOfInt[b] + " " + defaultListModel.elementAt(arrayOfInt[b])); 
      } else {
        System.err.println("Cancelled");
      } 
      System.exit(0);
    } catch (Exception exception) {
      exception.printStackTrace();
      System.err.println(exception.getMessage());
    } 
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\gui\ListSelectorDialog.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */