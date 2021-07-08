package weka.gui;

import java.beans.PropertyEditor;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;

class PropertyValueSelector extends JComboBox {
  PropertyEditor m_Editor;
  
  PropertyValueSelector(PropertyEditor paramPropertyEditor) {
    this.m_Editor = paramPropertyEditor;
    String str = this.m_Editor.getAsText();
    String[] arrayOfString = this.m_Editor.getTags();
    DefaultComboBoxModel defaultComboBoxModel = new DefaultComboBoxModel(this, (Object[])arrayOfString) {
        private final PropertyValueSelector this$0;
        
        public Object getSelectedItem() {
          return this.this$0.m_Editor.getAsText();
        }
        
        public void setSelectedItem(Object param1Object) {
          this.this$0.m_Editor.setAsText((String)param1Object);
        }
      };
    setModel(defaultComboBoxModel);
    setSelectedItem(str);
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\gui\PropertyValueSelector.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */