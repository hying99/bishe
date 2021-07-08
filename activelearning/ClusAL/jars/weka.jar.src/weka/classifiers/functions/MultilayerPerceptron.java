package weka.classifiers.functions;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.event.ActionListener;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Enumeration;
import java.util.Random;
import java.util.StringTokenizer;
import java.util.Vector;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.functions.neural.LinearUnit;
import weka.classifiers.functions.neural.NeuralConnection;
import weka.classifiers.functions.neural.NeuralMethod;
import weka.classifiers.functions.neural.NeuralNode;
import weka.classifiers.functions.neural.SigmoidUnit;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Option;
import weka.core.OptionHandler;
import weka.core.UnsupportedAttributeTypeException;
import weka.core.Utils;
import weka.core.WeightedInstancesHandler;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.NominalToBinary;

public class MultilayerPerceptron extends Classifier implements OptionHandler, WeightedInstancesHandler {
  private Instances m_instances = null;
  
  private Instance m_currentInstance = null;
  
  private boolean m_numeric;
  
  private double[] m_attributeRanges;
  
  private double[] m_attributeBases;
  
  private NeuralEnd[] m_outputs = new NeuralEnd[0];
  
  private NeuralEnd[] m_inputs = new NeuralEnd[0];
  
  private NeuralConnection[] m_neuralNodes;
  
  private int m_numClasses = 0;
  
  private int m_numAttributes = 0;
  
  private NodePanel m_nodePanel = null;
  
  private ControlPanel m_controlPanel = null;
  
  private int m_nextId;
  
  private FastVector m_selected;
  
  private FastVector m_graphers;
  
  private int m_numEpochs;
  
  private boolean m_stopIt;
  
  private boolean m_stopped;
  
  private boolean m_accepted;
  
  private JFrame m_win;
  
  private boolean m_autoBuild;
  
  private boolean m_gui;
  
  private int m_valSize;
  
  private int m_driftThreshold;
  
  private long m_randomSeed;
  
  private Random m_random;
  
  private boolean m_useNomToBin;
  
  private NominalToBinary m_nominalToBinaryFilter;
  
  private String m_hiddenLayers;
  
  private boolean m_normalizeAttributes;
  
  private boolean m_decay;
  
  private double m_learningRate;
  
  private double m_momentum;
  
  private int m_epoch = 0;
  
  private double m_error = 0.0D;
  
  private boolean m_reset;
  
  private boolean m_normalizeClass;
  
  private SigmoidUnit m_sigmoidUnit;
  
  private LinearUnit m_linearUnit;
  
  public static void main(String[] paramArrayOfString) {
    try {
      System.out.println(Evaluation.evaluateModel(new MultilayerPerceptron(), paramArrayOfString));
    } catch (Exception exception) {
      System.err.println(exception.getMessage());
      exception.printStackTrace();
    } 
    System.exit(0);
  }
  
  public MultilayerPerceptron() {
    this.m_numAttributes = 0;
    this.m_numClasses = 0;
    this.m_neuralNodes = new NeuralConnection[0];
    this.m_selected = new FastVector(4);
    this.m_graphers = new FastVector(2);
    this.m_nextId = 0;
    this.m_stopIt = true;
    this.m_stopped = true;
    this.m_accepted = false;
    this.m_numeric = false;
    this.m_random = null;
    this.m_nominalToBinaryFilter = new NominalToBinary();
    this.m_sigmoidUnit = new SigmoidUnit();
    this.m_linearUnit = new LinearUnit();
    this.m_normalizeClass = true;
    this.m_normalizeAttributes = true;
    this.m_autoBuild = true;
    this.m_gui = false;
    this.m_useNomToBin = true;
    this.m_driftThreshold = 20;
    this.m_numEpochs = 500;
    this.m_valSize = 0;
    this.m_randomSeed = 0L;
    this.m_hiddenLayers = "a";
    this.m_learningRate = 0.3D;
    this.m_momentum = 0.2D;
    this.m_reset = true;
    this.m_decay = false;
  }
  
  public void setDecay(boolean paramBoolean) {
    this.m_decay = paramBoolean;
  }
  
  public boolean getDecay() {
    return this.m_decay;
  }
  
  public void setReset(boolean paramBoolean) {
    if (this.m_gui)
      paramBoolean = false; 
    this.m_reset = paramBoolean;
  }
  
  public boolean getReset() {
    return this.m_reset;
  }
  
  public void setNormalizeNumericClass(boolean paramBoolean) {
    this.m_normalizeClass = paramBoolean;
  }
  
  public boolean getNormalizeNumericClass() {
    return this.m_normalizeClass;
  }
  
  public void setNormalizeAttributes(boolean paramBoolean) {
    this.m_normalizeAttributes = paramBoolean;
  }
  
  public boolean getNormalizeAttributes() {
    return this.m_normalizeAttributes;
  }
  
  public void setNominalToBinaryFilter(boolean paramBoolean) {
    this.m_useNomToBin = paramBoolean;
  }
  
  public boolean getNominalToBinaryFilter() {
    return this.m_useNomToBin;
  }
  
  public void setRandomSeed(long paramLong) {
    if (paramLong >= 0L)
      this.m_randomSeed = paramLong; 
  }
  
  public long getRandomSeed() {
    return this.m_randomSeed;
  }
  
  public void setValidationThreshold(int paramInt) {
    if (paramInt > 0)
      this.m_driftThreshold = paramInt; 
  }
  
  public int getValidationThreshold() {
    return this.m_driftThreshold;
  }
  
  public void setLearningRate(double paramDouble) {
    if (paramDouble > 0.0D && paramDouble <= 1.0D) {
      this.m_learningRate = paramDouble;
      if (this.m_controlPanel != null)
        this.m_controlPanel.m_changeLearning.setText("" + paramDouble); 
    } 
  }
  
  public double getLearningRate() {
    return this.m_learningRate;
  }
  
  public void setMomentum(double paramDouble) {
    if (paramDouble >= 0.0D && paramDouble <= 1.0D) {
      this.m_momentum = paramDouble;
      if (this.m_controlPanel != null)
        this.m_controlPanel.m_changeMomentum.setText("" + paramDouble); 
    } 
  }
  
  public double getMomentum() {
    return this.m_momentum;
  }
  
  public void setAutoBuild(boolean paramBoolean) {
    if (!this.m_gui)
      paramBoolean = true; 
    this.m_autoBuild = paramBoolean;
  }
  
  public boolean getAutoBuild() {
    return this.m_autoBuild;
  }
  
  public void setHiddenLayers(String paramString) {
    String str = "";
    StringTokenizer stringTokenizer = new StringTokenizer(paramString, ",");
    if (stringTokenizer.countTokens() == 0)
      return; 
    boolean bool = true;
    while (stringTokenizer.hasMoreTokens()) {
      String str1 = stringTokenizer.nextToken().trim();
      if (str1.equals("a") || str1.equals("i") || str1.equals("o") || str1.equals("t")) {
        str = str + str1;
      } else {
        double d = Double.valueOf(str1).doubleValue();
        int i = (int)d;
        if (i == d && (i != 0 || (stringTokenizer.countTokens() == 0 && bool)) && i >= 0) {
          str = str + i;
        } else {
          return;
        } 
      } 
      bool = false;
      if (stringTokenizer.hasMoreTokens())
        str = str + ", "; 
    } 
    this.m_hiddenLayers = str;
  }
  
  public String getHiddenLayers() {
    return this.m_hiddenLayers;
  }
  
  public void setGUI(boolean paramBoolean) {
    this.m_gui = paramBoolean;
    if (!paramBoolean) {
      setAutoBuild(true);
    } else {
      setReset(false);
    } 
  }
  
  public boolean getGUI() {
    return this.m_gui;
  }
  
  public void setValidationSetSize(int paramInt) {
    if (paramInt < 0 || paramInt > 99)
      return; 
    this.m_valSize = paramInt;
  }
  
  public int getValidationSetSize() {
    return this.m_valSize;
  }
  
  public void setTrainingTime(int paramInt) {
    if (paramInt > 0)
      this.m_numEpochs = paramInt; 
  }
  
  public int getTrainingTime() {
    return this.m_numEpochs;
  }
  
  private void addNode(NeuralConnection paramNeuralConnection) {
    NeuralConnection[] arrayOfNeuralConnection = new NeuralConnection[this.m_neuralNodes.length + 1];
    for (byte b = 0; b < this.m_neuralNodes.length; b++)
      arrayOfNeuralConnection[b] = this.m_neuralNodes[b]; 
    arrayOfNeuralConnection[arrayOfNeuralConnection.length - 1] = paramNeuralConnection;
    this.m_neuralNodes = arrayOfNeuralConnection;
  }
  
  private boolean removeNode(NeuralConnection paramNeuralConnection) {
    NeuralConnection[] arrayOfNeuralConnection = new NeuralConnection[this.m_neuralNodes.length - 1];
    byte b1 = 0;
    for (byte b2 = 0; b2 < this.m_neuralNodes.length; b2++) {
      if (paramNeuralConnection == this.m_neuralNodes[b2]) {
        b1++;
      } else if (b2 - b1 < arrayOfNeuralConnection.length) {
        arrayOfNeuralConnection[b2 - b1] = this.m_neuralNodes[b2];
      } else {
        return false;
      } 
    } 
    this.m_neuralNodes = arrayOfNeuralConnection;
    return true;
  }
  
  private Instances setClassType(Instances paramInstances) throws Exception {
    if (paramInstances != null) {
      double d1 = Double.POSITIVE_INFINITY;
      double d2 = Double.NEGATIVE_INFINITY;
      this.m_attributeRanges = new double[paramInstances.numAttributes()];
      this.m_attributeBases = new double[paramInstances.numAttributes()];
      for (byte b = 0; b < paramInstances.numAttributes(); b++) {
        d1 = Double.POSITIVE_INFINITY;
        d2 = Double.NEGATIVE_INFINITY;
        byte b1;
        for (b1 = 0; b1 < paramInstances.numInstances(); b1++) {
          if (!paramInstances.instance(b1).isMissing(b)) {
            double d = paramInstances.instance(b1).value(b);
            if (d < d1)
              d1 = d; 
            if (d > d2)
              d2 = d; 
          } 
        } 
        this.m_attributeRanges[b] = (d2 - d1) / 2.0D;
        this.m_attributeBases[b] = (d2 + d1) / 2.0D;
        if (b != paramInstances.classIndex() && this.m_normalizeAttributes)
          for (b1 = 0; b1 < paramInstances.numInstances(); b1++) {
            if (this.m_attributeRanges[b] != 0.0D) {
              paramInstances.instance(b1).setValue(b, (paramInstances.instance(b1).value(b) - this.m_attributeBases[b]) / this.m_attributeRanges[b]);
            } else {
              paramInstances.instance(b1).setValue(b, paramInstances.instance(b1).value(b) - this.m_attributeBases[b]);
            } 
          }  
      } 
      if (paramInstances.classAttribute().isNumeric()) {
        this.m_numeric = true;
      } else {
        this.m_numeric = false;
      } 
    } 
    return paramInstances;
  }
  
  public synchronized void blocker(boolean paramBoolean) {
    if (paramBoolean) {
      try {
        wait();
      } catch (InterruptedException interruptedException) {}
    } else {
      notifyAll();
    } 
  }
  
  private void updateDisplay() {
    if (this.m_gui) {
      this.m_controlPanel.m_errorLabel.repaint();
      this.m_controlPanel.m_epochsLabel.repaint();
    } 
  }
  
  private void resetNetwork() {
    for (byte b = 0; b < this.m_numClasses; b++)
      this.m_outputs[b].reset(); 
  }
  
  private void calculateOutputs() {
    for (byte b = 0; b < this.m_numClasses; b++)
      this.m_outputs[b].outputValue(true); 
  }
  
  private double calculateErrors() throws Exception {
    double d1 = 0.0D;
    double d2 = 0.0D;
    byte b;
    for (b = 0; b < this.m_numAttributes; b++)
      this.m_inputs[b].errorValue(true); 
    for (b = 0; b < this.m_numClasses; b++) {
      d2 = this.m_outputs[b].errorValue(false);
      d1 += d2 * d2;
    } 
    return d1;
  }
  
  private void updateNetworkWeights(double paramDouble1, double paramDouble2) {
    for (byte b = 0; b < this.m_numClasses; b++)
      this.m_outputs[b].updateWeights(paramDouble1, paramDouble2); 
  }
  
  private void setupInputs() throws Exception {
    this.m_inputs = new NeuralEnd[this.m_numAttributes];
    byte b1 = 0;
    for (byte b2 = 0; b2 < this.m_numAttributes + 1; b2++) {
      if (this.m_instances.classIndex() != b2) {
        this.m_inputs[b2 - b1] = new NeuralEnd(this, this.m_instances.attribute(b2).name());
        this.m_inputs[b2 - b1].setX(0.1D);
        this.m_inputs[b2 - b1].setY(((b2 - b1) + 1.0D) / (this.m_numAttributes + 1));
        this.m_inputs[b2 - b1].setLink(true, b2);
      } else {
        b1 = 1;
      } 
    } 
  }
  
  private void setupOutputs() throws Exception {
    this.m_outputs = new NeuralEnd[this.m_numClasses];
    for (byte b = 0; b < this.m_numClasses; b++) {
      if (this.m_numeric) {
        this.m_outputs[b] = new NeuralEnd(this, this.m_instances.classAttribute().name());
      } else {
        this.m_outputs[b] = new NeuralEnd(this, this.m_instances.classAttribute().value(b));
      } 
      this.m_outputs[b].setX(0.9D);
      this.m_outputs[b].setY((b + 1.0D) / (this.m_numClasses + 1));
      this.m_outputs[b].setLink(false, b);
      NeuralNode neuralNode = new NeuralNode(String.valueOf(this.m_nextId), this.m_random, (NeuralMethod)this.m_sigmoidUnit);
      this.m_nextId++;
      neuralNode.setX(0.75D);
      neuralNode.setY((b + 1.0D) / (this.m_numClasses + 1));
      addNode((NeuralConnection)neuralNode);
      NeuralConnection.connect((NeuralConnection)neuralNode, this.m_outputs[b]);
    } 
  }
  
  private void setupHiddenLayer() {
    StringTokenizer stringTokenizer = new StringTokenizer(this.m_hiddenLayers, ",");
    int i = 0;
    int j = 0;
    int k = stringTokenizer.countTokens();
    int m;
    for (m = 0; m < k; m++) {
      String str1 = stringTokenizer.nextToken().trim();
      if (str1.equals("a")) {
        i = (this.m_numAttributes + this.m_numClasses) / 2;
      } else if (str1.equals("i")) {
        i = this.m_numAttributes;
      } else if (str1.equals("o")) {
        i = this.m_numClasses;
      } else if (str1.equals("t")) {
        i = this.m_numAttributes + this.m_numClasses;
      } else {
        i = Double.valueOf(str1).intValue();
      } 
      for (byte b = 0; b < i; b++) {
        NeuralNode neuralNode = new NeuralNode(String.valueOf(this.m_nextId), this.m_random, (NeuralMethod)this.m_sigmoidUnit);
        this.m_nextId++;
        neuralNode.setX(0.5D / k * m + 0.25D);
        neuralNode.setY((b + 1.0D) / (i + 1));
        addNode((NeuralConnection)neuralNode);
        if (m > 0)
          for (int n = this.m_neuralNodes.length - b - 1 - j; n < this.m_neuralNodes.length - b - 1; n++)
            NeuralConnection.connect(this.m_neuralNodes[n], (NeuralConnection)neuralNode);  
      } 
      j = i;
    } 
    stringTokenizer = new StringTokenizer(this.m_hiddenLayers, ",");
    String str = stringTokenizer.nextToken();
    if (str.equals("a")) {
      i = (this.m_numAttributes + this.m_numClasses) / 2;
    } else if (str.equals("i")) {
      i = this.m_numAttributes;
    } else if (str.equals("o")) {
      i = this.m_numClasses;
    } else if (str.equals("t")) {
      i = this.m_numAttributes + this.m_numClasses;
    } else {
      i = Double.valueOf(str).intValue();
    } 
    if (i == 0) {
      for (m = 0; m < this.m_numAttributes; m++) {
        for (byte b = 0; b < this.m_numClasses; b++)
          NeuralConnection.connect(this.m_inputs[m], this.m_neuralNodes[b]); 
      } 
    } else {
      for (m = 0; m < this.m_numAttributes; m++) {
        for (int n = this.m_numClasses; n < this.m_numClasses + i; n++)
          NeuralConnection.connect(this.m_inputs[m], this.m_neuralNodes[n]); 
      } 
      for (m = this.m_neuralNodes.length - j; m < this.m_neuralNodes.length; m++) {
        for (byte b = 0; b < this.m_numClasses; b++)
          NeuralConnection.connect(this.m_neuralNodes[m], this.m_neuralNodes[b]); 
      } 
    } 
  }
  
  private void setEndsToLinear() {
    for (byte b = 0; b < this.m_neuralNodes.length; b++) {
      if ((this.m_neuralNodes[b].getType() & 0x8) == 8) {
        ((NeuralNode)this.m_neuralNodes[b]).setMethod((NeuralMethod)this.m_linearUnit);
      } else {
        ((NeuralNode)this.m_neuralNodes[b]).setMethod((NeuralMethod)this.m_sigmoidUnit);
      } 
    } 
  }
  
  public void buildClassifier(Instances paramInstances) throws Exception {
    if (paramInstances.checkForStringAttributes())
      throw new UnsupportedAttributeTypeException("Cannot handle string attributes!"); 
    if (paramInstances.numInstances() == 0)
      throw new IllegalArgumentException("No training instances."); 
    this.m_epoch = 0;
    this.m_error = 0.0D;
    this.m_instances = null;
    this.m_currentInstance = null;
    this.m_controlPanel = null;
    this.m_nodePanel = null;
    this.m_outputs = new NeuralEnd[0];
    this.m_inputs = new NeuralEnd[0];
    this.m_numAttributes = 0;
    this.m_numClasses = 0;
    this.m_neuralNodes = new NeuralConnection[0];
    this.m_selected = new FastVector(4);
    this.m_graphers = new FastVector(2);
    this.m_nextId = 0;
    this.m_stopIt = true;
    this.m_stopped = true;
    this.m_accepted = false;
    this.m_instances = new Instances(paramInstances);
    this.m_instances.deleteWithMissingClass();
    if (this.m_instances.numInstances() == 0) {
      this.m_instances = null;
      throw new IllegalArgumentException("All class values missing.");
    } 
    this.m_random = new Random(this.m_randomSeed);
    this.m_instances.randomize(this.m_random);
    if (this.m_useNomToBin) {
      this.m_nominalToBinaryFilter = new NominalToBinary();
      this.m_nominalToBinaryFilter.setInputFormat(this.m_instances);
      this.m_instances = Filter.useFilter(this.m_instances, (Filter)this.m_nominalToBinaryFilter);
    } 
    this.m_numAttributes = this.m_instances.numAttributes() - 1;
    this.m_numClasses = this.m_instances.numClasses();
    setClassType(this.m_instances);
    Instances instances = null;
    int i = (int)(this.m_valSize / 100.0D * this.m_instances.numInstances());
    if (this.m_valSize > 0) {
      if (i == 0)
        i = 1; 
      instances = new Instances(this.m_instances, 0, i);
    } 
    setupInputs();
    setupOutputs();
    if (this.m_autoBuild)
      setupHiddenLayer(); 
    if (this.m_gui) {
      this.m_win = new JFrame();
      this.m_win.addWindowListener(new WindowAdapter(this) {
            private final MultilayerPerceptron this$0;
            
            public void windowClosing(WindowEvent param1WindowEvent) {
              boolean bool = this.this$0.m_stopIt;
              this.this$0.m_stopIt = true;
              int i = JOptionPane.showConfirmDialog(this.this$0.m_win, "Are You Sure...\nClick Yes To Accept The Neural Network\n Click No To Return", "Accept Neural Network", 0);
              if (i == 0) {
                this.this$0.m_win.setDefaultCloseOperation(2);
                this.this$0.m_accepted = true;
                this.this$0.blocker(false);
              } else {
                this.this$0.m_win.setDefaultCloseOperation(0);
              } 
              this.this$0.m_stopIt = bool;
            }
          });
      this.m_win.getContentPane().setLayout(new BorderLayout());
      this.m_win.setTitle("Neural Network");
      this.m_nodePanel = new NodePanel(this);
      JScrollPane jScrollPane = new JScrollPane(this.m_nodePanel, 22, 31);
      this.m_controlPanel = new ControlPanel(this);
      this.m_win.getContentPane().add(jScrollPane, "Center");
      this.m_win.getContentPane().add(this.m_controlPanel, "South");
      this.m_win.setSize(640, 480);
      this.m_win.setVisible(true);
    } 
    if (this.m_gui) {
      blocker(true);
      this.m_controlPanel.m_changeEpochs.setEnabled(false);
      this.m_controlPanel.m_changeLearning.setEnabled(false);
      this.m_controlPanel.m_changeMomentum.setEnabled(false);
    } 
    if (this.m_numeric)
      setEndsToLinear(); 
    if (this.m_accepted) {
      this.m_win.dispose();
      this.m_controlPanel = null;
      this.m_nodePanel = null;
      this.m_instances = new Instances(this.m_instances, 0);
      return;
    } 
    double d1 = 0.0D;
    double d2 = 0.0D;
    double d3 = Double.POSITIVE_INFINITY;
    double d4 = 0.0D;
    double d5 = 0.0D;
    double d6 = this.m_learningRate;
    if (i == this.m_instances.numInstances())
      i--; 
    if (i < 0)
      i = 0; 
    int j;
    for (j = i; j < this.m_instances.numInstances(); j++) {
      if (!this.m_instances.instance(j).classIsMissing())
        d4 += this.m_instances.instance(j).weight(); 
    } 
    if (this.m_valSize != 0)
      for (j = 0; j < instances.numInstances(); j++) {
        if (!instances.instance(j).classIsMissing())
          d5 += instances.instance(j).weight(); 
      }  
    this.m_stopped = false;
    for (j = 1; j < this.m_numEpochs + 1; j++) {
      d1 = 0.0D;
      int k;
      for (k = i; k < this.m_instances.numInstances(); k++) {
        this.m_currentInstance = this.m_instances.instance(k);
        if (!this.m_currentInstance.classIsMissing()) {
          resetNetwork();
          calculateOutputs();
          double d = this.m_learningRate * this.m_currentInstance.weight();
          if (this.m_decay)
            d /= j; 
          d1 += calculateErrors() / this.m_instances.numClasses() * this.m_currentInstance.weight();
          updateNetworkWeights(d, this.m_momentum);
        } 
      } 
      d1 /= d4;
      if (Double.isInfinite(d1) || Double.isNaN(d1)) {
        if (!this.m_reset) {
          this.m_instances = null;
          throw new Exception("Network cannot train. Try restarting with a smaller learning rate.");
        } 
        this.m_learningRate /= 2.0D;
        buildClassifier(paramInstances);
        this.m_learningRate = d6;
        this.m_instances = new Instances(this.m_instances, 0);
        return;
      } 
      if (this.m_valSize != 0) {
        d1 = 0.0D;
        for (k = 0; k < instances.numInstances(); k++) {
          this.m_currentInstance = instances.instance(k);
          if (!this.m_currentInstance.classIsMissing()) {
            resetNetwork();
            calculateOutputs();
            d1 += calculateErrors() / instances.numClasses() * this.m_currentInstance.weight();
          } 
        } 
        if (d1 < d3) {
          d2 = 0.0D;
        } else {
          d2++;
        } 
        d3 = d1;
        if (d2 > this.m_driftThreshold || j + 1 >= this.m_numEpochs)
          this.m_accepted = true; 
        d1 /= d5;
      } 
      this.m_epoch = j;
      this.m_error = d1;
      updateDisplay();
      if (this.m_gui) {
        while ((this.m_stopIt || (this.m_epoch >= this.m_numEpochs && this.m_valSize == 0)) && !this.m_accepted) {
          this.m_stopIt = true;
          this.m_stopped = true;
          if (this.m_epoch >= this.m_numEpochs && this.m_valSize == 0) {
            this.m_controlPanel.m_startStop.setEnabled(false);
          } else {
            this.m_controlPanel.m_startStop.setEnabled(true);
          } 
          this.m_controlPanel.m_startStop.setText("Start");
          this.m_controlPanel.m_startStop.setActionCommand("Start");
          this.m_controlPanel.m_changeEpochs.setEnabled(true);
          this.m_controlPanel.m_changeLearning.setEnabled(true);
          this.m_controlPanel.m_changeMomentum.setEnabled(true);
          blocker(true);
          if (this.m_numeric)
            setEndsToLinear(); 
        } 
        this.m_controlPanel.m_changeEpochs.setEnabled(false);
        this.m_controlPanel.m_changeLearning.setEnabled(false);
        this.m_controlPanel.m_changeMomentum.setEnabled(false);
        this.m_stopped = false;
        if (this.m_accepted) {
          this.m_win.dispose();
          this.m_controlPanel = null;
          this.m_nodePanel = null;
          this.m_instances = new Instances(this.m_instances, 0);
          return;
        } 
      } 
      if (this.m_accepted) {
        this.m_instances = new Instances(this.m_instances, 0);
        return;
      } 
    } 
    if (this.m_gui) {
      this.m_win.dispose();
      this.m_controlPanel = null;
      this.m_nodePanel = null;
    } 
    this.m_instances = new Instances(this.m_instances, 0);
  }
  
  public double[] distributionForInstance(Instance paramInstance) throws Exception {
    if (this.m_useNomToBin) {
      this.m_nominalToBinaryFilter.input(paramInstance);
      this.m_currentInstance = this.m_nominalToBinaryFilter.output();
    } else {
      this.m_currentInstance = paramInstance;
    } 
    if (this.m_normalizeAttributes)
      for (byte b = 0; b < this.m_instances.numAttributes(); b++) {
        if (b != this.m_instances.classIndex())
          if (this.m_attributeRanges[b] != 0.0D) {
            this.m_currentInstance.setValue(b, (this.m_currentInstance.value(b) - this.m_attributeBases[b]) / this.m_attributeRanges[b]);
          } else {
            this.m_currentInstance.setValue(b, this.m_currentInstance.value(b) - this.m_attributeBases[b]);
          }  
      }  
    resetNetwork();
    double[] arrayOfDouble = new double[this.m_numClasses];
    for (byte b1 = 0; b1 < this.m_numClasses; b1++)
      arrayOfDouble[b1] = this.m_outputs[b1].outputValue(true); 
    if (this.m_instances.classAttribute().isNumeric())
      return arrayOfDouble; 
    double d = 0.0D;
    byte b2;
    for (b2 = 0; b2 < this.m_numClasses; b2++)
      d += arrayOfDouble[b2]; 
    if (d <= 0.0D)
      return null; 
    for (b2 = 0; b2 < this.m_numClasses; b2++)
      arrayOfDouble[b2] = arrayOfDouble[b2] / d; 
    return arrayOfDouble;
  }
  
