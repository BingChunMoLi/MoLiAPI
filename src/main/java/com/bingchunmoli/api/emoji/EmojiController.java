package com.bingchunmoli.api.emoji;

import cn.hutool.extra.emoji.EmojiUtil;
import com.vdurmont.emoji.Emoji;
import com.vdurmont.emoji.EmojiParser;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Set;

/**
 * emoji表情的各种接口
 *
 * @author bingchunmoli
 */
@RestController
@RequestMapping("emoji")
//@Api("emoji表情处理")
public class EmojiController {

    /**
     * 将字符串中的Unicode Emoji字符转换为别名表现形式（两个":"包围的格式）
     * <p>
     * 例如： <code>😄</code> 转换为 <code>:smile:</code>
     *
     * <p>
     * {@link EmojiParser.FitzpatrickAction}参数被设置为{@link EmojiParser.FitzpatrickAction#PARSE}，则别名后会增加"|"并追加fitzpatrick类型
     * <p>
     * 例如：<code>👦🏿</code> 转换为 <code>:boy|type_6:</code>
     *
     * <p>
     * {@link EmojiParser.FitzpatrickAction}参数被设置为{@link EmojiParser.FitzpatrickAction#REMOVE}，则别名后的"|"和类型将被去除
     * <p>
     * 例如：<code>👦🏿</code> 转换为 <code>:boy:</code>
     *
     * <p>
     * {@link EmojiParser.FitzpatrickAction}参数被设置为{@link EmojiParser.FitzpatrickAction#IGNORE}，则别名后的类型将被忽略
     * <p>
     * 例如：<code>👦🏿</code> 转换为 <code>:boy:🏿</code>
     *
     * @param emoji 包含Emoji Unicode字符的字符串
     * @return 替换后的字符串
     */
    @GetMapping("alise")
//    @ApiOperation("emoji转别名")
    public String emojiToAlise(String emoji) {
        return EmojiUtil.toAlias(emoji);
    }

    /**
     * 将子串中的Emoji别名（两个":"包围的格式）和其HTML表示形式替换为为Unicode Emoji符号
     * <p>
     * 例如：
     *
     * <pre>
     *  <code>:smile:</code>  替换为 <code>😄</code>
     * <code>&amp;#128516;</code> 替换为 <code>😄</code>
     * <code>:boy|type_6:</code> 替换为 <code>👦🏿</code>
     * </pre>
     *
     * @param emoji 包含Emoji别名或者HTML表现形式的字符串
     * @return 替换后的字符串
     */
    @GetMapping("unicode")
    //@ApiOperation("emoji转unicode")
    public String emojiToUnicode(String emoji) {
        return EmojiUtil.toUnicode(emoji);
    }

    /**
     * 将字符串中的Unicode Emoji字符转换为HTML表现形式
     * <p>
     * 例如：<code>👦🏿</code> 转换为 <code>&amp;#128102;</code>
     *
     * @param emoji 包含Emoji Unicode字符的字符串
     * @return 替换后的字符串
     */
    @GetMapping("html")
    //@ApiOperation("emoji转html表示法")
    public String emojiToHtml(String emoji) {
        return EmojiUtil.toHtml(emoji);
    }

    /**
     * 是否为Emoji表情的Unicode符
     *
     * @param emoji emoji表情
     * @return unicode
     */
    @GetMapping("isEmoji")
    //@ApiOperation("是否是emoji表情的unicode符")
    public Boolean isEmoji(String emoji) {
        return EmojiUtil.isEmoji(emoji);
    }

    /**
     * 是否包含Emoji表情的Unicode符
     *
     * @param emoji emoji表情
     * @return unicode
     */
    @GetMapping("contains")
    //@ApiOperation("是否含有emoji表情的Unicode符")
    public Boolean containsEmoji(String emoji) {
        return EmojiUtil.containsEmoji(emoji);
    }

    /**
     * //@ApiNote 通过tag方式获取对应的所有Emoji表情
     *
     * @param tag tag标签，例如“happy”
     * @return Emoji表情集合，如果找不到返回null
     */
    @GetMapping("tag")
    //@ApiOperation("根据tag获取对应所有的Emoji表情")
    public Set<Emoji> getByTag(String tag) {
        return EmojiUtil.getByTag(tag);
    }

    /**
     * 通过别名获取Emoji
     *
     * @param alise 别名，例如“smile”
     * @return Emoji对象，如果找不到返回null
     */
    @GetMapping("getByAlise")
    //@ApiOperation("通过别名获取Emoji")
    public Emoji getByAlise(String alise) {
        return EmojiUtil.get(alise);
    }

    /**
     * 去除字符串中所有的Emoji Unicode字符
     *
     * @param emojiStr 包含Emoji字符的字符串
     * @return 替换后的字符串
     */
    @GetMapping("removeAllEmojis")
    //@ApiOperation("去除字符串中所有的Emoji Unicode字符")
    public String removeAllEmojis(String emojiStr) {
        return EmojiUtil.removeAllEmojis(emojiStr);
    }

    /**
     * 提取字符串中所有的Emoji Unicode
     *
     * @param emojiStr 包含Emoji字符的字符串
     * @return Emoji字符列表
     */
    @GetMapping("extractEmojis")
    //@ApiOperation("提取字符串中所有的Emoji Unicode")
    public List<String> extractEmojis(String emojiStr) {
        return EmojiUtil.extractEmojis(emojiStr);
    }
}
