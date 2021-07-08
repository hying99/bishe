package weka.classifiers.trees;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyEditorManager;
import java.io.Serializable;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;
import weka.classifiers.Classifier;
import weka.classifiers.CostMatrix;
import weka.classifiers.Evaluation;
import weka.classifiers.functions.LinearRegression;
import weka.classifiers.rules.ZeroR;
import weka.core.Drawable;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.SelectedTag;
import weka.core.Utils;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Remove;
import weka.gui.CostMatrixEditor;
import weka.gui.GenericArrayEditor;
import weka.gui.GenericObjectEditor;
import weka.gui.PropertyDialog;
import weka.gui.SelectedTagEditor;
import weka.gui.treevisualizer.NodePlace;
import weka.gui.treevisualizer.PlaceNode1;
import weka.gui.treevisualizer.PlaceNode2;
import weka.gui.treevisualizer.TreeDisplayEvent;
import weka.gui.treevisualizer.TreeDisplayListener;
import weka.gui.treevisualizer.TreeVisualizer;
import weka.gui.visualize.VisualizePanel;
import weka.gui.visualize.VisualizePanelEvent;
import weka.gui.visualize.VisualizePanelListener;

public class UserClassifier extends Classifier implements Drawable, TreeDisplayListener, VisualizePanelListener {
  private static final int LEAF = 0;
  
  private static final int RECTANGLE = 1;
  
  private static final int POLYGON = 2;
  
  private static final int POLYLINE = 3;
  
  private static final int VLINE = 5;
  
  private static final int HLINE = 6;
  
  private transient TreeVisualizer m_tView = null;
  
  private transient VisualizePanel m_iView = null;
  
  private TreeClass m_top = null;
  
  private TreeClass m_focus;
  
  private int m_nextId;
  
  private transient JFrame m_treeFrame;
  
  private transient JFrame m_visFrame;
  
  private transient JTabbedPane m_reps;
  
  private transient JFrame m_mainWin;
  
  private boolean m_built = false;
  
  private GenericObjectEditor m_classifiers;
  
  private PropertyDialog m_propertyDialog;
  
  static Class array$Lweka$classifiers$Classifier;
  
  static Class array$Ljava$lang$Object;
  
  public static void main(String[] paramArrayOfString) {
    try {
      System.out.println(Evaluation.evaluateModel(new UserClassifier(), paramArrayOfString));
    } catch (Exception exception) {
      System.err.println(exception.getMessage());
      exception.printStackTrace();
    } 
    System.exit(0);
  }
  
  public String toString() {
    if (!this.m_built)
      return "Tree Not Built"; 
    StringBuffer stringBuffer = new StringBuffer();
    try {
      this.m_top.toString(0, stringBuffer);
      this.m_top.objectStrings(stringBuffer);
    } catch (Exception exception) {
      System.out.println("error: " + exception.getMessage());
    } 
    return stringBuffer.toString();
  }
  
