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

    def isVaccin(vaccine: Vaccine): Boolean = {
        val number = random.nextInt(100)
        number <= vaccine.percentage
    }

    def seqOf() : Seq[Vaccine] = Seq(Pfizer, Moderna, AstraZeneca, SpoutnikV, CanSinoBio)

    def randomVaccine() : Vaccine = {
        val seq = seqOf()
        seq(random.nextInt(seq.size))
    }
}
