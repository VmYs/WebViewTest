package com.example.webviewtest

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.webviewtest.databinding.ActivityMainBinding
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.webView.settings.javaScriptEnabled = true
        binding.webView.webViewClient = WebViewClient()
        binding.webView.loadUrl("https://www.bilibili.com")

        if (ContextCompat.checkSelfPermission(this,
                        android.Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    arrayOf(android.Manifest.permission.RECORD_AUDIO), 1)
        } else {
            val minBufferSize = AudioRecord.getMinBufferSize(44100, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT)
            val audioRecord = AudioRecord(MediaRecorder.AudioSource.MIC, 44100,
                    AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT, minBufferSize)
            val data = ByteArray(minBufferSize)
            val file = File(getExternalFilesDir(Environment.DIRECTORY_MUSIC), "test.pcm")
            if (!file.mkdirs()) {
                Log.d("record", "Directory not created")
            }
            if (file.exists()) {
                file.delete()
            }
            audioRecord.startRecording()
            val isRecording = true
            Thread {
                var os: FileOutputStream? = null
                try {
                    os = FileOutputStream(file)
                } catch (e: FileNotFoundException) {
                    e.printStackTrace()
                }
                if (null != os) {
                    while (isRecording) {
                        val read: Int = audioRecord.read(data, 0, minBufferSize)
                        if (AudioRecord.ERROR_INVALID_OPERATION != read) {
                            try {
                                os.write(data)
                            } catch (e: IOException) {
                                e.printStackTrace()
                            }
                        }
                    }
                    try {
                        Log.d("record", "run: close file output stream !")
                        os.close()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }.start()
            Log.d("record", "success")
        }
    }

//    override fun onRequestPermissionsResult(
//            requestCode: Int,
//            permissions: Array<String>,
//            grantResults: IntArray
//    ) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//        when (requestCode) {
//            1 -> {
//                if (grantResults.isNotEmpty() &&
//                        grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//
//                } else {
//                    Toast.makeText(this, "You denied this permission",
//                            Toast.LENGTH_SHORT).show()
//                }
//            }
//        }
//    }

//    private fun audioRecordInit(): AudioRecord? {
//        try {
//            return AudioRecord.Builder()
//                    .setAudioSource(MediaRecorder.AudioSource.VOICE_COMMUNICATION)
//                    .setAudioFormat(AudioFormat.Builder()
//                            .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
//                            .setSampleRate(32000)
//                            .setChannelMask(AudioFormat.CHANNEL_IN_MONO)
//                            .build())
//                    .build()
//        } catch (e: SecurityException) {
//            e.printStackTrace()
//        }
//        return audioRecordInit()
//    }


}