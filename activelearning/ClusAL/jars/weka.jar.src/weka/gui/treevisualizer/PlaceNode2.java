package weka.gui.treevisualizer;

import java.util.Vector;

public class PlaceNode2 implements NodePlace {
  private double m_yRatio;
  
  private Group[] m_groups;
  
  private Level[] m_levels;
  
  private int m_groupNum;
  
  private int m_levelNum;
  
  public void place(Node paramNode) {
    this.m_groupNum = Node.getGCount(paramNode, 0);
    this.m_groups = new Group[this.m_groupNum];
    byte b;
    for (b = 0; b < this.m_groupNum; b++) {
      this.m_groups[b] = new Group();
      (this.m_groups[b]).m_gap = 3.0D;
      (this.m_groups[b]).m_start = -1;
    } 
    groupBuild(paramNode);
    this.m_levelNum = Node.getHeight(paramNode, 0);
    this.m_yRatio = 1.0D / (this.m_levelNum + 1);
    this.m_levels = new Level[this.m_levelNum];
    for (b = 0; b < this.m_levelNum; b++)
      this.m_levels[b] = new Level(); 
    paramNode.setTop(this.m_yRatio);
    yPlacer();
    paramNode.setCenter(0.0D);
    xPlacer(0);
    untangle2();
    scaleByMax();
  }
  
  private void xPlacer(int paramInt) {
    if (this.m_groupNum > 0) {
      (this.m_groups[0]).m_p.setCenter(0.0D);
      for (int i = paramInt; i < this.m_groupNum; i++) {
        byte b2 = 0;
        double d = (this.m_groups[i]).m_gap;
        Node node = (this.m_groups[i]).m_p;
        Edge edge;
        byte b1;
        for (b1 = 0; (edge = node.getChild(b1)) != null; b1++) {
          if (edge.getTarget().getParent(0) == edge) {
            edge.getTarget().setCenter(b1 * d);
          } else {
            b2++;
          } 
        } 
        (this.m_groups[i]).m_size = (b1 - 1 - b2) * d;
        xShift(i);
      } 
    } 
  }
  
  private void xShift(int paramInt) {
    Node node = (this.m_groups[paramInt]).m_p;
    double d1 = (this.m_groups[paramInt]).m_size / 2.0D;
    double d2 = (this.m_groups[paramInt]).m_p.getCenter();
    double d3 = d2 - d1;
    (this.m_groups[paramInt]).m_left = d3;
    (this.m_groups[paramInt]).m_right = d2 + d1;
    Edge edge;
    for (byte b = 0; (edge = node.getChild(b)) != null; b++) {
      if (edge.getTarget().getParent(0) == edge)
        edge.getTarget().adjustCenter(d3); 
    } 
  }
  
  private void scaleByMax() {
    double d1 = 5000.0D;
    double d2 = -5000.0D;
    for (byte b = 0; b < this.m_groupNum; b++) {
      if (d1 > (this.m_groups[b]).m_left)
        d1 = (this.m_groups[b]).m_left; 
      if (d2 < (this.m_groups[b]).m_right)
        d2 = (this.m_groups[b]).m_right; 
    } 
    double d3 = d2 - d1 + 1.0D;
    if (this.m_groupNum > 0) {
      Node node = (this.m_groups[0]).m_p;
      node.setCenter((node.getCenter() - d1) / d3);
      for (byte b1 = 0; b1 < this.m_groupNum; b1++) {
        node = (this.m_groups[b1]).m_p;
        Edge edge;
        for (byte b2 = 0; (edge = node.getChild(b2)) != null; b2++) {
          Node node1 = edge.getTarget();
          if (node1.getParent(0) == edge)
            node1.setCenter((node1.getCenter() - d1) / d3); 
        } 
      } 
    } 
  }
  
  private void scaleByInd() {
    Node node = (this.m_groups[0]).m_p;
    node.setCenter(0.5D);
    for (byte b = 0; b < this.m_levelNum; b++) {
      double d1 = (this.m_groups[(this.m_levels[b]).m_start]).m_left;
      double d2 = (this.m_groups[(this.m_levels[b]).m_end]).m_right;
      double d3 = d2 - d1 + 1.0D;
      for (int i = (this.m_levels[b]).m_start; i <= (this.m_levels[b]).m_end; i++) {
        node = (this.m_groups[i]).m_p;
        Edge edge;
        for (byte b1 = 0; (edge = node.getChild(b1)) != null; b1++) {
          Node node1 = edge.getTarget();
          if (node1.getParent(0) == edge)
            node1.setCenter((node1.getCenter() - d1) / d3); 
        } 
      } 
    } 
  }
  
  private void untangle2() {
    Node node1 = null;
    Node node2 = null;
    int i = 0;
    byte b = 0;
    int j = 0;
    int k = 0;
    Ease ease;
    while ((ease = overlap(i)) != null) {
      b++;
      int m = ease.m_place;
      int n;
      for (n = ease.m_place + 1; m != n; n = (this.m_groups[n]).m_pg) {
        ease.m_lev--;
        j = m;
        k = n;
        m = (this.m_groups[m]).m_pg;
      } 
      i = ease.m_lev;
      byte b1 = 0;
      byte b2 = 0;
      Node node3 = (this.m_groups[m]).m_p;
      Node node4 = (this.m_groups[j]).m_p;
      node1 = null;
      node2 = null;
      byte b3;
      for (b3 = 0; node1 != node4; b3++) {
        b1++;
        node1 = node3.getChild(b3).getTarget();
      } 
      node4 = (this.m_groups[k]).m_p;
      for (b3 = b1; node2 != node4; b3++) {
        b2++;
        node2 = node3.getChild(b3).getTarget();
      } 
      Vector vector = new Vector(20, 10);
      Edge edge;
      for (byte b4 = 0; (edge = node3.getChild(b4)) != null; b4++) {
        if (edge.getTarget().getParent(0) == edge) {
          Double double_ = new Double(edge.getTarget().getCenter());
          vector.addElement(double_);
        } 
      } 
      b1--;
      double d = ease.m_amount / b2;
      for (byte b5 = 0; (edge = node3.getChild(b5)) != null; b5++) {
        node2 = edge.getTarget();
        if (node2.getParent(0) == edge)
          if (b5 > b1 + b2) {
            node2.adjustCenter(ease.m_amount);
          } else if (b5 > b1) {
            node2.adjustCenter(d * (b5 - b1));
          }  
      } 
      node1 = node3.getChild(0).getTarget();
      d = node2.getCenter() - node1.getCenter();
      (this.m_groups[m]).m_size = d;
      (this.m_groups[m]).m_left = node3.getCenter() - d / 2.0D;
      (this.m_groups[m]).m_right = (this.m_groups[m]).m_left + d;
      d = (this.m_groups[m]).m_left - node1.getCenter();
      byte b6 = 0;
      for (byte b7 = 0; (edge = node3.getChild(b7)) != null; b7++) {
        node2 = edge.getTarget();
        if (node2.getParent(0) == edge) {
          node2.adjustCenter(d);
          double d1 = node2.getCenter() - ((Double)vector.elementAt(b7)).doubleValue();
          if (node2.getChild(0) != null) {
            moveSubtree((this.m_groups[m]).m_start + b6, d1);
            b6++;
          } 
        } 
      } 
    } 
  }
  
  private void moveSubtree(int paramInt, double paramDouble) {
    Node node = (this.m_groups[paramInt]).m_p;
    Edge edge;
    int i;
    for (i = 0; (edge = node.getChild(i)) != null; i++) {
      if (edge.getTarget().getParent(0) == edge)
        edge.getTarget().adjustCenter(paramDouble); 
    } 
    (this.m_groups[paramInt]).m_left += paramDouble;
    (this.m_groups[paramInt]).m_right += paramDouble;
    if ((this.m_groups[paramInt]).m_start != -1)
      for (i = (this.m_groups[paramInt]).m_start; i <= (this.m_groups[paramInt]).m_end; i++)
        moveSubtree(i, paramDouble);  
  }
  
  private void untangle() {
    Node node1 = null;
    Node node2 = null;
    int i = 0;
    byte b = 0;
    int j = 0;
    int k = 0;
    Ease ease;
    while ((ease = overlap(i)) != null) {
      b++;
      int m = ease.m_place;
      int n;
      for (n = ease.m_place + 1; m != n; n = (this.m_groups[n]).m_pg) {
        ease.m_lev--;
        j = m;
        k = n;
        m = (this.m_groups[m]).m_pg;
      } 
      i = ease.m_lev;
      byte b1 = 0;
      byte b2 = 0;
      Node node3 = (this.m_groups[m]).m_p;
      Node node4 = (this.m_groups[j]).m_p;
      node1 = null;
      node2 = null;
      byte b3;
      for (b3 = 0; node1 != node4; b3++) {
        b1++;
        node1 = node3.getChild(b3).getTarget();
      } 
      node4 = (this.m_groups[k]).m_p;
      for (b3 = b1; node2 != node4; b3++) {
        b2++;
        node2 = node3.getChild(b3).getTarget();
      } 
      (this.m_groups[m]).m_gap = Math.ceil(ease.m_amount / b2 + (this.m_groups[m]).m_gap);
      xPlacer(m);
    } 
  }
  
