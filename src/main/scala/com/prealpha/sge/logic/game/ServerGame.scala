package com.prealpha.sge.logic.game

import com.prealpha.sge.messages.SyncMessage
import com.prealpha.sge.logging.log
import com.prealpha.sge.logic.{Time, FrameRate, ServerState}

abstract class ServerGame extends ServerState with Game {
    // The amount of frames in one second
    var curFrame = 0

    private[this] def runUpdates(deltaT: Long){
        actors.par.foreach(_.update(deltaT))
    }

    def serializeAndSend(displacement: Long){
        log.info("SERVER: serializing")
        val ser = new SyncMessage(actors, Time(curFrame,0))
        cPool.broadcast(ser)
    }

    protected def loop()(implicit rate: FrameRate){
        var lastTime = System.currentTimeMillis()
        while(running){
            val curTime = System.currentTimeMillis()
            val delta   = curTime - lastTime
            step(delta)
            lastTime = curTime

            val elapsed = System.currentTimeMillis() - curTime
            val toSleep = rate.stepInMillis - elapsed
            Thread.sleep(toSleep)
        }
    }

    @inline
    private[this] def step(deltaT: Long){
        log.info(f"SERVER: step of ${deltaT} milliseconds.")
        runUpdates(deltaT)
        update(deltaT)
        stepPhysics(deltaT)
        serializeAndSend(deltaT - framerate.stepInMillis)
    }
}
