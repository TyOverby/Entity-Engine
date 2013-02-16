package com.prealpha.sge.entity

import com.prealpha.sge.messages.Message

trait WorldComponent extends Component{
    type M = WorldMessage
    var gravity = 9.8f

    abstract override
    def update(deltaM: Long){
        println("world " + gravity)

        super.update(deltaM)
    }

    def handle(m: WorldMessage){
        this.gravity =  m.gravity
    }
}

case class WorldMessage(gravity: Float, frame: Frame) extends Message