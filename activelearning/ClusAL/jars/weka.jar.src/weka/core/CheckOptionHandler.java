package weka.core;

import java.util.Enumeration;

public class CheckOptionHandler {
  public static String printOptions(String[] paramArrayOfString) {
    return (paramArrayOfString == null) ? "<null>" : Utils.joinOptions(paramArrayOfString);
  }
  
  public static void compareOptions(String[] paramArrayOfString1, String[] paramArrayOfString2) throws Exception {
    if (paramArrayOfString1 == null)
      throw new Exception("first set of options is null!"); 
    if (paramArrayOfString2 == null)
      throw new Exception("second set of options is null!"); 
    if (paramArrayOfString1.length != paramArrayOfString2.length)
      throw new Exception("problem found!\nFirst set: " + printOptions(paramArrayOfString1) + '\n' + "Second set: " + printOptions(paramArrayOfString2) + '\n' + "options differ in length"); 
    for (byte b = 0; b < paramArrayOfString1.length; b++) {
      if (!paramArrayOfString1[b].equals(paramArrayOfString2[b]))
        throw new Exception("problem found!\n\tFirst set: " + printOptions(paramArrayOfString1) + '\n' + "\tSecond set: " + printOptions(paramArrayOfString2) + '\n' + '\t' + paramArrayOfString1[b] + " != " + paramArrayOfString2[b]); 
    } 
  }
  
  public static void checkOptionHandler(OptionHandler paramOptionHandler, String[] paramArrayOfString) throws Exception {
    System.out.println("OptionHandler: " + paramOptionHandler.getClass().getName());
    System.out.println("ListOptions:");
    Enumeration enumeration = paramOptionHandler.listOptions();
    while (enumeration.hasMoreElements()) {
      Option option = enumeration.nextElement();
      System.out.println(option.synopsis());
      System.out.println(option.description());
    } 
    String[] arrayOfString1 = paramOptionHandler.getOptions();
    System.out.print("Default options:");
    System.out.println(printOptions(arrayOfString1));
    System.out.print("User options:");
    System.out.println(printOptions(paramArrayOfString));
    System.out.println("Setting user options...");
    paramOptionHandler.setOptions(paramArrayOfString);
    System.out.print("Remaining options:");
    System.out.println(printOptions(paramArrayOfString));
    System.out.print("Getting canonical user options:");
    String[] arrayOfString2 = paramOptionHandler.getOptions();
    System.out.println(printOptions(arrayOfString2));
    System.out.println("Setting canonical user options...");
    paramOptionHandler.setOptions((String[])arrayOfString2.clone());
    System.out.print("Checking canonical user options...");
    String[] arrayOfString3 = paramOptionHandler.getOptions();
    compareOptions(arrayOfString2, arrayOfString3);
    System.out.println("OK");
    System.out.println("Resetting to default options...");
    paramOptionHandler.setOptions((String[])arrayOfString1.clone());
    System.out.print("Checking default options match previous default...");
    String[] arrayOfString4 = paramOptionHandler.getOptions();
    compareOptions(arrayOfString1, arrayOfString4);
    System.out.println("OK");
  }
  
  public static void main(String[] paramArrayOfString) {
    try {
      OptionHandler optionHandler;
      String str = Utils.getOption('W', paramArrayOfString);
      if (str.length() == 0)
        throw new Exception("Please give a class name with -W option"); 
      try {
        optionHandler = (OptionHandler)Class.forName(str).newInstance();
      } catch (Exception exception) {
        throw new Exception("Couldn't find OptionHandler with name " + str);
      } 
      String[] arrayOfString = Utils.partitionOptions(paramArrayOfString);
      Utils.checkForRemainingOptions(paramArrayOfString);
      checkOptionHandler(optionHandler, arrayOfString);
    } catch (Exception exception) {
      System.err.println(exception.getMessage());
    } 
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\core\CheckOptionHandler.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */