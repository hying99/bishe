/*     */ package jeans.graph.swing;
/*     */ 
/*     */ import java.awt.Dimension;
/*     */ import java.awt.LayoutManager;
/*     */ import java.awt.event.ActionEvent;
/*     */ import java.awt.event.ActionListener;
/*     */ import java.awt.event.FocusAdapter;
/*     */ import java.awt.event.FocusEvent;
/*     */ import java.awt.event.KeyAdapter;
/*     */ import java.awt.event.KeyEvent;
/*     */ import java.awt.event.MouseAdapter;
/*     */ import java.awt.event.MouseEvent;
/*     */ import java.awt.event.WindowListener;
/*     */ import java.util.Hashtable;
/*     */ import javax.swing.AbstractListModel;
/*     */ import javax.swing.BorderFactory;
/*     */ import javax.swing.ImageIcon;
/*     */ import javax.swing.JButton;
/*     */ import javax.swing.JDialog;
/*     */ import javax.swing.JFrame;
/*     */ import javax.swing.JLabel;
/*     */ import javax.swing.JList;
/*     */ import javax.swing.JPanel;
/*     */ import javax.swing.JScrollPane;
/*     */ import javax.swing.JTable;
/*     */ import javax.swing.JTextField;
/*     */ import javax.swing.ListModel;
/*     */ import javax.swing.ListSelectionModel;
/*     */ import javax.swing.border.Border;
/*     */ import javax.swing.event.ListSelectionEvent;
/*     */ import javax.swing.event.ListSelectionListener;
/*     */ import javax.swing.event.TableModelEvent;
/*     */ import javax.swing.event.TableModelListener;
/*     */ import javax.swing.table.AbstractTableModel;
/*     */ import javax.swing.table.TableColumn;
/*     */ import javax.swing.table.TableColumnModel;
/*     */ import javax.swing.table.TableModel;
/*     */ import jeans.graph.PercentLayout;
/*     */ import jeans.graph.WindowClosingListener;
/*     */ import jeans.io.filesys.MFileEntry;
/*     */ import jeans.io.filesys.MFileSystem;
/*     */ import jeans.resource.MediaCache;
/*     */ import jeans.util.thread.MCallback;
/*     */ 
/*     */ public class MFileDialog
/*     */   extends JDialog
/*     */   implements MCallback
/*     */ {
/*     */   public static final long serialVersionUID = 1L;
/*     */   protected JPanel m_hPanel;
/*     */   protected JLabel m_hTitleB;
/*     */   protected JButton m_hOkButton;
/*  53 */   protected Border m_hEtchedBorder = BorderFactory.createEtchedBorder(); protected JButton m_hCancelButton; protected JTextField m_hFile; protected MyList m_hDirList; protected MyTable m_hFileTable; protected JScrollPane m_hDirScroll;
/*     */   protected JScrollPane m_hFileScroll;
/*     */   protected WindowClosingListener m_hWindowListener;
/*     */   protected MFileSystem m_hFileSystem;
/*     */   protected MyTableModel m_hTableModel;
/*     */   protected MyListModel m_hListModel;
/*     */   protected MyTableSorter m_hSorter;
/*     */   protected String m_EntrySequence;
/*  61 */   protected Hashtable m_hIcons = new Hashtable<>();
/*     */   protected ImageIcon m_hDefaultIcon;
/*     */   protected ActionListener m_hOnOK;
/*     */   
/*     */   public MFileDialog(JFrame parent, String titleA, String titleB, String titleOK) {
/*  66 */     super(parent, titleA, true);
/*  67 */     setContentPane(this.m_hPanel = createPanel(titleB, titleOK));
/*  68 */     pack();
/*     */   }
/*     */   
/*     */   public MFileDialog(JFrame parent) {
/*  72 */     this(parent, "", "", "");
/*     */   }
/*     */   
/*     */   public void setOKListener(ActionListener listener) {
/*  76 */     this.m_hOnOK = listener;
/*     */   }
/*     */   
/*     */   public String getFileName() {
/*  80 */     return this.m_hFile.getText().trim();
/*     */   }
/*     */   
/*     */   public MFileEntry getFileEntry() {
/*  84 */     String name = getFileName();
/*  85 */     return this.m_hFileSystem.getEntry(name);
/*     */   }
/*     */   
/*     */   public void addWindowClosingListener(WindowClosingListener listener) {
/*  89 */     this.m_hWindowListener = listener;
/*  90 */     addWindowListener((WindowListener)listener);
/*  91 */     this.m_hCancelButton.addActionListener((ActionListener)listener);
/*     */   }
/*     */   
/*     */   public void setStrings(String titleA, String titleB, String titleOK) {
/*  95 */     setTitle(titleA);
/*  96 */     this.m_hTitleB.setText(titleB);
/*  97 */     this.m_hOkButton.setText(titleOK);
/*     */   }
/*     */   
/*     */   public void addIcon(String ex, ImageIcon im) {
/* 101 */     this.m_hIcons.put(ex, im);
/*     */   }
/*     */   
/*     */   public void setDefaultIcon(ImageIcon icon) {
/* 105 */     this.m_hDefaultIcon = icon;
/*     */   }
/*     */   
/*     */   public void setFileSystem(MFileSystem sys) {
/* 109 */     this.m_hFileSystem = sys;
/* 110 */     this.m_hFileSystem.setCallback(this);
/* 111 */     this.m_hFileSystem.reload(0);
/*     */   }
/*     */   
/*     */   public MFileSystem getFileSystem() {
/* 115 */     return this.m_hFileSystem;
/*     */   }
/*     */   
/*     */   public void callBack(Object result, int type) {
/* 119 */     switch (type) {
/*     */       case 0:
/*     */       case 1:
/* 122 */         this.m_hListModel.fireDataChanged();
/* 123 */         this.m_hTableModel.fireTableDataChanged();
/* 124 */         this.m_hDirList.setSelectedIndex(0);
/* 125 */         this.m_hFileTable.setRowSelectionInterval(0, 0);
/*     */         break;
/*     */     } 
/*     */   }
/*     */   
/*     */   public void dispose() {
/* 131 */     for (int i = 0; i < 5; i++) {
/* 132 */       TableColumn column = this.m_hFileTable.getColumnModel().getColumn(i);
/* 133 */       System.out.println("Column width " + i + ": " + column.getWidth());
/*     */     } 
/* 135 */     super.dispose();
/*     */   }
/*     */   
/*     */   public void setSingleSelection(boolean sel) {
/* 139 */     if (sel) { this.m_hFileTable.setSelectionMode(0); }
/* 140 */     else { this.m_hFileTable.setSelectionMode(2); }
/*     */   
/*     */   }
/*     */   public JPanel createPanel(String titleB, String titleOK) {
/* 144 */     JPanel panel = new JPanel();
/* 145 */     panel.setLayout((LayoutManager)new PercentLayout("p 2d 100% p p", 5, 15, true));
/* 146 */     panel.add(this.m_hTitleB = new JLabel(titleB));
/*     */     
/* 148 */     JPanel p1 = new JPanel();
/* 149 */     p1.setLayout((LayoutManager)new PercentLayout("30% 70%", 5, 0, false));
/* 150 */     this.m_hDirList = new MyList(this.m_hListModel = new MyListModel());
/* 151 */     this.m_hDirList.setSelectionMode(0);
/* 152 */     this.m_hDirList.setCellRenderer(new MImageCellRenderer(MediaCache.getInstance().getImage("dir.gif"), this.m_hDirList));
/* 153 */     this.m_hDirList.addKeyListener(new MyDirKeyListener());
/* 154 */     this.m_hDirList.addMouseListener(new MyDirMouseListener());
/* 155 */     this.m_hDirScroll = new JScrollPane(this.m_hDirList);
/* 156 */     p1.add(this.m_hDirScroll);
/* 157 */     this.m_hSorter = new MyTableSorter(this.m_hTableModel = new MyTableModel());
/* 158 */     this.m_hFileTable = new MyTable(this.m_hSorter);
/* 159 */     this.m_hSorter.addMouseListenerToHeaderInTable(this.m_hFileTable);
/* 160 */     this.m_hFileTable.setPreferredScrollableViewportSize(new Dimension(450, 200));
/* 161 */     this.m_hFileTable.addFocusListener(new MyFocusListener());
/* 162 */     this.m_hFileTable.getSelectionModel().addListSelectionListener(new MySelectionListener());
/* 163 */     this.m_hFileScroll = new JScrollPane(this.m_hFileTable);
/* 164 */     p1.add(this.m_hFileScroll);
/* 165 */     panel.add(p1);
/*     */     
/* 167 */     for (int i = 0; i < 5; i++) {
/* 168 */       TableColumn column = this.m_hFileTable.getColumnModel().getColumn(i);
/* 169 */       switch (i) { case 0:
/* 170 */           column.setPreferredWidth(20); break;
/*     */         case 1:
/* 172 */           column.setPreferredWidth(100); break;
/*     */         case 2:
/*     */         case 3:
/* 175 */           column.setPreferredWidth(40); break;
/*     */         case 4:
/* 177 */           column.setPreferredWidth(60);
/*     */           break; }
/*     */ 
/*     */     
/*     */     } 
/* 182 */     JPanel p2 = new JPanel();
/* 183 */     p2.setLayout((LayoutManager)new PercentLayout("p 100%", 5, 15, false));
/* 184 */     p2.setBorder(this.m_hEtchedBorder);
/* 185 */     p2.add(new JLabel("File:"));
/* 186 */     p2.add(this.m_hFile = new JTextField());
/* 187 */     panel.add(p2);
/*     */     
/* 189 */     JPanel p3 = new JPanel();
/* 190 */     p3.setLayout((LayoutManager)new PercentLayout("p 100%d p", 5, 0, false));
/* 191 */     MediaCache cache = MediaCache.getInstance();
/* 192 */     p3.add(this.m_hOkButton = new JButton(titleOK, new ImageIcon(cache.getImage("ok.gif"))));
/* 193 */     this.m_hOkButton.addActionListener(new OkListener());
/* 194 */     p3.add(this.m_hCancelButton = new JButton("Cancel", new ImageIcon(cache.getImage("cancel.gif"))));
/* 195 */     panel.add(p3);
/*     */     
/* 197 */     return panel;
/*     */   }
/*     */   
/*     */   public void requestInputFocus() {
/* 201 */     this.m_hFile.requestFocus();
/*     */   }
/*     */   
/*     */   public void selectFile(ListSelectionModel lsm) {
/* 205 */     if (lsm.isSelectionEmpty()) {
/* 206 */       this.m_hFile.setText("");
/*     */     } else {
/* 208 */       boolean isFirst = true;
/* 209 */       String value = null;
/* 210 */       int minIndex = lsm.getMinSelectionIndex();
/* 211 */       int maxIndex = lsm.getMaxSelectionIndex();
/* 212 */       for (int i = minIndex; i <= maxIndex; i++) {
/* 213 */         if (lsm.isSelectedIndex(i)) {
/* 214 */           String name = this.m_hFileTable.getValueAt(i, 1).toString();
/* 215 */           if (isFirst) {
/* 216 */             value = name;
/* 217 */             isFirst = false;
/*     */           } else {
/* 219 */             value = value + ", " + name;
/*     */           } 
/*     */         } 
/*     */       } 
/* 223 */       if (value != null) this.m_hFile.setText(value); 
/*     */     } 
/*     */   }
/*     */   
/*     */   public void chSelectedDir() {
/* 228 */     int idx = this.m_hDirList.getSelectedIndex();
/* 229 */     if (idx != -1) {
/* 230 */       String dir = this.m_hFileSystem.getDirectoryAt(idx);
/* 231 */       this.m_hFileSystem.chdir(dir, 1);
/* 232 */       this.m_EntrySequence = null;
/*     */       return;
/*     */     } 
/*     */   }
/*     */   
/*     */   public class MyDirMouseListener
/*     */     extends MouseAdapter {
/*     */     public void mouseClicked(MouseEvent e) {
/* 240 */       if (e.getClickCount() == 2) MFileDialog.this.chSelectedDir(); 
/*     */     }
/*     */   }
/*     */   
/*     */   public class MyDirKeyListener
/*     */     extends KeyAdapter {
/*     */     public void keyReleased(KeyEvent e) {
/* 247 */       int keycode = e.getKeyCode();
/* 248 */       if (keycode == 39 || keycode == 10) {
/* 249 */         MFileDialog.this.chSelectedDir();
/*     */         return;
/*     */       } 
/* 252 */       if (keycode == 37) {
/* 253 */         MFileDialog.this.m_hFileSystem.chdir("..", 1);
/* 254 */         MFileDialog.this.m_EntrySequence = null;
/*     */         return;
/*     */       } 
/* 257 */       if (keycode == 27) {
/* 258 */         MFileDialog.this.dispose();
/*     */         return;
/*     */       } 
/* 261 */       int ch = e.getKeyChar();
/* 262 */       if (ch >= 65 && ch <= 90) ch = ch + 97 - 65; 
/* 263 */       if (Character.isLetterOrDigit((char)ch) || ch == 45 || ch == 95) {
/* 264 */         if (MFileDialog.this.m_EntrySequence == null) { MFileDialog.this.m_EntrySequence = String.valueOf((char)ch); }
/* 265 */         else { MFileDialog.this.m_EntrySequence += String.valueOf((char)ch); }
/* 266 */          int seqlen = MFileDialog.this.m_EntrySequence.length();
/* 267 */         int len = MFileDialog.this.m_hFileSystem.getNbDirectories();
/* 268 */         for (int i = 0; i < len; i++) {
/* 269 */           String dir = MFileDialog.this.m_hFileSystem.getDirectoryAt(i);
/* 270 */           String cmp = (dir.length() > seqlen) ? dir.substring(0, seqlen) : dir;
/* 271 */           if (MFileDialog.this.m_EntrySequence.equals(cmp.toLowerCase())) {
/* 272 */             MFileDialog.this.m_hDirList.setSelectedValue(dir, true);
/*     */             return;
/*     */           } 
/*     */         } 
/*     */       } 
/* 277 */       MFileDialog.this.m_EntrySequence = null;
/*     */     }
/*     */   }
/*     */   
/*     */   public class OkListener
/*     */     implements ActionListener {
/*     */     public void actionPerformed(ActionEvent e) {
/* 284 */       MFileDialog.this.m_hOnOK.actionPerformed(e);
/*     */     }
/*     */   }
/*     */   
/*     */   public class MySelectionListener
/*     */     implements ListSelectionListener {
/*     */     public void valueChanged(ListSelectionEvent e) {
/* 291 */       if (e.getValueIsAdjusting())
/* 292 */         return;  ListSelectionModel lsm = (ListSelectionModel)e.getSource();
/* 293 */       MFileDialog.this.selectFile(lsm);
/*     */     }
/*     */   }
/*     */   
/*     */   public class MyFocusListener
/*     */     extends FocusAdapter {
/*     */     public void focusGained(FocusEvent e) {
/* 300 */       MFileDialog.this.m_hFileTable.setColumnSelectionInterval(1, 1);
/* 301 */       ListSelectionModel lsm = MFileDialog.this.m_hFileTable.getSelectionModel();
/* 302 */       MFileDialog.this.selectFile(lsm);
/*     */     }
/*     */   }
/*     */   
/*     */   public class MyList
/*     */     extends JList {
/*     */     public static final long serialVersionUID = 1L;
/*     */     
/*     */     public MyList(ListModel<E> lm) {
/* 311 */       super(lm);
/*     */     }
/*     */     
/*     */     public boolean isManagingFocus() {
/* 315 */       return true;
/*     */     }
/*     */   }
/*     */   
/*     */   public class MyTable
/*     */     extends JTable {
/*     */     public static final long serialVersionUID = 1L;
/*     */     
/*     */     public MyTable(TableModel tm) {
/* 324 */       super(tm);
/*     */     }
/*     */     
/*     */     public boolean isManagingFocus() {
/* 328 */       return false;
/*     */     }
/*     */   }
/*     */   
/*     */   public class MyListModel
/*     */     extends AbstractListModel
/*     */   {
/*     */     public static final long serialVersionUID = 1L;
/*     */     protected int m_iPrevSize;
/*     */     
/*     */     public Object getElementAt(int index) {
/* 339 */       return MFileDialog.this.m_hFileSystem.getDirectoryAt(index);
/*     */     }
/*     */     
/*     */     public int getSize() {
/* 343 */       return (MFileDialog.this.m_hFileSystem == null) ? 0 : MFileDialog.this.m_hFileSystem.getNbDirectories();
/*     */     }
/*     */     
/*     */     public void fireDataChanged() {
/* 347 */       int size = getSize();
/* 348 */       MFileDialog.this.m_hDirList.setSelectedIndex(0);
/* 349 */       if (size < this.m_iPrevSize) fireIntervalRemoved(this, size, this.m_iPrevSize - 1); 
/* 350 */       if (size > this.m_iPrevSize) fireIntervalAdded(this, this.m_iPrevSize, size - 1); 
/* 351 */       int max = Math.min(size, this.m_iPrevSize) - 1;
/* 352 */       if (max > 0) fireContentsChanged(this, 0, max); 
/* 353 */       this.m_iPrevSize = size;
/*     */     }
/*     */   }
/*     */   
/*     */   public class MyTableModel
/*     */     extends AbstractTableModel
/*     */   {
/*     */     public static final long serialVersionUID = 1L;
/* 361 */     final String[] columnNames = new String[] { "", "Name", "Time", "Date", "Size" };
/*     */     
/*     */     public int getColumnCount() {
/* 364 */       return this.columnNames.length;
/*     */     }
/*     */     
/*     */     public int getRowCount() {
/* 368 */       return (MFileDialog.this.m_hFileSystem == null) ? 0 : MFileDialog.this.m_hFileSystem.getNbFiles();
/*     */     }
/*     */     
/*     */     public String getColumnName(int col) {
/* 372 */       return this.columnNames[col];
/*     */     }
/*     */     
/*     */     public Object getValueAt(int row, int col) {
/* 376 */       MFileEntry entry = MFileDialog.this.m_hFileSystem.getFileAt(row);
/* 377 */       if (col == 0) {
/* 378 */         String ex = entry.getExtension();
/* 379 */         ImageIcon ic = (ImageIcon)MFileDialog.this.m_hIcons.get(ex);
/* 380 */         if (ic != null) return ic; 
/* 381 */         return MFileDialog.this.m_hDefaultIcon;
/*     */       } 
/* 383 */       if (col == 1) return entry.getName(); 
/* 384 */       if (col == 2) return entry.getTimeString(); 
/* 385 */       if (col == 3) return entry.getDateString(); 
/* 386 */       if (col == 4) return entry.getLengthString(); 
/* 387 */       return "";
/*     */     }
/*     */     
/*     */     public Class getColumnClass(int col) {
/* 391 */       if (col == 0) return ImageIcon.class; 
/* 392 */       return String.class;
/*     */     }
/*     */     
/*     */     public boolean isCellEditable(int row, int col) {
/* 396 */       if (col == 1) return true; 
/* 397 */       return false;
/*     */     }
/*     */ 
/*     */     
/*     */     public void setValueAt(Object value, int row, int col) {
/* 402 */       fireTableCellUpdated(row, col);
/*     */     }
/*     */   }
/*     */   
/*     */   public class MyTableSorter
/*     */     extends AbstractTableModel
/*     */     implements TableModelListener {
/*     */     public static final long serialVersionUID = 1L;
/*     */     TableModel model;
/*     */     int[] indexes;
/* 412 */     int sortingColumn = 1;
/*     */     boolean ascending = true;
/*     */     
/*     */     public MyTableSorter(TableModel model) {
/* 416 */       setModel(model);
/*     */     }
/*     */     
/*     */     public void setModel(TableModel model) {
/* 420 */       this.model = model;
/* 421 */       model.addTableModelListener(this);
/* 422 */       reallocateIndexes();
/*     */     }
/*     */     
/*     */     public int compareRowsByColumn(int row1, int row2, int column) {
/* 426 */       MFileEntry ent1 = MFileDialog.this.m_hFileSystem.getFileAt(row1);
/* 427 */       MFileEntry ent2 = MFileDialog.this.m_hFileSystem.getFileAt(row2);
/* 428 */       return ent1.compareTo(ent2, column);
/*     */     }
/*     */     
/*     */     public int compare(int row1, int row2) {
/* 432 */       int result = compareRowsByColumn(row1, row2, this.sortingColumn);
/* 433 */       return this.ascending ? result : -result;
/*     */     }
/*     */     
/*     */     public void reallocateIndexes() {
/* 437 */       int rowCount = this.model.getRowCount();
/* 438 */       this.indexes = new int[rowCount];
/* 439 */       for (int row = 0; row < rowCount; row++)
/* 440 */         this.indexes[row] = row; 
/*     */     }
/*     */     
/*     */     public void tableChanged(TableModelEvent e) {
/* 444 */       reallocateIndexes();
/* 445 */       fireTableChanged(e);
/*     */     }
/*     */     
/*     */     public void sort() {
/* 449 */       shuttlesort((int[])this.indexes.clone(), this.indexes, 0, this.indexes.length);
/*     */     }
/*     */     
/*     */     public void shuttlesort(int[] from, int[] to, int low, int high) {
/* 453 */       if (high - low < 2)
/* 454 */         return;  int middle = (low + high) / 2;
/* 455 */       shuttlesort(to, from, low, middle);
/* 456 */       shuttlesort(to, from, middle, high);
/* 457 */       int p = low;
/* 458 */       int q = middle;
/* 459 */       if (high - low >= 4 && compare(from[middle - 1], from[middle]) <= 0) {
/* 460 */         for (int j = low; j < high; ) { to[j] = from[j]; j++; }
/*     */          return;
/*     */       } 
/* 463 */       for (int i = low; i < high; i++) {
/* 464 */         if (q >= high || (p < middle && compare(from[p], from[q]) <= 0)) { to[i] = from[p++]; }
/* 465 */         else { to[i] = from[q++]; }
/*     */       
/*     */       } 
/*     */     }
/*     */     public Object getValueAt(int aRow, int aColumn) {
/* 470 */       return this.model.getValueAt(this.indexes[aRow], aColumn);
/*     */     }
/*     */     
/*     */     public void setValueAt(Object aValue, int aRow, int aColumn) {
/* 474 */       this.model.setValueAt(aValue, this.indexes[aRow], aColumn);
/*     */     }
/*     */     
/*     */     public int getRowCount() {
/* 478 */       return (this.model == null) ? 0 : this.model.getRowCount();
/*     */     }
/*     */     
/*     */     public int getColumnCount() {
/* 482 */       return (this.model == null) ? 0 : this.model.getColumnCount();
/*     */     }
/*     */     
/*     */     public String getColumnName(int aColumn) {
/* 486 */       return this.model.getColumnName(aColumn);
/*     */     }
/*     */     
/*     */     public Class getColumnClass(int aColumn) {
/* 490 */       return this.model.getColumnClass(aColumn);
/*     */     }
/*     */     
/*     */     public boolean isCellEditable(int row, int column) {
/* 494 */       return this.model.isCellEditable(row, column);
/*     */     }
/*     */     
/*     */     public void sortByColumn(int column, boolean ascending) {
/* 498 */       this.ascending = ascending;
/* 499 */       this.sortingColumn = column;
/* 500 */       sort();
/* 501 */       fireTableDataChanged();
/*     */     }
/*     */     
/*     */     public void addMouseListenerToHeaderInTable(JTable table) {
/* 505 */       MFileDialog.this.m_hFileTable.getTableHeader().addMouseListener(new MyMouseListener());
/*     */     }
/*     */     
/*     */     public class MyMouseListener
/*     */       extends MouseAdapter {
/*     */       public void mouseClicked(MouseEvent e) {
/* 511 */         TableColumnModel columnModel = MFileDialog.this.m_hFileTable.getColumnModel();
/* 512 */         int viewColumn = columnModel.getColumnIndexAtX(e.getX());
/* 513 */         int column = MFileDialog.this.m_hFileTable.convertColumnIndexToModel(viewColumn);
/* 514 */         if (e.getClickCount() == 1 && column != -1) {
/* 515 */           int shiftPressed = e.getModifiers() & 0x1;
/* 516 */           boolean ascending = (shiftPressed == 0);
/* 517 */           MFileDialog.MyTableSorter.this.sortByColumn(column, ascending);
/*     */         } 
/*     */       }
/*     */     }
/*     */   }
/*     */ }


/* Location:              C:\Users\1231\Desktop\dataprocessing\activelearning\Clus-AL\Clus-AL\ClusAL.jar!\jeans\graph\swing\MFileDialog.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */