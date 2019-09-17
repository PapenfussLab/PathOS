/*
 * Copyright (c) 2017. PathOS Variant Curation System. All rights reserved.
 *
 * Organisation: Peter MacCallum Cancer Centre
 * Author: seleznev andrei
 */

package org.petermac.util
import com.rabbitmq.client.Channel
import com.rabbitmq.client.Connection
import com.rabbitmq.client.ConnectionFactory
import net.sf.json.util.JSONUtils

/**
 * class to communicate with to RabbitMQ as a producer
 * todo currently unused
 * Created by seleznev andrei on 9/5/17.
 */
import groovy.util.logging.Log4j
@Log4j
class RabbitTalker {

    private String host
    private Integer port
    private String exchange
    private String queue

    /**
     * constructor
     * @param rhost
     * @param rport
     * @param rexchange
     * @param rqueue
     */
    RabbitTalker(String rexchange, String rqueue) {
        Locator loc = Locator.instance
        host = loc.mqHost
        port = loc.mqPort.toInteger()

        exchange = rexchange
        queue = rqueue
    }

    /**
     * establish a c/n to the rabbitmq server and publish a message to a queue
     * todo this is not currently used, but should work as a dumb publisher
     * @param message
     * @return true on success false otherwise
     */
    boolean publishMessage(String message) {
        //should we be connecting in constructor first? what deliverymode, prio, etc? prob. not

        //return if the string is definitely not JSON
        //
        if (!JSONUtils.mayBeJSON(message)) {
            log.warn("Error: the message is not in JSON format")
            println("Error: the message is not in JSON format")
            //return false
        }

        //  establish a connection and publish the message
        //
        byte[] messageBodyBytes = message.getBytes()



        ConnectionFactory factory = new ConnectionFactory()
        factory.setHost( host )
        factory.setPort( port )

        Connection connection

        try {
                connection = factory.newConnection()
        } catch (Exception e) {
            log.warn("Error: failed to establish connection to ${host} ${port}")
            return false
        }
        Channel channel    = connection.createChannel()

        channel.exchangeDeclare( exchange, "fanout", true )
        channel.queueDeclare(queue, true, false, false, null);
        channel.queueBind(queue, exchange, "")

        channel.basicPublish( exchange, "", null, messageBodyBytes )

        //todo is there a THREAD hanging around that we need to kill? checkthis!
        println "Published"
        return true
    }

}
