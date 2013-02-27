package com.prealpha.sge.data

import com.prealpha.sge.entity.Entity
import collection.mutable
import com.prealpha.sge.messages.UpdateMessage
import com.prealpha.sge.logging.log


class ActorCollection extends mutable.Iterable[Entity] with Serializable {
    private
    var actorset = new mutable.LinkedHashMap[Int, Entity]

    var count: Int = 0
    @transient val lock = new AnyRef with Serializable

    def fetchId(): Int = lock.synchronized {
        count += 1
        count
    }

    /**
     * Adds an entity to the actorset.  If an entity already exists there, then something
     * has gone terribly wrong, but we will try to agree with the definitive source
     * @param entity The entity to add
     */
    def add[E <: Entity](entity: E): E = {
        val key = entity.id
        if (actorset.contains(key)) {
            log.error(f"actorset already contains key: ${key} for object ${entity.getClass.getSimpleName}.  " +
                "replacing anyway :[")
        }
        actorset += key -> entity

        entity
    }

    def add(fn: Int => Entity): Entity = {
        val key = fetchId()
        val entity = fn(key)
        add(entity)
    }

    def get(id: Int): Entity = actorset(id)

    /**
     * Removes an entity from the
     * @param id
     */
    def remove(id: Int): Option[Entity] = {
        actorset.remove(id)
    }

    /**
     * Given an UpdateMessage, this will pass it off to the
     * correct entity
     * @param m The UpdateMessage to pass along
     */
    def passMessage(m: UpdateMessage) {
        val messageId = m.entityId
        val actor = actorset(messageId)
        actor.handleMessage(m)
    }

    /**
     * An iterator of all of the Entities in the collection
     * @return
     */
    def iterator: Iterator[Entity] = actorset.valuesIterator


    def takeFrom(other: ActorCollection) {
        this.actorset = other.actorset
    }
}
