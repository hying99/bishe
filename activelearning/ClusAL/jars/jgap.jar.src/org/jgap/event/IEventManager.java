package org.jgap.event;

import java.io.Serializable;

public interface IEventManager extends Serializable {
  void addEventListener(String paramString, GeneticEventListener paramGeneticEventListener);
  
  void removeEventListener(String paramString, GeneticEventListener paramGeneticEventListener);
  
  void fireGeneticEvent(GeneticEvent paramGeneticEvent);
}


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jars\jgap.jar!\org\jgap\event\IEventManager.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */