package io.github.cloudintheking.jt809.config.rabbitmq;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

@Slf4j
@Configuration
public class RabbitMqConfiguration {
    @Autowired
    private AmqpAdmin amqpAdmin;


    @Autowired
    private RabbitTemplate rabbitTemplate;


    @Bean
    public TopicExchange topicExchange() {
        TopicExchange topicExchange = new TopicExchange("jt809");
        return topicExchange;
    }

    @Bean
    public Queue jt809() {
        Queue queue = new Queue("gps_real_location");
//        queue.addArgument("x-dead-letter-exchange", "dlx.exchange");
//        queue.addArgument("x-dead-letter-routing-key", "dlx.jt809");
        return queue;
    }

    @Bean
    public Binding binding1() {
        return BindingBuilder.bind(jt809()).to(topicExchange()).with("jt809.exgMsg.realLocation");
    }


    @PostConstruct
    public void init() {
        amqpAdmin.declareExchange(topicExchange());
        amqpAdmin.declareQueue(jt809());
        amqpAdmin.declareBinding(binding1());

        rabbitTemplate.setReturnCallback(new RabbitTemplate.ReturnCallback() {
            @Override
            public void returnedMessage(Message message, int replyCode, String replyText, String exchange, String routingKey) {
                log.info("消息发送失败：{}", replyText);
            }
        });

        rabbitTemplate.setConfirmCallback(new RabbitTemplate.ConfirmCallback() {
            @Override
            public void confirm(CorrelationData correlationData, boolean ack, String cause) {
                if (!ack) {
                    log.error("消息id:{}发送失败：{}", correlationData.getId(), cause);
                }
            }
        });
    }
}
