package org.upc.edu.AIAExamples.communication;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.domain.FIPANames;
import jade.lang.acl.ACLMessage;
import jade.proto.AchieveREInitiator;

import java.util.logging.Level;
import java.util.logging.Logger;

public class Termostato_Extension extends Agent{

    AID Termometre;

    private class TermoInitiator extends AchieveREInitiator {
        public TermoInitiator(Agent a, ACLMessage mt) {
            super(a, mt);
        }

        protected void handleInform(ACLMessage inform) {
            String msg = inform.getContent();
            int t = Integer.parseInt(msg);
            if (t >= 15 & t <= 25) {
                System.out.println("temperatura adecuada");
            } else if (t > 25) {
                System.out.println("refrigeracion encendida");
            } else {
                System.out.println("calefaccion encendida");
            }
        }
    }
    public class TickerRequest extends TickerBehaviour {
        public TickerRequest(Agent a, long period) {
            super(a, period);
        }

        public void onTick() {
            ACLMessage request = new ACLMessage(ACLMessage.REQUEST);
            request.setProtocol(FIPANames.InteractionProtocol.FIPA_REQUEST);
            request.addReceiver(Termometre);
            myAgent.addBehaviour(new TermoInitiator(myAgent, request));
        }
    }


    protected void setup() {

        AID df = getDefaultDF();
        DFAgentDescription register_template = new DFAgentDescription();
        register_template.setName(getAID());
        try {
            DFService.register(this, df, register_template);
        } catch (FIPAException ex) {
            Logger.getLogger(Termostato_Extension.class.getName()).log(Level.SEVERE, null, ex);
        }

        DFAgentDescription busca = new DFAgentDescription();
        ServiceDescription servicio  = new ServiceDescription();
        servicio.setType( "Termometro" );
        busca.addServices(servicio);
        Termometre = new AID();
        try {
            DFAgentDescription[] result = DFService.search(this, busca);
            if (result.length>0)
                Termometre = result[0].getName();
        } catch (FIPAException ex) {
            Logger.getLogger(Termostato_Extension.class.getName()).log(Level.SEVERE, null, ex);
        }

        Termostato_Extension.TickerRequest Termostat = new Termostato_Extension.TickerRequest(this, 10000);
        this.addBehaviour(Termostat);

    }
}
