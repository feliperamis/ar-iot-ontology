package agents;


import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.rdf.model.Property;
import domain.OntologyDomain;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;

import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * @author Alvaro Macias
 */
public class SensorRuido_p3 extends Agent {

    private static final Logger logger = Logger.getLogger("NoiseSensor");
    private static final OntologyDomain DOMAIN = OntologyDomain.getInstance();

    /* Entities */
    private static final String Entity_Location = "Location";
    private static final String Entity_NoiseSensor = "NoiseSensor";
    private static final String Entity_Coverage = "Coverage";
    private static final String Entity_Output = "Output";
    private static final String Entity_Quality = "Quality";
    private static final String Entity_Unit = "Unit";

    /* Object properties */
    private static final String ObjectProperty_hasOutput = "SensorOutput";
    private static final String ObjectProperty_hasCoverage = "SensorCoverage";
    private static final String ObjectProperty_hasQuality = "SensorQuality";
    private static final String ObjectProperty_hasUnit = "SensorUnit";
    private static final String ObjectProperty_hasLocation = "SensorLocation";


    /* Individuals */
    private static final String SensorName = "NoiseSensor1";
    private static final String LocationName = "Place";
    private static final String CoverageName = "EventHall";
    private static final String OutputName = "NoiseSensorOutput";
    private static final String QualityGrade = "NoiseSensorQuality";
    private static final String UnitType = "NoiseSensorUnit";

    AID Environment;

    public class SendMessageTickerBehaviour extends TickerBehaviour {
        public SendMessageTickerBehaviour(Agent a, long period) {
            super(a, period);
        }

        public void onTick() {
            ACLMessage message = new ACLMessage(ACLMessage.INFORM);
            message.addReceiver(Environment);
            message.setContent(SensorName);
            send(message);
        }
    }

    protected void setup() {

        AID df = getDefaultDF();
        DFAgentDescription register_template = new DFAgentDescription();
        register_template.setName(getAID());
        try {
            DFService.register(this, df, register_template);
        } catch (FIPAException ex) {
            Logger.getLogger(SensorRuido_p3.class.getName()).log(Level.SEVERE, null, ex);
        }

        DFAgentDescription busca = new DFAgentDescription();
        ServiceDescription servicio = new ServiceDescription();
        servicio.setType("Environment");
        busca.addServices(servicio);
        Environment = new AID();
        try {
            DFAgentDescription[] result = DFService.search(this, busca);
            if (result.length > 0)
                Environment = result[0].getName();
        } catch (FIPAException ex) {
            Logger.getLogger(SensorRuido_p3.class.getName()).log(Level.SEVERE, null, ex);
        }


        SendMessageTickerBehaviour Sensor = new SendMessageTickerBehaviour(this, 10000);
        this.addBehaviour(Sensor);
        this.initSensor();

    }

    private void initSensor() {
        logger.info("Creating camera and a 3d environment");
        Individual noiseSensor = DOMAIN.createIndividual(OntologyDomain.OntologyUri.ARIOT, Entity_NoiseSensor, SensorName);
        Individual location = DOMAIN.createIndividual(OntologyDomain.OntologyUri.ARIOT, Entity_Location, LocationName);
        Individual coverage = DOMAIN.createIndividual(OntologyDomain.OntologyUri.IOTLITE, Entity_Coverage, CoverageName);
        Individual output = DOMAIN.createIndividual(OntologyDomain.OntologyUri.NETSSN, Entity_Output, OutputName);
        Individual quality = DOMAIN.createIndividual(OntologyDomain.OntologyUri.LOA,Entity_Quality, QualityGrade);
        Individual unit = DOMAIN.createIndividual(OntologyDomain.OntologyUri.NETQU,Entity_Unit, UnitType);

        Property hasOutput = DOMAIN.getProperty(OntologyDomain.OntologyUri.NETSSN, ObjectProperty_hasOutput);
        Property hasCoverage = DOMAIN.getProperty(OntologyDomain.OntologyUri.IOTLITE, ObjectProperty_hasCoverage);
        Property hasQuality = DOMAIN.getProperty(OntologyDomain.OntologyUri.LOA, ObjectProperty_hasQuality);
        Property hasUnit = DOMAIN.getProperty(OntologyDomain.OntologyUri.IOTLITE, ObjectProperty_hasUnit);
        Property hasLocation = DOMAIN.getProperty(OntologyDomain.OntologyUri.LOA,ObjectProperty_hasLocation);


        noiseSensor.addProperty(hasOutput, output);
        noiseSensor.addProperty(hasCoverage, coverage);
        noiseSensor.addProperty(hasQuality, quality);
        noiseSensor.addProperty(hasUnit, unit);
        noiseSensor.addProperty(hasLocation, location);
    }
}
