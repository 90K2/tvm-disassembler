package org.ton.disassembler.struct

class TrieNode<T>(
    val key: String?, var value: T?
) {
    var parent: TrieNode<T>? = null
    val children: HashMap<String, TrieNode<T>> = hashMapOf()
    var end: Boolean = false

    fun getWord(): String {
        val output = mutableListOf<String?>()
        var node: TrieNode<T>? = this
        while (node != null) {
            output.add(0, node.key)
            node = node.parent
        }
        return output.filterNotNull().joinToString("")
    }
}

class Trie<T> {

    val root = TrieNode<T>(null, null)

    private fun findAllWords(node: TrieNode<T>, arr: MutableList<String>) {
        // base case, if node is at a word, push to output
        if (node.end) arr.add(0, node.getWord())

        // iterate through each children, call recursive findAllWords
        node.children.entries.forEach {
            findAllWords(it.value, arr)
        }
    }

    fun insert(word: String, value: T) {
        var node = this.root

        for (i in word.indices) {
            if (node.end) throw RuntimeException("Word cannot start with already used prefix")
            val key = word[i].toString()
            if (node.children[key] == null) {
                node.children[key] = TrieNode(key, null)
                node.children[key]!!.parent = node
            }

            node = node.children[key]!!

            // finally, we check to see if it's the last word.
            if (i == word.indices.last) {
                if (node.children.size > 0) {
                    throw RuntimeException("Word cannot start with already used prefix")
                }

                // if it is, we set the end flag to true.
                node.end = true
                node.value = value
            }
        }
    }

    fun contains(word: String): Boolean {
        var node = this.root

        for (i in word.indices) {
            val key = word[i].toString()
            if (node.children[key] != null)
                node = node.children[key]!!
            else
                return false

        }
        return node.end
    }

    fun find(prefix: String): MutableList<String> {
        var node = this.root
        val output = mutableListOf<String>()

        for (i in prefix.indices) {
            val key = prefix[i].toString()
            if (node.children[key] != null) {
                node = node.children[key]!!
            } else
                return output
        }
        findAllWords(node, output)

        return output
    }

    fun getValue(key: String): T? {
        var node = this.root

        for (i in key.indices) {
            val k = key[i].toString()
            if (node.children[k] != null)
                node = node.children[k]!!
            else
                return null
        }
        if (!node.end) return null

        return node.value
    }
}
