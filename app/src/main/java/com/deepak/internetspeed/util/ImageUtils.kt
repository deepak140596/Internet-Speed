package com.deepak.internetspeed.util

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect

class ImageUtils{
    companion object{

        fun createBitmapFromString(speed: String, units: String) : Bitmap{
            var paint = Paint()
            paint.isAntiAlias = true
            paint.textSize = 55F
            paint.textAlign = Paint.Align.CENTER

            var unitsPaint = Paint()
            unitsPaint.isAntiAlias = true
            unitsPaint.textSize = 40F
            unitsPaint.textAlign = Paint.Align.CENTER

            var speedBounds = Rect()
            paint.getTextBounds(speed,0,speed.length,speedBounds)

            var unitsBounds = Rect()
            unitsPaint.getTextBounds(units,0,units.length,unitsBounds)

            var width = if(speedBounds.width() > unitsBounds.width()){
                speedBounds.width()
            }else{
                unitsBounds.width()
            }

            var bitmap = Bitmap.createBitmap(width + 10, 90,
                Bitmap.Config.ARGB_8888)

            var canvas = Canvas(bitmap)
            canvas.drawText(speed, (width/2F + 5), 50F, paint)
            canvas.drawText(units, width/2F, 90F, unitsPaint)

            return bitmap
        }
    }
}