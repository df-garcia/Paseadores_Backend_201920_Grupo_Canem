/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package co.edu.uniandes.csw.paseadores.persistence;

import co.edu.uniandes.csw.paseadores.entities.ComentarioEntity;
//import static com.sun.xml.internal.ws.spi.db.BindingContextFactory.LOGGER;
import java.util.List;
//import java.util.logging.Level;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;



/**
 *
 * @author Nicolas Potes Garcia
 */

@Stateless
public class ComentarioPersistence {
    
    
    @PersistenceContext(unitName="paseadoresPU")
    protected EntityManager em;
   
    
    public ComentarioEntity create(ComentarioEntity contrato) {
        
        em.persist(contrato);
        
        return contrato; 
        //throw new java.lang.UnsupportedOperationException("Not supported yet.");
    }
    
    
    public ComentarioEntity find(Long idComentario) {
        
       return em.find(ComentarioEntity.class, idComentario);
        
    }
    
    
    public List<ComentarioEntity> findAll() {
        
        TypedQuery query = em.createQuery("select u from ComentarioEntity u", ComentarioEntity.class);
        return query.getResultList();
        
    }
    
    
    public ComentarioEntity update(ComentarioEntity comentarioEntity) {
        //LOGGER.log(Level.INFO, "Actualizando el author con id={0}", contratoEntity.getId());
        /* Note que hacemos uso de un método propio del EntityManager llamado merge() que recibe como argumento
        la author con los cambios, esto es similar a 
        "UPDATE table_name SET column1 = value1, column2 = value2, ... WHERE condition;" en SQL.
         */
        return em.merge(comentarioEntity);
    }
    
    
    public void delete(Long comentarioId) {

       // LOGGER.log(Level.INFO, "Borrando el author con id={0}", contratoId);
      
        ComentarioEntity comentarioEntity = em.find(ComentarioEntity.class, comentarioId);
        /* Note que una vez obtenido el objeto desde la base de datos llamado "entity", volvemos hacer uso de un método propio del
        EntityManager para eliminar de la base de datos el objeto que encontramos y queremos borrar.
        Es similar a "delete from AuthorEntity where id=id;" - "DELETE FROM table_name WHERE condition;" en SQL.*/
        em.remove(comentarioEntity);
    }

}