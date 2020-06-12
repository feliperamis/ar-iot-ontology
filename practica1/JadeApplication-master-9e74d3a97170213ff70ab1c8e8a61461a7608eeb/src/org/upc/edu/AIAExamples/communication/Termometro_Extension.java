package org.upc.edu.AIAExamples.communication;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
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


public class Termometro_Extension extends Agent {

    private class TermoResponder extends AchieveREResponder {
        public TermoResponder(Agent a, MessageTemplate mt) {
            super(a, mt);
        }

        protected ACLMessage prepareResultNotification (ACLMessage request, ACLMessage response){
            int t = (int) (Math.random() * 40) - 10;
            ACLMessage informDone = request.createReply();
            informDone.setPerformative(ACLMessage.INFORM);
            informDone.setContent(Integer.toString(t));
            return informDone;
        }

    }

    protected void setup() {

        AID df = getDefaultDF();
        DFAgentDescription termometro = new DFAgentDescription();
        ServiceDescription servicio  = new ServiceDescription();
        servicio.setType( "Termometro" );
        servicio.setName( getLocalName() );
        termometro.addServices(servicio);
        termometro.setName(getAID());
        try {
            DFService.register(this, df, termometro);
        } catch (FIPAException ex) {
            Logger.getLogger(Termometro.class.getName()).log(Level.SEVERE, null, ex);
        }

        MessageTemplate mt = AchieveREResponder.createMessageTemplate((FIPANames.InteractionProtocol.FIPA_REQUEST));
        this.addBehaviour(new Termometro_Extension.TermoResponder(this, mt));
    }
}
