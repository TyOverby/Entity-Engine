package com.prealpha.sge.messages

import com.prealpha.sge.components.Entity


trait Message

/**
 * An UpdateMessage is used just for sending messages to
 * an Entities component.
 */
case class UpdateMessage(entityId: Int) extends Message

case class CreateMessage(entity: Entity) extends Message

/**
 * HandshakeMessages will be used when a connection
 * is established.
 */
trait HandshakeMessage extends Message
case object GoodbyeMessage extends HandshakeMessage

/**
 * Used to sync an entire ActorCollection
 */
trait SyncMessage extends Message