/*    */ package clus.ext.ilevelc;
/*    */ 
/*    */ import clus.data.rows.DataTuple;
/*    */ import clus.util.ClusException;
/*    */ import java.io.FileInputStream;
/*    */ import java.io.IOException;
/*    */ import java.io.InputStreamReader;
/*    */ import java.io.LineNumberReader;
/*    */ import java.io.Serializable;
/*    */ import java.util.ArrayList;
/*    */ import java.util.StringTokenizer;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class ILevelConstraint
/*    */   implements Serializable
/*    */ {
/*    */   public static final int ILevelCMustLink = 0;
/*    */   public static final int ILevelCCannotLink = 1;
/*    */   protected int m_Type;
/*    */   protected DataTuple m_T1;
/*    */   protected DataTuple m_T2;
/*    */   
/*    */   public ILevelConstraint(DataTuple t1, DataTuple t2, int type) {
/* 40 */     this.m_T1 = t1; this.m_T2 = t2; this.m_Type = type;
/*    */   }
/*    */   
/*    */   public DataTuple getT1() {
/* 44 */     return this.m_T1;
/*    */   }
/*    */   
/*    */   public DataTuple getT2() {
/* 48 */     return this.m_T2;
/*    */   }
/*    */   
/*    */   public int getType() {
/* 52 */     return this.m_Type;
/*    */   }
/*    */   
/*    */   public int getOtherTupleIdx(DataTuple tuple) {
/* 56 */     return (tuple == this.m_T1) ? this.m_T2.getIndex() : this.m_T1.getIndex();
/*    */   }
/*    */   
/*    */   public boolean isSideOne(DataTuple tuple) {
/* 60 */     return (tuple == this.m_T1);
/*    */   }
/*    */   
/*    */   public static void loadConstraints(String fname, ArrayList<ILevelConstraint> constr, ArrayList<DataTuple> points) throws IOException {
/* 64 */     LineNumberReader rdr = new LineNumberReader(new InputStreamReader(new FileInputStream(fname)));
/*    */     
/* 66 */     String line = rdr.readLine();
/* 67 */     while (line != null) {
/* 68 */       StringTokenizer tokens = new StringTokenizer(line, "\t");
/* 69 */       int t1 = Integer.parseInt(tokens.nextToken());
/* 70 */       int t2 = Integer.parseInt(tokens.nextToken());
/* 71 */       int type = (Integer.parseInt(tokens.nextToken()) == 1) ? 0 : 1;
/* 72 */       constr.add(new ILevelConstraint(points.get(t1), points.get(t2), type));
/* 73 */       line = rdr.readLine();
/*    */     } 
/* 75 */     rdr.close();
/*    */   }
/*    */   
/*    */   public static ArrayList loadConstraints(String fname, ArrayList points) throws ClusException {
/* 79 */     ArrayList constr = new ArrayList();
/*    */     try {
/* 81 */       loadConstraints(fname, constr, points);
/* 82 */       return constr;
/* 83 */     } catch (IOException e) {
/* 84 */       throw new ClusException("Error opening '" + fname + "': " + e.getMessage());
/*    */     } 
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\clus\ext\ilevelc\ILevelConstraint.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */