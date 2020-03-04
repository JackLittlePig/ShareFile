package com.example.sharefile

import android.content.Context
import android.content.Intent
import android.media.MediaMetadataRetriever
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.widget.Toast
import androidx.core.content.FileProvider
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        tv_share_file.setOnClickListener {
            shareFile(applicationContext)
        }
    }


    private fun shareFile(context: Context) {
        val uri: Uri
        val file = File(getDiskCachePath(context) + "/1582089006.mp3")
        val currentapiVersion = android.os.Build.VERSION.SDK_INT
        if (currentapiVersion >= 24) {//若SDK大于等于24  获取uri采用共享文件模式
            uri = FileProvider.getUriForFile(context, "应用包名.fileprovider", file)
        } else {
            uri = Uri.fromFile(file)
        }


        val share = Intent(Intent.ACTION_SEND)
        share.putExtra(Intent.EXTRA_STREAM, uri)
        share.type = getMimeType(file.absolutePath)//此处可发送多种文件
        share.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        share.addCategory(Intent.CATEGORY_DEFAULT)
        share.setPackage("com.tencent.mobileqq")

        if (share.resolveActivity(context.packageManager) != null) {
            context.startActivity(share)
        } else {
            Toast.makeText(context, "没有可以处理该pdf文件的应用", Toast.LENGTH_SHORT).show()
        }
    }

    /** * 根据文件后缀获取文件MIME类型
     *
     * @param filePath
     * @return
     */
    private fun getMimeType(filePath: String?): String {
        val mmr = MediaMetadataRetriever()
        var mime = "*/*"
        if (filePath != null) {
            try {
                mmr.setDataSource(filePath)
                mime = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_MIMETYPE)
            } catch (e: IllegalStateException) {
                return mime
            } catch (e: IllegalArgumentException) {
                return mime
            } catch (e: RuntimeException) {
                return mime
            }

        }
        return mime
    }

    /**
     * 获取cache路径
     *
     * @param context
     * @return
     */
    fun getDiskCachePath(context: Context): String {
        return if (Environment.MEDIA_MOUNTED == Environment.getExternalStorageState() || !Environment.isExternalStorageRemovable()) {
            context.externalCacheDir!!.path
        } else {
            context.cacheDir.path
        }
    }
}