  private Ease overlap(int paramInt) {
    Ease ease = new Ease();
    for (int i = paramInt; i < this.m_levelNum; i++) {
      for (int j = (this.m_levels[i]).m_start; j < (this.m_levels[i]).m_end; j++) {
        ease.m_amount = (this.m_groups[j]).m_right - (this.m_groups[j + 1]).m_left + 2.0D;
        if (ease.m_amount >= 0.0D) {
          ease.m_amount++;
          ease.m_lev = i;
          ease.m_place = j;
          return ease;
        } 
      } 
    } 
    return null;
  }
  
  private void yPlacer() {
    double d = this.m_yRatio;
    byte b = 0;
    if (this.m_groupNum > 0) {
      (this.m_groups[0]).m_p.setTop(this.m_yRatio);
      (this.m_levels[0]).m_start = 0;
      for (byte b1 = 0; b1 < this.m_groupNum; b1++) {
        if ((this.m_groups[b1]).m_p.getTop() != d) {
          (this.m_levels[b]).m_end = b1 - 1;
          (this.m_levels[++b]).m_start = b1;
          d = (this.m_groups[b1]).m_p.getTop();
        } 
        nodeY((this.m_groups[b1]).m_p);
      } 
      (this.m_levels[b]).m_end = this.m_groupNum - 1;
    } 
  }
  
  private void nodeY(Node paramNode) {
    double d = paramNode.getTop() + this.m_yRatio;
    Edge edge;
    for (byte b = 0; (edge = paramNode.getChild(b)) != null; b++) {
      if (edge.getTarget().getParent(0) == edge) {
        edge.getTarget().setTop(d);
        if (!edge.getTarget().getVisible());
      } 
    } 
  }
  
  private void groupBuild(Node paramNode) {
    if (this.m_groupNum > 0) {
      this.m_groupNum = 0;
      (this.m_groups[0]).m_p = paramNode;
      this.m_groupNum++;
      for (byte b = 0; b < this.m_groupNum; b++)
        groupFind((this.m_groups[b]).m_p, b); 
    } 
  }
  
  private void groupFind(Node paramNode, int paramInt) {
    boolean bool = true;
    Edge edge;
    for (byte b = 0; (edge = paramNode.getChild(b)) != null; b++) {
      if (edge.getTarget().getParent(0) == edge && edge.getTarget().getChild(0) != null && edge.getTarget().getCVisible()) {
        if (bool) {
          (this.m_groups[paramInt]).m_start = this.m_groupNum;
          bool = false;
        } 
        (this.m_groups[paramInt]).m_end = this.m_groupNum;
        (this.m_groups[this.m_groupNum]).m_p = edge.getTarget();
        (this.m_groups[this.m_groupNum]).m_pg = paramInt;
        (this.m_groups[this.m_groupNum]).m_id = this.m_groupNum;
        this.m_groupNum++;
      } 
    } 
  }
  
  private class Ease {
    public int m_place;
    
    public double m_amount;
    
    public int m_lev;
    
    private final PlaceNode2 this$0;
    
    private Ease(PlaceNode2 this$0) {
      PlaceNode2.this = PlaceNode2.this;
    }
  }
  
  private class Group {
    public Node m_p;
    
    public int m_pg;
    
    public double m_gap;
    
    public double m_left;
    
    public double m_right;
    
    public double m_size;
    
    public int m_start;
    
    public int m_end;
    
    public int m_id;
    
    private final PlaceNode2 this$0;
    
    private Group(PlaceNode2 this$0) {
      PlaceNode2.this = PlaceNode2.this;
    }
  }
  
  private class Level {
    public int m_start;
    
    public int m_end;
    
    public int m_left;
    
    public int m_right;
    
    private final PlaceNode2 this$0;
    
    private Level(PlaceNode2 this$0) {
      PlaceNode2.this = PlaceNode2.this;
    }
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\gui\treevisualizer\PlaceNode2.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */