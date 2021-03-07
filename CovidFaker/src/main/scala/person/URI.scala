package person

object URI {
    val baseUri = "http://extension.group1.fr/onto#"

    val typeProperty = "http://www.w3.org/1999/02/22-rdf-syntax-ns#type"
    val persons = List("http://swat.cse.lehigh.edu/onto/univ-bench.owl#FullProfessor"
        ,
        "http://swat.cse.lehigh.edu/onto/univ-bench.owl#AssociateProfessor"
        ,
        "http://swat.cse.lehigh.edu/onto/univ-bench.owl#AssistantProfessor"
        ,
        "http://swat.cse.lehigh.edu/onto/univ-bench.owl#Lecturer"
        ,
        "http://swat.cse.lehigh.edu/onto/univ-bench.owl#UndergraduateStudent"
        ,
        "http://swat.cse.lehigh.edu/onto/univ-bench.owl#GraduateStudent")

}
