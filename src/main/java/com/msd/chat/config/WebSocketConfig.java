package com.msd.chat.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.messaging.converter.DefaultContentTypeResolver;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.security.authorization.*;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@RequiredArgsConstructor
@EnableWebSocketMessageBroker
// @EnableWebSocketSecurity
@Order(Ordered.HIGHEST_PRECEDENCE + 99)
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
  private final WebSocketAuthInterceptor webSocketAuthInterceptor;

  @Override
  public boolean configureMessageConverters(List<MessageConverter> messageConverters) {
    DefaultContentTypeResolver resolver = new DefaultContentTypeResolver();
    resolver.setDefaultMimeType(MimeTypeUtils.APPLICATION_JSON);

    MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();

    converter.setObjectMapper(new ObjectMapper());

    converter.setContentTypeResolver(resolver);

    messageConverters.add(converter);

    return false;
  }

  @Override
  public void registerStompEndpoints(StompEndpointRegistry registry) {
    registry.addEndpoint("/ws").setAllowedOrigins("*");
    //        .setHandshakeHandler(websocketHandshakeHandler);
  }

  @Override
  public void configureMessageBroker(MessageBrokerRegistry registry) {
    registry.enableSimpleBroker("/messages");
    registry.setApplicationDestinationPrefixes("/app");
    registry.setUserDestinationPrefix("/user");
  }

  @Bean
  public Jackson2ObjectMapperBuilderCustomizer jsonCustomizer() {
    return builder -> builder.modules(new JavaTimeModule());
  }

  @Override
  public void configureClientInboundChannel(ChannelRegistration registration) {
    WebSocketMessageBrokerConfigurer.super.configureClientInboundChannel(registration);
    registration.interceptors(webSocketAuthInterceptor);
  }
  //
  //  @Bean
  //  public AuthorizationManager<Message<?>>
  // messageAuthorizationManager(MessageMatcherDelegatingAuthorizationManager.Builder manager) {
  //    manager.nullDestMatcher().authenticated()
  //            .simpSubscribeDestMatchers("/user/queue/errors").permitAll()
  //            .simpDestMatchers("/app/**").hasRole("USER")
  //            .simpSubscribeDestMatchers("/user/**").hasRole("USER")
  //            .simpTypeMatchers(MESSAGE, SUBSCRIBE).permitAll()
  //            .anyMessage().denyAll();
  //
  //    return manager.build();
  //  }
}
