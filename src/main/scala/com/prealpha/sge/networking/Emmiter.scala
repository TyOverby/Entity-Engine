package com.prealpha.sge.networking

import scala.collection.JavaConversions._
import concurrent.{Await, Promise}
import concurrent.duration.Duration.Inf

class Publisher[A] {
  private[this] val subscribers = new java.util.LinkedList[Observer[A]]

  def subscribe(sub: Observer[A]) {
    subscribers.+=(sub)
  }

  def unsubscribe(sub: Observer[A]) {
    subscribers.remove(sub)
  }

  def publish(a: A) {
    subscribers.map(_.onPublish(a))
  }

  val observe = Observer.observe[A](this) _
  val observeOnce = Observer.observeOnce[A](this) _
  val observeFilter = Observer.observeFilter[A](this) _
  val observeFilterOnce = Observer.observeFilterOnce[A](this) _
  val blockUntil = Observer.blockUntil[A](this) _

  def blockUntilObject[O >: A](o: O) = Observer.blockUntil[A](this) {
    case `o` => true
    case _ => false
  }

  def blockUnapply[B, O <: A](test: O => Option[B]) = Observer.blockUnapply[A, B, O](this)(test)
}


object Observer {
  def observe[A](pub: Publisher[A])(fn: A => Any) = new Observer[A](pub)(fn)

  def observeOnce[A](pub: Publisher[A])(fn: A => Any) = new OnceObserver[A](pub)(fn)

  def observeFilter[A](pub: Publisher[A])(test: A => Boolean)(fn: A => Any) = new ConditionalObserver[A](pub)(test)(fn)

  def observeFilterOnce[A](pub: Publisher[A])(test: A => Boolean)(fn: A => Any) = new ConditionalOnceObserver[A](pub)(test)(fn)

  def blockUntil[A](pub: Publisher[A])(test: A => Boolean): A = {
    val m = Promise[A]()
    new ConditionalOnceObserver[A](pub)(test)(a => m.success(a))
    Await.result(m.future, Inf)
  }

  def blockUnapply[A, B, O <: A](pub: Publisher[A])(unapp: O => Option[B]): B = {
    val m = Promise[O]()
    new ConditionalOnceObserverUn[A, B, O](pub)(unapp)(a => m.success(a.asInstanceOf[O]))
    unapp(Await.result(m.future, Inf)).get
  }
}

class Observer[A](pub: Publisher[A])(fn: A => Any) {
  pub.subscribe(this)

  def onPublish(a: A) {
    fn(a)
  }

  def unsubscribe() {
    pub.unsubscribe(this)
  }

  def resubscribe() {
    pub.subscribe(this)
  }
}

class OnceObserver[A](pub: Publisher[A])(fn: A => Any) extends Observer[A](pub)(fn) {
  override def onPublish(a: A) {
    fn(a)
    unsubscribe()
  }
}

class ConditionalObserver[A](pub: Publisher[A])(test: A => Boolean)(fn: A => Any) extends Observer[A](pub)(fn) {
  override def onPublish(a: A) {
    if (test(a)) {
      fn(a)
    }
  }
}

class ConditionalOnceObserver[A](pub: Publisher[A])(test: A => Boolean)(fn: A => Any) extends Observer[A](pub)(fn) {
  override def onPublish(a: A) {
    if (test(a)) {
      fn(a)
      unsubscribe()
    }
  }
}

class ConditionalOnceObserverUn[A, B, O <: A](pub: Publisher[A])(test: O => Option[B])(fn: A => Any) extends Observer[A](pub)(fn) {
  override def onPublish(a: A) {
    try{
      if (test(a.asInstanceOf[O]).isDefined) {
        fn(a.asInstanceOf[O])
        unsubscribe()
      }
    }
    catch{
      case e: ClassCastException => // it didn't match, don't do anything
    }
  }
}


