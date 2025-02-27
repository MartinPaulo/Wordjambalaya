package nz.co.martinpaulo.wordjambalaya

/**
 * Essentially a singleton wrapper around the list of unknown words
 * Created by martin paulo on 29/07/2014.
 */
class UnknownWords private constructor() {
    val words: ArrayList<UnknownWord> = ArrayList()

    companion object {
        private var instance: UnknownWords? = null


        fun getInstance(numberOfWords: Int): UnknownWords? {
            if (instance == null) {
                instance = UnknownWords()
            }
            for (i in instance!!.words.size until numberOfWords) {
                val word = UnknownWord()
                instance!!.words.add(word)
            }
            return instance
        }
    }
}
