package weka.core;

import java.io.Serializable;

public class Queue implements Serializable {
  protected QueueNode m_Head = null;
  
  protected QueueNode m_Tail = null;
  
  protected int m_Size = 0;
  
  public final synchronized void removeAllElements() {
    this.m_Size = 0;
    this.m_Head = null;
    this.m_Tail = null;
  }
  
  public synchronized Object push(Object paramObject) {
    QueueNode queueNode = new QueueNode(this, paramObject);
    if (this.m_Head == null) {
      this.m_Head = this.m_Tail = queueNode;
    } else {
      this.m_Tail = this.m_Tail.next(queueNode);
    } 
    this.m_Size++;
    return paramObject;
  }
  
  public synchronized Object pop() throws RuntimeException {
    if (this.m_Head == null)
      throw new RuntimeException("Queue is empty"); 
    Object object = this.m_Head.contents();
    this.m_Size--;
    this.m_Head = this.m_Head.next();
    if (this.m_Head == null)
      this.m_Tail = null; 
    return object;
  }
  
  public synchronized Object peek() throws RuntimeException {
    if (this.m_Head == null)
      throw new RuntimeException("Queue is empty"); 
    return this.m_Head.contents();
  }
  
  public boolean empty() {
    return (this.m_Head == null);
  }
  
  public int size() {
    return this.m_Size;
  }
  
  public String toString() {
    String str = "Queue Contents " + this.m_Size + " elements\n";
    QueueNode queueNode = this.m_Head;
    if (queueNode == null)
      return str + "Empty\n"; 
    while (queueNode != null) {
      str = str + queueNode.contents().toString() + "\n";
      queueNode = queueNode.next();
    } 
    return str;
  }
  
  public static void main(String[] paramArrayOfString) {
    try {
      Queue queue = new Queue();
      for (byte b = 0; b < paramArrayOfString.length; b++)
        queue.push(paramArrayOfString[b]); 
      System.out.println("After pushing command line arguments");
      System.out.println(queue.toString());
      while (!queue.empty())
        System.out.println("Pop: " + queue.pop().toString()); 
      try {
        queue.pop();
        System.out.println("ERROR: pop did not throw exception!");
      } catch (RuntimeException runtimeException) {
        System.out.println("Pop on empty queue correctly gave exception.");
      } 
    } catch (Exception exception) {
      System.out.println(exception.getMessage());
    } 
  }
  
  protected class QueueNode implements Serializable {
    protected QueueNode m_Next;
    
    protected Object m_Contents;
    
    private final Queue this$0;
    
    public QueueNode(Queue this$0, Object param1Object) {
      this.this$0 = this$0;
      this.m_Contents = param1Object;
      next(null);
    }
    
    public QueueNode next(QueueNode param1QueueNode) {
      return this.m_Next = param1QueueNode;
    }
    
    public QueueNode next() {
      return this.m_Next;
    }
    
    public Object contents(Object param1Object) {
      return this.m_Contents = param1Object;
    }
    
    public Object contents() {
      return this.m_Contents;
    }
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\core\Queue.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */