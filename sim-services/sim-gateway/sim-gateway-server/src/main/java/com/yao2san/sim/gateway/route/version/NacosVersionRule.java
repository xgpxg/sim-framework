/*
package com.yao2san.sim.gateway.route.version;

import com.alibaba.cloud.nacos.ribbon.NacosServer;
import com.netflix.loadbalancer.*;
import com.netflix.zuul.context.RequestContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Configuration
@Slf4j
public class NacosVersionRule extends PredicateBasedRule {
    private final CompositePredicate predicate;

    public NacosVersionRule() {
        super();
        this.predicate = createCompositePredicate(new VersionPredicate(),
                new AvailabilityPredicate(this, null));
    }

    @Override
    public AbstractServerPredicate getPredicate() {
        return this.predicate;
    }

    private CompositePredicate createCompositePredicate(VersionPredicate versionPredicate,
                                                        AvailabilityPredicate availabilityPredicate) {
        return CompositePredicate.withPredicates(versionPredicate, availabilityPredicate)
                .build();
    }

    @Override
    public Server choose(Object key) {
        List<Server> reachableServers = getLoadBalancer().getReachableServers();



        return super.choose(key);
    }

    @ConditionalOnProperty(prefix = "spring.cloud.nacos.config.server-addr")
    private static class VersionPredicate extends AbstractServerPredicate {

        @Value("${sim.gateway.router.version.version-key:'version'}")
        private String VERSION_KEY = "version";
        @Override
        public boolean apply(PredicateKey predicateKey) {
            if (predicateKey == null) {
                return true;
            }
            RequestContext ctx = RequestContext.getCurrentContext();
            HttpServletRequest request = ctx.getRequest();
            //form header
            String version = request.getHeader(VERSION_KEY);
            if (version == null) {
                return true;
            }
            // TODO from cookie

            //TODO from param

            //TODO

            NacosServer nacosServer = (NacosServer) predicateKey.getServer();
            if (!nacosServer.getMetadata().containsKey(VERSION_KEY)) {
                return true;
            }
            return nacosServer.getMetadata().get(VERSION_KEY).equals(version);
        }
    }
}
*/
