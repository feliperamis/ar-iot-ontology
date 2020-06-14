package agents;


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
public class Termometro_p3 extends Agent {

    AID Environment;
    public class SendMessageTickerBehaviour extends TickerBehaviour {
        public SendMessageTickerBehaviour(Agent a, long period) {
            super(a, period);
        }

        public void onTick() {
            int t = (int)(Math.random()*40)-10;
            ACLMessage message = new ACLMessage(ACLMessage.INFORM);
            message.addReceiver(Environment);
            message.setContent(Integer.toString(t));
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
            Logger.getLogger(Termometro_p3.class.getName()).log(Level.SEVERE, null, ex);
        }

        DFAgentDescription busca = new DFAgentDescription();
        ServiceDescription servicio  = new ServiceDescription();
        servicio.setType( "Environment" );
        busca.addServices(servicio);
        Environment = new AID();
        try {
            DFAgentDescription[] result = DFService.search(this, busca);
            if (result.length>0)
                Environment = result[0].getName();
        } catch (FIPAException ex) {
            Logger.getLogger(Termometro_p3.class.getName()).log(Level.SEVERE, null, ex);
        }


        SendMessageTickerBehaviour Sensor = new SendMessageTickerBehaviour(this, 10000);
        this.addBehaviour(Sensor);

    }
}
