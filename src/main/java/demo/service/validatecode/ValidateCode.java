package demo.service.validatecode;

import lombok.Data;

import java.awt.image.BufferedImage;
import java.time.LocalDateTime;

/**
 * 验证码
 */
@Data
public class ValidateCode {
    private BufferedImage image;
    private String code;
    private LocalDateTime expireTime;

    /**
     * @param expirtSecond 设置过期时间，单位秒
     */
    public ValidateCode(BufferedImage image, String code, int expirtSecond){
        this.image = image;
        this.code = code;
        // expireSecond秒后的时间
        this.expireTime = LocalDateTime.now().plusSeconds(expirtSecond);
    }
    /**
     * 验证码是否过期
     */
    public boolean isExpired(){
        return LocalDateTime.now().isAfter(expireTime);
    }
}
