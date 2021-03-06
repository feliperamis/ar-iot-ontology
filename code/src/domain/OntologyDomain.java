package domain;

import com.hp.hpl.jena.ontology.*;
import com.hp.hpl.jena.query.*;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.sparql.core.ResultBinding;
import model.Event;
import model.SensorUnit;

import java.io.FileNotFoundException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class OntologyDomain {

    private static OntologyDomain domain;

    public enum OntologyUri {
        ARIOT("http://www.semanticweb.org/alvaro/ontologies/2020/4/ar-iot-ontology"),
        WGS84_POS("http://www.w3.org/2003/01/geo/wgs84_pos"),
        NETQU("http://purl.org/NET/ssnx/qu/qu"),
        NETSSN("http://purl.org/NET/ssnx/ssn"),
        OCLC("http://purl.oclc.org/NET/ssnx/ssn"),
        LOA("http://www.loa-cnr.it/ontologies/DUL.owl"),
        TERMS("http://purl.org/dc/terms"),
        SKOS("http://www.w3.org/2004/02/skos/core"),
        RDFS("http://www.w3.org/2000/01/rdf-schema"),
        IOTLITE("http://purl.oclc.org/NET/UNIS/fiware/iot-lite"),
        NS("http://creativecommons.org/ns"),
        NS1("http://www.w3.org/2003/06/sw-vocab-status/ns"),
        OWL("http://www.w3.org/2002/07/owl"),
        RDF("http://www.w3.org/1999/02/22-rdf-syntax-ns"),
        XML("http://www.w3.org/XML/1998/namespace"),
        XSD("http://www.w3.org/2001/XMLSchema");

        private String uri;

        OntologyUri(String uri) {
            this.uri = uri;
        }

        String getUri() {
            return this.uri;
        }
    }

    public static OntologyDomain getInstance() {
        if (domain == null) {
            try {
                domain = OntologyParser.parse();
            } catch (URISyntaxException ex) {
                Logger.getLogger(OntologyDomain.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return domain;
    }

    private OntModel model;


    @Override
    public String toString() {
        return "Domain is instantiated, classes: " + getClasses();
    }

    public void setModel(OntModel model) {
        this.model = model;
    }

    public List<String> getClasses() {
        List<String> classes = new ArrayList<>();
        for (OntClass entity : model.listClasses().toList()) {
            classes.add(entity.toString());
        }

        return classes;
    }

    public Property getProperty(OntologyUri ontologyUri, String propertyName) {
        return model.getProperty(getUri(ontologyUri, propertyName));
    }

    public String getUri(OntologyUri ontologyUri, String propertyName) {
        return String.format("%s#%s", ontologyUri.getUri(), propertyName);
    }

    public void printPropertiesByClass() {
        System.out.println(model.listIndividuals().toList());
        for (OntClass ontClass : model.listNamedClasses().toList()) {
            System.out.println("Class: '" + ontClass.getURI() + "' has properties:");
            OntClass pizzaClass = model.getOntClass(ontClass.getURI());
            //List of ontology properties
            for (OntProperty property : ontClass.listDeclaredProperties().toList()) {
                System.out.println("    · Name :" + property.getLocalName());
                System.out.println("        · Domain :" + property.getDomain());
                System.out.println("        · Range :" + property.getRange());
                System.out.println("        · IsData :" + property.isDatatypeProperty());
                System.out.println("        · IsObject :" + property.isObjectProperty());
            }
        }
    }

    public List<Individual> getIndividualsByClass(OntologyUri ontologyUri, String className) {
        /* Mejor una consulta sparql */
        List<Individual> individualsInClass = new ArrayList<>();

        for (Individual individual : model.listIndividuals().toList()) {
            if (individual.hasOntClass(getUri(ontologyUri, className))) {
                individualsInClass.add(individual);
            }
        }
        return individualsInClass;
    }

    public void addInstances(String classUri, String className) {

        /* Usarlo como base para añadir nuevos individuos */
        System.out.println("   Adding instance to '" + className + "'");
        OntClass pizzaClass = model.getOntClass(classUri);
        Individual particularPizza = pizzaClass.createIndividual("The " + className + " I am eating right now");
        Property nameProperty = model.getProperty("<http://www.co-ode.org/ontologies/pizza/pizza.owl#hasPizzaName>");
        particularPizza.addProperty(nameProperty, "A yummy" + className);
    }

    public Object getPropertyValue(Individual individual, Property property, Class objectType) {
        return individual.getPropertyValue(property).as(objectType);
    }

    public Individual createIndividual(OntologyUri ontologyUri, String className, String individualName) {
        Individual individual = model.getIndividual(getUri(ontologyUri, individualName));
        if (individual == null) {
            OntClass entity = model.getOntClass(getUri(ontologyUri, className));
            individual = entity.createIndividual(getUri(ontologyUri, individualName));
        }


        return individual;
    }

    public Literal createLiteral(Object value) {
        return model.createTypedLiteral(value);
    }

    public Individual getIndividual(OntologyUri ontologyUri, String individualName) {
        return model.getIndividual(getUri(ontologyUri, individualName));
    }

    public void saveOntology() {
        try {
            OntologyParser.releaseOntology(this.model);
        } catch (FileNotFoundException e) {
            System.out.println("Error file not found: " + e.getMessage());
        }
    }

    public ArrayList<Event> queryFeatureOfInterestForLocation(String locationName) {
        String queryString = "PREFIX ariot: <http://www.semanticweb.org/alvaro/ontologies/2020/4/ar-iot-ontology#>\n"
        + "PREFIX oclc: <http://purl.oclc.org/NET/ssnx/ssn#>\n"
        + "PREFIX loa: <http://www.loa-cnr.it/ontologies/DUL.owl#>\n"
        + "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n"
        + "SELECT *\n"
        + "where {\n"
        + "?events a ?featuresOfInterest.\n"
        + "?featuresOfInterest rdfs:subClassOf oclc:FeatureOfInterest.\n"
        + "?events loa:hasLocation ariot:" + locationName + "}";

        //queryString = "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> PREFIX pizza: <http://www.co-ode.org/ontologies/pizza/pizza.owl#> SELECT ?Pizza ?PizzaBase ?PizzaTopping where {?Pizza a ?y. ?y rdfs:subClassOf pizza:Pizza. ?Pizza pizza:hasBase ?PizzaBase. ?Pizza pizza:hasTopping ?PizzaTopping. ?Pizza pizza:hasPizzaName?PizzaName. FILTER regex(?PizzaName, \"^My\") }";
        //queryString = "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> PREFIX pizza: <http://www.co-ode.org/ontologies/pizza/pizza.owl#> SELECT ?Pizza ?PizzaBase ?PizzaTopping where {?Pizza a ?y. ?y rdfs:subClassOf pizza:Pizza. ?Pizza pizza:hasBase ?PizzaBase. ?Pizza pizza:hasTopping ?PizzaTopping. ?Pizza pizza:hasPrice ?PizzaPrice. FILTER (?PizzaPrice < 20) }";

        Query query = QueryFactory.create(queryString);

        QueryExecution qe = QueryExecutionFactory.create(query, model);
        ResultSet results = qe.execSelect();

        ArrayList<Event> eventsInLocation = new ArrayList<>();
        for (Iterator iter = results; iter.hasNext(); ) {
            ResultBinding res = (ResultBinding)iter.next() ;
            Object eventType = res.get("featuresOfInterest") ;
            Object eventName= res.get("events") ;
            eventsInLocation.add(new Event(eventType.toString(), eventName.toString()));
        }
        qe.close() ;
        return eventsInLocation;
    }

    public ArrayList<SensorUnit> querySensorUnitForLocation(String locationName) {
        String queryString  = "PREFIX ariot: <http://www.semanticweb.org/alvaro/ontologies/2020/4/ar-iot-ontology#>\n"
        + "PREFIX oclc: <http://purl.oclc.org/NET/ssnx/ssn#>\n"
        + "PREFIX netqu: <http://purl.org/NET/ssnx/qu/qu#>\n"
        + "PREFIX iotlite: <http://purl.oclc.org/NET/UNIS/fiware/iot-lite#>\n"
        + "PREFIX loa: <http://www.loa-cnr.it/ontologies/DUL.owl#>\n"
        + "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n"
        + "SELECT *\n"
        + "where {\n"
        + "?SensorName a ?SensorType.\n"
        + "?SensorType rdfs:subClassOf oclc:SensingDevice.\n"
        + "?SensorName loa:hasLocation ariot:" + locationName + ".\n"
        + "?SensorName iotlite:hasQuantityKind ?Quantity.\n"
        + "?SensorName iotlite:hasUnit ?UnitType }";

        Query query = QueryFactory.create(queryString);

        QueryExecution qe = QueryExecutionFactory.create(query, model);
        ResultSet results = qe.execSelect();

        ArrayList<SensorUnit> sensorsInLocation = new ArrayList<>();
        for (Iterator iter = results; iter.hasNext(); ) {
            ResultBinding res = (ResultBinding)iter.next() ;
            Object sensorName = res.get("SensorName");
            Object sensorType = res.get("SensorType");
            Object quantityName = res.get("Quantity");
            Object unitType = res.get("UnitType");

            Individual quantity = getIndividual(OntologyUri.NETQU, quantityName.toString().split("#")[1]);
            Property hasQuantityValue = getProperty(OntologyUri.ARIOT, "quantityValue");

            sensorsInLocation.add(new SensorUnit(sensorName.toString().split("#")[1], sensorType.toString().split("#")[1],
                    quantity.getPropertyValue(hasQuantityValue).asLiteral().getFloat(), unitType.toString().split("#")[1]));
        }
        qe.close() ;
        return sensorsInLocation;
    }

/*
    Ejemplos sparql

    public void runSparqlQueryDataProperty()
    {

        String queryString = "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> "
                + "PREFIX pizza: <http://www.co-ode.org/ontologies/pizza/pizza.owl#> "
                + "SELECT ?Pizza ?PizzaName "
                + "where {"
                + " ?Pizza a ?y. "
                + " ?y rdfs:subClassOf pizza:Pizza. "
                + " ?Pizza pizza:hasPizzaName ?PizzaName"
                + "}";

        Query query = QueryFactory.create(queryString);

        QueryExecution qe = QueryExecutionFactory.create(query, model);
        ResultSet results = qe.execSelect();

        for ( Iterator iter = results ; iter.hasNext() ; )
        {
            ResultBinding res = (ResultBinding)iter.next() ;
            Object Pizza = res.get("Pizza") ;
            Object PizzaName = res.get("PizzaName") ;
            System.out.println("Pizza = "+ Pizza + " <- PizzaName -> " + PizzaName) ;
        }
        qe.close() ;
    }



    public void runSparqlQueryModify() {

        String queryString = "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> "
                + "PREFIX pizza: <http://www.co-ode.org/ontologies/pizza/pizza.owl#> "
                + "SELECT ?Pizza ?Eaten "
                + "where {?Pizza a ?y. "
                + "?y rdfs:subClassOf pizza:Pizza. "
                + "Optional {?Pizza pizza:Eaten ?Eaten}}";

        Query query = QueryFactory.create(queryString);

        QueryExecution qe = QueryExecutionFactory.create(query, model);
        ResultSet results = qe.execSelect();

        for ( Iterator iter = results ; iter.hasNext() ; )
        {
            ResultBinding res = (ResultBinding)iter.next() ;
            Object Pizza = res.get("Pizza") ;
            Object Eaten = res.get("Eaten") ;
            if (Eaten == null)
            {
                System.out.println("Pizza = "+ Pizza + " <-> false") ;
                Individual actualPizza = model.getIndividual(Pizza.toString());
                Property eatenProperty = model.getProperty("http://www.co-ode.org/ontologies/pizza/pizza.owl#Eaten");
                Literal rdfBoolean = model.createTypedLiteral(Boolean.valueOf("true"));
                actualPizza.addProperty(eatenProperty, rdfBoolean);
            }
            else
            {
                System.out.println("Pizza = "+ Pizza + " <-> " + Eaten) ;
            }
        }
        qe.close() ;
    }
    */
}
