package com.code_generator_server.config;

import com.code_generator_server.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    UserService userService;

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .csrf()
                    .disable()
                .headers()
                .contentTypeOptions()
                    .disable()
                .frameOptions()
                        .sameOrigin()
                .and()
                    .authorizeRequests()
                        .antMatchers("/").hasRole("USER")
                        .antMatchers("/main_page.html").hasRole("USER")
                        .antMatchers("/navigation_page/hyper_parameters.html").hasRole("USER")
                        .antMatchers("/navigation_page/model.html").hasRole("USER")
                        .antMatchers("/navigation_page/results.html").hasRole("USER")
                        .antMatchers("/css/**").hasRole("USER")
                        .antMatchers("/gui/**").hasRole("USER")
                        .antMatchers("/lib/**").hasRole("USER")
                        .antMatchers("/upload_layers").hasRole("USER")
                        .antMatchers("/upload_hyper_parameters").hasRole("USER")
                        .antMatchers("/download_layers").hasRole("USER")
                        .antMatchers("/download_hyper_parameters").hasRole("USER")
                        .antMatchers("/download_json").hasRole("USER")
                        .antMatchers("/register.html").not().fullyAuthenticated()
                .and()
                    .formLogin()
                        .loginPage("/login")
                        .defaultSuccessUrl("/")
                        .permitAll()
                .and()
                    .logout()
                        .logoutUrl("/logout")
                        .permitAll();
    }

    @Autowired
    protected void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userService).passwordEncoder(bCryptPasswordEncoder());
    }
}