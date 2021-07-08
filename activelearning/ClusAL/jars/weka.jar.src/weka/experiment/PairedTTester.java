package weka.experiment;

import java.io.BufferedReader;
import java.io.FileReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.Vector;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Option;
import weka.core.OptionHandler;
import weka.core.Range;
import weka.core.Utils;

public class PairedTTester implements OptionHandler {
  protected Instances m_Instances;
  
  protected int m_RunColumn = 0;
  
  protected int m_RunColumnSet = -1;
  
  protected int m_FoldColumn = -1;
  
  protected double m_SignificanceLevel = 0.05D;
  
  protected Range m_DatasetKeyColumnsRange = new Range();
  
  protected int[] m_DatasetKeyColumns;
  
  protected DatasetSpecifiers m_DatasetSpecifiers = new DatasetSpecifiers(this);
  
  protected Range m_ResultsetKeyColumnsRange = new Range();
  
  protected int[] m_ResultsetKeyColumns;
  
  protected int[] m_DisplayedResultsets = null;
  
  protected FastVector m_Resultsets = new FastVector();
  
  protected boolean m_ResultsetsValid;
  
  protected boolean m_ShowStdDevs = false;
  
  protected boolean m_latexOutput = false;
  
  protected boolean m_csvOutput = false;
  
  protected int m_MeanPrec = 2;
  
  protected int m_StdDevPrec = 2;
  
  protected String templateString(Instance paramInstance) {
    String str = "";
    for (byte b = 0; b < this.m_DatasetKeyColumns.length; b++)
      str = str + paramInstance.toString(this.m_DatasetKeyColumns[b]) + ' '; 
    if (str.startsWith("weka.classifiers."))
      str = str.substring("weka.classifiers.".length()); 
    return str.trim();
  }
  
  public void setMeanPrec(int paramInt) {
    this.m_MeanPrec = paramInt;
  }
  
  public int getMeanPrec() {
    return this.m_MeanPrec;
  }
  
  public void setStdDevPrec(int paramInt) {
    this.m_StdDevPrec = paramInt;
  }
  
  public int getStdDevPrec() {
    return this.m_StdDevPrec;
  }
  
  public void setProduceLatex(boolean paramBoolean) {
    this.m_latexOutput = paramBoolean;
    if (this.m_latexOutput)
      setProduceCSV(false); 
  }
  
  public boolean getProduceLatex() {
    return this.m_latexOutput;
  }
  
  public void setProduceCSV(boolean paramBoolean) {
    this.m_csvOutput = paramBoolean;
    if (this.m_csvOutput)
      setProduceLatex(false); 
  }
  
  public boolean getProduceCSV() {
    return this.m_csvOutput;
  }
  
  public void setShowStdDevs(boolean paramBoolean) {
    this.m_ShowStdDevs = paramBoolean;
  }
  
  public boolean getShowStdDevs() {
    return this.m_ShowStdDevs;
  }
  
  protected void prepareData() throws Exception {
    if (this.m_Instances == null)
      throw new Exception("No instances have been set"); 
    if (this.m_RunColumnSet == -1) {
      this.m_RunColumn = this.m_Instances.numAttributes() - 1;
    } else {
      this.m_RunColumn = this.m_RunColumnSet;
    } 
    if (this.m_ResultsetKeyColumnsRange == null)
      throw new Exception("No result specifier columns have been set"); 
    this.m_ResultsetKeyColumnsRange.setUpper(this.m_Instances.numAttributes() - 1);
    this.m_ResultsetKeyColumns = this.m_ResultsetKeyColumnsRange.getSelection();
    if (this.m_DatasetKeyColumnsRange == null)
      throw new Exception("No dataset specifier columns have been set"); 
    this.m_DatasetKeyColumnsRange.setUpper(this.m_Instances.numAttributes() - 1);
    this.m_DatasetKeyColumns = this.m_DatasetKeyColumnsRange.getSelection();
    this.m_Resultsets.removeAllElements();
    this.m_DatasetSpecifiers.removeAllSpecifiers();
    byte b;
    for (b = 0; b < this.m_Instances.numInstances(); b++) {
      Instance instance = this.m_Instances.instance(b);
      if (instance.isMissing(this.m_RunColumn))
        throw new Exception("Instance has missing value in run column!\n" + instance); 
      byte b1;
      for (b1 = 0; b1 < this.m_ResultsetKeyColumns.length; b1++) {
        if (instance.isMissing(this.m_ResultsetKeyColumns[b1]))
          throw new Exception("Instance has missing value in resultset key column " + (this.m_ResultsetKeyColumns[b1] + 1) + "!\n" + instance); 
      } 
      for (b1 = 0; b1 < this.m_DatasetKeyColumns.length; b1++) {
        if (instance.isMissing(this.m_DatasetKeyColumns[b1]))
          throw new Exception("Instance has missing value in dataset key column " + (this.m_DatasetKeyColumns[b1] + 1) + "!\n" + instance); 
      } 
      b1 = 0;
      for (byte b2 = 0; b2 < this.m_Resultsets.size(); b2++) {
        Resultset resultset = (Resultset)this.m_Resultsets.elementAt(b2);
        if (resultset.matchesTemplate(instance)) {
          resultset.add(instance);
          b1 = 1;
          break;
        } 
      } 
      if (b1 == 0) {
        Resultset resultset = new Resultset(this, instance);
        this.m_Resultsets.addElement(resultset);
      } 
      this.m_DatasetSpecifiers.add(instance);
    } 
    for (b = 0; b < this.m_Resultsets.size(); b++) {
      Resultset resultset = (Resultset)this.m_Resultsets.elementAt(b);
      if (this.m_FoldColumn >= 0)
        resultset.sort(this.m_FoldColumn); 
      resultset.sort(this.m_RunColumn);
    } 
    this.m_ResultsetsValid = true;
  }
  
  public int getNumDatasets() {
    if (!this.m_ResultsetsValid)
      try {
        prepareData();
      } catch (Exception exception) {
        exception.printStackTrace();
        return 0;
      }  
    return this.m_DatasetSpecifiers.numSpecifiers();
  }
  
  public int getNumResultsets() {
    if (!this.m_ResultsetsValid)
      try {
        prepareData();
      } catch (Exception exception) {
        exception.printStackTrace();
        return 0;
      }  
    return this.m_Resultsets.size();
  }
  
  public int getNumDisplayedResultsets() {
    if (!this.m_ResultsetsValid)
      try {
        prepareData();
      } catch (Exception exception) {
        exception.printStackTrace();
        return 0;
      }  
    byte b1 = 0;
    for (byte b2 = 0; b2 < getNumResultsets(); b2++) {
      if (displayResultset(b2))
        b1++; 
    } 
    return b1;
  }
  
  public String getResultsetName(int paramInt) {
    if (!this.m_ResultsetsValid)
      try {
        prepareData();
      } catch (Exception exception) {
        exception.printStackTrace();
        return null;
      }  
    return ((Resultset)this.m_Resultsets.elementAt(paramInt)).templateString();
  }
  
  public boolean displayResultset(int paramInt) {
    boolean bool = true;
    if (this.m_DisplayedResultsets != null) {
      bool = false;
      for (byte b = 0; b < this.m_DisplayedResultsets.length; b++) {
        if (this.m_DisplayedResultsets[b] == paramInt) {
          bool = true;
          break;
        } 
      } 
    } 
    return bool;
  }
  
  public PairedStats calculateStatistics(Instance paramInstance, int paramInt1, int paramInt2, int paramInt3) throws Exception {
    if (this.m_Instances.attribute(paramInt3).type() != 0)
      throw new Exception("Comparison column " + (paramInt3 + 1) + " (" + this.m_Instances.attribute(paramInt3).name() + ") is not numeric"); 
    if (!this.m_ResultsetsValid)
      prepareData(); 
    Resultset resultset1 = (Resultset)this.m_Resultsets.elementAt(paramInt1);
    Resultset resultset2 = (Resultset)this.m_Resultsets.elementAt(paramInt2);
    FastVector fastVector1 = resultset1.dataset(paramInstance);
    FastVector fastVector2 = resultset2.dataset(paramInstance);
    String str = templateString(paramInstance);
    if (fastVector1 == null)
      throw new Exception("No results for dataset=" + str + " for resultset=" + resultset1.templateString()); 
    if (fastVector2 == null)
      throw new Exception("No results for dataset=" + str + " for resultset=" + resultset2.templateString()); 
    if (fastVector1.size() != fastVector2.size())
      throw new Exception("Results for dataset=" + str + " differ in size for resultset=" + resultset1.templateString() + " and resultset=" + resultset2.templateString()); 
    PairedStats pairedStats = new PairedStats(this.m_SignificanceLevel);
    for (byte b = 0; b < fastVector1.size(); b++) {
      Instance instance1 = (Instance)fastVector1.elementAt(b);
      Instance instance2 = (Instance)fastVector2.elementAt(b);
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
      pairedStats.add(d1, d2);
    } 
    pairedStats.calculateDerived();
    System.err.println("Differences stats:\n" + pairedStats.differencesStats);
    return pairedStats;
  }
  
  public String resultsetKey() {
    if (!this.m_ResultsetsValid)
      try {
        prepareData();
      } catch (Exception exception) {
        exception.printStackTrace();
        return exception.getMessage();
      }  
    String str = "";
    for (byte b = 0; b < getNumResultsets(); b++)
      str = str + "(" + (b + 1) + ") " + getResultsetName(b) + '\n'; 
    return str + '\n';
  }
  
  public String header(int paramInt) {
    if (!this.m_ResultsetsValid)
      try {
        prepareData();
      } catch (Exception exception) {
        exception.printStackTrace();
        return exception.getMessage();
      }  
    return "Analysing:  " + this.m_Instances.attribute(paramInt).name() + '\n' + "Datasets:   " + getNumDatasets() + '\n' + "Resultsets: " + getNumResultsets() + '\n' + "Confidence: " + getSignificanceLevel() + " (two tailed)\n" + "Date:       " + (new SimpleDateFormat()).format(new Date()) + "\n\n";
  }
  
  public int[][] multiResultsetWins(int paramInt, int[][] paramArrayOfint) throws Exception {
    int i = getNumResultsets();
    int[][] arrayOfInt = new int[i][i];
    for (byte b = 0; b < i; b++) {
      for (int j = b + 1; j < i; j++) {
        System.err.print("Comparing (" + (b + 1) + ") with (" + (j + 1) + ")\r");
        System.err.flush();
        for (byte b1 = 0; b1 < getNumDatasets(); b1++) {
          try {
            PairedStats pairedStats = calculateStatistics(this.m_DatasetSpecifiers.specifier(b1), b, j, paramInt);
            if (pairedStats.differencesSignificance < 0) {
              arrayOfInt[b][j] = arrayOfInt[b][j] + 1;
            } else if (pairedStats.differencesSignificance > 0) {
              arrayOfInt[j][b] = arrayOfInt[j][b] + 1;
            } 
            if (pairedStats.differencesStats.mean < 0.0D) {
              paramArrayOfint[b][j] = paramArrayOfint[b][j] + 1;
            } else if (pairedStats.differencesStats.mean > 0.0D) {
              paramArrayOfint[j][b] = paramArrayOfint[j][b] + 1;
            } 
          } catch (Exception exception) {
            exception.printStackTrace();
            System.err.println(exception.getMessage());
          } 
        } 
      } 
    } 
    return arrayOfInt;
  }
  
  public String multiResultsetSummary(int paramInt) throws Exception {
    int i = getNumResultsets();
    int[][] arrayOfInt1 = new int[i][i];
    int[][] arrayOfInt2 = multiResultsetWins(paramInt, arrayOfInt1);
    int j = 1 + Math.max((int)(Math.log(i) / Math.log(10.0D)), (int)(Math.log(getNumDatasets()) / Math.log(10.0D)));
    String str1 = "";
    String str2 = "";
    if (this.m_latexOutput) {
      str1 = str1 + "{\\centering\n";
      str1 = str1 + "\\begin{table}[thb]\n\\caption{\\label{labelname}Table Caption}\n";
      str1 = str1 + "\\footnotesize\n";
      str1 = str1 + "\\begin{tabular}{l";
    } 
    byte b;
    for (b = 0; b < i; b++) {
      if (displayResultset(b)) {
        if (this.m_latexOutput) {
          str2 = str2 + " &";
          str1 = str1 + "c";
        } 
        str2 = str2 + ' ' + Utils.padLeft("" + (char)(97 + b % 26), j * 2 + 3);
      } 
    } 
    if (this.m_latexOutput) {
      str1 = str1 + "}\\\\\n\\hline\n";
      str1 = str1 + str2 + " \\\\\n\\hline\n";
    } else {
      str1 = str1 + str2 + "  (No. of datasets where [col] >> [row])\n";
    } 
    for (b = 0; b < i; b++) {
      if (displayResultset(b)) {
        for (byte b1 = 0; b1 < i; b1++) {
          if (displayResultset(b1)) {
            if (this.m_latexOutput && b1 == 0)
              str1 = str1 + (char)(97 + b % 26); 
            if (b1 == b) {
              if (this.m_latexOutput) {
                str1 = str1 + " & - ";
              } else {
                str1 = str1 + ' ' + Utils.padLeft("-", j * 2 + 3);
              } 
            } else if (this.m_latexOutput) {
              str1 = str1 + "& " + arrayOfInt1[b][b1] + " (" + arrayOfInt2[b][b1] + ") ";
            } else {
              str1 = str1 + ' ' + Utils.padLeft("" + arrayOfInt1[b][b1] + " (" + arrayOfInt2[b][b1] + ")", j * 2 + 3);
            } 
          } 
        } 
        if (!this.m_latexOutput) {
          str1 = str1 + " | " + (char)(97 + b % 26) + " = " + getResultsetName(b) + '\n';
        } else {
          str1 = str1 + "\\\\\n";
        } 
      } 
    } 
    if (this.m_latexOutput)
      str1 = str1 + "\\hline\n\\end{tabular} \\footnotesize \\par\n\\end{table}}"; 
    return str1;
  }
  
  public String multiResultsetRanking(int paramInt) throws Exception {
    String str;
    int i = getNumResultsets();
    int[][] arrayOfInt1 = new int[i][i];
    int[][] arrayOfInt2 = multiResultsetWins(paramInt, arrayOfInt1);
    int[] arrayOfInt3 = new int[i];
    int[] arrayOfInt4 = new int[i];
    int[] arrayOfInt5 = new int[i];
    int j;
    for (j = 0; j < arrayOfInt2.length; j++) {
      for (byte b = 0; b < (arrayOfInt2[j]).length; b++) {
        arrayOfInt3[b] = arrayOfInt3[b] + arrayOfInt2[j][b];
        arrayOfInt5[b] = arrayOfInt5[b] + arrayOfInt2[j][b];
        arrayOfInt4[j] = arrayOfInt4[j] + arrayOfInt2[j][b];
        arrayOfInt5[j] = arrayOfInt5[j] - arrayOfInt2[j][b];
      } 
    } 
    j = Math.max(arrayOfInt3[Utils.maxIndex(arrayOfInt3)], arrayOfInt4[Utils.maxIndex(arrayOfInt4)]);
    int k = Math.max(2 + (int)(Math.log(j) / Math.log(10.0D)), ">-<".length());
    if (this.m_latexOutput) {
      str = "\\begin{table}[thb]\n\\caption{\\label{labelname}Table Caption}\n\\footnotesize\n{\\centering \\begin{tabular}{rlll}\\\\\n\\hline\n";
      str = str + "Resultset & Wins$-$ & Wins & Losses \\\\\n& Losses & & \\\\\n\\hline\n";
    } else {
      str = Utils.padLeft(">-<", k) + ' ' + Utils.padLeft(">", k) + ' ' + Utils.padLeft("<", k) + " Resultset\n";
    } 
    int[] arrayOfInt6 = Utils.sort(arrayOfInt5);
    for (int m = i - 1; m >= 0; m--) {
      int n = arrayOfInt6[m];
      if (displayResultset(n))
        if (this.m_latexOutput) {
          str = str + "(" + (n + 1) + ") & " + Utils.padLeft("" + arrayOfInt5[n], k) + " & " + Utils.padLeft("" + arrayOfInt3[n], k) + " & " + Utils.padLeft("" + arrayOfInt4[n], k) + "\\\\\n";
        } else {
          str = str + Utils.padLeft("" + arrayOfInt5[n], k) + ' ' + Utils.padLeft("" + arrayOfInt3[n], k) + ' ' + Utils.padLeft("" + arrayOfInt4[n], k) + ' ' + getResultsetName(n) + '\n';
        }  
    } 
    if (this.m_latexOutput)
      str = str + "\\hline\n\\end{tabular} \\footnotesize \\par}\n\\end{table}"; 
    return str;
  }
  
  private String multiResultsetFullLatex(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    StringBuffer stringBuffer = new StringBuffer(1000);
    String str = "";
    int i = getNumDisplayedResultsets() * 2;
    if (this.m_ShowStdDevs)
      i += getNumDisplayedResultsets(); 
    stringBuffer.append("\\begin{table}[thb]\n\\caption{\\label{labelname}Table Caption}\n");
    if (!this.m_ShowStdDevs) {
      stringBuffer.append("\\footnotesize\n");
    } else {
      stringBuffer.append("\\scriptsize\n");
    } 
    if (!this.m_ShowStdDevs) {
      stringBuffer.append("{\\centering \\begin{tabular}{ll");
    } else {
      stringBuffer.append("{\\centering \\begin{tabular}{lr@{\\hspace{0cm}}l");
    } 
    byte b1;
    for (b1 = 0; b1 < getNumResultsets(); b1++) {
      if (displayResultset(b1) && b1 != paramInt1)
        if (!this.m_ShowStdDevs) {
          stringBuffer.append("l@{\\hspace{0.1cm}}l");
        } else {
          stringBuffer.append("r@{\\hspace{0cm}}l@{\\hspace{0cm}}r");
        }  
    } 
    stringBuffer.append("}\n\\\\\n\\hline\n");
    if (!this.m_ShowStdDevs) {
      stringBuffer.append("Data Set & (" + (paramInt1 + 1) + ")");
    } else {
      stringBuffer.append("Data Set & \\multicolumn{2}{c}{(" + (paramInt1 + 1) + ")}");
    } 
    for (b1 = 0; b1 < getNumResultsets(); b1++) {
      if (displayResultset(b1) && b1 != paramInt1)
        if (!this.m_ShowStdDevs) {
          stringBuffer.append("& (" + (b1 + 1) + ") & ");
        } else {
          stringBuffer.append("& \\multicolumn{3}{c}{(" + (b1 + 1) + ")} ");
        }  
    } 
    stringBuffer.append("\\\\\n\\hline\n");
    b1 = 25;
    int j = paramInt3 + 5 + this.m_MeanPrec;
    if (this.m_ShowStdDevs)
      j += paramInt4 + 8 + this.m_StdDevPrec; 
    for (byte b2 = 0; b2 < getNumDatasets(); b2++) {
      String str1 = templateString(this.m_DatasetSpecifiers.specifier(b2)).replace('_', '-');
      try {
        PairedStats pairedStats = calculateStatistics(this.m_DatasetSpecifiers.specifier(b2), paramInt1, paramInt1, paramInt2);
        str1 = Utils.padRight(str1, b1);
        stringBuffer.append(str1);
        if (!this.m_ShowStdDevs) {
          str = padIt(pairedStats.xStats.mean, paramInt3 + 5, this.m_MeanPrec);
        } else {
          str = padIt(pairedStats.xStats.mean, paramInt3 + 5, this.m_MeanPrec) + "$\\pm$";
          if (Double.isNaN(pairedStats.xStats.stdDev)) {
            str = str + "&" + Utils.doubleToString(0.0D, paramInt4 + 3, this.m_StdDevPrec) + " ";
          } else {
            str = str + "&" + padIt(pairedStats.xStats.stdDev, paramInt4 + 3, this.m_StdDevPrec) + " ";
          } 
        } 
        if (str.length() < j - 2)
          str = Utils.padLeft(str, j - 2); 
        stringBuffer.append("& ").append(str);
        for (byte b = 0; b < getNumResultsets(); b++) {
          if (displayResultset(b) && b != paramInt1)
            try {
              pairedStats = calculateStatistics(this.m_DatasetSpecifiers.specifier(b2), paramInt1, b, paramInt2);
              String str2 = "         ";
              if (pairedStats.differencesSignificance < 0) {
                str2 = "$\\circ$  ";
              } else if (pairedStats.differencesSignificance > 0) {
                str2 = "$\\bullet$";
              } 
              if (!this.m_ShowStdDevs) {
                str = padIt(pairedStats.yStats.mean, paramInt3 + 5, this.m_MeanPrec);
              } else {
                str = padIt(pairedStats.yStats.mean, paramInt3 + 5, this.m_MeanPrec) + "$\\pm$";
                if (Double.isNaN(pairedStats.yStats.stdDev)) {
                  str = str + "&" + Utils.doubleToString(0.0D, paramInt4 + 3, this.m_StdDevPrec) + " ";
                } else {
                  str = str + "&" + padIt(pairedStats.yStats.stdDev, paramInt4 + 3, this.m_StdDevPrec) + " ";
                } 
              } 
              if (str.length() < j - 2)
                str = Utils.padLeft(str, j - 2); 
              stringBuffer.append(" & ").append(str).append(" & ").append(str2);
            } catch (Exception exception) {
              exception.printStackTrace();
              stringBuffer.append(Utils.padLeft("", j + 1));
            }  
        } 
        stringBuffer.append("\\\\\n");
      } catch (Exception exception) {
        exception.printStackTrace();
      } 
    } 
    stringBuffer.append("\\hline\n\\multicolumn{" + i + "}{c}{$\\circ$, $\\bullet$" + " statistically significant improvement or degradation}" + "\\\\\n\\end{tabular} ");
    if (!this.m_ShowStdDevs) {
      stringBuffer.append("\\footnotesize ");
    } else {
      stringBuffer.append("\\scriptsize ");
    } 
    stringBuffer.append("\\par}\n\\end{table}\n");
    return stringBuffer.toString();
  }
  
  private String padIt(double paramDouble, int paramInt1, int paramInt2) {
    String str = Utils.doubleToString(paramDouble, paramInt1, paramInt2);
    int i = 0;
    int j = str.length();
    str = str.trim();
    if (str.indexOf(".") == -1) {
      if (paramInt2 > 0)
        str = str + "."; 
      i = 0;
    } else {
      i = str.substring(str.indexOf(".") + 1).length();
    } 
    for (int k = i; k < paramInt2; k++)
      str = str + "0"; 
    while (str.length() < j)
      str = " " + str; 
    return str;
  }
  
