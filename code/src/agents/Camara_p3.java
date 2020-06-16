package agents;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.ObjectProperty;
import com.hp.hpl.jena.rdf.model.Property;
import domain.OntologyDomain;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.domain.FIPANames;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.proto.AchieveREResponder;
import jade.util.Logger;

import java.util.Random;
import java.util.logging.Level;


public class Camara_p3 extends Agent {

    Individual camera;
    private static final Logger logger = Logger.getJADELogger("Camera");
    private static final OntologyDomain DOMAIN = OntologyDomain.getInstance();

    /* Entities */
    private static final String Entity_3dEnvironment = "3DEnvironment";
    private static final String Entity_Camera = "Camera";
    private static final String Entity_3dPosition = "3DPosition";

    /* Object properties */

    private static final String ObjectProperty_cameraRender = "cameraRender";
    private static final String ObjectProperty_pointingTo = "pointing_to";


    /* Individuals */
    private static final String EnvironmentName = "Camera3DEnvironment";



    private class CamaraResponder extends AchieveREResponder {
        public CamaraResponder(Agent a, MessageTemplate mt) {
            super(a, mt);
        }

        protected ACLMessage prepareResultNotification(ACLMessage request, ACLMessage response) {
            ACLMessage informDone = request.createReply();
            informDone.setPerformative(ACLMessage.INFORM);
            //TODO: Create model with event model que viene en la request
            informDone.setContent("Possss toy viendo esto ns aver dime tu");
            return informDone;
        }
    }

    public class ChangeLocationBehaviour extends TickerBehaviour {
        public ChangeLocationBehaviour(Agent a, long period) {
            super(a, period);
        }

        public void onTick() {
            Property cameraPointsTo = DOMAIN.getProperty(OntologyDomain.OntologyUri.ARIOT, ObjectProperty_pointingTo);
            String newLocation = "Location" + (new Random().nextInt(3) + 1);
            Individual location = DOMAIN.getIndividual(OntologyDomain.OntologyUri.ARIOT, newLocation);
            camera.setPropertyValue(cameraPointsTo, location);
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
        this.addBehaviour(new ChangeLocationBehaviour(this, 2000));
    }

    private void initCamera() {
        String cameraName = (String) this.getArguments()[0];
        logger.info("Creating camera and a 3d environment");
        logger.info("Camera name: " + cameraName);

        camera = DOMAIN.createIndividual(OntologyDomain.OntologyUri.ARIOT, Entity_Camera, cameraName);
        Individual environment3D = DOMAIN.createIndividual(OntologyDomain.OntologyUri.ARIOT, Entity_3dEnvironment, EnvironmentName);
        Property cameraRender = DOMAIN.getProperty(OntologyDomain.OntologyUri.ARIOT, ObjectProperty_cameraRender);

        //A camera renders a 3DEnvironment
        camera.addProperty(cameraRender, environment3D);
    }
}
