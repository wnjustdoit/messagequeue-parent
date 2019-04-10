package com.caiya.kafka.support;

/**
 * The Kafka specific message headers constants.
 *
 * @author Artem Bilan
 * @author Marius Bogoevici
 * @author Gary Russell
 * @author Biju Kunjummen
 */
public abstract class KafkaHeaders {

    private static final String PREFIX = "kafka_";

    /**
     * The header containing the topic when sending data to Kafka.
     */
    public static final String TOPIC = PREFIX + "topic";

    /**
     * The header containing the message key when sending data to Kafka.
     */
    public static final String MESSAGE_KEY = PREFIX + "messageKey";

    /**
     * The header containing the topic partition when sending data to Kafka.
     */
    public static final String PARTITION_ID = PREFIX + "partitionId";

    /**
     * The header for the partition offset.
     */
    public static final String OFFSET = PREFIX + "offset";

    /**
     * The header containing the raw data received from Kafka ({@code ConsumerRecord} or
     * {@code ConsumerRecords}). Usually used to enhance error messages.
     */
    public static final String RAW_DATA = PREFIX + "data";

    /**
     * The header containing the {@code RecordMetadata} object after successful send to the topic.
     */
    public static final String RECORD_METADATA = PREFIX + "recordMetadata";

    /**
     * The header for the {@link Acknowledgment}.
     */
    public static final String ACKNOWLEDGMENT = PREFIX + "acknowledgment";

    /**
     * The header for the {@code Consumer} object.
     */
    public static final String CONSUMER = PREFIX + "consumer";

    /**
     * The header containing the topic from which the message was received.
     */
    public static final String RECEIVED_TOPIC = PREFIX + "receivedTopic";

    /**
     * The header containing the message key for the received message.
     */
    public static final String RECEIVED_MESSAGE_KEY = PREFIX + "receivedMessageKey";

    /**
     * The header containing the topic partition for the received message.
     */
    public static final String RECEIVED_PARTITION_ID = PREFIX + "receivedPartitionId";

    /**
     * The header for holding the {@link org.apache.kafka.common.record.TimestampType type} of timestamp.
     */
    public static final String TIMESTAMP_TYPE = PREFIX + "timestampType";

    /**
     * The header for holding the timestamp of the producer record.
     */
    public static final String TIMESTAMP = PREFIX + "timestamp";

    /**
     * The header for holding the timestamp of the consumer record.
     */
    public static final String RECEIVED_TIMESTAMP = PREFIX + "receivedTimestamp";

    /**
     * The header for holding the native headers of the consumer record; only provided
     * if no header mapper is present.
     */
    public static final String NATIVE_HEADERS = PREFIX + "nativeHeaders";

    /**
     * The header for a list of Maps of converted native Kafka headers. Used for batch
     * listeners; the map at a particular list position corresponds to the data in the
     * payload list position.
     */
    public static final String BATCH_CONVERTED_HEADERS = PREFIX + "batchConvertedHeaders";

    /**
     * The header containing information to correlate requests/replies.
     * Type: byte[].
     *
     * @since 2.1.3
     */
    public static final String CORRELATION_ID = PREFIX + "correlationId";

    /**
     * The header containing the default reply topic.
     * Type: byte[].
     *
     * @since 2.1.3
     */
    public static final String REPLY_TOPIC = PREFIX + "replyTopic";

    /**
     * The header containing a partition number on which to send the reply.
     * Type: binary (int) in byte[].
     *
     * @since 2.1.3
     */
    public static final String REPLY_PARTITION = PREFIX + "replyPartition";

}
