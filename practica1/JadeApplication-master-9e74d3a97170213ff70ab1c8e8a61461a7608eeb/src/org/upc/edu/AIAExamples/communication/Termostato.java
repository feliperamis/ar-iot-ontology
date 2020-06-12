package org.upc.edu.AIAExamples.communication;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
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
public class Termostato extends Agent {

    public class Receiver extends CyclicBehaviour {
        public Receiver() { }
        public void action() {
            ACLMessage message = myAgent.receive();
            if (message != null) {
                String msg = message.getContent();
                int t = Integer.parseInt(msg);
                if (t >= 15 & t <= 25) {
                    System.out.println("temperatura adecuada");
                } else if (t > 25) {
                    System.out.println("refrigeracion encendida");
                } else {
                    System.out.println("calefaccion encendida");
                }
            } else {
                block();
            }
        }
    }

    protected void setup() {
        AID df = getDefaultDF();
        DFAgentDescription termostato = new DFAgentDescription();
        ServiceDescription servicio  = new ServiceDescription();
        servicio.setType( "Termostato" );
        servicio.setName( getLocalName() );
        termostato.addServices(servicio);
        termostato.setName(getAID());
        try {
            DFService.register(this, df, termostato);
        } catch (FIPAException ex) {
            Logger.getLogger(Termometro.class.getName()).log(Level.SEVERE, null, ex);
        }
        Receiver Termostat = new Receiver();
        this.addBehaviour(Termostat);
    }
}
