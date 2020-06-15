package agents;

import domain.WwtpDomain;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPANames;
import jade.lang.acl.ACLMessage;
import jade.proto.AchieveREInitiator;

import java.util.logging.Level;
import java.util.logging.Logger;

public class Device extends Agent {

    AID Environment;
    AID Camara;

    private class DeviceInitiator extends AchieveREInitiator{
        public DeviceInitiator(Agent a, ACLMessage mt){super (a,mt);}

        protected void handleInform(ACLMessage inform){
            ACLMessage message = new ACLMessage(ACLMessage.INFORM);
            message.addReceiver(Camara);
            message.setContent("Proyectame esta");
            send(message);
        }
    }

    public static final Logger logger = Logger.getLogger("Device");
    //private static final WwtpDomain DOMAIN = WwtpDomain.getInstance();

    protected void setup() {
        this.logger.info("Dispositivo iniciado");
        //this.logger.info(DOMAIN.toString());
        AID df = getDefaultDF();
        DFAgentDescription register_template = new DFAgentDescription();
        register_template.setName(getAID());
        try {
            DFService.register(this, df, register_template);
        } catch (FIPAException ex) {
            Logger.getLogger(Device.class.getName()).log(Level.SEVERE, null, ex);
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
            Logger.getLogger(Device.class.getName()).log(Level.SEVERE, null, ex);
        }
        servicio.setType("Camara");
        busca.addServices(servicio);
        Camara = new AID();
        try {
            DFAgentDescription[] result = DFService.search(this, busca);
            if (result.length>0)
                Camara = result[0].getName();
        } catch (FIPAException ex) {
            Logger.getLogger(Device.class.getName()).log(Level.SEVERE, null, ex);
        }

        Device.SendMessageTickerBehaviour Dispositivo = new Device.SendMessageTickerBehaviour(this, 10000);
        this.addBehaviour(Dispositivo);
    }

    public class SendMessageTickerBehaviour extends TickerBehaviour {
        public SendMessageTickerBehaviour(Agent var2, long var3) {
            super(var2, var3);
        }

        public void onTick() {
//            int var1 = (int)(Math.random() * 40.0D) - 10;
//            ACLMessage var2 = new ACLMessage(7);
//            //var2.addReceiver(Device.this.Termostat);
//            var2.setContent(Integer.toString(var1));
//            Device.this.send(var2);
            ACLMessage request = new ACLMessage(ACLMessage.REQUEST);
            request.setProtocol(FIPANames.InteractionProtocol.FIPA_REQUEST);
            request.addReceiver(Environment);
            myAgent.addBehaviour(new DeviceInitiator(myAgent,request));
        }
    }
}