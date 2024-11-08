package com.timebuddy.services;

import com.timebuddy.models.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Service class for handling JSON Web Tokens (JWT).
 * Provides methods for creating, extracting, and validating JWT tokens.
 */
@Service
public class JwtService {

    @Value("${JWT_EXPIRATION}")
    private long jwtExpiration;  // The expiration time of the token in milliseconds

    @Value("${JWT_SECRET}")
    private String secretKey;  // The secret key used to sign the JWT


    /**
     * Extracts a specific claim from a JWT token using a resolver function.
     *
     * @param token          The JWT token.
     * @param claimsResolver The resolver function to extract a specific claim.
     * @param <T>            The type of the claim to be extracted.
     * @return The extracted claim.
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Extracts all claims from a JWT token.
     *
     * @param token The JWT token.
     * @return The Claims object containing all claims.
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignInKey())  // Hämta den rätta signeringsnyckeln här
                .build()
                .parseClaimsJws(token)
                .getBody();  // Hämta alla claims från token
    }

    /**
     * Extracts the username (subject) from a JWT token.
     *
     * @param token The JWT token.
     * @return The username as a String.
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Extracts the expiration date from a JWT token.
     *
     * @param token The JWT token.
     * @return The expiration date as a Date object.
     */
    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Generates a signing key from the secret key.
     *
     * @return The signing key.
     */
    public Key getSignInKey() {
        // Get the secret key as a byte array and create a new SecretKeySpec
        return new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), SignatureAlgorithm.HS256.getJcaName());
    }


    /**
     * Generates a JWT token for a given user.
     *
     * @param user The user object.
     * @return The generated JWT token as a String.
     */
    public String generateToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("id", user.getId());
        return createToken(claims, user.getUsername());
    }

    /**
     * Creates a JWT token with extra claims and a username.
     *
     * @param extraClaims Additional claims to include in the token.
     * @param username    The username to set as the subject of the token.
     * @return The created JWT token as a String.
     */
    public String createToken(Map<String, Object> extraClaims, String username) {
        return Jwts.builder()
                .setClaims(extraClaims)  // Lägg till extra claims (exempelvis användarens ID)
                .setSubject(username)    // Ange användarnamn som subject
                .setIssuedAt(new Date(System.currentTimeMillis()))  // Sätt utställningstid
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration))  // Sätt utgångsdatum
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)  // Signera med den hemliga nyckeln
                .compact();  // Skapa och returnera den kompakta JWT-token
    }


    /**
     * Checks if a JWT token has expired.
     *
     * @param token The JWT token.
     * @return True if the token has expired, false otherwise.
     */
    public boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * Validates a JWT token by checking the username and expiration date.
     *
     * @param token       The JWT token.
     * @param userDetails The UserDetails object to compare the username.
     * @return True if the token is valid, false otherwise.
     */
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }
}
