package agents;

import domain.WwtpDomain;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Device extends Agent {
    //AID Termostat;

    public static final Logger logger = Logger.getLogger("Device");
    //private static final WwtpDomain DOMAIN = WwtpDomain.getInstance();

    protected void setup() {
        System.out.println("hola");
        this.logger.info("Dispositivo iniciado");
        //this.logger.info(DOMAIN.toString());
    }

    public class SendMessageTickerBehaviour extends TickerBehaviour {
        public SendMessageTickerBehaviour(Agent var2, long var3) {
            super(var2, var3);
        }

        public void onTick() {
            int var1 = (int)(Math.random() * 40.0D) - 10;
            ACLMessage var2 = new ACLMessage(7);
            //var2.addReceiver(Device.this.Termostat);
            var2.setContent(Integer.toString(var1));
            Device.this.send(var2);
        }
    }
}