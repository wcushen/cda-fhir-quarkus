package org.fhir;

import jakarta.enterprise.context.ApplicationScoped;
import org.apache.camel.builder.RouteBuilder;

@ApplicationScoped
public class CDAToFHIRRoute extends RouteBuilder {
    @Override
    public void configure() throws Exception {
        log.info("CDAToFHIRRoute is being configured.");
        
        // Read files from the input directory, apply XSLT, and write to the output directory
        from("file:src/main/resources/input?noop=true") // Source directory
            .routeId("CDAToFHIRRoute")
            .to("xslt:transform/cda-to-fhir.xsl") // Apply XSLT transformation
            .process(exchange -> {
                // Get the transformed output as a String (XML)
                String xmlOutput = exchange.getIn().getBody(String.class);

                // Strip the XML tags to get the JSON
                // In this case, assuming the output from the XSLT is a valid JSON string
                String jsonOutput = stripXml(xmlOutput);
                
                // Set the stripped JSON as the new body
                exchange.getIn().setBody(jsonOutput);
            })
            .to("file:src/main/resources/output?fileName=${file:name.noext}-fhir.json"); // Output directory with modified filename
    }

    // Method to remove XML wrapper (you can adjust this as per the structure of your XSLT output)
    private String stripXml(String xml) {
        // Assuming the XML has an unwanted wrapper or you want to extract the content
        // A simple example is using regex or other methods to clean up unwanted XML structure.
        // Here we use a very basic example to just remove the XML declaration (<?xml ...?>)
        
        // You can enhance this to remove any other unwanted XML tags if necessary
        return xml.replaceAll("^<\\?xml.*?\\?>", "").trim();
    }
}
