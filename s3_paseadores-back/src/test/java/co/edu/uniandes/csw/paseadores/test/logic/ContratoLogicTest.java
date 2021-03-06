package co.edu.uniandes.csw.paseadores.test.logic;

import co.edu.uniandes.csw.paseadores.ejb.ContratoLogic;
import co.edu.uniandes.csw.paseadores.entities.ClienteEntity;
import co.edu.uniandes.csw.paseadores.entities.ComentarioEntity;
import co.edu.uniandes.csw.paseadores.entities.ContratoEntity;
import co.edu.uniandes.csw.paseadores.entities.PagoEntity;
import co.edu.uniandes.csw.paseadores.entities.PaseadorEntity;
import co.edu.uniandes.csw.paseadores.entities.ZonaEntity;
import co.edu.uniandes.csw.paseadores.entities.FranjaHorariaEntity;
import co.edu.uniandes.csw.paseadores.entities.MascotaEntity;
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

    private ZonaEntity zonaTest;

    private FranjaHorariaEntity franjaTest;

    private ClienteEntity clienteTest;

    private PaseadorEntity paseadorTest;

    private List<MascotaEntity> mascotasTest = new ArrayList<MascotaEntity>();

    private PagoEntity pagoRealizadoTest;

    private PagoEntity pagoNoRealizadoTest;

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
        em.createQuery("delete from ZonaEntity").executeUpdate();
        em.createQuery("delete from ClienteEntity").executeUpdate();
        em.createQuery("delete from PaseadorEntity").executeUpdate();
        em.createQuery("delete from FranjaHorariaEntity").executeUpdate();
        em.createQuery("delete from MascotaEntity").executeUpdate();
        em.createQuery("delete from PagoEntity").executeUpdate();
    }

    /**
     * Inserta los datos iniciales para el correcto funcionamiento de las
     * pruebas.
     */
    private void insertData() {

        ZonaEntity zona = factory.manufacturePojo(ZonaEntity.class);
        em.persist(zona);
        FranjaHorariaEntity franja = factory.manufacturePojo(FranjaHorariaEntity.class);
        em.persist(franja);
        ClienteEntity cliente = factory.manufacturePojo(ClienteEntity.class);
        em.persist(cliente);
        PaseadorEntity paseador = factory.manufacturePojo(PaseadorEntity.class);
        em.persist(paseador);
        MascotaEntity mascota = factory.manufacturePojo(MascotaEntity.class);
        em.persist(mascota);
        PagoEntity pago = factory.manufacturePojo(PagoEntity.class);
        pago.setPagoRealizado(false);
        em.persist(pago);
        PagoEntity pago1 = factory.manufacturePojo(PagoEntity.class);
        pago1.setPagoRealizado(true);
        em.persist(pago1);

        zonaTest = zona;
        franjaTest = franja;
        clienteTest = cliente;
        paseadorTest = paseador;
        mascotasTest.add(mascota);
        pagoRealizadoTest = pago1;
        pagoNoRealizadoTest = pago;

        for (int i = 0; i < 3; ++i) {

            ContratoEntity entity = factory.manufacturePojo(ContratoEntity.class);
            entity.setZona(zona);
            entity.setFranja(franja);
            entity.setCliente(cliente);
            entity.setPaseador(paseador);
            entity.setMascotas(mascotasTest);
            if (i % 2 == 0) {
                entity.setPago(pagoRealizadoTest);
            } else {
                entity.setPago(pagoNoRealizadoTest);
            }
            if (i < 2) {
                entity.setFinalizado(true);
            } else {
                entity.setFinalizado(false);
            }
            em.persist(entity);
            data.add(entity);
        }
    }

    @Test
    public void createContrato() throws BusinessLogicException {

        ContratoEntity newEntity = factory.manufacturePojo(ContratoEntity.class);

        //Set de relaciones
        newEntity.setZona(zonaTest);
        newEntity.setFranja(franjaTest);
        newEntity.setCliente(clienteTest);
        newEntity.setPaseador(paseadorTest);
        newEntity.setMascotas(mascotasTest);
        newEntity.setPago(pagoRealizadoTest);
        newEntity.setFinalizado(false);
        newEntity.setValorServicio(5000.0);

        ContratoEntity result = contratoLogic.createContrato(newEntity);
        Assert.assertNotNull(result);

        //Zona Coverage
        newEntity.setSatisfactorio(true);
        newEntity.setComentario(new ComentarioEntity());
        ComentarioEntity nuevoComentario = newEntity.getComentario();
        newEntity.setComentario(nuevoComentario);
        //Fin zoan coverage
        ContratoEntity entity = em.find(ContratoEntity.class, result.getId());
        Assert.assertEquals(result.getPaseador().getId(), entity.getPaseador().getId());
        Assert.assertEquals(result.getCliente().getId(), entity.getCliente().getId());
    }

    @Test(expected = BusinessLogicException.class)
    public void createContratoValorNeg() throws BusinessLogicException {
        ContratoEntity newEntity = factory.manufacturePojo(ContratoEntity.class);
        newEntity.setValorServicio(-1.0);
        ContratoEntity result = contratoLogic.createContrato(newEntity);
    }

    @Test(expected = BusinessLogicException.class)
    public void createContratoValorNull() throws BusinessLogicException {
        ContratoEntity newEntity = factory.manufacturePojo(ContratoEntity.class);
        newEntity.setValorServicio(null);
        ContratoEntity result = contratoLogic.createContrato(newEntity);
    }

//	@Test( expected = BusinessLogicException.class)
//	public void createContratoMascotasNull() throws BusinessLogicException{
//		ContratoEntity newEntity = factory.manufacturePojo(ContratoEntity.class);
//		newEntity.setMascotas(null);
//		ContratoEntity result = contratoLogic.createContrato(newEntity);
//	}
//        
//        
//	@Test( expected = BusinessLogicException.class)
//	public void createContratoZonaNull() throws BusinessLogicException{
//		ContratoEntity newEntity = factory.manufacturePojo(ContratoEntity.class);
//		newEntity.setZona(null);
//		ContratoEntity result = contratoLogic.createContrato(newEntity);
//	}
//	
//	
//	@Test( expected = BusinessLogicException.class)
//	public void createContratoFranjaNull() throws BusinessLogicException{
//		ContratoEntity newEntity = factory.manufacturePojo(ContratoEntity.class);
//		newEntity.setFranja(null);
//		ContratoEntity result = contratoLogic.createContrato(newEntity);
//	}
//	
//	
//	@Test( expected = BusinessLogicException.class)
//	public void createContratoPaseadorNull() throws BusinessLogicException{
//		ContratoEntity newEntity = factory.manufacturePojo(ContratoEntity.class);
//		newEntity.setPaseador(null);
//		ContratoEntity result = contratoLogic.createContrato(newEntity);
//	}
//	
//	
//	@Test( expected = BusinessLogicException.class)
//	public void createContratoClienteNull() throws BusinessLogicException{
//		ContratoEntity newEntity = factory.manufacturePojo(ContratoEntity.class);
//		newEntity.setCliente(null);
//		ContratoEntity result = contratoLogic.createContrato(newEntity);
//	}
//	
//	@Test( expected = BusinessLogicException.class)
//	public void createContratoPagoNull() throws BusinessLogicException{
//		ContratoEntity newEntity = factory.manufacturePojo(ContratoEntity.class);
//		newEntity.setPago(null);
//		ContratoEntity result = contratoLogic.createContrato(newEntity);
//	}
    @Test(expected = BusinessLogicException.class)
    public void createContratoValorNegativo() throws BusinessLogicException {
        ContratoEntity newEntity = factory.manufacturePojo(ContratoEntity.class);
        newEntity.setValorServicio(-10.0);
        ContratoEntity result = contratoLogic.createContrato(newEntity);
    }

    @Test(expected = BusinessLogicException.class)
    public void createContratoValorCero() throws BusinessLogicException {
        ContratoEntity newEntity = factory.manufacturePojo(ContratoEntity.class);
        newEntity.setValorServicio(0.0);
        ContratoEntity result = contratoLogic.createContrato(newEntity);
    }

    //ELIMINAR CONTRATO
    /**
     * Prueba para eliminar un Contrato
     *
     * @throws co.edu.uniandes.csw.paseadores.exceptions.BusinessLogicException
     */
    @Test
    public void deleteContratoTest() throws BusinessLogicException {
        ContratoEntity entity = data.get(0);
        contratoLogic.deleteContrato(entity.getId());
        ContratoEntity deleted = em.find(ContratoEntity.class, entity.getId());
        Assert.assertNull(deleted);
    }

    @Test(expected = BusinessLogicException.class)
    public void deleteContratoSinFinalizar() throws BusinessLogicException {
        ContratoEntity newEntity = data.get(2);
        contratoLogic.deleteContrato(newEntity.getId());
    }

    @Test(expected = BusinessLogicException.class)
    public void deleteContratoSinPagar() throws BusinessLogicException {
        ContratoEntity newEntity = data.get(1);
        contratoLogic.deleteContrato(newEntity.getId());
    }
    //OBTENER  CONTRATO
    /**
     * Prueba para consultar un Contrato.
     */
    @Test
    public void getContratoTest() {
        ContratoEntity entity = data.get(0);
        ContratoEntity resultEntity = contratoLogic.getContrato(entity.getId());
        Assert.assertNotNull(resultEntity);
        Assert.assertEquals(entity.getId(), resultEntity.getId());
        Assert.assertEquals(entity.getPaseador().getId(), resultEntity.getPaseador().getId());
    }

    /**
     * Prueba para consultar la lista de Contratos.
     */
    @Test
    public void getContratosTest() {
        List<ContratoEntity> list = contratoLogic.getContratos();
        Assert.assertEquals(data.size(), list.size());
        for (ContratoEntity entity : list) {
            boolean found = false;
            for (ContratoEntity storedEntity : data) {
                if (entity.getId().equals(storedEntity.getId())) {
                    found = true;
                    break;
                }
            }
            Assert.assertTrue(found);
        }
    }

    //ACTUALIZAR CONTRATO
    /**
     * Prueba para actualizar un Contrato.
     */
    @Test
    public void updateContratoTest() throws BusinessLogicException {
        ContratoEntity entity = data.get(0);
        ContratoEntity pojoEntity = factory.manufacturePojo(ContratoEntity.class);

        FranjaHorariaEntity franjaEntity = factory.manufacturePojo(FranjaHorariaEntity.class);
        PaseadorEntity paseadorEntity = factory.manufacturePojo(PaseadorEntity.class);
        ClienteEntity clienteEntity = factory.manufacturePojo(ClienteEntity.class);
        PagoEntity pagoEntity = factory.manufacturePojo(PagoEntity.class);

        pojoEntity.setId(entity.getId());

        pojoEntity.setFranja(franjaEntity);
        pojoEntity.setPaseador(paseadorEntity);
        pojoEntity.setCliente(clienteEntity);
        pojoEntity.setPago(pagoEntity);
        pojoEntity.setZona(zonaTest);
        pojoEntity.setMascotas(mascotasTest);

        contratoLogic.updateContrato(pojoEntity.getId(), pojoEntity);

        ContratoEntity resp = em.find(ContratoEntity.class, entity.getId());

        Assert.assertEquals(pojoEntity.getId(), resp.getId());

    }

    @Test(expected = BusinessLogicException.class)
    public void updateContratoValorNegativo() throws BusinessLogicException {
        ContratoEntity newEntity = factory.manufacturePojo(ContratoEntity.class);
        newEntity.setValorServicio(-10.0);
        contratoLogic.updateContrato(newEntity.getId(), newEntity);
    }

}
