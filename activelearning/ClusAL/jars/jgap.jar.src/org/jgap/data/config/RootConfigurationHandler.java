/*     */ package org.jgap.data.config;
/*     */ 
/*     */ import java.lang.reflect.Field;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import org.jgap.InvalidConfigurationException;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class RootConfigurationHandler
/*     */   implements ConfigurationHandler
/*     */ {
/*     */   private static final String CVS_REVISION = "$Revision: 1.11 $";
/*     */   private static final String CONFIG_NAMESPACE = "org.jgap.Configuration";
/*     */   private static final String GENETIC_OPS = "GeneticOperators";
/*     */   private static final String NATURAL_SELS = "NaturalSelectors";
/*     */   private Configurable m_configurable;
/*     */   
/*     */   public String getName() {
/*  48 */     return "Configuration";
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public List getConfigProperties() {
/*  57 */     return null;
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
/*     */   public String getNS() {
/*  69 */     return "org.jgap.Configuration";
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
/*     */   public void readConfig() throws ConfigException, InvalidConfigurationException {
/*  87 */     ConfigFileReader.instance().setNS("org.jgap.Configuration");
/*  88 */     String value = ConfigFileReader.instance().getValue("m_populationSize");
/*     */     try {
/*  90 */       if (value != null) {
/*  91 */         setConfigProperty(this.m_configurable, "m_populationSize", value);
/*     */       }
/*  93 */     } catch (IllegalAccessException ex) {
/*  94 */       ex.printStackTrace();
/*  95 */       throw new InvalidConfigurationException(ex.getMessage());
/*     */     } 
/*     */     
/*  98 */     configureClass("GeneticOperators");
/*     */     
/* 100 */     configureClass("NaturalSelectors");
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
/*     */   public void setConfigurable(Configurable a_configurable) {
/* 112 */     this.m_configurable = a_configurable;
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
/*     */   public void setConfigProperty(Object a_configurable, String a_propertyName, String a_value) throws IllegalAccessException {
/* 141 */     String configVarName = "m_config";
/* 142 */     Field configVar = getPrivateField(a_configurable, configVarName);
/* 143 */     configVar.setAccessible(true);
/* 144 */     Object configObj = configVar.get(a_configurable);
/* 145 */     Field propertyVar = getPrivateField(configObj, a_propertyName);
/* 146 */     propertyVar.setAccessible(true);
/* 147 */     Class<?> type = propertyVar.getType();
/* 148 */     if (type.equals(boolean.class)) {
/* 149 */       propertyVar.setBoolean(configObj, Boolean.valueOf(a_value).booleanValue());
/*     */     }
/* 151 */     else if (type.equals(byte.class)) {
/* 152 */       propertyVar.setByte(configObj, Byte.valueOf(a_value).byteValue());
/*     */     }
/* 154 */     else if (type.equals(char.class)) {
/* 155 */       propertyVar.setChar(configObj, a_value.charAt(0));
/*     */     }
/* 157 */     else if (type.equals(double.class)) {
/* 158 */       propertyVar.setDouble(configObj, Double.valueOf(a_value).doubleValue());
/*     */     }
/* 160 */     else if (type.equals(float.class)) {
/* 161 */       propertyVar.setFloat(configObj, Float.valueOf(a_value).floatValue());
/*     */     }
/* 163 */     else if (type.equals(int.class)) {
/* 164 */       propertyVar.setInt(configObj, Integer.valueOf(a_value).intValue());
/*     */     }
/* 166 */     else if (type.equals(long.class)) {
/* 167 */       propertyVar.setLong(configObj, Long.valueOf(a_value).longValue());
/*     */     }
/* 169 */     else if (type.equals(short.class)) {
/* 170 */       propertyVar.setShort(configObj, Short.valueOf(a_value).shortValue());
/*     */     }
/* 172 */     else if (type.equals(String.class)) {
/* 173 */       propertyVar.set(configObj, a_value);
/*     */     } else {
/*     */       
/* 176 */       throw new RuntimeException("Unknown field type: " + type.getName());
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
/*     */   
/*     */   public static Field getPrivateField(Object a_instance, String a_fieldName) {
/* 190 */     Field[] fields = a_instance.getClass().getDeclaredFields();
/* 191 */     for (int i = 0; i < fields.length; i++) {
/* 192 */       if (a_fieldName.equals(fields[i].getName())) {
/* 193 */         fields[i].setAccessible(true);
/* 194 */         return fields[i];
/*     */       } 
/*     */     } 
/* 197 */     return null;
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
/*     */   public static void configureClass(String className) throws ConfigException {
/* 211 */     List values = ConfigFileReader.instance().getValues(className);
/* 212 */     if (values != null && values.size() > 0) {
/* 213 */       String cName = "";
/*     */ 
/*     */       
/* 216 */       for (Iterator<String> iter = values.iterator(); iter.hasNext();) {
/*     */         try {
/* 218 */           cName = iter.next();
/* 219 */           Class<?> genClass = Class.forName(cName);
/* 220 */           Configurable conObj = (Configurable)genClass.newInstance();
/*     */         
/*     */         }
/* 223 */         catch (Exception ex) {
/* 224 */           throw new ConfigException("Error while configuring " + className + "." + cName);
/*     */         } 
/*     */       } 
/*     */     } 
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\jgap.jar!\org\jgap\data\config\RootConfigurationHandler.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */