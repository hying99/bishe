package weka.gui.visualize;

import java.awt.Color;
import weka.core.FastVector;
import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Add;

public class PlotData2D {
  protected Instances m_plotInstances = null;
  
  protected String m_plotName = "new plot";
  
  public boolean m_useCustomColour = false;
  
  public Color m_customColour = null;
  
  public boolean m_displayAllPoints = false;
  
  protected double[][] m_pointLookup;
  
  protected int[] m_shapeSize;
  
  protected int[] m_shapeType;
  
  protected boolean[] m_connectPoints;
  
  private int m_xIndex;
  
  private int m_yIndex;
  
  private int m_cIndex;
  
  protected double m_maxX;
  
  protected double m_minX;
  
  protected double m_maxY;
  
  protected double m_minY;
  
  protected double m_maxC;
  
  protected double m_minC;
  
  public PlotData2D(Instances paramInstances) {
    this.m_plotInstances = paramInstances;
    this.m_xIndex = this.m_yIndex = this.m_cIndex = 0;
    this.m_pointLookup = new double[this.m_plotInstances.numInstances()][4];
    this.m_shapeSize = new int[this.m_plotInstances.numInstances()];
    this.m_shapeType = new int[this.m_plotInstances.numInstances()];
    this.m_connectPoints = new boolean[this.m_plotInstances.numInstances()];
    for (byte b = 0; b < this.m_plotInstances.numInstances(); b++) {
      this.m_shapeSize[b] = 2;
      this.m_shapeType[b] = -1;
    } 
    determineBounds();
  }
  
  public void addInstanceNumberAttribute() {
    String str = this.m_plotInstances.relationName();
    try {
      Add add = new Add();
      add.setAttributeName("Instance_number");
      add.setAttributeIndex("first");
      add.setInputFormat(this.m_plotInstances);
      this.m_plotInstances = Filter.useFilter(this.m_plotInstances, (Filter)add);
      this.m_plotInstances.setClassIndex(this.m_plotInstances.numAttributes() - 1);
      for (byte b = 0; b < this.m_plotInstances.numInstances(); b++)
        this.m_plotInstances.instance(b).setValue(0, b); 
      this.m_plotInstances.setRelationName(str);
    } catch (Exception exception) {
      exception.printStackTrace();
    } 
  }
  
  public Instances getPlotInstances() {
    return new Instances(this.m_plotInstances);
  }
  
  public void setPlotName(String paramString) {
    this.m_plotName = paramString;
  }
  
  public String getPlotName() {
    return this.m_plotName;
  }
  
  public void setShapeType(int[] paramArrayOfint) throws Exception {
    this.m_shapeType = paramArrayOfint;
    if (this.m_shapeType.length != this.m_plotInstances.numInstances())
      throw new Exception("PlotData2D: Shape type array must have the same number of entries as number of data points!"); 
    for (byte b = 0; b < paramArrayOfint.length; b++) {
      if (this.m_shapeType[b] == 1000)
        this.m_shapeSize[b] = 3; 
    } 
  }
  
  public void setShapeType(FastVector paramFastVector) throws Exception {
    if (paramFastVector.size() != this.m_plotInstances.numInstances())
      throw new Exception("PlotData2D: Shape type vector must have the same number of entries as number of data points!"); 
    this.m_shapeType = new int[paramFastVector.size()];
    for (byte b = 0; b < paramFastVector.size(); b++) {
      this.m_shapeType[b] = ((Integer)paramFastVector.elementAt(b)).intValue();
      if (this.m_shapeType[b] == 1000)
        this.m_shapeSize[b] = 3; 
    } 
  }
  
  public void setShapeSize(int[] paramArrayOfint) throws Exception {
    this.m_shapeSize = paramArrayOfint;
    if (this.m_shapeType.length != this.m_plotInstances.numInstances())
      throw new Exception("PlotData2D: Shape size array must have the same number of entries as number of data points!"); 
  }
  
  public void setShapeSize(FastVector paramFastVector) throws Exception {
    if (paramFastVector.size() != this.m_plotInstances.numInstances())
      throw new Exception("PlotData2D: Shape size vector must have the same number of entries as number of data points!"); 
    this.m_shapeSize = new int[paramFastVector.size()];
    for (byte b = 0; b < paramFastVector.size(); b++)
      this.m_shapeSize[b] = ((Integer)paramFastVector.elementAt(b)).intValue(); 
  }
  
  public void setConnectPoints(boolean[] paramArrayOfboolean) throws Exception {
    this.m_connectPoints = paramArrayOfboolean;
    if (this.m_connectPoints.length != this.m_plotInstances.numInstances())
      throw new Exception("PlotData2D: connect points array must have the same number of entries as number of data points!"); 
    this.m_connectPoints[0] = false;
  }
  
  public void setConnectPoints(FastVector paramFastVector) throws Exception {
    if (paramFastVector.size() != this.m_plotInstances.numInstances())
      throw new Exception("PlotData2D: connect points array must have the same number of entries as number of data points!"); 
    this.m_shapeSize = new int[paramFastVector.size()];
    for (byte b = 0; b < paramFastVector.size(); b++)
      this.m_connectPoints[b] = ((Boolean)paramFastVector.elementAt(b)).booleanValue(); 
    this.m_connectPoints[0] = false;
  }
  
  public void setCustomColour(Color paramColor) {
    this.m_customColour = paramColor;
    if (paramColor != null) {
      this.m_useCustomColour = true;
    } else {
      this.m_useCustomColour = false;
    } 
  }
  
  public void setXindex(int paramInt) {
    this.m_xIndex = paramInt;
    determineBounds();
  }
  
  public void setYindex(int paramInt) {
    this.m_yIndex = paramInt;
    determineBounds();
  }
  
  public void setCindex(int paramInt) {
    this.m_cIndex = paramInt;
    determineBounds();
  }
  
  public int getXindex() {
    return this.m_xIndex;
  }
  
  public int getYindex() {
    return this.m_yIndex;
  }
  
  public int getCindex() {
    return this.m_cIndex;
  }
  
  private void determineBounds() {
    if (this.m_plotInstances != null && this.m_plotInstances.numAttributes() > 0 && this.m_plotInstances.numInstances() > 0) {
      double d1 = Double.POSITIVE_INFINITY;
      double d2 = Double.NEGATIVE_INFINITY;
      if (this.m_plotInstances.attribute(this.m_xIndex).isNominal()) {
        this.m_minX = 0.0D;
        this.m_maxX = (this.m_plotInstances.attribute(this.m_xIndex).numValues() - 1);
      } else {
        for (byte b1 = 0; b1 < this.m_plotInstances.numInstances(); b1++) {
          if (!this.m_plotInstances.instance(b1).isMissing(this.m_xIndex)) {
            double d = this.m_plotInstances.instance(b1).value(this.m_xIndex);
            if (d < d1)
              d1 = d; 
            if (d > d2)
              d2 = d; 
          } 
        } 
        if (d1 == Double.POSITIVE_INFINITY)
          d1 = d2 = 0.0D; 
        this.m_minX = d1;
        this.m_maxX = d2;
        if (d1 == d2) {
          this.m_maxX += 0.05D;
          this.m_minX -= 0.05D;
        } 
      } 
      d1 = Double.POSITIVE_INFINITY;
      d2 = Double.NEGATIVE_INFINITY;
      if (this.m_plotInstances.attribute(this.m_yIndex).isNominal()) {
        this.m_minY = 0.0D;
        this.m_maxY = (this.m_plotInstances.attribute(this.m_yIndex).numValues() - 1);
      } else {
        for (byte b1 = 0; b1 < this.m_plotInstances.numInstances(); b1++) {
          if (!this.m_plotInstances.instance(b1).isMissing(this.m_yIndex)) {
            double d = this.m_plotInstances.instance(b1).value(this.m_yIndex);
            if (d < d1)
              d1 = d; 
            if (d > d2)
              d2 = d; 
          } 
        } 
        if (d1 == Double.POSITIVE_INFINITY)
          d1 = d2 = 0.0D; 
        this.m_minY = d1;
        this.m_maxY = d2;
        if (d1 == d2) {
          this.m_maxY += 0.05D;
          this.m_minY -= 0.05D;
        } 
      } 
      d1 = Double.POSITIVE_INFINITY;
      d2 = Double.NEGATIVE_INFINITY;
      for (byte b = 0; b < this.m_plotInstances.numInstances(); b++) {
        if (!this.m_plotInstances.instance(b).isMissing(this.m_cIndex)) {
          double d = this.m_plotInstances.instance(b).value(this.m_cIndex);
          if (d < d1)
            d1 = d; 
          if (d > d2)
            d2 = d; 
        } 
      } 
      if (d1 == Double.POSITIVE_INFINITY)
        d1 = d2 = 0.0D; 
      this.m_minC = d1;
      this.m_maxC = d2;
    } 
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\gui\visualize\PlotData2D.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */