package com.layrin.smsapp.model.model

import android.content.Context
import android.util.Log
import com.layrin.smsapp.model.message.Message
import kotlinx.coroutines.Dispatchers
import org.json.JSONObject
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.io.IOException
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel

class Model(
    private val context: Context,
) {

    private val loadModel = loadModelFile()
    private val model: Interpreter = Interpreter(loadModel, Interpreter.Options())

    private val token = "words.json"
    private var tokenData: HashMap<String, Int>? = null
    private val stopWords = "stopwords.json"
    private var stopWordsHash: HashMap<String, Int>? = null

    private fun loadModelFile(): MappedByteBuffer {
        val fileDescriptor = context.assets.openFd("model.tflite")
        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
        val fileChannel = inputStream.channel
        val startOffset = fileDescriptor.startOffset
        val declaredLength = fileDescriptor.declaredLength
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
    }

    fun getPrediction (message: Message) = getPredictions(arrayListOf(message))

    fun getPredictions(messages: ArrayList<Message>): Array<Float> {
        val probability = Array(3) { 0F }

        for (i in 0 until messages.size) {
            var msg = messages[i].text.lowercase().trim()
            msg = cleanText(msg)
            val token = tokenizer(msg)
            val pad = padSequence(token)

            val input = arrayOf(pad.map { it.toFloat() }.toFloatArray())

            val output = arrayOf(FloatArray(3))
            model.run(input, output)

            for (j in 0..2) probability[j] += output[0][j]
            Log.i("msg_tag", msg)
            Log.i("msg_tag", "[${probability[0]}, ${probability[1]}, ${probability[2]}]")
        }
        Log.i("msg_tag", "${probability.indexOf(probability.max())}")
        return probability
    }

    private fun padSequence (sequence: IntArray): IntArray {
        val maxLen = 17
        return if (sequence.size > maxLen) sequence.sliceArray(0..maxLen)
        else if (sequence.size < maxLen) {
            val array = arrayListOf<Int>()
            array.addAll(sequence.asList())
            for (i in array.size until maxLen) array.add(0)
            array.toIntArray()
        } else sequence
    }

    private fun cleanText(text: String): String {
        val replaceBySpace = Regex("[/(){}\\[\\]|@,;]")
        val badSymbols = Regex("[^0-9a-z $+_]")
        val duplicateSpace = Regex("\\s+")
        var newText = text.lowercase()
        newText = removeRegex(newText, replaceBySpace)
        newText = removeRegex(newText, badSymbols)
        newText = removeRegex(newText, duplicateSpace)
        return newText

    }

    private fun removeRegex(text: String, regex: Regex): String {
        val all = regex.findAll(text)
        var newText = text
        for (one in all) newText = newText.replace(one.groupValues.first().toString(), " ")
        return newText.trim()
    }

    private fun tokenizer(message: String): IntArray {
        val words = loadJsonFromAsset(token)
        val stopWordList = loadJsonFromAsset(stopWords)
        with(Dispatchers.Default) {
            val jsonToken = convertJson(words)
            val jsonStopWords = convertJson(stopWordList)
            with(Dispatchers.Main) {
                tokenData = jsonToken
                stopWordsHash = jsonStopWords
            }
        }
        val parts = message.split(" ")
        parts.forEach {
            if (it in stopWordsHash!!.keys) {
                it.replace(it, "")
            }
        }
        val tokenizedMessage = arrayListOf<Int>()
        for (part in parts) {
            if (part.trim() != "") {
                var index: Int? = 0
                index = if (tokenData?.get(part) != null) tokenData!![part]
                else 0
                if (index != null) {
                    tokenizedMessage.add(index)
                }
            }
        }
        return tokenizedMessage.toIntArray()
    }

    private fun convertJson(file: String?): HashMap<String, Int> {
        val json = file?.let { JSONObject(it) }
        val iterator: Iterator<String>? = json?.keys()
        val data = HashMap<String, Int>()
        while (iterator?.hasNext() == true) {
            val key = iterator.next()
            data[key] = json.get(key) as Int
        }
        return data
    }

    private fun loadJsonFromAsset(file: String): String? {
        val json: String?
        try {
            val inputStream = context.assets.open(file)
            val size = inputStream.available()
            val buffer = ByteArray(size)
            inputStream.read(buffer)
            inputStream.close()
            json = String(buffer)
        } catch (ex: IOException) {
            ex.printStackTrace()
            return null
        }
        return json
    }

    fun close() {
        model.close()
        loadModel.clear()
    }

}