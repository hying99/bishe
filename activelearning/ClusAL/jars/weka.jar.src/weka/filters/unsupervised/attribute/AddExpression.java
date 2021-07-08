package weka.filters.unsupervised.attribute;

import java.io.Serializable;
import java.util.Enumeration;
import java.util.Stack;
import java.util.StringTokenizer;
import java.util.Vector;
import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Option;
import weka.core.OptionHandler;
import weka.core.SparseInstance;
import weka.core.Utils;
import weka.filters.Filter;
import weka.filters.StreamableFilter;
import weka.filters.UnsupervisedFilter;

public class AddExpression extends Filter implements UnsupervisedFilter, StreamableFilter, OptionHandler {
  private String m_infixExpression = "a1^2";
  
  private Stack m_operatorStack = new Stack();
  
  private static final String OPERATORS = "+-*/()^lbcesfhrtn";
  
  private static final String UNARY_FUNCTIONS = "lbcesfhrtn";
  
  private Vector m_postFixExpVector;
  
  private boolean m_signMod = false;
  
  private String m_previousTok = "";
  
  private String m_attributeName = "expression";
  
  private boolean m_Debug = false;
  
  public String globalInfo() {
    return "An instance filter that creates a new attribute by applying a mathematical expression to existing attributes. The expression can contain attribute references and numeric constants. Supported opperators are :  +, -, *, /, ^, log, abs, cos, exp, sqrt, floor, ceil, rint, tan, sin, (, ). Attributes are specified by prefixing with 'a', eg. a7 is attribute number 7 (starting from 1). Example expression : a1^2*a5/log(a7*4.0).";
  }
  
  private void handleOperand(String paramString) throws Exception {
    if (paramString.indexOf('a') != -1) {
      this.m_postFixExpVector.addElement(new AttributeOperand(this, paramString, this.m_signMod));
    } else {
      try {
        this.m_postFixExpVector.addElement(new NumericOperand(this, paramString, this.m_signMod));
      } catch (NumberFormatException numberFormatException) {
        throw new Exception("Trouble parsing numeric constant");
      } 
    } 
    this.m_signMod = false;
  }
  
  private void handleOperator(String paramString) throws Exception {
    boolean bool = true;
    char c = paramString.charAt(0);
    if (c == ')') {
      String str = " ";
      do {
        str = this.m_operatorStack.pop();
        if (str.charAt(0) == '(')
          continue; 
        this.m_postFixExpVector.addElement(new Operator(this, str.charAt(0)));
      } while (str.charAt(0) != '(');
    } else {
      int i = infixPriority(paramString.charAt(0));
      while (!this.m_operatorStack.empty() && stackPriority(((String)this.m_operatorStack.peek()).charAt(0)) >= i) {
        if (this.m_previousTok.length() == 1 && isOperator(this.m_previousTok.charAt(0)) && this.m_previousTok.charAt(0) != ')') {
          if (paramString.charAt(0) == '-') {
            this.m_signMod = true;
          } else {
            this.m_signMod = false;
          } 
          bool = false;
          break;
        } 
        String str = this.m_operatorStack.pop();
        this.m_postFixExpVector.addElement(new Operator(this, str.charAt(0)));
      } 
      if (this.m_postFixExpVector.size() == 0 && paramString.charAt(0) == '-') {
        this.m_signMod = true;
        bool = false;
      } 
      if (bool)
        this.m_operatorStack.push(paramString); 
    } 
  }
  
