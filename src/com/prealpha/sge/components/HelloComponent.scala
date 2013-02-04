package com.prealpha.sge.components

import com.prealpha.sge.messages.Message

trait HelloComponent extends Component{
    var word = "hello"

    abstract override
    def update(deltaM: Int){
        println(word)

        super.update(deltaM)
    }

    def handle(m: HelloMessage){
        this.word = m.newWord
    }
}

case class HelloMessage(newWord: String) extends Message
