package weka.experiment;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Enumeration;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Option;
import weka.core.Utils;

public class PairedCorrectedTTester extends PairedTTester {
  public PairedStats calculateStatistics(Instance paramInstance, int paramInt1, int paramInt2, int paramInt3) throws Exception {
    if (this.m_Instances.attribute(paramInt3).type() != 0)
      throw new Exception("Comparison column " + (paramInt3 + 1) + " (" + this.m_Instances.attribute(paramInt3).name() + ") is not numeric"); 
    if (!this.m_ResultsetsValid)
      prepareData(); 
    PairedTTester.Resultset resultset1 = (PairedTTester.Resultset)this.m_Resultsets.elementAt(paramInt1);
    PairedTTester.Resultset resultset2 = (PairedTTester.Resultset)this.m_Resultsets.elementAt(paramInt2);
    FastVector fastVector1 = resultset1.dataset(paramInstance);
    FastVector fastVector2 = resultset2.dataset(paramInstance);
    String str = templateString(paramInstance);
    if (fastVector1 == null)
      throw new Exception("No results for dataset=" + str + " for resultset=" + resultset1.templateString()); 
    if (fastVector2 == null)
      throw new Exception("No results for dataset=" + str + " for resultset=" + resultset2.templateString()); 
    if (fastVector1.size() != fastVector2.size())
      throw new Exception("Results for dataset=" + str + " differ in size for resultset=" + resultset1.templateString() + " and resultset=" + resultset2.templateString()); 
    double d = 0.0D;
    byte b1 = -1;
    byte b2 = -1;
    for (byte b3 = 0; b3 < this.m_Instances.numAttributes(); b3++) {
      if (this.m_Instances.attribute(b3).name().toLowerCase().equals("number_of_training_instances")) {
        b1 = b3;
      } else if (this.m_Instances.attribute(b3).name().toLowerCase().equals("number_of_testing_instances")) {
        b2 = b3;
      } 
    } 
    if (b1 >= 0 && b2 >= 0) {
      double d1 = 0.0D;
      double d2 = 0.0D;
      for (byte b = 0; b < fastVector1.size(); b++) {
        Instance instance = (Instance)fastVector1.elementAt(b);
        d1 += instance.value(b1);
        d2 += instance.value(b2);
      } 
      d = d2 / d1;
    } 
    PairedStatsCorrected pairedStatsCorrected = new PairedStatsCorrected(this.m_SignificanceLevel, d);
    for (byte b4 = 0; b4 < fastVector1.size(); b4++) {
      Instance instance1 = (Instance)fastVector1.elementAt(b4);
      Instance instance2 = (Instance)fastVector2.elementAt(b4);
      if (instance1.isMissing(paramInt3))
        throw new Exception("Instance has missing value in comparison column!\n" + instance1); 
      if (instance2.isMissing(paramInt3))
        throw new Exception("Instance has missing value in comparison column!\n" + instance2); 
      if (instance1.value(this.m_RunColumn) != instance2.value(this.m_RunColumn))
        System.err.println("Run numbers do not match!\n" + instance1 + instance2); 
      if (this.m_FoldColumn != -1 && instance1.value(this.m_FoldColumn) != instance2.value(this.m_FoldColumn))
        System.err.println("Fold numbers do not match!\n" + instance1 + instance2); 
      double d1 = instance1.value(paramInt3);
      double d2 = instance2.value(paramInt3);
      pairedStatsCorrected.add(d1, d2);
    } 
    pairedStatsCorrected.calculateDerived();
    return pairedStatsCorrected;
  }
  
  public static void main(String[] paramArrayOfString) {
    try {
      PairedCorrectedTTester pairedCorrectedTTester = new PairedCorrectedTTester();
      String str1 = Utils.getOption('t', paramArrayOfString);
      String str2 = Utils.getOption('c', paramArrayOfString);
      String str3 = Utils.getOption('b', paramArrayOfString);
      boolean bool1 = Utils.getFlag('s', paramArrayOfString);
      boolean bool2 = Utils.getFlag('r', paramArrayOfString);
      try {
        if (str1.length() == 0 || str2.length() == 0)
          throw new Exception("-t and -c options are required"); 
        pairedCorrectedTTester.setOptions(paramArrayOfString);
        Utils.checkForRemainingOptions(paramArrayOfString);
      } catch (Exception exception) {
        String str = "";
        Enumeration enumeration = pairedCorrectedTTester.listOptions();
        while (enumeration.hasMoreElements()) {
          Option option = enumeration.nextElement();
          str = str + option.synopsis() + '\n' + option.description() + '\n';
        } 
        throw new Exception("Usage:\n\n-t <file>\n\tSet the dataset containing data to evaluate\n-b <index>\n\tSet the resultset to base comparisons against (optional)\n-c <index>\n\tSet the column to perform a comparison on\n-s\n\tSummarize wins over all resultset pairs\n\n-r\n\tGenerate a resultset ranking\n\n" + str);
      } 
      Instances instances = new Instances(new BufferedReader(new FileReader(str1)));
      pairedCorrectedTTester.setInstances(instances);
      int i = Integer.parseInt(str2) - 1;
      System.out.println(pairedCorrectedTTester.header(i));
      if (bool2) {
        System.out.println(pairedCorrectedTTester.multiResultsetRanking(i));
      } else if (bool1) {
        System.out.println(pairedCorrectedTTester.multiResultsetSummary(i));
      } else {
        System.out.println(pairedCorrectedTTester.resultsetKey());
        if (str3.length() == 0) {
          for (byte b = 0; b < pairedCorrectedTTester.getNumResultsets(); b++)
            System.out.println(pairedCorrectedTTester.multiResultsetFull(b, i)); 
        } else {
          int j = Integer.parseInt(str3) - 1;
          System.out.println(pairedCorrectedTTester.multiResultsetFull(j, i));
        } 
      } 
    } catch (Exception exception) {
      exception.printStackTrace();
      System.err.println(exception.getMessage());
    } 
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\experiment\PairedCorrectedTTester.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */