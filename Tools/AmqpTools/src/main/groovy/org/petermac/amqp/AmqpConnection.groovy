package org.petermac.amqp

import org.petermac.yaml.*
import java.util.UUID
import com.rabbitmq.client.AMQP
import com.rabbitmq.client.Channel
import com.rabbitmq.client.Connection
import com.rabbitmq.client.ConnectionFactory
import groovy.util.logging.Log4j

/**
 *
 * The AmqpConnection connection class manages a connection between the
 * client program and an AMQP server such as RabbitMQ.
 *
 */
@Log4j
class AmqpConnection {
    static private final Map exchDefaults = ['type':'fanout', 'durable':true, 'routingKey':'']
    static private final Map queueDefaults = ['durable':true, 'exclusive':false]

    private ConnectionFactory conFac;
    private Connection conn
    private Channel chan
    private Boolean retry

    public Map exchConfig
    public Map queueConfig

    /**
     *
     * Create a new AmqpConnection instance.
     *
     * The actual connection is not created untill it is first
     * used.
     *
     * @param config specifies the parameters of the configuration.
     * The following fields are allowed (required unless otherwise specified):
     * <dl>
     *  <dt>host</dt>
     *  <dd>The hostname to which the connection should be made.</dd>
     *  <dt>port</dt>
     *  <dd>The port on which the connection should be made.
     *      [default: 5672]</dd>
     *  <dt>username</dt>
     *  <dd>The username for authenticating to the AMQP server. [optional]</dd>
     *  <dt>password</dt>
     *  <dd>The password for authenticating to the AMQP server. [optional]</dd>
     *  <dt>exchange</dt>
     *  <dd>The configuration for an exchange. See below for details. [optional]</dd>
     *  <dt>queue</dt>
     *  <dd>The configuration for a queue. See below for details. [optional]</dd>
     *  <dt>retry</dt>
     *  <dd>If set to true, rather than throwing an exception if the attempt to connect fails, keep retrying until it succeeds. [default: false]</dd>
     * </dl>
     *
     * The exchange parameter is a map with the follwoing fields:
     * <dl>
     *  <dt>name</dt>
     *  <dd>The name of the exchange. [required]</dd>
     *  <dt>type</dt>
     *  <dd>The type of the exchange. [default: fanout]</dd>
     *  <dt>durable</dt>
     *  <dd>Should the exchange be durable? [default: true]</dd>
     *  <dt>routingKey</dt>
     *  <dd>The routing key for the exchange, if required. [optional]</dd>
     * </dl>
     *
     * The queue parameter is a map with the follwoing fields:
     * <dl>
     *  <dt>name</dt>
     *  <dd>The name of the queue. [required]</dd>
     *  <dt>durable</dt>
     *  <dd>Should the queue be durable? [default: true]</dd>
     *  <dt>exclusive</dt>
     *  <dd>Should the queue be exclusive? [default: false]</dd>
     * </dl>
     *
     */
    AmqpConnection(Map config) {
        Set mustHave = []
        mustHave << "host"

        Set mayHave = []
        mayHave << "exchange"
        mayHave << "password"
        mayHave << "port"
        mayHave << "queue"
        mayHave << "username"
        mayHave << "encoding"
        mayHave << "retry"

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

        retry = false

        config.each { param, val ->
            switch (param) {
                case 'host':
                    conFac.setHost(val)
                    break
                case 'port':
                    conFac.setPort(val.toInteger())
                    break
                case 'username':
                    conFac.setUsername(val)
                    break
                case 'password':
                    conFac.setPassword(val)
                    break
                case 'exchange':
                    YamlConfig.checkParams(val, exchMustHave, exchMayHave)
                    val = addDefaults(val, exchDefaults)
                    exchConfig['name'] = val['name']
                    exchConfig['type'] = val['type']
                    exchConfig['durable'] = val['durable'].toBoolean()
                    exchConfig['routingKey'] = val['routingKey']
                    break
                case 'queue':
                    YamlConfig.checkParams(val, queueMustHave, queueMayHave)
                    val = addDefaults(val, queueDefaults)
                    queueConfig['name'] = val['name']
                    queueConfig['durable'] = val['durable'].toBoolean()
                    queueConfig['exclusive'] = val['exclusive'].toBoolean()
                    println "queueConfig = ${queueConfig}"
                    break
                case 'encoding':
                    // ignore encoding. Not our problem!
                    break
                case 'retry':
                    retry = val.toBoolean()
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
        if (!retry) {
            tryToConnect()
            return
        }
        Integer n = 0
        while (true) {
            n += 1
            try {
                tryToConnect()
                if (n > 1) {
                    log.warn "connected after ${n} attempts"
                }
                return
            } catch (ConnectException e) {
                log.warn "attempt ${n} to connect to ${conFac.getHost()}:${conFac.getPort()}"
                log.warn "${e}"
            }
            sleep(1000)
        }
    }

    private synchronized void tryToConnect() {
        if (conn && chan) {
            return
        }
        if (!conn) {
            conn = conFac.newConnection(UUID.randomUUID().toString())
            conn.setId(UUID.randomUUID().toString())
            log.info "new connection to ${conFac.getHost()}:${conFac.getPort()} ${conn.getId()}"
            chan = null
        }
        if (!chan) {
            chan = conn.createChannel()
            log.debug "new channel ${conn.getId()}/${chan.getChannelNumber()}"
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

    public void apply(Closure thingToDo) {
        connectIfNecessary()
        try {
            thingToDo(conn, chan)
        } catch (Exception e) {
            if (chan) {
                log.error "nuking channel ${conn.getId()}/${chan.getChannelNumber()}"
                chan.close()
                chan = null
            }
            if (conn) {
                log.error "nuking connection ${conn.getId()}"
                conn.close()
                conn = null
            }
            throw e
        }
    }

    synchronized void close() {
        if (chan) {
            log.debug "closing channel ${conn.getId()}/${chan.getChannelNumber()}"
            chan.close()
            chan = null
        }
        if (conn) {
            log.debug "closing connection to ${conFac.getHost()}:${conFac.getPort()} ${conn.getId()}"
            conn.close()
            conn = null
        }
    }

    void finalize() {
        close()
    }

    /**
     * Compose a map containing parameter values with a map containing default values.
     *
     * @param items     the given parameters
     * @param defaults  the default parmeters to use if not given in items.
     * @return          the composed parameters.
     */
    static private Map addDefaults(Map items, final Map defaults) {
        Map everything = [:]
        everything << defaults
        everything << items
        return everything
    }
}
