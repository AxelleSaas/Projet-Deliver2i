/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vuecontrole;

import io.InstanceReader;
import io.exception.ReaderException;
import java.awt.Color;
import java.awt.FlowLayout;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.Query;
import javax.swing.DefaultListModel;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.ListSelectionModel;
import metier.RequetePlanning;
import modele.Instance;
import modele.Shift;
import modele.Solution;
import modele.Tournee;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.data.category.IntervalCategoryDataset;
import org.jfree.data.gantt.Task;
import org.jfree.data.gantt.TaskSeries;
import org.jfree.data.gantt.TaskSeriesCollection;

/**
 * @author Axelle
 */
public class ListeInstances extends javax.swing.JFrame {
    /**
     * Creates new form ListeInstances
     */
    
    private RequetePlanning requetePlanning;
    
  
    public ListeInstances() {
        initConnexion();
        initComponents();
        this.initialisationFenetre();
        this.remplirListeInstances();
        this.remplirListeSolution();
    }
    
    private void initConnexion() {
        this.requetePlanning = requetePlanning.getInstance();
    }

    private void initialisationFenetre(){
        this.setVisible(true);
        this.setLocation(0, 0 );
        this.setTitle("Gestion des instances");
        this.getContentPane().setBackground(Color.LIGHT_GRAY);
        listeInstancesSauv.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listeSolutions.addItem("Solution triviale");
        listeSolutions.addItem("Solution basique");
    }
       
    private void remplirListeInstances() {
        DefaultListModel list = new DefaultListModel();
        listeInstancesSauv.setModel(list);
        final EntityManagerFactory emf = Persistence.createEntityManagerFactory("Deliver2iPU");
        final EntityManager em = emf.createEntityManager();
        try{
           Query query = em.createNamedQuery("Instance.findAll");
           List<Instance> listeObjInstance = query.getResultList();
           for(Instance i : listeObjInstance) {
               Query query2 = em.createNamedQuery("Tournee.getTourneeByInstanceId");
               query2.setParameter("id", i);
               List<Tournee> listeTournee = query2.getResultList();
               for(Tournee t : listeTournee){
                   i.getTournees().add(t);
               }
               list.addElement(i);
               listeInstancesSauv.setModel(list);
           }
        } 
        finally {
            if(em != null && em.isOpen()){
                em.close();
            }
            if(emf != null && emf.isOpen()){
                emf.close();
            }
        }
        
        
    }
    
    private IntervalCategoryDataset getCategoryDataset(Solution s) {
     
         TaskSeries serie = new TaskSeries("Tournées");
         
         System.out.println(s.getShifts());
         
          Date datedebut = new Date(0);
          Date datefin = new Date(86400000);
            
          System.out.println("Date debut : "+ datedebut);
          System.out.println("Date fin : "+ datefin);

          int j =0;
          for(Shift shift : s.getShifts()){
                String name = "Shift " + j + " : " + shift.getTempsMort() + "min";
                final Task t = new Task(name, datedebut,  datefin);
                for(Tournee tournee : shift.getTournees()){
                    final Task st = new Task(name, tournee.getDebut(), tournee.getFin());
                    t.addSubtask(st);
                }
               serie.add(t);
                j++;
            }
          
          final TaskSeriesCollection dataset = new TaskSeriesCollection();
          dataset.add(serie);
          return dataset;
     }
     
