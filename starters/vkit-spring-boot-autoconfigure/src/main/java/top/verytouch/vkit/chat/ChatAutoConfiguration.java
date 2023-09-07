package top.verytouch.vkit.chat;

import com.knuddels.jtokkit.api.EncodingRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import top.verytouch.vkit.chat.gpt.ChatGPTService;
import top.verytouch.vkit.chat.gpt.ChatGPTProperties;

/**
 * chat自动配置
 *
 * @author verytouch
 * @since 2023/7/3 10:47
 *
 */
@Configuration
@EnableConfigurationProperties(ChatGPTProperties.class)
@ConditionalOnProperty(prefix = "vkit.chat.gpt", name = "enabled", havingValue = "true", matchIfMissing = true)
@ConditionalOnClass(EncodingRegistry.class)
@Slf4j
public class ChatAutoConfiguration {

    @Bean
    public ChatGPTService chatGPTService(ChatGPTProperties properties) {
        log.info("已启用chat-gpt自动配置");
        return new ChatGPTService(properties);
    }

}
