package cd.presenceless.organisationservice.config;

import cd.presenceless.organisationservice.client.IdentityClient;
import org.springframework.cloud.client.loadbalancer.reactive.LoadBalancedExchangeFilterFunction;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@Configuration
public class WebClientConfig {

    private final LoadBalancedExchangeFilterFunction filterFunction;

    public WebClientConfig(LoadBalancedExchangeFilterFunction filterFunction) {
        this.filterFunction = filterFunction;
    }

    @Bean
    public WebClient identityWebClient() {
        return WebClient.builder()
                .baseUrl("http://identity-service")
                .filter(filterFunction)
                .build();
    }

    @Bean
    public IdentityClient identityClient() {
        return HttpServiceProxyFactory
                .builder(WebClientAdapter.forClient(identityWebClient()))
                .build()
                .createClient(IdentityClient.class);
    }
}
