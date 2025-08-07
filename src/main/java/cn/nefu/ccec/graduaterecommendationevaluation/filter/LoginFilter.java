package cn.nefu.ccec.graduaterecommendationevaluation.filter;

import cn.nefu.ccec.graduaterecommendationevaluation.component.JWTComponent;
import cn.nefu.ccec.graduaterecommendationevaluation.exception.Code;
import cn.nefu.ccec.graduaterecommendationevaluation.vo.TokenAttribute;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import org.springframework.web.util.pattern.PathPattern;
import org.springframework.web.util.pattern.PathPatternParser;
import reactor.core.publisher.Mono;

import java.util.Map;

@Component
@Slf4j
@Order(1)
@RequiredArgsConstructor
public class LoginFilter implements WebFilter {

    private final PathPattern includes = new PathPatternParser().parse("/api/**");
    private final PathPattern openPath = new PathPatternParser().parse("/api/open/**");

    private final JWTComponent jwtComponent;
    private final ResponseHelper responseHelper;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        if (!includes.matches(request.getPath().pathWithinApplication())) {
            return chain.filter(exchange);
        }
        if (openPath.matches(request.getPath().pathWithinApplication())) {
            return chain.filter(exchange);
        }
        String token = request.getHeaders().getFirst(TokenAttribute.TOKEN);
        if (token == null) {
            return responseHelper.response(Code.UNAUTHORIZED, exchange);
        }
        var decode = jwtComponent.decode(token);
        Map<String, Object> attributes = exchange.getAttributes();
        attributes.put(TokenAttribute.UID, decode.getClaim(TokenAttribute.UID).asLong());
        attributes.put(TokenAttribute.ROLE, decode.getClaim(TokenAttribute.ROLE).asString());
        //
        if (!decode.getClaim(TokenAttribute.COLLID).isMissing()) {
            attributes.put(TokenAttribute.COLLID, decode.getClaim(TokenAttribute.COLLID).asLong());
        }
        if(!decode.getClaim(TokenAttribute.CATID).isMissing()){
            attributes.put(TokenAttribute.CATID, decode.getClaim(TokenAttribute.CATID).asLong());
        }
        if(!decode.getClaim(TokenAttribute.MAGORID).isMissing()){
            attributes.put(TokenAttribute.MAGORID, decode.getClaim(TokenAttribute.MAGORID).asLong());
        }
        return chain.filter(exchange);
    }
}
