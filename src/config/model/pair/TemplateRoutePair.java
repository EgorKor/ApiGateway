package config.model.pair;

import lombok.Getter;
import lombok.Setter;

/**
 * Класс инкапсулирующий шаблон URI например api/v1/*
 * и хост ИЛИ хост + url на который нужно проксировать запрос
 * с этого шаблона. Например ("api/v1/*","localhost:8080") ИЛИ
 * ("api/v1/auth","localhost:8080/api/v1/auth") */
@Getter
@Setter
public class TemplateRoutePair {
    private String template;
    private String proxyURL;

    private TemplateRoutePair(String template, String proxyURL) {
        this.template = template;
        this.proxyURL = proxyURL;
    }

    public static TemplateRoutePair of(String template, String url){
        return new TemplateRoutePair(template, url);
    }
}
