package weka.gui.graphvisualizer;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JRadioButton;
import weka.core.FastVector;

public class HierarchicalBCEngine implements GraphConstants, LayoutEngine {
  protected FastVector m_nodes;
  
  protected FastVector m_edges;
  
  protected FastVector layoutCompleteListeners;
  
  protected int[][] graphMatrix;
  
  protected int[][] nodeLevels;
  
  protected int m_nodeWidth;
  
  protected int m_nodeHeight;
  
  protected JRadioButton m_jRbNaiveLayout;
  
  protected JRadioButton m_jRbPriorityLayout;
  
  protected JRadioButton m_jRbTopdown;
  
  protected JRadioButton m_jRbBottomup;
  
  protected JCheckBox m_jCbEdgeConcentration;
  
  protected JPanel m_controlsPanel;
  
  protected JProgressBar m_progress;
  
  protected boolean m_completeReLayout = false;
  
  private int origNodesSize;
  
  public HierarchicalBCEngine(FastVector paramFastVector1, FastVector paramFastVector2, int paramInt1, int paramInt2) {
    this.m_nodes = paramFastVector1;
    this.m_edges = paramFastVector2;
    this.m_nodeWidth = paramInt1;
    this.m_nodeHeight = paramInt2;
    makeGUIPanel(false);
  }
  
  public HierarchicalBCEngine(FastVector paramFastVector1, FastVector paramFastVector2, int paramInt1, int paramInt2, boolean paramBoolean) {
    this.m_nodes = paramFastVector1;
    this.m_edges = paramFastVector2;
    this.m_nodeWidth = paramInt1;
    this.m_nodeHeight = paramInt2;
    makeGUIPanel(paramBoolean);
  }
  
  public HierarchicalBCEngine() {}
  
  protected void makeGUIPanel(boolean paramBoolean) {
    this.m_jRbNaiveLayout = new JRadioButton("Naive Layout");
    this.m_jRbPriorityLayout = new JRadioButton("Priority Layout");
    ButtonGroup buttonGroup = new ButtonGroup();
    buttonGroup.add(this.m_jRbNaiveLayout);
    buttonGroup.add(this.m_jRbPriorityLayout);
    this.m_jRbPriorityLayout.setSelected(true);
    ActionListener actionListener = new ActionListener(this) {
        private final HierarchicalBCEngine this$0;
        
        public void actionPerformed(ActionEvent param1ActionEvent) {
          this.this$0.m_completeReLayout = true;
        }
      };
    this.m_jRbTopdown = new JRadioButton("Top Down");
    this.m_jRbBottomup = new JRadioButton("Bottom Up");
    this.m_jRbTopdown.addActionListener(actionListener);
    this.m_jRbBottomup.addActionListener(actionListener);
    buttonGroup = new ButtonGroup();
    buttonGroup.add(this.m_jRbTopdown);
    buttonGroup.add(this.m_jRbBottomup);
    this.m_jRbBottomup.setSelected(true);
    this.m_jCbEdgeConcentration = new JCheckBox("With Edge Concentration", paramBoolean);
    this.m_jCbEdgeConcentration.setSelected(paramBoolean);
    this.m_jCbEdgeConcentration.addActionListener(actionListener);
    JPanel jPanel1 = new JPanel(new GridBagLayout());
    GridBagConstraints gridBagConstraints = new GridBagConstraints();
    gridBagConstraints.gridwidth = 0;
    gridBagConstraints.anchor = 18;
    gridBagConstraints.weightx = 1.0D;
    gridBagConstraints.fill = 2;
    jPanel1.add(this.m_jRbNaiveLayout, gridBagConstraints);
    jPanel1.add(this.m_jRbPriorityLayout, gridBagConstraints);
    jPanel1.setBorder(BorderFactory.createTitledBorder("Layout Type"));
    JPanel jPanel2 = new JPanel(new GridBagLayout());
    jPanel2.add(this.m_jRbTopdown, gridBagConstraints);
    jPanel2.add(this.m_jRbBottomup, gridBagConstraints);
    jPanel2.setBorder(BorderFactory.createTitledBorder("Layout Method"));
    this.m_progress = new JProgressBar(0, 11);
    this.m_progress.setBorderPainted(false);
    this.m_progress.setStringPainted(true);
    this.m_progress.setString("");
    this.m_progress.setValue(0);
    this.m_controlsPanel = new JPanel(new GridBagLayout());
    this.m_controlsPanel.add(jPanel1, gridBagConstraints);
    this.m_controlsPanel.add(jPanel2, gridBagConstraints);
    this.m_controlsPanel.add(this.m_jCbEdgeConcentration, gridBagConstraints);
  }
  
  public JPanel getControlPanel() {
    return this.m_controlsPanel;
  }
  
  public JProgressBar getProgressBar() {
    return this.m_progress;
  }
  
  public void setNodesEdges(FastVector paramFastVector1, FastVector paramFastVector2) {
    this.m_nodes = paramFastVector1;
    this.m_edges = paramFastVector2;
  }
  
  public void setNodeSize(int paramInt1, int paramInt2) {
    this.m_nodeWidth = paramInt1;
    this.m_nodeHeight = paramInt2;
  }
  
  public void addLayoutCompleteEventListener(LayoutCompleteEventListener paramLayoutCompleteEventListener) {
    if (this.layoutCompleteListeners == null)
      this.layoutCompleteListeners = new FastVector(); 
    this.layoutCompleteListeners.addElement(paramLayoutCompleteEventListener);
  }
  
  public void removeLayoutCompleteEventListener(LayoutCompleteEventListener paramLayoutCompleteEventListener) {
    if (this.layoutCompleteListeners != null) {
      for (byte b = 0; b < this.layoutCompleteListeners.size(); b++) {
        LayoutCompleteEventListener layoutCompleteEventListener = (LayoutCompleteEventListener)this.layoutCompleteListeners.elementAt(b);
        if (layoutCompleteEventListener == paramLayoutCompleteEventListener) {
          this.layoutCompleteListeners.removeElementAt(b);
          return;
        } 
      } 
      System.err.println("layoutCompleteListener to be remove not present");
    } else {
      System.err.println("layoutCompleteListener to be remove not present");
    } 
  }
  
  public void fireLayoutCompleteEvent(LayoutCompleteEvent paramLayoutCompleteEvent) {
    if (this.layoutCompleteListeners != null && this.layoutCompleteListeners.size() != 0)
      for (byte b = 0; b < this.layoutCompleteListeners.size(); b++) {
        LayoutCompleteEventListener layoutCompleteEventListener = (LayoutCompleteEventListener)this.layoutCompleteListeners.elementAt(b);
        layoutCompleteEventListener.layoutCompleted(paramLayoutCompleteEvent);
      }  
  }
  
  public void layoutGraph() {
    if (this.m_nodes == null || this.m_edges == null)
      return; 
    Thread thread = new Thread(this) {
        private final HierarchicalBCEngine this$0;
        
        public void run() {
          this.this$0.m_progress.setBorderPainted(true);
          if (this.this$0.nodeLevels == null) {
            this.this$0.makeProperHierarchy();
          } else if (this.this$0.m_completeReLayout == true) {
            this.this$0.clearTemps_and_EdgesFromNodes();
            this.this$0.makeProperHierarchy();
            this.this$0.m_completeReLayout = false;
          } 
          if (this.this$0.m_jRbTopdown.isSelected()) {
            int i = this.this$0.crossings(this.this$0.nodeLevels);
            int j = 0;
            byte b = 0;
            do {
              this.this$0.m_progress.setValue(b + 4);
              this.this$0.m_progress.setString("Minimizing Crossings: Pass" + (b + 1));
              if (b != 0)
                i = j; 
              this.this$0.nodeLevels = this.this$0.minimizeCrossings(false, this.this$0.nodeLevels);
              j = this.this$0.crossings(this.this$0.nodeLevels);
              b++;
            } while (j < i && b < 6);
          } else {
            int i = this.this$0.crossings(this.this$0.nodeLevels);
            int j = 0;
            byte b = 0;
            do {
              this.this$0.m_progress.setValue(b + 4);
              this.this$0.m_progress.setString("Minimizing Crossings: Pass" + (b + 1));
              if (b != 0)
                i = j; 
              this.this$0.nodeLevels = this.this$0.minimizeCrossings(true, this.this$0.nodeLevels);
              j = this.this$0.crossings(this.this$0.nodeLevels);
              b++;
            } while (j < i && b < 6);
          } 
          this.this$0.m_progress.setValue(10);
          this.this$0.m_progress.setString("Laying out vertices");
          if (this.this$0.m_jRbNaiveLayout.isSelected()) {
            this.this$0.naiveLayout();
          } else {
            this.this$0.priorityLayout1();
          } 
          this.this$0.m_progress.setValue(11);
          this.this$0.m_progress.setString("Layout Complete");
          this.this$0.m_progress.repaint();
          this.this$0.fireLayoutCompleteEvent(new LayoutCompleteEvent(this));
          this.this$0.m_progress.setValue(0);
          this.this$0.m_progress.setString("");
          this.this$0.m_progress.setBorderPainted(false);
        }
      };
    thread.start();
  }
  
  protected void clearTemps_and_EdgesFromNodes() {
    int i = this.m_nodes.size();
    int j;
    for (j = this.origNodesSize; j < i; j++)
      this.m_nodes.removeElementAt(this.origNodesSize); 
    for (j = 0; j < this.m_nodes.size(); j++)
      ((GraphNode)this.m_nodes.elementAt(j)).edges = (int[][])null; 
    this.nodeLevels = (int[][])null;
  }
  
  protected void processGraph() {
    this.origNodesSize = this.m_nodes.size();
    this.graphMatrix = new int[this.m_nodes.size()][this.m_nodes.size()];
    for (byte b = 0; b < this.m_edges.size(); b++)
      this.graphMatrix[((GraphEdge)this.m_edges.elementAt(b)).src][((GraphEdge)this.m_edges.elementAt(b)).dest] = ((GraphEdge)this.m_edges.elementAt(b)).type; 
  }
  
  protected void makeProperHierarchy() {
    processGraph();
    this.m_progress.setValue(1);
    this.m_progress.setString("Removing Cycles");
    removeCycles();
    this.m_progress.setValue(2);
    this.m_progress.setString("Assigning levels to nodes");
    int[] arrayOfInt1 = new int[this.m_nodes.size()];
    boolean bool = false;
    int i;
    for (i = 0; i < this.graphMatrix.length; i++)
      assignLevels(arrayOfInt1, bool, i, 0); 
    for (i = 0; i < arrayOfInt1.length; i++) {
      if (arrayOfInt1[i] == 0) {
        int j = 65536;
        for (byte b3 = 0; b3 < (this.graphMatrix[i]).length; b3++) {
          if (this.graphMatrix[i][b3] == 1 && j > arrayOfInt1[b3])
            j = arrayOfInt1[b3]; 
        } 
        if (j != 65536 && j > 1)
          arrayOfInt1[i] = j - 1; 
      } 
    } 
    i = 0;
    for (byte b1 = 0; b1 < arrayOfInt1.length; b1++) {
      if (arrayOfInt1[b1] > i)
        i = arrayOfInt1[b1]; 
    } 
    int[] arrayOfInt2 = new int[i + 1];
    for (byte b2 = 0; b2 < arrayOfInt1.length; b2++)
      arrayOfInt2[arrayOfInt1[b2]] = arrayOfInt2[arrayOfInt1[b2]] + 1; 
    int[] arrayOfInt3 = new int[i + 1];
    this.nodeLevels = new int[i + 1][];
    byte b;
    for (b = 0; b < arrayOfInt1.length; b = (byte)(b + 1)) {
      if (this.nodeLevels[arrayOfInt1[b]] == null)
        this.nodeLevels[arrayOfInt1[b]] = new int[arrayOfInt2[arrayOfInt1[b]]]; 
      arrayOfInt3[arrayOfInt1[b]] = arrayOfInt3[arrayOfInt1[b]] + 1;
      this.nodeLevels[arrayOfInt1[b]][arrayOfInt3[arrayOfInt1[b]]] = b;
    } 
    this.m_progress.setValue(3);
    this.m_progress.setString("Removing gaps by adding dummy vertices");
    if (this.m_jCbEdgeConcentration.isSelected()) {
      removeGapsWithEdgeConcentration(arrayOfInt1);
    } else {
      removeGaps(arrayOfInt1);
    } 
    for (b = 0; b < this.graphMatrix.length; b++) {
      GraphNode graphNode = (GraphNode)this.m_nodes.elementAt(b);
      byte b3 = 0;
      byte b4;
      for (b4 = 0; b4 < (this.graphMatrix[b]).length; b4++) {
        if (this.graphMatrix[b][b4] != 0)
          b3++; 
      } 
      graphNode.edges = new int[b3][2];
      b4 = 0;
      byte b5 = 0;
      while (b4 < (this.graphMatrix[b]).length) {
        if (this.graphMatrix[b][b4] != 0) {
          graphNode.edges[b5][0] = b4;
          graphNode.edges[b5][1] = this.graphMatrix[b][b4];
          b5++;
        } 
        b4++;
      } 
    } 
  }
  
  private void removeGaps(int[] paramArrayOfint) {
    int i = this.m_nodes.size();
    int j = (this.graphMatrix[0]).length;
    byte b1 = 1;
    for (byte b2 = 0; b2 < i; b2++) {
      for (byte b = 0; b < j; b++) {
        int k = this.graphMatrix.length;
        if (this.graphMatrix[b2][b] > 0)
          if (paramArrayOfint[b] > paramArrayOfint[b2] + 1) {
            int[][] arrayOfInt = new int[this.graphMatrix.length + paramArrayOfint[b] - paramArrayOfint[b2] - 1][this.graphMatrix.length + paramArrayOfint[b] - paramArrayOfint[b2] - 1];
            int m = paramArrayOfint[b2] + 1;
            copyMatrix(this.graphMatrix, arrayOfInt);
            String str = new String("S" + b1++);
            this.m_nodes.addElement(new GraphNode(str, str, 1));
            int[] arrayOfInt1 = new int[(this.nodeLevels[m]).length + 1];
            System.arraycopy(this.nodeLevels[m], 0, arrayOfInt1, 0, (this.nodeLevels[m]).length);
            arrayOfInt1[arrayOfInt1.length - 1] = this.m_nodes.size() - 1;
            this.nodeLevels[m] = arrayOfInt1;
            m++;
            int n;
            for (n = k; n < k + paramArrayOfint[b] - paramArrayOfint[b2] - 1 - 1; n++) {
              String str1 = new String("S" + b1);
              this.m_nodes.addElement(new GraphNode(str1, str1, 1));
              arrayOfInt1 = new int[(this.nodeLevels[m]).length + 1];
              System.arraycopy(this.nodeLevels[m], 0, arrayOfInt1, 0, (this.nodeLevels[m]).length);
              arrayOfInt1[arrayOfInt1.length - 1] = this.m_nodes.size() - 1;
              this.nodeLevels[m++] = arrayOfInt1;
              arrayOfInt[n][n + 1] = arrayOfInt[b2][b];
              b1++;
              if (n > k)
                arrayOfInt[n][n - 1] = -1 * arrayOfInt[b2][b]; 
            } 
            arrayOfInt[n][b] = arrayOfInt[b2][b];
            arrayOfInt[b2][k] = arrayOfInt[b2][b];
            arrayOfInt[k][b2] = -1 * arrayOfInt[b2][b];
            arrayOfInt[b][n] = -1 * arrayOfInt[b2][b];
            if (n > k)
              arrayOfInt[n][n - 1] = -1 * arrayOfInt[b2][b]; 
            arrayOfInt[b2][b] = 0;
            arrayOfInt[b][b2] = 0;
            this.graphMatrix = arrayOfInt;
          } else {
            this.graphMatrix[b][b2] = -1 * this.graphMatrix[b2][b];
          }  
      } 
    } 
  }
  
  private void removeGapsWithEdgeConcentration(int[] paramArrayOfint) {
    int i = this.m_nodes.size();
    int j = (this.graphMatrix[0]).length;
    byte b1 = 1;
    for (byte b2 = 0; b2 < i; b2++) {
      for (byte b = 0; b < j; b++) {
        if (this.graphMatrix[b2][b] > 0)
          if (paramArrayOfint[b] > paramArrayOfint[b2] + 1) {
            int k = paramArrayOfint[b2];
            boolean bool = false;
            int m = i;
            int n = b2;
            while (k < paramArrayOfint[b] - 1) {
              bool = false;
              while (m < this.graphMatrix.length) {
                if (this.graphMatrix[n][m] > 0) {
                  bool = true;
                  break;
                } 
                m++;
              } 
              if (bool) {
                n = m;
                m++;
                k++;
                continue;
              } 
              if (n != b2)
                n = m - 1; 
            } 
            if (((GraphNode)this.m_nodes.elementAt(n)).nodeType == 1)
              ((GraphNode)this.m_nodes.elementAt(n)).nodeType = 2; 
            if (bool) {
              this.graphMatrix[n][b] = this.graphMatrix[b2][b];
              this.graphMatrix[b][n] = -this.graphMatrix[b2][b];
              this.graphMatrix[b2][b] = 0;
              this.graphMatrix[b][b2] = 0;
            } else {
              int i1 = this.graphMatrix.length;
              int[][] arrayOfInt = new int[this.graphMatrix.length + paramArrayOfint[b] - paramArrayOfint[n] - 1][this.graphMatrix.length + paramArrayOfint[b] - paramArrayOfint[n] - 1];
              int i2 = paramArrayOfint[n] + 1;
              copyMatrix(this.graphMatrix, arrayOfInt);
              String str = new String("S" + b1++);
              this.m_nodes.addElement(new GraphNode(str, str, 1));
              int[] arrayOfInt1 = new int[(this.nodeLevels[i2]).length + 1];
              System.arraycopy(this.nodeLevels[i2], 0, arrayOfInt1, 0, (this.nodeLevels[i2]).length);
              arrayOfInt1[arrayOfInt1.length - 1] = this.m_nodes.size() - 1;
              this.nodeLevels[i2] = arrayOfInt1;
              arrayOfInt1 = new int[this.m_nodes.size() + 1];
              System.arraycopy(paramArrayOfint, 0, arrayOfInt1, 0, paramArrayOfint.length);
              arrayOfInt1[this.m_nodes.size() - 1] = i2;
              paramArrayOfint = arrayOfInt1;
              i2++;
              int i3;
              for (i3 = i1; i3 < i1 + paramArrayOfint[b] - paramArrayOfint[n] - 1 - 1; i3++) {
                String str1 = new String("S" + b1++);
                this.m_nodes.addElement(new GraphNode(str1, str1, 1));
                arrayOfInt1 = new int[(this.nodeLevels[i2]).length + 1];
                System.arraycopy(this.nodeLevels[i2], 0, arrayOfInt1, 0, (this.nodeLevels[i2]).length);
                arrayOfInt1[arrayOfInt1.length - 1] = this.m_nodes.size() - 1;
                this.nodeLevels[i2] = arrayOfInt1;
                arrayOfInt1 = new int[this.m_nodes.size() + 1];
                System.arraycopy(paramArrayOfint, 0, arrayOfInt1, 0, paramArrayOfint.length);
                arrayOfInt1[this.m_nodes.size() - 1] = i2;
                paramArrayOfint = arrayOfInt1;
                i2++;
                arrayOfInt[i3][i3 + 1] = arrayOfInt[b2][b];
                if (i3 > i1)
                  arrayOfInt[i3][i3 - 1] = -1 * arrayOfInt[b2][b]; 
              } 
              arrayOfInt[i3][b] = arrayOfInt[b2][b];
              arrayOfInt[n][i1] = arrayOfInt[b2][b];
              arrayOfInt[i1][n] = -1 * arrayOfInt[b2][b];
              arrayOfInt[b][i3] = -1 * arrayOfInt[b2][b];
              if (i3 > i1)
                arrayOfInt[i3][i3 - 1] = -1 * arrayOfInt[b2][b]; 
              arrayOfInt[b2][b] = 0;
              arrayOfInt[b][b2] = 0;
              this.graphMatrix = arrayOfInt;
            } 
          } else {
            this.graphMatrix[b][b2] = -1 * this.graphMatrix[b2][b];
          }  
      } 
    } 
  }
  
  private int indexOfElementInLevel(int paramInt, int[] paramArrayOfint) throws Exception {
    for (byte b = 0; b < paramArrayOfint.length; b++) {
      if (paramArrayOfint[b] == paramInt)
        return b; 
    } 
    throw new Exception("Error. Didn't find element " + ((GraphNode)this.m_nodes.elementAt(paramInt)).ID + " in level. Inspect code for " + "weka.gui.graphvisualizer.HierarchicalBCEngine");
  }
  
  protected int crossings(int[][] paramArrayOfint) {
    int i = 0;
    for (byte b = 0; b < paramArrayOfint.length - 1; b++) {
      MyList myList1 = new MyList();
      MyList myList2 = new MyList();
      MyListNode[] arrayOfMyListNode = new MyListNode[this.m_nodes.size()];
      int[] arrayOfInt = new int[this.m_nodes.size()];
      byte b1 = 0;
      byte b2 = 0;
      byte b3 = 0;
      while (b1 < (paramArrayOfint[b]).length + (paramArrayOfint[b + 1]).length) {
        if ((b1 % 2 == 0 && b2 < (paramArrayOfint[b]).length) || b3 >= (paramArrayOfint[b + 1]).length) {
          int j = 0;
          int k = 0;
          int m = 0;
          GraphNode graphNode = (GraphNode)this.m_nodes.elementAt(paramArrayOfint[b][b2]);
          if (arrayOfMyListNode[paramArrayOfint[b][b2]] != null) {
            MyListNode myListNode = new MyListNode(this, -1);
            myListNode.next = myList1.first;
            try {
              do {
                myListNode = myListNode.next;
                if (paramArrayOfint[b][b2] == myListNode.n) {
                  j++;
                  m += k;
                  myList1.remove(myListNode);
                } else {
                  k++;
                } 
              } while (myListNode != arrayOfMyListNode[paramArrayOfint[b][b2]]);
            } catch (NullPointerException nullPointerException) {
              System.out.println("levels[i][uidx]: " + paramArrayOfint[b][b2] + " which is: " + ((GraphNode)this.m_nodes.elementAt(paramArrayOfint[b][b2])).ID + " temp: " + myListNode + " upper.first: " + myList1.first);
              nullPointerException.printStackTrace();
              System.exit(-1);
            } 
            arrayOfMyListNode[paramArrayOfint[b][b2]] = null;
            i = i + j * myList2.size() + m;
          } 
          byte b4;
          for (b4 = 0; b4 < graphNode.edges.length; b4++) {
            if (graphNode.edges[b4][1] > 0)
              try {
                if (indexOfElementInLevel(graphNode.edges[b4][0], paramArrayOfint[b + 1]) >= b2)
                  arrayOfInt[graphNode.edges[b4][0]] = 1; 
              } catch (Exception exception) {
                exception.printStackTrace();
              }  
          } 
          for (b4 = 0; b4 < (paramArrayOfint[b + 1]).length; b4++) {
            if (arrayOfInt[paramArrayOfint[b + 1][b4]] == 1) {
              MyListNode myListNode = new MyListNode(this, paramArrayOfint[b + 1][b4]);
              myList2.add(myListNode);
              arrayOfMyListNode[paramArrayOfint[b + 1][b4]] = myListNode;
              arrayOfInt[paramArrayOfint[b + 1][b4]] = 0;
            } 
          } 
          b2++;
        } else {
          int j = 0;
          int k = 0;
          int m = 0;
          GraphNode graphNode = (GraphNode)this.m_nodes.elementAt(paramArrayOfint[b + 1][b3]);
          if (arrayOfMyListNode[paramArrayOfint[b + 1][b3]] != null) {
            MyListNode myListNode = new MyListNode(this, -1);
            myListNode.next = myList2.first;
            try {
              do {
                myListNode = myListNode.next;
                if (paramArrayOfint[b + 1][b3] == myListNode.n) {
                  j++;
                  m += k;
                  myList2.remove(myListNode);
                } else {
                  k++;
                } 
              } while (myListNode != arrayOfMyListNode[paramArrayOfint[b + 1][b3]]);
            } catch (NullPointerException nullPointerException) {
              System.out.print("levels[i+1][lidx]: " + paramArrayOfint[b + 1][b3] + " which is: " + ((GraphNode)this.m_nodes.elementAt(paramArrayOfint[b + 1][b3])).ID + " temp: " + myListNode);
              System.out.println(" lower.first: " + myList2.first);
              nullPointerException.printStackTrace();
              System.exit(-1);
            } 
            arrayOfMyListNode[paramArrayOfint[b + 1][b3]] = null;
            i = i + j * myList1.size() + m;
          } 
          byte b4;
          for (b4 = 0; b4 < graphNode.edges.length; b4++) {
            if (graphNode.edges[b4][1] < 0)
              try {
                if (indexOfElementInLevel(graphNode.edges[b4][0], paramArrayOfint[b]) > b3)
                  arrayOfInt[graphNode.edges[b4][0]] = 1; 
              } catch (Exception exception) {
                exception.printStackTrace();
              }  
          } 
          for (b4 = 0; b4 < (paramArrayOfint[b]).length; b4++) {
            if (arrayOfInt[paramArrayOfint[b][b4]] == 1) {
              MyListNode myListNode = new MyListNode(this, paramArrayOfint[b][b4]);
              myList1.add(myListNode);
              arrayOfMyListNode[paramArrayOfint[b][b4]] = myListNode;
              arrayOfInt[paramArrayOfint[b][b4]] = 0;
            } 
          } 
          b3++;
        } 
        b1++;
      } 
    } 
    return i;
  }
  
  protected void removeCycles() {
    int[] arrayOfInt = new int[this.m_nodes.size()];
    for (byte b = 0; b < this.graphMatrix.length; b++) {
      if (arrayOfInt[b] == 0) {
        removeCycles2(b, arrayOfInt);
        arrayOfInt[b] = 1;
      } 
    } 
  }
  
  private void removeCycles2(int paramInt, int[] paramArrayOfint) {
    paramArrayOfint[paramInt] = 2;
    for (byte b = 0; b < (this.graphMatrix[paramInt]).length; b++) {
      if (this.graphMatrix[paramInt][b] == 1)
        if (paramArrayOfint[b] == 0) {
          removeCycles2(b, paramArrayOfint);
          paramArrayOfint[b] = 1;
        } else if (paramArrayOfint[b] == 2) {
          if (paramInt == b) {
            this.graphMatrix[paramInt][b] = 0;
          } else if (this.graphMatrix[b][paramInt] == 1) {
            this.graphMatrix[b][paramInt] = 3;
            this.graphMatrix[paramInt][b] = -3;
          } else {
            this.graphMatrix[b][paramInt] = 2;
            this.graphMatrix[paramInt][b] = -2;
          } 
        }  
    } 
  }
  
  protected void assignLevels(int[] paramArrayOfint, int paramInt1, int paramInt2, int paramInt3) {
    if (paramInt2 >= this.graphMatrix.length)
      return; 
    if (paramInt3 >= (this.graphMatrix[paramInt2]).length)
      return; 
    if (this.graphMatrix[paramInt2][paramInt3] <= 0) {
      assignLevels(paramArrayOfint, paramInt1, paramInt2, ++paramInt3);
    } else if (this.graphMatrix[paramInt2][paramInt3] == 1 || this.graphMatrix[paramInt2][paramInt3] == 3) {
      if (paramInt1 + 1 > paramArrayOfint[paramInt3]) {
        paramArrayOfint[paramInt3] = paramInt1 + 1;
        assignLevels(paramArrayOfint, paramInt1 + 1, paramInt3, 0);
      } 
      assignLevels(paramArrayOfint, paramInt1, paramInt2, ++paramInt3);
    } 
  }
  
  private int[][] minimizeCrossings(boolean paramBoolean, int[][] paramArrayOfint) {
    if (!paramBoolean) {
      for (byte b1 = 0; b1 < 1; b1++) {
        int[][] arrayOfInt = new int[paramArrayOfint.length][];
        copy2DArray(paramArrayOfint, arrayOfInt);
        int i;
        for (i = 0; i < paramArrayOfint.length - 1; i++)
          phaseID(i, arrayOfInt); 
        if (crossings(arrayOfInt) < crossings(paramArrayOfint))
          paramArrayOfint = arrayOfInt; 
        arrayOfInt = new int[paramArrayOfint.length][];
        copy2DArray(paramArrayOfint, arrayOfInt);
        for (i = paramArrayOfint.length - 2; i >= 0; i--)
          phaseIU(i, arrayOfInt); 
        if (crossings(arrayOfInt) < crossings(paramArrayOfint))
          paramArrayOfint = arrayOfInt; 
        arrayOfInt = new int[paramArrayOfint.length][];
        copy2DArray(paramArrayOfint, arrayOfInt);
        for (i = 0; i < paramArrayOfint.length - 1; i++)
          phaseIID(i, arrayOfInt); 
        if (crossings(arrayOfInt) < crossings(paramArrayOfint))
          paramArrayOfint = arrayOfInt; 
        arrayOfInt = new int[paramArrayOfint.length][];
        copy2DArray(paramArrayOfint, arrayOfInt);
        for (i = paramArrayOfint.length - 2; i >= 0; i--)
          phaseIIU(i, arrayOfInt); 
        if (crossings(arrayOfInt) < crossings(paramArrayOfint))
          paramArrayOfint = arrayOfInt; 
      } 
      return paramArrayOfint;
    } 
    for (byte b = 0; b < 1; b++) {
      int[][] arrayOfInt = new int[paramArrayOfint.length][];
      copy2DArray(paramArrayOfint, arrayOfInt);
      int i;
      for (i = paramArrayOfint.length - 2; i >= 0; i--)
        phaseIU(i, arrayOfInt); 
      if (crossings(arrayOfInt) < crossings(paramArrayOfint))
        paramArrayOfint = arrayOfInt; 
      arrayOfInt = new int[paramArrayOfint.length][];
      copy2DArray(paramArrayOfint, arrayOfInt);
      for (i = 0; i < paramArrayOfint.length - 1; i++)
        phaseID(i, arrayOfInt); 
      if (crossings(arrayOfInt) < crossings(paramArrayOfint))
        paramArrayOfint = arrayOfInt; 
      arrayOfInt = new int[paramArrayOfint.length][];
      copy2DArray(paramArrayOfint, arrayOfInt);
      for (i = paramArrayOfint.length - 2; i >= 0; i--)
        phaseIIU(i, arrayOfInt); 
      if (crossings(arrayOfInt) < crossings(paramArrayOfint))
        paramArrayOfint = arrayOfInt; 
      arrayOfInt = new int[paramArrayOfint.length][];
      copy2DArray(paramArrayOfint, arrayOfInt);
      for (i = 0; i < paramArrayOfint.length - 1; i++)
        phaseIID(i, arrayOfInt); 
      if (crossings(arrayOfInt) < crossings(paramArrayOfint))
        paramArrayOfint = arrayOfInt; 
    } 
    return paramArrayOfint;
  }
  
  protected void phaseID(int paramInt, int[][] paramArrayOfint) {
    float[] arrayOfFloat = calcColBC(paramInt, paramArrayOfint);
    isort(paramArrayOfint[paramInt + 1], arrayOfFloat);
  }
  
  public void phaseIU(int paramInt, int[][] paramArrayOfint) {
    float[] arrayOfFloat = calcRowBC(paramInt, paramArrayOfint);
    isort(paramArrayOfint[paramInt], arrayOfFloat);
  }
  
  public void phaseIID(int paramInt, int[][] paramArrayOfint) {
    float[] arrayOfFloat = calcColBC(paramInt, paramArrayOfint);
    for (byte b = 0; b < arrayOfFloat.length - 1; b++) {
      if (arrayOfFloat[b] == arrayOfFloat[b + 1]) {
        int[][] arrayOfInt = new int[paramArrayOfint.length][];
        copy2DArray(paramArrayOfint, arrayOfInt);
        int i = paramArrayOfint[paramInt + 1][b];
        int j = paramArrayOfint[paramInt + 1][b + 1];
        paramArrayOfint[paramInt + 1][b + 1] = i;
        paramArrayOfint[paramInt + 1][b] = j;
        int k;
        for (k = paramInt + 1; k < paramArrayOfint.length - 1; k++)
          phaseID(k, paramArrayOfint); 
        if (crossings(paramArrayOfint) <= crossings(arrayOfInt)) {
          copy2DArray(paramArrayOfint, arrayOfInt);
        } else {
          copy2DArray(arrayOfInt, paramArrayOfint);
          paramArrayOfint[paramInt + 1][b + 1] = i;
          paramArrayOfint[paramInt + 1][b] = j;
        } 
        for (k = paramArrayOfint.length - 2; k >= 0; k--)
          phaseIU(k, paramArrayOfint); 
        if (crossings(arrayOfInt) < crossings(paramArrayOfint))
          copy2DArray(arrayOfInt, paramArrayOfint); 
      } 
    } 
  }
  
  public void phaseIIU(int paramInt, int[][] paramArrayOfint) {
    float[] arrayOfFloat = calcRowBC(paramInt, paramArrayOfint);
    for (byte b = 0; b < arrayOfFloat.length - 1; b++) {
      if (arrayOfFloat[b] == arrayOfFloat[b + 1]) {
        int[][] arrayOfInt = new int[paramArrayOfint.length][];
        copy2DArray(paramArrayOfint, arrayOfInt);
        int i = paramArrayOfint[paramInt][b];
        int j = paramArrayOfint[paramInt][b + 1];
        paramArrayOfint[paramInt][b + 1] = i;
        paramArrayOfint[paramInt][b] = j;
        int k;
        for (k = paramInt - 1; k >= 0; k--)
          phaseIU(k, paramArrayOfint); 
        if (crossings(paramArrayOfint) <= crossings(arrayOfInt)) {
          copy2DArray(paramArrayOfint, arrayOfInt);
        } else {
          copy2DArray(arrayOfInt, paramArrayOfint);
          paramArrayOfint[paramInt][b + 1] = i;
          paramArrayOfint[paramInt][b] = j;
        } 
        for (k = 0; k < paramArrayOfint.length - 1; k++)
          phaseID(k, paramArrayOfint); 
        if (crossings(arrayOfInt) <= crossings(paramArrayOfint))
          copy2DArray(arrayOfInt, paramArrayOfint); 
      } 
    } 
  }
  
  protected float[] calcRowBC(int paramInt, int[][] paramArrayOfint) {
    float[] arrayOfFloat = new float[(paramArrayOfint[paramInt]).length];
    for (byte b = 0; b < (paramArrayOfint[paramInt]).length; b++) {
      byte b1 = 0;
      GraphNode graphNode = (GraphNode)this.m_nodes.elementAt(paramArrayOfint[paramInt][b]);
      for (byte b2 = 0; b2 < graphNode.edges.length; b2++) {
        if (graphNode.edges[b2][1] > 0) {
          b1++;
          try {
            arrayOfFloat[b] = arrayOfFloat[b] + indexOfElementInLevel(graphNode.edges[b2][0], paramArrayOfint[paramInt + 1]) + 1.0F;
          } catch (Exception exception) {
            return null;
          } 
        } 
      } 
      if (arrayOfFloat[b] != 0.0F)
        arrayOfFloat[b] = arrayOfFloat[b] / b1; 
    } 
    return arrayOfFloat;
  }
  
  protected float[] calcColBC(int paramInt, int[][] paramArrayOfint) {
    float[] arrayOfFloat = new float[(paramArrayOfint[paramInt + 1]).length];
    for (byte b = 0; b < (paramArrayOfint[paramInt + 1]).length; b++) {
      byte b1 = 0;
      GraphNode graphNode = (GraphNode)this.m_nodes.elementAt(paramArrayOfint[paramInt + 1][b]);
      for (byte b2 = 0; b2 < graphNode.edges.length; b2++) {
        if (graphNode.edges[b2][1] < 1) {
          b1++;
          try {
            arrayOfFloat[b] = arrayOfFloat[b] + indexOfElementInLevel(graphNode.edges[b2][0], paramArrayOfint[paramInt]) + 1.0F;
          } catch (Exception exception) {
            return null;
          } 
        } 
      } 
      if (arrayOfFloat[b] != 0.0F)
        arrayOfFloat[b] = arrayOfFloat[b] / b1; 
    } 
    return arrayOfFloat;
  }
  
  protected void printMatrices(int[][] paramArrayOfint) {
    byte b = 0;
    for (b = 0; b < paramArrayOfint.length - 1; b++) {
      float[] arrayOfFloat1 = null;
      float[] arrayOfFloat2 = null;
      try {
        arrayOfFloat1 = calcRowBC(b, paramArrayOfint);
        arrayOfFloat2 = calcColBC(b, paramArrayOfint);
      } catch (NullPointerException nullPointerException) {
        System.out.println("i: " + b + " levels.length: " + paramArrayOfint.length);
        nullPointerException.printStackTrace();
        return;
      } 
      System.out.print("\nM" + (b + 1) + "\t");
      byte b1;
      for (b1 = 0; b1 < (paramArrayOfint[b + 1]).length; b1++)
        System.out.print(((GraphNode)this.m_nodes.elementAt(paramArrayOfint[b + 1][b1])).ID + " "); 
      System.out.println("");
      for (b1 = 0; b1 < (paramArrayOfint[b]).length; b1++) {
        System.out.print(((GraphNode)this.m_nodes.elementAt(paramArrayOfint[b][b1])).ID + "\t");
        for (byte b2 = 0; b2 < (paramArrayOfint[b + 1]).length; b2++)
          System.out.print(this.graphMatrix[paramArrayOfint[b][b1]][paramArrayOfint[b + 1][b2]] + " "); 
        System.out.println(arrayOfFloat1[b1]);
      } 
      System.out.print("\t");
      for (b1 = 0; b1 < (paramArrayOfint[b + 1]).length; b1++)
        System.out.print(arrayOfFloat2[b1] + " "); 
    } 
    System.out.println("\nAt the end i: " + b + " levels.length: " + paramArrayOfint.length);
  }
  
  protected static void isort(int[] paramArrayOfint, float[] paramArrayOffloat) {
    for (byte b = 0; b < paramArrayOffloat.length - 1; b++) {
      byte b1 = b;
      float f = paramArrayOffloat[b1 + 1];
      int i = paramArrayOfint[b1 + 1];
      if (f != 0.0F) {
        int j = b1 + 1;
        while (b1 > -1 && (f < paramArrayOffloat[b1] || paramArrayOffloat[b1] == 0.0F)) {
          if (paramArrayOffloat[b1] == 0.0F) {
            b1--;
            continue;
          } 
          paramArrayOffloat[j] = paramArrayOffloat[b1];
          paramArrayOfint[j] = paramArrayOfint[b1];
          j = b1;
          b1--;
        } 
        paramArrayOffloat[j] = f;
        paramArrayOfint[j] = i;
      } 
    } 
  }
  
  protected void copyMatrix(int[][] paramArrayOfint1, int[][] paramArrayOfint2) {
    for (byte b = 0; b < paramArrayOfint1.length; b++) {
      for (byte b1 = 0; b1 < (paramArrayOfint1[b]).length; b1++)
        paramArrayOfint2[b][b1] = paramArrayOfint1[b][b1]; 
    } 
  }
  
  protected void copy2DArray(int[][] paramArrayOfint1, int[][] paramArrayOfint2) {
    for (byte b = 0; b < paramArrayOfint1.length; b++) {
      paramArrayOfint2[b] = new int[(paramArrayOfint1[b]).length];
      System.arraycopy(paramArrayOfint1[b], 0, paramArrayOfint2[b], 0, (paramArrayOfint1[b]).length);
    } 
  }
  
  protected void naiveLayout() {
    if (this.nodeLevels == null)
      makeProperHierarchy(); 
    byte b = 0;
    int i = 0;
    while (b < this.nodeLevels.length) {
      for (byte b1 = 0; b1 < (this.nodeLevels[b]).length; b1++) {
        i = this.nodeLevels[b][b1];
        GraphNode graphNode = (GraphNode)this.m_nodes.elementAt(i);
        graphNode.x = b1 * this.m_nodeWidth;
        graphNode.y = b * 3 * this.m_nodeHeight;
      } 
      b++;
    } 
  }
  
  protected int uConnectivity(int paramInt1, int paramInt2) {
    byte b1 = 0;
    for (byte b2 = 0; b2 < (this.nodeLevels[paramInt1 - 1]).length; b2++) {
      if (this.graphMatrix[this.nodeLevels[paramInt1 - 1][b2]][this.nodeLevels[paramInt1][paramInt2]] > 0)
        b1++; 
    } 
    return b1;
  }
  
  protected int lConnectivity(int paramInt1, int paramInt2) {
    byte b1 = 0;
    for (byte b2 = 0; b2 < (this.nodeLevels[paramInt1 + 1]).length; b2++) {
      if (this.graphMatrix[this.nodeLevels[paramInt1][paramInt2]][this.nodeLevels[paramInt1 + 1][b2]] > 0)
        b1++; 
    } 
    return b1;
  }
  
  protected int uBCenter(int paramInt1, int paramInt2, int[] paramArrayOfint) {
    int i = 0;
    for (byte b = 0; b < (this.nodeLevels[paramInt1 - 1]).length; b++) {
      if (this.graphMatrix[this.nodeLevels[paramInt1 - 1][b]][this.nodeLevels[paramInt1][paramInt2]] > 0)
        i += paramArrayOfint[this.nodeLevels[paramInt1 - 1][b]]; 
    } 
    if (i != 0)
      i /= uConnectivity(paramInt1, paramInt2); 
    return i;
  }
  
  protected int lBCenter(int paramInt1, int paramInt2, int[] paramArrayOfint) {
    int i = 0;
    for (byte b = 0; b < (this.nodeLevels[paramInt1 + 1]).length; b++) {
      if (this.graphMatrix[this.nodeLevels[paramInt1][paramInt2]][this.nodeLevels[paramInt1 + 1][b]] > 0)
        i += paramArrayOfint[this.nodeLevels[paramInt1 + 1][b]]; 
    } 
    if (i != 0)
      i /= lConnectivity(paramInt1, paramInt2); 
    return i;
  }
  
  private void tempMethod(int[] paramArrayOfint) {
    int i = paramArrayOfint[0];
    byte b;
    for (b = 0; b < paramArrayOfint.length; b++) {
      if (paramArrayOfint[b] < i)
        i = paramArrayOfint[b]; 
    } 
    if (i < 0) {
      i *= -1;
      for (b = 0; b < paramArrayOfint.length; b++)
        paramArrayOfint[b] = paramArrayOfint[b] + i; 
    } 
    b = 0;
    int j = 0;
    while (b < this.nodeLevels.length) {
      for (byte b1 = 0; b1 < (this.nodeLevels[b]).length; b1++) {
        j = this.nodeLevels[b][b1];
        GraphNode graphNode = (GraphNode)this.m_nodes.elementAt(j);
        graphNode.x = paramArrayOfint[j] * this.m_nodeWidth;
        graphNode.y = b * 3 * this.m_nodeHeight;
      } 
      b++;
    } 
  }
  
  protected void priorityLayout1() {
    int[] arrayOfInt = new int[this.m_nodes.size()];
    byte b1 = 0;
    for (byte b2 = 0; b2 < this.nodeLevels.length; b2++) {
      byte b4 = 0;
      for (byte b5 = 0; b5 < (this.nodeLevels[b2]).length; b5++) {
        arrayOfInt[this.nodeLevels[b2][b5]] = b5;
        b4++;
      } 
      if (b4 > b1)
        b1 = b4; 
    } 
    int i;
    for (i = 1; i < this.nodeLevels.length; i++) {
      int[] arrayOfInt1 = new int[(this.nodeLevels[i]).length];
      int[] arrayOfInt2 = new int[(this.nodeLevels[i]).length];
      for (byte b = 0; b < (this.nodeLevels[i]).length; b++) {
        if (((GraphNode)this.m_nodes.elementAt(this.nodeLevels[i][b])).ID.startsWith("S")) {
          arrayOfInt1[b] = b1 + 1;
        } else {
          arrayOfInt1[b] = uConnectivity(i, b);
        } 
        arrayOfInt2[b] = uBCenter(i, b, arrayOfInt);
      } 
      priorityLayout2(this.nodeLevels[i], arrayOfInt1, arrayOfInt2, arrayOfInt);
    } 
    for (i = this.nodeLevels.length - 2; i >= 0; i--) {
      int[] arrayOfInt1 = new int[(this.nodeLevels[i]).length];
      int[] arrayOfInt2 = new int[(this.nodeLevels[i]).length];
      for (byte b = 0; b < (this.nodeLevels[i]).length; b++) {
        if (((GraphNode)this.m_nodes.elementAt(this.nodeLevels[i][b])).ID.startsWith("S")) {
          arrayOfInt1[b] = b1 + 1;
        } else {
          arrayOfInt1[b] = lConnectivity(i, b);
        } 
        arrayOfInt2[b] = lBCenter(i, b, arrayOfInt);
      } 
      priorityLayout2(this.nodeLevels[i], arrayOfInt1, arrayOfInt2, arrayOfInt);
    } 
    for (i = 2; i < this.nodeLevels.length; i++) {
      int[] arrayOfInt1 = new int[(this.nodeLevels[i]).length];
      int[] arrayOfInt2 = new int[(this.nodeLevels[i]).length];
      for (byte b = 0; b < (this.nodeLevels[i]).length; b++) {
        if (((GraphNode)this.m_nodes.elementAt(this.nodeLevels[i][b])).ID.startsWith("S")) {
          arrayOfInt1[b] = b1 + 1;
        } else {
          arrayOfInt1[b] = uConnectivity(i, b);
        } 
        arrayOfInt2[b] = uBCenter(i, b, arrayOfInt);
      } 
      priorityLayout2(this.nodeLevels[i], arrayOfInt1, arrayOfInt2, arrayOfInt);
    } 
    i = arrayOfInt[0];
    byte b3;
    for (b3 = 0; b3 < arrayOfInt.length; b3++) {
      if (arrayOfInt[b3] < i)
        i = arrayOfInt[b3]; 
    } 
    if (i < 0) {
      i *= -1;
      for (b3 = 0; b3 < arrayOfInt.length; b3++)
        arrayOfInt[b3] = arrayOfInt[b3] + i; 
    } 
    b3 = 0;
    int j = 0;
    while (b3 < this.nodeLevels.length) {
      for (byte b = 0; b < (this.nodeLevels[b3]).length; b++) {
        j = this.nodeLevels[b3][b];
        GraphNode graphNode = (GraphNode)this.m_nodes.elementAt(j);
        graphNode.x = arrayOfInt[j] * this.m_nodeWidth;
        graphNode.y = b3 * 3 * this.m_nodeHeight;
      } 
      b3++;
    } 
  }
  
  private void priorityLayout2(int[] paramArrayOfint1, int[] paramArrayOfint2, int[] paramArrayOfint3, int[] paramArrayOfint4) {
    int[] arrayOfInt = new int[paramArrayOfint2.length];
    arrayOfInt[0] = 0;
    byte b;
    for (b = 0; b < paramArrayOfint2.length - 1; b++) {
      byte b1 = b;
      int i = b + 1;
      while (b1 > -1 && paramArrayOfint2[arrayOfInt[b1]] < paramArrayOfint2[i]) {
        arrayOfInt[b1 + 1] = arrayOfInt[b1];
        b1--;
      } 
      arrayOfInt[++b1] = i;
    } 
    for (b = 0; b < arrayOfInt.length; b++) {
      for (byte b1 = 0; b1 < arrayOfInt.length; b1++) {
        byte b2 = 0;
        byte b3 = 0;
        int i;
        for (i = 0; i < paramArrayOfint2.length; i++) {
          if (paramArrayOfint4[paramArrayOfint1[arrayOfInt[b1]]] > paramArrayOfint4[paramArrayOfint1[i]]) {
            b2++;
          } else if (paramArrayOfint4[paramArrayOfint1[arrayOfInt[b1]]] < paramArrayOfint4[paramArrayOfint1[i]]) {
            b3++;
          } 
        } 
        int[] arrayOfInt1 = new int[b2];
        int[] arrayOfInt2 = new int[b3];
        i = 0;
        byte b4 = 0;
        int j = 0;
        while (i < paramArrayOfint2.length) {
          if (paramArrayOfint4[paramArrayOfint1[arrayOfInt[b1]]] > paramArrayOfint4[paramArrayOfint1[i]]) {
            arrayOfInt1[b4++] = i;
          } else if (paramArrayOfint4[paramArrayOfint1[arrayOfInt[b1]]] < paramArrayOfint4[paramArrayOfint1[i]]) {
            arrayOfInt2[j++] = i;
          } 
          i++;
        } 
        while (Math.abs(paramArrayOfint4[paramArrayOfint1[arrayOfInt[b1]]] - 1 - paramArrayOfint3[arrayOfInt[b1]]) < Math.abs(paramArrayOfint4[paramArrayOfint1[arrayOfInt[b1]]] - paramArrayOfint3[arrayOfInt[b1]])) {
          i = paramArrayOfint4[paramArrayOfint1[arrayOfInt[b1]]];
          b4 = 0;
          for (j = arrayOfInt1.length - 1; j >= 0 && i - paramArrayOfint4[paramArrayOfint1[arrayOfInt1[j]]] <= 1; j--) {
            if (paramArrayOfint2[arrayOfInt[b1]] <= paramArrayOfint2[arrayOfInt1[j]]) {
              b4 = 1;
              break;
            } 
            i = paramArrayOfint4[paramArrayOfint1[arrayOfInt1[j]]];
          } 
          if (b4 != 0)
            break; 
          i = paramArrayOfint4[paramArrayOfint1[arrayOfInt[b1]]] - 1;
          for (j = arrayOfInt1.length - 1; j >= 0; j--) {
            if (i == paramArrayOfint4[paramArrayOfint1[arrayOfInt1[j]]])
              paramArrayOfint4[paramArrayOfint1[arrayOfInt1[j]]] = i = paramArrayOfint4[paramArrayOfint1[arrayOfInt1[j]]] - 1; 
          } 
          paramArrayOfint4[paramArrayOfint1[arrayOfInt[b1]]] = paramArrayOfint4[paramArrayOfint1[arrayOfInt[b1]]] - 1;
        } 
        while (Math.abs(paramArrayOfint4[paramArrayOfint1[arrayOfInt[b1]]] + 1 - paramArrayOfint3[arrayOfInt[b1]]) < Math.abs(paramArrayOfint4[paramArrayOfint1[arrayOfInt[b1]]] - paramArrayOfint3[arrayOfInt[b1]])) {
          i = paramArrayOfint4[paramArrayOfint1[arrayOfInt[b1]]];
          b4 = 0;
          for (j = 0; j < arrayOfInt2.length && paramArrayOfint4[paramArrayOfint1[arrayOfInt2[j]]] - i <= 1; j++) {
            if (paramArrayOfint2[arrayOfInt[b1]] <= paramArrayOfint2[arrayOfInt2[j]]) {
              b4 = 1;
              break;
            } 
            i = paramArrayOfint4[paramArrayOfint1[arrayOfInt2[j]]];
          } 
          if (b4 != 0)
            break; 
          i = paramArrayOfint4[paramArrayOfint1[arrayOfInt[b1]]] + 1;
          for (j = 0; j < arrayOfInt2.length; j++) {
            if (i == paramArrayOfint4[paramArrayOfint1[arrayOfInt2[j]]])
              paramArrayOfint4[paramArrayOfint1[arrayOfInt2[j]]] = i = paramArrayOfint4[paramArrayOfint1[arrayOfInt2[j]]] + 1; 
          } 
          paramArrayOfint4[paramArrayOfint1[arrayOfInt[b1]]] = paramArrayOfint4[paramArrayOfint1[arrayOfInt[b1]]] + 1;
        } 
      } 
    } 
  }
  
  private class MyListNode {
    int n;
    
    MyListNode next;
    
    MyListNode previous;
    
    private final HierarchicalBCEngine this$0;
    
    public MyListNode(HierarchicalBCEngine this$0, int param1Int) {
      this.this$0 = this$0;
      this.n = param1Int;
      this.next = null;
      this.previous = null;
    }
  }
  
  private class MyList {
    int size;
    
    HierarchicalBCEngine.MyListNode first;
    
    HierarchicalBCEngine.MyListNode last;
    
    private final HierarchicalBCEngine this$0;
    
    private MyList(HierarchicalBCEngine this$0) {
      HierarchicalBCEngine.this = HierarchicalBCEngine.this;
      this.first = null;
      this.last = null;
    }
    
    public void add(int param1Int) {
      if (this.first == null) {
        this.first = this.last = new HierarchicalBCEngine.MyListNode(HierarchicalBCEngine.this, param1Int);
      } else if (this.last.next == null) {
        this.last.next = new HierarchicalBCEngine.MyListNode(HierarchicalBCEngine.this, param1Int);
        this.last.next.previous = this.last;
        this.last = this.last.next;
      } else {
        System.err.println("Error shouldn't be in here. Check MyList code");
        this.size--;
      } 
      this.size++;
    }
    
    public void add(HierarchicalBCEngine.MyListNode param1MyListNode) {
      if (this.first == null) {
        this.first = this.last = param1MyListNode;
      } else if (this.last.next == null) {
        this.last.next = param1MyListNode;
        this.last.next.previous = this.last;
        this.last = this.last.next;
      } else {
        System.err.println("Error shouldn't be in here. Check MyList code");
        this.size--;
      } 
      this.size++;
    }
    
    public void remove(HierarchicalBCEngine.MyListNode param1MyListNode) {
      if (param1MyListNode.previous != null)
        param1MyListNode.previous.next = param1MyListNode.next; 
      if (param1MyListNode.next != null)
        param1MyListNode.next.previous = param1MyListNode.previous; 
      if (this.last == param1MyListNode)
        this.last = param1MyListNode.previous; 
      if (this.first == param1MyListNode)
        this.first = param1MyListNode.next; 
      this.size--;
    }
    
    public void remove(int param1Int) {
      HierarchicalBCEngine.MyListNode myListNode;
      for (myListNode = this.first; myListNode != null && myListNode.n != param1Int; myListNode = myListNode.next);
      if (myListNode == null) {
        System.err.println("element " + param1Int + "not present in the list");
        return;
      } 
      if (myListNode.previous != null)
        myListNode.previous.next = myListNode.next; 
      if (myListNode.next != null)
        myListNode.next.previous = myListNode.previous; 
      if (this.last == myListNode)
        this.last = myListNode.previous; 
      if (this.first == myListNode)
        this.first = myListNode.next; 
      this.size--;
    }
    
    public int size() {
      return this.size;
    }
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\gui\graphvisualizer\HierarchicalBCEngine.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */