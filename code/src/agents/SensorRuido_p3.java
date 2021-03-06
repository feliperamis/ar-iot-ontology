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

import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * @author Alvaro Macias
 */
public class SensorRuido_p3 extends Agent {

    Individual sensorRuido;

    private static final Logger logger = Logger.getLogger("NoiseSensor");
    private static final OntologyDomain DOMAIN = OntologyDomain.getInstance();

    /* Entities */
    private static final String Entity_Location = "Location";
    private static final String Entity_NoiseSensor = "NoiseSensor";
    private static final String Entity_Coverage = "Coverage";
    private static final String Entity_Quantity = "QuantityKind";
    private static final String Entity_Quality = "Quality";
    private static final String Entity_Unit = "Unit";

    /* Object properties */
    private static final String ObjectProperty_hasQuantityKind = "hasQuantityKind";
    private static final String ObjectProperty_hasCoverage = "hasCoverage";
    private static final String ObjectProperty_hasQuality = "hasQuality";
    private static final String ObjectProperty_hasUnit = "hasUnit";
    private static final String ObjectProperty_hasLocation = "hasLocation";

    /* Data properties */
    private static final String DataProperty_quantityValue = "quantityValue";


    /* Individuals */
    private static final String SensorName = "SensorRuido";
    private final String LocationName = "Location" + (new Random().nextInt(3) + 1);
    private static final String CoverageName = "EventHall";
    private static final String OutputName = "NoiseSensorOutput";
    private static final String QualityGrade = "NoiseSensorQuality";
    private static final String UnitType = "Decibels";

    AID Environment;

    public class SendMessageTickerBehaviour extends TickerBehaviour {
        public SendMessageTickerBehaviour(Agent a, long period) {
            super(a, period);
        }

        public void onTick() {
            ACLMessage message = new ACLMessage(ACLMessage.INFORM);
            message.addReceiver(Environment);

            Property hasQuantity = DOMAIN.getProperty(OntologyDomain.OntologyUri.IOTLITE, ObjectProperty_hasQuantityKind);
            Property quantityValue = DOMAIN.getProperty(OntologyDomain.OntologyUri.ARIOT, DataProperty_quantityValue);
            Individual quantity = sensorRuido.getPropertyValue(hasQuantity).as(Individual.class);
            quantity.setPropertyValue(quantityValue, DOMAIN.createLiteral(Math.abs(new Random().nextFloat() * 35)));

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


        SendMessageTickerBehaviour Sensor = new SendMessageTickerBehaviour(this, 7000);
        this.addBehaviour(Sensor);
        this.initSensor();
    }

    private void initSensor() {
        sensorRuido = DOMAIN.createIndividual(OntologyDomain.OntologyUri.ARIOT, Entity_NoiseSensor, SensorName);
        logger.info("NoiseSensor generated at " + LocationName);

        Individual location = DOMAIN.createIndividual(OntologyDomain.OntologyUri.ARIOT, Entity_Location, LocationName);
        Individual coverage = DOMAIN.createIndividual(OntologyDomain.OntologyUri.IOTLITE, Entity_Coverage, CoverageName);
        Individual quantity = DOMAIN.createIndividual(OntologyDomain.OntologyUri.NETQU, Entity_Quantity, OutputName);
        Individual quality = DOMAIN.createIndividual(OntologyDomain.OntologyUri.LOA,Entity_Quality, QualityGrade);
        Individual unit = DOMAIN.createIndividual(OntologyDomain.OntologyUri.NETQU,Entity_Unit, UnitType);

        Property hasQuantity = DOMAIN.getProperty(OntologyDomain.OntologyUri.IOTLITE, ObjectProperty_hasQuantityKind);
        Property hasCoverage = DOMAIN.getProperty(OntologyDomain.OntologyUri.IOTLITE, ObjectProperty_hasCoverage);
        Property hasQuality = DOMAIN.getProperty(OntologyDomain.OntologyUri.LOA, ObjectProperty_hasQuality);
        Property hasUnit = DOMAIN.getProperty(OntologyDomain.OntologyUri.IOTLITE, ObjectProperty_hasUnit);
        Property hasLocation = DOMAIN.getProperty(OntologyDomain.OntologyUri.LOA,ObjectProperty_hasLocation);

        sensorRuido.addProperty(hasQuantity, quantity);
        sensorRuido.addProperty(hasCoverage, coverage);
        sensorRuido.addProperty(hasQuality, quality);
        sensorRuido.addProperty(hasUnit, unit);
        sensorRuido.addProperty(hasLocation, location);
    }
}
