package kafka

import person.Vaccine.SideEffects

/***
 * The class that will represent the values that will be parse and send
 * @param subject the String of the subject.
 * @param obj the String of the object.
 * @param sideEffect the SideEffects of a vaccine.
 */
case class ProducerValue(val subject : String, val obj : String, val sideEffect: SideEffects)
