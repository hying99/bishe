package weka.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyEditor;
import javax.swing.BorderFactory;
import javax.swing.JPanel;

public class PropertyPanel extends JPanel {
  private PropertyEditor m_Editor;
  
  private PropertyDialog m_PD;
  
  private boolean m_HasCustomPanel = false;
  
  public PropertyPanel(PropertyEditor paramPropertyEditor) {
    this(paramPropertyEditor, false);
  }
  
  public PropertyPanel(PropertyEditor paramPropertyEditor, boolean paramBoolean) {
    this.m_Editor = paramPropertyEditor;
    if (!paramBoolean && this.m_Editor instanceof CustomPanelSupplier) {
      setLayout(new BorderLayout());
      add(((CustomPanelSupplier)this.m_Editor).getCustomPanel(), "Center");
      this.m_HasCustomPanel = true;
    } else {
      createDefaultPanel();
    } 
  }
  
  protected void createDefaultPanel() {
    setBorder(BorderFactory.createEtchedBorder());
    setToolTipText("Click to edit properties for this object");
    setOpaque(true);
    addMouseListener(new MouseAdapter(this) {
          private final PropertyPanel this$0;
          
          public void mouseClicked(MouseEvent param1MouseEvent) {
            this.this$0.showPropertyDialog();
          }
        });
    Dimension dimension = getPreferredSize();
    dimension.height = getFontMetrics(getFont()).getHeight() * 5 / 4;
    dimension.width = dimension.height * 5;
    setPreferredSize(dimension);
    this.m_Editor.addPropertyChangeListener(new PropertyChangeListener(this) {
          private final PropertyPanel this$0;
          
          public void propertyChange(PropertyChangeEvent param1PropertyChangeEvent) {
            this.this$0.repaint();
          }
        });
  }
  
  public void showPropertyDialog() {
    if (this.m_Editor.getValue() != null)
      if (this.m_PD == null) {
        int i = (getLocationOnScreen()).x;
        int j = (getLocationOnScreen()).y;
        this.m_PD = new PropertyDialog(this.m_Editor, i, j);
      } else {
        this.m_PD.setVisible(true);
      }  
  }
  
  public void removeNotify() {
    super.removeNotify();
    if (this.m_PD != null) {
      this.m_PD.dispose();
      this.m_PD = null;
    } 
  }
  
  public void paintComponent(Graphics paramGraphics) {
    if (!this.m_HasCustomPanel) {
      Insets insets = getInsets();
      Rectangle rectangle = new Rectangle(insets.left, insets.top, (getSize()).width - insets.left - insets.right - 1, (getSize()).height - insets.top - insets.bottom - 1);
      paramGraphics.clearRect(insets.left, insets.top, (getSize()).width - insets.right - insets.left, (getSize()).height - insets.bottom - insets.top);
      this.m_Editor.paintValue(paramGraphics, rectangle);
    } 
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\gui\PropertyPanel.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */