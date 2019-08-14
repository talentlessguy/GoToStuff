import StringE.*
 
sealed class StringE {
    class Str(val value: String) : StringE()
    class Error(val value: String) : StringE()
 
    override fun toString(): String {
        return when (this) {
            is Str -> value
            is Error -> value
        }
    }
}
 
abstract class Parser {
    abstract fun parse(string: StringE): StringE
}
 
class ParseChar(val char: Char) : Parser() {
    override fun parse(input: StringE): StringE {
        return when (input) {
            is Str -> if (input.value.isNotEmpty()) {
                if (input.value[0] == char) {
                    Str(input.value.substring(1))
                } else {
                    Error(input.value + "\nparse failed")
                }
            } else {
                Error(input.value + "\nempty")
            }
            is Error -> Error(input.value)
        }
    }
}
 
class Parse123: Parser() {
    override fun parse(string: StringE): StringE {
 
        var a = ParseChar('1').parse(string)
        var b = ParseChar('2').parse(a)
 
        return ParseChar('3').parse(b)
    }
}
 
fun compose(p1: Parser, p2: Parser, s: StringE): StringE {
    var tryParse1 = p1.parse(s)
    var tryParse2 = p2.parse(tryParse1)
    var parsed: StringE
 
    when (tryParse1) {
        is Str -> {
            parsed = tryParse1
        }
        is Error -> {
            return Error(tryParse1.value)
        }
    }
    when (tryParse2) {
        is Str -> {
            parsed = tryParse2
        }
        is Error -> {
            return Error(tryParse2.value)
        }
    }
    return parsed
}
 
fun main() {
    val p1 = ParseChar('h')
    val p2 = Parse123()
    val parsedStuff = compose(p1, p2, Str("h123abc"))
    print(parsedStuff)
}