  public void userCommand(TreeDisplayEvent paramTreeDisplayEvent) {
    // Byte code:
    //   0: aload_0
    //   1: getfield m_propertyDialog : Lweka/gui/PropertyDialog;
    //   4: ifnull -> 19
    //   7: aload_0
    //   8: getfield m_propertyDialog : Lweka/gui/PropertyDialog;
    //   11: invokevirtual dispose : ()V
    //   14: aload_0
    //   15: aconst_null
    //   16: putfield m_propertyDialog : Lweka/gui/PropertyDialog;
    //   19: aload_0
    //   20: getfield m_iView : Lweka/gui/visualize/VisualizePanel;
    //   23: ifnull -> 33
    //   26: aload_0
    //   27: getfield m_tView : Lweka/gui/treevisualizer/TreeVisualizer;
    //   30: ifnonnull -> 33
    //   33: aload_1
    //   34: invokevirtual getCommand : ()I
    //   37: aload_1
    //   38: pop
    //   39: iconst_0
    //   40: if_icmpne -> 46
    //   43: goto -> 702
    //   46: aload_1
    //   47: invokevirtual getCommand : ()I
    //   50: aload_1
    //   51: pop
    //   52: iconst_1
    //   53: if_icmpne -> 223
    //   56: aload_0
    //   57: getfield m_top : Lweka/classifiers/trees/UserClassifier$TreeClass;
    //   60: ifnonnull -> 74
    //   63: getstatic java/lang/System.out : Ljava/io/PrintStream;
    //   66: ldc 'Error : Received event from a TreeDisplayer that is unknown to the classifier.'
    //   68: invokevirtual println : (Ljava/lang/String;)V
    //   71: goto -> 702
    //   74: aload_0
    //   75: getfield m_tView : Lweka/gui/treevisualizer/TreeVisualizer;
    //   78: aload_1
    //   79: invokevirtual getID : ()Ljava/lang/String;
    //   82: invokevirtual setHighlight : (Ljava/lang/String;)V
    //   85: aload_0
    //   86: aload_0
    //   87: getfield m_top : Lweka/classifiers/trees/UserClassifier$TreeClass;
    //   90: aload_1
    //   91: invokevirtual getID : ()Ljava/lang/String;
    //   94: invokevirtual getNode : (Ljava/lang/String;)Lweka/classifiers/trees/UserClassifier$TreeClass;
    //   97: putfield m_focus : Lweka/classifiers/trees/UserClassifier$TreeClass;
    //   100: aload_0
    //   101: getfield m_iView : Lweka/gui/visualize/VisualizePanel;
    //   104: aload_0
    //   105: getfield m_focus : Lweka/classifiers/trees/UserClassifier$TreeClass;
    //   108: getfield m_training : Lweka/core/Instances;
    //   111: invokevirtual setInstances : (Lweka/core/Instances;)V
    //   114: aload_0
    //   115: getfield m_focus : Lweka/classifiers/trees/UserClassifier$TreeClass;
    //   118: getfield m_attrib1 : I
    //   121: iflt -> 138
    //   124: aload_0
    //   125: getfield m_iView : Lweka/gui/visualize/VisualizePanel;
    //   128: aload_0
    //   129: getfield m_focus : Lweka/classifiers/trees/UserClassifier$TreeClass;
    //   132: getfield m_attrib1 : I
    //   135: invokevirtual setXIndex : (I)V
    //   138: aload_0
    //   139: getfield m_focus : Lweka/classifiers/trees/UserClassifier$TreeClass;
    //   142: getfield m_attrib2 : I
    //   145: iflt -> 162
    //   148: aload_0
    //   149: getfield m_iView : Lweka/gui/visualize/VisualizePanel;
    //   152: aload_0
    //   153: getfield m_focus : Lweka/classifiers/trees/UserClassifier$TreeClass;
    //   156: getfield m_attrib2 : I
    //   159: invokevirtual setYIndex : (I)V
    //   162: aload_0
    //   163: getfield m_iView : Lweka/gui/visualize/VisualizePanel;
    //   166: aload_0
    //   167: getfield m_focus : Lweka/classifiers/trees/UserClassifier$TreeClass;
    //   170: getfield m_training : Lweka/core/Instances;
    //   173: invokevirtual classIndex : ()I
    //   176: invokevirtual setColourIndex : (I)V
    //   179: aload_0
    //   180: getfield m_focus : Lweka/classifiers/trees/UserClassifier$TreeClass;
    //   183: getfield m_ranges : Lweka/core/FastVector;
    //   186: iconst_0
    //   187: invokevirtual elementAt : (I)Ljava/lang/Object;
    //   190: checkcast weka/core/FastVector
    //   193: iconst_0
    //   194: invokevirtual elementAt : (I)Ljava/lang/Object;
    //   197: checkcast java/lang/Double
    //   200: invokevirtual intValue : ()I
    //   203: ifeq -> 702
    //   206: aload_0
    //   207: getfield m_iView : Lweka/gui/visualize/VisualizePanel;
    //   210: aload_0
    //   211: getfield m_focus : Lweka/classifiers/trees/UserClassifier$TreeClass;
    //   214: getfield m_ranges : Lweka/core/FastVector;
    //   217: invokevirtual setShapes : (Lweka/core/FastVector;)V
    //   220: goto -> 702
    //   223: aload_1
    //   224: invokevirtual getCommand : ()I
    //   227: aload_1
    //   228: pop
    //   229: iconst_2
    //   230: if_icmpne -> 458
    //   233: aload_0
    //   234: aload_0
    //   235: getfield m_top : Lweka/classifiers/trees/UserClassifier$TreeClass;
    //   238: aload_1
    //   239: invokevirtual getID : ()Ljava/lang/String;
    //   242: invokevirtual getNode : (Ljava/lang/String;)Lweka/classifiers/trees/UserClassifier$TreeClass;
    //   245: putfield m_focus : Lweka/classifiers/trees/UserClassifier$TreeClass;
    //   248: aload_0
    //   249: getfield m_iView : Lweka/gui/visualize/VisualizePanel;
    //   252: aload_0
    //   253: getfield m_focus : Lweka/classifiers/trees/UserClassifier$TreeClass;
    //   256: getfield m_training : Lweka/core/Instances;
    //   259: invokevirtual setInstances : (Lweka/core/Instances;)V
    //   262: aload_0
    //   263: getfield m_focus : Lweka/classifiers/trees/UserClassifier$TreeClass;
    //   266: getfield m_attrib1 : I
    //   269: iflt -> 286
    //   272: aload_0
    //   273: getfield m_iView : Lweka/gui/visualize/VisualizePanel;
    //   276: aload_0
    //   277: getfield m_focus : Lweka/classifiers/trees/UserClassifier$TreeClass;
    //   280: getfield m_attrib1 : I
    //   283: invokevirtual setXIndex : (I)V
    //   286: aload_0
    //   287: getfield m_focus : Lweka/classifiers/trees/UserClassifier$TreeClass;
    //   290: getfield m_attrib2 : I
    //   293: iflt -> 310
    //   296: aload_0
    //   297: getfield m_iView : Lweka/gui/visualize/VisualizePanel;
    //   300: aload_0
    //   301: getfield m_focus : Lweka/classifiers/trees/UserClassifier$TreeClass;
    //   304: getfield m_attrib2 : I
    //   307: invokevirtual setYIndex : (I)V
    //   310: aload_0
    //   311: getfield m_iView : Lweka/gui/visualize/VisualizePanel;
    //   314: aload_0
    //   315: getfield m_focus : Lweka/classifiers/trees/UserClassifier$TreeClass;
    //   318: getfield m_training : Lweka/core/Instances;
    //   321: invokevirtual classIndex : ()I
    //   324: invokevirtual setColourIndex : (I)V
    //   327: aload_0
    //   328: getfield m_focus : Lweka/classifiers/trees/UserClassifier$TreeClass;
    //   331: getfield m_ranges : Lweka/core/FastVector;
    //   334: iconst_0
    //   335: invokevirtual elementAt : (I)Ljava/lang/Object;
    //   338: checkcast weka/core/FastVector
    //   341: iconst_0
    //   342: invokevirtual elementAt : (I)Ljava/lang/Object;
    //   345: checkcast java/lang/Double
    //   348: invokevirtual intValue : ()I
    //   351: ifeq -> 368
    //   354: aload_0
    //   355: getfield m_iView : Lweka/gui/visualize/VisualizePanel;
    //   358: aload_0
    //   359: getfield m_focus : Lweka/classifiers/trees/UserClassifier$TreeClass;
    //   362: getfield m_ranges : Lweka/core/FastVector;
    //   365: invokevirtual setShapes : (Lweka/core/FastVector;)V
    //   368: aload_0
    //   369: getfield m_focus : Lweka/classifiers/trees/UserClassifier$TreeClass;
    //   372: aconst_null
    //   373: putfield m_set1 : Lweka/classifiers/trees/UserClassifier$TreeClass;
    //   376: aload_0
    //   377: getfield m_focus : Lweka/classifiers/trees/UserClassifier$TreeClass;
    //   380: aconst_null
    //   381: putfield m_set2 : Lweka/classifiers/trees/UserClassifier$TreeClass;
    //   384: aload_0
    //   385: getfield m_focus : Lweka/classifiers/trees/UserClassifier$TreeClass;
    //   388: aload_0
    //   389: getfield m_focus : Lweka/classifiers/trees/UserClassifier$TreeClass;
    //   392: getfield m_attrib1 : I
    //   395: aload_0
    //   396: getfield m_focus : Lweka/classifiers/trees/UserClassifier$TreeClass;
    //   399: getfield m_attrib2 : I
    //   402: aconst_null
    //   403: invokevirtual setInfo : (IILweka/core/FastVector;)V
    //   406: aload_0
    //   407: new weka/gui/treevisualizer/TreeVisualizer
    //   410: dup
    //   411: aload_0
    //   412: aload_0
    //   413: invokevirtual graph : ()Ljava/lang/String;
    //   416: new weka/gui/treevisualizer/PlaceNode2
    //   419: dup
    //   420: invokespecial <init> : ()V
    //   423: invokespecial <init> : (Lweka/gui/treevisualizer/TreeDisplayListener;Ljava/lang/String;Lweka/gui/treevisualizer/NodePlace;)V
    //   426: putfield m_tView : Lweka/gui/treevisualizer/TreeVisualizer;
    //   429: aload_0
    //   430: getfield m_reps : Ljavax/swing/JTabbedPane;
    //   433: iconst_0
    //   434: aload_0
    //   435: getfield m_tView : Lweka/gui/treevisualizer/TreeVisualizer;
    //   438: invokevirtual setComponentAt : (ILjava/awt/Component;)V
    //   441: aload_0
    //   442: getfield m_tView : Lweka/gui/treevisualizer/TreeVisualizer;
    //   445: aload_0
    //   446: getfield m_focus : Lweka/classifiers/trees/UserClassifier$TreeClass;
    //   449: getfield m_identity : Ljava/lang/String;
    //   452: invokevirtual setHighlight : (Ljava/lang/String;)V
    //   455: goto -> 702
    //   458: aload_1
    //   459: invokevirtual getCommand : ()I
    //   462: aload_1
    //   463: pop
    //   464: iconst_4
    //   465: if_icmpne -> 655
    //   468: aload_0
    //   469: aload_0
    //   470: getfield m_top : Lweka/classifiers/trees/UserClassifier$TreeClass;
    //   473: aload_1
    //   474: invokevirtual getID : ()Ljava/lang/String;
    //   477: invokevirtual getNode : (Ljava/lang/String;)Lweka/classifiers/trees/UserClassifier$TreeClass;
    //   480: putfield m_focus : Lweka/classifiers/trees/UserClassifier$TreeClass;
    //   483: aload_0
    //   484: getfield m_iView : Lweka/gui/visualize/VisualizePanel;
    //   487: aload_0
    //   488: getfield m_focus : Lweka/classifiers/trees/UserClassifier$TreeClass;
    //   491: getfield m_training : Lweka/core/Instances;
    //   494: invokevirtual setInstances : (Lweka/core/Instances;)V
    //   497: aload_0
    //   498: getfield m_focus : Lweka/classifiers/trees/UserClassifier$TreeClass;
    //   501: getfield m_attrib1 : I
    //   504: iflt -> 521
    //   507: aload_0
    //   508: getfield m_iView : Lweka/gui/visualize/VisualizePanel;
    //   511: aload_0
    //   512: getfield m_focus : Lweka/classifiers/trees/UserClassifier$TreeClass;
    //   515: getfield m_attrib1 : I
    //   518: invokevirtual setXIndex : (I)V
    //   521: aload_0
    //   522: getfield m_focus : Lweka/classifiers/trees/UserClassifier$TreeClass;
    //   525: getfield m_attrib2 : I
    //   528: iflt -> 545
    //   531: aload_0
    //   532: getfield m_iView : Lweka/gui/visualize/VisualizePanel;
    //   535: aload_0
    //   536: getfield m_focus : Lweka/classifiers/trees/UserClassifier$TreeClass;
    //   539: getfield m_attrib2 : I
    //   542: invokevirtual setYIndex : (I)V
    //   545: aload_0
    //   546: getfield m_iView : Lweka/gui/visualize/VisualizePanel;
    //   549: aload_0
    //   550: getfield m_focus : Lweka/classifiers/trees/UserClassifier$TreeClass;
    //   553: getfield m_training : Lweka/core/Instances;
    //   556: invokevirtual classIndex : ()I
    //   559: invokevirtual setColourIndex : (I)V
    //   562: aload_0
    //   563: getfield m_focus : Lweka/classifiers/trees/UserClassifier$TreeClass;
    //   566: getfield m_ranges : Lweka/core/FastVector;
    //   569: iconst_0
    //   570: invokevirtual elementAt : (I)Ljava/lang/Object;
    //   573: checkcast weka/core/FastVector
    //   576: iconst_0
    //   577: invokevirtual elementAt : (I)Ljava/lang/Object;
    //   580: checkcast java/lang/Double
    //   583: invokevirtual intValue : ()I
    //   586: ifeq -> 603
    //   589: aload_0
    //   590: getfield m_iView : Lweka/gui/visualize/VisualizePanel;
    //   593: aload_0
    //   594: getfield m_focus : Lweka/classifiers/trees/UserClassifier$TreeClass;
    //   597: getfield m_ranges : Lweka/core/FastVector;
    //   600: invokevirtual setShapes : (Lweka/core/FastVector;)V
    //   603: aload_0
    //   604: new weka/gui/PropertyDialog
    //   607: dup
    //   608: aload_0
    //   609: getfield m_classifiers : Lweka/gui/GenericObjectEditor;
    //   612: aload_0
    //   613: getfield m_mainWin : Ljavax/swing/JFrame;
    //   616: invokevirtual getLocationOnScreen : ()Ljava/awt/Point;
    //   619: getfield x : I
    //   622: aload_0
    //   623: getfield m_mainWin : Ljavax/swing/JFrame;
    //   626: invokevirtual getLocationOnScreen : ()Ljava/awt/Point;
    //   629: getfield y : I
    //   632: invokespecial <init> : (Ljava/beans/PropertyEditor;II)V
    //   635: putfield m_propertyDialog : Lweka/gui/PropertyDialog;
    //   638: aload_0
    //   639: getfield m_tView : Lweka/gui/treevisualizer/TreeVisualizer;
    //   642: aload_0
    //   643: getfield m_focus : Lweka/classifiers/trees/UserClassifier$TreeClass;
    //   646: getfield m_identity : Ljava/lang/String;
    //   649: invokevirtual setHighlight : (Ljava/lang/String;)V
    //   652: goto -> 702
    //   655: aload_1
    //   656: invokevirtual getCommand : ()I
    //   659: aload_1
    //   660: pop
    //   661: iconst_3
    //   662: if_icmpne -> 702
    //   665: aload_0
    //   666: getfield m_mainWin : Ljavax/swing/JFrame;
    //   669: ldc 'Are You Sure...\\nClick Yes To Accept The Tree\\n Click No To Return'
    //   671: ldc 'Accept Tree'
    //   673: iconst_0
    //   674: invokestatic showConfirmDialog : (Ljava/awt/Component;Ljava/lang/Object;Ljava/lang/String;I)I
    //   677: istore_2
    //   678: iload_2
    //   679: ifne -> 702
    //   682: aload_0
    //   683: getfield m_mainWin : Ljavax/swing/JFrame;
    //   686: iconst_2
    //   687: invokevirtual setDefaultCloseOperation : (I)V
    //   690: aload_0
    //   691: getfield m_mainWin : Ljavax/swing/JFrame;
    //   694: invokevirtual dispose : ()V
    //   697: aload_0
    //   698: iconst_0
    //   699: invokespecial blocker : (Z)V
    //   702: goto -> 743
    //   705: astore_2
    //   706: getstatic java/lang/System.out : Ljava/io/PrintStream;
    //   709: new java/lang/StringBuffer
    //   712: dup
    //   713: invokespecial <init> : ()V
    //   716: ldc 'Error : '
    //   718: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuffer;
    //   721: aload_2
    //   722: invokevirtual append : (Ljava/lang/Object;)Ljava/lang/StringBuffer;
    //   725: invokevirtual toString : ()Ljava/lang/String;
    //   728: invokevirtual println : (Ljava/lang/String;)V
    //   731: getstatic java/lang/System.out : Ljava/io/PrintStream;
    //   734: ldc 'Part of user input so had to catch here'
    //   736: invokevirtual println : (Ljava/lang/String;)V
    //   739: aload_2
    //   740: invokevirtual printStackTrace : ()V
    //   743: return
    // Exception table:
    //   from	to	target	type
    //   19	702	705	java/lang/Exception
  }
  
