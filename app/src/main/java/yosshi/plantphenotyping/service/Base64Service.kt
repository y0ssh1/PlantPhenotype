package yosshi.plantphenotyping.service
import android.graphics.Bitmap
import android.util.Base64
import java.io.ByteArrayOutputStream

class Base64Service {

    fun encode(bitmap: Bitmap): String {
        return ByteArrayOutputStream().let {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)
             Base64.encodeToString(it.toByteArray(), Base64.DEFAULT)
        }
    }

}