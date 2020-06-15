package agents;

import com.hp.hpl.jena.ontology.Individual;
import domain.OntologyDomain;
import jade.core.AID;
import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.domain.FIPANames;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.proto.AchieveREResponder;

import java.util.logging.Level;
import java.util.logging.Logger;


public class Camara_p3 extends Agent {

    private static final Logger logger = Logger.getLogger("Camera");
    private static final OntologyDomain DOMAIN = OntologyDomain.getInstance();

    /* Entities */
    private static final String Entity_3dEnvironment = "3DEnvironment";
    private static final String Entity_Camera = "Camera";
    private static final String Entity_3dPosition = "3DPosition";

    /* Object properties */


    /* Individuals */
    private static final String CameraName = "ARDeviceCamera";
    private static final String EnvironmentName = "Camera3DEnvironment";



    private class CamaraResponder extends AchieveREResponder {
        public CamaraResponder(Agent a, MessageTemplate mt) {
            super(a, mt);
        }

        protected ACLMessage prepareResultNotification(ACLMessage request, ACLMessage response) {
            ACLMessage informDone = request.createReply();
            informDone.setPerformative(ACLMessage.INFORM);
            informDone.setContent("Possss toy viendo esto ns aver dime tu");
            return informDone;
        }
    }

    protected void setup() {

        AID df = getDefaultDF();
        DFAgentDescription camara = new DFAgentDescription();
        ServiceDescription servicio = new ServiceDescription();
        servicio.setType("Camara");
        servicio.setName(getLocalName());
        camara.addServices(servicio);
        camara.setName(getAID());
        try {
            DFService.register(this, df, camara);
        } catch (FIPAException ex) {
            Logger.getLogger(Camara_p3.class.getName()).log(Level.SEVERE, null, ex);
        }

        MessageTemplate mt = AchieveREResponder.createMessageTemplate((FIPANames.InteractionProtocol.FIPA_REQUEST));
        this.addBehaviour(new CamaraResponder(this, mt));
        this.initCamera();
    }

    private void initCamera() {
        /* Check if camera and 3d environment exists */
        /* Crear un 3d environment */
        logger.info(DOMAIN.toString());
        Individual camera = DOMAIN.getIndividual(OntologyDomain.OntologyUri.ARIOT, CameraName);
        if (camera == null) {
            logger.info ("Es null");
            camera = DOMAIN.createIndividual(OntologyDomain.OntologyUri.ARIOT, Entity_Camera, CameraName);
            logger.info(Boolean.toString(camera == null));
            logger.info(camera.toString());
            logger.info(camera.getURI());
            logger.info(camera.getOntClass().getURI());
            logger.info(DOMAIN.getIndividualsByClass(OntologyDomain.OntologyUri.ARIOT, Entity_Camera).toString());
        } else {
            logger.info ("No es null");
            logger.info(camera.toString());
            logger.info(camera.getURI());
            logger.info(camera.getOntClass().getURI());
        }
    }
}
