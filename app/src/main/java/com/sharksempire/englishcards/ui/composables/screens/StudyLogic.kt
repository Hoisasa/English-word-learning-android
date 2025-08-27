package com.sharksempire.englishcards.ui.composables.screens

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.media.MediaPlayer
import android.util.Log
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlin.math.roundToInt


private var activePlayer: MediaPlayer? = null


fun playOggFromAssets(context: Context, assetPath: String) {
    try {
        // Stop currently playing audio
        activePlayer?.stop()
        activePlayer?.release()

        // Set up new media player
        val afd = context.assets.openFd(assetPath)
        val mediaPlayer = MediaPlayer().apply {
            setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
            prepare()
            start()
            setOnCompletionListener {
                it.release()
                if (activePlayer === it) activePlayer = null
            }
        }

        afd.close()
        activePlayer = mediaPlayer

    } catch (e: Exception) {
        Log.e("AudioPlay", "Error playing audio $assetPath", e)
    }
}


fun buildAssetFilePath(subGroup: String, wordName: String): String {
    val safeSubGroup = subGroup.replace("/", "-").replace(":", "-")
    val safeWordName = wordName.split(" (")[0]
    return "audiofiles/$safeSubGroup/$safeWordName.ogg"
}
