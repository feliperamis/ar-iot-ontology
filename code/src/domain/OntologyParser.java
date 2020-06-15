package domain;

import com.hp.hpl.jena.ontology.OntDocumentManager;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.util.FileManager;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.logging.Logger;

public class OntologyParser {

    private static final String PATH_TO_ONTOLOGY = "./ar-iot-ontology-merged.owl";
    private static final Logger logger = Logger.getLogger("OntologyParser");

    public static OntologyDomain parse() throws URISyntaxException {
        OntologyDomain domain = new OntologyDomain();
        OntModel model = ModelFactory.createOntologyModel(OntModelSpec.OWL_DL_MEM_TRANS_INF);
        System.out.println("· Loading Ontology");
        OntDocumentManager dm = model.getDocumentManager();
        dm.addAltEntry("http://www.semanticweb.org/alvaro/ontologies/2020/4/ar-iot-ontology","file:" + PATH_TO_ONTOLOGY);
        model.read("http://www.semanticweb.org/alvaro/ontologies/2020/4/ar-iot-ontology");
        /*
        InputStream ontInputStream = FileManager.get().open(PATH_TO_ONTOLOGY);
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
*/
        domain.setModel(model);
        return domain;
    }

    public static void releaseOntology(OntModel model) throws FileNotFoundException {
        System.out.println("· Releasing Ontology");
        if (!model.isClosed()) {
            model.write(new FileOutputStream(PATH_TO_ONTOLOGY, true));
            model.close();
        }
    }

    public static void main(String[] args) throws URISyntaxException {
        OntologyDomain domain = OntologyParser.parse();
        System.out.println(domain.getClasses());
    }
}
