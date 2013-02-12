package com.prealpha.sge.messages

import com.prealpha.sge.entity.Entity
import com.prealpha.sge.data.ActorCollection
import com.prealpha.sge.logic.Frame


trait Message{
    val frame: Frame
}

/**
 * An UpdateMessage is used just for sending messages to
 * an Entities component.
 */
case class UpdateMessage(entityId: Int, frame: Frame) extends Message

case class DeleteMessage(id: Int, frame: Frame) extends Message

case class CreateMessage(entity: Entity, frame: Frame) extends Message

/**
 * HandshakeMessages will be used when a connection
 * is established or disestablished.
 */
trait HandshakeMessage extends Message
case object GoodbyeMessage extends HandshakeMessage{
    val frame = Frame(0,0) // they don't actually care at this point
}

/**
 * Used to sync an entire ActorCollection
 */
case class SyncMessage(newActors: ActorCollection, frame: Frame) extends Message