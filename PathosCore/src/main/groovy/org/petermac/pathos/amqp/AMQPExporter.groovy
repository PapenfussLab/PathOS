package org.petermac.pathos.amqp

import org.petermac.pathos.api.*

import com.rabbitmq.client.AMQP
import com.rabbitmq.client.Channel
import com.rabbitmq.client.Channel
import com.rabbitmq.client.Connection
import com.rabbitmq.client.ConnectionFactory
import com.rabbitmq.client.DefaultConsumer

import groovy.util.logging.Log4j

/**
 * Plugin for exporting from PathOS to an AMQP server.
 *
 * Configuration:
 *
 * Basic connection parameters:
 *
 *      host: <hostname>
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
 *      queue?:
 *          name: <string>
 *          durable?: <boolean>
 *          exclusive?: <boolean>
 *
 * Data packing and format:
 *
 *          pack?: <boolean>    [default: true]
 *          encoding?: <string> [default: json]
 *
 */
@Log4j
class AMQPExporter implements ExportReceiverFactory {

    class AMQPExportReceiver implements ExportReceiver {
        private AMQPConnection amqp
        private Boolean pack
        private Codec encoder

        AMQPExportReceiver(Map config) {
            Set extraMayHave = []
            extraMayHave << 'pack'
            extraMayHave << 'encoding'
            amqp = new AMQPConnection(config, extraMayHave)

            pack = config['pack'] ? config['pack'].toBoolean() :  true
            def enc = config['encoding'] ?: 'json'
            encoder = Codecs.find(enc)
        }

        void receive(String domain, String action, Object data) {
            Object msg
            //log.info "exporting ${domain} ${action} ${data}"
            if (pack) {
                Map m = [:]
                m['domain'] = domain
                m['action'] = action
                m['data'] = data
                msg = m
            } else {
                msg = data
            }
            send(encoder.encode(msg))
        }

        private synchronized void send(String msg) {
            amqp.connectIfNecessary()
            try {
                def ex = amqp.exchConfig['name'] ?: ''
                def qq = amqp.exchConfig['name'] ? amqp.exchConfig['routingKey'] : amqp.queueConfig['name']
                //log.info "AMQP publish ${ex} ${qq} ${msg}"
                amqp.chan.basicPublish(ex, qq, null, msg.getBytes())
            } catch (Exception e) {
                amqp.close()
                throw e
            }
        }

        void close() {
            amqp.close()
        }

        void finalize() {
            close()
        }
    }

    ExportReceiver create(Map config) {
        return new AMQPExportReceiver(config)
    }
}
