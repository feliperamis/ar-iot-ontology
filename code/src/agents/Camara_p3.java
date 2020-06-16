package agents;

import com.hp.hpl.jena.ontology.DatatypeProperty;
import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.impl.LiteralImpl;
import domain.OntologyDomain;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.ParallelBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.NotUnderstoodException;
import jade.domain.FIPAAgentManagement.RefuseException;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.domain.FIPANames;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import jade.proto.AchieveREResponder;
import jade.util.Logger;
import model.Event;
import model.SensorUnit;

import java.util.ArrayList;
import java.util.Random;
import java.util.UUID;
import java.util.logging.Level;


public class Camara_p3 extends Agent {

    Individual camera;
    Individual environment3D;
    String eventBeingShown = "";
    private static final Logger logger = Logger.getJADELogger("Camera");
    private static final OntologyDomain DOMAIN = OntologyDomain.getInstance();

    /* Entities */
    private static final String Entity_3dEnvironment = "3DEnvironment";
    private static final String Entity_Camera = "Camera";
    private static final String Entity_3dPosition = "3DPosition";
    private static final String Entity_EventModel = "EventModel";


    /* Object properties */

    private static final String ObjectProperty_cameraRender = "cameraRender";
    public static final String ObjectProperty_pointingTo = "pointing_to";
    private static final String ObjectProperty_represents = "represents";
    private static final String ObjectProperty_generates = "generates";
    private static final String ObjectProperty_isRenderedIn = "isRenderedIn";
    private static final String ObjectProperty_hasLocation = "hasLocation";

    /* Data properties */
    private static final String DataProperty_objectXValue = "object_XValue";
    private static final String DataProperty_objectYValue = "object_YValue";
    private static final String DataProperty_objectZValue = "object_ZValue";


    /* Individuals */
    private static final String EnvironmentName = "Camera3DEnvironment";
    private static final String EventModelName = "EventModel";
    private static final String PositionName = "3DPosition";


    private class CamaraResponder extends AchieveREResponder {
        public CamaraResponder(Agent a, MessageTemplate mt) {
            super(a, mt);
        }

        protected ACLMessage prepareResultNotification(ACLMessage request, ACLMessage response) {
            ACLMessage informDone = request.createReply();
            informDone.setPerformative(ACLMessage.INFORM);
            //Event eventToShowOnCamera = (Event) request.getContentObject();
            return informDone;
        }
    }

    private class CameraResponse extends CyclicBehaviour {
        @Override
        public void action() {
            ACLMessage message = myAgent.receive();
            if (message != null) {
                Event eventToShowOnCamera = null;
                try {
                    eventToShowOnCamera = (Event) message.getContentObject();
                } catch (UnreadableException e) {
                    logger.warning("Error while reading event from ACL Message");
                }
                logger.info("Event to show on camera is: " + eventToShowOnCamera + ", and the one being shown now is: " + eventBeingShown);
                if (!eventToShowOnCamera.toString().equals(eventBeingShown)) {
                    String eventModelName = EventModelName + UUID.randomUUID();
                    Individual eventModel = DOMAIN.createIndividual(OntologyDomain.OntologyUri.ARIOT, Entity_EventModel, eventModelName);
                    Individual event = DOMAIN.getIndividual(OntologyDomain.OntologyUri.ARIOT, eventToShowOnCamera.getEventNameLabel());
                    Property eventModelRepresentsEvent = DOMAIN.getProperty(OntologyDomain.OntologyUri.ARIOT, ObjectProperty_represents);
                    eventModel.setPropertyValue(eventModelRepresentsEvent, event);

                    Property environmentGeneratesModel = DOMAIN.getProperty(OntologyDomain.OntologyUri.ARIOT, ObjectProperty_generates);
                    environment3D.setPropertyValue(environmentGeneratesModel, eventModel);

                    Individual position3D = DOMAIN.createIndividual(OntologyDomain.OntologyUri.ARIOT, Entity_3dPosition, PositionName + UUID.randomUUID());
                    Property xValue = DOMAIN.getProperty(OntologyDomain.OntologyUri.ARIOT, DataProperty_objectXValue);
                    Property yValue = DOMAIN.getProperty(OntologyDomain.OntologyUri.ARIOT, DataProperty_objectYValue);
                    Property zValue = DOMAIN.getProperty(OntologyDomain.OntologyUri.ARIOT, DataProperty_objectZValue);
                    Random random = new Random();

                    position3D.setPropertyValue(xValue, DOMAIN.createLiteral(random.nextFloat()));
                    position3D.setPropertyValue(yValue, DOMAIN.createLiteral(random.nextFloat()));
                    position3D.setPropertyValue(zValue, DOMAIN.createLiteral(random.nextFloat()));
                    Property renderedIn = DOMAIN.getProperty(OntologyDomain.OntologyUri.ARIOT, ObjectProperty_isRenderedIn);
                    eventModel.setPropertyValue(renderedIn, position3D);
                    eventBeingShown = eventToShowOnCamera.toString();

                    showEventOnDevice(eventToShowOnCamera, eventModelName, position3D, event);
                } else {
                    logger.info("Camera keeps showing " + eventToShowOnCamera.getEventNameLabel());
                }

            } else {
                block();
            }
        }
    };

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
        servicio.setType("Camera");
        servicio.setName(getLocalName());
        camara.addServices(servicio);
        camara.setName(getAID());
        try {
            DFService.register(this, df, camara);
        } catch (FIPAException ex) {
            Logger.getLogger(Camara_p3.class.getName()).log(Level.SEVERE, null, ex);
        }

        final ParallelBehaviour parallelBehaviour = new ParallelBehaviour(this, ParallelBehaviour.WHEN_ALL);
        this.initCamera();

        MessageTemplate mt = AchieveREResponder.createMessageTemplate((FIPANames.InteractionProtocol.FIPA_REQUEST));
//        parallelBehaviour.addSubBehaviour(new CamaraResponder(this, mt));
        parallelBehaviour.addSubBehaviour(new CameraResponse());
        parallelBehaviour.addSubBehaviour(new ChangeLocationBehaviour(this, 4000));

        this.addBehaviour(parallelBehaviour);
    }

    private void initCamera() {
        String cameraName = (String) this.getArguments()[0];
        logger.info("Creating camera and a 3d environment");
        logger.info("Camera name: " + cameraName);

        camera = DOMAIN.createIndividual(OntologyDomain.OntologyUri.ARIOT, Entity_Camera, cameraName);
        environment3D = DOMAIN.createIndividual(OntologyDomain.OntologyUri.ARIOT, Entity_3dEnvironment, EnvironmentName);
        Property cameraRender = DOMAIN.getProperty(OntologyDomain.OntologyUri.ARIOT, ObjectProperty_cameraRender);

        //A camera renders a 3DEnvironment
        camera.addProperty(cameraRender, environment3D);
    }


    private void showEventOnDevice(Event eventObject, String eventModelName, Individual position3D, Individual event) {
        String message = "\nNew EventModel " + eventModelName + " shown in the 3DEnvironment in augmented reality\n";
        message += "\tThis event is called " + eventObject.getEventNameLabel() + " and it's a " + eventObject.getEventNameLabel() + "\n";

        Property hasLocation = DOMAIN.getProperty(OntologyDomain.OntologyUri.LOA, ObjectProperty_hasLocation);
        Individual location = event.getPropertyValue(hasLocation).as(Individual.class);
        Property xValue = DOMAIN.getProperty(OntologyDomain.OntologyUri.ARIOT, DataProperty_objectXValue);
        Property yValue = DOMAIN.getProperty(OntologyDomain.OntologyUri.ARIOT, DataProperty_objectYValue);
        Property zValue = DOMAIN.getProperty(OntologyDomain.OntologyUri.ARIOT, DataProperty_objectZValue);

        message += "\tThis event is live in " + location.toString().split("#")[1] + " and it's rendered in (" + position3D.getPropertyValue(xValue).asLiteral().getFloat()
                + "x, " + position3D.getPropertyValue(yValue).asLiteral().getFloat() + "y, " + position3D.getPropertyValue(zValue).asLiteral().getFloat()
                + "z) in the AR environment\n";


        ArrayList<SensorUnit> sensorUnits = DOMAIN.querySensorUnitForLocation(location.toString().split("#")[1]);
        if (sensorUnits.isEmpty()) {
            message += "\t*There are no sensors in this location\n";
        } else {
            for (SensorUnit sensor : sensorUnits) {
                message += "\t*" + sensor + "\n";
            }
        }

        logger.info(message);
    }
}
