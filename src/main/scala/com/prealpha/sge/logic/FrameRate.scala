package com.prealpha.sge.logic

case class FrameRate(stepInMillis: Int){
    def countMillis(frames: Int) = frames * stepInMillis
}

case class Time(frame: Int, millis: Long) extends Ordered[Time]{
    def toMillis(implicit frameRate: FrameRate) = frameRate.countMillis(frame) + millis

    def compare(that: Time): Int = {
        if (this.frame < that.frame){
            -1
        }
        else if (this.frame > that.frame){
            1
        }
        else{
            if (this.millis < that.millis){
                -1
            }
            else if (this.millis > that.millis){
                1
            }
            else{
                0
            }
        }
    }
}

