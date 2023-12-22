package cd.presenceless.organisationservice.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableRabbit
public class RabbitmqConfig {
    @Value("${rabbitmq.organisation-service.exchange}")
    public String exchange;

    @Value("${rabbitmq.organisation-service.queue}")
    public String queueName;
    @Value("${rabbitmq.organisation-service.queue.application-notification}")
    public String applicationNotificationQueueName;
    @Value("${rabbitmq.organisation-service.queue.approval-notification}")
    public String approvalNotificationQueueName;
    @Value("${rabbitmq.organisation-service.queue.rejection-notification}")
    public String rejectionNotificationQueueName;
    @Value("${rabbitmq.organisation-service.queue.api-keys-notification}")
    public String apiKeysNotificationQueueName;

    @Value("${rabbitmq.organisation-service.routing-key}")
    public String routingKey;
    @Value("${rabbitmq.organisation-service.routing-key.application-notification}")
    public String applicationNotificationRoutingKey;
    @Value("${rabbitmq.organisation-service.routing-key.approval-notification}")
    public String approvalNotificationRoutingKey;
    @Value("${rabbitmq.organisation-service.routing-key.rejection-notification}")
    public String rejectionNotificationRoutingKey;
    @Value("${rabbitmq.organisation-service.routing-key.api-keys-notification}")
    public String apiKeysNotificationRoutingKey;

    @Bean
    public Queue queue() {
        return new Queue(queueName);
    }

    @Bean
    public Queue applicationNotificationQueue() {
        return new Queue(applicationNotificationQueueName);
    }

    @Bean
    public Queue approvalNotificationQueue() {
        return new Queue(approvalNotificationQueueName);
    }

    @Bean
    public Queue rejectionNotificationQueue() {
        return new Queue(rejectionNotificationQueueName);
    }

    @Bean
    public Queue apiKeysNotificationQueue() {
        return new Queue(apiKeysNotificationQueueName);
    }

    @Bean
    public TopicExchange exchange() {
        return new TopicExchange(exchange);
    }

    @Bean
    public Binding binding(Queue queue, TopicExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(routingKey);
    }

    @Bean
    public Binding applicationNotificationBinding(Queue applicationNotificationQueue, TopicExchange exchange) {
        return BindingBuilder
                .bind(applicationNotificationQueue)
                .to(exchange)
                .with(applicationNotificationRoutingKey);
    }

    @Bean
    public Binding approvalNotificationBinding(Queue approvalNotificationQueue, TopicExchange exchange) {
        return BindingBuilder
                .bind(approvalNotificationQueue)
                .to(exchange)
                .with(approvalNotificationRoutingKey);
    }

    @Bean
    public Binding rejectionNotificationBinding(Queue rejectionNotificationQueue, TopicExchange exchange) {
        return BindingBuilder
                .bind(rejectionNotificationQueue)
                .to(exchange)
                .with(rejectionNotificationRoutingKey);
    }

    @Bean
    public Binding apiKeysNotificationBinding(Queue apiKeysNotificationQueue, TopicExchange exchange) {
        return BindingBuilder
                .bind(apiKeysNotificationQueue)
                .to(exchange)
                .with(apiKeysNotificationRoutingKey);
    }

    @Bean
    public MessageConverter converter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public AmqpTemplate template(ConnectionFactory connectionFactory) {
        final var rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(converter());
        return rabbitTemplate;
    }
}
