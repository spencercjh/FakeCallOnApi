package spencercjh.top.fastrun.common.security;

import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

/**
 * @author spencercjh
 */
@Configuration
@EnableWebSecurity
@Log4j2
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        log.info("配置Security");
        http
                .authorizeRequests()
                .antMatchers("/", "/run").permitAll()
                .anyRequest().authenticated()
                .and()
                .formLogin()
                .defaultSuccessUrl("/swgger-ui.html")
                .permitAll()
                .and()
                .logout()
                .permitAll();
        http
                .csrf().disable();
    }
}