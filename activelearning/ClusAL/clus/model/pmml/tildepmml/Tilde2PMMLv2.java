/*     */ package clus.model.pmml.tildepmml;
/*     */ 
/*     */ import clus.algo.tdidt.ClusNode;
/*     */ import clus.model.modelio.tilde.TildeOutReader;
/*     */ import clus.statistic.ClassificationStat;
/*     */ import java.io.File;
/*     */ import java.io.FileInputStream;
/*     */ import java.io.FileOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.PrintWriter;
/*     */ import java.util.HashMap;
/*     */ import java.util.Vector;
/*     */ import jeans.util.FileUtil;
/*     */ import jeans.util.MStreamTokenizer;
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
/*     */ public class Tilde2PMMLv2
/*     */ {
/*     */   static final String COPYRIGHT = "Jan Struyf, Darek Krzywania";
/*     */   static final String DESCRIPTION = "Predicting skidding for the SolEuNet Challenge";
/*     */   static final String APPLICATIONNAME = "SolEuNet Challenge";
/*     */   static final String APPLICATIONVERSION = "1.0";
/*     */   static final String XMLVERSION = "1.0";
/*     */   static final String XMLENCODING = "ISO-8859-1";
/*  45 */   static Vector itemSets = new Vector();
/*  46 */   static Vector items = new Vector();
/*  47 */   static HashMap itemsmap = new HashMap<>();
/*  48 */   static int nbOfItems = 0;
/*  49 */   static int itemSetCount = 0;
/*  50 */   static int selectClass = 0;
/*     */ 
/*     */   
/*     */   public static ClusNode loadTildeTree(InputStream strm, int dim, String schema) throws IOException {
/*  54 */     TildeOutReader reader = new TildeOutReader(strm);
/*  55 */     reader.setDim(dim);
/*  56 */     reader.doParse();
/*  57 */     if (schema != null) reader.readTargetSchema(schema); 
/*  58 */     ClusNode root = reader.getTree();
/*  59 */     reader.close();
/*  60 */     return root;
/*     */   }
/*     */   
/*     */   public static void main(String[] args) {
/*     */     try {
/*  65 */       int dim = 1;
/*  66 */       String schemaName = null;
/*  67 */       if (args.length >= 2) dim = Integer.parseInt(args[1]); 
/*  68 */       if (args.length >= 3) selectClass = Integer.parseInt(args[2]); 
/*  69 */       if (args.length >= 4) schemaName = args[3]; 
/*  70 */       ClusNode root = loadTildeTree(new FileInputStream(args[0]), dim, schemaName);
/*     */ 
/*     */       
/*  73 */       File outFile = new File(FileUtil.getName(args[0]) + ".pmml");
/*  74 */       FileOutputStream outFileStream = new FileOutputStream(outFile);
/*  75 */       PrintWriter outStream = new PrintWriter(outFileStream);
/*     */ 
/*     */       
/*  78 */       String header = "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\n<PMML>\n<Header copyright=\"Jan Struyf, Darek Krzywania\" description=\"Predicting skidding for the SolEuNet Challenge\">\n<Application name=\"SolEuNet Challenge\" version=\"1.0\">\n</Header>";
/*  79 */       outStream.println(header);
/*     */       
/*  81 */       depthFirstInit(root, outStream);
/*  82 */       printItems(itemSets, outStream);
/*  83 */       printItemsets(itemSets, outStream);
/*  84 */       depthFirstPrint(root, outStream, 0);
/*     */ 
/*     */ 
/*     */ 
/*     */       
/*  89 */       outStream.println("</PMML>");
/*     */ 
/*     */       
/*  92 */       outStream.close();
/*     */     
/*     */     }
/*  95 */     catch (IOException e) {
/*  96 */       System.out.println("Error: " + e.getMessage());
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   private static Item getOrAddItem(String name, boolean fieldOrValue) {
/* 102 */     Item TempItem = (Item)itemsmap.get(name);
/* 103 */     if (TempItem == null) {
/* 104 */       TempItem = new Item(nbOfItems, fieldOrValue, name);
/* 105 */       itemsmap.put(name, TempItem);
/* 106 */       items.add(TempItem);
/* 107 */       nbOfItems++;
/*     */     } 
/* 109 */     return TempItem;
/*     */   }
/*     */   
/*     */   private static Itemset addItemSet(String name) {
/* 113 */     Itemset TempItemSet = new Itemset(itemSets.size(), name);
/* 114 */     itemSets.add(TempItemSet);
/* 115 */     return TempItemSet;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private static void depthFirstInit(ClusNode $currentNode, PrintWriter $outStream) throws IOException {
/*     */     try {
/* 122 */       String test = $currentNode.getTest().toString();
/* 123 */       MStreamTokenizer tokens = MStreamTokenizer.createStringParser(test);
/* 124 */       tokens.setCharTokens(",[]():=><");
/* 125 */       System.out.println("Line = " + test);
/* 126 */       CompoundPredicate pred = new CompoundPredicate();
/* 127 */       $currentNode.setVisitor(pred);
/* 128 */       while (tokens.hasMoreTokens()) {
/* 129 */         String token1 = tokens.readToken();
/* 130 */         String token2 = tokens.readToken();
/* 131 */         System.out.println("token1 = " + token1);
/* 132 */         System.out.println("token2 = " + token2);
/*     */         
/* 134 */         if (token2.equals("(")) {
/*     */           
/* 136 */           Itemset TempItemSet = addItemSet(token1);
/*     */           while (true) {
/* 138 */             String arg = tokens.readToken();
/* 139 */             Item TempItem = getOrAddItem(arg, false);
/* 140 */             TempItemSet.addItemRef(TempItem);
/* 141 */             if (!tokens.isNextToken(',')) {
/* 142 */               pred.addItemset(TempItemSet);
/* 143 */               tokens.readChar(')'); break;
/*     */             } 
/*     */           } 
/*     */         } else {
/* 147 */           String name = "unknown";
/* 148 */           if (token2.equals("=")) {
/* 149 */             name = "equals";
/* 150 */             tokens.readToken();
/*     */           }
/* 152 */           else if (token2.equals("<")) {
/* 153 */             name = "lessThan";
/*     */           }
/* 155 */           else if (token2.equals(">")) {
/* 156 */             name = "greaterThan";
/*     */           } 
/*     */           
/* 159 */           Itemset TempItemSet = addItemSet(name);
/*     */           
/* 161 */           Item TempItem1 = getOrAddItem(token1, false);
/* 162 */           TempItemSet.addItemRef(TempItem1);
/*     */           
/* 164 */           String arg2 = tokens.readToken();
/* 165 */           Item TempItem2 = getOrAddItem(arg2, true);
/* 166 */           TempItemSet.addItemRef(TempItem2);
/* 167 */           pred.addItemset(TempItemSet);
/*     */         } 
/*     */         
/* 170 */         if (!tokens.isNextToken(',')) {
/* 171 */           tokens.readChar('?');
/*     */         }
/*     */       } 
/*     */ 
/*     */ 
/*     */       
/* 177 */       for (int idx = 0; idx < $currentNode.getNbChildren(); idx++) {
/* 178 */         ClusNode $childNode = (ClusNode)$currentNode.getChild(idx);
/* 179 */         if (!$childNode.atBottomLevel()) {
/* 180 */           depthFirstInit($childNode, $outStream);
/*     */         }
/*     */       } 
/* 183 */     } catch (IOException e) {
/* 184 */       System.out.println("Error: " + e.getMessage());
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static void printItems(Vector $itemSets, PrintWriter $outStream) {
/* 193 */     for (int i = 0; i < items.size(); i++) {
/* 194 */       ((Item)items.elementAt(i)).print($outStream);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static void printItemsets(Vector<Itemset> $itemSets, PrintWriter $outStream) {
/* 203 */     for (int i = 0; i < $itemSets.size(); i++) {
/* 204 */       ((Itemset)$itemSets.elementAt(i)).print($outStream);
/*     */     }
/*     */   }
/*     */   
/*     */   private static void printTabs(PrintWriter $outStream, int tabs) {
/* 209 */     for (int counter = 0; counter < tabs; counter++) {
/* 210 */       $outStream.print("\t");
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static void depthFirstPrint(ClusNode $currentNode, PrintWriter $outStream, int tabs) throws IOException {
/*     */     try {
/* 220 */       printTabs($outStream, tabs);
/* 221 */       ClassificationStat stat = (ClassificationStat)$currentNode.getClusteringStat();
/* 222 */       $outStream.println("<Node recordCount=\"" + (int)stat.getTotalWeight() + "\" score=\"" + stat.getPredictedClassName(selectClass) + "\">");
/*     */       
/* 224 */       for (int idx = 0; idx < stat.getNbClasses(selectClass); idx++) {
/* 225 */         printTabs($outStream, tabs);
/* 226 */         $outStream.println("<ScoreDistribution value=\"" + stat.getClassName(selectClass, idx) + "\" recordCount=\"" + (int)stat.getCount(selectClass, idx) + "\" />");
/*     */       } 
/*     */       
/* 229 */       CompoundPredicate pred = (CompoundPredicate)$currentNode.getVisitor();
/* 230 */       pred.print($outStream, tabs);
/* 231 */       $outStream.println();
/*     */       
/* 233 */       int newTab = tabs + 1;
/* 234 */       int nbChild = $currentNode.getNbChildren();
/* 235 */       for (int i = 0; i < nbChild; i++) {
/* 236 */         ClusNode $childNode = (ClusNode)$currentNode.getChild(i);
/*     */         
/* 238 */         if (!$childNode.atBottomLevel()) {
/* 239 */           depthFirstPrint($childNode, $outStream, newTab);
/*     */         }
/* 241 */         if (i == nbChild - 1) {
/* 242 */           printTabs($outStream, tabs);
/* 243 */           $outStream.write("</Node>\n");
/*     */         
/*     */         }
/*     */       
/*     */       }
/*     */     
/*     */     }
/* 250 */     catch (IOException e) {
/* 251 */       System.out.println("Error: " + e.getMessage());
/*     */     } 
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\clus\model\pmml\tildepmml\Tilde2PMMLv2.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */