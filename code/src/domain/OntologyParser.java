package domain;

import com.hp.hpl.jena.ontology.*;
import com.hp.hpl.jena.rdf.model.InfModel;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.reasoner.Reasoner;
import com.hp.hpl.jena.reasoner.ReasonerRegistry;
import com.hp.hpl.jena.util.FileManager;

import java.io.*;
import java.net.URISyntaxException;
import java.util.logging.Logger;

public class OntologyParser {

    private static final String PATH_TO_ONTOLOGY = "ar-iot-ontology-merged.owl";
    private static final Logger logger = Logger.getLogger("OntologyParser");

    public static WwtpDomain parse() throws URISyntaxException {
        WwtpDomain domain = new WwtpDomain();
        OntModel model = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM);
        //InputStream inputStream = FileManager.get().open(String.valueOf(OntologyParser.class.getClassLoader();
        InputStream ontInputStream = FileManager.get().open(PATH_TO_ONTOLOGY);
        System.out.println(ontInputStream);
        try {
            if (ontInputStream == null) {
                System.out.println("No input file Found");
            } else {
                //ontInputStream = FileManager.get().open(PATH_TO_ONTOLOGY);
                model.read(ontInputStream, null);
            }
        } catch (Exception e) {
            logger.warning("Error while reading the ontology: \n " + e.getMessage());
        }

        domain.setModel(model);
        return domain;
    }

    public void releaseOntology(OntModel model) throws FileNotFoundException {
        System.out.println("· Releasing Ontology");
        if (!model.isClosed()) {
            model.write(new FileOutputStream(PATH_TO_ONTOLOGY, true));
            model.close();
        }
    }

    public static void main(String[] args) throws URISyntaxException {
        WwtpDomain domain = OntologyParser.parse();
        System.out.println(domain.getClasses());
        domain.printPropertiesByClass();
    }
}
