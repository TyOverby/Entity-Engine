package com.prealpha.sge.logic

case class FrameRate(stepInMillis: Int){
    def countMillis(frames: Int) = frames * stepInMillis
}

case class Time(frame: Int, millis: Int) extends Ordered[Time]{
    def compare(that: Time): Int = {

    }
}