  private String multiResultsetFullPlainText(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    StringBuffer stringBuffer1 = new StringBuffer(1000);
    String str = "";
    byte b1 = 25;
    int i = paramInt3 + 5 + this.m_MeanPrec;
    if (this.m_ShowStdDevs)
      i += paramInt4 + 3 + this.m_StdDevPrec; 
    StringBuffer stringBuffer2 = new StringBuffer(Utils.padRight("Dataset", b1));
    stringBuffer2.append(' ');
    StringBuffer stringBuffer3 = new StringBuffer(Utils.padLeft("(" + (paramInt1 + 1) + ") " + getResultsetName(paramInt1), i + 3));
    stringBuffer2.append(stringBuffer3);
    StringBuffer stringBuffer4 = new StringBuffer(Utils.padRight("", b1));
    while (stringBuffer4.length() < stringBuffer2.length())
      stringBuffer4.append('-'); 
    stringBuffer4.append("---");
    stringBuffer2.append(" | ");
    for (byte b2 = 0; b2 < getNumResultsets(); b2++) {
      if (displayResultset(b2) && b2 != paramInt1) {
        stringBuffer3 = new StringBuffer(Utils.padLeft("(" + (b2 + 1) + ") " + getResultsetName(b2), i));
        stringBuffer2.append(stringBuffer3).append(' ');
        for (byte b = 0; b < stringBuffer3.length(); b++)
          stringBuffer4.append('-'); 
        stringBuffer4.append('-');
      } 
    } 
    stringBuffer1.append(stringBuffer2).append('\n').append(stringBuffer4).append('\n');
    int[] arrayOfInt1 = new int[getNumResultsets()];
    int[] arrayOfInt2 = new int[getNumResultsets()];
    int[] arrayOfInt3 = new int[getNumResultsets()];
    StringBuffer stringBuffer5 = new StringBuffer("");
    byte b3;
    for (b3 = 0; b3 < getNumDatasets(); b3++) {
      String str1 = templateString(this.m_DatasetSpecifiers.specifier(b3));
      try {
        PairedStats pairedStats = calculateStatistics(this.m_DatasetSpecifiers.specifier(b3), paramInt1, paramInt1, paramInt2);
        str1 = Utils.padRight(str1, b1);
        stringBuffer1.append(str1);
        stringBuffer1.append(Utils.padLeft('(' + Utils.doubleToString(pairedStats.count, 0) + ')', 5)).append(' ');
        if (!this.m_ShowStdDevs) {
          str = padIt(pairedStats.xStats.mean, paramInt3 + 1 + this.m_MeanPrec, this.m_MeanPrec);
        } else {
          str = padIt(pairedStats.xStats.mean, paramInt3 + 1 + this.m_MeanPrec, this.m_MeanPrec);
          if (Double.isInfinite(pairedStats.xStats.stdDev)) {
            str = str + '(' + Utils.padRight("Inf", paramInt4 + 1 + this.m_StdDevPrec) + ')';
          } else {
            str = str + '(' + padIt(pairedStats.xStats.stdDev, paramInt4 + 1 + this.m_StdDevPrec, this.m_StdDevPrec) + ')';
          } 
        } 
        stringBuffer1.append(Utils.padLeft(str, i - 2)).append(" | ");
        for (byte b = 0; b < getNumResultsets(); b++) {
          if (displayResultset(b) && b != paramInt1)
            try {
              pairedStats = calculateStatistics(this.m_DatasetSpecifiers.specifier(b3), paramInt1, b, paramInt2);
              byte b4 = 32;
              if (pairedStats.differencesSignificance < 0) {
                b4 = 118;
                arrayOfInt1[b] = arrayOfInt1[b] + 1;
              } else if (pairedStats.differencesSignificance > 0) {
                b4 = 42;
                arrayOfInt2[b] = arrayOfInt2[b] + 1;
              } else {
                arrayOfInt3[b] = arrayOfInt3[b] + 1;
              } 
              if (!this.m_ShowStdDevs) {
                str = padIt(pairedStats.yStats.mean, i - 2, this.m_MeanPrec);
              } else {
                str = padIt(pairedStats.yStats.mean, paramInt3 + 5, this.m_MeanPrec);
                if (Double.isInfinite(pairedStats.yStats.stdDev)) {
                  str = str + '(' + Utils.padRight("Inf", paramInt4 + 3) + ')';
                } else {
                  str = str + '(' + padIt(pairedStats.yStats.stdDev, paramInt4 + 3, this.m_StdDevPrec) + ')';
                } 
              } 
              stringBuffer1.append(Utils.padLeft(str, i - 2)).append(' ').append(b4).append(' ');
            } catch (Exception exception) {
              exception.printStackTrace();
              stringBuffer1.append(Utils.padLeft("", i + 1));
            }  
        } 
        stringBuffer1.append('\n');
      } catch (Exception exception) {
        exception.printStackTrace();
        stringBuffer5.append(str1).append(' ');
      } 
    } 
    stringBuffer1.append(stringBuffer4).append('\n');
    stringBuffer1.append(Utils.padLeft("(v/ /*)", b1 + 4 + i)).append(" | ");
    for (b3 = 0; b3 < getNumResultsets(); b3++) {
      if (displayResultset(b3) && b3 != paramInt1)
        stringBuffer1.append(Utils.padLeft("(" + arrayOfInt1[b3] + '/' + arrayOfInt3[b3] + '/' + arrayOfInt2[b3] + ')', i)).append(' '); 
    } 
    stringBuffer1.append('\n');
    if (!stringBuffer5.equals(""))
      stringBuffer1.append("Skipped: ").append(stringBuffer5).append('\n'); 
    return stringBuffer1.toString();
  }
  
  private String multiResultsetFullCSV(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    StringBuffer stringBuffer1 = new StringBuffer(1000);
    int i = paramInt3 + 7;
    if (this.m_ShowStdDevs)
      i += paramInt4 + 5; 
    StringBuffer stringBuffer2 = new StringBuffer("Dataset");
    stringBuffer2.append(",");
    StringBuffer stringBuffer3 = new StringBuffer(Utils.quote("(" + (paramInt1 + 1) + ") " + getResultsetName(paramInt1)));
    stringBuffer2.append(stringBuffer3);
    if (this.m_ShowStdDevs)
      stringBuffer2.append(",").append("StdDev"); 
    for (byte b1 = 0; b1 < getNumResultsets(); b1++) {
      if (displayResultset(b1) && b1 != paramInt1) {
        stringBuffer3 = new StringBuffer(Utils.quote("(" + (b1 + 1) + ") " + getResultsetName(b1)));
        stringBuffer2.append(",").append(stringBuffer3).append(",");
        if (this.m_ShowStdDevs)
          stringBuffer2.append("StdDev").append(","); 
      } 
    } 
    stringBuffer1.append(stringBuffer2).append('\n');
    int[] arrayOfInt1 = new int[getNumResultsets()];
    int[] arrayOfInt2 = new int[getNumResultsets()];
    int[] arrayOfInt3 = new int[getNumResultsets()];
    StringBuffer stringBuffer4 = new StringBuffer("");
    byte b2;
    for (b2 = 0; b2 < getNumDatasets(); b2++) {
      String str = templateString(this.m_DatasetSpecifiers.specifier(b2));
      try {
        PairedStats pairedStats = calculateStatistics(this.m_DatasetSpecifiers.specifier(b2), paramInt1, paramInt1, paramInt2);
        stringBuffer1.append(Utils.quote(str + Utils.doubleToString(pairedStats.count, 0)));
        if (!this.m_ShowStdDevs) {
          stringBuffer1.append(',').append(padIt(pairedStats.xStats.mean, i - 2, this.m_MeanPrec).trim());
        } else {
          stringBuffer1.append(',').append(padIt(pairedStats.xStats.mean, paramInt3 + 5, this.m_MeanPrec).trim());
          if (Double.isInfinite(pairedStats.xStats.stdDev)) {
            stringBuffer1.append(',').append("Inf");
          } else {
            stringBuffer1.append(',').append(padIt(pairedStats.xStats.stdDev, paramInt4 + 3, this.m_StdDevPrec));
          } 
        } 
        for (byte b = 0; b < getNumResultsets(); b++) {
          if (displayResultset(b) && b != paramInt1)
            try {
              pairedStats = calculateStatistics(this.m_DatasetSpecifiers.specifier(b2), paramInt1, b, paramInt2);
              byte b3 = 32;
              if (pairedStats.differencesSignificance < 0) {
                b3 = 118;
                arrayOfInt1[b] = arrayOfInt1[b] + 1;
              } else if (pairedStats.differencesSignificance > 0) {
                b3 = 42;
                arrayOfInt2[b] = arrayOfInt2[b] + 1;
              } else {
                arrayOfInt3[b] = arrayOfInt3[b] + 1;
              } 
              if (!this.m_ShowStdDevs) {
                stringBuffer1.append(',').append(padIt(pairedStats.yStats.mean, i - 2, this.m_MeanPrec).trim()).append(',').append(b3);
              } else {
                stringBuffer1.append(',').append(padIt(pairedStats.yStats.mean, paramInt3 + 5, this.m_MeanPrec).trim());
                if (Double.isInfinite(pairedStats.yStats.stdDev)) {
                  stringBuffer1.append(',').append("Inf");
                } else {
                  stringBuffer1.append(',').append(padIt(pairedStats.yStats.stdDev, paramInt4 + 3, this.m_StdDevPrec).trim());
                } 
                stringBuffer1.append(',').append(b3);
              } 
            } catch (Exception exception) {
              exception.printStackTrace();
            }  
        } 
        stringBuffer1.append('\n');
      } catch (Exception exception) {
        exception.printStackTrace();
        if (stringBuffer4.length() > 0)
          stringBuffer4.append(','); 
        stringBuffer4.append(str);
      } 
    } 
    stringBuffer1.append(',').append(Utils.quote("(v/ /*)"));
    if (this.m_ShowStdDevs)
      stringBuffer1.append(','); 
    for (b2 = 0; b2 < getNumResultsets(); b2++) {
      if (displayResultset(b2) && b2 != paramInt1) {
        stringBuffer1.append(',');
        if (this.m_ShowStdDevs)
          stringBuffer1.append(','); 
        stringBuffer1.append(',').append(Utils.quote("(" + arrayOfInt1[b2] + '/' + arrayOfInt3[b2] + '/' + arrayOfInt2[b2] + ')'));
      } 
    } 
    stringBuffer1.append('\n');
    if (!stringBuffer4.equals(""))
      stringBuffer1.append("Skipped: ").append(stringBuffer4).append('\n'); 
    return stringBuffer1.toString();
  }
  
  public String multiResultsetFull(int paramInt1, int paramInt2) throws Exception {
    int i = 2;
    int j = 2;
    for (byte b1 = 0; b1 < getNumDatasets(); b1++) {
      for (byte b = 0; b < getNumResultsets(); b++) {
        if (displayResultset(b))
          try {
            PairedStats pairedStats = calculateStatistics(this.m_DatasetSpecifiers.specifier(b1), paramInt1, b, paramInt2);
            if (!Double.isInfinite(pairedStats.yStats.mean) && !Double.isNaN(pairedStats.yStats.mean)) {
              double d = Math.log(Math.abs(pairedStats.yStats.mean)) / Math.log(10.0D) + 1.0D;
              if (d > i)
                i = (int)d; 
            } 
            if (this.m_ShowStdDevs && !Double.isInfinite(pairedStats.yStats.stdDev) && !Double.isNaN(pairedStats.yStats.stdDev)) {
              double d = Math.log(Math.abs(pairedStats.yStats.stdDev)) / Math.log(10.0D) + 1.0D;
              if (d > j)
                j = (int)d; 
            } 
          } catch (Exception exception) {
            exception.printStackTrace();
          }  
      } 
    } 
    StringBuffer stringBuffer = new StringBuffer(1000);
    if (this.m_latexOutput) {
      stringBuffer = new StringBuffer(multiResultsetFullLatex(paramInt1, paramInt2, i, j));
    } else if (this.m_csvOutput) {
      stringBuffer = new StringBuffer(multiResultsetFullCSV(paramInt1, paramInt2, i, j));
    } else {
      stringBuffer = new StringBuffer(multiResultsetFullPlainText(paramInt1, paramInt2, i, j));
    } 
    stringBuffer.append("\nKey:\n\n");
    for (byte b2 = 0; b2 < getNumResultsets(); b2++) {
      if (displayResultset(b2)) {
        stringBuffer.append("(" + (b2 + 1) + ") ");
        stringBuffer.append(getResultsetName(b2) + "\n");
      } 
    } 
    return stringBuffer.toString();
  }
  
  public Enumeration listOptions() {
    Vector vector = new Vector(6);
    vector.addElement(new Option("\tSpecify list of columns that specify a unique\n\tdataset.\n\tFirst and last are valid indexes. (default none)", "D", 1, "-D <index,index2-index4,...>"));
    vector.addElement(new Option("\tSet the index of the column containing the run number", "R", 1, "-R <index>"));
    vector.addElement(new Option("\tSet the index of the column containing the fold number", "F", 1, "-F <index>"));
    vector.addElement(new Option("\tSpecify list of columns that specify a unique\n\t'result generator' (eg: classifier name and options).\n\tFirst and last are valid indexes. (default none)", "G", 1, "-G <index1,index2-index4,...>"));
    vector.addElement(new Option("\tSet the significance level for comparisons (default 0.05)", "S", 1, "-S <significance level>"));
    vector.addElement(new Option("\tShow standard deviations", "V", 0, "-V"));
    vector.addElement(new Option("\tProduce table comparisons in Latex table format", "L", 0, "-L"));
    vector.addElement(new Option("\tProduce table comparisons in CSV table format", "csv", 0, "-csv"));
    return vector.elements();
  }
  
  public void setOptions(String[] paramArrayOfString) throws Exception {
    setShowStdDevs(Utils.getFlag('V', paramArrayOfString));
    setProduceLatex(Utils.getFlag('L', paramArrayOfString));
    setProduceCSV(Utils.getFlag("csv", paramArrayOfString));
    String str1 = Utils.getOption('D', paramArrayOfString);
    Range range1 = new Range();
    if (str1.length() != 0)
      range1.setRanges(str1); 
    setDatasetKeyColumns(range1);
    String str2 = Utils.getOption('R', paramArrayOfString);
    if (str2.length() != 0) {
      if (str2.equals("first")) {
        setRunColumn(0);
      } else if (str2.equals("last")) {
        setRunColumn(-1);
      } else {
        setRunColumn(Integer.parseInt(str2) - 1);
      } 
    } else {
      setRunColumn(-1);
    } 
    String str3 = Utils.getOption('F', paramArrayOfString);
    if (str3.length() != 0) {
      setFoldColumn(Integer.parseInt(str3) - 1);
    } else {
      setFoldColumn(-1);
    } 
    String str4 = Utils.getOption('S', paramArrayOfString);
    if (str4.length() != 0) {
      setSignificanceLevel((new Double(str4)).doubleValue());
    } else {
      setSignificanceLevel(0.05D);
    } 
    String str5 = Utils.getOption('G', paramArrayOfString);
    Range range2 = new Range();
    if (str5.length() != 0)
      range2.setRanges(str5); 
    setResultsetKeyColumns(range2);
  }
  
  public String[] getOptions() {
    String[] arrayOfString = new String[10];
    byte b = 0;
    if (!getResultsetKeyColumns().getRanges().equals("")) {
      arrayOfString[b++] = "-G";
      arrayOfString[b++] = getResultsetKeyColumns().getRanges();
    } 
    if (!getDatasetKeyColumns().getRanges().equals("")) {
      arrayOfString[b++] = "-D";
      arrayOfString[b++] = getDatasetKeyColumns().getRanges();
    } 
    arrayOfString[b++] = "-R";
    arrayOfString[b++] = "" + (getRunColumn() + 1);
    arrayOfString[b++] = "-S";
    arrayOfString[b++] = "" + getSignificanceLevel();
    if (getShowStdDevs())
      arrayOfString[b++] = "-V"; 
    if (getProduceLatex())
      arrayOfString[b++] = "-L"; 
    if (getProduceCSV())
      arrayOfString[b++] = "-csv"; 
    while (b < arrayOfString.length)
      arrayOfString[b++] = ""; 
    return arrayOfString;
  }
  
  public Range getResultsetKeyColumns() {
    return this.m_ResultsetKeyColumnsRange;
  }
  
  public void setResultsetKeyColumns(Range paramRange) {
    this.m_ResultsetKeyColumnsRange = paramRange;
    this.m_ResultsetsValid = false;
  }
  
  public int[] getDisplayedResultsets() {
    return this.m_DisplayedResultsets;
  }
  
  public void setDisplayedResultsets(int[] paramArrayOfint) {
    this.m_DisplayedResultsets = paramArrayOfint;
  }
  
  public double getSignificanceLevel() {
    return this.m_SignificanceLevel;
  }
  
  public void setSignificanceLevel(double paramDouble) {
    this.m_SignificanceLevel = paramDouble;
  }
  
  public Range getDatasetKeyColumns() {
    return this.m_DatasetKeyColumnsRange;
  }
  
  public void setDatasetKeyColumns(Range paramRange) {
    this.m_DatasetKeyColumnsRange = paramRange;
    this.m_ResultsetsValid = false;
  }
  
  public int getRunColumn() {
    return this.m_RunColumnSet;
  }
  
  public void setRunColumn(int paramInt) {
    this.m_RunColumnSet = paramInt;
    this.m_ResultsetsValid = false;
  }
  
  public int getFoldColumn() {
    return this.m_FoldColumn;
  }
  
  public void setFoldColumn(int paramInt) {
    this.m_FoldColumn = paramInt;
    this.m_ResultsetsValid = false;
  }
  
  public Instances getInstances() {
    return this.m_Instances;
  }
  
  public void setInstances(Instances paramInstances) {
    this.m_Instances = paramInstances;
    this.m_ResultsetsValid = false;
  }
  
  public static void main(String[] paramArrayOfString) {
    try {
      PairedTTester pairedTTester = new PairedTTester();
      String str1 = Utils.getOption('t', paramArrayOfString);
      String str2 = Utils.getOption('c', paramArrayOfString);
      String str3 = Utils.getOption('b', paramArrayOfString);
      boolean bool1 = Utils.getFlag('s', paramArrayOfString);
      boolean bool2 = Utils.getFlag('r', paramArrayOfString);
      try {
        if (str1.length() == 0 || str2.length() == 0)
          throw new Exception("-t and -c options are required"); 
        pairedTTester.setOptions(paramArrayOfString);
        Utils.checkForRemainingOptions(paramArrayOfString);
      } catch (Exception exception) {
        String str = "";
        Enumeration enumeration = pairedTTester.listOptions();
        while (enumeration.hasMoreElements()) {
          Option option = enumeration.nextElement();
          str = str + option.synopsis() + '\n' + option.description() + '\n';
        } 
        throw new Exception("Usage:\n\n-t <file>\n\tSet the dataset containing data to evaluate\n-b <index>\n\tSet the resultset to base comparisons against (optional)\n-c <index>\n\tSet the column to perform a comparison on\n-s\n\tSummarize wins over all resultset pairs\n\n-r\n\tGenerate a resultset ranking\n\n" + str);
      } 
      Instances instances = new Instances(new BufferedReader(new FileReader(str1)));
      pairedTTester.setInstances(instances);
      int i = Integer.parseInt(str2) - 1;
      System.out.println(pairedTTester.header(i));
      if (bool2) {
        System.out.println(pairedTTester.multiResultsetRanking(i));
      } else if (bool1) {
        System.out.println(pairedTTester.multiResultsetSummary(i));
      } else {
        System.out.println(pairedTTester.resultsetKey());
        if (str3.length() == 0) {
          for (byte b = 0; b < pairedTTester.getNumResultsets(); b++) {
            if (pairedTTester.displayResultset(b))
              System.out.println(pairedTTester.multiResultsetFull(b, i)); 
          } 
        } else {
          int j = Integer.parseInt(str3) - 1;
          System.out.println(pairedTTester.multiResultsetFull(j, i));
        } 
      } 
    } catch (Exception exception) {
      exception.printStackTrace();
      System.err.println(exception.getMessage());
    } 
  }
  
  protected class Resultset {
    Instance m_Template;
    
    FastVector m_Datasets;
    
    private final PairedTTester this$0;
    
    public Resultset(PairedTTester this$0, Instance param1Instance) {
      this.this$0 = this$0;
      this.m_Template = param1Instance;
      this.m_Datasets = new FastVector();
      add(param1Instance);
    }
    
    protected boolean matchesTemplate(Instance param1Instance) {
      for (byte b = 0; b < this.this$0.m_ResultsetKeyColumns.length; b++) {
        if (param1Instance.value(this.this$0.m_ResultsetKeyColumns[b]) != this.m_Template.value(this.this$0.m_ResultsetKeyColumns[b]))
          return false; 
      } 
      return true;
    }
    
    protected String templateString() {
      String str1 = "";
      String str2 = "";
      for (byte b = 0; b < this.this$0.m_ResultsetKeyColumns.length; b++) {
        str2 = this.m_Template.toString(this.this$0.m_ResultsetKeyColumns[b]) + ' ';
        str2 = Utils.removeSubstring(str2, "weka.classifiers.");
        str2 = Utils.removeSubstring(str2, "weka.filters.");
        str2 = Utils.removeSubstring(str2, "weka.attributeSelection.");
        str1 = str1 + str2;
      } 
      return str1.trim();
    }
    
    public FastVector dataset(Instance param1Instance) {
      for (byte b = 0; b < this.m_Datasets.size(); b++) {
        if (((PairedTTester.Dataset)this.m_Datasets.elementAt(b)).matchesTemplate(param1Instance))
          return ((PairedTTester.Dataset)this.m_Datasets.elementAt(b)).contents(); 
      } 
      return null;
    }
    
    public void add(Instance param1Instance) {
      for (byte b = 0; b < this.m_Datasets.size(); b++) {
        if (((PairedTTester.Dataset)this.m_Datasets.elementAt(b)).matchesTemplate(param1Instance)) {
          ((PairedTTester.Dataset)this.m_Datasets.elementAt(b)).add(param1Instance);
          return;
        } 
      } 
      PairedTTester.Dataset dataset = new PairedTTester.Dataset(this.this$0, param1Instance);
      this.m_Datasets.addElement(dataset);
    }
    
    public void sort(int param1Int) {
      for (byte b = 0; b < this.m_Datasets.size(); b++)
        ((PairedTTester.Dataset)this.m_Datasets.elementAt(b)).sort(param1Int); 
    }
  }
  
  protected class Dataset {
    Instance m_Template;
    
    FastVector m_Dataset;
    
    private final PairedTTester this$0;
    
    public Dataset(PairedTTester this$0, Instance param1Instance) {
      this.this$0 = this$0;
      this.m_Template = param1Instance;
      this.m_Dataset = new FastVector();
      add(param1Instance);
    }
    
    protected boolean matchesTemplate(Instance param1Instance) {
      for (byte b = 0; b < this.this$0.m_DatasetKeyColumns.length; b++) {
        if (param1Instance.value(this.this$0.m_DatasetKeyColumns[b]) != this.m_Template.value(this.this$0.m_DatasetKeyColumns[b]))
          return false; 
      } 
      return true;
    }
    
    protected void add(Instance param1Instance) {
      this.m_Dataset.addElement(param1Instance);
    }
    
    protected FastVector contents() {
      return this.m_Dataset;
    }
    
    public void sort(int param1Int) {
      double[] arrayOfDouble = new double[this.m_Dataset.size()];
      for (byte b1 = 0; b1 < arrayOfDouble.length; b1++)
        arrayOfDouble[b1] = ((Instance)this.m_Dataset.elementAt(b1)).value(param1Int); 
      int[] arrayOfInt = Utils.stableSort(arrayOfDouble);
      FastVector fastVector = new FastVector(arrayOfDouble.length);
      for (byte b2 = 0; b2 < arrayOfInt.length; b2++)
        fastVector.addElement(this.m_Dataset.elementAt(arrayOfInt[b2])); 
      this.m_Dataset = fastVector;
    }
  }
  
  protected class DatasetSpecifiers {
    FastVector m_Specifiers;
    
    private final PairedTTester this$0;
    
    protected DatasetSpecifiers(PairedTTester this$0) {
      this.this$0 = this$0;
      this.m_Specifiers = new FastVector();
    }
    
    protected void removeAllSpecifiers() {
      this.m_Specifiers.removeAllElements();
    }
    
    protected void add(Instance param1Instance) {
      for (byte b = 0; b < this.m_Specifiers.size(); b++) {
        Instance instance = (Instance)this.m_Specifiers.elementAt(b);
        boolean bool = true;
        for (byte b1 = 0; b1 < this.this$0.m_DatasetKeyColumns.length; b1++) {
          if (param1Instance.value(this.this$0.m_DatasetKeyColumns[b1]) != instance.value(this.this$0.m_DatasetKeyColumns[b1]))
            bool = false; 
        } 
        if (bool)
          return; 
      } 
      this.m_Specifiers.addElement(param1Instance);
    }
    
    protected Instance specifier(int param1Int) {
      return (Instance)this.m_Specifiers.elementAt(param1Int);
    }
    
    protected int numSpecifiers() {
      return this.m_Specifiers.size();
    }
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\experiment\PairedTTester.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */