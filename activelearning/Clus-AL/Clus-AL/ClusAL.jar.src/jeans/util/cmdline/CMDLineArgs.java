/*     */ package jeans.util.cmdline;
/*     */ 
/*     */ import java.util.Hashtable;
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
/*     */ public class CMDLineArgs
/*     */ {
/*     */   protected CMDLineArgsProvider $prov;
/*  30 */   protected Hashtable $optargs = new Hashtable<>();
/*     */   protected String[] $mainargs;
/*     */   protected boolean $ok;
/*     */   protected int m_NbMainArgs;
/*     */   
/*     */   public CMDLineArgs(CMDLineArgsProvider prov) {
/*  36 */     this.$prov = prov;
/*  37 */     this.$mainargs = new String[prov.getNbMainArgs()];
/*     */   }
/*     */   
/*     */   public void process(String[] args) {
/*  41 */     int idx = 0;
/*  42 */     boolean done = false;
/*  43 */     String[] options = this.$prov.getOptionArgs();
/*  44 */     int[] arities = this.$prov.getOptionArgArities();
/*  45 */     while (idx < args.length && !done) {
/*  46 */       String arg = args[idx];
/*  47 */       if (arg.charAt(0) == '-' && arg.length() > 1) {
/*  48 */         idx++;
/*  49 */         arg = arg.substring(1);
/*  50 */         boolean found = false;
/*  51 */         for (int i = 0; i < options.length && !found; i++) {
/*  52 */           if (arg.equals(options[i])) {
/*  53 */             int arity = arities[i];
/*  54 */             if (args.length - idx >= arity) {
/*  55 */               if (arity == 0) {
/*  56 */                 this.$optargs.put(arg, this);
/*  57 */               } else if (arity == 1) {
/*  58 */                 this.$optargs.put(arg, args[idx++]);
/*     */               } else {
/*  60 */                 String[] vals = new String[arity];
/*  61 */                 for (int j = 0; j < arity; j++) {
/*  62 */                   vals[j] = args[idx + j];
/*     */                 }
/*  64 */                 idx += arity;
/*  65 */                 this.$optargs.put(arg, vals);
/*     */               } 
/*     */             } else {
/*  68 */               this.$prov.showHelp();
/*  69 */               System.out.println();
/*  70 */               System.out.println("Option -" + arg + " requires " + arity + " arguments");
/*     */               return;
/*     */             } 
/*  73 */             found = true;
/*     */           } 
/*     */         } 
/*  76 */         if (!found) {
/*  77 */           this.$prov.showHelp();
/*  78 */           System.out.println();
/*  79 */           System.out.println("Unknown option: -" + arg); return;
/*     */         } 
/*     */         continue;
/*     */       } 
/*  83 */       done = true;
/*     */     } 
/*     */     
/*  86 */     int nbleft = args.length - idx;
/*  87 */     if (nbleft == this.$prov.getNbMainArgs()) {
/*  88 */       for (int i = 0; i < nbleft; i++) {
/*  89 */         this.$mainargs[i] = args[idx + i];
/*     */       }
/*  91 */       this.$ok = true;
/*     */     } 
/*  93 */     this.m_NbMainArgs = nbleft;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean allOK() {
/*  98 */     return this.$ok;
/*     */   }
/*     */   
/*     */   public String getMainArg(int idx) {
/* 102 */     return this.$mainargs[idx];
/*     */   }
/*     */   
/*     */   public boolean hasOption(String option) {
/* 106 */     return (this.$optargs.get(option) != null);
/*     */   }
/*     */   
/*     */   public String getOptionValue(String option) {
/* 110 */     return (String)this.$optargs.get(option);
/*     */   }
/*     */   
/*     */   public int getOptionInteger(String option) {
/* 114 */     return Integer.parseInt((String)this.$optargs.get(option));
/*     */   }
/*     */   
/*     */   public int getOptionInteger(String option, int min, int max) throws IllegalArgumentException {
/* 118 */     String val = (String)this.$optargs.get(option);
/*     */     try {
/* 120 */       int res = Integer.parseInt(val);
/* 121 */       if (res < min || res > max) {
/* 122 */         throw new IllegalArgumentException("Value " + val + " supplied for option '" + option + "' is out of range");
/*     */       }
/* 124 */       return res;
/* 125 */     } catch (NumberFormatException e) {
/* 126 */       throw new IllegalArgumentException("Illegal value '" + val + "' supplied for option '" + option + "': expected integer");
/*     */     } 
/*     */   }
/*     */   
/*     */   public double getOptionDouble(String option, double min, double max) throws IllegalArgumentException {
/* 131 */     String val = (String)this.$optargs.get(option);
/*     */     try {
/* 133 */       double res = Double.parseDouble(val);
/* 134 */       if (res < min || res > max) {
/* 135 */         throw new IllegalArgumentException("Value " + val + " supplied for option '" + option + "' is out of range");
/*     */       }
/* 137 */       return res;
/* 138 */     } catch (NumberFormatException e) {
/* 139 */       throw new IllegalArgumentException("Illegal value '" + val + "' supplied for option '" + option + "': expected real");
/*     */     } 
/*     */   }
/*     */   
/*     */   public int getNbMainArgs() {
/* 144 */     return this.m_NbMainArgs;
/*     */   }
/*     */   
/*     */   public String getOptionValue(String option, int index) {
/* 148 */     String[] vals = (String[])this.$optargs.get(option);
/* 149 */     return vals[index];
/*     */   }
/*     */   
/*     */   public String[] getOptionValues(String option) {
/* 153 */     return (String[])this.$optargs.get(option);
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\jean\\util\cmdline\CMDLineArgs.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */