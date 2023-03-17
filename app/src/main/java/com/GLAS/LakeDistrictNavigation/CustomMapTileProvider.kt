package com.GLAS.LakeDistrictNavigation

import android.content.res.AssetManager
import com.google.android.gms.maps.model.Tile
import com.google.android.gms.maps.model.TileProvider
import com.google.android.gms.maps.model.TileProvider.NO_TILE
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream

//https://stackoverflow.com/questions/14784841/tileprovider-using-local-tiles

class CustomMapTileProvider(private val mAssets: AssetManager) : TileProvider {
    override fun getTile(x: Int, y: Int, zoom: Int): Tile? {
        val image = readTileImage(x, y, zoom)
        return if (image == null) NO_TILE else Tile(TILE_WIDTH, TILE_HEIGHT, image)
    }


    private fun readTileImage(x: Int, y: Int, zoom: Int): ByteArray? {
        var inStream : InputStream? = null
        var buffer: ByteArrayOutputStream? = null
        return try {
            inStream = mAssets.open(getTileFilename(x, y, zoom))
            buffer = ByteArrayOutputStream()
            var nRead: Int
            val data = ByteArray(BUFFER_SIZE)
            while (inStream.read(data, 0, BUFFER_SIZE).also {
                    nRead = it
                } != -1) {
                buffer.write(data, 0, nRead)
            }
            buffer.flush()
            buffer.toByteArray()
        } catch (e: IOException) {
            e.printStackTrace()
            null
        } catch (e: OutOfMemoryError) {
            e.printStackTrace()
            null
        } finally {
            if (inStream != null) try {
                inStream.close()
            } catch (ignored: Exception) {
            }
            if (buffer != null) try {
                buffer.close()
            } catch (ignored: Exception) {
            }
        }
    }

    private fun getTileFilename(x: Int, y: Int, zoom: Int): String {
        return "WebPTiles/$zoom/$x/$y.webp"
    }

    companion object {
        private const val TILE_WIDTH = 256
        private const val TILE_HEIGHT = 256
        private const val BUFFER_SIZE = 2 * 1024
    }
}