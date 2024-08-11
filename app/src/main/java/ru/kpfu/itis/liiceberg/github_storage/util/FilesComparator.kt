package ru.kpfu.itis.liiceberg.github_storage.util

/*
first = saved, second = new files
 */

class FilesComparator(
    private val firstDirectory: Map<String, String>,
    private val secondDirectory: Map<String, String>
) {

    private var addedLinesCount: Int = 0
    private var deletedLinesCount: Int = 0

    init {
        compare()
    }

    fun added(): Int = addedLinesCount
    fun deleted(): Int = deletedLinesCount

    private fun compare() {
        val files1 = firstDirectory.keys.toMutableSet()
        val files2 = secondDirectory.keys.toMutableSet()

        firstDirectory.keys.forEach { path ->
            if (secondDirectory.keys.contains(path)) {
                compareFiles(
                    getContentAsLines(firstDirectory.getValue(path)),
                    getContentAsLines(secondDirectory.getValue(path))
                )
                files1.remove(path)
                files2.remove(path)
            }
        }

        deletedLinesCount += files1.sumOf { path -> countFileLines(firstDirectory.getValue(path)) }
        addedLinesCount += files2.sumOf { path -> countFileLines(secondDirectory.getValue(path)) }
    }

    private fun compareFiles(file1Lines: List<String>, file2Lines: List<String>) {
        val set1 = file1Lines.toMutableSet()
        val set2 = file2Lines.toMutableSet()

        // Lines added in file2
        set2.removeAll(file1Lines.toSet())
        addedLinesCount += set2.size

        // Lines deleted from file1
        set1.removeAll(file2Lines.toSet())
        deletedLinesCount += set1.size
    }

    private fun countFileLines(content: String) : Int {
        return getContentAsLines(content).size
    }

    private fun getContentAsLines(content: String): List<String> {
        return content.split("\n")
    }

}