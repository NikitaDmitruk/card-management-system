package by.testprojects.cardmanagementsystem.security.service;

import by.testprojects.cardmanagementsystem.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Сервис для работы с JWT (JSON Web Tokens).
 * Обеспечивает генерацию, валидацию и извлечение данных из токенов.
 * Реализован с использованием современного API JJWT (0.12.x).
 */
@Service
public class JwtService {

    @Value("${jwt.secret-key}")
    private String secretKey;

    @Value("${jwt.expiration}")
    private long jwtExpiration;

    @Value("${jwt.issuer}")
    private String issuer;

    /**
     * Генерирует JWT токен для пользователя без дополнительных claims.
     *
     * @param user данные пользователя (Spring Security)
     * @return строка JWT токена
     * @throws JwtException если возникла ошибка при генерации токена
     * @see #generateToken(Map, User)
     */
    public String generateToken(User user) {
        return generateToken(new HashMap<>(), user);
    }

    /**
     * Генерирует JWT токен с дополнительными claims.
     *
     * @param extraClaims дополнительные данные для включения в токен (например, роли, ID пользователя)
     * @param user        данные пользователя (Spring Security)
     * @return строка JWT токена
     * @throws IllegalArgumentException если userDetails == null
     * @throws JwtException             если возникла ошибка при генерации токена
     * @apiNote Пример использования:
     * {@code
     * Map<String, Object> claims = Map.of("role", "ADMIN");
     * String token = jwtService.generateToken(claims, userDetails);
     * }
     */
    public String generateToken(Map<String, Object> extraClaims, User user) {
        // Добавляем роль в claims токена
        extraClaims.put("id", user.getId());
        extraClaims.put("role", user.getAuthorities()); // Сохраняем как строку "ADMIN" или "USER"
        return Jwts.builder()
                .subject(user.getEmail())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .claims(extraClaims)
                .issuer(issuer)
                .signWith(getSigningKey(), Jwts.SIG.HS256)
                .compact();
    }

    /**
     * Проверяет валидность токена для указанного пользователя.
     *
     * @param token       токен для проверки
     * @param userDetails данные пользователя для сравнения
     * @return true если токен валиден и соответствует пользователю
     * @throws JwtException если токен не может быть распарсен
     * @see #extractUsername(String)
     * @see #isTokenExpired(String)
     */
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    /**
     * Извлекает имя пользователя (subject) из токена.
     *
     * @param token JWT токен
     * @return имя пользователя (email или username)
     * @throws JwtException если токен не может быть распарсен
     * @see Claims#getSubject()
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }


    /**
     * Извлекает конкретный claim из токена с помощью функции.
     *
     * @param token          JWT токен
     * @param claimsResolver функция для извлечения конкретного claim
     * @param <T>            тип возвращаемого значения
     * @return значение запрошенного claim
     * @throws JwtException если токен не может быть распарсен
     * @see #extractAllClaims(String)
     */
    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Парсит и извлекает все claims из токена.
     *
     * @param token JWT токен
     * @return объект Claims со всеми данными токена
     * @throws JwtException             если токен невалиден или подпись неверна
     * @throws IllegalArgumentException если токен пустой или некорректного формата
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * Создает ключ для подписи на основе секретного ключа из конфигурации.
     *
     * @return SecretKey для подписи JWT
     * @throws IllegalArgumentException если секретный ключ пустой или некорректный
     * @apiNote Секретный ключ должен быть в BASE64-кодировке длиной минимум 256 бит
     */
    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Проверяет, истек ли срок действия токена.
     *
     * @param token JWT токен
     * @return true если токен просрочен
     * @throws JwtException если невозможно извлечь дату истечения
     * @see #extractExpiration(String)
     */
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * Извлекает дату истечения токена.
     *
     * @param token JWT токен
     * @return дата истечения срока действия
     * @throws JwtException если claim expiration отсутствует или невалиден
     * @see Claims#getExpiration()
     */
    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }
}