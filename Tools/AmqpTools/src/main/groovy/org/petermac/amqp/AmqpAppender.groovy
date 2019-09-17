package org.petermac.amqp

import com.rabbitmq.client.Address
import com.rabbitmq.client.Channel
import com.rabbitmq.client.Connection
import com.rabbitmq.client.ConnectionFactory

import org.apache.log4j.AppenderSkeleton
import org.apache.log4j.spi.LoggingEvent

import java.text.SimpleDateFormat

import org.yaml.snakeyaml.Yaml

import java.lang.management.ManagementFactory
import java.lang.StackTraceElement

/*
 * Log4j 1.x appender.
 *
 * Configuration example:
 *
 * log4j.appender.Amqp=org.petermac.amqp.AmqpAppender
 * log4j.append.Ampq.host=my-rabbitmq-server
 * log4j.append.Ampq.port=5672
 * log4j.append.Ampq.exchange=my-exch
 * log4j.append.Ampq.encoding='yaml'
 *
 * Configuration Parameters:
 *  host            - hostname for the AMQP connection [default: localhost]
 *  port            - port for the AMQP connection [default: 5672]
 *  username        - username for the AMQP connection [default: null]
 *  password        - password for the AMQP connection [default: null]
 *  exchange        - if set, messages will be published to the named exchange
 *  exchangeType    - the type of the exchange [default: fanout]
 *  exchangeDurable - is the exchange durable? [default: true]
 *  exchangeRoutingKey - routing key for exchange [default: null]
 *  queue           - if set, messages will be published to the named queue (via the exchange if specified)
 *  queueDurable    - is the queue durable? [default: true]
 *  queueExclusive  - is the queue exclusive? [default: false]
 *  encoding        - set to 'yaml' to use yaml encoding. Default behaviour is to send log formatted strings.
 *  label           - set a label to be attached to yaml messages [default: null]
 *  topic           - if set, the template for topic strings for messages. 
 *  exceptions      - if true, include stacktraces in messages [default: false]
 *
 */
class AmqpAppender extends AppenderSkeleton {
    
    private ConnectionFactory factory
    private Connection connection
    private Channel channel
    private String identifier
    private String host
    private int port
    private String username
    private String password
    private String exchange
    private String exchangeType
    private Boolean exchangeDurable
    private String exchangeRoutingKey
    private String queue
    private Boolean queueDurable
    private Boolean queueExclusive
    private String encoding
    private String myHostName
    private String label
    private String topic
    private Boolean exceptions
    private List topicParts
    private Yaml yaml
    private AmqpConnection amqp
    private SimpleDateFormat timestampFormatter

    AmqpAppender() {
        factory = new ConnectionFactory()
        host = "localhost"
        port = 5672
        exchangeType = 'fanout'
        exchangeDurable = true
        exchangeRoutingKey = ''
        queueDurable = true
        queueExclusive = false
        timestampFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS")
        exceptions = false
    }

    /**
     * Submits LoggingEvent for publishing if it reaches severity threshold.
     * @param loggingEvent
     */
    @Override
    protected void append(LoggingEvent loggingEvent) {

        Map e = [:]

        if (!myHostName) {
            myHostName = ManagementFactory.getRuntimeMXBean().getName()
        }
        e['host'] = myHostName
        if (label) {
            e['label'] = label
        }
        e['level'] = loggingEvent.getLevel().toString()
        e['logger'] = loggingEvent.getLoggerName()
        e['message'] = loggingEvent.getRenderedMessage()
        if (loggingEvent.getProperties().size() > 0) {
            e['properties'] = loggingEvent.getProperties()
        }
        long ts = loggingEvent.getTimeStamp()
        Date d = new Date(ts)
        e['timestamp'] = timestampFormatter.format(d)
        e['thread'] = loggingEvent.getThreadName()
        if (exceptions && loggingEvent.getThrowableInformation()) {
            e['throwable'] = throwable(loggingEvent.getThrowableInformation().getThrowable())
        }

        String topicStr
        if (topic) {
            if (!topicParts) {
                topicParts = topic.tokenize('.')
            }
            List parts = []
            for (int i = 0; i < topicParts.size(); i++) {
                String part = topicParts[i]
                if (part.startsWith('$')) {
                    String nm = part.substring(1)
                    parts << e[nm]
                } else {
                    parts << part
                }
            }
            topicStr = parts.join('.')
        }

        String payload
        if (encoding && encoding == 'yaml') {
            if (!yaml) {
                yaml = new Yaml()
            }
            payload = yaml.dump(e)
        } else {
            payload = layout.format(loggingEvent)
        }

        amqp.apply { conn, chan ->
            def ex = amqp.exchConfig['name'] ?: ''
            def qq = amqp.exchConfig['name'] ? amqp.exchConfig['routingKey'] : amqp.queueConfig['name']
            if (topicStr) {
                qq = topicStr
            }
            amqp.chan.basicPublish(ex, qq, null, payload.getBytes())
        }
    }

    Map throwable(Throwable t) {
        Map res = [:]
        if (t.getMessage()) {
            res['message'] = t.getMessage()
        }
        if (t.getCause()) {
            res['cause'] = throwable(t.getCause())
        }
        t.fillInStackTrace()
        res['stack'] = []
        StackTraceElement[] stk = t.getStackTrace()
        for (int i = 0; i < stk.length; i++) {
            StackTraceElement s = stk[i]
            Map e = [:]
            e['class'] = s.getClassName()
            e['method'] = s.getMethodName()
            e['file'] = s.getFileName()
            e['line'] = s.getLineNumber()
            res['stack'] << e
        }
        return res
    }

    /**
     * Creates the connection, channel to RabbitMQ. Declares exchange and queue
     * @see AppenderSkeleton
     */
    @Override
     void activateOptions() {
        super.activateOptions()

        Map conf = [:]
        conf['host'] = host
        conf['port'] = port
        if (username) {
            conf['username'] = username
        }
        if (password) {
            conf['password'] = password
        }
        if (exchange) {
            conf['exchange'] = [:]
            conf['exchange']['name'] = exchange
            conf['exchange']['type'] = exchangeType
            conf['exchange']['durable'] = exchangeDurable
            conf['exchange']['routingKey'] = exchangeRoutingKey
        }
        if (queue) {
            conf['queue'] = [:]
            conf['queue']['name'] = queue
            conf['queue']['durable'] = queueDurable
            conf['queue']['exclusive'] = queueExclusive
        }
        amqp = new AmqpConnection(conf)
    }

    /**
     * Returns host property as set in appender configuration
     * @return host
     */
     String getHost() {
        return host
    }

    /**
     * Sets host property from parameter in appender configuration
     * @param host
     */
     void setHost(String host) {
        this.host = host
    }

    /**
     * Returns port property as set in appender configuration
     * @return port
     */
     int getPort() {
        return port
    }

    /**
     * Sets port property from parameter in appender configuration
     * @param port
     */
     void setPort(int port) {
        this.port = port
    }

    /**
     * Returns username property as set in appender configuration
     * @return
     */
     String getUsername() {
        return username
    }

    /**
     * Sets username property from parameter in appender configuration
     * @param username
     */
     void setUsername(String username) {
        this.username = username
    }

    /**
     * Returns password property as set in appender configuration
     * @return
     */
     String getPassword() {
        return password
    }

    /**
     * Sets password property from parameter in appender configuration
     * @param password
     */
     void setPassword(String password) {
        this.password = password
    }

    /**
     * Returns exchange property as set in appender configuration
     * @return
     */
     String getExchange() {
        return exchange
    }

    /**
     * Sets exchange property from parameter in appender configuration
     * @param exchange
     */
     void setExchange(String exchange) {
        this.exchange = exchange
    }

    /**
     * Returns exchangeType property as set in appender configuration
     * @return
     */
     String getExchangeType() {
        return exchangeType
    }

    /**
     * Sets exchange property from parameter in appender configuration
     * @param exchange
     */
     void setExchangeType(String exchangeType) {
        this.exchangeType = exchangeType
    }

    /**
     * Returns exchangeDurable property as set in appender configuration
     * @return
     */
     String getExchangeDurable() {
        return exchangeDurable
    }

    /**
     * Sets exchangeDurable property from parameter in appender configuration
     * @param exchangeDurable
     */
     void setExchangeDurable(String exchangeDurable) {
        this.exchangeDurable = exchangeDurable
    }

    /**
     * Returns exchangeRoutingKey property as set in appender configuration
     * @return
     */
     String getExchangeRoutingKey() {
        return exchangeRoutingKey
    }

    /**
     * Sets exchangeRoutingKey property from parameter in appender configuration
     * @param exchangeRoutingKey
     */
     void setExchangeRoutingKey(String exchangeRoutingKey) {
        this.exchangeRoutingKey = exchangeRoutingKey
    }

    /**
     * Returns queue property as set in appender configuration
     * @return
     */
     String getQueue() {
        return queue
    }

    /**
     * Sets queue property from parameter in appender configuration
     * @param exchange
     */
     void setQueue(String queue) {
        this.queue = queue
    }

    /**
     * Returns queueDurable property as set in appender configuration
     * @return
     */
     String getQueueDurable() {
        return queueDurable
    }

    /**
     * Sets queueDurable property from parameter in appender configuration
     * @param queueDurable
     */
     void setQueueDurable(String queueDurable) {
        this.queueDurable = queueDurable
    }

    /**
     * Returns queueDurable property as set in appender configuration
     * @return
     */
     String getQueueExclusive() {
        return queueExclusive
    }

    /**
     * Sets queueExclusive property from parameter in appender configuration
     * @param queueExclusive
     */
     void setQueueExclusive(String queueExclusive) {
        this.queueExclusive = queueExclusive
    }

    /**
     * Returns encoding property as set in appender configuration
     * @return
     */
     String getEncoding() {
        return queueExclusive
    }

    /**
     * Sets encoding property from parameter in appender configuration
     * @param queueExclusive
     */
     void setEncoding(String encoding) {
        this.encoding = encoding
    }

    /**
     * Returns label property as set in appender configuration
     * @return label
     */
     String getLabel() {
        return label
    }

    /**
     * Sets label property from parameter in appender configuration
     * @param label
     */
     void setLabel(String label) {
        this.label = label
    }

    /**
     * Returns topic property as set in appender configuration
     * @return topic
     */
     String getTopic() {
        return topic
    }

    /**
     * Sets topic property from parameter in appender configuration
     * @param topic
     */
     void setTopic(String topic) {
        this.topic = topic
    }

    /**
     * Returns exceptions property as set in appender configuration
     * @return exceptions
     */
     String getExceptions() {
        return exceptions
    }

    /**
     * Sets exceptions property from parameter in appender configuration
     * @param topic
     */
     void setExceptions(String exceptions) {
        this.exceptions = exceptions
    }

    void close() {
        if (amqp) {
            amqp.close()
            amqp = null
        }
    }

    /**
     * Ensures that a Layout property is required
     * @return
     */
    @Override
     boolean requiresLayout() {
        return (encoding == null)
    }
}