  private void convertInfixToPostfix(String paramString) throws Exception {
    paramString = Utils.removeSubstring(paramString, " ");
    paramString = Utils.replaceSubstring(paramString, "log", "l");
    paramString = Utils.replaceSubstring(paramString, "abs", "b");
    paramString = Utils.replaceSubstring(paramString, "cos", "c");
    paramString = Utils.replaceSubstring(paramString, "exp", "e");
    paramString = Utils.replaceSubstring(paramString, "sqrt", "s");
    paramString = Utils.replaceSubstring(paramString, "floor", "f");
    paramString = Utils.replaceSubstring(paramString, "ceil", "h");
    paramString = Utils.replaceSubstring(paramString, "rint", "r");
    paramString = Utils.replaceSubstring(paramString, "tan", "t");
    paramString = Utils.replaceSubstring(paramString, "sin", "n");
    StringTokenizer stringTokenizer = new StringTokenizer(paramString, "+-*/()^lbcesfhrtn", true);
    this.m_postFixExpVector = new Vector();
    while (stringTokenizer.hasMoreTokens()) {
      String str = stringTokenizer.nextToken();
      if (str.length() > 1) {
        handleOperand(str);
      } else if (isOperator(str.charAt(0))) {
        handleOperator(str);
      } else {
        handleOperand(str);
      } 
      this.m_previousTok = str;
    } 
    while (!this.m_operatorStack.empty()) {
      String str = this.m_operatorStack.pop();
      if (str.charAt(0) == '(' || str.charAt(0) == ')')
        throw new Exception("Mis-matched parenthesis!"); 
      this.m_postFixExpVector.addElement(new Operator(this, str.charAt(0)));
    } 
  }
  
  private void evaluateExpression(double[] paramArrayOfdouble) throws Exception {
    Stack stack = new Stack();
    for (byte b = 0; b < this.m_postFixExpVector.size(); b++) {
      Operator operator = (Operator)this.m_postFixExpVector.elementAt(b);
      if (operator instanceof NumericOperand) {
        stack.push(new Double(((NumericOperand)operator).m_numericConst));
      } else if (operator instanceof AttributeOperand) {
        double d = paramArrayOfdouble[((AttributeOperand)operator).m_attributeIndex];
        if (d == Instance.missingValue()) {
          paramArrayOfdouble[paramArrayOfdouble.length - 1] = Instance.missingValue();
          break;
        } 
        if (((AttributeOperand)operator).m_negative)
          d = -d; 
        stack.push(new Double(d));
      } else if (operator instanceof Operator) {
        char c = operator.m_operator;
        if (isUnaryFunction(c)) {
          double d1 = ((Double)stack.pop()).doubleValue();
          double d2 = ((Operator)operator).applyFunction(d1);
          stack.push(new Double(d2));
        } else {
          double d1 = ((Double)stack.pop()).doubleValue();
          double d2 = ((Double)stack.pop()).doubleValue();
          double d3 = operator.applyOperator(d2, d1);
          stack.push(new Double(d3));
        } 
      } else {
        throw new Exception("Unknown object in postfix vector!");
      } 
    } 
    if (stack.size() != 1)
      throw new Exception("Problem applying function"); 
    Double double_ = stack.pop();
    if (double_.isNaN() || double_.isInfinite()) {
      paramArrayOfdouble[paramArrayOfdouble.length - 1] = Instance.missingValue();
    } else {
      paramArrayOfdouble[paramArrayOfdouble.length - 1] = double_.doubleValue();
    } 
  }
  
  private boolean isOperator(char paramChar) {
    return !("+-*/()^lbcesfhrtn".indexOf(paramChar) == -1);
  }
  
  private boolean isUnaryFunction(char paramChar) {
    return !("lbcesfhrtn".indexOf(paramChar) == -1);
  }
  
  private int infixPriority(char paramChar) {
    switch (paramChar) {
      case 'b':
      case 'c':
      case 'e':
      case 'f':
      case 'h':
      case 'l':
      case 'n':
      case 'r':
      case 's':
      case 't':
        return 3;
      case '^':
        return 2;
      case '*':
        return 2;
      case '/':
        return 2;
      case '+':
        return 1;
      case '-':
        return 1;
      case '(':
        return 4;
      case ')':
        return 0;
    } 
    throw new IllegalArgumentException("Unrecognized operator:" + paramChar);
  }
  