  public void userDataEvent(VisualizePanelEvent paramVisualizePanelEvent) {
    if (this.m_propertyDialog != null) {
      this.m_propertyDialog.dispose();
      this.m_propertyDialog = null;
    } 
    try {
      if (this.m_focus != null) {
        double d = (paramVisualizePanelEvent.getInstances1().numInstances() + paramVisualizePanelEvent.getInstances2().numInstances());
        if (d == 0.0D)
          d = 1.0D; 
        TreeClass treeClass = this.m_focus;
        this.m_focus.m_set1 = new TreeClass(this, null, paramVisualizePanelEvent.getAttribute1(), paramVisualizePanelEvent.getAttribute2(), this.m_nextId, paramVisualizePanelEvent.getInstances1().numInstances() / d, paramVisualizePanelEvent.getInstances1(), this.m_focus);
        this.m_focus.m_set2 = new TreeClass(this, null, paramVisualizePanelEvent.getAttribute1(), paramVisualizePanelEvent.getAttribute2(), this.m_nextId, paramVisualizePanelEvent.getInstances2().numInstances() / d, paramVisualizePanelEvent.getInstances2(), this.m_focus);
        this.m_focus.setInfo(paramVisualizePanelEvent.getAttribute1(), paramVisualizePanelEvent.getAttribute2(), paramVisualizePanelEvent.getValues());
        this.m_tView = new TreeVisualizer(this, graph(), (NodePlace)new PlaceNode2());
        this.m_reps.setComponentAt(0, (Component)this.m_tView);
        this.m_focus = this.m_focus.m_set2;
        this.m_tView.setHighlight(this.m_focus.m_identity);
        this.m_iView.setInstances(this.m_focus.m_training);
        if (treeClass.m_attrib1 >= 0)
          this.m_iView.setXIndex(treeClass.m_attrib1); 
        if (treeClass.m_attrib2 >= 0)
          this.m_iView.setYIndex(treeClass.m_attrib2); 
        this.m_iView.setColourIndex(this.m_focus.m_training.classIndex());
        if (((Double)((FastVector)this.m_focus.m_ranges.elementAt(0)).elementAt(0)).intValue() != 0)
          this.m_iView.setShapes(this.m_focus.m_ranges); 
      } else {
        System.out.println("Somehow the focus is null");
      } 
    } catch (Exception exception) {
      System.out.println("Error : " + exception);
      System.out.println("Part of user input so had to catch here");
    } 
  }
  
  public UserClassifier() {
    this.m_tView = null;
    this.m_iView = null;
    this.m_nextId = 0;
  }
  
  public int graphType() {
    return 1;
  }
  
  public String graph() throws Exception {
    StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append("digraph UserClassifierTree {\nnode [fontsize=10]\nedge [fontsize=10 style=bold]\n");
    this.m_top.toDotty(stringBuffer);
    return stringBuffer.toString() + "}\n";
  }
  
  private synchronized void blocker(boolean paramBoolean) {
    if (paramBoolean) {
      try {
        wait();
      } catch (InterruptedException interruptedException) {}
    } else {
      notifyAll();
    } 
  }
  
  public String globalInfo() {
    return "Interactively classify through visual means. You are Presented with a scatter graph of the data against two user selectable attributes, as well as a view of the decision tree. You can create binary splits by creating polygons around data plotted on the scatter graph, as well as by allowing another classifier to take over at points in the decision tree should you see fit.";
  }
  
  public void buildClassifier(Instances paramInstances) throws Exception {
    this.m_classifiers = new GenericObjectEditor(true);
    this.m_classifiers.setClassType(Classifier.class);
    this.m_classifiers.setValue(new ZeroR());
    ((GenericObjectEditor.GOEPanel)this.m_classifiers.getCustomEditor()).addOkListener(new ActionListener(this) {
          private final UserClassifier this$0;
          
          public void actionPerformed(ActionEvent param1ActionEvent) {
            try {
              this.this$0.m_focus.m_set1 = null;
              this.this$0.m_focus.m_set2 = null;
              this.this$0.m_focus.setInfo(this.this$0.m_focus.m_attrib1, this.this$0.m_focus.m_attrib2, null);
              this.this$0.m_focus.setClassifier((Classifier)this.this$0.m_classifiers.getValue());
              this.this$0.m_classifiers = new GenericObjectEditor();
              this.this$0.m_classifiers.setClassType((UserClassifier.class$weka$classifiers$Classifier == null) ? (UserClassifier.class$weka$classifiers$Classifier = UserClassifier.class$("weka.classifiers.Classifier")) : UserClassifier.class$weka$classifiers$Classifier);
              this.this$0.m_classifiers.setValue(new ZeroR());
              ((GenericObjectEditor.GOEPanel)this.this$0.m_classifiers.getCustomEditor()).addOkListener(this);
              this.this$0.m_tView = new TreeVisualizer(this.this$0, this.this$0.graph(), (NodePlace)new PlaceNode2());
              this.this$0.m_tView.setHighlight(this.this$0.m_focus.m_identity);
              this.this$0.m_reps.setComponentAt(0, (Component)this.this$0.m_tView);
              this.this$0.m_iView.setShapes(null);
            } catch (Exception exception) {
              System.out.println("Error : " + exception);
              System.out.println("Part of user input so had to catch here");
            } 
          }
        });
    this.m_built = false;
    this.m_mainWin = new JFrame();
    this.m_mainWin.addWindowListener(new WindowAdapter(this) {
          private final UserClassifier this$0;
          
          public void windowClosing(WindowEvent param1WindowEvent) {
            int i = JOptionPane.showConfirmDialog(this.this$0.m_mainWin, "Are You Sure...\nClick Yes To Accept The Tree\n Click No To Return", "Accept Tree", 0);
            if (i == 0) {
              this.this$0.m_mainWin.setDefaultCloseOperation(2);
              this.this$0.blocker(false);
            } else {
              this.this$0.m_mainWin.setDefaultCloseOperation(0);
            } 
          }
        });
    this.m_reps = new JTabbedPane();
    this.m_mainWin.getContentPane().add(this.m_reps);
    Instances instances = new Instances(paramInstances, paramInstances.numInstances());
    for (byte b = 0; b < paramInstances.numInstances(); b++)
      instances.add(paramInstances.instance(b)); 
    instances.deleteWithMissingClass();
    this.m_top = new TreeClass(this, null, 0, 0, this.m_nextId, 1.0D, instances, null);
    this.m_focus = this.m_top;
    this.m_tView = new TreeVisualizer(this, graph(), (NodePlace)new PlaceNode1());
    this.m_reps.add("Tree Visualizer", (Component)this.m_tView);
    this.m_tView.setHighlight(this.m_top.m_identity);
    this.m_iView = new VisualizePanel(this);
    this.m_iView.setInstances(this.m_top.m_training);
    this.m_iView.setColourIndex(instances.classIndex());
    this.m_reps.add("Data Visualizer", (Component)this.m_iView);
    this.m_mainWin.setSize(560, 420);
    this.m_mainWin.setVisible(true);
    blocker(true);
    if (this.m_propertyDialog != null) {
      this.m_propertyDialog.dispose();
      this.m_propertyDialog = null;
    } 
    this.m_classifiers = null;
    this.m_built = true;
  }
  
  public double[] distributionForInstance(Instance paramInstance) throws Exception {
    if (!this.m_built)
      return null; 
    double[] arrayOfDouble = this.m_top.calcClassType(paramInstance);
    if (this.m_top.m_training.classAttribute().isNumeric())
      return arrayOfDouble; 
    double d1 = 0.0D;
    double d2 = -1.0D;
    double d3 = 0.0D;
    byte b;
    for (b = 0; b < this.m_top.m_training.numClasses(); b++) {
      d3 += arrayOfDouble[b];
      if (arrayOfDouble[b] > d2) {
        d1 = b;
        d2 = arrayOfDouble[b];
      } 
    } 
    if (d3 <= 0.0D)
      return null; 
    for (b = 0; b < this.m_top.m_training.numClasses(); b++)
      arrayOfDouble[b] = arrayOfDouble[b] / d3; 
    return arrayOfDouble;
  }
  
  static {
    PropertyEditorManager.registerEditor(SelectedTag.class, SelectedTagEditor.class);
    PropertyEditorManager.registerEditor(Filter.class, GenericObjectEditor.class);
    PropertyEditorManager.registerEditor((array$Lweka$classifiers$Classifier == null) ? (array$Lweka$classifiers$Classifier = class$("[Lweka.classifiers.Classifier;")) : array$Lweka$classifiers$Classifier, GenericArrayEditor.class);
    PropertyEditorManager.registerEditor((array$Ljava$lang$Object == null) ? (array$Ljava$lang$Object = class$("[Ljava.lang.Object;")) : array$Ljava$lang$Object, GenericArrayEditor.class);
    PropertyEditorManager.registerEditor(Classifier.class, GenericObjectEditor.class);
    PropertyEditorManager.registerEditor(CostMatrix.class, CostMatrixEditor.class);
  }
  
