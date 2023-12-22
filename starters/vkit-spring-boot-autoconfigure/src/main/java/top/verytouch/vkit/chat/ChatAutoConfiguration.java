package top.verytouch.vkit.chat;

import com.knuddels.jtokkit.api.EncodingRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import top.verytouch.vkit.chat.common.ChatService;
import top.verytouch.vkit.chat.gpt.ChatGPTProperties;
import top.verytouch.vkit.chat.gpt.ChatGPTService;
import top.verytouch.vkit.chat.yiyan.YiYanProperties;
import top.verytouch.vkit.chat.yiyan.YiYanService;

/**
 * chat自动配置
 *
 * @author verytouch
 * @since 2023/7/3 10:47
 */
@Configuration
@EnableConfigurationProperties({ChatGPTProperties.class, YiYanProperties.class})
@Slf4j
public class ChatAutoConfiguration {

    /**
     * chat-gpt
     */
    @ConditionalOnExpression("'${vkit.chat.gpt.apikey:}' != ''")
    @ConditionalOnClass(name = "com.knuddels.jtokkit.api.EncodingRegistry")
    @Bean
    public ChatService chatGPTService(ChatGPTProperties properties) {
        log.info("已启用chat-gpt自动配置");
        return new ChatGPTService(properties);
    }

    /**
     * 文心一言
     */
    @ConditionalOnExpression("'${vkit.chat.yiyan.client-id:}' != ''")
    @Bean
    public ChatService yiYanService(YiYanProperties yiYanProperties) {
        log.info("已启用文心一言自动配置");
        return new YiYanService(yiYanProperties);
    }

}
