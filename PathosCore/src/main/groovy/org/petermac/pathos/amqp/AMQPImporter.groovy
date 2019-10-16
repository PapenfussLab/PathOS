package org.petermac.pathos.amqp

import org.petermac.yaml.*
import org.petermac.pathos.api.*
import org.petermac.amqp.AmqpConnection

import com.rabbitmq.client.AMQP
import com.rabbitmq.client.Channel
import com.rabbitmq.client.Connection
import com.rabbitmq.client.ConnectionFactory
import com.rabbitmq.client.Consumer
import com.rabbitmq.client.DefaultConsumer
import com.rabbitmq.client.Envelope
import groovy.util.logging.Log4j

/**
 * Plugin for importing from an AMQP server in to PathOS.
 *
 * Configuration:
 *
 * Basic connection parameters:
 *
 *      host: <hostname>        [default: localhost]
 *      port?: <number>         [default: 5672]
 *      username?: <username>
 *      password?: <password>
 *
 * Exchange and Queue parameters:
 *
 *      exchange?:
 *          name: <string>
 *          type?: <string>
 *          durable?: <string>
 *          routingKey?: <string>
 *      queue:
 *          name: <string>
 *          durable?: <boolean>
 *          exclusive?: <boolean>
 *
 * Data packing and format:
 *
 *          unpack?: <boolean>  [default: true]
 *          encoding?: <string> [default: json]
 *          domain?: <string>
 *          action?: <string>
 *
 */
@Log4j
class AMQPImporter implements ImporterFactory {
    class ImporterImpl implements Importer {
        private AmqpConnection amqp
        private Boolean unpack
        private Codec decoder
        private String domain
        private String action

        ImporterImpl(Map config) {
            amqp = new AmqpConnection(config)

            if (config['queue'] == null) {
                throw new RequiredParameterMissing('queue')
            }
            log.info "${config['queue']}"
            log.info "${amqp.queueConfig}"

            unpack = config['unpack'] ? config['unpack'].toBoolean() :  true
            def enc = config['encoding'] ?: 'json'
            decoder = Codecs.find(enc)

            if (!unpack) {
                if (config['domain'] == null) {
                    throw new InvalidParameterCombination("unpack=false implies domain is required")
                }
                domain = config['domain']
                if (config['action'] == null) {
                    throw new InvalidParameterCombination("unpack=false implies action is required")
                }
                action = config['action']
            }
        }

        class OurConsumer extends DefaultConsumer {
            private ImportReceiver receiver

            OurConsumer(Channel chan, ImportReceiver receiver) {
                super(chan)
                this.receiver = receiver
            }

            @Override
            public void handleDelivery(String consumerTag, Envelope env, AMQP.BasicProperties prop, byte[] body) {
                Boolean succeeded = false
                try {
                    handleDeliveryInner(consumerTag, env, prop, body)
                    succeeded = true
                } finally {
                    if (succeeded) {
                        this.getChannel().basicAck(env.getDeliveryTag(), false)
                    } else {
                        this.getChannel().basicNack(env.getDeliveryTag(), false, true)
                    }
                }
            }

            private void handleDeliveryInner(String consumerTag, Envelope env, AMQP.BasicProperties prop, byte[] body) {
                String msg = new String(body, "UTF-8")
                String dom
                String act
                Object dat
                Object obj = decoder.decode(msg)
                if (unpack) {
                    Map m = obj
                    dom = m['domain']
                    act = m['action']
                    dat = m['data']
                } else {
                    dom = domain
                    act = action
                    dat = obj
                }
                receiver.receive(dom, act, dat)
            }
        }

        void importData(ImportReceiver receiver) {
            amqp.apply { theConn, theChan ->
                Consumer consumer = new OurConsumer(theChan, receiver)
                theChan.basicConsume(amqp.queueConfig['name'], false, consumer)
            }
        }
    }

    Importer create(Map config) {
        return new ImporterImpl(config)
    }
}
