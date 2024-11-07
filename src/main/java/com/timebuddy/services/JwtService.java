package com.timebuddy.services;

import com.timebuddy.models.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cglib.core.internal.Function;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Serviceklass för hantering av JWT (JSON Web Tokens).
 * Den här klassen tillhandahåller metoder för att skapa, extrahera och validera JWT.
 */
@Service
public class JwtService {

    @Value("${jwt.expiration}")
    private long jwtExpiration;  // Lösenordets livslängd i millisekunder

    @Value("${jwt.secret}")
    private String secretKey;  // Hemlig nyckel för att signera JWT

    /**
     * Extraherar ett krav från tokenet baserat på en given resolver.
     *
     * @param token         JWT-token som krav ska extraheras från.
     * @param claimsResolver Funktion för att hantera de extraherade kraven.
     * @param <T>          Typ av värdet som ska extraheras.
     * @return Det extraherade värdet av typen T.
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);  // Hämta alla krav från tokenet
        return claimsResolver.apply(claims);  // Tillämpa resolvern för att få det specifika värdet
    }

    /**
     * Extraherar alla krav från en JWT-token.
     *
     * @param token JWT-token som krav ska extraheras från.
     * @return Claims-objekt som innehåller alla krav.
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigniInKey())  // Ange signeringsnyckeln
                .build()
                .parseClaimsJws(token)
                .getBody();  // Hämta kroppen av JWT
    }

    /**
     * Extraherar användarnamnet från JWT-tokenet.
     *
     * @param token JWT-token som användarnamnet ska extraheras från.
     * @return Användarnamnet som en sträng.
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);  // Extrahera användarnamnet från token
    }

    /**
     * Extraherar utgångsdatumet för JWT-tokenet.
     *
     * @param token JWT-token som utgångsdatumet ska extraheras från.
     * @return Utgångsdatumet som ett Date-objekt.
     */
    Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);  // Extrahera utgångsdatumet
    }

    /**
     * Extraherar rollerna från UserDetails-objektet.
     *
     * @param userDetails UserDetails-objektet som innehåller roller.
     * @return En lista med roller som strängar.
     */
    private Collection<String> extractRoles(UserDetails userDetails) {
        return userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)  // Hämta varje roll som en sträng
                .collect(Collectors.toList());
    }

    /**
     * Hämtar signeringsnyckeln för JWT.
     *
     * @return Nyckel som används för att signera JWT.
     */
    private Key getSigniInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);  // Dekoda den hemliga nyckeln
        return Keys.hmacShaKeyFor(keyBytes);  // Skapa en HMAC SHA-nyckel
    }

    /**
     * Genererar en JWT-token för en given användare.
     *
     * @param user Användaren för vilken token ska genereras.
     * @return Den genererade JWT-token som en sträng.
     */
    public String generateToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("id", user.getId());  // Spara användarens ID i kraven
        return createToken(claims, user.getUsername());  // Skapa och returnera token
    }

    /**
     * Skapar en JWT-token med extra krav och användarnamn.
     *
     * @param extraClaims En karta med extra krav att inkludera i token.
     * @param username    Användarnamnet som ska anges i token.
     * @return Den skapade JWT-token som en sträng.
     */
    public String createToken(Map<String, Object> extraClaims, String username) {
        return Jwts.builder()
                .setClaims(extraClaims)  // Sätt de extra kraven
                .setSubject(username)  // Sätt användarnamnet
                .setIssuedAt(new Date(System.currentTimeMillis()))  // Sätt utfärdandedatum
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration))  // Sätt utgångsdatum
                .signWith(getSigniInKey(), SignatureAlgorithm.HS256)  // Signera token
                .compact();  // Kompaktisera token
    }

    /**
     * Kontrollerar om JWT-tokenet har gått ut.
     *
     * @param token JWT-token som ska kontrolleras.
     * @return Sant om tokenet har gått ut, annars falskt.
     */
    public boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());  // Jämför utgångsdatum med nuvarande datum
    }

    /**
     * Validerar JWT-tokenet genom att kontrollera användarnamn och utgångsdatum.
     *
     * @param token      JWT-token som ska valideras.
     * @param userDetails UserDetails-objektet för att jämföra användarnamn.
     * @return Sant om tokenet är giltigt, annars falskt.
     */
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);  // Extrahera användarnamnet från token
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));  // Kontrollera giltighet
    }
}
