/*     */ package clus.heuristic;
/*     */ 
/*     */ import clus.algo.tdidt.ClusNode;
/*     */ import clus.data.rows.RowData;
/*     */ import clus.data.type.ClusAttrType;
/*     */ import java.util.ArrayList;
/*     */ import org.apache.commons.math.special.Gamma;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class EncodingCost
/*     */ {
/*     */   protected ArrayList<RowData> data;
/*     */   protected ArrayList<Integer> m_ClusterIds;
/*     */   protected ArrayList<ClusNode> nodes;
/*     */   protected ClusAttrType[] attributes;
/*  22 */   protected double[] m_MixtureValues = new double[] { 0.178091D, 0.056591D, 0.0960191D, 0.0781233D, 0.0834977D, 0.0904123D, 0.114468D, 0.0682132D, 0.234585D };
/*  23 */   protected double[][] m_AlphaValues = new double[][] { { 1.18065D, 0.270671D, 0.039848D, 0.017576D, 0.016415D, 0.014268D, 0.131916D, 0.012391D, 0.022599D, 0.020358D, 0.030727D, 0.015315D, 0.048298D, 0.053803D, 0.020662D, 0.023612D, 0.216147D, 0.147226D, 0.065438D, 0.003758D, 0.009621D }, { 1.35583D, 0.021465D, 0.0103D, 0.011741D, 0.010883D, 0.385651D, 0.016416D, 0.076196D, 0.035329D, 0.013921D, 0.093517D, 0.022034D, 0.028593D, 0.013086D, 0.023011D, 0.018866D, 0.029156D, 0.018153D, 0.0361D, 0.07177D, 0.419641D }, { 6.66436D, 0.561459D, 0.045448D, 0.438366D, 0.764167D, 0.087364D, 0.259114D, 0.21494D, 0.145928D, 0.762204D, 0.24732D, 0.118662D, 0.441564D, 0.174822D, 0.53084D, 0.465529D, 0.583402D, 0.445586D, 0.22705D, 0.02951D, 0.12109D }, { 2.08141D, 0.070143D, 0.01114D, 0.019479D, 0.094657D, 0.013162D, 0.048038D, 0.077D, 0.032939D, 0.576639D, 0.072293D, 0.02824D, 0.080372D, 0.037661D, 0.185037D, 0.506783D, 0.073732D, 0.071587D, 0.042532D, 0.011254D, 0.028723D }, { 2.08101D, 0.041103D, 0.014794D, 0.00561D, 0.010216D, 0.153602D, 0.007797D, 0.007175D, 0.299635D, 0.010849D, 0.999446D, 0.210189D, 0.006127D, 0.013021D, 0.019798D, 0.014509D, 0.012049D, 0.035799D, 0.180085D, 0.012744D, 0.026466D }, { 2.56819D, 0.115607D, 0.037381D, 0.012414D, 0.018179D, 0.051778D, 0.017255D, 0.004911D, 0.796882D, 0.017074D, 0.285858D, 0.075811D, 0.014548D, 0.015092D, 0.011382D, 0.012696D, 0.027535D, 0.088333D, 0.94434D, 0.004373D, 0.016741D }, { 1.76606D, 0.093461D, 0.004737D, 0.387252D, 0.347841D, 0.010822D, 0.105877D, 0.049776D, 0.014963D, 0.094276D, 0.027761D, 0.01004D, 0.187869D, 0.050018D, 0.110039D, 0.038668D, 0.119471D, 0.065802D, 0.02543D, 0.003215D, 0.018742D }, { 4.98768D, 0.452171D, 0.114613D, 0.06246D, 0.115702D, 0.284246D, 0.140204D, 0.100358D, 0.55023D, 0.143995D, 0.700649D, 0.27658D, 0.118569D, 0.09747D, 0.126673D, 0.143634D, 0.278983D, 0.358482D, 0.66175D, 0.061533D, 0.199373D }, { 0.0995D, 0.005193D, 0.004039D, 0.006722D, 0.006121D, 0.003468D, 0.016931D, 0.003647D, 0.002184D, 0.005019D, 0.00599D, 0.001473D, 0.004158D, 0.009055D, 0.00363D, 0.006583D, 0.003172D, 0.00369D, 0.002967D, 0.002772D, 0.002686D } };
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
/*  37 */   protected double[] m_MixtureValuesRecode4 = new double[] { 0.383048D, 0.174635D, 0.0852555D, 0.0594628D, 0.0406419D, 0.0398597D, 0.0297682D, 0.0280806D, 0.0242095D, 0.019244D, 0.0178775D, 0.0174343D, 0.0168758D, 0.0139663D, 0.0123711D, 0.0123012D, 0.0111316D, 0.00698083D, 0.00365577D, 0.00320122D };
/*  38 */   protected double[][] m_AlphaValuesRecode4 = new double[][] { { 4.04735D, 0.335796D, 0.0276254D, 0.270793D, 0.348046D, 0.0863198D, 0.225768D, 0.115656D, 0.120617D, 0.395388D, 0.21713D, 0.0692591D, 0.22952D, 0.159096D, 0.25475D, 0.307135D, 0.275135D, 0.265742D, 0.182309D, 0.043852D, 0.117411D }, { 3.5659D, 0.273194D, 0.0647217D, 0.0365618D, 0.0448084D, 0.266448D, 0.0796421D, 0.039374D, 0.522913D, 0.0461325D, 0.864337D, 0.183843D, 0.0445531D, 0.0609869D, 0.050193D, 0.0545329D, 0.0736139D, 0.136326D, 0.533931D, 0.057054D, 0.132732D }, { 0.803228D, 0.05911D, 0.0127852D, 0.0785758D, 0.0363021D, 0.0289509D, 0.160842D, 0.0281857D, 0.0130422D, 0.0203385D, 0.0267507D, 0.00397971D, 0.0430017D, 0.0689402D, 0.0127304D, 0.026D, 0.0605953D, 0.0423799D, 0.0340264D, 0.0125806D, 0.0341105D }, { 9.08397D, 2.29698D, 0.265451D, 0.155901D, 0.171394D, 0.3867D, 0.869853D, 0.102854D, 0.514941D, 0.0695931D, 0.759599D, 0.234631D, 0.14661D, 0.177732D, 0.127139D, 0.135953D, 0.74977D, 0.457832D, 1.05026D, 0.0986083D, 0.312162D }, { 53.6176D, 3.26115D, 0.745759D, 3.18917D, 2.67227D, 4.15961D, 4.55029D, 3.01435D, 2.39119D, 0.70697D, 4.38827D, 1.29158D, 3.68427D, 1.80724D, 1.47D, 1.6602D, 3.67109D, 2.30334D, 2.75308D, 2.05938D, 3.8384D }, { 105.708D, 12.1186D, 0.472681D, 3.87403D, 6.5048D, 3.46117D, 2.97363D, 1.67069D, 8.55045D, 9.64583D, 13.6136D, 3.75321D, 2.19455D, 1.68259D, 4.18394D, 10.7077D, 2.96826D, 3.09571D, 8.1825D, 1.72721D, 4.32637D }, { 76.7013D, 10.2085D, 1.7407D, 2.52751D, 1.66695D, 4.64628D, 2.93189D, 2.32366D, 7.7074D, 1.63543D, 12.4467D, 3.3191D, 2.36645D, 0.915427D, 2.74109D, 1.49944D, 2.30864D, 3.94574D, 7.72884D, 0.865209D, 3.17628D }, { 34.3245D, 1.69321D, 0.561402D, 0.242201D, 0.163758D, 1.3284D, 0.426017D, 0.217733D, 9.19979D, 0.365122D, 3.00267D, 0.470859D, 0.200578D, 0.454379D, 0.166355D, 0.285794D, 0.566137D, 1.17851D, 12.8947D, 0.182001D, 0.724889D }, { 13.549D, 0.699016D, 0.123918D, 0.564493D, 0.441809D, 0.265585D, 0.455207D, 0.157372D, 0.45222D, 0.315356D, 0.285096D, 0.11236D, 0.700959D, 0.384523D, 0.341402D, 0.19505D, 3.04705D, 3.86283D, 0.771455D, 0.0747102D, 0.298617D }, { 27.5029D, 2.69506D, 0.0984665D, 0.999399D, 0.702161D, 0.18828D, 14.7923D, 0.204418D, 0.341519D, 0.895261D, 0.604879D, 0.264405D, 1.20003D, 0.526937D, 0.485106D, 0.528848D, 1.54988D, 0.759294D, 0.220308D, 0.11594D, 0.330426D }, { 40.1814D, 3.07208D, 0.179581D, 1.59068D, 1.1802D, 1.04447D, 1.82765D, 0.299515D, 1.5785D, 2.07133D, 2.48759D, 0.1886D, 1.17791D, 13.8668D, 0.831189D, 1.07904D, 2.36316D, 1.88465D, 2.35323D, 0.274462D, 0.83075D }, { 17.5374D, 1.41269D, 0.00565739D, 3.37209D, 5.65739D, 0.244139D, 0.61415D, 0.121496D, 0.154197D, 0.767153D, 0.494589D, 0.155884D, 0.280603D, 0.263088D, 1.48884D, 0.353594D, 1.00131D, 0.538288D, 0.33596D, 0.0728146D, 0.20351D }, { 86.0527D, 3.33374D, 1.15604D, 1.93175D, 1.30643D, 4.97546D, 21.2633D, 2.00541D, 9.73505D, 1.95251D, 5.17488D, 3.25167D, 2.10234D, 3.3081D, 1.36967D, 2.43658D, 2.02859D, 5.17926D, 7.52289D, 1.61701D, 4.402D }, { 58.4428D, 2.53998D, 0.64182D, 6.22299D, 1.7898D, 1.14222D, 9.70006D, 1.57777D, 0.358056D, 2.07334D, 1.25521D, 0.410731D, 11.0338D, 3.16446D, 1.38371D, 1.5993D, 9.83971D, 1.71336D, 0.41867D, 0.428225D, 1.14954D }, { 157.364D, 25.0469D, 0.0287555D, 14.1297D, 23.3767D, 0.557453D, 5.28742D, 2.81354D, 1.20677D, 28.7555D, 4.133D, 0.151853D, 6.64437D, 2.01447D, 14.4893D, 12.8818D, 10.9112D, 3.32116D, 1.0243D, 0.0487539D, 0.54082D }, { 37.4997D, 1.50426D, 0.106938D, 15.2516D, 2.40265D, 0.329304D, 1.80122D, 0.505069D, 0.674739D, 1.40725D, 0.728632D, 0.237147D, 5.72791D, 0.73936D, 0.725283D, 0.628562D, 2.17878D, 1.44031D, 0.811792D, 0.0152516D, 0.283625D }, { 17.3228D, 0.485358D, 0.120765D, 0.132386D, 0.22325D, 2.74084D, 0.290926D, 0.536354D, 0.448205D, 0.240466D, 0.81734D, 0.216503D, 0.223577D, 0.141958D, 0.247041D, 0.367824D, 0.470015D, 0.441281D, 0.803322D, 0.635744D, 7.73963D }, { 16.9772D, 0.422519D, 0.162589D, 0.063178D, 0.232174D, 8.8294D, 0.198806D, 0.135336D, 0.589954D, 0.127259D, 1.13028D, 0.213526D, 0.0760594D, 0.140012D, 0.15649D, 0.0125594D, 0.33088D, 0.331744D, 0.624835D, 0.909754D, 2.28988D }, { 20.8417D, 0.350372D, 0.113692D, 0.0720512D, 0.210516D, 1.2529D, 0.258305D, 0.13086D, 0.43595D, 0.15333D, 1.31274D, 0.32149D, 0.0944224D, 0.0966389D, 0.0963917D, 0.356382D, 0.206486D, 0.167836D, 0.582403D, 13.55D, 1.07893D }, { 29.9957D, 0.161616D, 28.1472D, 0.0281472D, 0.0281472D, 0.26712D, 0.0281472D, 0.0281472D, 0.220459D, 0.0281472D, 0.0527309D, 0.0281472D, 0.0440255D, 0.0281472D, 0.0281472D, 0.0281472D, 0.152965D, 0.0886627D, 0.456531D, 0.0947103D, 0.0563106D } };
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
/*     */   protected double[] m_ZAlpha;
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
/* 122 */   public long[] m_Duration = new long[3];
/*     */   protected double[][] m_LogGammaAlpha;
/*     */   protected double[][] m_LogPMatrix;
/*     */   
/*     */   public EncodingCost(ArrayList<ClusNode> listNodes, ArrayList<RowData> subsets, ClusAttrType[] attrs) {
/* 127 */     this.data = subsets;
/* 128 */     this.nodes = listNodes;
/* 129 */     this.attributes = attrs;
/*     */ 
/*     */     
/* 132 */     this.m_ZAlpha = new double[this.m_AlphaValues.length];
/*     */     
/* 134 */     for (int k = 0; k < this.m_AlphaValues.length; k++)
/*     */     {
/*     */       
/* 137 */       this.m_ZAlpha[k] = functionZ(this.m_AlphaValues[k], 1, 20);
/*     */     }
/*     */   }
/*     */   
/*     */   public EncodingCost(ArrayList<RowData> subsets, ClusAttrType[] attrs) {
/* 142 */     this.data = subsets;
/* 143 */     this.attributes = attrs;
/*     */ 
/*     */     
/* 146 */     this.m_ZAlpha = new double[this.m_AlphaValues.length];
/*     */     
/* 148 */     for (int k = 0; k < this.m_AlphaValues.length; k++)
/*     */     {
/*     */       
/* 151 */       this.m_ZAlpha[k] = functionZ(this.m_AlphaValues[k], 1, 20);
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public EncodingCost() {
/* 157 */     this.m_ZAlpha = new double[this.m_AlphaValues.length];
/*     */     
/* 159 */     for (int k = 0; k < this.m_AlphaValues.length; k++)
/*     */     {
/*     */       
/* 162 */       this.m_ZAlpha[k] = functionZ(this.m_AlphaValues[k], 1, 20);
/*     */     }
/*     */     
/* 165 */     calculateLogGammaAlphaValues();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void initializeLogPMatrix(int nbnodes) {
/* 171 */     this.m_LogPMatrix = new double[nbnodes][this.attributes.length];
/*     */   }
/*     */   
/*     */   public void calculateLogGammaAlphaValues() {
/* 175 */     this.m_LogGammaAlpha = new double[this.m_AlphaValues.length][(this.m_AlphaValues[0]).length];
/* 176 */     for (int j = 0; j < this.m_LogGammaAlpha.length; j++) {
/* 177 */       for (int i = 0; i < (this.m_LogGammaAlpha[0]).length; i++) {
/* 178 */         this.m_LogGammaAlpha[j][i] = Gamma.logGamma(this.m_AlphaValues[j][i]);
/*     */       }
/*     */     } 
/*     */   }
/*     */   
/*     */   public void printDuration() {
/* 184 */     System.out.print("Timings: ");
/* 185 */     for (int i = 0; i < this.m_Duration.length; i++) {
/* 186 */       System.out.print(this.m_Duration[i] + " ");
/*     */     }
/* 188 */     System.out.println();
/*     */   }
/*     */   
/*     */   public void setClusters(ArrayList<RowData> clusters, ArrayList<Integer> clusterIds) {
/* 192 */     this.data = clusters;
/* 193 */     this.m_ClusterIds = clusterIds;
/*     */   }
/*     */   
/*     */   public void setAttributes(ClusAttrType[] attrs) {
/* 197 */     this.attributes = attrs;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getNbClusters() {
/* 202 */     return this.data.size();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int printAlphaValues() {
/* 210 */     for (int i = 0; i < this.m_AlphaValues.length; i++) {
/*     */       
/* 212 */       for (int j = 0; j < (this.m_AlphaValues[i]).length; j++) {
/* 213 */         System.out.print(this.m_AlphaValues[i][j] + "\t");
/*     */       }
/* 215 */       System.out.print("\n");
/*     */     } 
/* 217 */     return 0;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private int printMatrix(int[][] frequency) {
/* 224 */     for (int i = 0; i < frequency.length; i++) {
/*     */       
/* 226 */       for (int j = 0; j < (frequency[i]).length; j++) {
/* 227 */         System.out.print(frequency[i][j] + "\t");
/*     */       }
/* 229 */       System.out.print("\n");
/*     */     } 
/* 231 */     return 0;
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
/*     */   private int printInstanceLabels(int nbSubsets) {
/* 313 */     for (int i = 0; i < nbSubsets; i++) {
/* 314 */       int nbRows = ((RowData)this.data.get(i)).getNbRows();
/* 315 */       for (int r = 0; r < nbRows; r++) {
/* 316 */         String key = ((RowData)this.data.get(i)).getSchema().getKeyAttribute()[0].getString(((RowData)this.data.get(i)).getTuple(r));
/* 317 */         System.out.print(key + " ");
/*     */       } 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 323 */       System.out.print("\n");
/*     */     } 
/* 325 */     return 0;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected int[][] calculateFrequency(int position, int nbSubsets) {
/* 332 */     int[][] frequency = new int[nbSubsets][21];
/* 333 */     for (int i = 0; i < nbSubsets; i++) {
/*     */       
/* 335 */       int nbRows = ((RowData)this.data.get(i)).getNbRows();
/* 336 */       for (int r = 0; r < nbRows; r++) {
/*     */         
/* 338 */         int index = this.attributes[position].getNominal(((RowData)this.data.get(i)).getTuple(r));
/* 339 */         if (index < 20) {
/* 340 */           frequency[i][index + 1] = frequency[i][index + 1] + 1;
/* 341 */           frequency[i][0] = frequency[i][0] + 1;
/*     */         } 
/*     */       } 
/*     */     } 
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
/* 357 */     return frequency;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public double functionGamma(double x) {
/* 364 */     return Math.exp(Gamma.logGamma(x));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected double functionZ(double[] vector, int start, int end) {
/* 373 */     double valueFunction = 1.0D;
/* 374 */     double sum = 0.0D;
/*     */     
/* 376 */     for (int i = start; i <= end; i++) {
/* 377 */       valueFunction *= functionGamma(vector[i]);
/* 378 */       sum += vector[i];
/*     */     } 
/*     */     
/* 381 */     valueFunction /= functionGamma(sum);
/*     */     
/* 383 */     return valueFunction;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected double functionZAlternative(double[] vector) {
/* 392 */     double valueFunction = 1.0D;
/* 393 */     double sum = 0.0D;
/*     */     
/* 395 */     double logpart1 = 0.0D;
/*     */ 
/*     */     
/* 398 */     for (int i = 0; i < vector.length; i++) {
/* 399 */       logpart1 += Gamma.logGamma(vector[i]);
/* 400 */       sum += vector[i];
/*     */     } 
/*     */     
/* 403 */     double logpart2 = Gamma.logGamma(sum);
/*     */     
/* 405 */     valueFunction = Math.exp(logpart1 - logpart2);
/*     */     
/* 407 */     return valueFunction;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected double functionLogZ(double[] vector) {
/* 416 */     double sum = 0.0D;
/* 417 */     double logpart1 = 0.0D;
/*     */ 
/*     */     
/* 420 */     for (int i = 0; i < vector.length; i++) {
/* 421 */       logpart1 += Gamma.logGamma(vector[i]);
/* 422 */       sum += vector[i];
/*     */     } 
/*     */     
/* 425 */     double logpart2 = Gamma.logGamma(sum);
/*     */     
/* 427 */     double valueFunction = logpart1 - logpart2;
/*     */     
/* 429 */     return valueFunction;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected double[] addAlphaVectorAndFrequencyvector(double[] alphaVector, int[] frequencyVector) {
/* 437 */     double[] addedVector = new double[20];
/* 438 */     for (int i = 1; i < 21; i++) {
/* 439 */       addedVector[i - 1] = frequencyVector[i] + alphaVector[i];
/*     */     }
/* 441 */     return addedVector;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public double getEncodingCostValueStandard() {
/* 451 */     int nbAttr = this.attributes.length;
/* 452 */     int nbSubsets = this.data.size();
/* 453 */     double encodingCostValue = nbSubsets * Math.log(nbSubsets) / Math.log(2.0D);
/*     */     
/* 455 */     double[] zAlpha = new double[this.m_AlphaValues.length];
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 461 */     for (int k = 0; k < this.m_AlphaValues.length; k++)
/*     */     {
/*     */       
/* 464 */       zAlpha[k] = functionZ(this.m_AlphaValues[k], 1, 20);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 472 */     for (int j = 0; j < nbAttr; j++) {
/* 473 */       int[][] frequency = calculateFrequency(j, nbSubsets);
/*     */ 
/*     */ 
/*     */       
/* 477 */       for (int i = 0; i < nbSubsets; i++) {
/*     */ 
/*     */         
/* 480 */         double probability = 0.0D;
/*     */         
/* 482 */         for (int m = 0; m < this.m_AlphaValues.length; m++) {
/*     */           
/* 484 */           double mixture = this.m_MixtureValues[m];
/*     */ 
/*     */ 
/*     */           
/* 488 */           double ZAlphaFreq = functionZAlternative(addAlphaVectorAndFrequencyvector(this.m_AlphaValues[m], frequency[i]));
/*     */ 
/*     */           
/* 491 */           probability += mixture * ZAlphaFreq / zAlpha[m];
/*     */         } 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */         
/* 498 */         double logProb = Math.log(probability);
/*     */         
/* 500 */         encodingCostValue -= logProb;
/*     */       } 
/*     */     } 
/*     */     
/* 504 */     return encodingCostValue;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public double getEncodingCostValueWithNormalizerSlower() {
/* 511 */     long start = System.currentTimeMillis();
/*     */     
/* 513 */     int nbAttr = this.attributes.length;
/*     */     
/* 515 */     int nbSubsets = this.data.size();
/*     */ 
/*     */ 
/*     */     
/* 519 */     double encodingCostValue = nbSubsets * Math.log(nbSubsets) / Math.log(2.0D);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 527 */     for (int j = 0; j < nbAttr; j++) {
/*     */       
/* 529 */       long freqstart = System.currentTimeMillis();
/*     */       
/* 531 */       int[][] frequency = calculateFrequency(j, nbSubsets);
/*     */       
/* 533 */       long freqstop = System.currentTimeMillis();
/* 534 */       this.m_Duration[1] = this.m_Duration[1] + freqstop - freqstart;
/*     */ 
/*     */       
/* 537 */       for (int i = 0; i < nbSubsets; i++) {
/*     */ 
/*     */         
/* 540 */         double[] logPJ = new double[this.m_AlphaValues.length];
/* 541 */         double normalizer = 0.0D;
/*     */         
/* 543 */         for (int k = 0; k < this.m_AlphaValues.length; k++) {
/* 544 */           double mixture = this.m_MixtureValues[k];
/*     */           
/* 546 */           long logzafstart = System.currentTimeMillis();
/*     */ 
/*     */           
/* 549 */           double logZAlphaFreq = functionLogZ(addAlphaVectorAndFrequencyvector(this.m_AlphaValues[k], frequency[i]));
/* 550 */           long logzafstop = System.currentTimeMillis();
/* 551 */           this.m_Duration[2] = this.m_Duration[2] + logzafstop - logzafstart;
/*     */           
/* 553 */           logPJ[k] = Math.log(mixture) + logZAlphaFreq - Math.log(this.m_ZAlpha[k]);
/*     */ 
/*     */           
/* 556 */           if (k == 0) {
/* 557 */             normalizer = logPJ[k];
/*     */           }
/* 559 */           else if (normalizer < logPJ[k]) {
/* 560 */             normalizer = logPJ[k];
/*     */           } 
/*     */         } 
/*     */ 
/*     */         
/* 565 */         double sumAllPJ = 0.0D;
/*     */         
/* 567 */         for (int m = 0; m < this.m_AlphaValues.length; m++) {
/* 568 */           double convertingBackValue = Math.exp(logPJ[m] - normalizer);
/* 569 */           sumAllPJ += convertingBackValue;
/*     */         } 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */         
/* 577 */         double logProb = Math.log(sumAllPJ) + normalizer;
/*     */         
/* 579 */         encodingCostValue -= logProb;
/*     */       } 
/*     */     } 
/*     */     
/* 583 */     long stop = System.currentTimeMillis();
/* 584 */     this.m_Duration[0] = this.m_Duration[0] + stop - start;
/*     */     
/* 586 */     return encodingCostValue;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public double getEncodingCostValueWithNormalizer() {
/* 592 */     int nbAttr = this.attributes.length;
/* 593 */     int nbSubsets = this.data.size();
/*     */     
/* 595 */     double encodingCostValue = nbSubsets * Math.log(nbSubsets) / Math.log(2.0D);
/*     */ 
/*     */     
/* 598 */     for (int c = 0; c < nbAttr; c++) {
/*     */ 
/*     */ 
/*     */       
/* 602 */       int[][] frequency = calculateFrequency(c, nbSubsets);
/*     */ 
/*     */ 
/*     */       
/* 606 */       for (int s = 0; s < nbSubsets; s++) {
/*     */         double logProb;
/* 608 */         if (this.m_LogPMatrix[((Integer)this.m_ClusterIds.get(s)).intValue()][c] != 0.0D) {
/* 609 */           logProb = this.m_LogPMatrix[((Integer)this.m_ClusterIds.get(s)).intValue()][c];
/*     */         }
/*     */         else {
/*     */           
/* 613 */           double[] logP = new double[this.m_MixtureValues.length];
/* 614 */           double normalizer = Double.NEGATIVE_INFINITY;
/*     */           
/* 616 */           for (int j = 0; j < this.m_MixtureValues.length; j++) {
/* 617 */             double tmp = 0.0D;
/* 618 */             for (int k = 1; k < (this.m_AlphaValues[0]).length; k++) {
/* 619 */               if (frequency[s][k] > 0) {
/* 620 */                 tmp += Gamma.logGamma(this.m_AlphaValues[j][k] + frequency[s][k]) - this.m_LogGammaAlpha[j][k];
/*     */               }
/*     */             } 
/*     */             
/* 624 */             double[] sumVectors = addAlphaVectorAndFrequencyvector(this.m_AlphaValues[j], frequency[s]);
/* 625 */             double sum = 0.0D;
/* 626 */             for (int x = 0; x < sumVectors.length; x++) {
/* 627 */               sum += sumVectors[x];
/*     */             }
/* 629 */             double tmp2 = this.m_LogGammaAlpha[j][0] - Gamma.logGamma(sum);
/*     */             
/* 631 */             logP[j] = Math.log(this.m_MixtureValues[j]) + tmp + tmp2;
/*     */ 
/*     */             
/* 634 */             if (logP[j] > normalizer) {
/* 635 */               normalizer = logP[j];
/*     */             }
/*     */           } 
/*     */ 
/*     */           
/* 640 */           double sumAllPJ = 0.0D;
/* 641 */           for (int i = 0; i < this.m_MixtureValues.length; i++) {
/* 642 */             double convertingBackValue = Math.exp(logP[i] - normalizer);
/* 643 */             sumAllPJ += convertingBackValue;
/*     */           } 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */           
/* 650 */           logProb = Math.log(sumAllPJ) + normalizer;
/*     */           
/* 652 */           this.m_LogPMatrix[((Integer)this.m_ClusterIds.get(s)).intValue()][c] = logProb;
/*     */         } 
/* 654 */         encodingCostValue -= logProb;
/*     */       } 
/*     */     } 
/* 657 */     return encodingCostValue;
/*     */   }
/*     */   
/*     */   public double getEncodingCostValueWithNormalizerComputeEverything() {
/* 661 */     int nbAttr = this.attributes.length;
/* 662 */     int nbSubsets = this.data.size();
/*     */     
/* 664 */     double encodingCostValue = nbSubsets * Math.log(nbSubsets) / Math.log(2.0D);
/*     */ 
/*     */     
/* 667 */     for (int c = 0; c < nbAttr; c++) {
/*     */ 
/*     */ 
/*     */       
/* 671 */       int[][] frequency = calculateFrequency(c, nbSubsets);
/*     */ 
/*     */ 
/*     */       
/* 675 */       for (int s = 0; s < nbSubsets; s++) {
/*     */ 
/*     */         
/* 678 */         double[] logP = new double[this.m_MixtureValues.length];
/* 679 */         double normalizer = Double.NEGATIVE_INFINITY;
/*     */         
/* 681 */         for (int j = 0; j < this.m_MixtureValues.length; j++) {
/* 682 */           double tmp = 0.0D;
/* 683 */           for (int k = 1; k < (this.m_AlphaValues[0]).length; k++) {
/* 684 */             if (frequency[s][k] > 0) {
/* 685 */               tmp += Gamma.logGamma(this.m_AlphaValues[j][k] + frequency[s][k]) - this.m_LogGammaAlpha[j][k];
/*     */             }
/*     */           } 
/*     */           
/* 689 */           double[] sumVectors = addAlphaVectorAndFrequencyvector(this.m_AlphaValues[j], frequency[s]);
/* 690 */           double sum = 0.0D;
/* 691 */           for (int x = 0; x < sumVectors.length; x++) {
/* 692 */             sum += sumVectors[x];
/*     */           }
/* 694 */           double tmp2 = this.m_LogGammaAlpha[j][0] - Gamma.logGamma(sum);
/*     */           
/* 696 */           logP[j] = Math.log(this.m_MixtureValues[j]) + tmp + tmp2;
/*     */ 
/*     */           
/* 699 */           if (logP[j] > normalizer) {
/* 700 */             normalizer = logP[j];
/*     */           }
/*     */         } 
/*     */ 
/*     */         
/* 705 */         double sumAllPJ = 0.0D;
/* 706 */         for (int i = 0; i < this.m_MixtureValues.length; i++) {
/* 707 */           double convertingBackValue = Math.exp(logP[i] - normalizer);
/* 708 */           sumAllPJ += convertingBackValue;
/*     */         } 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */         
/* 715 */         double logProb = Math.log(sumAllPJ) + normalizer;
/*     */         
/* 717 */         encodingCostValue -= logProb;
/*     */       } 
/*     */     } 
/* 720 */     return encodingCostValue;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public double getEncodingCostValue() {
/* 726 */     return getEncodingCostValueWithNormalizer();
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\clus\heuristic\EncodingCost.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */