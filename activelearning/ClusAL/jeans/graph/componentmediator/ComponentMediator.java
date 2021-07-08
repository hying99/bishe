/*     */ package jeans.graph.componentmediator;
/*     */ 
/*     */ import java.awt.Component;
/*     */ import java.awt.event.ActionEvent;
/*     */ import java.awt.event.ActionListener;
/*     */ import java.util.Enumeration;
/*     */ import java.util.Hashtable;
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
/*     */ public class ComponentMediator
/*     */ {
/*     */   private static ComponentMediator instance;
/*  33 */   private Hashtable groups = new Hashtable<>();
/*  34 */   private Hashtable components = new Hashtable<>();
/*     */   private ComponentInterface componentInterface;
/*     */   private boolean enabledAll = true;
/*  37 */   private ActionListener myListener = new MyActionListener();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static ComponentMediator getInstance() {
/*  44 */     if (instance == null)
/*  45 */       instance = new ComponentMediator(); 
/*  46 */     return instance;
/*     */   }
/*     */   
/*     */   public void resetAll() {
/*  50 */     if (!this.components.isEmpty()) {
/*  51 */       this.groups = new Hashtable<>();
/*  52 */       this.components = new Hashtable<>();
/*     */     } 
/*     */   }
/*     */   
/*     */   public Enumeration getGroupNames() {
/*  57 */     return this.groups.keys();
/*     */   }
/*     */   
/*     */   public GroupWrapper getGroup(String name) {
/*  61 */     return (GroupWrapper)this.groups.get(name);
/*     */   }
/*     */   
/*     */   public ComponentWrapper getWrapper(Component comp) {
/*  65 */     return (ComponentWrapper)this.components.get(comp);
/*     */   }
/*     */   
/*     */   public Enumeration getComponents() {
/*  69 */     return this.components.elements();
/*     */   }
/*     */   
/*     */   public ComponentWrapper addComponent(Component component) {
/*  73 */     ComponentWrapper wrapper = null;
/*  74 */     if (!this.components.containsKey(component)) {
/*  75 */       wrapper = new ComponentWrapper(component);
/*  76 */       this.components.put(component, wrapper);
/*     */     } else {
/*  78 */       wrapper = getWrapper(component);
/*     */     } 
/*  80 */     if (!this.enabledAll) {
/*  81 */       setInGroupEnabled(wrapper, this.enabledAll);
/*     */     }
/*  83 */     return wrapper;
/*     */   }
/*     */   
/*     */   public ComponentWrapper addComponent(String group, ComponentWrapper wrapper) {
/*  87 */     if (!this.groups.containsKey(group)) {
/*  88 */       GroupWrapper newList = new GroupWrapper();
/*  89 */       newList.addElement((E)wrapper);
/*  90 */       this.groups.put(group, newList);
/*     */     } else {
/*  92 */       GroupWrapper list = (GroupWrapper)this.groups.get(group);
/*  93 */       list.addElement((E)wrapper);
/*     */     } 
/*  95 */     return wrapper;
/*     */   }
/*     */   
/*     */   public ComponentWrapper addComponent(String group, Component component) {
/*  99 */     ComponentWrapper wrapper = addComponent(component);
/* 100 */     addComponent(group, wrapper);
/* 101 */     return wrapper;
/*     */   }
/*     */   
/*     */   public void addSpecialComponent(String group, ComponentWrapper wrapper) {
/* 105 */     this.components.put(wrapper, wrapper);
/* 106 */     addComponent(group, wrapper);
/*     */   }
/*     */   
/*     */   public void setEnabled(String group, boolean enable) {
/* 110 */     GroupWrapper mygroup = getGroup(group);
/* 111 */     if (mygroup.shouldSetEnabled(enable)) {
/* 112 */       for (Enumeration<E> e = mygroup.elements(); e.hasMoreElements(); ) {
/* 113 */         ComponentWrapper comp = (ComponentWrapper)e.nextElement();
/* 114 */         setInGroupEnabled(comp, enable);
/*     */       } 
/*     */     }
/*     */   }
/*     */   
/*     */   public void setEnabled(ComponentWrapper comp, boolean enable) {
/* 120 */     if (comp.shouldSetEnabled(enable))
/* 121 */       this.componentInterface.setEnabled(comp, enable); 
/*     */   }
/*     */   
/*     */   private void setInGroupEnabled(ComponentWrapper comp, boolean enable) {
/* 125 */     if (comp.groupShouldSetEnabled(enable))
/* 126 */       this.componentInterface.setEnabled(comp, enable); 
/*     */   }
/*     */   
/*     */   public void onCheckSetEnabled(ComponentWrapper source, String group, boolean invert) {
/* 130 */     if (!source.hasEvents())
/* 131 */       this.componentInterface.addActionListener(source, this.myListener); 
/* 132 */     OnCheckSetEnabled action = new OnCheckSetEnabled(source, group, invert);
/* 133 */     source.addAction(action);
/*     */   }
/*     */   
/*     */   public void setEnabledAll(boolean enable) {
/* 137 */     if (this.enabledAll != enable) {
/* 138 */       this.enabledAll = enable;
/* 139 */       for (Enumeration<ComponentWrapper> e = getComponents(); e.hasMoreElements(); ) {
/* 140 */         ComponentWrapper comp = e.nextElement();
/* 141 */         setInGroupEnabled(comp, enable);
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   public void setCheck(ComponentWrapper comp, boolean check) {
/* 147 */     this.componentInterface.setCheck(comp, check);
/*     */   }
/*     */   
/*     */   public void setCheck(String group, boolean check) {
/* 151 */     for (Enumeration<E> e = getGroup(group).elements(); e.hasMoreElements(); ) {
/* 152 */       ComponentWrapper comp = (ComponentWrapper)e.nextElement();
/* 153 */       setCheck(comp, check);
/*     */     } 
/*     */   }
/*     */   
/*     */   public boolean isCheck(ComponentWrapper comp) {
/* 158 */     return this.componentInterface.isCheck(comp);
/*     */   }
/*     */   
/*     */   public void setComponentInterface(ComponentInterface componentInterface) {
/* 162 */     this.componentInterface = componentInterface;
/*     */   }
/*     */   
/*     */   public void performActions(ComponentWrapper wrapper) {
/* 166 */     for (Enumeration<ComponentAction> e = wrapper.getActions(); e.hasMoreElements(); ) {
/* 167 */       ComponentAction action = e.nextElement();
/* 168 */       action.execute(this);
/*     */     } 
/*     */   }
/*     */   
/*     */   private class MyActionListener
/*     */     implements ActionListener {
/*     */     public void actionPerformed(ActionEvent evt) {
/* 175 */       Component source = (Component)evt.getSource();
/* 176 */       ComponentWrapper wrapper = ComponentMediator.this.getWrapper(source);
/* 177 */       if (wrapper != null) ComponentMediator.this.performActions(wrapper); 
/*     */     }
/*     */     
/*     */     private MyActionListener() {}
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL\!\jeans\graph\componentmediator\ComponentMediator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */