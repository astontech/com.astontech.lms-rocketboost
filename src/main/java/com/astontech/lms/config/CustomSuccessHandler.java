package com.astontech.lms.config;

import com.astontech.lms.domain.UserProfile;
import com.astontech.lms.services.UserProfileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.*;
import java.util.function.Consumer;

@Component
public class CustomSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private static final Logger logger = LoggerFactory.getLogger(CustomSuccessHandler.class);

    @Value("${spring.security.authentication.method}")
    String SECURITY_METHOD;

    @Autowired
    private UserProfileService userProfileService;

    private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

    @Override
    protected void handle(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {

        String targetUrl = determineTargetUrl(authentication, request.getSession());
        response.addHeader("Access-Control-Allow-Origin", "*");
        response.addHeader("Access-Control-Allow-Methods", "GET, POST, PATCH, PUTH, DELETE, OPTIONS");
        redirectStrategy.sendRedirect(request, response, targetUrl);
    };

    protected String determineTargetUrl(Authentication authentication, HttpSession session) {
        String url = "#/login/process";

        List<String> roles = getRolesFromAuthorities(authentication);
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        Consumer<String> setProfileInfo = role -> {
            session.setAttribute("role", role);
            session.setAttribute("username", userDetails.getUsername());
            createProfile(session);
        };

        if (isSuperUser(userDetails.getUsername()) || isMentor(roles)) {
            setProfileInfo.accept("ADMIN");
        }
        else if (isMentee(roles)) {
            setProfileInfo.accept("USER");
        }

        logger.info("User " + session.getAttribute("username") + " logged in.");
        logger.info(session.getAttribute("username") + " was assigned role: " + session.getAttribute("role"));

        return url;
    }

    private boolean isMentor(List<String> roles) {
        return roles.contains("Mentor-Dev") ||
                roles.contains("Mentor-Cisco") ||
                roles.contains("Mentor-Virl");
    }

    private boolean isMentee(List<String> roles) {
        return (roles.contains("Field Engineer - Cisco")  ||
                roles.contains("Field Engineer - Dev"));
    }

    private boolean isSuperUser(String username) {
        final List<String> superUsers =
                Arrays.asList("josh.nickleby@astontech.com");

        return superUsers.contains(username);
    }

    /**
     * Creates a UserProfile if it does not already exist in the database.
     * Otherwise if it does exist, it will initialize the UserProfile with a role if needed.
     *
     * @param session   the current HttpSession, derived from request.getSession() inside of handle()
     */
    private void createProfile(HttpSession session) {
        String username = session.getAttribute("username").toString();

        // Strip email signature if it exists
        // john.doe AND john.doe@astontech.com are both valid entries for LDAP
        if (session.getAttribute("username").toString().contains("@astontech.com")) {
            username = removeEmailSignature(session.getAttribute("username").toString());
        }

        UserProfile userProfile = userProfileService.findByUsername(username);

        if (userProfile == null) {
            logger.info("Creating new profile for " + username);
            userProfile = new UserProfile(username, session.getAttribute("role").toString());
            userProfileService.save(userProfile);
        }
        else {
            if (userProfile.getRole() == null) {
                userProfile.setRole(session.getAttribute("role").toString());
            }
        }
    }

    public static String removeEmailSignature(String name) {
        return name.contains("@") ? name.substring(0, name.indexOf("@")) : name;
    }

    private List<String> getRolesFromAuthorities(Authentication authentication) {
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        List<String> roles = new ArrayList<String>();

        for (GrantedAuthority grantedAuthority : authorities) {
            roles.add(grantedAuthority.getAuthority());
        }

        roles.forEach(r -> logger.info("Role Found: " + r));

        return roles;
    }

}

