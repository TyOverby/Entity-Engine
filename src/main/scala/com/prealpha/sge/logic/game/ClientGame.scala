package com.prealpha.sge.logic.game

import com.prealpha.sge.messages.SyncMessage
import com.prealpha.sge.logic.{Time, FrameRate, ClientState}

abstract class ClientGame(host: String) extends ClientState(host) with Game {
    var curFrame = 0
    var millisSinceUpdate = 0L

    toServer.messagePublisher.observe {
        case m: SyncMessage => onStateUpdate(m)
        case _ =>
    }

    /**
     * Run the update method on all actors in the collection
     * @param deltaT The delta-time in miliseconds
     */
    private[this]
    def runUpdates(deltaT: Long) {
        actors.par.foreach(_.update(deltaT))
    }

    /**
     * Run an update loop on the client outside of the
     * actual frame limit imposed by the server
     * @param deltaT
     */
    @inline
    def microSequence(deltaT: Long) {
        actors.synchronized{
            runUpdates(deltaT)
            update(deltaT)
            stepPhysics(deltaT)
            sendMessages()
        }
    }

    /**
     * Called whenever we receive a [[com.prealpha.sge.messages.SyncMessage]].
     *
     * @param syncM
     */
    private[this]
    def onStateUpdate(syncM: SyncMessage) {
        val SyncMessage(newActors, time) = syncM
        // Synchronize the actors so we don't fuck
        // tons of things up
        actors.synchronized {
            // merge the actors
            actors.takeFrom(newActors)
            // Update with the difference between when
            // the frame was sent and when we get it
            microSequence(Time(curFrame, millisSinceUpdate).toMillis - time.toMillis)

            // Apply the messages that we had but didn't
            // get a chance to
            applyMessages(time)
        }
        millisSinceUpdate = 0
    }

    /**
     * The main game loop mostly just calls an inlined
     * microSequence with a deltaT
     */

    protected def loop()(implicit rate: FrameRate) {
        var lastTime = 0L
        while (running) {
            val curTime = System.currentTimeMillis()
            val delta = curTime - lastTime
            microSequence(delta)
            millisSinceUpdate = millisSinceUpdate + delta
            lastTime = curTime
        }
    }
}