  private class TreeClass implements Serializable {
    public FastVector m_ranges;
    
    public int m_attrib1;
    
    public int m_attrib2;
    
    public TreeClass m_set1;
    
    public TreeClass m_set2;
    
    public TreeClass m_parent;
    
    public String m_identity;
    
    public double m_weight;
    
    public Instances m_training;
    
    public Classifier m_classObject;
    
    public Filter m_filter;
    
    private final UserClassifier this$0;
    
    public TreeClass(UserClassifier this$0, FastVector param1FastVector, int param1Int1, int param1Int2, int param1Int3, double param1Double, Instances param1Instances, TreeClass param1TreeClass) throws Exception {
      this.this$0 = this$0;
      this.m_set1 = null;
      this.m_set2 = null;
      this.m_ranges = param1FastVector;
      this.m_classObject = null;
      this.m_filter = null;
      this.m_training = param1Instances;
      this.m_attrib1 = param1Int1;
      this.m_attrib2 = param1Int2;
      this.m_identity = "N" + String.valueOf(param1Int3);
      this.m_weight = param1Double;
      this.m_parent = param1TreeClass;
      this$0.m_nextId++;
      if (this.m_ranges == null)
        setLeaf(); 
    }
    
    public void setClassifier(Classifier param1Classifier) throws Exception {
      this.m_classObject = param1Classifier;
      this.m_classObject.buildClassifier(this.m_training);
    }
    
    public void setInfo(int param1Int1, int param1Int2, FastVector param1FastVector) throws Exception {
      this.m_classObject = null;
      this.m_filter = null;
      this.m_attrib1 = param1Int1;
      this.m_attrib2 = param1Int2;
      this.m_ranges = param1FastVector;
      if (this.m_ranges == null)
        setLeaf(); 
    }
    
    private void setLeaf() throws Exception {
      if (this.m_training != null)
        if (this.m_training.classAttribute().isNominal()) {
          this.m_ranges = new FastVector(1);
          this.m_ranges.addElement(new FastVector(this.m_training.numClasses() + 1));
          FastVector fastVector = (FastVector)this.m_ranges.elementAt(0);
          fastVector.addElement(new Double(0.0D));
          byte b;
          for (b = 0; b < this.m_training.numClasses(); b++)
            fastVector.addElement(new Double(0.0D)); 
          for (b = 0; b < this.m_training.numInstances(); b++)
            fastVector.setElementAt(new Double(((Double)fastVector.elementAt((int)this.m_training.instance(b).classValue() + 1)).doubleValue() + this.m_training.instance(b).weight()), (int)this.m_training.instance(b).classValue() + 1); 
        } else {
          this.m_ranges = new FastVector(1);
          double d1 = 0.0D;
          for (byte b1 = 0; b1 < this.m_training.numInstances(); b1++)
            d1 += this.m_training.instance(b1).classValue(); 
          if (this.m_training.numInstances() != 0)
            d1 /= this.m_training.numInstances(); 
          double d2 = 0.0D;
          for (byte b2 = 0; b2 < this.m_training.numInstances(); b2++)
            d2 += Math.pow(this.m_training.instance(b2).classValue() - d1, 2.0D); 
          if (this.m_training.numInstances() != 0) {
            d1 = Math.sqrt(d2 / this.m_training.numInstances());
            this.m_ranges.addElement(new FastVector(2));
            FastVector fastVector = (FastVector)this.m_ranges.elementAt(0);
            fastVector.addElement(new Double(0.0D));
            fastVector.addElement(new Double(d1));
          } else {
            this.m_ranges.addElement(new FastVector(2));
            FastVector fastVector = (FastVector)this.m_ranges.elementAt(0);
            fastVector.addElement(new Double(0.0D));
            fastVector.addElement(new Double(Double.NaN));
          } 
        }  
    }
    
    public double[] calcClassType(Instance param1Instance) throws Exception {
      // Byte code:
      //   0: dconst_0
      //   1: dstore_2
      //   2: dconst_0
      //   3: dstore #4
      //   5: aload_0
      //   6: getfield m_attrib1 : I
      //   9: iflt -> 21
      //   12: aload_1
      //   13: aload_0
      //   14: getfield m_attrib1 : I
      //   17: invokevirtual value : (I)D
      //   20: dstore_2
      //   21: aload_0
      //   22: getfield m_attrib2 : I
      //   25: iflt -> 38
      //   28: aload_1
      //   29: aload_0
      //   30: getfield m_attrib2 : I
      //   33: invokevirtual value : (I)D
      //   36: dstore #4
      //   38: aload_0
      //   39: getfield m_training : Lweka/core/Instances;
      //   42: invokevirtual classAttribute : ()Lweka/core/Attribute;
      //   45: invokevirtual isNominal : ()Z
      //   48: ifeq -> 65
      //   51: aload_0
      //   52: getfield m_training : Lweka/core/Instances;
      //   55: invokevirtual numClasses : ()I
      //   58: newarray double
      //   60: astore #6
      //   62: goto -> 70
      //   65: iconst_1
      //   66: newarray double
      //   68: astore #6
      //   70: aload_0
      //   71: getfield m_classObject : Lweka/classifiers/Classifier;
      //   74: ifnull -> 158
      //   77: aload_0
      //   78: getfield m_training : Lweka/core/Instances;
      //   81: invokevirtual classAttribute : ()Lweka/core/Attribute;
      //   84: invokevirtual isNominal : ()Z
      //   87: ifeq -> 106
      //   90: aload #6
      //   92: aload_0
      //   93: getfield m_classObject : Lweka/classifiers/Classifier;
      //   96: aload_1
      //   97: invokevirtual classifyInstance : (Lweka/core/Instance;)D
      //   100: d2i
      //   101: dconst_1
      //   102: dastore
      //   103: goto -> 155
      //   106: aload_0
      //   107: getfield m_filter : Lweka/filters/Filter;
      //   110: ifnull -> 143
      //   113: aload_0
      //   114: getfield m_filter : Lweka/filters/Filter;
      //   117: aload_1
      //   118: invokevirtual input : (Lweka/core/Instance;)Z
      //   121: pop
      //   122: aload #6
      //   124: iconst_0
      //   125: aload_0
      //   126: getfield m_classObject : Lweka/classifiers/Classifier;
      //   129: aload_0
      //   130: getfield m_filter : Lweka/filters/Filter;
      //   133: invokevirtual output : ()Lweka/core/Instance;
      //   136: invokevirtual classifyInstance : (Lweka/core/Instance;)D
      //   139: dastore
      //   140: goto -> 155
      //   143: aload #6
      //   145: iconst_0
      //   146: aload_0
      //   147: getfield m_classObject : Lweka/classifiers/Classifier;
      //   150: aload_1
      //   151: invokevirtual classifyInstance : (Lweka/core/Instance;)D
      //   154: dastore
      //   155: aload #6
      //   157: areturn
      //   158: aload_0
      //   159: getfield m_ranges : Lweka/core/FastVector;
      //   162: iconst_0
      //   163: invokevirtual elementAt : (I)Ljava/lang/Object;
      //   166: checkcast weka/core/FastVector
      //   169: iconst_0
      //   170: invokevirtual elementAt : (I)Ljava/lang/Object;
      //   173: checkcast java/lang/Double
      //   176: invokevirtual intValue : ()I
      //   179: ifne -> 336
      //   182: aload_0
      //   183: getfield m_training : Lweka/core/Instances;
      //   186: invokevirtual classAttribute : ()Lweka/core/Attribute;
      //   189: invokevirtual isNumeric : ()Z
      //   192: ifeq -> 229
      //   195: aload_0
      //   196: invokespecial setLinear : ()V
      //   199: aload_0
      //   200: getfield m_filter : Lweka/filters/Filter;
      //   203: aload_1
      //   204: invokevirtual input : (Lweka/core/Instance;)Z
      //   207: pop
      //   208: aload #6
      //   210: iconst_0
      //   211: aload_0
      //   212: getfield m_classObject : Lweka/classifiers/Classifier;
      //   215: aload_0
      //   216: getfield m_filter : Lweka/filters/Filter;
      //   219: invokevirtual output : ()Lweka/core/Instance;
      //   222: invokevirtual classifyInstance : (Lweka/core/Instance;)D
      //   225: dastore
      //   226: aload #6
      //   228: areturn
      //   229: iconst_0
      //   230: istore #8
      //   232: aload_0
      //   233: getfield m_ranges : Lweka/core/FastVector;
      //   236: iconst_0
      //   237: invokevirtual elementAt : (I)Ljava/lang/Object;
      //   240: checkcast weka/core/FastVector
      //   243: astore #7
      //   245: iconst_0
      //   246: istore #9
      //   248: iload #9
      //   250: aload_0
      //   251: getfield m_training : Lweka/core/Instances;
      //   254: invokevirtual numClasses : ()I
      //   257: if_icmpge -> 298
      //   260: aload #6
      //   262: iload #9
      //   264: aload #7
      //   266: iload #9
      //   268: iconst_1
      //   269: iadd
      //   270: invokevirtual elementAt : (I)Ljava/lang/Object;
      //   273: checkcast java/lang/Double
      //   276: invokevirtual doubleValue : ()D
      //   279: dastore
      //   280: iload #8
      //   282: i2d
      //   283: aload #6
      //   285: iload #9
      //   287: daload
      //   288: dadd
      //   289: d2i
      //   290: istore #8
      //   292: iinc #9, 1
      //   295: goto -> 248
      //   298: iconst_0
      //   299: istore #9
      //   301: iload #9
      //   303: aload_0
      //   304: getfield m_training : Lweka/core/Instances;
      //   307: invokevirtual numClasses : ()I
      //   310: if_icmpge -> 333
      //   313: aload #6
      //   315: iload #9
      //   317: aload #6
      //   319: iload #9
      //   321: daload
      //   322: iload #8
      //   324: i2d
      //   325: ddiv
      //   326: dastore
      //   327: iinc #9, 1
      //   330: goto -> 301
      //   333: aload #6
      //   335: areturn
      //   336: iconst_0
      //   337: istore #8
      //   339: iload #8
      //   341: aload_0
      //   342: getfield m_ranges : Lweka/core/FastVector;
      //   345: invokevirtual size : ()I
      //   348: if_icmpge -> 755
      //   351: aload_0
      //   352: getfield m_ranges : Lweka/core/FastVector;
      //   355: iload #8
      //   357: invokevirtual elementAt : (I)Ljava/lang/Object;
      //   360: checkcast weka/core/FastVector
      //   363: astore #7
      //   365: aload #7
      //   367: iconst_0
      //   368: invokevirtual elementAt : (I)Ljava/lang/Object;
      //   371: checkcast java/lang/Double
      //   374: invokevirtual intValue : ()I
      //   377: iconst_5
      //   378: if_icmpne -> 393
      //   381: aload_1
      //   382: pop
      //   383: dload_2
      //   384: invokestatic isMissingValue : (D)Z
      //   387: ifne -> 393
      //   390: goto -> 749
      //   393: aload #7
      //   395: iconst_0
      //   396: invokevirtual elementAt : (I)Ljava/lang/Object;
      //   399: checkcast java/lang/Double
      //   402: invokevirtual intValue : ()I
      //   405: bipush #6
      //   407: if_icmpne -> 423
      //   410: aload_1
      //   411: pop
      //   412: dload #4
      //   414: invokestatic isMissingValue : (D)Z
      //   417: ifne -> 423
      //   420: goto -> 749
      //   423: aload_1
      //   424: pop
      //   425: dload_2
      //   426: invokestatic isMissingValue : (D)Z
      //   429: ifne -> 442
      //   432: aload_1
      //   433: pop
      //   434: dload #4
      //   436: invokestatic isMissingValue : (D)Z
      //   439: ifeq -> 568
      //   442: aload_0
      //   443: getfield m_set1 : Lweka/classifiers/trees/UserClassifier$TreeClass;
      //   446: aload_1
      //   447: invokevirtual calcClassType : (Lweka/core/Instance;)[D
      //   450: astore #6
      //   452: aload_0
      //   453: getfield m_set2 : Lweka/classifiers/trees/UserClassifier$TreeClass;
      //   456: aload_1
      //   457: invokevirtual calcClassType : (Lweka/core/Instance;)[D
      //   460: astore #9
      //   462: aload_0
      //   463: getfield m_training : Lweka/core/Instances;
      //   466: invokevirtual classAttribute : ()Lweka/core/Attribute;
      //   469: invokevirtual isNominal : ()Z
      //   472: ifeq -> 532
      //   475: iconst_0
      //   476: istore #10
      //   478: iload #10
      //   480: aload_0
      //   481: getfield m_training : Lweka/core/Instances;
      //   484: invokevirtual numClasses : ()I
      //   487: if_icmpge -> 565
      //   490: aload #6
      //   492: iload #10
      //   494: dup2
      //   495: daload
      //   496: aload_0
      //   497: getfield m_set1 : Lweka/classifiers/trees/UserClassifier$TreeClass;
      //   500: getfield m_weight : D
      //   503: dmul
      //   504: dastore
      //   505: aload #6
      //   507: iload #10
      //   509: dup2
      //   510: daload
      //   511: aload #9
      //   513: iload #10
      //   515: daload
      //   516: aload_0
      //   517: getfield m_set2 : Lweka/classifiers/trees/UserClassifier$TreeClass;
      //   520: getfield m_weight : D
      //   523: dmul
      //   524: dadd
      //   525: dastore
      //   526: iinc #10, 1
      //   529: goto -> 478
      //   532: aload #6
      //   534: iconst_0
      //   535: dup2
      //   536: daload
      //   537: aload_0
      //   538: getfield m_set1 : Lweka/classifiers/trees/UserClassifier$TreeClass;
      //   541: getfield m_weight : D
      //   544: dmul
      //   545: dastore
      //   546: aload #6
      //   548: iconst_0
      //   549: dup2
      //   550: daload
      //   551: aload #9
      //   553: iconst_0
      //   554: daload
      //   555: aload_0
      //   556: getfield m_set2 : Lweka/classifiers/trees/UserClassifier$TreeClass;
      //   559: getfield m_weight : D
      //   562: dmul
      //   563: dadd
      //   564: dastore
      //   565: aload #6
      //   567: areturn
      //   568: aload #7
      //   570: iconst_0
      //   571: invokevirtual elementAt : (I)Ljava/lang/Object;
      //   574: checkcast java/lang/Double
      //   577: invokevirtual intValue : ()I
      //   580: iconst_1
      //   581: if_icmpne -> 667
      //   584: dload_2
      //   585: aload #7
      //   587: iconst_1
      //   588: invokevirtual elementAt : (I)Ljava/lang/Object;
      //   591: checkcast java/lang/Double
      //   594: invokevirtual doubleValue : ()D
      //   597: dcmpl
      //   598: iflt -> 749
      //   601: dload_2
      //   602: aload #7
      //   604: iconst_3
      //   605: invokevirtual elementAt : (I)Ljava/lang/Object;
      //   608: checkcast java/lang/Double
      //   611: invokevirtual doubleValue : ()D
      //   614: dcmpg
      //   615: ifgt -> 749
      //   618: dload #4
      //   620: aload #7
      //   622: iconst_2
      //   623: invokevirtual elementAt : (I)Ljava/lang/Object;
      //   626: checkcast java/lang/Double
      //   629: invokevirtual doubleValue : ()D
      //   632: dcmpg
      //   633: ifgt -> 749
      //   636: dload #4
      //   638: aload #7
      //   640: iconst_4
      //   641: invokevirtual elementAt : (I)Ljava/lang/Object;
      //   644: checkcast java/lang/Double
      //   647: invokevirtual doubleValue : ()D
      //   650: dcmpl
      //   651: iflt -> 749
      //   654: aload_0
      //   655: getfield m_set1 : Lweka/classifiers/trees/UserClassifier$TreeClass;
      //   658: aload_1
      //   659: invokevirtual calcClassType : (Lweka/core/Instance;)[D
      //   662: astore #6
      //   664: aload #6
      //   666: areturn
      //   667: aload #7
      //   669: iconst_0
      //   670: invokevirtual elementAt : (I)Ljava/lang/Object;
      //   673: checkcast java/lang/Double
      //   676: invokevirtual intValue : ()I
      //   679: iconst_2
      //   680: if_icmpne -> 708
      //   683: aload_0
      //   684: aload #7
      //   686: dload_2
      //   687: dload #4
      //   689: invokespecial inPoly : (Lweka/core/FastVector;DD)Z
      //   692: ifeq -> 749
      //   695: aload_0
      //   696: getfield m_set1 : Lweka/classifiers/trees/UserClassifier$TreeClass;
      //   699: aload_1
      //   700: invokevirtual calcClassType : (Lweka/core/Instance;)[D
      //   703: astore #6
      //   705: aload #6
      //   707: areturn
      //   708: aload #7
      //   710: iconst_0
      //   711: invokevirtual elementAt : (I)Ljava/lang/Object;
      //   714: checkcast java/lang/Double
      //   717: invokevirtual intValue : ()I
      //   720: iconst_3
      //   721: if_icmpne -> 749
      //   724: aload_0
      //   725: aload #7
      //   727: dload_2
      //   728: dload #4
      //   730: invokespecial inPolyline : (Lweka/core/FastVector;DD)Z
      //   733: ifeq -> 749
      //   736: aload_0
      //   737: getfield m_set1 : Lweka/classifiers/trees/UserClassifier$TreeClass;
      //   740: aload_1
      //   741: invokevirtual calcClassType : (Lweka/core/Instance;)[D
      //   744: astore #6
      //   746: aload #6
      //   748: areturn
      //   749: iinc #8, 1
      //   752: goto -> 339
      //   755: aload_0
      //   756: getfield m_set2 : Lweka/classifiers/trees/UserClassifier$TreeClass;
      //   759: ifnull -> 772
      //   762: aload_0
      //   763: getfield m_set2 : Lweka/classifiers/trees/UserClassifier$TreeClass;
      //   766: aload_1
      //   767: invokevirtual calcClassType : (Lweka/core/Instance;)[D
      //   770: astore #6
      //   772: aload #6
      //   774: areturn
    }
    
    private void setLinear() throws Exception {
      boolean[] arrayOfBoolean = new boolean[this.m_training.numAttributes()];
      for (byte b1 = 0; b1 < this.m_training.numAttributes(); b1++)
        arrayOfBoolean[b1] = false; 
      TreeClass treeClass = this;
      arrayOfBoolean[this.m_training.classIndex()] = true;
      while (treeClass != null) {
        arrayOfBoolean[treeClass.m_attrib1] = true;
        arrayOfBoolean[treeClass.m_attrib2] = true;
        treeClass = treeClass.m_parent;
      } 
      byte b2 = 0;
      byte b3;
      for (b3 = 0; b3 < this.m_training.classIndex(); b3++) {
        if (arrayOfBoolean[b3])
          b2++; 
      } 
      b3 = 0;
      for (byte b4 = 0; b4 < this.m_training.numAttributes(); b4++) {
        if (arrayOfBoolean[b4])
          b3++; 
      } 
      int[] arrayOfInt = new int[b3];
      b3 = 0;
      for (byte b5 = 0; b5 < this.m_training.numAttributes(); b5++) {
        if (arrayOfBoolean[b5]) {
          arrayOfInt[b3] = b5;
          b3++;
        } 
      } 
      this.m_filter = (Filter)new Remove();
      ((Remove)this.m_filter).setInvertSelection(true);
      ((Remove)this.m_filter).setAttributeIndicesArray(arrayOfInt);
      this.m_filter.setInputFormat(this.m_training);
      Instances instances = Filter.useFilter(this.m_training, this.m_filter);
      instances.setClassIndex(b2);
      this.m_classObject = (Classifier)new LinearRegression();
      this.m_classObject.buildClassifier(instances);
    }
    
    private boolean inPolyline(FastVector param1FastVector, double param1Double1, double param1Double2) {
      byte b1 = 0;
      for (byte b2 = 1; b2 < param1FastVector.size() - 4; b2 += 2) {
        double d6 = ((Double)param1FastVector.elementAt(b2 + 1)).doubleValue();
        double d8 = ((Double)param1FastVector.elementAt(b2 + 3)).doubleValue();
        double d5 = ((Double)param1FastVector.elementAt(b2)).doubleValue();
        double d7 = ((Double)param1FastVector.elementAt(b2 + 2)).doubleValue();
        double d4 = d8 - d6;
        double d3 = d7 - d5;
        if (b2 == 1 && b2 == param1FastVector.size() - 6) {
          if (d4 != 0.0D) {
            double d = (param1Double2 - d6) / d4;
            if (d3 * d + d5 >= param1Double1)
              b1++; 
          } 
        } else if (b2 == 1) {
          if ((param1Double2 < d8 && d4 > 0.0D) || (param1Double2 > d8 && d4 < 0.0D)) {
            double d = (param1Double2 - d6) / d4;
            if (d3 * d + d5 >= param1Double1)
              b1++; 
          } 
        } else if (b2 == param1FastVector.size() - 6) {
          if ((param1Double2 <= d6 && d4 < 0.0D) || (param1Double2 >= d6 && d4 > 0.0D)) {
            double d = (param1Double2 - d6) / d4;
            if (d3 * d + d5 >= param1Double1)
              b1++; 
          } 
        } else if (((d6 <= param1Double2 && param1Double2 < d8) || (d8 < param1Double2 && param1Double2 <= d6)) && d4 != 0.0D) {
          double d = (param1Double2 - d6) / d4;
          if (d3 * d + d5 >= param1Double1)
            b1++; 
        } 
      } 
      double d1 = ((Double)param1FastVector.elementAt(param1FastVector.size() - 2)).doubleValue();
      double d2 = ((Double)param1FastVector.elementAt(param1FastVector.size() - 1)).doubleValue();
      if (d1 > d2) {
        if (d1 >= param1Double2 && param1Double2 > d2)
          b1++; 
      } else if (d1 >= param1Double2 || param1Double2 > d2) {
        b1++;
      } 
      return (b1 % 2 == 1);
    }
    
    private boolean inPoly(FastVector param1FastVector, double param1Double1, double param1Double2) {
      byte b1 = 0;
      for (byte b2 = 1; b2 < param1FastVector.size() - 2; b2 += 2) {
        double d1 = ((Double)param1FastVector.elementAt(b2 + 1)).doubleValue();
        double d2 = ((Double)param1FastVector.elementAt(b2 + 3)).doubleValue();
        if ((d1 <= param1Double2 && param1Double2 < d2) || (d2 < param1Double2 && param1Double2 <= d1)) {
          double d = d2 - d1;
          if (d != 0.0D) {
            double d5 = ((Double)param1FastVector.elementAt(b2)).doubleValue();
            double d6 = ((Double)param1FastVector.elementAt(b2 + 2)).doubleValue();
            double d3 = d6 - d5;
            double d4 = (param1Double2 - d1) / d;
            if (d3 * d4 + d5 >= param1Double1)
              b1++; 
          } 
        } 
      } 
      return (b1 % 2 == 1);
    }
    
    public TreeClass getNode(String param1String) {
      if (param1String.equals(this.m_identity))
        return this; 
      if (this.m_set1 != null) {
        TreeClass treeClass = this.m_set1.getNode(param1String);
        if (treeClass != null)
          return treeClass; 
      } 
      if (this.m_set2 != null) {
        TreeClass treeClass = this.m_set2.getNode(param1String);
        if (treeClass != null)
          return treeClass; 
      } 
      return null;
    }
    
    public void getAlternateLabel(StringBuffer param1StringBuffer) throws Exception {
      FastVector fastVector = (FastVector)this.m_ranges.elementAt(0);
      if (this.m_classObject != null && this.m_training.classAttribute().isNominal()) {
        param1StringBuffer.append("Classified by " + this.m_classObject.getClass().getName());
      } else if (((Double)fastVector.elementAt(0)).intValue() == 0) {
        if (this.m_training.classAttribute().isNominal()) {
          double d1 = -1000.0D;
          int i = 0;
          double d2 = 0.0D;
          for (byte b = 0; b < this.m_training.classAttribute().numValues(); b++) {
            if (((Double)fastVector.elementAt(b + 1)).doubleValue() > d1) {
              d1 = ((Double)fastVector.elementAt(b + 1)).doubleValue();
              i = b + 1;
            } 
            d2 += ((Double)fastVector.elementAt(b + 1)).doubleValue();
          } 
          param1StringBuffer.append(this.m_training.classAttribute().value(i - 1) + "(" + d2);
          if (d2 > d1)
            param1StringBuffer.append("/" + (d2 - d1)); 
          param1StringBuffer.append(")");
        } else {
          if (this.m_classObject == null && ((Double)fastVector.elementAt(0)).intValue() == 0)
            setLinear(); 
          param1StringBuffer.append("Standard Deviation = " + Utils.doubleToString(((Double)fastVector.elementAt(1)).doubleValue(), 6));
        } 
      } else {
        param1StringBuffer.append("Split on ");
        param1StringBuffer.append(this.m_training.attribute(this.m_attrib1).name() + " AND ");
        param1StringBuffer.append(this.m_training.attribute(this.m_attrib2).name());
      } 
    }
    
    public void getLabel(StringBuffer param1StringBuffer) throws Exception {
      FastVector fastVector = (FastVector)this.m_ranges.elementAt(0);
      if (this.m_classObject != null && this.m_training.classAttribute().isNominal()) {
        param1StringBuffer.append("Classified by\\n" + this.m_classObject.getClass().getName());
      } else if (((Double)fastVector.elementAt(0)).intValue() == 0) {
        if (this.m_training.classAttribute().isNominal()) {
          boolean bool = true;
          for (byte b = 0; b < this.m_training.classAttribute().numValues(); b++) {
            if (((Double)fastVector.elementAt(b + 1)).doubleValue() > 0.0D) {
              if (bool) {
                param1StringBuffer.append("[" + this.m_training.classAttribute().value(b));
                bool = false;
              } else {
                param1StringBuffer.append("\\n[" + this.m_training.classAttribute().value(b));
              } 
              param1StringBuffer.append(", " + ((Double)fastVector.elementAt(b + 1)).doubleValue() + "]");
            } 
          } 
        } else {
          if (this.m_classObject == null && ((Double)fastVector.elementAt(0)).intValue() == 0)
            setLinear(); 
          param1StringBuffer.append("Standard Deviation = " + Utils.doubleToString(((Double)fastVector.elementAt(1)).doubleValue(), 6));
        } 
      } else {
        param1StringBuffer.append("Split on\\n");
        param1StringBuffer.append(this.m_training.attribute(this.m_attrib1).name() + " AND\\n");
        param1StringBuffer.append(this.m_training.attribute(this.m_attrib2).name());
      } 
    }
    
    public void toDotty(StringBuffer param1StringBuffer) throws Exception {
      param1StringBuffer.append(this.m_identity + " [label=\"");
      getLabel(param1StringBuffer);
      param1StringBuffer.append("\" ");
      if (((Double)((FastVector)this.m_ranges.elementAt(0)).elementAt(0)).intValue() == 0) {
        param1StringBuffer.append("shape=box ");
      } else {
        param1StringBuffer.append("shape=ellipse ");
      } 
      param1StringBuffer.append("style=filled color=gray95]\n");
      if (this.m_set1 != null) {
        param1StringBuffer.append(this.m_identity + "->");
        param1StringBuffer.append(this.m_set1.m_identity + " [label=\"True\"]\n");
        this.m_set1.toDotty(param1StringBuffer);
      } 
      if (this.m_set2 != null) {
        param1StringBuffer.append(this.m_identity + "->");
        param1StringBuffer.append(this.m_set2.m_identity + " [label=\"False\"]\n");
        this.m_set2.toDotty(param1StringBuffer);
      } 
    }
    
    public void objectStrings(StringBuffer param1StringBuffer) {
      if (this.m_classObject != null)
        param1StringBuffer.append("\n\n" + this.m_identity + " {\n" + this.m_classObject.toString() + "\n}"); 
      if (this.m_set1 != null)
        this.m_set1.objectStrings(param1StringBuffer); 
      if (this.m_set2 != null)
        this.m_set2.objectStrings(param1StringBuffer); 
    }
    
    public void toString(int param1Int, StringBuffer param1StringBuffer) throws Exception {
      if (((Double)((FastVector)this.m_ranges.elementAt(0)).elementAt(0)).intValue() == 0) {
        param1StringBuffer.append(": " + this.m_identity + " ");
        getAlternateLabel(param1StringBuffer);
      } 
      if (this.m_set1 != null) {
        param1StringBuffer.append("\n");
        for (byte b = 0; b < param1Int; b++)
          param1StringBuffer.append("|   "); 
        getAlternateLabel(param1StringBuffer);
        param1StringBuffer.append(" (In Set)");
        this.m_set1.toString(param1Int + 1, param1StringBuffer);
      } 
      if (this.m_set2 != null) {
        param1StringBuffer.append("\n");
        for (byte b = 0; b < param1Int; b++)
          param1StringBuffer.append("|   "); 
        getAlternateLabel(param1StringBuffer);
        param1StringBuffer.append(" (Not in Set)");
        this.m_set2.toString(param1Int + 1, param1StringBuffer);
      } 
    }
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\classifiers\trees\UserClassifier.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */