package com.caiya.kafka.spring.config;


import java.lang.reflect.Method;
import java.util.Arrays;

import com.caiya.kafka.listener.KafkaListenerErrorHandler;
import com.caiya.kafka.listener.MessageListenerContainer;
import com.caiya.kafka.spring.listener.adaptor.HandlerAdapter;
import com.caiya.kafka.spring.listener.adaptor.MessagingMessageListenerAdapter;
import com.caiya.kafka.spring.listener.adaptor.BatchMessagingMessageListenerAdapter;
import com.caiya.kafka.spring.listener.adaptor.RecordMessagingMessageListenerAdapter;
import com.caiya.kafka.support.converter.BatchMessageConverter;
import com.caiya.kafka.support.converter.MessageConverter;
import com.caiya.kafka.support.converter.RecordMessageConverter;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.handler.annotation.support.MessageHandlerMethodFactory;
import org.springframework.messaging.handler.invocation.InvocableHandlerMethod;
import org.springframework.util.Assert;

/**
 * A {@link KafkaListenerEndpoint} providing the method to invoke to process
 * an incoming message for this endpoint.
 *
 * @param <K> the key type.
 * @param <V> the value type.
 *
 * @author Stephane Nicoll
 * @author Artem Bilan
 * @author Gary Russell
 * @author Venil Noronha
 */
public class MethodKafkaListenerEndpoint<K, V> extends AbstractKafkaListenerEndpoint<K, V> {

    private Object bean;

    private Method method;

    private MessageHandlerMethodFactory messageHandlerMethodFactory;

    private KafkaListenerErrorHandler errorHandler;

    /**
     * Set the object instance that should manage this endpoint.
     * @param bean the target bean instance.
     */
    public void setBean(Object bean) {
        this.bean = bean;
    }

    public Object getBean() {
        return this.bean;
    }

    /**
     * Set the method to invoke to process a message managed by this endpoint.
     * @param method the target method for the {@link #bean}.
     */
    public void setMethod(Method method) {
        this.method = method;
    }

    public Method getMethod() {
        return this.method;
    }

    /**
     * Set the {@link MessageHandlerMethodFactory} to use to build the
     * {@link InvocableHandlerMethod} responsible to manage the invocation
     * of this endpoint.
     * @param messageHandlerMethodFactory the {@link MessageHandlerMethodFactory} instance.
     */
    public void setMessageHandlerMethodFactory(MessageHandlerMethodFactory messageHandlerMethodFactory) {
        this.messageHandlerMethodFactory = messageHandlerMethodFactory;
    }

    /**
     * Set the {@link KafkaListenerErrorHandler} to invoke if the listener method
     * throws an exception.
     * @param errorHandler the error handler.
     * @since 1.3
     */
    public void setErrorHandler(KafkaListenerErrorHandler errorHandler) {
        this.errorHandler = errorHandler;
    }

    private String getReplyTopic() {
        Method method = getMethod();
        if (method != null) {
            SendTo ann = AnnotationUtils.getAnnotation(method, SendTo.class);
            if (ann != null) {
                String[] destinations = ann.value();
                if (destinations.length > 1) {
                    throw new IllegalStateException("Invalid @" + SendTo.class.getSimpleName() + " annotation on '"
                            + method + "' one destination must be set (got " + Arrays.toString(destinations) + ")");
                }
                return destinations.length == 1 ? resolve(destinations[0]) : "";
            }
        }
        return null;
    }

    /**
     * Return the {@link MessageHandlerMethodFactory}.
     * @return the messageHandlerMethodFactory
     */
    protected MessageHandlerMethodFactory getMessageHandlerMethodFactory() {
        return this.messageHandlerMethodFactory;
    }

    @Override
    protected MessagingMessageListenerAdapter<K, V> createMessageListener(MessageListenerContainer container,
                                                                          MessageConverter messageConverter) {
        Assert.state(this.messageHandlerMethodFactory != null,
                "Could not create message listener - MessageHandlerMethodFactory not set");
        MessagingMessageListenerAdapter<K, V> messageListener = createMessageListenerInstance(messageConverter);
        messageListener.setHandlerMethod(configureListenerAdapter(messageListener));
        String replyTopic = getReplyTopic();
        if (replyTopic != null) {
            Assert.state(getReplyTemplate() != null, "a KafkaTemplate is required to support replies");
            messageListener.setReplyTopic(replyTopic);
        }
        if (getReplyTemplate() != null) {
            messageListener.setReplyTemplate(getReplyTemplate());
        }
        return messageListener;
    }

    /**
     * Create a {@link HandlerAdapter} for this listener adapter.
     * @param messageListener the listener adapter.
     * @return the handler adapter.
     */
    protected HandlerAdapter configureListenerAdapter(MessagingMessageListenerAdapter<K, V> messageListener) {
        InvocableHandlerMethod invocableHandlerMethod =
                this.messageHandlerMethodFactory.createInvocableHandlerMethod(getBean(), getMethod());
        return new HandlerAdapter(invocableHandlerMethod);
    }

    /**
     * Create an empty {@link MessagingMessageListenerAdapter} instance.
     * @param messageConverter the converter (may be null).
     * @return the {@link MessagingMessageListenerAdapter} instance.
     */
    protected MessagingMessageListenerAdapter<K, V> createMessageListenerInstance(MessageConverter messageConverter) {
        MessagingMessageListenerAdapter<K, V> listener;
        if (isBatchListener()) {
            BatchMessagingMessageListenerAdapter<K, V> messageListener = new BatchMessagingMessageListenerAdapter<K, V>(
                    this.bean, this.method, this.errorHandler);
            if (messageConverter instanceof BatchMessageConverter) {
                messageListener.setBatchMessageConverter((BatchMessageConverter) messageConverter);
            }
            listener = messageListener;
        }
        else {
            RecordMessagingMessageListenerAdapter<K, V> messageListener = new RecordMessagingMessageListenerAdapter<K, V>(
                    this.bean, this.method, this.errorHandler);
            if (messageConverter instanceof RecordMessageConverter) {
                messageListener.setMessageConverter((RecordMessageConverter) messageConverter);
            }
            listener = messageListener;
        }
        if (getBeanResolver() != null) {
            listener.setBeanResolver(getBeanResolver());
        }
        return listener;
    }

    private String resolve(String value) {
        if (getResolver() != null) {
            Object newValue = getResolver().evaluate(value, getBeanExpressionContext());
            Assert.isInstanceOf(String.class, newValue, "Invalid @SendTo expression");
            return (String) newValue;
        }
        else {
            return value;
        }
    }

    @Override
    protected StringBuilder getEndpointDescription() {
        return super.getEndpointDescription()
                .append(" | bean='").append(this.bean).append("'")
                .append(" | method='").append(this.method).append("'");
    }

}