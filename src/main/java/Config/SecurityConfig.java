package Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.firewall.HttpFirewall;
import org.springframework.security.web.firewall.StrictHttpFirewall;


@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Bean
    public AuthenticationManager authenticationManager() throws Exception {
        return super.authenticationManager();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()  // ✅ Disable CSRF protection (needed for H2 console)
                .authorizeRequests()
                .antMatchers(
                        "/api/users/register",
                        "/api/users/login",
                        "/api/users/getUser",
                        "/h2-console/**"
                ).permitAll()  // ✅ Allow public access to H2 console & user endpoints
                .anyRequest().authenticated()
                .and()
                .headers().frameOptions().disable()  // ✅ Allow iframes for H2 console
                .and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)  // ✅ Stateless session
                .and()
                .httpBasic()  // ✅ Enable Basic Authentication for protected endpoints
                .and()
                .formLogin().disable();  // ✅ Disable Spring's default login form
    }
    @Bean
    public HttpFirewall allowEncodedCharactersFirewall() {
        StrictHttpFirewall firewall = new StrictHttpFirewall();
        firewall.setAllowUrlEncodedPercent(true);  // ✅ Allow encoded "%" characters
        firewall.setAllowUrlEncodedSlash(true);  // ✅ Allow encoded "/" in URLs
        return firewall;
    }



}
