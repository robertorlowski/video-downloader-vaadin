package pl.luxtech.movie.server.servlet;

import org.atmosphere.cpr.ApplicationConfig;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import com.vaadin.spring.boot.internal.VaadinServletConfiguration;
import com.vaadin.spring.boot.internal.VaadinServletConfigurationProperties;

@Configuration
@EnableConfigurationProperties(VaadinServletConfigurationProperties.class)
@Import(VaadinServletConfiguration.class)
public class VideoVaadinServletConfiguration extends VaadinServletConfiguration
{
    @Bean
    protected
    ServletRegistrationBean vaadinServletRegistration()
    {
        return createServletRegistrationBean();
    }

    @Override
    protected void addInitParameters(
            ServletRegistrationBean servletRegistrationBean)
    {
        super.addInitParameters(servletRegistrationBean);
        
        addInitParameter(servletRegistrationBean, "org.atmosphere.websocket.suppressJSR356", "true");    
        addInitParameter(servletRegistrationBean, ApplicationConfig.JSR356_MAPPING_PATH, "/VAADIN");
    }
    
    private void addInitParameter(ServletRegistrationBean servletRegistrationBean, String paramName, String propertyValue) {
        if (propertyValue != null) {
            getLogger().info("Set servlet init parameter [{}] = [{}]", paramName, propertyValue);
            servletRegistrationBean.addInitParameter(paramName, propertyValue);
        }
    }
}