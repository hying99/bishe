/*    */ package clus.model.modelio;
/*    */ 
/*    */ import clus.model.ClusModel;
/*    */ import clus.model.ClusModelInfo;
/*    */ import java.io.FileInputStream;
/*    */ import java.io.FileOutputStream;
/*    */ import java.io.IOException;
/*    */ import java.io.Serializable;
/*    */ import java.util.ArrayList;
/*    */ import jeans.io.ObjectLoadStream;
/*    */ import jeans.io.ObjectSaveStream;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class ClusModelCollectionIO
/*    */   implements Serializable
/*    */ {
/*    */   public static final long serialVersionUID = 1L;
/* 39 */   protected ArrayList m_ModelInfos = new ArrayList();
/*    */   
/*    */   public int getNbModels() {
/* 42 */     return this.m_ModelInfos.size();
/*    */   }
/*    */   
/*    */   public void addModel(ClusModelInfo model) {
/* 46 */     this.m_ModelInfos.add(model);
/*    */   }
/*    */   
/*    */   public void insertModel(int idx, ClusModelInfo model) {
/* 50 */     this.m_ModelInfos.add(null);
/* 51 */     for (int i = this.m_ModelInfos.size() - 1; i >= idx + 1; i--) {
/* 52 */       this.m_ModelInfos.set(i, this.m_ModelInfos.get(i - 1));
/*    */     }
/* 54 */     this.m_ModelInfos.set(idx, model);
/*    */   }
/*    */   
/*    */   public ClusModelInfo getModelInfo(int index) {
/* 58 */     return this.m_ModelInfos.get(index);
/*    */   }
/*    */   
/*    */   public ClusModel getModel(int index) {
/* 62 */     ClusModelInfo info = this.m_ModelInfos.get(index);
/* 63 */     return info.getModel();
/*    */   }
/*    */   
/*    */   public ClusModel getModel(String name) {
/* 67 */     for (int i = 0; i < getNbModels(); i++) {
/* 68 */       ClusModelInfo info = this.m_ModelInfos.get(i);
/* 69 */       if (info.getName().equals(name)) return info.getModel(); 
/*    */     } 
/* 71 */     return null;
/*    */   }
/*    */   
/*    */   public void printModelNames() {
/* 75 */     if (getNbModels() == 0) {
/* 76 */       System.out.println("Collection does not contain any models");
/*    */     } else {
/* 78 */       for (int i = 0; i < getNbModels(); i++) {
/* 79 */         ClusModelInfo info = this.m_ModelInfos.get(i);
/* 80 */         System.out.println("Model: " + info.getName());
/*    */       } 
/*    */     } 
/*    */   }
/*    */   
/*    */   public void save(String filename) throws IOException {
/* 86 */     ObjectSaveStream strm = new ObjectSaveStream(new FileOutputStream(filename));
/* 87 */     strm.writeObject(this);
/* 88 */     strm.close();
/*    */   }
/*    */   
/*    */   public static ClusModelCollectionIO load(String filename) throws IOException, ClassNotFoundException {
/* 92 */     ObjectLoadStream strm = new ObjectLoadStream(new FileInputStream(filename));
/* 93 */     ClusModelCollectionIO result = (ClusModelCollectionIO)strm.readObject();
/* 94 */     strm.close();
/* 95 */     return result;
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\clus\model\modelio\ClusModelCollectionIO.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */