    package domain;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.ParallelBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.util.leap.Iterator;

import java.util.logging.Logger;

public class Environment extends Agent {

    private static final Logger LOGGER = Logger.getLogger("Environment");

    private static final WwtpDomain DOMAIN = WwtpDomain.getInstance();
    private static final int TIME_UNIT = DOMAIN.getTimeUnit();  // Time in s

    private final float CHANCE_OF_DETECTING_ILLEGAL_DISCHARGE = DOMAIN.getChanceOfDetectingIllegalDischarge();  // Chance in %
    private final float RELEASED_WATER_PER_TIME_UNIT = DOMAIN.getReleasedWaterPerTimeUnit();  // Volume in m3
    private final int SANCTION_PER_TON_DISCHARGED = DOMAIN.getSanctionPerTonDischarged();  // Monetary units per ton
    private final float WATER_RECEIVED_SOLIDS_CONCENTRATION = DOMAIN.getWaterReceivedSolidsConcentration();  // Concentration in g/m3
    private final float WATER_RECEIVED_VOLUME = DOMAIN.getWaterReceivedVolume();  // Volume in m3
    
    private float currentVolume = WATER_RECEIVED_VOLUME;  // Volume in m3
    private float currentConcentration = WATER_RECEIVED_SOLIDS_CONCENTRATION;  // Concentration in g/m3
    
    // DEBUG
    private float illegalDischargesDetected = 0;
    //

    @Override
    protected void setup() {
        final DFAgentDescription desc = new DFAgentDescription();
        desc.setName(getAID());

        final ServiceDescription sdesc = new ServiceDescription();
        sdesc.setName("Environment");
        sdesc.setType("Environment");
        desc.addServices(sdesc);

        try {
            DFService.register(this, getDefaultDF(), desc);
        } catch (FIPAException e) {
            e.printStackTrace();
        }

        final ParallelBehaviour parallelBehaviour = new ParallelBehaviour(this, ParallelBehaviour.WHEN_ALL);

        final TickerBehaviour tickerBehaviour = new TickerBehaviour(this, TIME_UNIT * 1000) {
            @Override
            protected void onTick() {
                
                LOGGER.info("ENVIRONMENT[currentVolume]: " + currentVolume);
                
                float currentMassOfPollutant = currentVolume * currentConcentration;  // Mass in g
                
                currentVolume += WATER_RECEIVED_VOLUME;  // Volume in m3
                float massOfPollutantReceived = WATER_RECEIVED_VOLUME * WATER_RECEIVED_SOLIDS_CONCENTRATION;  // Mass in g
                
                float totalMassOfPollutant = currentMassOfPollutant + massOfPollutantReceived;  // Mass in g
                
                currentConcentration = totalMassOfPollutant / currentVolume;  // Concentration in g/m3
                
                currentVolume = WATER_RECEIVED_VOLUME;  // Discharge the rest, otherwise it overflows
                
                LOGGER.info("ENVIRONMENT[currentConcentration]: " + currentConcentration);
                // DEBUG
                LOGGER.info("ENVIRONMENT[illegalDischargesDetected]: " + illegalDischargesDetected);
                //
            }
        };
        parallelBehaviour.addSubBehaviour(tickerBehaviour);

        final MessageTemplate mt = new MessageTemplate(new MessageTemplate.MatchExpression() {
            @Override
            public boolean match(ACLMessage msg) {
                try {
                    String content = msg.getContent();
                    String[] contentArray = content.split(" ");
                    String contentFirst = contentArray[0];
                    return contentFirst.equals("(discharge");
                } catch (Exception ex) {
                    return false;
                }
            }
        });
        final CyclicBehaviour requestResponder = new CyclicBehaviour(this) {
            @Override
            public void action() {
                final ACLMessage request = Environment.this.blockingReceive(mt);
                final AID sender = request.getSender();
                final ACLMessage reply = request.createReply();
                try {
                    final DFAgentDescription desc = new DFAgentDescription();
                    desc.setName(sender);
                    final DFAgentDescription[] search = DFService.search(Environment.this, getDefaultDF(), desc);
                    final Iterator services = search[0].getAllServices();
                    final ServiceDescription service = (ServiceDescription) services.next();
                    String content = request.getContent();
                    String[] contentArray = content.split(" ");
                    float volumeDischarged = Float.valueOf(contentArray[2]);
                    float concentrationDischarged = Float.valueOf(contentArray[4].replace(")", ""));
                    if (service.getType().equals("Industry") && 100 * Math.random() < CHANCE_OF_DETECTING_ILLEGAL_DISCHARGE) {
                        // DEBUG
                        illegalDischargesDetected += 1;
                        //
                        reply.setPerformative(ACLMessage.REQUEST);
                        reply.setContent("(sanction :cost " + volumeDischarged * SANCTION_PER_TON_DISCHARGED + ")");
                    } else {
                        reply.setPerformative(ACLMessage.INFORM);
                    }
                    Environment.this.send(reply);
                    float currentMassOfPollutant = currentVolume * currentConcentration;  // Mass in g
                    float massOfPollutantDischarged = volumeDischarged * concentrationDischarged;  // Mass in g
                    float totalMassOfPollutant = currentMassOfPollutant + massOfPollutantDischarged;  // Mass in g
                    currentVolume += volumeDischarged;  // Volume in m3
                    currentConcentration = totalMassOfPollutant / currentVolume;  // Concentration in g/m3
                } catch (FIPAException e) {
                    e.printStackTrace();
                }
            }
        };
        parallelBehaviour.addSubBehaviour(requestResponder);

        this.addBehaviour(parallelBehaviour);
    }
}