  public Enumeration listOptions() {
    Vector vector = new Vector(14);
    vector.addElement(new Option("\tLearning Rate for the backpropagation algorithm.\n\t(Value should be between 0 - 1, Default = 0.3).", "L", 1, "-L <learning rate>"));
    vector.addElement(new Option("\tMomentum Rate for the backpropagation algorithm.\n\t(Value should be between 0 - 1, Default = 0.2).", "M", 1, "-M <momentum>"));
    vector.addElement(new Option("\tNumber of epochs to train through.\n\t(Default = 500).", "N", 1, "-N <number of epochs>"));
    vector.addElement(new Option("\tPercentage size of validation set to use to terminate training (if this is non zero it can pre-empt num of epochs.\n\t(Value should be between 0 - 100, Default = 0).", "V", 1, "-V <percentage size of validation set>"));
    vector.addElement(new Option("\tThe value used to seed the random number generator\t(Value should be >= 0 and and a long, Default = 0).", "S", 1, "-S <seed>"));
    vector.addElement(new Option("\tThe consequetive number of errors allowed for validation testing before the netwrok terminates.\t(Value should be > 0, Default = 20).", "E", 1, "-E <threshold for number of consequetive errors>"));
    vector.addElement(new Option("\tGUI will be opened.\n\t(Use this to bring up a GUI).", "G", 0, "-G"));
    vector.addElement(new Option("\tAutocreation of the network connections will NOT be done.\n\t(This will be ignored if -G is NOT set)", "A", 0, "-A"));
    vector.addElement(new Option("\tA NominalToBinary filter will NOT automatically be used.\n\t(Set this to not use a NominalToBinary filter).", "B", 0, "-B"));
    vector.addElement(new Option("\tThe hidden layers to be created for the network.\n\t(Value should be a list of comma seperated Natural numbers or the letters 'a' = (attribs + classes) / 2, 'i' = attribs, 'o' = classes, 't' = attribs .+ classes) For wildcard values,Default = a).", "H", 1, "-H <comma seperated numbers for nodes on each layer>"));
    vector.addElement(new Option("\tNormalizing a numeric class will NOT be done.\n\t(Set this to not normalize the class if it's numeric).", "C", 0, "-C"));
    vector.addElement(new Option("\tNormalizing the attributes will NOT be done.\n\t(Set this to not normalize the attributes).", "I", 0, "-I"));
    vector.addElement(new Option("\tReseting the network will NOT be allowed.\n\t(Set this to not allow the network to reset).", "R", 0, "-R"));
    vector.addElement(new Option("\tLearning rate decay will occur.\n\t(Set this to cause the learning rate to decay).", "D", 0, "-D"));
    return vector.elements();
  }
  
  public void setOptions(String[] paramArrayOfString) throws Exception {
    String str1 = Utils.getOption('L', paramArrayOfString);
    if (str1.length() != 0) {
      setLearningRate((new Double(str1)).doubleValue());
    } else {
      setLearningRate(0.3D);
    } 
    String str2 = Utils.getOption('M', paramArrayOfString);
    if (str2.length() != 0) {
      setMomentum((new Double(str2)).doubleValue());
    } else {
      setMomentum(0.2D);
    } 
    String str3 = Utils.getOption('N', paramArrayOfString);
    if (str3.length() != 0) {
      setTrainingTime(Integer.parseInt(str3));
    } else {
      setTrainingTime(500);
    } 
    String str4 = Utils.getOption('V', paramArrayOfString);
    if (str4.length() != 0) {
      setValidationSetSize(Integer.parseInt(str4));
    } else {
      setValidationSetSize(0);
    } 
    String str5 = Utils.getOption('S', paramArrayOfString);
    if (str5.length() != 0) {
      setRandomSeed(Long.parseLong(str5));
    } else {
      setRandomSeed(0L);
    } 
    String str6 = Utils.getOption('E', paramArrayOfString);
    if (str6.length() != 0) {
      setValidationThreshold(Integer.parseInt(str6));
    } else {
      setValidationThreshold(20);
    } 
    String str7 = Utils.getOption('H', paramArrayOfString);
    if (str7.length() != 0) {
      setHiddenLayers(str7);
    } else {
      setHiddenLayers("a");
    } 
    if (Utils.getFlag('G', paramArrayOfString)) {
      setGUI(true);
    } else {
      setGUI(false);
    } 
    if (Utils.getFlag('A', paramArrayOfString)) {
      setAutoBuild(false);
    } else {
      setAutoBuild(true);
    } 
    if (Utils.getFlag('B', paramArrayOfString)) {
      setNominalToBinaryFilter(false);
    } else {
      setNominalToBinaryFilter(true);
    } 
    if (Utils.getFlag('C', paramArrayOfString)) {
      setNormalizeNumericClass(false);
    } else {
      setNormalizeNumericClass(true);
    } 
    if (Utils.getFlag('I', paramArrayOfString)) {
      setNormalizeAttributes(false);
    } else {
      setNormalizeAttributes(true);
    } 
    if (Utils.getFlag('R', paramArrayOfString)) {
      setReset(false);
    } else {
      setReset(true);
    } 
    if (Utils.getFlag('D', paramArrayOfString)) {
      setDecay(true);
    } else {
      setDecay(false);
    } 
    Utils.checkForRemainingOptions(paramArrayOfString);
  }
  
  public String[] getOptions() {
    String[] arrayOfString = new String[21];
    byte b = 0;
    arrayOfString[b++] = "-L";
    arrayOfString[b++] = "" + getLearningRate();
    arrayOfString[b++] = "-M";
    arrayOfString[b++] = "" + getMomentum();
    arrayOfString[b++] = "-N";
    arrayOfString[b++] = "" + getTrainingTime();
    arrayOfString[b++] = "-V";
    arrayOfString[b++] = "" + getValidationSetSize();
    arrayOfString[b++] = "-S";
    arrayOfString[b++] = "" + getRandomSeed();
    arrayOfString[b++] = "-E";
    arrayOfString[b++] = "" + getValidationThreshold();
    arrayOfString[b++] = "-H";
    arrayOfString[b++] = getHiddenLayers();
    if (getGUI())
      arrayOfString[b++] = "-G"; 
    if (!getAutoBuild())
      arrayOfString[b++] = "-A"; 
    if (!getNominalToBinaryFilter())
      arrayOfString[b++] = "-B"; 
    if (!getNormalizeNumericClass())
      arrayOfString[b++] = "-C"; 
    if (!getNormalizeAttributes())
      arrayOfString[b++] = "-I"; 
    if (!getReset())
      arrayOfString[b++] = "-R"; 
    if (getDecay())
      arrayOfString[b++] = "-D"; 
    while (b < arrayOfString.length)
      arrayOfString[b++] = ""; 
    return arrayOfString;
  }
  
  public String toString() {
    StringBuffer stringBuffer = new StringBuffer(this.m_neuralNodes.length * 100);
    byte b;
    for (b = 0; b < this.m_neuralNodes.length; b++) {
      NeuralNode neuralNode = (NeuralNode)this.m_neuralNodes[b];
      double[] arrayOfDouble = neuralNode.getWeights();
      NeuralConnection[] arrayOfNeuralConnection = neuralNode.getInputs();
      if (neuralNode.getMethod() instanceof SigmoidUnit) {
        stringBuffer.append("Sigmoid ");
      } else if (neuralNode.getMethod() instanceof LinearUnit) {
        stringBuffer.append("Linear ");
      } 
      stringBuffer.append("Node " + neuralNode.getId() + "\n    Inputs    Weights\n");
      stringBuffer.append("    Threshold    " + arrayOfDouble[0] + "\n");
      for (byte b1 = 1; b1 < neuralNode.getNumInputs() + 1; b1++) {
        if ((arrayOfNeuralConnection[b1 - 1].getType() & 0x1) == 1) {
          stringBuffer.append("    Attrib " + this.m_instances.attribute(((NeuralEnd)arrayOfNeuralConnection[b1 - 1]).getLink()).name() + "    " + arrayOfDouble[b1] + "\n");
        } else {
          stringBuffer.append("    Node " + arrayOfNeuralConnection[b1 - 1].getId() + "    " + arrayOfDouble[b1] + "\n");
        } 
      } 
    } 
    for (b = 0; b < this.m_outputs.length; b++) {
      NeuralConnection[] arrayOfNeuralConnection = this.m_outputs[b].getInputs();
      stringBuffer.append("Class " + this.m_instances.classAttribute().value(this.m_outputs[b].getLink()) + "\n    Input\n");
      for (byte b1 = 0; b1 < this.m_outputs[b].getNumInputs(); b1++) {
        if ((arrayOfNeuralConnection[b1].getType() & 0x1) == 1) {
          stringBuffer.append("    Attrib " + this.m_instances.attribute(((NeuralEnd)arrayOfNeuralConnection[b1]).getLink()).name() + "\n");
        } else {
          stringBuffer.append("    Node " + arrayOfNeuralConnection[b1].getId() + "\n");
        } 
      } 
    } 
    return stringBuffer.toString();
  }
  
  public String globalInfo() {
    return "This neural network uses backpropagation to train.";
  }
  
  public String learningRateTipText() {
    return "The amount the weights are updated.";
  }
  
  public String momentumTipText() {
    return "Momentum applied to the weights during updating.";
  }
  
  public String autoBuildTipText() {
    return "Adds and connects up hidden layers in the network.";
  }
  
  public String randomSeedTipText() {
    return "Seed used to initialise the random number generator.Random numbers are used for setting the initial weights of the connections betweem nodes, and also for shuffling the training data.";
  }
  
  public String validationThresholdTipText() {
    return "Used to terminate validation testing.The value here dictates how many times in a row the validation set error can get worse before training is terminated.";
  }
  
  public String GUITipText() {
    return "Brings up a gui interface. This will allow the pausing and altering of the nueral network during training.\n\n* To add a node left click (this node will be automatically selected, ensure no other nodes were selected).\n* To select a node left click on it either while no other node is selected or while holding down the control key (this toggles that node as being selected and not selected.\n* To connect a node, first have the start node(s) selected, then click either the end node or on an empty space (this will create a new node that is connected with the selected nodes). The selection status of nodes will stay the same after the connection. (Note these are directed connections, also a connection between two nodes will not be established more than once and certain connections that are deemed to be invalid will not be made).\n* To remove a connection select one of the connected node(s) in the connection and then right click the other node (it does not matter whether the node is the start or end the connection will be removed).\n* To remove a node right click it while no other nodes (including it) are selected. (This will also remove all connections to it)\n.* To deselect a node either left click it while holding down control, or right click on empty space.\n* The raw inputs are provided from the labels on the left.\n* The red nodes are hidden layers.\n* The orange nodes are the output nodes.\n* The labels on the right show the class the output node represents. Note that with a numeric class the output node will automatically be made into an unthresholded linear unit.\n\nAlterations to the neural network can only be done while the network is not running, This also applies to the learning rate and other fields on the control panel.\n\n* You can accept the network as being finished at any time.\n* The network is automatically paused at the beginning.\n* There is a running indication of what epoch the network is up to and what the (rough) error for that epoch was (or for the validation if that is being used). Note that this error value is based on a network that changes as the value is computed. (also depending on whether the class is normalized will effect the error reported for numeric classes.\n* Once the network is done it will pause again and either wait to be accepted or trained more.\n\nNote that if the gui is not set the network will not require any interaction.\n";
  }
  
  public String validationSetSizeTipText() {
    return "The percentage size of the validation set.(The training will continue until it is observed that the error on the validation set has been consistently getting worse, or if the training time is reached).\nIf This is set to zero no validation set will be used and instead the network will train for the specified number of epochs.";
  }
  
  public String trainingTimeTipText() {
    return "The number of epochs to train through. If the validation set is non-zero then it can terminate the network early";
  }
  
  public String nominalToBinaryFilterTipText() {
    return "This will preprocess the instances with the filter. This could help improve performance if there are nominal attributes in the data.";
  }
  
  public String hiddenLayersTipText() {
    return "This defines the hidden layers of the neural network. This is a list of positive whole numbers. 1 for each hidden layer. Comma seperated. To have no hidden layers put a single 0 here. This will only be used if autobuild is set. There are also wildcard values 'a' = (attribs + classes) / 2, 'i' = attribs, 'o' = classes , 't' = attribs + classes.";
  }
  
  public String normalizeNumericClassTipText() {
    return "This will normalize the class if it's numeric. This could help improve performance of the network, It normalizes the class to be between -1 and 1. Note that this is only internally, the output will be scaled back to the original range.";
  }
  
  public String normalizeAttributesTipText() {
    return "This will normalize the attributes. This could help improve performance of the network. This is not reliant on the class being numeric. This will also normalize nominal attributes as well (after they have been run through the nominal to binary filter if that is in use) so that the nominal values are between -1 and 1";
  }
  
  public String resetTipText() {
    return "This will allow the network to reset with a lower learning rate. If the network diverges from the answer this will automatically reset the network with a lower learning rate and begin training again. This option is only available if the gui is not set. Note that if the network diverges but isn't allowed to reset it will fail the training process and return an error message.";
  }
  
  public String decayTipText() {
    return "This will cause the learning rate to decrease. This will divide the starting learning rate by the epoch number, to determine what the current learning rate should be. This may help to stop the network from diverging from the target output, as well as improve general performance. Note that the decaying learning rate will not be shown in the gui, only the original learning rate. If the learning rate is changed in the gui, this is treated as the starting learning rate.";
  }
  
  class ControlPanel extends JPanel {
    public JButton m_startStop;
    
    public JButton m_acceptButton;
    
    public JPanel m_epochsLabel;
    
    public JLabel m_totalEpochsLabel;
    
    public JTextField m_changeEpochs;
    
    public JLabel m_learningLabel;
    
    public JLabel m_momentumLabel;
    
    public JTextField m_changeLearning;
    
    public JTextField m_changeMomentum;
    
    public JPanel m_errorLabel;
    
    private final MultilayerPerceptron this$0;
    
    public ControlPanel(MultilayerPerceptron this$0) {
      this.this$0 = this$0;
      setBorder(BorderFactory.createTitledBorder("Controls"));
      this.m_totalEpochsLabel = new JLabel("Num Of Epochs  ");
      this.m_epochsLabel = (JPanel)new Object(this);
      this.m_epochsLabel.setFont(this.m_totalEpochsLabel.getFont());
      this.m_changeEpochs = new JTextField();
      this.m_changeEpochs.setText("" + this$0.m_numEpochs);
      this.m_errorLabel = (JPanel)new Object(this);
      this.m_errorLabel.setFont(this.m_epochsLabel.getFont());
      this.m_learningLabel = new JLabel("Learning Rate = ");
      this.m_momentumLabel = new JLabel("Momentum = ");
      this.m_changeLearning = new JTextField();
      this.m_changeMomentum = new JTextField();
      this.m_changeLearning.setText("" + this$0.m_learningRate);
      this.m_changeMomentum.setText("" + this$0.m_momentum);
      setLayout(new BorderLayout(15, 10));
      this$0.m_stopIt = true;
      this$0.m_accepted = false;
      this.m_startStop = new JButton("Start");
      this.m_startStop.setActionCommand("Start");
      this.m_acceptButton = new JButton("Accept");
      this.m_acceptButton.setActionCommand("Accept");
      JPanel jPanel1 = new JPanel();
      jPanel1.setLayout(new BoxLayout(jPanel1, 1));
      jPanel1.add(this.m_startStop);
      jPanel1.add(this.m_acceptButton);
      add(jPanel1, "West");
      JPanel jPanel2 = new JPanel();
      jPanel2.setLayout(new BoxLayout(jPanel2, 1));
      Box box = new Box(0);
      box.add(this.m_epochsLabel);
      jPanel2.add(box);
      box = new Box(0);
      Component component = Box.createGlue();
      box.add(this.m_totalEpochsLabel);
      box.add(this.m_changeEpochs);
      this.m_changeEpochs.setMaximumSize(new Dimension(200, 20));
      box.add(component);
      jPanel2.add(box);
      box = new Box(0);
      box.add(this.m_errorLabel);
      jPanel2.add(box);
      add(jPanel2, "Center");
      jPanel2 = new JPanel();
      jPanel2.setLayout(new BoxLayout(jPanel2, 1));
      box = new Box(0);
      component = Box.createGlue();
      box.add(this.m_learningLabel);
      box.add(this.m_changeLearning);
      this.m_changeLearning.setMaximumSize(new Dimension(200, 20));
      box.add(component);
      jPanel2.add(box);
      box = new Box(0);
      component = Box.createGlue();
      box.add(this.m_momentumLabel);
      box.add(this.m_changeMomentum);
      this.m_changeMomentum.setMaximumSize(new Dimension(200, 20));
      box.add(component);
      jPanel2.add(box);
      add(jPanel2, "East");
      this.m_startStop.addActionListener((ActionListener)new Object(this));
      this.m_acceptButton.addActionListener((ActionListener)new Object(this));
      this.m_changeEpochs.addActionListener((ActionListener)new Object(this));
    }
  }
  
  private class NodePanel extends JPanel {
    private final MultilayerPerceptron this$0;
    
    public NodePanel(MultilayerPerceptron this$0) {
      this.this$0 = this$0;
      addMouseListener((MouseListener)new Object(this, this$0));
    }
    
    private void selection(FastVector param1FastVector, boolean param1Boolean1, boolean param1Boolean2) {
      if (param1FastVector == null) {
        this.this$0.m_selected.removeAllElements();
        repaint();
        return;
      } 
      if ((param1Boolean1 || this.this$0.m_selected.size() == 0) && param1Boolean2) {
        boolean bool = false;
        for (byte b = 0; b < param1FastVector.size(); b++) {
          bool = false;
          for (byte b1 = 0; b1 < this.this$0.m_selected.size(); b1++) {
            if (param1FastVector.elementAt(b) == this.this$0.m_selected.elementAt(b1)) {
              this.this$0.m_selected.removeElementAt(b1);
              bool = true;
              break;
            } 
          } 
          if (!bool)
            this.this$0.m_selected.addElement(param1FastVector.elementAt(b)); 
        } 
        repaint();
        return;
      } 
      if (param1Boolean2) {
        for (byte b = 0; b < this.this$0.m_selected.size(); b++) {
          for (byte b1 = 0; b1 < param1FastVector.size(); b1++)
            NeuralConnection.connect((NeuralConnection)this.this$0.m_selected.elementAt(b), (NeuralConnection)param1FastVector.elementAt(b1)); 
        } 
      } else if (this.this$0.m_selected.size() > 0) {
        for (byte b = 0; b < this.this$0.m_selected.size(); b++) {
          for (byte b1 = 0; b1 < param1FastVector.size(); b1++) {
            NeuralConnection.disconnect((NeuralConnection)this.this$0.m_selected.elementAt(b), (NeuralConnection)param1FastVector.elementAt(b1));
            NeuralConnection.disconnect((NeuralConnection)param1FastVector.elementAt(b1), (NeuralConnection)this.this$0.m_selected.elementAt(b));
          } 
        } 
      } else {
        for (byte b = 0; b < param1FastVector.size(); b++) {
          ((NeuralConnection)param1FastVector.elementAt(b)).removeAllInputs();
          ((NeuralConnection)param1FastVector.elementAt(b)).removeAllOutputs();
          this.this$0.removeNode((NeuralConnection)param1FastVector.elementAt(b));
        } 
      } 
      repaint();
    }
    
    public void paintComponent(Graphics param1Graphics) {
      super.paintComponent(param1Graphics);
      int i = getWidth();
      int j = getHeight();
      if (25 * this.this$0.m_numAttributes > 25 * this.this$0.m_numClasses && 25 * this.this$0.m_numAttributes > j) {
        setSize(i, 25 * this.this$0.m_numAttributes);
      } else if (25 * this.this$0.m_numClasses > j) {
        setSize(i, 25 * this.this$0.m_numClasses);
      } else {
        setSize(i, j);
      } 
      j = getHeight();
      byte b;
      for (b = 0; b < this.this$0.m_numAttributes; b++)
        this.this$0.m_inputs[b].drawInputLines(param1Graphics, i, j); 
      for (b = 0; b < this.this$0.m_numClasses; b++) {
        this.this$0.m_outputs[b].drawInputLines(param1Graphics, i, j);
        this.this$0.m_outputs[b].drawOutputLines(param1Graphics, i, j);
      } 
      for (b = 0; b < this.this$0.m_neuralNodes.length; b++)
        this.this$0.m_neuralNodes[b].drawInputLines(param1Graphics, i, j); 
      for (b = 0; b < this.this$0.m_numAttributes; b++)
        this.this$0.m_inputs[b].drawNode(param1Graphics, i, j); 
      for (b = 0; b < this.this$0.m_numClasses; b++)
        this.this$0.m_outputs[b].drawNode(param1Graphics, i, j); 
      for (b = 0; b < this.this$0.m_neuralNodes.length; b++)
        this.this$0.m_neuralNodes[b].drawNode(param1Graphics, i, j); 
      for (b = 0; b < this.this$0.m_selected.size(); b++)
        ((NeuralConnection)this.this$0.m_selected.elementAt(b)).drawHighlight(param1Graphics, i, j); 
    }
  }
  
  protected class NeuralEnd extends NeuralConnection {
    private int m_link;
    
    private boolean m_input;
    
    private final MultilayerPerceptron this$0;
    
    public NeuralEnd(MultilayerPerceptron this$0, String param1String) {
      super(param1String);
      this.this$0 = this$0;
      this.m_link = 0;
      this.m_input = true;
    }
    
    public boolean onUnit(Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4) {
      FontMetrics fontMetrics = param1Graphics.getFontMetrics();
      int i = (int)(this.m_x * param1Int3) - fontMetrics.stringWidth(this.m_id) / 2;
      int j = (int)(this.m_y * param1Int4) - fontMetrics.getHeight() / 2;
      return !(param1Int1 < i || param1Int1 > i + fontMetrics.stringWidth(this.m_id) + 4 || param1Int2 < j || param1Int2 > j + fontMetrics.getHeight() + fontMetrics.getDescent() + 4);
    }
    
    public void drawNode(Graphics param1Graphics, int param1Int1, int param1Int2) {
      if ((this.m_type & 0x1) == 1) {
        param1Graphics.setColor(Color.green);
      } else {
        param1Graphics.setColor(Color.orange);
      } 
      FontMetrics fontMetrics = param1Graphics.getFontMetrics();
      int i = (int)(this.m_x * param1Int1) - fontMetrics.stringWidth(this.m_id) / 2;
      int j = (int)(this.m_y * param1Int2) - fontMetrics.getHeight() / 2;
      param1Graphics.fill3DRect(i, j, fontMetrics.stringWidth(this.m_id) + 4, fontMetrics.getHeight() + fontMetrics.getDescent() + 4, true);
      param1Graphics.setColor(Color.black);
      param1Graphics.drawString(this.m_id, i + 2, j + fontMetrics.getHeight() + 2);
    }
    
    public void drawHighlight(Graphics param1Graphics, int param1Int1, int param1Int2) {
      param1Graphics.setColor(Color.black);
      FontMetrics fontMetrics = param1Graphics.getFontMetrics();
      int i = (int)(this.m_x * param1Int1) - fontMetrics.stringWidth(this.m_id) / 2;
      int j = (int)(this.m_y * param1Int2) - fontMetrics.getHeight() / 2;
      param1Graphics.fillRect(i - 2, j - 2, fontMetrics.stringWidth(this.m_id) + 8, fontMetrics.getHeight() + fontMetrics.getDescent() + 8);
      drawNode(param1Graphics, param1Int1, param1Int2);
    }
    
    public double outputValue(boolean param1Boolean) {
      if (Double.isNaN(this.m_unitValue) && param1Boolean)
        if (this.m_input) {
          if (this.this$0.m_currentInstance.isMissing(this.m_link)) {
            this.m_unitValue = 0.0D;
          } else {
            this.m_unitValue = this.this$0.m_currentInstance.value(this.m_link);
          } 
        } else {
          this.m_unitValue = 0.0D;
          for (byte b = 0; b < this.m_numInputs; b++)
            this.m_unitValue += this.m_inputList[b].outputValue(true); 
          if (this.this$0.m_numeric && this.this$0.m_normalizeClass)
            this.m_unitValue = this.m_unitValue * this.this$0.m_attributeRanges[this.this$0.m_instances.classIndex()] + this.this$0.m_attributeBases[this.this$0.m_instances.classIndex()]; 
        }  
      return this.m_unitValue;
    }
    
    public double errorValue(boolean param1Boolean) {
      if (!Double.isNaN(this.m_unitValue) && Double.isNaN(this.m_unitError) && param1Boolean)
        if (this.m_input) {
          this.m_unitError = 0.0D;
          for (byte b = 0; b < this.m_numOutputs; b++)
            this.m_unitError += this.m_outputList[b].errorValue(true); 
        } else if (this.this$0.m_currentInstance.classIsMissing()) {
          this.m_unitError = 0.1D;
        } else if (this.this$0.m_instances.classAttribute().isNominal()) {
          if (this.this$0.m_currentInstance.classValue() == this.m_link) {
            this.m_unitError = 1.0D - this.m_unitValue;
          } else {
            this.m_unitError = 0.0D - this.m_unitValue;
          } 
        } else if (this.this$0.m_numeric) {
          if (this.this$0.m_normalizeClass) {
            if (this.this$0.m_attributeRanges[this.this$0.m_instances.classIndex()] == 0.0D) {
              this.m_unitError = 0.0D;
            } else {
              this.m_unitError = (this.this$0.m_currentInstance.classValue() - this.m_unitValue) / this.this$0.m_attributeRanges[this.this$0.m_instances.classIndex()];
            } 
          } else {
            this.m_unitError = this.this$0.m_currentInstance.classValue() - this.m_unitValue;
          } 
        }  
      return this.m_unitError;
    }
    
    public void reset() {
      if (!Double.isNaN(this.m_unitValue) || !Double.isNaN(this.m_unitError)) {
        this.m_unitValue = Double.NaN;
        this.m_unitError = Double.NaN;
        this.m_weightsUpdated = false;
        for (byte b = 0; b < this.m_numInputs; b++)
          this.m_inputList[b].reset(); 
      } 
    }
    
    public void setLink(boolean param1Boolean, int param1Int) throws Exception {
      this.m_input = param1Boolean;
      if (param1Boolean) {
        this.m_type = 1;
      } else {
        this.m_type = 2;
      } 
      if (param1Int < 0 || (param1Boolean && param1Int > this.this$0.m_instances.numAttributes()) || (!param1Boolean && this.this$0.m_instances.classAttribute().isNominal() && param1Int > this.this$0.m_instances.classAttribute().numValues())) {
        this.m_link = 0;
      } else {
        this.m_link = param1Int;
      } 
    }
    
    public int getLink() {
      return this.m_link;
    }
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\classifiers\functions\MultilayerPerceptron.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */