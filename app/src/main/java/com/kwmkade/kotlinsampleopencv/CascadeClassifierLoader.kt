package com.kwmkade.kotlinsampleopencv

import android.content.Context
import android.content.res.Resources
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import org.opencv.objdetect.CascadeClassifier
import java.io.File

class CascadeClassifierLoader(
    private var mContext: Context,
    private var mResources: Resources
) {
    companion object {
        private val mXmpMap = mapOf(
            R.raw.haarcascade_eye to "haarcascade_eye.xml",
            R.raw.haarcascade_eye_tree_eyeglasses to "haarcascade_eye_tree_eyeglasses.xml",
            R.raw.haarcascade_frontalcatface to "haarcascade_frontalcatface.xml",
            R.raw.haarcascade_frontalcatface_extended to "haarcascade_frontalcatface_extended.xml",
            R.raw.haarcascade_frontalface_alt to "haarcascade_frontalface_alt.xml",
            R.raw.haarcascade_frontalface_alt2 to "haarcascade_frontalface_alt2.xml",
            R.raw.haarcascade_frontalface_alt_tree to "haarcascade_frontalface_alt_tree.xml",
            R.raw.haarcascade_frontalface_default to "haarcascade_frontalface_default.xml",
            R.raw.haarcascade_fullbody to "haarcascade_fullbody.xml",
            R.raw.haarcascade_lefteye_2splits to "haarcascade_lefteye_2splits.xml",
            R.raw.haarcascade_licence_plate_rus_16stages to "haarcascade_licence_plate_rus_16stages.xml",
            R.raw.haarcascade_lowerbody to "haarcascade_lowerbody.xml",
            R.raw.haarcascade_profileface to "haarcascade_profileface.xml",
            R.raw.haarcascade_righteye_2splits to "haarcascade_righteye_2splits.xml",
            R.raw.haarcascade_russian_plate_number to "haarcascade_russian_plate_number.xml",
            R.raw.haarcascade_smile to "haarcascade_smile.xml",
            R.raw.haarcascade_upperbody to "haarcascade_upperbody.xml",
            R.raw.lbpcascade_frontalcatface to "lbpcascade_frontalcatface.xml",
            R.raw.lbpcascade_frontalface to "lbpcascade_frontalface.xml",
            R.raw.lbpcascade_frontalface_improved to "lbpcascade_frontalface_improved.xml",
            R.raw.lbpcascade_profileface to "lbpcascade_profileface.xml",
            R.raw.lbpcascade_silverware to "lbpcascade_silverware.xml",
        )

        fun classifiers() = mXmpMap.values.toList()

        fun findClassifierId(pos: Int): Int {
            val xmlName = classifiers()[pos]
            mXmpMap.forEach {
                if (it.value == xmlName) {
                    return it.key
                }
            }
            return R.raw.haarcascade_frontalface_alt2
        }

        fun classifierName(classifierId: Int) = mXmpMap[classifierId]
    }

    private var mCaseFile: File? = null

    suspend fun Load(classifierId: Int): Flow<CascadeClassifier> {
        withContext(Dispatchers.IO) {
            mResources.openRawResource(classifierId).bufferedReader().use { r ->
                var cascadeDir = mContext.getDir("cascade", Context.MODE_PRIVATE)
                mCaseFile = File(cascadeDir, mXmpMap[classifierId])
                mCaseFile!!.bufferedWriter().use { out ->
                    while (true) {
                        var chars = CharArray(4096)
                        var len: Int = r.read(chars)
                        if (len == -1) break
                        out.write(chars, 0, len)
                    }
                }
            }
        }

        if (mCaseFile == null) {
            return emptyFlow()
        }

        return flow {
            emit(CascadeClassifier(mCaseFile!!.absolutePath))
        }
    }

}