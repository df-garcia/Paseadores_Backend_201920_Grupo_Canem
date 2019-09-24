/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package co.edu.uniandes.csw.paseadores.test.logic;

import co.edu.uniandes.csw.paseadores.ejb.ContratoLogic;
import co.edu.uniandes.csw.paseadores.entities.ContratoEntity;
import co.edu.uniandes.csw.paseadores.entities.ZonaEntity;
import co.edu.uniandes.csw.paseadores.exceptions.BusinessLogicException;
import co.edu.uniandes.csw.paseadores.persistence.ContratoPersistence;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.UserTransaction;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

/**
 *
 * @author Nicolas Potes Garcia
 */
@RunWith(Arquillian.class)
public class ContratoLogicTest {
    
    
    private PodamFactory factory = new PodamFactoryImpl();
    
    @Inject
    private ContratoLogic contratoLogic;
    
    @Inject
    UserTransaction utx;
    
    @PersistenceContext
    private EntityManager em;
    
    private ArrayList<ContratoEntity> data = new ArrayList<ContratoEntity>();
    
    @Deployment
    public static JavaArchive createDeployment() {
        return ShrinkWrap.create(JavaArchive.class)
                .addPackage(ContratoEntity.class.getPackage())
                .addPackage(ContratoLogic.class.getPackage())
                .addPackage(ContratoPersistence.class.getPackage())
                .addAsManifestResource("META-INF/persistence.xml", "persistence.xml")
                .addAsManifestResource("META-INF/beans.xml", "beans.xml");
    }
    
    
    /**
     * Configuración inicial de la prueba.
     */
    @Before
    public void configTest() {
        try {
            utx.begin();
            clearData();
            insertData();
            utx.commit();
        } catch (Exception e) {
            e.printStackTrace();
            try {
                utx.rollback();
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
    }
    
    
    
    /**
     * Limpia las tablas que están implicadas en la prueba.
     */
    private void clearData() {
        em.createQuery("delete from ContratoEntity").executeUpdate();
    }
    
    /**
     * Inserta los datos iniciales para el correcto funcionamiento de las
     * pruebas.
     */
    private void insertData() {
        for( int i = 0; i < 3 ; ++i ){
            ContratoEntity entity = factory.manufacturePojo(ContratoEntity.class);

            em.persist(entity);
            data.add(entity);
        }
    }
    
    
    @Test
    public void createContrato() throws BusinessLogicException {
        ContratoEntity newEntity = factory.manufacturePojo(ContratoEntity.class);
//        System.out.println("HOLAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
//        System.out.println(newEntity.getName());
//        System.out.println(newEntity.getHorarios());
//        System.out.println(newEntity.getZona());
//        System.out.println("HOLAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
//        ZonaEntity zona = new ZonaEntity();
//        newEntity.setZona(zona);
        ContratoEntity result = contratoLogic.createContrato(newEntity);
        Assert.assertNotNull(result);     
        
        ContratoEntity entity = em.find( ContratoEntity.class , result.getId());
//        ZonaEntity zonaX = new ZonaEntity();
//        entity.setZona(zonaX);
        Assert.assertEquals(result.getName(), entity.getName());
        //Assert.assertEquals(result.getFranja(), entity.getFranja());
       // Assert.assertNotNull(entity.getZona());
        
    }
    
    
            @Test( expected = BusinessLogicException.class)
            public void createContratoNombreNull() throws BusinessLogicException{
                ContratoEntity newEntity = factory.manufacturePojo(ContratoEntity.class);
                newEntity.setName(null);
                ContratoEntity result = contratoLogic.createContrato(newEntity);
            }
            
            
            
            @Test( expected = BusinessLogicException.class)
            public void deleteContratoSinFinalizar() throws BusinessLogicException{
                ContratoEntity newEntity = factory.manufacturePojo(ContratoEntity.class);
                newEntity.setFinalizado(Boolean.FALSE);
                contratoLogic.deleteContratoSinFinalizar(newEntity);
                
            }
            
            /**
     * Prueba para eliminar un Contrato
     */
    @Test
    public void deleteContratoTest() throws BusinessLogicException 
    {
        ContratoEntity entity = data.get(0);
        contratoLogic.deleteContrato(entity.getId());
        ContratoEntity deleted = em.find(ContratoEntity.class, entity.getId());
        Assert.assertNull(deleted);
    }
            
            @Test( expected = BusinessLogicException.class)
             public void createContratoValorNeg() throws BusinessLogicException{
        ContratoEntity newEntity = factory.manufacturePojo(ContratoEntity.class);
        newEntity.setValorServicio(-1.0);
        ContratoEntity result = contratoLogic.createContrato(newEntity);
    }
	
	
	@Test( expected = BusinessLogicException.class)
    public void createContratoValorNull() throws BusinessLogicException{
        ContratoEntity newEntity = factory.manufacturePojo(ContratoEntity.class);
        newEntity.setValorServicio(null);
        ContratoEntity result = contratoLogic.createContrato(newEntity);
    }
	
	
	
	@Test( expected = BusinessLogicException.class)
    public void createContratoPaseadorNull() throws BusinessLogicException{
        ContratoEntity newEntity = factory.manufacturePojo(ContratoEntity.class);
        newEntity.setIdPaseador(null);
        ContratoEntity result = contratoLogic.createContrato(newEntity);
    }
	
//	@Test( expected = BusinessLogicException.class)
//    public void createContratoClienteNull() throws BusinessLogicException{
//        ContratoEntity newEntity = factory.manufacturePojo(ContratoEntity.class);
//        newEntity.setIdCliente(null);
//        ContratoEntity result = contratoLogic.createContrato(newEntity);
//    }
	
	
	@Test( expected = BusinessLogicException.class)
    public void createContratoMascotaNull() throws BusinessLogicException{
        ContratoEntity newEntity = factory.manufacturePojo(ContratoEntity.class);
        newEntity.setIdMascota(null);
        ContratoEntity result = contratoLogic.createContrato(newEntity);
    }
        
    
    /**
     * Prueba para consultar un Contrato.
     */
    @Test
    public void getContratoTest() 
    {
        ContratoEntity entity = data.get(0);
        ContratoEntity resultEntity = contratoLogic.getContrato(entity.getId());
        Assert.assertNotNull(resultEntity);
        Assert.assertEquals(entity.getId(), resultEntity.getId());
    }
    
    /**
     * Prueba para consultar la lista de Contratos.
     */
    @Test
    public void getContratosTest() 
    {
        List<ContratoEntity> list = contratoLogic.getContratos();
        Assert.assertEquals(data.size(), list.size());
        for (ContratoEntity entity : list) {
            boolean found = false;
            for (ContratoEntity storedEntity : data) {
                if (entity.getId().equals(storedEntity.getId())) {
                    found = true;
                }
            }
            Assert.assertTrue(found);
        }
    }
    
    /**
     * Prueba para actualizar un Contrato.
     */
    @Test
    public void updateContratoTest() throws BusinessLogicException 
    {
        ContratoEntity entity = data.get(0);
        ContratoEntity pojoEntity = factory.manufacturePojo(ContratoEntity.class);

        pojoEntity.setId(entity.getId());

        contratoLogic.updateContrato(pojoEntity.getId(), pojoEntity);

        ContratoEntity resp = em.find(ContratoEntity.class, entity.getId());

        Assert.assertEquals(pojoEntity.getId(), resp.getId());
        
    }
            
            
            
//             @Test( expected = BusinessLogicException.class)
//            public void createContratoFranjaNull() throws BusinessLogicException{
//                ContratoEntity newEntity = factory.manufacturePojo(ContratoEntity.class);
//                newEntity.setFranja(null);
//                ContratoEntity result = contratoLogic.createContrato(newEntity);
//            }
            
    
//            @Test( expected = BusinessLogicException.class)
//            public void createContratoZonaNull() throws BusinessLogicException{
//                ContratoEntity newEntity = factory.manufacturePojo(ContratoEntity.class);
//                newEntity.setZona(null);
//                ContratoEntity result = contratoLogic.createContrato(newEntity);
//            }
    
    
//            @Test( expected = BusinessLogicException.class)
//            public void createContratoZonaNoValida() throws BusinessLogicException{
//               ContratoEntity newEntity = factory.manufacturePojo(ContratoEntity.class);
//               ZonaEntity zona = new ZonaEntity();
//               zona.setInfoZona("Colombia/Bogota/Usaquen");
//               newEntity.setZona(zona);
//               ContratoEntity result = contratoLogic.createContrato(newEntity);
//            }
    
    
}