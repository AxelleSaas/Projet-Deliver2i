/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package metier;

import io.InstanceReader;
import io.exception.ReaderException;
import java.util.HashMap;
import java.util.Map;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import modele.Instance;
import javax.persistence.Persistence;

/**
 *
 * @author HugoPortable
 */
public class RequetePlanning {
    private final EntityManagerFactory entityManagerFactory;
    private static RequetePlanning instance;

    /* C O N S T R U C T E U R S */
    private RequetePlanning() {
        System.out.println("Creation de l'entityManagerFactory");
        Map<String, String> properties = new HashMap<String, String>();
        properties.put("javax.persistence.jdbc.user", "Deliver2i");
        properties.put("javax.persistence.jdbc.password", "poo");
        this.entityManagerFactory = Persistence.createEntityManagerFactory("Deliver2iPU", properties);
    }
    
    /* A C C E S S E U R S   E T   M U T A T E U R S */
    public EntityManagerFactory getEntityManagerFactory() {
        return entityManagerFactory;
    }
    
    public static RequetePlanning getInstance() {
        if(instance == null)
            instance = new RequetePlanning();
        return instance;
    }  
    
    public void close() {
        if(this.entityManagerFactory != null && this.entityManagerFactory.isOpen()){
            this.entityManagerFactory.close();
        }
    }
    
    public boolean ajouterInstance(Instance i) {
        EntityManager em = this.entityManagerFactory.createEntityManager();
        try{
            EntityTransaction et = em.getTransaction();
            try{
                et.begin();
                em.persist(i);
                et.commit();
            }
            catch (Exception ex) {
                et.rollback();
            }
        } 
        finally {
            if(em != null && em.isOpen()){
                em.close();
            }
        }
        return true;
    }
    
    
    
    public static void main(String[] args) {
        String chemin = "./resources/instances/instance_3.csv";
        RequetePlanning requetePlanning = RequetePlanning.getInstance();
        try{
            InstanceReader ir = new InstanceReader(chemin);
            requetePlanning.ajouterInstance(ir.readInstance());
        } 
        catch (ReaderException ex) {
            System.out.println("Erreur de lecture de l'instance");
        }       
        chemin = "./resources/instances/instance_4.csv";
        try{
            InstanceReader ir = new InstanceReader(chemin);
            requetePlanning.ajouterInstance(ir.readInstance());
        } 
        catch (ReaderException ex) {
            System.out.println("Erreur de lecture de l'instance");
        }       
    }
}
