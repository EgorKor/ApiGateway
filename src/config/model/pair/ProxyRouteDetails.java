package config.model.pair;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.function.Consumer;

/**
 * Класс инкапсулирующий детали маршрутизации конкретного шаблона*/
@Getter
@Setter
@Builder
public class ProxyRouteDetails {
    /*прокси маршрут*/
    private TemplateRoutePair templateRoutePair;
    /*действие после проксирования*/
    private Consumer<ProxyResponseThisServerExchangePair> proxyPostAction;
    /*флаг - нужно ли проксировать исходный URL запроса
    * true - нужно проксировать запрос на переданный хост и  исходный URL
    * false - нужно проксировать на переданный хост и URL*/
    private boolean proxyRequestURL;

}
