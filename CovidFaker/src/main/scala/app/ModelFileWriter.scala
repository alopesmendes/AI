package app

import org.apache.jena.rdf.model.Model

import java.io.FileOutputStream

object ModelFileWriter {
    /***
     * Writes the file in our resources directory. Will create the file if it does not exist.
     * @param fileName the name of the file.
     * @param model the Model.
     * @param lang how to write the file bug TTL, N3 and TURTLE do not work.
     */
    def write(fileName: String, model: Model, lang: String): Unit = {

        val out = new FileOutputStream(s"src/main/resources/$fileName")
        model.write(out, lang)
        out.flush()
        out.close()

    }
}
