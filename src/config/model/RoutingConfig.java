package config.model;

import config.model.pair.ProxyResponseThisServerExchangePair;
import config.model.pair.ProxyRouteDetails;
import config.model.pair.TemplateRoutePair;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Сервер инкапсулирующий конфигурацию роутинга проксируемых запросов*/
@Getter
@Setter
public class RoutingConfig {
    private List<ProxyRouteDetails> templateRoutes;

    private RoutingConfig(){
        templateRoutes = new ArrayList<>();
    }

    public static RoutingConfig builder(){
        return new RoutingConfig();
    }

    public RoutingConfig addRoute(String url, String proxyURL, boolean proxyRequestURL){
        templateRoutes.add(ProxyRouteDetails.builder()
                .templateRoutePair(TemplateRoutePair.of(url, proxyURL))
                .proxyRequestURL(proxyRequestURL)
                .build());
        return this;
    }

    public RoutingConfig addRoute(String url, String proxyURL,
                                  Consumer<ProxyResponseThisServerExchangePair> postAction,
                                  boolean proxyRequestURL){
        templateRoutes.add(ProxyRouteDetails.builder()
                .templateRoutePair(TemplateRoutePair.of(url, proxyURL))
                .proxyRequestURL(proxyRequestURL)
                .proxyPostAction(postAction)
                .build()
        );
        return this;
    }



}
