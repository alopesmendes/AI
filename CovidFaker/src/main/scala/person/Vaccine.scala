package person

import person.URI.baseUri

import scala.util.Random

trait Vaccine {
    val percentage : Double
    val name : String
}

object Vaccine {
    val random = new Random()

    case object Pfizer extends Vaccine {
        override val percentage: Double = 35
        override val name: String = "Pfizer"
    }

    case object Moderna extends Vaccine {
        override val percentage: Double = 15
        override val name: String = "Moderna"
    }

    case object AstraZeneca extends Vaccine {
        override val percentage: Double = 60
        override val name: String = "AstraZeneca"
    }

    case object SpoutnikV extends Vaccine {
        override val percentage: Double = 42
        override val name: String = "SpoutnikV"
    }

    case object CanSinoBio extends Vaccine {
        override val percentage: Double = 77
        override val name: String = "CanSinoBio"
    }

    case object Nil extends Vaccine {
        override val percentage: Double = 0
        override val name: String = "Nil"
    }

    trait SideEffects {
        val name : String
        val sideCode : String
    }

    object SideEffects {
        case object InjectionSitePain extends SideEffects {
            override val name: String = "injection site pain"
            override val sideCode: String = "C0151828"
        }

        case object Fatigue extends SideEffects {
            override val name: String = "fatigue"
            override val sideCode: String = "C0015672"
        }

        case object Headache extends SideEffects {
            override val name: String = "headache"
            override val sideCode: String = "C0018681"
        }

        case object MusclePain extends SideEffects {
            override val name: String = "muscle pain"
            override val sideCode: String = "C0231528"
        }

        case object Chills extends SideEffects {
            override val name: String = "chills"
            override val sideCode: String = "C0085593"
        }

        case object JointPain extends SideEffects {
            override val name: String = "joint pain"
            override val sideCode: String = "C0003862"
        }

        case object Fever extends SideEffects {
            override val name: String = "fever"
            override val sideCode: String = "C0015967"
        }

        case object InjectionSiteSwelling extends SideEffects {
            override val name: String = "injection site swelling"
            override val sideCode: String = "C0151605"
        }

        case object InjectionSiteRedness extends SideEffects {
            override val name: String = "injection site redness"
            override val sideCode: String = "C0852625"
        }

        case object Nausea extends SideEffects {
            override val name: String = "nausea"
            override val sideCode: String = "C0027497"
        }

        case object Malaise extends SideEffects {
            override val name: String = "malaise"
            override val sideCode: String = "C0231218"
        }

        case object Lymphadenopathy extends SideEffects {
            override val name: String = "lymphadenopathy"
            override val sideCode: String = "C0497156"
        }

        case object InjectionSiteTenderness extends SideEffects {
            override val name: String = "injection site tenderness"
            override val sideCode: String = "C0863083"
        }

        case object Nil extends SideEffects {
            override val name: String = ""
            override val sideCode: String = ""
        }

        def hasSideEffect(vaccine: Vaccine) : Boolean = random.nextInt(100) <= vaccine.percentage

        private def seqOf() : Seq[SideEffects] = Seq(
            InjectionSitePain, Fatigue, Headache, MusclePain, Chills, JointPain,
            Fever, InjectionSiteSwelling, InjectionSiteRedness, Nausea, Malaise,
            Lymphadenopathy, InjectionSiteTenderness
        )

        def randomSideEffect(vaccine: Vaccine) : SideEffects = {
            if (hasSideEffect(vaccine)) {
                val seq = seqOf()
                seq(random.nextInt(seq.size))
            } else {
                Nil
            }

        }
    }


    private def seqOf() : Seq[Vaccine] = Seq(Pfizer, Moderna, AstraZeneca, SpoutnikV, CanSinoBio)

    private def isVaccine() : Boolean = random.nextInt(100) <= 69

    /***
     * Selects a random vaccine if vaccinated otherwise returns Nil
     * @return Vaccine
     */
    def randomVaccine() : Vaccine = {
        if (isVaccine()) {
            val seq = seqOf()
            seq(random.nextInt(seq.size))
        } else {
            Nil
        }
    }

    /***
     * Returns the URI for a Vaccine.
     * @param vaccine a Vaccine
     * @return a String of a URI
     */
    def toUri(vaccine: Vaccine): String = {
        s"$baseUri${vaccine.name}"
    }

    /***
     * Will find the vaccine of an URL. If it odes not exist return Nil
     * Otherwise the corresponding Vaccine.
     * @param url the String URL of the Vaccine we are looking for.
     * @return Vaccine
     */
    def findVaccine(url : String): Vaccine = {
        url match {
            case s"${URI.baseUri}Pfizer" => Pfizer
            case s"${URI.baseUri}Moderna" => Moderna
            case s"${URI.baseUri}AstraZeneca" => AstraZeneca
            case s"${URI.baseUri}SpoutnikV" => SpoutnikV
            case s"${URI.baseUri}CanSinoBio" => CanSinoBio
            case _ => Nil
        }
    }
}
