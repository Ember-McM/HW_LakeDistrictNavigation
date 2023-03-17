package com.GLAS.LakeDistrictNavigation

import android.content.Context
import android.util.Log

class optionMenu {

    fun useMiles(context: Context) : Boolean
    {

        var files: Array<String> = context.fileList()
        Log.v("Options", files.count().toString())
        if (files.isNotEmpty()){
            for (e in files){//
                if (e.startsWith("UseMiles"))
                    return true
            }
        }
        return false
    }



}