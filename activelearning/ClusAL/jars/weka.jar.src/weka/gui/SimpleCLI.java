package weka.gui;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.TextArea;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.PrintStream;
import java.io.Reader;
import java.lang.reflect.Method;
import java.util.StringTokenizer;
import java.util.Vector;

public class SimpleCLI extends Frame implements ActionListener {
  protected TextArea m_OutputArea = new TextArea();
  
  protected TextField m_Input = new TextField();
  
  protected Vector m_CommandHistory = new Vector();
  
  protected int m_HistoryPos = 0;
  
  protected PipedOutputStream m_POO = new PipedOutputStream();
  
  protected PipedOutputStream m_POE = new PipedOutputStream();
  
  protected Thread m_OutRedirector;
  
  protected Thread m_ErrRedirector;
  
  protected Thread m_RunThread;
  
  static Class array$Ljava$lang$String;
  
  public SimpleCLI() throws Exception {
    setLayout(new BorderLayout());
    add(this.m_OutputArea, "Center");
    add(this.m_Input, "South");
    this.m_Input.addActionListener(this);
    this.m_Input.addKeyListener(new KeyAdapter(this) {
          private final SimpleCLI this$0;
          
          public void keyPressed(KeyEvent param1KeyEvent) {
            this.this$0.doHistory(param1KeyEvent);
          }
        });
    this.m_OutputArea.setEditable(false);
    this.m_OutputArea.setFont(new Font("Monospaced", 0, 12));
    PipedInputStream pipedInputStream1 = new PipedInputStream(this.m_POO);
    System.setOut(new PrintStream(this.m_POO));
    InputStreamReader inputStreamReader = new InputStreamReader(pipedInputStream1);
    this.m_OutRedirector = new ReaderToTextArea(this, inputStreamReader, this.m_OutputArea);
    this.m_OutRedirector.start();
    PipedInputStream pipedInputStream2 = new PipedInputStream(this.m_POE);
    System.setErr(new PrintStream(this.m_POE));
    inputStreamReader = new InputStreamReader(pipedInputStream2);
    this.m_ErrRedirector = new ReaderToTextArea(this, inputStreamReader, this.m_OutputArea);
    this.m_ErrRedirector.start();
    pack();
    setSize(600, 500);
    System.out.println("\nWelcome to the WEKA SimpleCLI\n\nEnter commands in the textfield at the bottom of \nthe window. Use the up and down arrows to move \nthrough previous commands.\n\n");
    runCommand("help");
  }
  
  public void runCommand(String paramString) throws Exception {
    System.out.println("> " + paramString + '\n');
    System.out.flush();
    String[] arrayOfString = splitOptions(paramString);
    if (arrayOfString.length == 0)
      return; 
    if (arrayOfString[0].equals("java")) {
      arrayOfString[0] = "";
      try {
        if (arrayOfString.length == 1)
          throw new Exception("No class name given"); 
        String str = arrayOfString[1];
        arrayOfString[1] = "";
        if (this.m_RunThread != null)
          throw new Exception("An object is already running, use \"break\" to interrupt it."); 
        Class clazz = Class.forName(str);
        this.m_RunThread = new ClassRunner(this, clazz, arrayOfString);
        this.m_RunThread.setPriority(1);
        this.m_RunThread.start();
      } catch (Exception exception) {
        System.err.println(exception.getMessage());
      } 
    } else if (arrayOfString[0].equals("cls")) {
      this.m_OutputArea.setText("");
    } else if (arrayOfString[0].equals("break")) {
      if (this.m_RunThread == null) {
        System.err.println("Nothing is currently running.");
      } else {
        System.out.println("[Interrupt...]");
        this.m_RunThread.interrupt();
      } 
    } else if (arrayOfString[0].equals("kill")) {
      if (this.m_RunThread == null) {
        System.err.println("Nothing is currently running.");
      } else {
        System.out.println("[Kill...]");
        this.m_RunThread.stop();
        this.m_RunThread = null;
      } 
    } else if (arrayOfString[0].equals("exit")) {
      processEvent(new WindowEvent(this, 201));
    } else {
      boolean bool = (arrayOfString.length > 1 && arrayOfString[0].equals("help")) ? true : false;
      if (bool && arrayOfString[1].equals("java")) {
        System.err.println("java <classname> <args>\n\nStarts the main method of <classname> with the supplied command line arguments (if any).\nThe command is started in a separate thread, and may be interrupted with the \"break\"\ncommand (friendly), or killed with the \"kill\" command (unfriendly).\n");
      } else if (bool && arrayOfString[1].equals("break")) {
        System.err.println("break\n\nAttempts to nicely interrupt the running job, if any. If this doesn't respond in an\nacceptable time, use \"kill\".\n");
      } else if (bool && arrayOfString[1].equals("kill")) {
        System.err.println("kill\n\nKills the running job, if any. You should only use this if the job doesn't respond to\n\"break\".\n");
      } else if (bool && arrayOfString[1].equals("cls")) {
        System.err.println("cls\n\nClears the output area.\n");
      } else if (bool && arrayOfString[1].equals("exit")) {
        System.err.println("exit\n\nExits the SimpleCLI program.\n");
      } else {
        System.err.println("Command must be one of:\n\tjava <classname> <args>\n\tbreak\n\tkill\n\tcls\n\texit\n\thelp <command>\n");
      } 
    } 
  }
  
  protected static String[] splitOptions(String paramString) {
    Vector vector = new Vector();
    StringTokenizer stringTokenizer = new StringTokenizer(paramString);
    while (stringTokenizer.hasMoreTokens())
      vector.addElement(stringTokenizer.nextToken()); 
    String[] arrayOfString = new String[vector.size()];
    for (byte b = 0; b < vector.size(); b++)
      arrayOfString[b] = vector.elementAt(b); 
    return arrayOfString;
  }
  
  public void doHistory(KeyEvent paramKeyEvent) {
    if (paramKeyEvent.getSource() == this.m_Input)
      switch (paramKeyEvent.getKeyCode()) {
        case 38:
          if (this.m_HistoryPos > 0) {
            this.m_HistoryPos--;
            String str = this.m_CommandHistory.elementAt(this.m_HistoryPos);
            this.m_Input.setText(str);
          } 
          break;
        case 40:
          if (this.m_HistoryPos < this.m_CommandHistory.size()) {
            this.m_HistoryPos++;
            String str = "";
            if (this.m_HistoryPos < this.m_CommandHistory.size())
              str = this.m_CommandHistory.elementAt(this.m_HistoryPos); 
            this.m_Input.setText(str);
          } 
          break;
      }  
  }
  
  public void actionPerformed(ActionEvent paramActionEvent) {
    try {
      if (paramActionEvent.getSource() == this.m_Input) {
        String str = this.m_Input.getText();
        int i = this.m_CommandHistory.size() - 1;
        if (i < 0 || !str.equals(this.m_CommandHistory.elementAt(i))) {
          this.m_CommandHistory.addElement(str);
          this.m_HistoryPos = this.m_CommandHistory.size();
        } 
        runCommand(str);
        this.m_Input.setText("");
      } 
    } catch (Exception exception) {
      System.err.println(exception.getMessage());
    } 
  }
  
  public static void main(String[] paramArrayOfString) {
    try {
      SimpleCLI simpleCLI = new SimpleCLI();
      simpleCLI.addWindowListener(new WindowAdapter(simpleCLI) {
            private final SimpleCLI val$frame;
            
            public void windowClosing(WindowEvent param1WindowEvent) {
              System.err.println("window closed");
              this.val$frame.dispose();
            }
          });
      simpleCLI.setVisible(true);
    } catch (Exception exception) {
      System.out.println(exception.getMessage());
      System.exit(0);
    } 
  }
  
  static Class class$(String paramString) {
    try {
      return Class.forName(paramString);
    } catch (ClassNotFoundException classNotFoundException) {
      throw new NoClassDefFoundError(classNotFoundException.getMessage());
    } 
  }
  
  class ClassRunner extends Thread {
    protected Method m_MainMethod;
    
    String[] m_CommandArgs;
    
    private final SimpleCLI this$0;
    
    public ClassRunner(SimpleCLI this$0, Class param1Class, String[] param1ArrayOfString) throws Exception {
      this.this$0 = this$0;
      setDaemon(true);
      Class[] arrayOfClass = { (SimpleCLI.array$Ljava$lang$String == null) ? (SimpleCLI.array$Ljava$lang$String = SimpleCLI.class$("[Ljava.lang.String;")) : SimpleCLI.array$Ljava$lang$String };
      this.m_CommandArgs = param1ArrayOfString;
      this.m_MainMethod = param1Class.getMethod("main", arrayOfClass);
      if ((this.m_MainMethod.getModifiers() & 0x8) == 0 || (this.m_MainMethod.getModifiers() & 0x1) == 0)
        throw new NoSuchMethodException("main(String[]) method of " + param1Class.getName() + " is not public and static."); 
    }
    
    public void run() {
      try {
        Object[] arrayOfObject = { this.m_CommandArgs };
        this.m_MainMethod.invoke(null, arrayOfObject);
        if (isInterrupted())
          System.err.println("[...Interrupted]"); 
      } catch (Exception exception) {
        if (exception.getMessage() == null) {
          System.err.println("[...Killed]");
        } else {
          System.err.println("[Run exception] " + exception.getMessage());
        } 
      } finally {
        this.this$0.m_RunThread = null;
      } 
    }
  }
  
  class ReaderToTextArea extends Thread {
    protected LineNumberReader m_Input;
    
    protected TextArea m_Output;
    
    private final SimpleCLI this$0;
    
    public ReaderToTextArea(SimpleCLI this$0, Reader param1Reader, TextArea param1TextArea) {
      this.this$0 = this$0;
      setDaemon(true);
      this.m_Input = new LineNumberReader(param1Reader);
      this.m_Output = param1TextArea;
    }
    
    public void run() {
      while (true) {
        try {
          while (true)
            this.m_Output.append(this.m_Input.readLine() + '\n'); 
          break;
        } catch (Exception exception) {
          try {
            sleep(100L);
          } catch (Exception exception1) {}
        } 
      } 
    }
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\gui\SimpleCLI.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */