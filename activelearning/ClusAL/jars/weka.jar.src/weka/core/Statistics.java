package weka.core;

public class Statistics {
  protected static final double MACHEP = 1.1102230246251565E-16D;
  
  protected static final double MAXLOG = 709.782712893384D;
  
  protected static final double MINLOG = -745.1332191019412D;
  
  protected static final double MAXGAM = 171.6243769563027D;
  
  protected static final double SQTPI = 2.5066282746310007D;
  
  protected static final double SQRTH = 0.7071067811865476D;
  
  protected static final double LOGPI = 1.1447298858494002D;
  
  protected static final double big = 4.503599627370496E15D;
  
  protected static final double biginv = 2.220446049250313E-16D;
  
  protected static final double[] P0 = new double[] { -59.96335010141079D, 98.00107541859997D, -56.67628574690703D, 13.931260938727968D, -1.2391658386738125D };
  
  protected static final double[] Q0 = new double[] { 1.9544885833814176D, 4.676279128988815D, 86.36024213908905D, -225.46268785411937D, 200.26021238006066D, -82.03722561683334D, 15.90562251262117D, -1.1833162112133D };
  
  protected static final double[] P1 = new double[] { 4.0554489230596245D, 31.525109459989388D, 57.16281922464213D, 44.08050738932008D, 14.684956192885803D, 2.1866330685079025D, -0.1402560791713545D, -0.03504246268278482D, -8.574567851546854E-4D };
  
  protected static final double[] Q1 = new double[] { 15.779988325646675D, 45.39076351288792D, 41.3172038254672D, 15.04253856929075D, 2.504649462083094D, -0.14218292285478779D, -0.03808064076915783D, -9.332594808954574E-4D };
  
  protected static final double[] P2 = new double[] { 3.2377489177694603D, 6.915228890689842D, 3.9388102529247444D, 1.3330346081580755D, 0.20148538954917908D, 0.012371663481782003D, 3.0158155350823543E-4D, 2.6580697468673755E-6D, 6.239745391849833E-9D };
  
  protected static final double[] Q2 = new double[] { 6.02427039364742D, 3.6798356385616087D, 1.3770209948908132D, 0.21623699359449663D, 0.013420400608854318D, 3.2801446468212774E-4D, 2.8924786474538068E-6D, 6.790194080099813E-9D };
  
  public static double binomialStandardError(double paramDouble, int paramInt) {
    return (paramInt == 0) ? 0.0D : Math.sqrt(paramDouble * (1.0D - paramDouble) / paramInt);
  }
  
  public static double chiSquaredProbability(double paramDouble1, double paramDouble2) {
    return (paramDouble1 < 0.0D || paramDouble2 < 1.0D) ? 0.0D : incompleteGammaComplement(paramDouble2 / 2.0D, paramDouble1 / 2.0D);
  }
  
  public static double FProbability(double paramDouble, int paramInt1, int paramInt2) {
    return incompleteBeta(paramInt2 / 2.0D, paramInt1 / 2.0D, paramInt2 / (paramInt2 + paramInt1 * paramDouble));
  }
  
  public static double normalProbability(double paramDouble) {
    double d2;
    double d1 = paramDouble * 0.7071067811865476D;
    double d3 = Math.abs(d1);
    if (d3 < 0.7071067811865476D) {
      d2 = 0.5D + 0.5D * errorFunction(d1);
    } else {
      d2 = 0.5D * errorFunctionComplemented(d3);
      if (d1 > 0.0D)
        d2 = 1.0D - d2; 
    } 
    return d2;
  }
  
  public static double normalInverse(double paramDouble) {
    double d5;
    double d6 = Math.sqrt(6.283185307179586D);
    if (paramDouble <= 0.0D)
      throw new IllegalArgumentException(); 
    if (paramDouble >= 1.0D)
      throw new IllegalArgumentException(); 
    boolean bool = true;
    double d2 = paramDouble;
    if (d2 > 0.8646647167633873D) {
      d2 = 1.0D - d2;
      bool = false;
    } 
    if (d2 > 0.1353352832366127D) {
      d2 -= 0.5D;
      double d8 = d2 * d2;
      double d7 = d2 + d2 * d8 * polevl(d8, P0, 4) / p1evl(d8, Q0, 8);
      d7 *= d6;
      return d7;
    } 
    double d1 = Math.sqrt(-2.0D * Math.log(d2));
    double d4 = d1 - Math.log(d1) / d1;
    double d3 = 1.0D / d1;
    if (d1 < 8.0D) {
      d5 = d3 * polevl(d3, P1, 8) / p1evl(d3, Q1, 8);
    } else {
      d5 = d3 * polevl(d3, P2, 8) / p1evl(d3, Q2, 8);
    } 
    d1 = d4 - d5;
    if (bool)
      d1 = -d1; 
    return d1;
  }
  
  public static double lnGamma(double paramDouble) {
    double[] arrayOfDouble1 = { 8.116141674705085E-4D, -5.950619042843014E-4D, 7.936503404577169E-4D, -0.002777777777300997D, 0.08333333333333319D };
    double[] arrayOfDouble2 = { -1378.2515256912086D, -38801.631513463784D, -331612.9927388712D, -1162370.974927623D, -1721737.0082083966D, -853555.6642457654D };
    double[] arrayOfDouble3 = { -351.81570143652345D, -17064.210665188115D, -220528.59055385445D, -1139334.4436798252D, -2532523.0717758294D, -2018891.4143353277D };
    if (paramDouble < -34.0D) {
      double d4 = -paramDouble;
      double d5 = lnGamma(d4);
      double d3 = Math.floor(d4);
      if (d3 == d4)
        throw new ArithmeticException("lnGamma: Overflow"); 
      null = d4 - d3;
      if (null > 0.5D) {
        d3++;
        null = d3 - d4;
      } 
      null = d4 * Math.sin(Math.PI * null);
      if (null == 0.0D)
        throw new ArithmeticException("lnGamma: Overflow"); 
      return 1.1447298858494002D - Math.log(null) - d5;
    } 
    if (paramDouble < 13.0D) {
      double d4;
      for (d4 = 1.0D; paramDouble >= 3.0D; d4 *= paramDouble)
        paramDouble--; 
      while (paramDouble < 2.0D) {
        if (paramDouble == 0.0D)
          throw new ArithmeticException("lnGamma: Overflow"); 
        d4 /= paramDouble;
        paramDouble++;
      } 
      if (d4 < 0.0D)
        d4 = -d4; 
      if (paramDouble == 2.0D)
        return Math.log(d4); 
      paramDouble -= 2.0D;
      double d3 = paramDouble * polevl(paramDouble, arrayOfDouble2, 5) / p1evl(paramDouble, arrayOfDouble3, 6);
      return Math.log(d4) + d3;
    } 
    if (paramDouble > 2.556348E305D)
      throw new ArithmeticException("lnGamma: Overflow"); 
    double d2 = (paramDouble - 0.5D) * Math.log(paramDouble) - paramDouble + 0.9189385332046728D;
    if (paramDouble > 1.0E8D)
      return d2; 
    double d1 = 1.0D / paramDouble * paramDouble;
    if (paramDouble >= 1000.0D) {
      d2 += ((7.936507936507937E-4D * d1 - 0.002777777777777778D) * d1 + 0.08333333333333333D) / paramDouble;
    } else {
      d2 += polevl(d1, arrayOfDouble1, 4) / paramDouble;
    } 
    return d2;
  }
  
  static double errorFunction(double paramDouble) {
    double[] arrayOfDouble1 = { 9.604973739870516D, 90.02601972038427D, 2232.005345946843D, 7003.325141128051D, 55592.30130103949D };
    double[] arrayOfDouble2 = { 33.56171416475031D, 521.3579497801527D, 4594.323829709801D, 22629.000061389095D, 49267.39426086359D };
    if (Math.abs(paramDouble) > 1.0D)
      return 1.0D - errorFunctionComplemented(paramDouble); 
    double d = paramDouble * paramDouble;
    return paramDouble * polevl(d, arrayOfDouble1, 4) / p1evl(d, arrayOfDouble2, 5);
  }
  
