/*     */ package clus.model.pmml.tildepmml;
/*     */ 
/*     */ import clus.algo.tdidt.ClusNode;
/*     */ import clus.model.modelio.tilde.TildeOutReader;
/*     */ import java.io.File;
/*     */ import java.io.FileInputStream;
/*     */ import java.io.FileOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.PrintWriter;
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
/*     */ public class Tilde2PMML
/*     */ {
/*     */   static final String COPYRIGHT = "Jan Struyf, Darek Krzywania";
/*     */   static final String DESCRIPTION = "Predicting skidding for the SolEuNet Challenge";
/*     */   static final String APPLICATIONNAME = "SolEuNet Challenge";
/*     */   static final String APPLICATIONVERSION = "1.0";
/*     */   static final String XMLVERSION = "1.0";
/*     */   static final String XMLENCODING = "ISO-8859-1";
/*     */   
/*     */   public static ClusNode loadTildeTree(InputStream strm) throws IOException {
/*  44 */     TildeOutReader reader = new TildeOutReader(strm);
/*  45 */     reader.doParse();
/*  46 */     ClusNode root = reader.getTree();
/*  47 */     reader.close();
/*  48 */     return root;
/*     */   }
/*     */   
/*     */   public static void main(String[] args) {
/*     */     try {
/*  53 */       ClusNode root = loadTildeTree(new FileInputStream(args[0]));
/*     */ 
/*     */ 
/*     */       
/*  57 */       File outFile = new File("pmmlcode.pmml");
/*  58 */       FileOutputStream outFileStream = new FileOutputStream(outFile);
/*  59 */       PrintWriter outStream = new PrintWriter(outFileStream);
/*     */ 
/*     */       
/*  62 */       String header = "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\n<PMML>\n<Header copyright=\"Jan Struyf, Darek Krzywania\" description=\"Predicting skidding for the SolEuNet Challenge\">\n<Application name=\"SolEuNet Challenge\" version=\"1.0\">\n</Header>\n";
/*  63 */       outStream.write(header);
/*     */ 
/*     */       
/*  66 */       ClusNode currentNode = root;
/*  67 */       Vector itemSets = new Vector();
/*  68 */       depthFirstInit(currentNode, outStream, 0, itemSets, 0, 0, 0, 0);
/*     */ 
/*     */ 
/*     */       
/*  72 */       outStream.write("</PMML>");
/*     */ 
/*     */       
/*  75 */       outStream.close();
/*     */ 
/*     */     
/*     */     }
/*  79 */     catch (IOException e) {
/*  80 */       System.out.println("Error: " + e.getMessage());
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static void depthFirstInit(ClusNode $currentNode, PrintWriter $outStream, int tabs, Vector<Itemset> $itemSets, int $nbOfItems, int $nbOfItemSets, int $open, int $close) throws IOException {
/*     */     try {
/*  90 */       String test = $currentNode.getTest().toString();
/*  91 */       MStreamTokenizer tokens = MStreamTokenizer.createStringParser(test);
/*  92 */       tokens.setCharTokens(",[]():");
/*     */       
/*  94 */       while (tokens.hasMoreTokens()) {
/*  95 */         String name = tokens.readToken();
/*     */         
/*  97 */         Vector TempVector = new Vector();
/*  98 */         Itemset TempItemSet = new Itemset($nbOfItemSets, name, 0, TempVector);
/*  99 */         $nbOfItemSets++;
/*     */         
/* 101 */         int counter1 = tabs;
/*     */         
/* 103 */         while (counter1 > 0) {
/* 104 */           $outStream.write("\t");
/* 105 */           counter1--;
/*     */         } 
/*     */         
/* 108 */         $outStream.write("<Node " + name + ">");
/* 109 */         $open++;
/* 110 */         tokens.readChar('(');
/*     */         
/*     */         do {
/* 113 */           String arg = tokens.readToken();
/* 114 */           $outStream.write("  Arg " + arg);
/*     */           
/* 116 */           Item TempItem = new Item($nbOfItems, false, arg);
/* 117 */           $nbOfItems++;
/* 118 */           TempItemSet.addItemRef(TempItem);
/*     */         }
/* 120 */         while (tokens.isNextToken(','));
/*     */         
/* 122 */         $itemSets.add($nbOfItemSets - 1, TempItemSet);
/*     */         
/* 124 */         tokens.readChar(')');
/* 125 */         tokens.readChar('?');
/* 126 */         tokens.isNextToken(',');
/*     */       } 
/*     */       
/* 129 */       $outStream.write("\n");
/*     */       
/* 131 */       int newTab = tabs + 1;
/*     */       
/* 133 */       for (int idx = 0; idx < $currentNode.getNbChildren(); idx++)
/*     */       {
/* 135 */         ClusNode $childNode = (ClusNode)$currentNode.getChild(idx);
/* 136 */         if (!$childNode.atBottomLevel()) depthFirstInit($childNode, $outStream, newTab, $itemSets, $nbOfItems, $nbOfItemSets, $open, $close);
/*     */ 
/*     */         
/* 139 */         if (idx == $currentNode.getNbChildren() - 1)
/*     */         {
/* 141 */           int counter2 = tabs;
/*     */           
/* 143 */           while (counter2 > 0) {
/* 144 */             $outStream.write("\t");
/* 145 */             counter2--;
/*     */           } 
/*     */ 
/*     */           
/* 149 */           $outStream.write("</Node>\n");
/* 150 */           $close++;
/*     */           
/* 152 */           if ($open == $close) {
/* 153 */             done($itemSets, $outStream);
/*     */           }
/*     */         }
/*     */       
/*     */       }
/*     */     
/* 159 */     } catch (IOException e) {
/* 160 */       System.out.println("Error: " + e.getMessage());
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   static void done(Vector<Itemset> ItemSets, PrintWriter outStream) {
/* 167 */     int counter = ItemSets.size();
/* 168 */     int pointer = 0;
/*     */     
/* 170 */     while (counter > 0) {
/*     */       
/* 172 */       ((Itemset)ItemSets.elementAt(pointer)).print(outStream);
/*     */       
/* 174 */       counter--;
/* 175 */       pointer++;
/*     */     } 
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\clus\model\pmml\tildepmml\Tilde2PMML.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */