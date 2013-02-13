package com.prealpha.sge.logic

case class Frame(frameNumber: Int, millis: Long) extends Ordered[Frame] {
    def toMillis:Long = (millis + frameNumber * (1.0/Frame.updateRate)).toLong


    // But Ty, why don't you just convert it toMillis and then do a
    // comparison?

    // Because this is faster and it disregards the millis if the frameNumber
    // is higher.  (this might not actually be what we want)
    def compare(that: Frame): Int = {
        if (this.frameNumber < that.frameNumber){
            -1
        }
        else if (this.frameNumber > that.frameNumber){
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

    def + (mill: Long): Frame = {
        Frame(frameNumber, millis + mill)
    }

    def - (other: Frame): Long = this.toMillis - other.toMillis
}
