package load

import com.bigdata.journal.Options
import com.bigdata.rdf.sail.BigdataSail
import org.openrdf.repository.Repository

import java.io.InputStreamReader
import java.util.Properties

/**
 * Will load the data from file.
 * @author Ailton LOPES MENDES
 * @author Jonathan CHU
 * @author Fabien LAMBERT--DELAVAQUERIE
 * @author Gérald LIN
 */
object Load {

    import com.bigdata.rdf.sail.BigdataSailRepository
    import org.openrdf.OpenRDFException
    import org.openrdf.query.{QueryLanguage, TupleQueryResult}
    import org.openrdf.repository.RepositoryConnection
    import org.openrdf.rio.RDFFormat

    import java.io.{BufferedInputStream, IOException, InputStream}

    /***
     * Will load the blazegraph with our file created.
     * Then will apply cons to the TupleQueryResult.
     * @param fileName the name of the file
     * @param query the sparql query
     * @param cons the function that we apply to the TupleQueryResult
     * @throws IOException
     * @throws OpenRDFException
     */
    @throws[IOException]
    @throws[OpenRDFException]
    def load(fileName : String, query : String, cons: TupleQueryResult => Unit): Unit = {

        val props = loadProperties("/noInference.properties")
        props.put(Options.FILE, "/tmp/blazegraph/test.jnl") // journal file location

        // instantiate a sail
        val sail = new BigdataSail(props)
        val repo = new BigdataSailRepository(sail)

        try {
            repo.initialize
            loadData(repo, fileName, "")
            val result = executeSelectQuery(repo, query, QueryLanguage.SPARQL)
            cons(result)
        } finally repo.shutDown


    }

    /*
     * Load a Properties object from a file.
     */ @throws[IOException]
    def loadProperties(resource: String): Properties = {
        val p = new Properties()
        val is: InputStream = getClass.getResourceAsStream(resource)
        p.load(new InputStreamReader(new BufferedInputStream(is)))
        p
    }

    /*
     * Load data from resources into a repository.
     */ @throws[OpenRDFException]
    @throws[IOException]
    def loadData(repo: Repository, resource: String, baseURL: String): Unit = {
        val cxn = repo.getConnection
        try {
            cxn.begin()
            try {
                val is = getClass.getResourceAsStream(resource)
                if (is == null) throw new IOException("Could not locate resource: " + resource)
                val reader = new InputStreamReader(new BufferedInputStream(is))
                try cxn.add(reader, baseURL, RDFFormat.RDFXML)
                finally reader.close
                cxn.commit()
            } catch {
                case ex: OpenRDFException =>
                    cxn.rollback()
                    throw ex
            }
        } finally {
            // close the repository connection
            cxn.close()
        }
    }

    /*
     * Execute sparql select query.
     */ @throws[OpenRDFException]
    def executeSelectQuery(repo: Repository, query: String, ql: QueryLanguage): TupleQueryResult = {
        var cxn : RepositoryConnection = null
        if (repo.isInstanceOf[BigdataSailRepository]) cxn = repo.asInstanceOf[BigdataSailRepository].getReadOnlyConnection
        else cxn = repo.getConnection
        try {
            val tupleQuery = cxn.prepareTupleQuery(ql, query)
            tupleQuery.setIncludeInferred(true /* includeInferred */)
            tupleQuery.evaluate
        } finally cxn.close()
    }

}
