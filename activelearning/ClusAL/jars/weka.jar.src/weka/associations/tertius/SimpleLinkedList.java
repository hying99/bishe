package weka.associations.tertius;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Comparator;
import java.util.NoSuchElementException;

public class SimpleLinkedList implements Serializable {
  private Entry first = new Entry(null, null, null);
  
  private Entry last = new Entry(null, null, null);
  
  public SimpleLinkedList() {
    this.first.next = this.last;
    this.last.previous = this.first;
  }
  
  public Object removeFirst() {
    if (this.first.next == this.last)
      throw new NoSuchElementException(); 
    Object object = this.first.next.element;
    this.first.next.next.previous = this.first;
    this.first.next = this.first.next.next;
    return object;
  }
  
  public Object getFirst() {
    if (this.first.next == this.last)
      throw new NoSuchElementException(); 
    return this.first.next.element;
  }
  
  public Object getLast() {
    if (this.last.previous == this.first)
      throw new NoSuchElementException(); 
    return this.last.previous.element;
  }
  
  public void addFirst(Object paramObject) {
    Entry entry = new Entry(paramObject, this.first.next, this.first);
    this.first.next.previous = entry;
    this.first.next = entry;
  }
  
  public void add(Object paramObject) {
    Entry entry = new Entry(paramObject, this.last, this.last.previous);
    this.last.previous.next = entry;
    this.last.previous = entry;
  }
  
  public void addAll(SimpleLinkedList paramSimpleLinkedList) {
    this.last.previous.next = paramSimpleLinkedList.first.next;
    paramSimpleLinkedList.first.next.previous = this.last.previous;
    this.last = paramSimpleLinkedList.last;
  }
  
  public void clear() {
    this.first.next = this.last;
    this.last.previous = this.first;
  }
  
  public boolean isEmpty() {
    return (this.first.next == this.last);
  }
  
  public LinkedListIterator iterator() {
    return new LinkedListIterator(this);
  }
  
  public LinkedListInverseIterator inverseIterator() {
    return new LinkedListInverseIterator(this);
  }
  
  public int size() {
    byte b = 0;
    LinkedListIterator linkedListIterator = new LinkedListIterator(this);
    while (linkedListIterator.hasNext()) {
      b++;
      linkedListIterator.next();
    } 
    return b;
  }
  
  public void merge(SimpleLinkedList paramSimpleLinkedList, Comparator paramComparator) {
    LinkedListIterator linkedListIterator1 = iterator();
    LinkedListIterator linkedListIterator2 = paramSimpleLinkedList.iterator();
    Object object1 = linkedListIterator1.next();
    Object object2 = linkedListIterator2.next();
    while (object2 != null) {
      if (object1 == null || paramComparator.compare(object2, object1) < 0) {
        linkedListIterator1.addBefore(object2);
        object2 = linkedListIterator2.next();
        continue;
      } 
      object1 = linkedListIterator1.next();
    } 
  }
  
  public void sort(Comparator paramComparator) {
    LinkedListIterator linkedListIterator = iterator();
    if (linkedListIterator.hasNext()) {
      SimpleLinkedList simpleLinkedList1 = new SimpleLinkedList();
      SimpleLinkedList simpleLinkedList2 = new SimpleLinkedList();
      Object object = linkedListIterator.next();
      while (linkedListIterator.hasNext()) {
        Object object1 = linkedListIterator.next();
        if (paramComparator.compare(object1, object) < 0) {
          simpleLinkedList1.add(object1);
          continue;
        } 
        simpleLinkedList2.add(object1);
      } 
      simpleLinkedList1.sort(paramComparator);
      simpleLinkedList2.sort(paramComparator);
      clear();
      addAll(simpleLinkedList1);
      add(object);
      addAll(simpleLinkedList2);
    } 
  }
  
  public String toString() {
    StringBuffer stringBuffer = new StringBuffer();
    LinkedListIterator linkedListIterator = iterator();
    stringBuffer.append("[");
    while (linkedListIterator.hasNext()) {
      stringBuffer.append(String.valueOf(linkedListIterator.next()));
      if (linkedListIterator.hasNext())
        stringBuffer.append(", "); 
    } 
    stringBuffer.append("]");
    return stringBuffer.toString();
  }
  
  private synchronized void writeObject(ObjectOutputStream paramObjectOutputStream) throws IOException {
    paramObjectOutputStream.defaultWriteObject();
    paramObjectOutputStream.writeInt(size());
    for (Entry entry = this.first.next; entry != this.last; entry = entry.next)
      paramObjectOutputStream.writeObject(entry.element); 
  }
  
  private synchronized void readObject(ObjectInputStream paramObjectInputStream) throws IOException, ClassNotFoundException {
    paramObjectInputStream.defaultReadObject();
    int i = paramObjectInputStream.readInt();
    this.first = new Entry(null, null, null);
    this.last = new Entry(null, null, null);
    this.first.next = this.last;
    this.last.previous = this.first;
    for (byte b = 0; b < i; b++)
      add(paramObjectInputStream.readObject()); 
  }
  
  private static class Entry implements Serializable {
    Object element;
    
    Entry next;
    
    Entry previous;
    
    Entry(Object param1Object, Entry param1Entry1, Entry param1Entry2) {
      this.element = param1Object;
      this.next = param1Entry1;
      this.previous = param1Entry2;
    }
  }
  
  public class LinkedListInverseIterator implements Serializable {
    SimpleLinkedList.Entry current;
    
    SimpleLinkedList.Entry lastReturned;
    
    private final SimpleLinkedList this$0;
    
    public LinkedListInverseIterator(SimpleLinkedList this$0) {
      this.this$0 = this$0;
      this.current = this.this$0.last;
      this.lastReturned = null;
    }
    
    public boolean hasPrevious() {
      return (this.current.previous != this.this$0.first);
    }
    
    public Object previous() {
      if (this.current == this.this$0.first)
        throw new NoSuchElementException(); 
      this.current = this.current.previous;
      this.lastReturned = this.current;
      return this.current.element;
    }
    
    public void remove() {
      if (this.lastReturned == this.this$0.first || this.lastReturned == null)
        throw new IllegalStateException(); 
      this.lastReturned.previous.next = this.lastReturned.next;
      this.lastReturned.next.previous = this.lastReturned.previous;
      this.current = this.lastReturned.next;
      this.lastReturned = null;
    }
  }
  
  public class LinkedListIterator implements Serializable {
    SimpleLinkedList.Entry current;
    
    SimpleLinkedList.Entry lastReturned;
    
    private final SimpleLinkedList this$0;
    
    public LinkedListIterator(SimpleLinkedList this$0) {
      this.this$0 = this$0;
      this.current = this.this$0.first;
      this.lastReturned = null;
    }
    
    public boolean hasNext() {
      return (this.current.next != this.this$0.last);
    }
    
    public Object next() {
      if (this.current == this.this$0.last)
        throw new NoSuchElementException(); 
      this.current = this.current.next;
      this.lastReturned = this.current;
      return this.current.element;
    }
    
    public void remove() {
      if (this.lastReturned == this.this$0.last || this.lastReturned == null)
        throw new IllegalStateException(); 
      this.lastReturned.previous.next = this.lastReturned.next;
      this.lastReturned.next.previous = this.lastReturned.previous;
      this.current = this.lastReturned.previous;
      this.lastReturned = null;
    }
    
    public void addBefore(Object param1Object) {
      if (this.lastReturned == null)
        throw new IllegalStateException(); 
      SimpleLinkedList.Entry entry = new SimpleLinkedList.Entry(param1Object, this.lastReturned, this.lastReturned.previous);
      this.lastReturned.previous.next = entry;
      this.lastReturned.previous = entry;
    }
  }
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\weka.jar!\weka\associations\tertius\SimpleLinkedList.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */