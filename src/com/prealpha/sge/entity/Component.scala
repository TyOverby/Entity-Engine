package com.prealpha.sge.entity

/**
 * All Game Objects are built out of entity.
 * Each component stores its own data, and can access
 * data in other entity by the properties of mixins.
 *
 * Therefore, Components can modify the values that actually
 * belong in other entity, so beware!
 */
trait Component extends ComponentCap{

    /**
     * Called every frame, the update method is what prompts
     * changes to the game state.
     *
     * @param deltaM The time in milliseconds since the last
     *               call to update.
     */
    abstract override
    def update(deltaM: Long): Unit
}