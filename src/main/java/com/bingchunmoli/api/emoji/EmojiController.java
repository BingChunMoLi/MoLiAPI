package com.bingchunmoli.api.emoji;

import cn.hutool.extra.emoji.EmojiUtil;
import com.bingchunmoli.api.bean.ResultVO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author bingchunmoli
 */
@RestController
@RequestMapping("emoji")
public class EmojiController {

    @GetMapping("alise")
    public ResultVO emojiToAlise(String emoji){
        return new ResultVO(EmojiUtil.toAlias(emoji));
    }

    @GetMapping("unicode")
    public ResultVO emojiToUnicode(String emoji){
        return new ResultVO(EmojiUtil.toUnicode(emoji));
    }
    @GetMapping("html")
    public ResultVO emojiToHtml(String emoji){
        return new ResultVO(EmojiUtil.toHtml(emoji));
    }

    @GetMapping("isEmoji")
    public ResultVO isEmoji(String emoji){
        return new ResultVO(EmojiUtil.isEmoji(emoji));
    }

    @GetMapping("contains")
    public ResultVO containsEmoji(String emoji){
        return new ResultVO(EmojiUtil.containsEmoji(emoji));
    }

    @GetMapping("tag")
    public ResultVO getByTag(String tag){
        return new ResultVO(EmojiUtil.getByTag(tag));
    }

    @GetMapping("tag")
    public ResultVO getByAlise(String alise){
        return new ResultVO(EmojiUtil.get(alise));
    }

    @GetMapping("removeAllEmojis")
    public ResultVO removeAllEmojis(String emojiStr){
        return new ResultVO(EmojiUtil.removeAllEmojis(emojiStr));
    }

    @GetMapping("extractEmojis")
    public ResultVO extractEmojis(String emojiStr){
        return new ResultVO(EmojiUtil.extractEmojis(emojiStr));
    }
}
