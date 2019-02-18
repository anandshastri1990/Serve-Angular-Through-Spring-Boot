package com.sample.demo.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.resource.ResourceResolver;
import org.springframework.web.servlet.resource.ResourceResolverChain;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;


@Configuration
public class ApplicationRouterConfiguration {

    @Configuration
    public static class RouterConfiguration implements WebMvcConfigurer {

        //Add handlers to serve static resources such as images, js, and, css files from specific locations under web application root, the classpath, and others.
        @Override
        public void addResourceHandlers(final ResourceHandlerRegistry registry) {
            registry.addResourceHandler("/**").addResourceLocations("classpath:/static/").setCachePeriod(4000)
                    .resourceChain(true).addResolver(new WebResourceResolver());
        }
    }
}

//Provides mechanisms for resolving an incoming request to an actual Resource and for obtaining the public URL path that clients should use when requesting the resource.
class WebResourceResolver implements ResourceResolver {

    //Resolve the supplied request and request path to a Resource that exists under one of the given resource locations.
    @Override
    public Resource resolveResource(HttpServletRequest httpServletRequest, String s, List<? extends Resource> list, ResourceResolverChain resourceResolverChain) {
        return resolve(s, list);
    }

    //Resolve the externally facing public URL path for clients to use to access the resource that is located at the given internal resource path.
    @Override
    public String resolveUrlPath(String s, List<? extends Resource> list, ResourceResolverChain resourceResolverChain) {
        final Resource resolvedResource = resolve(s, list);
        if (resolvedResource == null) {
            return null;
        }
        try {
            return resolvedResource.getURL().toString();
        } catch (Exception e) {
            return resolvedResource.getFilename();
        }
    }

    private Resource resolve(String s, List<? extends Resource> list) {
        return list.stream().map(item -> {
            try {
                return (item).createRelative(s);
            } catch (IOException e) {
                return null;
            }
        }).filter(resource -> resource != null && resource.exists()).findFirst().orElse(null);
    }
}
