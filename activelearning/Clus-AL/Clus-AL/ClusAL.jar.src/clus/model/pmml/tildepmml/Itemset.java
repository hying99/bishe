/*     */ package clus.model.pmml.tildepmml;
/*     */ 
/*     */ import java.io.PrintWriter;
/*     */ import java.util.Vector;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class Itemset
/*     */ {
/*     */   private int id;
/*     */   private String predicate;
/*     */   private int numberOfItems;
/*     */   private Vector itemRefs;
/*  35 */   private String displayTerm = "";
/*     */   
/*     */   public Itemset(int $id, String $predicate) {
/*  38 */     this.id = $id;
/*  39 */     this.predicate = $predicate;
/*  40 */     this.numberOfItems = 0;
/*  41 */     this.itemRefs = new Vector();
/*     */   }
/*     */ 
/*     */   
/*     */   public Itemset(int $id, String $predicate, int $numberOfItems, Vector $itemRefs) {
/*  46 */     this.id = $id;
/*  47 */     this.predicate = $predicate;
/*  48 */     this.numberOfItems = $numberOfItems;
/*  49 */     this.itemRefs = $itemRefs;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public Itemset(int $id, String $predicate, int $numberOfItems, Vector $itemRefs, String $displayTerm) {
/*  55 */     this.id = $id;
/*  56 */     this.predicate = $predicate;
/*  57 */     this.numberOfItems = $numberOfItems;
/*  58 */     this.itemRefs = $itemRefs;
/*  59 */     this.displayTerm = $displayTerm;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void setId(int $id) {
/*  65 */     this.id = $id;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void setPredicate(String $predicate) {
/*  71 */     this.predicate = $predicate;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void setNumberOfItems(int $numberOfItems) {
/*  77 */     this.numberOfItems = $numberOfItems;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void addItemRef(Item itemObject) {
/*  90 */     this.itemRefs.add(itemObject);
/*  91 */     this.numberOfItems++;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public int getId() {
/*  97 */     return this.id;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public String getPredicate() {
/* 103 */     return this.predicate;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public int getNumberOfItems() {
/* 109 */     return this.numberOfItems;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public Vector getItemRefs() {
/* 115 */     return this.itemRefs;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public String getDisplayTerm() {
/* 121 */     return this.displayTerm;
/*     */   }
/*     */ 
/*     */   
/*     */   public void print(PrintWriter outStream) {
/* 126 */     int nbItems = this.itemRefs.size();
/* 127 */     outStream.println("<Itemset id=\"" + this.id + "\" predicate=\"" + this.predicate + "\" numberOfItems=\"" + nbItems + "\">");
/*     */     
/* 129 */     for (int i = 0; i < nbItems; i++) {
/* 130 */       Item item = this.itemRefs.elementAt(i);
/* 131 */       outStream.println(" <ItemRef itemRef=\"" + item.getId() + "\" position=\"" + i + "\"/>");
/*     */     } 
/*     */     
/* 134 */     if (this.displayTerm != "") {
/* 135 */       outStream.println(" <DisplayTerm value=\"" + this.displayTerm + "\"/>");
/*     */     }
/*     */     
/* 138 */     outStream.println("</Itemset>");
/* 139 */     outStream.println();
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\clus\model\pmml\tildepmml\Itemset.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */