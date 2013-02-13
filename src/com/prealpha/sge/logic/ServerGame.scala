package com.prealpha.sge.logic

import com.prealpha.sge.messages.SyncMessage

abstract class ServerGame extends ServerState {
    // The amount of frames in one second
    val rate: Int

    def init()
    def update(deltaT: Long)
    def stepPhysics(deltaT: Long)

    var curFrame = new Frame(0,0)

    private[this] def runUpdates(deltaT: Long){
        actors.par.foreach(_.update(deltaT))
    }

    def serializeAndSend(){
        val ser = new SyncMessage(actors,curFrame)
        cPool.broadcast(ser)
    }


    private[this] def loop(){}

    @inline
    private[this] def step(deltaT: Long){
        runUpdates(deltaT)
        update(deltaT)
        serializeAndSend()
    }
}
