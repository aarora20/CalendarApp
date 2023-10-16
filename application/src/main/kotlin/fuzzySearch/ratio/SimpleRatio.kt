package fuzzySearch.ratio


import fuzzySearch.DiffUtils
import fuzzySearch.Ratio
import fuzzySearch.ToStringFunction
import kotlin.math.round

class SimpleRatio : Ratio {



    /**
     * Computes a simple Levenshtein distance ratio between the strings
     *
     * @param s1 Input string
     * @param s2 Input string
     * @return The resulting ratio of similarity
     */
    override fun apply(s1: String, s2: String): Int {

        return round(100 * DiffUtils.getRatio(s1, s2)).toInt()

    }

    override fun apply(s1: String, s2: String, sp: ToStringFunction<String>): Int {
        return apply(sp.apply(s1), sp.apply(s2))
    }
}