/*     */ package jeans.list;
/*     */ 
/*     */ import java.io.Serializable;
/*     */ import java.util.Arrays;
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
/*     */ 
/*     */ public class BitList
/*     */   implements Serializable
/*     */ {
/*     */   public static final long serialVersionUID = 1L;
/*     */   private static final int ADDRESS_BITS_PER_UNIT = 6;
/*     */   private static final int BITS_PER_UNIT = 64;
/*     */   private static final int BIT_INDEX_MASK = 63;
/*     */   private long[] m_Bits;
/*     */   private int m_Size;
/*     */   
/*     */   public BitList() {}
/*     */   
/*     */   public BitList(int size) {
/*  43 */     resize(size);
/*     */   }
/*     */   
/*     */   public final void resize(int size) {
/*  47 */     this.m_Size = size;
/*  48 */     this.m_Bits = new long[unitIndex(size - 1) + 1];
/*     */   }
/*     */   
/*     */   public final BitList cloneList() {
/*  52 */     return new BitList(this.m_Size);
/*     */   }
/*     */   
/*     */   public final int size() {
/*  56 */     return this.m_Size;
/*     */   }
/*     */   
/*     */   public final void setBit(int idx) {
/*  60 */     this.m_Bits[unitIndex(idx)] = this.m_Bits[unitIndex(idx)] | bit(idx);
/*     */   }
/*     */   
/*     */   public final boolean getBit(int idx) {
/*  64 */     return ((this.m_Bits[unitIndex(idx)] & bit(idx)) != 0L);
/*     */   }
/*     */   
/*     */   public final void reset() {
/*  68 */     Arrays.fill(this.m_Bits, 0L);
/*     */   }
/*     */   
/*     */   public final void copy(BitList other) {
/*  72 */     int olen = other.m_Bits.length;
/*  73 */     if (this.m_Bits == null || olen != this.m_Bits.length) {
/*  74 */       this.m_Bits = new long[olen];
/*  75 */       this.m_Size = other.size();
/*     */     } 
/*  77 */     System.arraycopy(other.m_Bits, 0, this.m_Bits, 0, olen);
/*     */   }
/*     */   
/*     */   public final void add(BitList other) {
/*  81 */     int nb = this.m_Bits.length;
/*  82 */     for (int i = 0; i < nb; i++) {
/*  83 */       this.m_Bits[i] = this.m_Bits[i] | other.m_Bits[i];
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final void subtractFromThis(BitList other) {
/*  95 */     int nb = this.m_Bits.length;
/*  96 */     for (int i = 0; i < nb; i++)
/*  97 */       this.m_Bits[i] = this.m_Bits[i] & (other.m_Bits[i] ^ 0xFFFFFFFFFFFFFFFFL); 
/*     */   }
/*     */   
/*     */   public final void subtractFromOther(BitList other) {
/* 101 */     int nb = this.m_Bits.length;
/* 102 */     for (int i = 0; i < nb; i++)
/* 103 */       this.m_Bits[i] = other.m_Bits[i] & (this.m_Bits[i] ^ 0xFFFFFFFFFFFFFFFFL); 
/*     */   }
/*     */   
/*     */   public String toString() {
/* 107 */     StringBuffer buf = new StringBuffer();
/* 108 */     for (int i = 0; i < this.m_Size; i++) {
/* 109 */       buf.append(getBit(i) ? 49 : 48);
/*     */     }
/* 111 */     return buf.toString();
/*     */   }
/*     */   
/*     */   public String toVector() {
/* 115 */     StringBuffer buf = new StringBuffer();
/* 116 */     buf.append("[");
/* 117 */     for (int i = 0; i < this.m_Size; i++) {
/* 118 */       if (i != 0) buf.append(","); 
/* 119 */       buf.append(getBit(i) ? 49 : 48);
/*     */     } 
/* 121 */     buf.append("]");
/* 122 */     return buf.toString();
/*     */   }
/*     */   
/*     */   public final int getNbOnes() {
/* 126 */     int nb = 0;
/* 127 */     for (int i = 0; i < this.m_Size; i++) {
/* 128 */       if (getBit(i)) nb++; 
/*     */     } 
/* 130 */     return nb;
/*     */   }
/*     */   
/*     */   private static final int unitIndex(int bitIndex) {
/* 134 */     return bitIndex >> 6;
/*     */   }
/*     */   
/*     */   private static final long bit(int bitIndex) {
/* 138 */     return 1L << (bitIndex & 0x3F);
/*     */   }
/*     */   
/*     */   public void copyAndSubtractFromThis(BitList bits, BitList bits2) {
/* 142 */     System.err.println("BitList.java:copyAndSubtractFromThis(): unimplemented method!");
/* 143 */     System.exit(-1);
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\jeans\list\BitList.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */