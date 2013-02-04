package com.prealpha.sge.data

import com.prealpha.sge.components.Entity
import collection.mutable
import com.prealpha.sge.messages.UpdateMessage


class ActorCollection extends mutable.Iterable[Entity]{
    private[this]
    val actorset = new mutable.LinkedHashMap[Int,Entity]

    var count: Int = 0
    val lock = new AnyRef
    def fetchId(): Int = lock.synchronized {
        count += 1
        count
    }

    def add(entity: Entity){
        actorset += entity.id -> entity
    }
    def remove(entity: Entity){
        actorset.remove(entity.id)
    }

    def passMessage(m: UpdateMessage){
        val messageId = m.entityId
        val actor     = actorset(messageId)
        actor.handleMessage(m)
    }

    def iterator: Iterator[Entity] = actorset.valuesIterator
}
