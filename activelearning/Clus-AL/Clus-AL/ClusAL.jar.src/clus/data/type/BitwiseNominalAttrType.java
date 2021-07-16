/*    */ package clus.data.type;
/*    */ 
/*    */ import clus.data.rows.DataTuple;
/*    */ 
/*    */ public class BitwiseNominalAttrType
/*    */   extends NominalAttrType
/*    */ {
/*    */   public static final long serialVersionUID = 1L;
/*    */   protected int m_Bits;
/*    */   protected int m_BitMask;
/*    */   protected int m_BitPosition;
/* 12 */   public static final double LOG2 = Math.log(2.0D);
/*    */   
/*    */   public BitwiseNominalAttrType(String name, String type) {
/* 15 */     super(name, type);
/* 16 */     initBitwiseNominal();
/*    */   }
/*    */   
/*    */   public BitwiseNominalAttrType(String name, String[] values) {
/* 20 */     super(name, values);
/* 21 */     initBitwiseNominal();
/*    */   }
/*    */   
/*    */   public BitwiseNominalAttrType(String name) {
/* 25 */     super(name);
/* 26 */     initBitwiseNominal();
/*    */   }
/*    */   
/*    */   public BitwiseNominalAttrType(String name, int nbvalues) {
/* 30 */     super(name, nbvalues);
/* 31 */     initBitwiseNominal();
/*    */   }
/*    */   
/*    */   public void initBitwiseNominal() {
/* 35 */     this.m_Bits = Math.round((float)(Math.log((getNbValues() + 1)) / LOG2 + 0.5D));
/* 36 */     this.m_BitMask = (1 << this.m_Bits) - 1;
/*    */   }
/*    */   
/*    */   public int getValueType() {
/* 40 */     return 3;
/*    */   }
/*    */   
/*    */   public int getNominal(DataTuple tuple) {
/* 44 */     return tuple.m_Ints[this.m_ArrayIndex] >> this.m_BitPosition & this.m_BitMask;
/*    */   }
/*    */ 
/*    */   
/*    */   public void setNominal(DataTuple tuple, int value) {
/* 49 */     int intvalue = tuple.getIntVal(getArrayIndex()) | value << getBitPosition();
/* 50 */     tuple.setIntVal(intvalue, getArrayIndex());
/*    */   }
/*    */   
/*    */   public int getNbBits() {
/* 54 */     return this.m_Bits;
/*    */   }
/*    */   
/*    */   public void setBitPosition(int bp) {
/* 58 */     this.m_BitPosition = bp;
/*    */   }
/*    */   
/*    */   public int getBitPosition() {
/* 62 */     return this.m_BitPosition;
/*    */   }
/*    */   
/*    */   public ClusAttrType cloneType() {
/* 66 */     BitwiseNominalAttrType at = new BitwiseNominalAttrType(this.m_Name, this.m_Values);
/* 67 */     cloneType(at);
/* 68 */     return at;
/*    */   }
/*    */   
/*    */   public void copyArrayIndex(ClusAttrType type) {
/* 72 */     this.m_Index = type.m_Index;
/* 73 */     this.m_ArrayIndex = type.m_ArrayIndex;
/* 74 */     this.m_Status = type.m_Status;
/* 75 */     this.m_BitPosition = ((BitwiseNominalAttrType)type).m_BitPosition;
/*    */   }
/*    */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\clus\data\type\BitwiseNominalAttrType.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */