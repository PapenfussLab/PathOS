package org.petermac.pathos.amqp

import org.petermac.pathos.api.*
import org.petermac.yaml.*

import com.rabbitmq.client.AMQP
import com.rabbitmq.client.Channel
import com.rabbitmq.client.Channel
import com.rabbitmq.client.Connection
import com.rabbitmq.client.ConnectionFactory
import com.rabbitmq.client.DefaultConsumer
import com.rabbitmq.client.Envelope

class AMQPConnection {
    private ConnectionFactory conFac;
    private Map exchConfig
    private Map queueConfig
    private Connection conn
    private Channel chan

    AMQPConnection(Map config, Set extraMayHave) {
        Set mustHave = []
        mustHave << "host"

        Set mayHave = []
        mayHave << "exchange"
        mayHave << "password"
        mayHave << "port"
        mayHave << "queue"
        mayHave << "username"
        mayHave.addAll(extraMayHave)

        Set exchMustHave = []
        exchMustHave << "name"

        Set exchMayHave = []
        exchMayHave << "type"
        exchMayHave << "durable"
        exchMayHave << "routingKey"

        Set queueMustHave = []
        queueMustHave << "name"

        Set queueMayHave = []
        queueMayHave << "durable"
        queueMayHave << "exclusive"

        YamlConfig.checkParams(config, mustHave, mayHave)

        conFac = new ConnectionFactory()
        exchConfig = [:]
        queueConfig = [:]

        config.each { param, val ->
            switch (param) {
                case 'host':
                    conFac.setHost(val)
                    break
                case 'port':
                    conFac.setPort(val)
                    break
                case 'username':
                    conFac.setUsername(val)
                    break
                case 'password':
                    conFac.setPassword(val)
                    break
                case 'exchange':
                    YamlConfig.checkParams(val, exchMustHave, exchMayHave)
                    exchConfig['name'] = val['name']
                    exchConfig['type'] = val['type'] ?: 'fanout'
                    exchConfig['durable'] = val['durable'] ? val['durable'].toBoolean() : true
                    exchConfig['routingKey'] = val['routingKey'] ?: ''
                    break
                case 'queue':
                    YamlConfig.checkParams(val, queueMustHave, queueMayHave)
                    queueConfig['name'] = val['name']
                    queueConfig['durable'] = val['durable'] ? val['durable'].toBoolean() : true
                    queueConfig['exclusive'] = val['exclusive'] ? val['exclusive'].toBoolean() : false
                    break
                default:
                    throw new UnknownParameterName(param)
            }
        }

        if (exchConfig.size() == 0 && queueConfig.size() == 0) {
            throw new InvalidParameterCombination("AMQPExporter requires an exchange or a queue")
        }
    }

    private synchronized void connectIfNecessary() {
        if (conn && chan) {
            return
        }
        if (!conn) {
            conn = conFac.newConnection()
            chan = null
        }
        if (!chan) {
            chan = conn.createChannel()
        }

        if (exchConfig.size() > 0) {
            chan.exchangeDeclare(exchConfig['name'], exchConfig['type'], exchConfig['durable'])
        }

        if (queueConfig.size() > 0) {
            chan.queueDeclare(queueConfig['name'], queueConfig['durable'], queueConfig['exclusive'], queueConfig['exclusive'], null)
        }

        if (exchConfig.size() > 0 && queueConfig.size() > 0) {
            chan.queueBind(queueConfig['name'], exchConfig['name'], exchConfig['routingKey'])
        }
    }

    synchronized void close() {
        if (chan) {
            chan.close()
            chan = null
        }
        if (conn) {
            conn.close()
            conn = null
        }
    }

    void finalize() {
        close()
    }
}
