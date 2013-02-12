package com.prealpha.sge.logic

import java.util.concurrent.LinkedBlockingQueue
import com.prealpha.sge.messages.{UpdateMessage, SyncMessage, Message}
import com.prealpha.sge.gamestate.ClientGameState
import scala.collection.JavaConversions._

trait ClientGame extends ClientGameState {

    private[this] var running = false
    private[this] var alreadyInit = false

    private[this] var currentFrame = Frame(0,0L)
    // The public accessor for curframe
    def curFrame = currentFrame

    private[this] val outgoingMessages = new LinkedBlockingQueue[Message]()
    private[this] val myMessages       = new LinkedBlockingQueue[UpdateMessage]()
    private[this] val messageQueue     = new LinkedBlockingQueue[UpdateMessage]()


    def init()
    def update(deltaT: Long)
    def stepPhysics(deltaT: Long)
    def render()

    /**
     * Send all of the message that have accumulated over the last frame
     */
    private[this]
    def sendMessages(){
        outgoingMessages.foreach{ m =>
            this.serverConnection.write(m)
            m match {
                case m: UpdateMessage => messageQueue.put(m)
            }
        }
        outgoingMessages.clear()
        this.serverConnection.flush()
    }

    /**
     * Run the update method on all actors in the collection
     * @param deltaT The delta-time in miliseconds
     */
    private[this]
    def runUpdates(deltaT: Long){
        this.actors.foreach(_.update(deltaT))
    }

    /**
     * Run an update loop on the client outside of the
     * actual frame limit imposed by the server
     * @param deltaT
     */
    @inline
    private[this]
    def microSequence(deltaT: Long){
        runUpdates(deltaT)
        update(deltaT)
        stepPhysics(deltaT)
        sendMessages()
        render()
    }

    /**
     * Called whenever we receive a [[com.prealpha.sge.messages.SyncMessage]].
     *
     * @param syncM
     */
    private[this]
    def onStateUpdate(syncM: SyncMessage){
        val SyncMessage(newActors, newFrame) = syncM
        // Syncronize the actors so we don't fuck
        // tons of things up
        actors.synchronized{
            // merge the actors
            actors.takeFrom(newActors)
            // Update with the difference between when
            // the frame was sent and when we get it
            microSequence(currentFrame - newFrame)

            // pass all the messages that we've been storing into
            // the new actors
            myMessages
                .filter(_.frame>newFrame)
                .foreach(actors.passMessage)
            myMessages.clear()

            // pass all the messages that we've gotten since the last
            // state update into the new actors
            messageQueue
                .filter(_.frame < newFrame)
                .foreach(actors.passMessage)
            messageQueue.clear()
        }
    }

    /**
     * The main game loop mostly just calls an inlined
     * microSequence with a deltaT
     */
    private[this]
    def loop(){
        var lastTime = 0L
        while(running){
            val curTime = System.currentTimeMillis()
            val delta = curTime - lastTime
            microSequence(delta)
            currentFrame = currentFrame + delta
            lastTime = curTime
        }
    }

    /**
     * Starts the game loop
     */
    def start(){
        if (!alreadyInit){
            init()
            alreadyInit = true
        }
        running = true
        loop()
    }

    /**
     * Stops the game loop
     */
    def stop(){
        running = false
    }
}
