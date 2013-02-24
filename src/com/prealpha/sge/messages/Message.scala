package com.prealpha.sge.messages

import com.prealpha.sge.entity.Entity
import com.prealpha.sge.data.ActorCollection
import com.prealpha.sge.logic.Time


trait Message

/**
 * An UpdateMessage is used just for sending messages to
 * an Entities component.
 */
trait UpdateMessage extends Message{
    val entityId: Int
    val frame: Time
}

class DeleteMessage(val id: Int, val frame: Time) extends Message

class CreateMessage(val entity: Entity, val frame: Time) extends Message

/**
 * HandshakeMessages will be used when a connection
 * is established or disestablished.
 */
trait HandshakeMessage extends Message
case object GoodbyeMessage extends HandshakeMessage

/**
 * Used to sync an entire ActorCollection
 */
case class SyncMessage(newActors: ActorCollection, frame: Time) extends Message