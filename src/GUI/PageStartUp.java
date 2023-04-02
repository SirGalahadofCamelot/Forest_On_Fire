package GUI;

import java.awt.Image;
import java.io.File;
import java.util.SplittableRandom;
import javax.swing.ImageIcon;

/**
 *Forest on Fire: Section GUI: Classe PageStartUp
 * 
 * Frame de chargement qui appelle lorsqu'il a fini une fenêtre FPagePrincipale
 * @author Gorgette Nicolas et Jouteau Louis
 */

public class PageStartUp extends javax.swing.JFrame {
    
    
    static private SplittableRandom rand = new SplittableRandom();      

    /**
     * Creates new form PageStartUp
     */
    public PageStartUp() {
        initComponents();
        setLocationRelativeTo(null);        
        String emplacement = System.getProperty("user.dir")+File.separator+"src"+File.separator+"GUI"+File.separator+"logo_Forest_on_Fire.png";
        
        lLogo.setIcon(new ImageIcon(new ImageIcon(emplacement).getImage().getScaledInstance(lLogo.getWidth(), lLogo.getHeight(), Image.SCALE_DEFAULT)));
        lLogo.setVisible(true);    //Lignes pour afficher le logo du programme
        
}
   
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pPanelPrincipal = new javax.swing.JPanel();
        lLogo = new javax.swing.JLabel();
        pbBarreProg = new javax.swing.JProgressBar();
        lInfoChargmt = new javax.swing.JLabel();
        lValeurCharg = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setBackground(new java.awt.Color(255, 255, 255));

        pPanelPrincipal.setBackground(new java.awt.Color(255, 255, 255));
        pPanelPrincipal.setName("pPanelPrincipal"); // NOI18N

        lLogo.setBackground(new java.awt.Color(255, 255, 255));
        lLogo.setName("lLogo"); // NOI18N
        lLogo.setOpaque(true);

        pbBarreProg.setName("pbBarreProg"); // NOI18N

        lInfoChargmt.setFont(new java.awt.Font("Impact", 0, 14)); // NOI18N
        lInfoChargmt.setForeground(new java.awt.Color(0, 0, 0));
        lInfoChargmt.setName("lInfoChargmt"); // NOI18N

        lValeurCharg.setFont(new java.awt.Font("Impact", 0, 14)); // NOI18N
        lValeurCharg.setForeground(new java.awt.Color(0, 0, 0));
        lValeurCharg.setName("lValeurCharg"); // NOI18N

        javax.swing.GroupLayout pPanelPrincipalLayout = new javax.swing.GroupLayout(pPanelPrincipal);
        pPanelPrincipal.setLayout(pPanelPrincipalLayout);
        pPanelPrincipalLayout.setHorizontalGroup(
            pPanelPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pPanelPrincipalLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pPanelPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pPanelPrincipalLayout.createSequentialGroup()
                        .addComponent(lInfoChargmt, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lLogo, javax.swing.GroupLayout.PREFERRED_SIZE, 294, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 79, Short.MAX_VALUE)
                        .addComponent(lValeurCharg, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(pbBarreProg, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        pPanelPrincipalLayout.setVerticalGroup(
            pPanelPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pPanelPrincipalLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(pPanelPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lInfoChargmt, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lLogo, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 353, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lValeurCharg, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(12, 12, 12)
                .addComponent(pbBarreProg, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pPanelPrincipal, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pPanelPrincipal, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

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
            java.util.logging.Logger.getLogger(PageStartUp.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(PageStartUp.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(PageStartUp.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(PageStartUp.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        
        
        
                PageStartUp psu = new PageStartUp();                
                psu.setVisible(true);
                char pt = '.';
                String points;//Nb de points à afficher
                
                try{
                    for(int i=0;i<=100;i++){
                        points="";
                        Thread.sleep(rand.nextInt(10, 25));
                        psu.lValeurCharg.setText(i+"%");//Pourcentage de chargement
                        for(int j=0; j<i%4;j++){points+=pt;}//Donne tour à tour 0 à 3 points
                        psu.lInfoChargmt.setText("Initialisation"+points);
                        psu.pbBarreProg.setValue(i);                                               
                    }    
                }
                catch(Exception e){} 
                FPagePrincipale.main(args);
                psu.setVisible(false);
    }

  
    

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel lInfoChargmt;
    private javax.swing.JLabel lLogo;
    private javax.swing.JLabel lValeurCharg;
    private javax.swing.JPanel pPanelPrincipal;
    private javax.swing.JProgressBar pbBarreProg;
    // End of variables declaration//GEN-END:variables
}
