package com.example.mvp

import android.content.ContentValues
import android.content.Context
import android.graphics.*
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.system.ErrnoException
import android.util.Log
import androidx.camera.core.ImageProxy
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.tensorflow.lite.DataType
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.support.common.FileUtil
import org.tensorflow.lite.support.common.TensorOperator
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.image.ops.ResizeOp
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import org.tensorflow.lite.support.tensorbuffer.TensorBufferFloat
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.OutputStream
import java.nio.ByteBuffer
import kotlin.math.sqrt

class Model (context: Context){
    companion object{
        var modelName = "mobile_face_net.tflite"
        var modelInput = 112
        var modelOutput = 192
        var threshold = 0.5f
        var fileName = "embsKnown"
        var modelDesc = "MobileFaceNet"

//        fun changeModel(status: Int){
//            modelName = if(status == 1) "facenet_quantized.tflite" else "mobile_face_net.tflite"
//            modelInput = if(status == 1) 160 else 112
//            modelOutput = if(status == 1) 128 else 192
//            threshold = if(status == 1) 0.65f else 0.5f
//            fileName = if(status == 1) "embsKnownFacenet" else "embsKnown"
//            modelDesc = if(status == 1) "Facenet" else "MobileFaceNet"
//        }
    }

    private var interpreter : Interpreter
    private var embsKnown: ArrayList<FloatArray> = ArrayList()
    private val contextLocal:Context
    @Serializable
    data class EmbeddingSerializable(val embList: ArrayList<FloatArray>)

    init{
        val interpreterOptions = Interpreter.Options().apply { numThreads = 4 }
        interpreter = Interpreter(FileUtil.loadMappedFile(context, modelName) , interpreterOptions )
        contextLocal = context
        loadKnownFaces()
    }



    fun getKnownFacesSize():Int{
        return embsKnown.size
    }
    private fun loadKnownFaces(){
        val savedEmbs = File(contextLocal.filesDir, fileName)
        try {
            val contents = savedEmbs.readText()
            embsKnown.clear()
            embsKnown = Json.decodeFromString<EmbeddingSerializable>(contents).embList
        } catch (ex: FileNotFoundException){
            embsKnown.clear()
        }
    }

    fun registerFace(image: Bitmap) {
        val embs = getEmbeddingsFromBitmap(image)
        embsKnown.clear()
        embsKnown.add(embs!!)
        val embsSerialized = Json.encodeToString(EmbeddingSerializable(embsKnown))
        contextLocal.openFileOutput(fileName,Context.MODE_PRIVATE).use{
            it.write(embsSerialized.toByteArray())
        }
        loadKnownFaces()
    }

    private val imageTensorProcessor = ImageProcessor.Builder()
        .add( ResizeOp( modelInput , modelInput , ResizeOp.ResizeMethod.BILINEAR ) )
        .add( StandardizeOp()  )
        .build()

    private fun convertBitmapToBuffer( image : Bitmap) : ByteBuffer {
        val imageTensor = imageTensorProcessor.process( TensorImage.fromBitmap( image ) )
        return imageTensor.buffer
    }

    private fun getEmbeddingsFromBitmap(image: Bitmap): FloatArray? {
        return runModel(convertBitmapToBuffer(image))[0]
    }

    private fun runModel(inputs: Any): Array<FloatArray> {
        val t1 = System.currentTimeMillis()
        val outputs = Array(1) { FloatArray(modelOutput ) }
        interpreter.run(inputs, outputs)
        Log.e( "Performance" , "$modelDesc Inference Speed in ms : ${System.currentTimeMillis() - t1}")
        return outputs
    }

    fun compareFace(image: Bitmap): Array<String> {
        val embs = getEmbeddingsFromBitmap(image)

        var score = 0f
        for(i in embsKnown){
            score += cosineSimilarity(embs!!,i)
        }
        score /= embsKnown.size
        if(score > threshold){
            embsKnown.add(embs!!)
            if(embsKnown.size > 10){
                embsKnown.removeAt(0)
            }
            val embsSerialized = Json.encodeToString(EmbeddingSerializable(embsKnown))
            contextLocal.openFileOutput(fileName,Context.MODE_PRIVATE).use{
                it.write(embsSerialized.toByteArray())
            }
            loadKnownFaces()
            return arrayOf("true",score.toString()) // Passed
        }
        return arrayOf("failed",score.toString()) //Failed
    }

    private fun cosineSimilarity( x1 : FloatArray , x2 : FloatArray ) : Float {
        val mag1 = sqrt( x1.map { it * it }.sum() )
        val mag2 = sqrt( x2.map { it * it }.sum() )
        val dot = x1.mapIndexed{ i , xi -> xi * x2[ i ] }.sum()
        return dot / (mag1 * mag2)
    }

    class StandardizeOp : TensorOperator {

        override fun apply(p0: TensorBuffer?): TensorBuffer {
            val pixels = p0!!.floatArray
            for ( i in pixels.indices ) {
                pixels[ i ] = ( pixels[ i ] - 127.5f ) / 128f
            }
            val output = TensorBufferFloat.createFixedSize( p0.shape , DataType.FLOAT32 )
            output.loadArray( pixels )
            return output
        }

    }
}