     private void remplirListeSolution() {
        DefaultListModel list = new DefaultListModel();
        listeSolution.setModel(list);
        final EntityManagerFactory emf =Persistence.createEntityManagerFactory("Deliver2iPU");
        final EntityManager em = emf.createEntityManager();
        try{
            
           Query query = em.createNamedQuery("Solution.findAll");
           List<Solution> listeObjSolution = query.getResultList();
           
           for(Solution s : listeObjSolution) {
               
               Query query2 = em.createNamedQuery("Shift.getShiftBySolutionId");
               query2.setParameter("id", s);
               List<Shift> listeShift = query2.getResultList();
               
               for(Shift shift : listeShift){
                   
                    s.getShifts().add(shift);
               }
               //System.out.println(s.getShifts());
               list.addElement(s);
               listeSolution.setModel(list);
           }
        } 
        finally {
            if(em != null && em.isOpen()){
                em.close();
            }
            if(emf != null && emf.isOpen()){
                emf.close();
            }
        }
        
        
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        listeInstancesSauv = new javax.swing.JList<>();
        ajoutInstance = new javax.swing.JButton();
        listeSolutions = new javax.swing.JComboBox<>();
        ajoutSolution = new javax.swing.JButton();
        supprimerInstance = new javax.swing.JButton();
        afficherSolution = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        listeSolution = new javax.swing.JList<>();
        jPanel1 = new javax.swing.JPanel();
        jSeparator1 = new javax.swing.JSeparator();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setMinimumSize(new java.awt.Dimension(1920, 1080));

        listeInstancesSauv.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jScrollPane1.setViewportView(listeInstancesSauv);

        ajoutInstance.setText("Ajouter une instance");
        ajoutInstance.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ajoutInstanceActionPerformed(evt);
            }
        });

        ajoutSolution.setText("Ajouter une solution");
        ajoutSolution.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ajoutSolutionActionPerformed(evt);
            }
        });

        supprimerInstance.setText("Supprimer une instance");
        supprimerInstance.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                supprimerInstanceActionPerformed(evt);
            }
        });

        afficherSolution.setText("Afficher");
        afficherSolution.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                afficherSolutionActionPerformed(evt);
            }
        });

        jScrollPane2.setViewportView(listeSolution);

        jPanel1.setMinimumSize(new java.awt.Dimension(0, 1000));
        jPanel1.setPreferredSize(new java.awt.Dimension(1198, 1000));

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1198, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1000, Short.MAX_VALUE)
        );

        jLabel1.setFont(new java.awt.Font("Kozuka Gothic Pr6N EL", 1, 24)); // NOI18N
        jLabel1.setText("Deliver2I");

        jLabel2.setText("Liste des instances chargées");

        jLabel3.setText("Liste des solutions");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(179, 179, 179)
                        .addComponent(jLabel1))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 432, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jLabel2)
                                .addGroup(layout.createSequentialGroup()
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                        .addComponent(listeSolutions, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 237, Short.MAX_VALUE)
                                        .addComponent(afficherSolution, javax.swing.GroupLayout.DEFAULT_SIZE, 237, Short.MAX_VALUE)
                                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                        .addComponent(ajoutInstance, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(ajoutSolution, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(supprimerInstance, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                                .addComponent(jLabel3)))))
                .addGap(18, 18, 18)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(15, 15, 15)
                        .addComponent(jLabel1)
                        .addGap(16, 16, 16)
                        .addComponent(jLabel2)
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(ajoutInstance)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(supprimerInstance))
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(ajoutSolution)
                            .addComponent(listeSolutions, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(3, 3, 3)
                        .addComponent(jLabel3)
                        .addGap(18, 18, 18)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 156, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(afficherSolution))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void ajoutInstanceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ajoutInstanceActionPerformed
        JFileChooser chooser = new JFileChooser();
        if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION){
            String chemin = chooser.getSelectedFile().getAbsolutePath();
            try{
                InstanceReader ir = new InstanceReader(chemin);
                this.requetePlanning.ajouterInstance(ir.readInstance());
            }      
            catch (ReaderException ex) {
                Logger.getLogger(ListeInstances.class.getName()).log(Level.SEVERE, null, ex);
            }            finally {
                this.remplirListeInstances();
            }
        }
    }//GEN-LAST:event_ajoutInstanceActionPerformed

    private void ajoutSolutionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ajoutSolutionActionPerformed
        // TODO add your handling code here:
        if(!listeInstancesSauv.isSelectionEmpty()){
            
            switch(listeSolutions.getItemAt(listeSolutions.getSelectedIndex())){
                 case "Solution basique":
                            final EntityManagerFactory emf1 =Persistence.createEntityManagerFactory("Deliver2iPU");
                            final EntityManager em1 = emf1.createEntityManager();
                            try{
                                final EntityTransaction et1 = em1.getTransaction();
                                try{
                                    et1.begin();

                                    Solution s = new Solution();
                                    Object obj1 = listeInstancesSauv.getSelectedValue();
                                    Instance i1 = (Instance)obj1;

                                    s.ajouterInstance(i1);

                                    s.solutionBasique(0);
                                    System.out.println(s);
                                    em1.persist(s);
                                    et1.commit();
                                    JOptionPane.showMessageDialog(rootPane, "Ajout le solution réussi.");
                                }
                                catch (Exception ex) {
                                    et1.rollback();
                                }
                            } 
                            finally {
                                if(em1 != null && em1.isOpen()){
                                    em1.close();
                                }
                                if(emf1 != null && emf1.isOpen()){
                                    emf1.close();
                                }
                            }
                        break;
                case "Solution triviale":
                    final EntityManagerFactory emf =Persistence.createEntityManagerFactory("Deliver2iPU");
                    final EntityManager em = emf.createEntityManager();
                    try{
                        final EntityTransaction et = em.getTransaction();
                        try{
                            et.begin();
                            
                            Solution s = new Solution();
                            Object obj = listeInstancesSauv.getSelectedValue();
                            Instance i = (Instance)obj;
                            
                            s.ajouterInstance(i);
                            
                            s.solutionTriviale();
                            em.persist(s);
                            et.commit();
                            JOptionPane.showMessageDialog(rootPane, "Ajout le solution réussi.");
                        }
                        catch (Exception ex) {
                            et.rollback();
                        }
                    } 
                    finally {
                        if(em != null && em.isOpen()){
                            em.close();
                        }
                        if(emf != null && emf.isOpen()){
                            emf.close();
                        }
                    }
                    break;
                   
            }
        this.remplirListeSolution();
        } 
    }//GEN-LAST:event_ajoutSolutionActionPerformed

    private void supprimerInstanceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_supprimerInstanceActionPerformed
         if(!listeInstancesSauv.isSelectionEmpty()){
            final EntityManagerFactory emf = Persistence.createEntityManagerFactory("Deliver2iPU");
            final EntityManager em = emf.createEntityManager();
            try{
                final EntityTransaction et = em.getTransaction();
                try{
                    et.begin();
                    Object obj = listeInstancesSauv.getSelectedValue();
                    Instance i = (Instance)obj;
                    Instance it = em.find(Instance.class, i.getId());
                    em.remove(it);
                    et.commit();
                    JOptionPane.showMessageDialog(rootPane, "Supression réussie.");
                }
                catch (Exception ex) {
                    et.rollback();
                }
            } 
            finally {
                if(em != null && em.isOpen()){
                    em.close();
                }
                if(emf != null && emf.isOpen()){
                    emf.close();
                    this.remplirListeInstances();
                }
            }
         }
    }//GEN-LAST:event_supprimerInstanceActionPerformed

    private void afficherSolutionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_afficherSolutionActionPerformed
        // TODO add your handling code here:
        if(!listeSolution.isSelectionEmpty()){
            Object obj = listeSolution.getSelectedValue();
            Solution s = (Solution)obj;

            final IntervalCategoryDataset dataset = getCategoryDataset(s);

            // create the chart...
            final JFreeChart chart = ChartFactory.createGanttChart(
                "",  // chart title
                "Task",              // domain axis label
                "Date",              // range axis label
                dataset,             // data
                false,                // include legend
                true,                // tooltips
                false                // urls
            );
            final CategoryPlot plot = (CategoryPlot) chart.getPlot();
            //      plot.getDomainAxis().setMaxCategoryLabelWidthRatio(10.0f);
            final CategoryItemRenderer renderer = plot.getRenderer();
            renderer.setSeriesPaint(0, Color.blue);

            final ChartPanel chartPanel = new ChartPanel(chart);

            chartPanel.setPreferredSize(new java.awt.Dimension(jPanel1.getWidth(), jPanel1.getHeight()));
            jPanel1.removeAll();
            jPanel1.setLayout(new FlowLayout(FlowLayout.LEFT));
            jPanel1.add(chartPanel);

            this.revalidate();
        }
    }//GEN-LAST:event_afficherSolutionActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(ListeInstances.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ListeInstances.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ListeInstances.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ListeInstances.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new ListeInstances().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton afficherSolution;
    private javax.swing.JButton ajoutInstance;
    private javax.swing.JButton ajoutSolution;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JList<String> listeInstancesSauv;
    private javax.swing.JList<String> listeSolution;
    private javax.swing.JComboBox<String> listeSolutions;
    private javax.swing.JButton supprimerInstance;
    // End of variables declaration//GEN-END:variables
}
