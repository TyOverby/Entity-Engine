package com.prealpha.sge.components


/**
 * This is a cap that fulfils everything that a
 * Component needs to be able to propogate
 */
trait ComponentCap extends Entity{
    /**
     * Do nothing.  This is here for the traits to
     * be able to propogate their update messages
     *
     * @param deltaM The time (in milliseconds) since
     *               the last update.
     */
    def update(deltaM: Int){}
}
