package com.auto.apigateway.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerResponse;

import static org.springframework.cloud.gateway.server.mvc.filter.LoadBalancerFilterFunctions.lb;
import static org.springframework.cloud.gateway.server.mvc.handler.GatewayRouterFunctions.route;
import static org.springframework.cloud.gateway.server.mvc.handler.HandlerFunctions.http;
import static org.springframework.cloud.gateway.server.mvc.predicate.GatewayRequestPredicates.path;

@Configuration
public class GatewayRoutesConfig {

    @Bean
    public RouterFunction<ServerResponse> authServiceRoute(){
        return route("auth-service-route")
                .route(path("/api/auth/**"), http())
                .filter(lb("AUTH-SERVICE"))
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> userServiceRoute(){
        return route("user-service-route")
                .route(path("/api/users/**"), http())
                .filter(lb("AUTH-SERVICE"))
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> seriesServiceRoute() {
        return route("series-service-route")
                .route(path("/api/series/**"), http())
                .filter(lb("SERIES-SERVICE"))
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> examServiceRoute() {
        return route("exam-service-route")
                .route(path("/api/exam/**"), http())
                .filter(lb("SERIES-SERVICE"))
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> tutorialServiceRoute(){
        return route("tutorial-service-route")
                .route(path("/api/tutorials/**").or(path("/api/admin/tutorials/**")), http())
                .filter(lb("TUTORIAL-SERVICE"))
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> registrationServiceRoute(){
        return route("registration-service-route")
                .route(path("/api/registrations/**").or(path("/api/admin/registrations/**")), http())
                .filter(lb("REGISTRATION-SERVICE"))
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> notificationServiceRoute(){
        return route("notification-service-route")
                .route(path("/api/notifications/**"), http())
                .filter(lb("NOTIFICATION-SERVICE"))
                .build();
    }
}