  private int stackPriority(char paramChar) {
    switch (paramChar) {
      case 'b':
      case 'c':
      case 'e':
      case 'f':
      case 'h':
      case 'l':
      case 'n':
      case 'r':
      case 's':
      case 't':
        return 3;
      case '^':
        return 2;
      case '*':
        return 2;
      case '/':
        return 2;
      case '+':
        return 1;
      case '-':
        return 1;
      case '(':
        return 0;
      case ')':
        return -1;
    } 
    throw new IllegalArgumentException("Unrecognized operator:" + paramChar);
  }
  
  public Enumeration listOptions() {
    Vector vector = new Vector(3);
    vector.addElement(new Option("\tSpecify the expression to apply. Eg a1^2*a5/log(a7*4.0).\n\tSupported opperators: ,+, -, *, /, ^, log, abs, cos, \n\texp, sqrt, floor, ceil, rint, tan, sin, (, )", "E", 1, "-E <expression>"));
    vector.addElement(new Option("\tSpecify the name for the new attribute. (default is the expression provided with -E)", "N", 1, "-N <name>"));
    vector.addElement(new Option("\tDebug. Names attribute with the postfix parse of the expression.", "D", 0, "-D"));
    return vector.elements();
  }
  
  public void setOptions(String[] paramArrayOfString) throws Exception {
    String str1 = Utils.getOption('E', paramArrayOfString);
    if (str1.length() != 0) {
      setExpression(str1);
    } else {
      throw new Exception("Must specify an expression with the -E option");
    } 
    String str2 = Utils.getOption('N', paramArrayOfString);
    if (str2.length() != 0)
      setName(str2); 
    setDebug(Utils.getFlag('D', paramArrayOfString));
  }
  
  public String[] getOptions() {
    String[] arrayOfString = new String[5];
    byte b = 0;
    arrayOfString[b++] = "-E";
    arrayOfString[b++] = getExpression();
    arrayOfString[b++] = "-N";
    arrayOfString[b++] = getName();
    if (getDebug())
      arrayOfString[b++] = "-D"; 
    while (b < arrayOfString.length)
      arrayOfString[b++] = ""; 
    return arrayOfString;
  }
  
  public String nameTipText() {
    return "Set the name of the new attribute.";
  }
  
  public void setName(String paramString) {
    this.m_attributeName = paramString;
  }
  
  public String getName() {
    return this.m_attributeName;
  }
  
  public String debugTipText() {
    return "Set debug mode. If true then the new attribute will be named with the postfix parse of the supplied expression.";
  }
  
  public void setDebug(boolean paramBoolean) {
    this.m_Debug = paramBoolean;
  }
  
  public boolean getDebug() {
    return this.m_Debug;
  }
  
  public String expressionTipText() {
    return "Set the math expression to apply. Eg. a1^2*a5/log(a7*4.0)";
  }
  
  public void setExpression(String paramString) {
    this.m_infixExpression = paramString;
  }
  
  public String getExpression() {
    return this.m_infixExpression;
  }
  
  public boolean setInputFormat(Instances paramInstances) throws Exception {
    Attribute attribute;
    convertInfixToPostfix(new String(this.m_infixExpression));
    super.setInputFormat(paramInstances);
    Instances instances = new Instances(paramInstances, 0);
    if (this.m_Debug) {
      attribute = new Attribute(this.m_postFixExpVector.toString());
    } else if (this.m_attributeName.compareTo("expression") != 0) {
      attribute = new Attribute(this.m_attributeName);
    } else {
      attribute = new Attribute(this.m_infixExpression);
    } 
    instances.insertAttributeAt(attribute, paramInstances.numAttributes());
    setOutputFormat(instances);
    return true;
  }
  
  public boolean input(Instance paramInstance) throws Exception {
    Instance instance;
    if (getInputFormat() == null)
      throw new IllegalStateException("No input instance format defined"); 
    if (this.m_NewBatch) {
      resetQueue();
      this.m_NewBatch = false;
    } 
    double[] arrayOfDouble = new double[paramInstance.numAttributes() + 1];
    for (byte b = 0; b < paramInstance.numAttributes(); b++) {
      if (paramInstance.isMissing(b)) {
        arrayOfDouble[b] = Instance.missingValue();
      } else {
        arrayOfDouble[b] = paramInstance.value(b);
      } 
    } 
    evaluateExpression(arrayOfDouble);
    SparseInstance sparseInstance = null;
    if (paramInstance instanceof SparseInstance) {
      sparseInstance = new SparseInstance(paramInstance.weight(), arrayOfDouble);
    } else {
      instance = new Instance(paramInstance.weight(), arrayOfDouble);
    } 
    copyStringValues(instance, false, paramInstance.dataset(), getOutputFormat());
    instance.setDataset(getOutputFormat());
    push(instance);
    return true;
  }
  
  public static void main(String[] paramArrayOfString) {
    try {
      if (Utils.getFlag('b', paramArrayOfString)) {
        Filter.batchFilterFile(new AddExpression(), paramArrayOfString);
      } else {
        Filter.filterFile(new AddExpression(), paramArrayOfString);
      } 
    } catch (Exception exception) {
      System.out.println(exception.getMessage());
    } 
  }
  
  private class Operator implements Serializable {
    protected char m_operator;
    
    private final AddExpression this$0;
    
    public Operator(AddExpression this$0, char param1Char) {
      this.this$0 = this$0;
      if (!this$0.isOperator(param1Char))
        throw new IllegalArgumentException("Unrecognized operator:" + param1Char); 
      this.m_operator = param1Char;
    }
    
    protected double applyOperator(double param1Double1, double param1Double2) {
      switch (this.m_operator) {
        case '+':
          return param1Double1 + param1Double2;
        case '-':
          return param1Double1 - param1Double2;
        case '*':
          return param1Double1 * param1Double2;
        case '/':
          return param1Double1 / param1Double2;
        case '^':
          return Math.pow(param1Double1, param1Double2);
      } 
      return Double.NaN;
    }
    
    protected double applyFunction(double param1Double) {
      switch (this.m_operator) {
        case 'l':
          return Math.log(param1Double);
        case 'b':
          return Math.abs(param1Double);
        case 'c':
          return Math.cos(param1Double);
        case 'e':
          return Math.exp(param1Double);
        case 's':
          return Math.sqrt(param1Double);
        case 'f':
          return Math.floor(param1Double);
        case 'h':
          return Math.ceil(param1Double);
        case 'r':
          return Math.rint(param1Double);
        case 't':
          return Math.tan(param1Double);
        case 'n':
          return Math.sin(param1Double);
      } 
      return Double.NaN;
    }
    
    public String toString() {
      return "" + this.m_operator;
    }
  }
  
  private class NumericOperand implements Serializable {
    protected double m_numericConst;
    
    private final AddExpression this$0;
    
    public NumericOperand(AddExpression this$0, String param1String, boolean param1Boolean) throws Exception {
      this.this$0 = this$0;
      this.m_numericConst = Double.valueOf(param1String).doubleValue();
      if (param1Boolean)
        this.m_numericConst *= -1.0D; 
    }
    
    public String toString() {
      return "" + this.m_numericConst;
    }
  }
  
  private class AttributeOperand implements Serializable {
    protected int m_attributeIndex;
    
    protected boolean m_negative;
    
    private final AddExpression this$0;
    
    public AttributeOperand(AddExpression this$0, String param1String, boolean param1Boolean) throws Exception {
      this.this$0 = this$0;
      this.m_attributeIndex = Integer.parseInt(param1String.substring(1)) - 1;
      this.m_negative = param1Boolean;
    }
    
    public String toString() {
      String str = "";
      if (this.m_negative)
        str = str + '-'; 
      return str + "a" + (this.m_attributeIndex + 1);
    }
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\filter\\unsupervised\attribute\AddExpression.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */