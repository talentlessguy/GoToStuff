import StringE.*
import kotlin.reflect.typeOf

sealed class StringE<T> {
    class Str<T>(val value: String, val struct: T) : StringE<T>()
    class Error<T>(val value: String) : StringE<T>()

    override fun toString(): String {
        return when (this) {
            is Str -> value
            is Error -> value
        }
    }
}

abstract class Parser<T> {
    abstract fun parse(string: StringE<T>): StringE<T>
}

class ParseChar<T>() : Parser<T>() {
    override fun parse(input: StringE<T>): StringE<T> {
        return when (input) {
            is Str -> if (input.value.isNotEmpty()) {
                if (!input.value[0].isDigit()) {
                    Str(("" + input.value).substring(1), input.struct)
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

class ParseCustomChar<T>(val char: Char) : Parser<T>() {
    override fun parse(input: StringE<T>): StringE<T> {
        return when (input) {
            is Str -> if (input.value.isNotEmpty()) {
                if (input.value[0] == char) {
                    Str(("" + input.value).substring(1), input.struct)
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

class Compose<T>(val p1: Parser<T>, val p2: Parser<T>) : Parser<T>() {
    override fun parse(string: StringE<T>): StringE<T> {
        return p2.parse(p1.parse(string))
    }
}

class Alternative<T>(val p1: Parser<T>, val p2: Parser<T>) : Parser<T>() {
    override fun parse(string: StringE<T>): StringE<T> {
        val r = p1.parse(string)
        return when (r) {
            is Str -> r
            is Error -> p2.parse(string)
        }
    }
}

class Repeat<T>(val p: Parser<T>) : Parser<T>() {
    override fun parse(string: StringE<T>): StringE<T> {
        val r = p.parse(string)
        return when (r) {
            is Str -> parse(r)
            is Error -> string
        }
    }
}

class Optional<T>(val p: Parser<T>) : Parser<T>() {
    override fun parse(string: StringE<T>): StringE<T> {
        val r = p.parse(string)

        return when (r) {
            is Str -> r
            is Error -> string
        }
    }
}

class ParseDigit: Parser<Int>() {
    override fun parse(input: StringE<Int>): StringE<Int> {
        return when (input) {
            is Str -> if (input.value.isNotEmpty()) {
                if (input.value[0].isDigit()) {
                    Str(("" + input.value).substring(1), input.struct * 10 + (input.value[0].toInt() - '0'.toInt()))
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

class ParseInt: Parser<Int>() {
    override fun parse(string: StringE<Int>): StringE<Int> {
        val digitParser = ParseDigit()
        return Repeat(digitParser).parse(string)
    }
}

class ParseString: Parser<String>() {
    override fun parse(string: StringE<String>): StringE<String> {
        return Repeat(ParseChar<String>()).parse(string)
    }
}

class ParseXMLLight: Parser<String>() {
    override fun parse(string: StringE<String>): StringE<String> {
        val leftParser = ParseCustomChar<String>('<')
        val rightParser = ParseCustomChar<String>('>')
        val xmlTokensParser = Alternative(leftParser, rightParser)

        return Repeat(xmlTokensParser).parse(string)
    }
}

class XML (val name: String, val children: List<XML>)

class XMLOutput (private val name: String, private val children: String) {
    override fun toString(): String {
        var items = mutableListOf<String>()
        items.add("$name -> XML")
        items.add(children)
        return items.joinToString(separator = ", ")
    }
}

fun substringAfter(str: String, delimiter: Char): String {
    return if (str.contains(delimiter)) {
        str.substringAfterLast(delimiter)
    } else ""
}

fun structurize(xml: XML) : List<XMLOutput> {

    val outputList = mutableListOf<XMLOutput>()
    val root = XML("root", listOf(xml))

    for (i in root.children) {
        val id = substringAfter(i.name, '#')
        val tagAlreadyExists = outputList.contains(outputList.find{ x -> substringAfter(i.name,'#').isNotBlank() })

        val tagHasChildren = i.children.isNotEmpty()

        if (tagHasChildren) {
            for (child in i.children) {
                val tagNum = substringAfter(child.name, '#')
                println("Number: #${tagNum}")
            }
        } else {
            if (tagAlreadyExists) {
                outputList.add(XMLOutput("${i.name}#${id}", "XML"))
            } else {
                outputList.add(XMLOutput("${i.name}#1", "XML"))
            }
        }
    }
    return outputList.toList()
}

fun main() {
    // Input: xml("aa", listOf(xml("bb", listOf())))
    /*
        Output:
        ("bb#1","XML")
        ("aa#1", "(cc#1, bb#1) -> XML")
    */

    val xml = XML(
        "aa",
        listOf(XML(
            "bb", listOf()
        ), XML(
            "cc", listOf()
        ), XML(
                "aa", listOf()
        ),
            XML("bb", listOf())
            )
    )

    structurize(xml)
}