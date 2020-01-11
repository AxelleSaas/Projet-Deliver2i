/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package metier;

import java.util.HashMap;
import java.util.Map;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import static metier.RequetePlanning.entityManager;

/**
 *
 * @author HugoPortable
 */
public class RequetePlanning {
    public EntityManagerFactory entityManagerFactory;
    public EntityManager entityManager;
    private static RequetePlanning instance;

    private RequetePlanning() {
        this.connect();
    }
    
    public static RequetePlanning getInstance() {
        if(instance == null)
            instance = new RequetePlanning();
        return instance;
    }

    public EntityManager getEntityManager() {
        return entityManager;
    }
    
    public EntityManagerFactory getEntityManagerFactory() {
        return entityManagerFactory;
    }
    
    /**
     * Initialise l'attribut Connection en établissant une connexion à la BDD
     * @throws ClassNotFoundException
     * @throws SQLException 
     */
    private void connect() {        
        Map<String, String> properties = new HashMap<String, String>();
        properties.put("javax.persistence.jdbc.user", "Deliver2i");
        properties.put("javax.persistence.jdbc.password", "poo");
        RequetePlanning.entityManagerFactory = Persistence.createEntityManagerFactory("Deliver2iPU");
        RequetePlanning.entityManager = this.entityManagerFactory.createEntityManager();
    }
    
    public void close() {
        if(this.entityManager != null && this.entityManager.isOpen()){
                this.entityManager.close();
            }
    }
}
