//package edu.itmo.blps.filter;
//
//import edu.itmo.blps.JwtUtils;
//import edu.itmo.blps.model.user.User;
//import edu.itmo.blps.model.user.UserRepository;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpHeaders;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
//import org.springframework.stereotype.Component;
//import org.springframework.web.filter.OncePerRequestFilter;
//
//import javax.servlet.FilterChain;
//import javax.servlet.ServletException;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import java.io.IOException;
//import java.util.Objects;
//
//@Component
//@Slf4j
//public class JwtTokenFilter extends OncePerRequestFilter {
//
//	@Autowired
//	private UserRepository userRepo;
//
//	@Override
//	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
////		final String header = request.getHeader(HttpHeaders.AUTHORIZATION);
////		if (Objects.isNull(header) || !JwtUtils.validateAccessToken(header)) {
////			chain.doFilter(request, response);
////			return;
////		}
////		User userDetails = userRepo.findByUsername(JwtUtils.parseToken(header).getSubject()).orElse(null);
////		if (userDetails != null) {
////			log.debug("loaded user details {}", userDetails);
////			UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
////					userDetails, userDetails.getPassword(), userDetails.getAuthorities());
////			authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
////			SecurityContextHolder.getContext().setAuthentication(authentication);
////		}
//		chain.doFilter(request, response);
//	}
//}
