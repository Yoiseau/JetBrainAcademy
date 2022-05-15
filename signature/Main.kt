package signature
import java.io.File
import kotlin.math.abs

fun main() {

//    print("Enter name and surname:")
    val (firstName, secondName) = readLine()!!.split(" ")
//    print("Enter person's status:")
    val status = readLine()!!

    val nameTag: Board = Board(firstName, secondName, status)
    nameTag.printTag()

}

class Font(val fontPath: String) {

    val font = mutableMapOf<String, MutableList<String>>()
    var charHeight: Int = 0

    init {
        fontParser()
    }

    fun fontParser() {
        val fileContent: MutableList<String> = mutableListOf()
        File(fontPath).forEachLine {
            fileContent.add(it)
        }

//        var lines = File(fontPath).readLines()
        val (width, nbCharacters) = fileContent[0].split(" ")
        charHeight = width.toInt()

        val spaceChar: MutableList<String> = MutableList(charHeight + 1) {""}
        spaceChar[0] = if (charHeight == 10) width  else "5"
//        font.put(" ", mutableListOf("10"))

        for (i in 1 .. charHeight) {
            spaceChar[i] = (" ".repeat(if (charHeight == 10) charHeight else 5))
        }
        font.put(" ", spaceChar)

        for(i in 0 until nbCharacters.toInt()) {
            val currIndex = i * (charHeight + 1) + 1
            val charInfo = fileContent[currIndex]
            val (letter, charWidth) = charInfo.split(" ")
            var charLines = mutableListOf<String>()
            charLines.add(charWidth)

            for(j in 1..charHeight) {
                charLines.add(fileContent[currIndex + j])
            }
            font.put(letter, charLines)

        }
    }

    fun printFont(char: String) {
        println(font.getValue(char))
    }
}

class Board(val firstName: String, val lastName: String, val status: String) {

    lateinit var tag: String
    var borders: String = ""
    var nameTag: String = ""
    var statusTag: String = ""

    var nameTagWidth: Int = 0
    var statusTagWidth: Int = 0
    var bordersLen: Int = 0
    var offset: Int = 0

    val simpleFont: Font = Font("C:\\Users\\johan\\Documents\\Coding_Repository\\Kotlin\\ASCII Text Signature\\ASCII Text Signature\\task\\src\\signature\\medium.txt")
    val romanFont: Font = Font("C:\\Users\\johan\\Documents\\Coding_Repository\\Kotlin\\ASCII Text Signature\\ASCII Text Signature\\task\\src\\signature\\roman.txt")

    init {
        this.__generateTag()
    }

    private fun __createWordTag(wordArray: MutableList<String>, offset: Int, font: Font, isEven: Int = 0) : String {
        var tag: String = ""
        val border: String = "88"
        var rightOffset = offset

        if(isEven % 2 != 0 ){
            rightOffset += 1
        }
        for (i in 1 .. font.charHeight) {
            tag += border + " ".repeat(offset) + wordArray[i].toString() + " ".repeat(rightOffset) + border + "\n"
        }
        tag = tag.trimEnd()

        return tag
    }

    private fun __generateWord(word: String, font: Font): Pair<MutableList<String>, Int> {
        var wordWidth: Int = 0
        var wordHeight: Int = font.charHeight
        val wordArray: MutableList<String> = MutableList(font.charHeight + 1) {""}

        for (letter in word) {
            wordWidth += font.font.getValue(letter.toString())[0].toInt()

            for (line in 1 .. wordHeight) {
                wordArray[line] += font.font.getValue(letter.toString())[line]
            }
        }

        return Pair(wordArray, wordWidth)
    }

    private fun __handleTagCentering(nameTagArray: MutableList<String>, statusTagArray: MutableList<String>) {

        var nameTagOffset: Int = 0
        var statusTagOffset: Int = 0

        this.bordersLen = maxOf(this.nameTagWidth,this.statusTagWidth) + 8

        if (this.nameTagWidth >= this.statusTagWidth) {
            nameTagOffset = 2
            statusTagOffset = ((this.nameTagWidth - this.statusTagWidth) / 2) + 2
            this.nameTag = this.__createWordTag(nameTagArray, nameTagOffset, romanFont)
            this.statusTag = this.__createWordTag(statusTagArray, statusTagOffset, simpleFont, this.nameTagWidth - this.statusTagWidth)
        }
        else {
            nameTagOffset = ((this.statusTagWidth - this.nameTagWidth) / 2) + 2
            statusTagOffset = 2
            this.nameTag = this.__createWordTag(nameTagArray, nameTagOffset, romanFont, this.statusTagWidth - this.nameTagWidth)
            this.statusTag = this.__createWordTag(statusTagArray, statusTagOffset, simpleFont)

        }
    }

    private fun __generateTag() {
        val (nameTagArray, nameTagWidth) = this.__generateWord(firstName + " " + lastName, romanFont)
        val (statusTagArray, statusTagWidth) = this.__generateWord(status, simpleFont)

        this.nameTagWidth = nameTagWidth
        this.statusTagWidth = statusTagWidth

        this.__handleTagCentering(nameTagArray, statusTagArray)
        this.borders += "8".repeat(this.bordersLen).trimEnd()

        tag = "${this.borders}\n" +
                "${this.nameTag}\n" +
                "${this.statusTag}\n"+
                "${this.borders}".trimEnd()
    }

    fun printTag() {
        println(tag)
    }


}