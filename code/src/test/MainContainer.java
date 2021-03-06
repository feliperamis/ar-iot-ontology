package test;

import agents.*;
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
        final Profile profile = new ProfileImpl();
        profile.setParameter(Profile.CONTAINER_NAME, "SIDP3-2020");
        profile.setParameter(Profile.GUI, "true");

        final Runtime runtime = Runtime.instance();
        runtime.createMainContainer(new ProfileImpl());
        container = runtime.createAgentContainer(profile);

        String cameraName = "ARDeviceCamera";
        final AgentController sniffer = container.createNewAgent("Sniffer", Sniffer.class.getName(), null);
        final AgentController environment = container.createNewAgent("Environment", Environment.class.getName(), null);
        final AgentController camera = container.createNewAgent("Camera", Camara_p3.class.getName(), new Object[] {cameraName});
        final AgentController device = container.createNewAgent("Device", Device.class.getName(), new Object[] {cameraName});
        // TODO: Los sensores crean las unidades con el mismo nombre de individuo (debería tener algo random)
        final AgentController sensorRuido = container.createNewAgent("SensorRuido", SensorRuido_p3.class.getName(), null);
        final AgentController sensorPasos = container.createNewAgent("SensorPasos", SensorPasos_p3.class.getName(), null);
        final AgentController sensorTemperatura = container.createNewAgent("SensorTemperatura", Termometro_p3.class.getName(), null);

        sniffer.start();

        try {
            System.out.println("Add agents to sniffer and press enter to continue.");
            new BufferedReader(new InputStreamReader(System.in)).readLine();
        } catch (IOException ex) {
            ex.printStackTrace();
        }


        /* We make a sleep between agent start to avoid concurrent modifications on the ontology model */
        try {
            environment.start();
            Thread.sleep(1);

            camera.start();
            Thread.sleep(1);

            sensorPasos.start();
            Thread.sleep(1);

            sensorRuido.start();
            Thread.sleep(1);

            sensorTemperatura.start();
            Thread.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        device.start();
    }
}
