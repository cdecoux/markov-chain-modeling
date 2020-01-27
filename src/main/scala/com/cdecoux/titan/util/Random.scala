package com.cdecoux.titan.util

import scala.annotation.tailrec
import scala.util.{Failure, Random, Success, Try}

object Random {

    def selectFromDistribution(probabilities: Array[Double]): Int = selectFromDistribution(probabilities.map(x => x.toFloat))

    @throws[RandomDistributionError]
    def selectFromDistribution(probabilities: Array[Float]): Int = {

        // Check for incorrect format for probability model
        if(probabilities.sum != 1.0) throw new RandomDistributionError("Probability values summation is not 1.0.")


        // Generate a random float (0, 1.000]
        lazy val num: Float = {
            new Random().nextFloat() match {
                case x: Float => if (x != 0) x else num
            }
        }

        // Define control flow for checking distribution and the random number
        @tailrec
        def attempt(probabilities: Array[Float], index: Int, acc: Float, random: Float): Try[Any] = {
            Try {
                acc + probabilities(index)
            } match {
                case Success(x: Float) => if (num < x) Success(index) else attempt(probabilities, index + 1, x, random)
                case Failure(exception) =>
                    println(exception.getMessage)
                    Failure(new RandomDistributionError("Failed to retrieve a random number.", exception.getCause))
            }
        }

        // Initiate control flow and evaluate return value
        attempt(probabilities, 0, 0, num).get match {
            case x: Int => x
            case e: Exception => throw e
        }
    }

    class RandomDistributionError(msg: String) extends Exception(msg: String) {
        def this(msg: String, cause: Throwable) {
            this(msg)
            initCause(cause)
        }
    }
}