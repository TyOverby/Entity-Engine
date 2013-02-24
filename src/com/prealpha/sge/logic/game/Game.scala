package com.prealpha.sge.logic.game

import com.prealpha.sge.logic.FrameRate

/**
 * The Game trait contains all of the logic
 * that should be done on both client and server.
 *
 * This includes things like physics simulations,
 * regular updates, shared-initialization
 */
trait Game {
    implicit val framerate: FrameRate
    protected[this] var running = false
    private[this] var already_init = false

    def init()

    def update(deltaT: Long)

    def stepPhysics(deltatT: Long)


    protected def loop()(implicit framerate: FrameRate)

    def start() {
        if (!already_init) {
            init()
            already_init = true
        }

        this.running = true
        loop()
    }

    def stop() {
        this.running = false
    }
}
