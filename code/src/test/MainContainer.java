package test;

import agents.Camara_p3;
import agents.Device;
import agents.Environment;
import domain.OntologyDomain;
import domain.OntologyParser;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.tools.sniffer.Sniffer;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.StaleProxyException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;

public class MainContainer {
    private static AgentContainer container;

    public static void main(String[] args) throws StaleProxyException {
        try {
            final OntologyDomain domain = OntologyParser.parse();
        } catch (URISyntaxException e) {
            System.out.println("Error while parsing ontology");
        }
        System.out.println("" +
                "");
        final Profile profile = new ProfileImpl();
        profile.setParameter(Profile.CONTAINER_NAME, "SIDP3-2020");
        profile.setParameter(Profile.GUI, "true");

        final Runtime runtime = Runtime.instance();
        runtime.createMainContainer(new ProfileImpl());
        container = runtime.createAgentContainer(profile);

        final AgentController sniffer = container.createNewAgent("Sniffer", Sniffer.class.getName(), null);
        final AgentController environment = container.createNewAgent("Environment", Environment.class.getName(), null);
        final AgentController camera = container.createNewAgent("Camera", Camara_p3.class.getName(), null);
        final AgentController device = container.createNewAgent("Device", Device.class.getName(), null);

        sniffer.start();

        try {
            System.out.println("Add agents to sniffer and press enter to continue.");
            new BufferedReader(new InputStreamReader(System.in)).readLine();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        environment.start();
        camera.start();
        device.start();
    }
}