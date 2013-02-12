package com.prealpha.sge.data

import com.prealpha.sge.entity.Entity
import collection.mutable
import com.prealpha.sge.messages.UpdateMessage
import com.prealpha.sge.logging.log


class ActorCollection extends mutable.Iterable[Entity]{
    private
    var actorset = new mutable.LinkedHashMap[Int,Entity]

    var count: Int = 0
    val lock = new AnyRef
    def fetchId(): Int = lock.synchronized {
        count += 1
        count
    }

    /**
     * Adds an entity to the actorset.  If an entity already exists there, then something
     * has gone terribly wrong, but we will try to agree with the definitive source
     * @param entity The entity to add
     */
    def add(entity: Entity){
        val key = entity.id
        if (actorset.contains(key)){
            log.error(f"actorset already contains key: ${key} for object ${entity.getClass.getSimpleName}.  " +
                "replacing anyway :[")
        }
        actorset += key -> entity
    }

    /**
     * Removes an entity from the
     * @param id
     */
    def remove(id: Int){
        actorset.remove(id)
    }

    /**
     * Given an UpdateMessage, this will pass it off to the
     * correct entity
     * @param m The UpdateMessage to pass along
     */
    def passMessage(m: UpdateMessage){
        val messageId = m.entityId
        val actor     = actorset(messageId)
        actor.handleMessage(m)
    }

    /**
     * An iterator of all of the Entities in the collection
     * @return
     */
    def iterator: Iterator[Entity] = actorset.valuesIterator


    def takeFrom(other: ActorCollection){
        this.actorset = other.actorset
    }
}
