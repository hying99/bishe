package jeans.util.cmdline;

public interface CMDLineArgsProvider {
  String[] getOptionArgs();
  
  int[] getOptionArgArities();
  
  int getNbMainArgs();
  
  void showHelp();
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jean\\util\cmdline\CMDLineArgsProvider.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */