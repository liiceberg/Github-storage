package ru.kpfu.itis.liiceberg.github_storage.util

import android.util.Log

/*
first = saved, second = new files
 */

class FilesComparator(
    private val firstDirectory: Map<String, String>,
    private val secondDirectory: Map<String, String>
) {

    enum class FileStatus {
        CREATED, MODIFIED, DELETED
    }

    private var addedLinesCount: Int = 0
    private var deletedLinesCount: Int = 0
    private val status = mutableMapOf<String, FileStatus>()

    init {
        compare()
    }

    fun added() = addedLinesCount
    fun deleted() = deletedLinesCount
    fun all() = status

    private fun compare() {
        val files1 = firstDirectory.keys.toMutableSet()
        val files2 = secondDirectory.keys.toMutableSet()

        Log.d("FILES 1", files1.toString())
        Log.d("FILES 2", files2.toString())

        firstDirectory.keys.forEach { path ->
            if (secondDirectory.keys.contains(path)) {

                val content1 = firstDirectory.getValue(path)
                val content2 = secondDirectory.getValue(path)

                if (content1 != content2) {
                    status[path] = FileStatus.MODIFIED
                }

                compareFiles(
                    getContentAsLines(content1),
                    getContentAsLines(content2)
                )
                files1.remove(path)
                files2.remove(path)
            }
        }

        files1.forEach { file -> status[file] = FileStatus.DELETED }
        files2.forEach { file -> status[file] = FileStatus.CREATED }

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