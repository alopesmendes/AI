import org.apache.jena.rdf.model.Model

import java.io.{FileWriter, IOException}

object ModelFileWriter {
    def write(fileName : String, model : Model) : Unit = {
        val out = new FileWriter(fileName)
        try model.write(out, "RDF/XML-ABBREV")
        finally try out.close()
        catch {
            case closeException: IOException => out.close()
        }

    }
}