  static double errorFunctionComplemented(double paramDouble) {
    double d1;
    double d4;
    double d5;
    double[] arrayOfDouble1 = { 2.461969814735305E-10D, 0.5641895648310689D, 7.463210564422699D, 48.63719709856814D, 196.5208329560771D, 526.4451949954773D, 934.5285271719576D, 1027.5518868951572D, 557.5353353693994D };
    double[] arrayOfDouble2 = { 13.228195115474499D, 86.70721408859897D, 354.9377788878199D, 975.7085017432055D, 1823.9091668790973D, 2246.3376081871097D, 1656.6630919416134D, 557.5353408177277D };
    double[] arrayOfDouble3 = { 0.5641895835477551D, 1.275366707599781D, 5.019050422511805D, 6.160210979930536D, 7.4097426995044895D, 2.9788666537210022D };
    double[] arrayOfDouble4 = { 2.2605286322011726D, 9.396035249380015D, 12.048953980809666D, 17.08144507475659D, 9.608968090632859D, 3.369076451000815D };
    if (paramDouble < 0.0D) {
      d1 = -paramDouble;
    } else {
      d1 = paramDouble;
    } 
    if (d1 < 1.0D)
      return 1.0D - errorFunction(paramDouble); 
    double d3 = -paramDouble * paramDouble;
    if (d3 < -709.782712893384D)
      return (paramDouble < 0.0D) ? 2.0D : 0.0D; 
    d3 = Math.exp(d3);
    if (d1 < 8.0D) {
      d4 = polevl(d1, arrayOfDouble1, 8);
      d5 = p1evl(d1, arrayOfDouble2, 8);
    } else {
      d4 = polevl(d1, arrayOfDouble3, 5);
      d5 = p1evl(d1, arrayOfDouble4, 6);
    } 
    double d2 = d3 * d4 / d5;
    if (paramDouble < 0.0D)
      d2 = 2.0D - d2; 
    return (d2 == 0.0D) ? ((paramDouble < 0.0D) ? 2.0D : 0.0D) : d2;
  }
  
  static double p1evl(double paramDouble, double[] paramArrayOfdouble, int paramInt) {
    double d = paramDouble + paramArrayOfdouble[0];
    for (byte b = 1; b < paramInt; b++)
      d = d * paramDouble + paramArrayOfdouble[b]; 
    return d;
  }
  
  static double polevl(double paramDouble, double[] paramArrayOfdouble, int paramInt) {
    double d = paramArrayOfdouble[0];
    for (byte b = 1; b <= paramInt; b++)
      d = d * paramDouble + paramArrayOfdouble[b]; 
    return d;
  }
  
  static double incompleteGamma(double paramDouble1, double paramDouble2) {
    if (paramDouble2 <= 0.0D || paramDouble1 <= 0.0D)
      return 0.0D; 
    if (paramDouble2 > 1.0D && paramDouble2 > paramDouble1)
      return 1.0D - incompleteGammaComplement(paramDouble1, paramDouble2); 
    double d2 = paramDouble1 * Math.log(paramDouble2) - paramDouble2 - lnGamma(paramDouble1);
    if (d2 < -709.782712893384D)
      return 0.0D; 
    d2 = Math.exp(d2);
    double d4 = paramDouble1;
    double d3 = 1.0D;
    double d1 = 1.0D;
    while (true) {
      d4++;
      d3 *= paramDouble2 / d4;
      d1 += d3;
      if (d3 / d1 <= 1.1102230246251565E-16D)
        return d1 * d2 / paramDouble1; 
    } 
  }
  
  static double incompleteGammaComplement(double paramDouble1, double paramDouble2) {
    if (paramDouble2 <= 0.0D || paramDouble1 <= 0.0D)
      return 1.0D; 
    if (paramDouble2 < 1.0D || paramDouble2 < paramDouble1)
      return 1.0D - incompleteGamma(paramDouble1, paramDouble2); 
    double d2 = paramDouble1 * Math.log(paramDouble2) - paramDouble2 - lnGamma(paramDouble1);
    if (d2 < -709.782712893384D)
      return 0.0D; 
    d2 = Math.exp(d2);
    double d4 = 1.0D - paramDouble1;
    double d5 = paramDouble2 + d4 + 1.0D;
    double d3 = 0.0D;
    double d7 = 1.0D;
    double d9 = paramDouble2;
    double d6 = paramDouble2 + 1.0D;
    double d8 = d5 * paramDouble2;
    double d1 = d6 / d8;
    while (true) {
      double d11;
      d3++;
      d4++;
      d5 += 2.0D;
      double d10 = d4 * d3;
      double d12 = d6 * d5 - d7 * d10;
      double d13 = d8 * d5 - d9 * d10;
      if (d13 != 0.0D) {
        double d = d12 / d13;
        d11 = Math.abs((d1 - d) / d);
        d1 = d;
      } else {
        d11 = 1.0D;
      } 
      d7 = d6;
      d6 = d12;
      d9 = d8;
      d8 = d13;
      if (Math.abs(d12) > 4.503599627370496E15D) {
        d7 *= 2.220446049250313E-16D;
        d6 *= 2.220446049250313E-16D;
        d9 *= 2.220446049250313E-16D;
        d8 *= 2.220446049250313E-16D;
      } 
      if (d11 <= 1.1102230246251565E-16D)
        return d1 * d2; 
    } 
  }
  
  static double gamma(double paramDouble) {
    double[] arrayOfDouble1 = { 1.6011952247675185E-4D, 0.0011913514700658638D, 0.010421379756176158D, 0.04763678004571372D, 0.20744822764843598D, 0.4942148268014971D, 1.0D };
    double[] arrayOfDouble2 = { -2.3158187332412014E-5D, 5.396055804933034E-4D, -0.004456419138517973D, 0.011813978522206043D, 0.035823639860549865D, -0.23459179571824335D, 0.0714304917030273D, 1.0D };
    double d3 = Math.abs(paramDouble);
    if (d3 > 33.0D) {
      if (paramDouble < 0.0D) {
        double d4 = Math.floor(d3);
        if (d4 == d3)
          throw new ArithmeticException("gamma: overflow"); 
        int i = (int)d4;
        double d5 = d3 - d4;
        if (d5 > 0.5D) {
          d4++;
          d5 = d3 - d4;
        } 
        d5 = d3 * Math.sin(Math.PI * d5);
        if (d5 == 0.0D)
          throw new ArithmeticException("gamma: overflow"); 
        d5 = Math.abs(d5);
        d5 = Math.PI / d5 * stirlingFormula(d3);
        return -d5;
      } 
      return stirlingFormula(paramDouble);
    } 
    double d2;
    for (d2 = 1.0D; paramDouble >= 3.0D; d2 *= paramDouble)
      paramDouble--; 
    while (paramDouble < 0.0D) {
      if (paramDouble == 0.0D)
        throw new ArithmeticException("gamma: singular"); 
      if (paramDouble > -1.0E-9D)
        return d2 / (1.0D + 0.5772156649015329D * paramDouble) * paramDouble; 
      d2 /= paramDouble;
      paramDouble++;
    } 
    while (paramDouble < 2.0D) {
      if (paramDouble == 0.0D)
        throw new ArithmeticException("gamma: singular"); 
      if (paramDouble < 1.0E-9D)
        return d2 / (1.0D + 0.5772156649015329D * paramDouble) * paramDouble; 
      d2 /= paramDouble;
      paramDouble++;
    } 
    if (paramDouble == 2.0D || paramDouble == 3.0D)
      return d2; 
    paramDouble -= 2.0D;
    double d1 = polevl(paramDouble, arrayOfDouble1, 6);
    d3 = polevl(paramDouble, arrayOfDouble2, 7);
    return d2 * d1 / d3;
  }
  
  static double stirlingFormula(double paramDouble) {
    double[] arrayOfDouble = { 7.873113957930937E-4D, -2.2954996161337813E-4D, -0.0026813261780578124D, 0.0034722222160545866D, 0.08333333333334822D };
    double d1 = 143.01608D;
    double d2 = 1.0D / paramDouble;
    null = Math.exp(paramDouble);
    d2 = 1.0D + d2 * polevl(d2, arrayOfDouble, 4);
    if (paramDouble > d1) {
      double d = Math.pow(paramDouble, 0.5D * paramDouble - 0.25D);
      null = d * d / null;
    } else {
      null = Math.pow(paramDouble, paramDouble - 0.5D) / null;
    } 
    return 2.5066282746310007D * null * d2;
  }
  
  public static double incompleteBeta(double paramDouble1, double paramDouble2, double paramDouble3) {
    double d1;
    double d2;
    double d4;
    double d5;
    if (paramDouble1 <= 0.0D || paramDouble2 <= 0.0D)
      throw new ArithmeticException("ibeta: Domain error!"); 
    if (paramDouble3 <= 0.0D || paramDouble3 >= 1.0D) {
      if (paramDouble3 == 0.0D)
        return 0.0D; 
      if (paramDouble3 == 1.0D)
        return 1.0D; 
      throw new ArithmeticException("ibeta: Domain error!");
    } 
    boolean bool = false;
    if (paramDouble2 * paramDouble3 <= 1.0D && paramDouble3 <= 0.95D)
      return powerSeries(paramDouble1, paramDouble2, paramDouble3); 
    double d6 = 1.0D - paramDouble3;
    if (paramDouble3 > paramDouble1 / (paramDouble1 + paramDouble2)) {
      bool = true;
      d1 = paramDouble2;
      d2 = paramDouble1;
      d5 = paramDouble3;
      d4 = d6;
    } else {
      d1 = paramDouble1;
      d2 = paramDouble2;
      d5 = d6;
      d4 = paramDouble3;
    } 
    if (bool && d2 * d4 <= 1.0D && d4 <= 0.95D) {
      double d = powerSeries(d1, d2, d4);
      if (d <= 1.1102230246251565E-16D) {
        d = 0.9999999999999999D;
      } else {
        d = 1.0D - d;
      } 
      return d;
    } 
    double d7 = d4 * (d1 + d2 - 2.0D) - d1 - 1.0D;
    if (d7 < 0.0D) {
      d6 = incompleteBetaFraction1(d1, d2, d4);
    } else {
      d6 = incompleteBetaFraction2(d1, d2, d4) / d5;
    } 
    d7 = d1 * Math.log(d4);
    double d3 = d2 * Math.log(d5);
    if (d1 + d2 < 171.6243769563027D && Math.abs(d7) < 709.782712893384D && Math.abs(d3) < 709.782712893384D) {
      d3 = Math.pow(d5, d2);
      d3 *= Math.pow(d4, d1);
      d3 /= d1;
      d3 *= d6;
      d3 *= gamma(d1 + d2) / gamma(d1) * gamma(d2);
      if (bool)
        if (d3 <= 1.1102230246251565E-16D) {
          d3 = 0.9999999999999999D;
        } else {
          d3 = 1.0D - d3;
        }  
      return d3;
    } 
    d7 += d3 + lnGamma(d1 + d2) - lnGamma(d1) - lnGamma(d2);
    d7 += Math.log(d6 / d1);
    if (d7 < -745.1332191019412D) {
      d3 = 0.0D;
    } else {
      d3 = Math.exp(d7);
    } 
    if (bool)
      if (d3 <= 1.1102230246251565E-16D) {
        d3 = 0.9999999999999999D;
      } else {
        d3 = 1.0D - d3;
      }  
    return d3;
  }
  
  static double incompleteBetaFraction1(double paramDouble1, double paramDouble2, double paramDouble3) {
    double d5 = paramDouble1;
    double d6 = paramDouble1 + paramDouble2;
    double d7 = paramDouble1;
    double d8 = paramDouble1 + 1.0D;
    double d9 = 1.0D;
    double d10 = paramDouble2 - 1.0D;
    double d11 = d8;
    double d12 = paramDouble1 + 2.0D;
    double d2 = 0.0D;
    double d4 = 1.0D;
    double d1 = 1.0D;
    double d3 = 1.0D;
    double d14 = 1.0D;
    double d13 = 1.0D;
    byte b = 0;
    double d15 = 3.3306690738754696E-16D;
    while (true) {
      double d19;
      double d16 = -(paramDouble3 * d5 * d6) / d7 * d8;
      double d17 = d1 + d2 * d16;
      double d18 = d3 + d4 * d16;
      d2 = d1;
      d1 = d17;
      d4 = d3;
      d3 = d18;
      d16 = paramDouble3 * d9 * d10 / d11 * d12;
      d17 = d1 + d2 * d16;
      d18 = d3 + d4 * d16;
      d2 = d1;
      d1 = d17;
      d4 = d3;
      d3 = d18;
      if (d18 != 0.0D)
        d13 = d17 / d18; 
      if (d13 != 0.0D) {
        d19 = Math.abs((d14 - d13) / d13);
        d14 = d13;
      } else {
        d19 = 1.0D;
      } 
      if (d19 < d15)
        return d14; 
      d5++;
      d6++;
      d7 += 2.0D;
      d8 += 2.0D;
      d9++;
      d10--;
      d11 += 2.0D;
      d12 += 2.0D;
      if (Math.abs(d18) + Math.abs(d17) > 4.503599627370496E15D) {
        d2 *= 2.220446049250313E-16D;
        d1 *= 2.220446049250313E-16D;
        d4 *= 2.220446049250313E-16D;
        d3 *= 2.220446049250313E-16D;
      } 
      if (Math.abs(d18) < 2.220446049250313E-16D || Math.abs(d17) < 2.220446049250313E-16D) {
        d2 *= 4.503599627370496E15D;
        d1 *= 4.503599627370496E15D;
        d4 *= 4.503599627370496E15D;
        d3 *= 4.503599627370496E15D;
      } 
      if (++b >= 'Ĭ')
        return d14; 
    } 
  }
  
