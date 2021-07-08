/*     */ package org.jgap;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.io.FileInputStream;
/*     */ import java.io.FileOutputStream;
/*     */ import java.io.InputStream;
/*     */ import java.io.ObjectInputStream;
/*     */ import java.io.ObjectOutputStream;
/*     */ import java.io.OutputStream;
/*     */ import java.io.Serializable;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import junit.framework.TestCase;
/*     */ import junitx.util.PrivateAccessor;
/*     */ import org.jgap.impl.DefaultConfiguration;
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
/*     */ public abstract class JGAPTestCase
/*     */   extends TestCase
/*     */ {
/*     */   private static final String CVS_REVISION = "$Revision: 1.19 $";
/*     */   protected static final double DELTA = 1.0E-7D;
/*  32 */   public static final PrivateAccessor privateAccessor = null;
/*     */   public Configuration conf;
/*     */   
/*     */   public JGAPTestCase(String a_name) {
/*  36 */     super(a_name);
/*     */   }
/*     */ 
/*     */   
/*     */   public JGAPTestCase() {}
/*     */ 
/*     */   
/*     */   public void setUp() {
/*  44 */     Genotype.setStaticConfiguration(null);
/*     */     
/*  46 */     System.setProperty("JGAPFACTORYCLASS", "");
/*  47 */     Configuration.resetProperty("JGAPFITEVALINST");
/*  48 */     Configuration.resetProperty("JGAPEVNTMGRINST");
/*  49 */     this.conf = (Configuration)new DefaultConfiguration();
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
/*     */   public static boolean isChromosomesEqual(IChromosome[] a_list1, IChromosome[] a_list2) {
/*  62 */     if (a_list1 == null) {
/*  63 */       return (a_list2 == null);
/*     */     }
/*  65 */     if (a_list2 == null) {
/*  66 */       return false;
/*     */     }
/*     */     
/*  69 */     if (a_list1.length != a_list2.length) {
/*  70 */       return false;
/*     */     }
/*     */     
/*  73 */     for (int i = 0; i < a_list1.length; i++) {
/*  74 */       IChromosome c1 = a_list1[i];
/*  75 */       IChromosome c2 = a_list2[i];
/*  76 */       if (!c1.equals(c2)) {
/*  77 */         return false;
/*     */       }
/*     */     } 
/*  80 */     return true;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void assertEqualsMap(Map a_map1, Map a_map2) {}
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public class TestFitnessFunction
/*     */     extends FitnessFunction
/*     */   {
/*     */     protected double evaluate(IChromosome a_subject) {
/*  98 */       return 1.0D;
/*     */     } }
/*     */   
/*     */   public static void assertInList(Map a_list, Object a_object) {
/* 102 */     if (a_list.containsKey(a_object)) {
/* 103 */       a_list.remove(a_object);
/*     */ 
/*     */ 
/*     */     
/*     */     }
/* 108 */     else if (a_list.containsKey("java.lang." + a_object)) {
/* 109 */       a_list.remove("java.lang." + a_object);
/*     */     } else {
/*     */       
/* 112 */       fail("Object " + a_object + " not in list!");
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public static void assertInList(List a_list, Object a_object) {
/* 118 */     if (a_list.contains(a_object)) {
/* 119 */       a_list.remove(a_object);
/*     */ 
/*     */ 
/*     */     
/*     */     }
/* 124 */     else if (a_list.contains("java.lang." + a_object)) {
/* 125 */       a_list.remove("java.lang." + a_object);
/*     */     } else {
/*     */       
/* 128 */       fail("Object " + a_object + " not in list!");
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
/*     */   
/*     */   public boolean isSerializable(Object a_obj) {
/* 141 */     return Serializable.class.isInstance(a_obj);
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
/*     */ 
/*     */   
/*     */   public Object doSerialize(Object a_obj) throws Exception {
/* 156 */     File f = File.createTempFile("object", "ser");
/* 157 */     OutputStream os = new FileOutputStream(f);
/* 158 */     ObjectOutputStream oos = new ObjectOutputStream(os);
/* 159 */     oos.writeObject(a_obj);
/* 160 */     oos.flush();
/* 161 */     oos.close();
/* 162 */     InputStream oi = new FileInputStream(f);
/* 163 */     ObjectInputStream ois = new ObjectInputStream(oi);
/* 164 */     Object result = ois.readObject();
/* 165 */     ois.close();
/* 166 */     return result;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Object getNestedField(Object a_instance, String a_parentFieldName, String a_childFieldName) throws NoSuchFieldException {
/* 184 */     Object parentField = PrivateAccessor.getField(a_instance, a_parentFieldName);
/* 185 */     Object childField = PrivateAccessor.getField(parentField, a_childFieldName);
/* 186 */     return childField;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setNestedField(Object a_instance, String a_parentFieldName, String a_childFieldName, Object a_value) throws NoSuchFieldException {
/* 204 */     Object parentField = PrivateAccessor.getField(a_instance, a_parentFieldName);
/* 205 */     PrivateAccessor.setField(parentField, a_childFieldName, a_value);
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
/*     */   public boolean uniqueChromosomes(Population a_pop) {
/* 217 */     for (int i = 0; i < a_pop.size() - 1; i++) {
/* 218 */       IChromosome c = a_pop.getChromosome(i);
/* 219 */       for (int j = i + 1; j < a_pop.size(); j++) {
/* 220 */         IChromosome c2 = a_pop.getChromosome(j);
/* 221 */         if (c == c2) {
/* 222 */           return false;
/*     */         }
/*     */       } 
/*     */     } 
/* 226 */     return true;
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
/*     */   public void assertEquals(double a_one, double a_two) {
/* 239 */     assertEquals(a_one, a_two, 1.0E-7D);
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\jgap.jar!\org\jgap\JGAPTestCase.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */