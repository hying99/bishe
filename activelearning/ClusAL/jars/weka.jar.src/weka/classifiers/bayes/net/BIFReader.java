package weka.classifiers.bayes.net;

import java.io.File;
import java.util.StringTokenizer;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.CharacterData;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import weka.classifiers.bayes.BayesNet;
import weka.classifiers.bayes.net.estimate.DiscreteEstimatorBayes;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instances;
import weka.estimators.Estimator;

public class BIFReader extends BayesNet {
  private int[] m_nPositionX;
  
  private int[] m_nPositionY;
  
  private int[] m_order;
  
  String m_sFile;
  
  public BIFReader processFile(String paramString) throws Exception {
    this.m_sFile = paramString;
    DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
    documentBuilderFactory.setValidating(false);
    Document document = documentBuilderFactory.newDocumentBuilder().parse(new File(paramString));
    buildInstances(document, paramString);
    buildStructure(document);
    return this;
  }
  
  public String getFileName() {
    return this.m_sFile;
  }
  
  void buildStructure(Document paramDocument) throws Exception {
    this.m_Distributions = new Estimator[this.m_Instances.numAttributes()][];
    for (byte b = 0; b < this.m_Instances.numAttributes(); b++) {
      String str1 = this.m_Instances.attribute(b).name();
      Element element = getDefinition(paramDocument, str1);
      FastVector fastVector = getParentNodes(element);
      int i;
      for (i = 0; i < fastVector.size(); i++) {
        Node node = ((Node)fastVector.elementAt(i)).getFirstChild();
        String str = ((CharacterData)node).getData();
        int k = getNode(str);
        this.m_ParentSets[b].addParent(k, this.m_Instances);
      } 
      i = this.m_ParentSets[b].getCardinalityOfParents();
      int j = this.m_Instances.attribute(b).numValues();
      this.m_Distributions[b] = new Estimator[i];
      for (byte b1 = 0; b1 < i; b1++)
        this.m_Distributions[b][b1] = (Estimator)new DiscreteEstimatorBayes(j, 0.0D); 
      String str2 = getTable(element);
      StringTokenizer stringTokenizer = new StringTokenizer(str2.toString());
      for (byte b2 = 0; b2 < i; b2++) {
        DiscreteEstimatorBayes discreteEstimatorBayes = (DiscreteEstimatorBayes)this.m_Distributions[b][b2];
        for (byte b3 = 0; b3 < j; b3++) {
          String str = stringTokenizer.nextToken();
          discreteEstimatorBayes.addValue(b3, (new Double(str)).doubleValue());
        } 
      } 
    } 
  }
  
  public void Sync(BayesNet paramBayesNet) throws Exception {
    int i = this.m_Instances.numAttributes();
    if (i != paramBayesNet.m_Instances.numAttributes())
      throw new Exception("Cannot synchronize networks: different number of attributes."); 
    this.m_order = new int[i];
    for (byte b = 0; b < i; b++) {
      String str = paramBayesNet.getNodeName(b);
      this.m_order[getNode(str)] = b;
    } 
  }
  
  public int getNode(String paramString) throws Exception {
    for (byte b = 0; b < this.m_Instances.numAttributes(); b++) {
      if (this.m_Instances.attribute(b).name().equals(paramString))
        return b; 
    } 
    throw new Exception("Could not find node [[" + paramString + "]]");
  }
  
  void buildInstances(Document paramDocument, String paramString) throws Exception {
    NodeList nodeList = selectAllNames(paramDocument);
    if (nodeList.getLength() > 0)
      paramString = ((CharacterData)nodeList.item(0).getFirstChild()).getData(); 
    nodeList = selectAllVariables(paramDocument);
    int i = nodeList.getLength();
    FastVector fastVector = new FastVector(i);
    this.m_nPositionX = new int[nodeList.getLength()];
    this.m_nPositionY = new int[nodeList.getLength()];
    for (byte b = 0; b < nodeList.getLength(); b++) {
      FastVector fastVector1 = selectOutCome(nodeList.item(b));
      int j = fastVector1.size();
      FastVector fastVector2 = new FastVector(j + 1);
      for (byte b1 = 0; b1 < j; b1++) {
        Node node = ((Node)fastVector1.elementAt(b1)).getFirstChild();
        String str1 = ((CharacterData)node).getData();
        if (str1 == null)
          str1 = "Value" + (b1 + 1); 
        fastVector2.addElement(str1);
      } 
      FastVector fastVector3 = selectName(nodeList.item(b));
      if (fastVector3.size() == 0)
        throw new Exception("No name specified for variable"); 
      String str = ((CharacterData)((Node)fastVector3.elementAt(0)).getFirstChild()).getData();
      Attribute attribute = new Attribute(str, fastVector2);
      fastVector.addElement(attribute);
      fastVector1 = selectProperty(nodeList.item(b));
      j = fastVector1.size();
      for (byte b2 = 0; b2 < j; b2++) {
        Node node = ((Node)fastVector1.elementAt(b2)).getFirstChild();
        String str1 = ((CharacterData)node).getData();
        if (str1.startsWith("position")) {
          int k = str1.indexOf('(');
          int m = str1.indexOf(',');
          int n = str1.indexOf(')');
          String str2 = str1.substring(k + 1, m).trim();
          String str3 = str1.substring(m + 1, n).trim();
          try {
            this.m_nPositionX[b] = Integer.parseInt(str2);
            this.m_nPositionY[b] = Integer.parseInt(str3);
          } catch (NumberFormatException numberFormatException) {
            System.err.println("Wrong number format in position :(" + str2 + "," + str3 + ")");
            this.m_nPositionX[b] = 0;
            this.m_nPositionY[b] = 0;
          } 
        } 
      } 
    } 
    this.m_Instances = new Instances(paramString, fastVector, 100);
    this.m_Instances.setClassIndex(i - 1);
    setUseADTree(false);
    initStructure();
  }
  
  NodeList selectAllNames(Document paramDocument) throws Exception {
    return paramDocument.getElementsByTagName("NAME");
  }
  
  NodeList selectAllVariables(Document paramDocument) throws Exception {
    return paramDocument.getElementsByTagName("VARIABLE");
  }
  
  Element getDefinition(Document paramDocument, String paramString) throws Exception {
    NodeList nodeList = paramDocument.getElementsByTagName("DEFINITION");
    for (byte b = 0; b < nodeList.getLength(); b++) {
      Node node1 = nodeList.item(b);
      FastVector fastVector = selectElements(node1, "FOR");
      Node node2 = (Node)fastVector.elementAt(0);
      if (fastVector.size() > 0 && node2.toString().equals("<FOR>" + paramString + "</FOR>"))
        return (Element)node1; 
    } 
    throw new Exception("Could not find definition for ((" + paramString + "))");
  }
  
  FastVector getParentNodes(Node paramNode) throws Exception {
    return selectElements(paramNode, "GIVEN");
  }
  
  String getTable(Node paramNode) throws Exception {
    FastVector fastVector = selectElements(paramNode, "TABLE");
    null = ((Node)fastVector.elementAt(0)).toString();
    null = null.replaceFirst("<TABLE>", "");
    null = null.replaceFirst("</TABLE>", "");
    return null.replaceAll("\\n", " ");
  }
  
  FastVector selectOutCome(Node paramNode) throws Exception {
    return selectElements(paramNode, "OUTCOME");
  }
  
  FastVector selectName(Node paramNode) throws Exception {
    return selectElements(paramNode, "NAME");
  }
  
  FastVector selectProperty(Node paramNode) throws Exception {
    return selectElements(paramNode, "PROPERTY");
  }
  
  FastVector selectElements(Node paramNode, String paramString) throws Exception {
    NodeList nodeList = paramNode.getChildNodes();
    FastVector fastVector = new FastVector();
    for (byte b = 0; b < nodeList.getLength(); b++) {
      Node node = nodeList.item(b);
      if (node.getNodeType() == 1 && node.getNodeName().equals(paramString))
        fastVector.addElement(node); 
    } 
    return fastVector;
  }
  
  public int missingArcs(BayesNet paramBayesNet) {
    try {
      Sync(paramBayesNet);
      byte b1 = 0;
      for (byte b2 = 0; b2 < this.m_Instances.numAttributes(); b2++) {
        for (byte b = 0; b < this.m_ParentSets[b2].getNrOfParents(); b++) {
          int i = this.m_ParentSets[b2].getParent(b);
          if (!paramBayesNet.getParentSet(this.m_order[b2]).contains(this.m_order[i]) && !paramBayesNet.getParentSet(this.m_order[i]).contains(this.m_order[b2]))
            b1++; 
        } 
      } 
      return b1;
    } catch (Exception exception) {
      System.err.println(exception.getMessage());
      return 0;
    } 
  }
  
  public int extraArcs(BayesNet paramBayesNet) {
    try {
      Sync(paramBayesNet);
      byte b1 = 0;
      for (byte b2 = 0; b2 < this.m_Instances.numAttributes(); b2++) {
        for (byte b = 0; b < paramBayesNet.getParentSet(this.m_order[b2]).getNrOfParents(); b++) {
          int i = this.m_order[paramBayesNet.getParentSet(this.m_order[b2]).getParent(b)];
          if (!this.m_ParentSets[b2].contains(i) && !this.m_ParentSets[i].contains(b2))
            b1++; 
        } 
      } 
      return b1;
    } catch (Exception exception) {
      System.err.println(exception.getMessage());
      return 0;
    } 
  }
  
  public double divergence(BayesNet paramBayesNet) {
    try {
      Sync(paramBayesNet);
      double d = 0.0D;
      int i = this.m_Instances.numAttributes();
      int[] arrayOfInt1 = new int[i];
      for (byte b1 = 0; b1 < i; b1++)
        arrayOfInt1[b1] = this.m_Instances.attribute(b1).numValues(); 
      int[] arrayOfInt2 = new int[i];
      byte b2 = 0;
      while (b2 < i) {
        arrayOfInt2[b2] = arrayOfInt2[b2] + 1;
        while (b2 < i && arrayOfInt2[b2] == this.m_Instances.attribute(b2).numValues()) {
          arrayOfInt2[b2] = 0;
          if (++b2 < i)
            arrayOfInt2[b2] = arrayOfInt2[b2] + 1; 
        } 
        if (b2 < i) {
          b2 = 0;
          double d1 = 1.0D;
          for (byte b3 = 0; b3 < i; b3++) {
            int j = 0;
            for (byte b = 0; b < this.m_ParentSets[b3].getNrOfParents(); b++) {
              int k = this.m_ParentSets[b3].getParent(b);
              j = j * arrayOfInt1[k] + arrayOfInt2[k];
            } 
            d1 *= this.m_Distributions[b3][j].getProbability(arrayOfInt2[b3]);
          } 
          double d2 = 1.0D;
          for (byte b4 = 0; b4 < i; b4++) {
            int j = 0;
            for (byte b = 0; b < paramBayesNet.getParentSet(this.m_order[b4]).getNrOfParents(); b++) {
              int k = this.m_order[paramBayesNet.getParentSet(this.m_order[b4]).getParent(b)];
              j = j * arrayOfInt1[k] + arrayOfInt2[k];
            } 
            d2 *= paramBayesNet.m_Distributions[this.m_order[b4]][j].getProbability(arrayOfInt2[b4]);
          } 
          if (d1 > 0.0D && d2 > 0.0D)
            d += d1 * Math.log(d2 / d1); 
        } 
      } 
      return d;
    } catch (Exception exception) {
      System.err.println(exception.getMessage());
      return 0.0D;
    } 
  }
  
  public int reversedArcs(BayesNet paramBayesNet) {
    try {
      Sync(paramBayesNet);
      byte b1 = 0;
      for (byte b2 = 0; b2 < this.m_Instances.numAttributes(); b2++) {
        for (byte b = 0; b < this.m_ParentSets[b2].getNrOfParents(); b++) {
          int i = this.m_ParentSets[b2].getParent(b);
          if (!paramBayesNet.getParentSet(this.m_order[b2]).contains(this.m_order[i]) && paramBayesNet.getParentSet(this.m_order[i]).contains(this.m_order[b2]))
            b1++; 
        } 
      } 
      return b1;
    } catch (Exception exception) {
      System.err.println(exception.getMessage());
      return 0;
    } 
  }
  
  public static void main(String[] paramArrayOfString) {
    try {
      BIFReader bIFReader = new BIFReader();
      bIFReader.processFile(paramArrayOfString[0]);
      System.out.println(bIFReader.toString());
    } catch (Throwable throwable) {
      throwable.printStackTrace();
    } 
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\classifiers\bayes\net\BIFReader.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */