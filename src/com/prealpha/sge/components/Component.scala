package com.prealpha.sge.components

/**
 * All Game Objects are built out of components.
 * Each component stores its own data, and can access
 * data in other components by the properties of mixins.
 *
 * Therefore, Components can modify the values that actually
 * belong in other components, so beware!
 */
trait Component extends ComponentCap{

    /**
     * Called every frame, the update method is what prompts
     * changes to the game state.
     *
     * @param deltaM The time in milliseconds since the last
     *               call to update.
     */
    def update(deltaM: Int): Unit
}