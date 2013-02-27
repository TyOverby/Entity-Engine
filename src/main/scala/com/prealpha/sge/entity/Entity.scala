package com.prealpha.sge.entity

import com.prealpha.sge.messages.UpdateMessage

/**
 * Everything in the game is a Entity.  What makes
 * different GameObjects special is that they mixin
 * Components.  These entity define the properties
 * that make up the Entity.
 *
 * This constructor should be used on the client.
 *
 * @param id The identification number for the Entity
 */
abstract class Entity(val id: Int) extends Serializable{

    /**
     * If you use a Entity in a hash,
     * hash on just the id
     * @return
     */
    override
    def hashCode = id

    override
    def toString = "Entity id: " + id

    def handleMessage(message: UpdateMessage)

    def update(deltaM: Long){}
}
