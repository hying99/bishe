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
/*     */ 
/*     */ public class Tilde2PMMLv1
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
/*     */ 
/*     */   
/*     */   public static ClusNode loadTildeTree(InputStream strm) throws IOException {
/*  53 */     TildeOutReader reader = new TildeOutReader(strm);
/*  54 */     reader.doParse();
/*  55 */     ClusNode root = reader.getTree();
/*  56 */     reader.close();
/*  57 */     return root;
/*     */   }
/*     */   
/*     */   public static void main(String[] args) {
/*     */     try {
/*  62 */       ClusNode root = loadTildeTree(new FileInputStream(args[0]));
/*     */ 
/*     */       
/*  65 */       File outFile = new File("pmmlcode.pmml");
/*  66 */       FileOutputStream outFileStream = new FileOutputStream(outFile);
/*  67 */       PrintWriter outStream = new PrintWriter(outFileStream);
/*     */ 
/*     */       
/*  70 */       String header = "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\n<PMML>\n<Header copyright=\"Jan Struyf, Darek Krzywania\" description=\"Predicting skidding for the SolEuNet Challenge\">\n<Application name=\"SolEuNet Challenge\" version=\"1.0\">\n</Header>\n";
/*  71 */       outStream.write(header);
/*     */       
/*  73 */       depthFirstInit(root, outStream);
/*  74 */       printItems(itemSets, outStream);
/*  75 */       printItemsets(itemSets, outStream);
/*  76 */       depthFirstPrint(root, outStream, 0);
/*     */ 
/*     */       
/*  79 */       outStream.write("</PMML>");
/*     */ 
/*     */       
/*  82 */       outStream.close();
/*     */ 
/*     */     
/*     */     }
/*  86 */     catch (IOException e) {
/*  87 */       System.out.println("Error: " + e.getMessage());
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static void depthFirstInit(ClusNode $currentNode, PrintWriter $outStream) throws IOException {
/*     */     try {
/*  96 */       String test = $currentNode.getTest().toString();
/*  97 */       MStreamTokenizer tokens = MStreamTokenizer.createStringParser(test);
/*  98 */       tokens.setCharTokens(",[]():");
/*     */       
/* 100 */       while (tokens.hasMoreTokens()) {
/* 101 */         String name = tokens.readToken();
/*     */         
/* 103 */         Vector TempVector = new Vector();
/* 104 */         Itemset TempItemSet = new Itemset(itemSets.size(), name, 0, TempVector);
/*     */         
/* 106 */         tokens.readChar('(');
/*     */         
/*     */         do {
/* 109 */           String arg = tokens.readToken();
/*     */ 
/*     */           
/* 112 */           Item TempItem = (Item)itemsmap.get(arg);
/* 113 */           if (TempItem == null) {
/* 114 */             TempItem = new Item(nbOfItems, false, arg);
/* 115 */             itemsmap.put(arg, TempItem);
/* 116 */             items.add(TempItem);
/* 117 */             nbOfItems++;
/*     */           } 
/*     */           
/* 120 */           TempItemSet.addItemRef(TempItem);
/*     */         
/*     */         }
/* 123 */         while (tokens.isNextToken(','));
/*     */         
/* 125 */         itemSets.add(TempItemSet);
/*     */         
/* 127 */         tokens.readChar(')');
/* 128 */         tokens.readChar('?');
/* 129 */         tokens.isNextToken(',');
/*     */       } 
/*     */ 
/*     */ 
/*     */       
/* 134 */       for (int idx = 0; idx < $currentNode.getNbChildren(); idx++)
/*     */       {
/* 136 */         ClusNode $childNode = (ClusNode)$currentNode.getChild(idx);
/* 137 */         if (!$childNode.atBottomLevel()) depthFirstInit($childNode, $outStream);
/*     */         
/*     */       
/*     */       }
/*     */     
/*     */     }
/* 143 */     catch (IOException e) {
/* 144 */       System.out.println("Error: " + e.getMessage());
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static void printItems(Vector $itemSets, PrintWriter $outStream) {
/* 154 */     for (int i = 0; i < items.size(); i++)
/*     */     {
/* 156 */       ((Item)items.elementAt(i)).print($outStream);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static void printItemsets(Vector<Itemset> $itemSets, PrintWriter $outStream) {
/* 167 */     int nbOfItemSets = $itemSets.size();
/* 168 */     int counter = nbOfItemSets;
/* 169 */     int pointer = 0;
/*     */     
/* 171 */     while (counter > 0) {
/*     */       
/* 173 */       ((Itemset)$itemSets.elementAt(pointer)).print($outStream);
/*     */       
/* 175 */       counter--;
/* 176 */       pointer++;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static void depthFirstPrint(ClusNode $currentNode, PrintWriter $outStream, int tabs) throws IOException {
/*     */     try {
/* 188 */       String test = $currentNode.getTest().toString();
/* 189 */       MStreamTokenizer tokens = MStreamTokenizer.createStringParser(test);
/* 190 */       tokens.setCharTokens(",[]():");
/*     */       
/* 192 */       while (tokens.hasMoreTokens()) {
/* 193 */         tokens.readToken();
/*     */         
/* 195 */         int counter1 = tabs;
/*     */         
/* 197 */         while (counter1 > 0) {
/* 198 */           $outStream.write("\t");
/* 199 */           counter1--;
/*     */         } 
/*     */         
/* 202 */         ClassificationStat stat = (ClassificationStat)$currentNode.getClusteringStat();
/* 203 */         System.out.println(stat);
/* 204 */         $outStream.write("<Node recordCount=\"" + (int)stat.getTotalWeight() + "\" score=\"" + stat.getPredictedClassName(0) + "\">\n");
/*     */         
/* 206 */         for (int i = 0; i < stat.getNbClasses(0); i++) {
/*     */           
/* 208 */           int counter2 = tabs;
/*     */           
/* 210 */           while (counter2 > 0) {
/* 211 */             $outStream.write("\t");
/* 212 */             counter2--;
/*     */           } 
/*     */           
/* 215 */           $outStream.write("<ScoreDistribution value=\"" + stat.getClassName(0, i) + "\" recordCount=\"" + (int)stat.getCount(0, i) + "\" />\n");
/*     */         } 
/*     */ 
/*     */         
/* 219 */         tokens.readChar('(');
/*     */         
/*     */         do {
/* 222 */           tokens.readToken();
/*     */         }
/* 224 */         while (tokens.isNextToken(','));
/*     */         
/* 226 */         int counter3 = tabs;
/*     */         
/* 228 */         while (counter3 > 0) {
/* 229 */           $outStream.write("\t");
/* 230 */           counter3--;
/*     */         } 
/*     */         
/* 233 */         $outStream.write("<ItemsetRef itemsetRef=\"" + itemSetCount + "\" />\n");
/* 234 */         itemSetCount++;
/*     */         
/* 236 */         tokens.readChar(')');
/* 237 */         tokens.readChar('?');
/* 238 */         tokens.isNextToken(',');
/*     */       } 
/*     */ 
/*     */ 
/*     */       
/* 243 */       $outStream.write("\n");
/*     */       
/* 245 */       int newTab = tabs + 1;
/*     */       
/* 247 */       for (int idx = 0; idx < $currentNode.getNbChildren(); idx++)
/*     */       {
/* 249 */         ClusNode $childNode = (ClusNode)$currentNode.getChild(idx);
/* 250 */         if (!$childNode.atBottomLevel()) depthFirstPrint($childNode, $outStream, newTab);
/*     */         
/* 252 */         if (idx == $currentNode.getNbChildren() - 1)
/*     */         {
/* 254 */           int counter4 = tabs;
/*     */           
/* 256 */           while (counter4 > 0) {
/* 257 */             $outStream.write("\t");
/* 258 */             counter4--;
/*     */           } 
/*     */ 
/*     */           
/* 262 */           $outStream.write("</Node>\n");
/*     */         
/*     */         }
/*     */       
/*     */       }
/*     */     
/*     */     }
/* 269 */     catch (IOException e) {
/* 270 */       System.out.println("Error: " + e.getMessage());
/*     */     } 
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\clus\model\pmml\tildepmml\Tilde2PMMLv1.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */