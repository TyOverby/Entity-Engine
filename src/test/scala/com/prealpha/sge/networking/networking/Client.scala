package com.prealpha.sge.networking.networking

import com.prealpha.sge.networking.AbstractConnectionThread
import java.net.Socket
import com.prealpha.sge.logging.log


/**
 * ONLY FOR USE IN TESTS
 * @param ip The ip address to connect to.
 * @param port The port to connect to.
 */
class Client(ip: String, port: Int) extends AbstractConnectionThread(new Socket(ip, port)){
  val observer  = messagePublisher.observe{
    case m => log.info(f"CLIENT: Got message: ${m.getClass.getSimpleName}")
  }
  val stopListening =  observer.unsubscribe _

  start()
}
