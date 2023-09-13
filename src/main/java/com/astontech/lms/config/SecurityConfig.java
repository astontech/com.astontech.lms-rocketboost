package com.astontech.lms.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.ldap.authentication.ad.ActiveDirectoryLdapAuthenticationProvider;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    // region FIELDS / PROPERTIES


    @Value("${spring.security.authentication.method}")
    private String authenticationMethod;

    @Value("${spring.security.ldap.domain}")
    private String ldapDomain;

    @Value("${spring.security.ldap.url}")
    private String ldapUrl;

    private static final String[] AUTH_WHITELIST = {
        // -- swagger ui
        "/v2/api-docs",
        "/swagger-resources",
        "/swagger-resources/**",
        "/configuration/ui",
        "/configuration/security",
        "/swagger-ui.html",
        "/webjars/**"
        // other public endpoints of your API may be appended to this array
    };

    CustomSuccessHandler successHandler;

    //endregion

    public SecurityConfig(CustomSuccessHandler successHandler) {
        this.successHandler = successHandler;
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception  {

        if (authenticationMethod.equals("IN_MEMORY")) {

            auth.inMemoryAuthentication()
                    .withUser("test_admin").password("123").roles("ADMIN")
                    .and().withUser("test_mentee").password("123").roles("MENTEE");

        } else if (authenticationMethod.equals("LDAP")) {

            auth.authenticationProvider(activeDirectoryLdapAuthenticationProvider());

        }

    }

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurerAdapter() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedMethods("HEAD", "GET", "PUT", "POST", "DELETE", "PATCH");
            }
        };
    }

    protected void configure(HttpSecurity http) throws Exception {

        if (authenticationMethod.equals("NONE")) {

            http
                    .cors()
                    .and()
                    .csrf()
                    .disable()
                    .exceptionHandling()
                    .and()
                    .authorizeRequests()
                    .antMatchers("/").permitAll();

        } else if (authenticationMethod.equals("IN_MEMORY")) {

            http.authorizeRequests()
                    .antMatchers("/")
                    .access("hasRole('ADMIN')")
                    .anyRequest().authenticated().and()
                    .formLogin()
                    .successHandler(successHandler)
                    .failureUrl("/login?error=1")
                    .usernameParameter("username").passwordParameter("password");

        } else if (authenticationMethod.equals("LDAP")) {

            http
                    .authorizeRequests()
                    .antMatchers(AUTH_WHITELIST).permitAll()
                    .and()
                    .authorizeRequests()
                    .anyRequest().authenticated()
                    .and()
                    .formLogin()
                    .loginPage("/login")
                    .successHandler(successHandler)
                    .permitAll()
                    .and()
                    .logout().logoutSuccessUrl("/login?logout")
                    .and().csrf().disable();
        }
    }

    @Bean
    public ActiveDirectoryLdapAuthenticationProvider activeDirectoryLdapAuthenticationProvider() {
        ActiveDirectoryLdapAuthenticationProvider authenticationProvider =
                new ActiveDirectoryLdapAuthenticationProvider(ldapDomain, ldapUrl);

        authenticationProvider.setConvertSubErrorCodesToExceptions(true);
        authenticationProvider.setUseAuthenticationRequestCredentials(true);

        return authenticationProvider;
    }
}