  static double incompleteBetaFraction2(double paramDouble1, double paramDouble2, double paramDouble3) {
    double d5 = paramDouble1;
    double d6 = paramDouble2 - 1.0D;
    double d7 = paramDouble1;
    double d8 = paramDouble1 + 1.0D;
    double d9 = 1.0D;
    double d10 = paramDouble1 + paramDouble2;
    double d11 = paramDouble1 + 1.0D;
    double d12 = paramDouble1 + 2.0D;
    double d2 = 0.0D;
    double d4 = 1.0D;
    double d1 = 1.0D;
    double d3 = 1.0D;
    double d15 = paramDouble3 / (1.0D - paramDouble3);
    double d14 = 1.0D;
    double d13 = 1.0D;
    byte b = 0;
    double d16 = 3.3306690738754696E-16D;
    while (true) {
      double d20;
      double d17 = -(d15 * d5 * d6) / d7 * d8;
      double d18 = d1 + d2 * d17;
      double d19 = d3 + d4 * d17;
      d2 = d1;
      d1 = d18;
      d4 = d3;
      d3 = d19;
      d17 = d15 * d9 * d10 / d11 * d12;
      d18 = d1 + d2 * d17;
      d19 = d3 + d4 * d17;
      d2 = d1;
      d1 = d18;
      d4 = d3;
      d3 = d19;
      if (d19 != 0.0D)
        d13 = d18 / d19; 
      if (d13 != 0.0D) {
        d20 = Math.abs((d14 - d13) / d13);
        d14 = d13;
      } else {
        d20 = 1.0D;
      } 
      if (d20 < d16)
        return d14; 
      d5++;
      d6--;
      d7 += 2.0D;
      d8 += 2.0D;
      d9++;
      d10++;
      d11 += 2.0D;
      d12 += 2.0D;
      if (Math.abs(d19) + Math.abs(d18) > 4.503599627370496E15D) {
        d2 *= 2.220446049250313E-16D;
        d1 *= 2.220446049250313E-16D;
        d4 *= 2.220446049250313E-16D;
        d3 *= 2.220446049250313E-16D;
      } 
      if (Math.abs(d19) < 2.220446049250313E-16D || Math.abs(d18) < 2.220446049250313E-16D) {
        d2 *= 4.503599627370496E15D;
        d1 *= 4.503599627370496E15D;
        d4 *= 4.503599627370496E15D;
        d3 *= 4.503599627370496E15D;
      } 
      if (++b >= 'Ĭ')
        return d14; 
    } 
  }
  
  static double powerSeries(double paramDouble1, double paramDouble2, double paramDouble3) {
    double d8 = 1.0D / paramDouble1;
    double d3 = (1.0D - paramDouble2) * paramDouble3;
    double d4 = d3 / (paramDouble1 + 1.0D);
    double d6 = d4;
    double d2 = d3;
    double d5 = 2.0D;
    double d1 = 0.0D;
    double d7 = 1.1102230246251565E-16D * d8;
    while (Math.abs(d4) > d7) {
      d3 = (d5 - paramDouble2) * paramDouble3 / d5;
      d2 *= d3;
      d4 = d2 / (paramDouble1 + d5);
      d1 += d4;
      d5++;
    } 
    d1 += d6;
    d1 += d8;
    d3 = paramDouble1 * Math.log(paramDouble3);
    if (paramDouble1 + paramDouble2 < 171.6243769563027D && Math.abs(d3) < 709.782712893384D) {
      d2 = gamma(paramDouble1 + paramDouble2) / gamma(paramDouble1) * gamma(paramDouble2);
      d1 = d1 * d2 * Math.pow(paramDouble3, paramDouble1);
    } else {
      d2 = lnGamma(paramDouble1 + paramDouble2) - lnGamma(paramDouble1) - lnGamma(paramDouble2) + d3 + Math.log(d1);
      if (d2 < -745.1332191019412D) {
        d1 = 0.0D;
      } else {
        d1 = Math.exp(d2);
      } 
    } 
    return d1;
  }
  
  public static void main(String[] paramArrayOfString) {
    System.out.println("Binomial standard error (0.5, 100): " + binomialStandardError(0.5D, 100));
    System.out.println("Chi-squared probability (2.558, 10): " + chiSquaredProbability(2.558D, 10.0D));
    System.out.println("Normal probability (0.2): " + normalProbability(0.2D));
    System.out.println("F probability (5.1922, 4, 5): " + FProbability(5.1922D, 4, 5));
    System.out.println("lnGamma(6): " + lnGamma(6.0D));
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\core\Statistics.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */