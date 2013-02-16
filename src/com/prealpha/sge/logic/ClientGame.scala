package com.prealpha.sge.logic

import com.prealpha.sge.messages.SyncMessage

abstract class ClientGame(host: String) extends ClientState(host) {

    private[this] var running = false
    private[this] var alreadyInit = false

    implicit val frameRate: FrameRate

    // The public accessor for curframe
    def curFrame = 0

    def init()

    def update(deltaT: Long)

    def stepPhysics(deltaT: Long)

    def render()


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
    private[this]
    def microSequence(deltaT: Long) {
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
    def onStateUpdate(syncM: SyncMessage) {
        val SyncMessage(newActors, newFrame) = syncM
        // Synchronize the actors so we don't fuck
        // tons of things up
        actors.synchronized {
            // merge the actors
            actors.takeFrom(newActors)
            // Update with the difference between when
            // the frame was sent and when we get it
            microSequence(currentFrame - newFrame)

            // Apply the messages that we had but didn't
            // get a chance to
            applyMessages(newFrame)
        }
    }

    /**
     * The main game loop mostly just calls an inlined
     * microSequence with a deltaT
     */
    private[this]
    def loop() {
        var lastTime = 0L
        while (running) {
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
    def start() {
        if (!alreadyInit) {
            init()
            alreadyInit = true
        }
        running = true
        loop()
    }

    /**
     * Stops the game loop
     */
    def stop() {
        running = false
    }
}
