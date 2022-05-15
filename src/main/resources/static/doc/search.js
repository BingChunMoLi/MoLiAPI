let api = [];
const apiDocListSize = 1
api.push({
    name: 'default',
    order: '1',
    list: []
})
api[0].list.push({
    alias: 'BingController',
    order: '1',
    link: 'bing每日美图',
    desc: 'bing每日美图',
    list: []
})
api[0].list[0].list.push({
    order: '1',
    deprecated: 'false',
    url: 'https://api.bingchunmoli.com/bing/cn',
    desc: '',
});
api[0].list[0].list.push({
    order: '2',
    deprecated: 'false',
    url: 'https://api.bingchunmoli.com/bing/en',
    desc: '',
});
api[0].list[0].list.push({
    order: '3',
    deprecated: 'false',
    url: 'https://api.bingchunmoli.com/bing/all',
    desc: '',
});
api[0].list[0].list.push({
    order: '4',
    deprecated: 'false',
    url: 'https://api.bingchunmoli.com/bing/random',
    desc: '获取随机一张图的url',
});
api[0].list.push({
    alias: 'ErrorController',
    order: '2',
    link: '错误页面与错误信息处理',
    desc: '错误页面与错误信息处理',
    list: []
})
api[0].list[1].list.push({
    order: '1',
    deprecated: 'false',
    url: 'https://api.bingchunmoli.com/error',
    desc: '友好错误返回页面',
});
api[0].list[1].list.push({
    order: '2',
    deprecated: 'false',
    url: 'https://api.bingchunmoli.com/error',
    desc: '友好错误信息',
});
api[0].list.push({
    alias: 'EmojiController',
    order: '3',
    link: 'emoji表情的各种接口',
    desc: 'emoji表情的各种接口',
    list: []
})
api[0].list[2].list.push({
    order: '1',
    deprecated: 'false',
    url: 'https://api.bingchunmoli.com/emoji/alise',
    desc: '将字符串中的Unicode Emoji字符转换为别名表现形式（两个":"包围的格式） &lt;p&gt; 例如： &lt;code&gt;😄&lt;/code&gt; 转换为 &lt;code&gt;:smile:&lt;/code&gt;  &lt;p&gt; {@link EmojiParser.FitzpatrickAction}参数被设置为{@link EmojiParser.FitzpatrickAction#PARSE}，则别名后会增加"|"并追加fitzpatrick类型 &lt;p&gt; 例如：&lt;code&gt;👦🏿&lt;/code&gt; 转换为 &lt;code&gt;:boy|type_6:&lt;/code&gt;  &lt;p&gt; {@link EmojiParser.FitzpatrickAction}参数被设置为{@link EmojiParser.FitzpatrickAction#REMOVE}，则别名后的"|"和类型将被去除 &lt;p&gt; 例如：&lt;code&gt;👦🏿&lt;/code&gt; 转换为 &lt;code&gt;:boy:&lt;/code&gt;  &lt;p&gt; {@link EmojiParser.FitzpatrickAction}参数被设置为{@link EmojiParser.FitzpatrickAction#IGNORE}，则别名后的类型将被忽略 &lt;p&gt; 例如：&lt;code&gt;👦🏿&lt;/code&gt; 转换为 &lt;code&gt;:boy:🏿&lt;/code&gt;',
});
api[0].list[2].list.push({
    order: '2',
    deprecated: 'false',
    url: 'https://api.bingchunmoli.com/emoji/unicode',
    desc: '将子串中的Emoji别名（两个":"包围的格式）和其HTML表示形式替换为为Unicode Emoji符号 &lt;p&gt; 例如：  &lt;pre&gt;  &lt;code&gt;:smile:&lt;/code&gt;  替换为 &lt;code&gt;😄&lt;/code&gt; &lt;code&gt;&amp;#128516;&lt;/code&gt; 替换为 &lt;code&gt;😄&lt;/code&gt; &lt;code&gt;:boy|type_6:&lt;/code&gt; 替换为 &lt;code&gt;👦🏿&lt;/code&gt; &lt;/pre&gt;',
});
api[0].list[2].list.push({
    order: '3',
    deprecated: 'false',
    url: 'https://api.bingchunmoli.com/emoji/html',
    desc: '将字符串中的Unicode Emoji字符转换为HTML表现形式 &lt;p&gt; 例如：&lt;code&gt;👦🏿&lt;/code&gt; 转换为 &lt;code&gt;&amp;#128102;&lt;/code&gt;',
});
api[0].list[2].list.push({
    order: '4',
    deprecated: 'false',
    url: 'https://api.bingchunmoli.com/emoji/isEmoji',
    desc: '是否为Emoji表情的Unicode符',
});
api[0].list[2].list.push({
    order: '5',
    deprecated: 'false',
    url: 'https://api.bingchunmoli.com/emoji/contains',
    desc: '是否包含Emoji表情的Unicode符',
});
api[0].list[2].list.push({
    order: '6',
    deprecated: 'false',
    url: 'https://api.bingchunmoli.com/emoji/tag',
    desc: '',
});
api[0].list[2].list.push({
    order: '7',
    deprecated: 'false',
    url: 'https://api.bingchunmoli.com/emoji/getByAlise',
    desc: '通过别名获取Emoji',
});
api[0].list[2].list.push({
    order: '8',
    deprecated: 'false',
    url: 'https://api.bingchunmoli.com/emoji/removeAllEmojis',
    desc: '去除字符串中所有的Emoji Unicode字符',
});
api[0].list[2].list.push({
    order: '9',
    deprecated: 'false',
    url: 'https://api.bingchunmoli.com/emoji/extractEmojis',
    desc: '提取字符串中所有的Emoji Unicode',
});
api[0].list.push({
    alias: 'HostController',
    order: '4',
    link: 'hosts订阅',
    desc: 'hosts订阅',
    list: []
})
api[0].list[3].list.push({
    order: '1',
    deprecated: 'false',
    url: 'https://api.bingchunmoli.com/host/json',
    desc: '根据参数获取hosts',
});
api[0].list[3].list.push({
    order: '2',
    deprecated: 'false',
    url: 'https://api.bingchunmoli.com/host/raw',
    desc: 'raw的host',
});
api[0].list[3].list.push({
    order: '3',
    deprecated: 'false',
    url: 'https://api.bingchunmoli.com/host/file',
    desc: '下载文件的hosts',
});
api[0].list.push({
    alias: 'ImgController',
    order: '5',
    link: '随机图',
    desc: '随机图',
    list: []
})
api[0].list[4].list.push({
    order: '1',
    deprecated: 'false',
    url: 'https://api.bingchunmoli.com/img/mobile',
    desc: '手机版',
});
api[0].list[4].list.push({
    order: '2',
    deprecated: 'false',
    url: 'https://api.bingchunmoli.com/img/pc',
    desc: 'pc图片 使用ResponseEntity写出',
});
api[0].list[4].list.push({
    order: '3',
    deprecated: 'false',
    url: 'https://api.bingchunmoli.com/img/pc1',
    desc: 'pc图片 备用写法，使用BufferedImage对象写出',
});
api[0].list.push({
    alias: 'IpController',
    order: '6',
    link: '来源ip',
    desc: '来源IP',
    list: []
})
api[0].list[5].list.push({
    order: '1',
    deprecated: 'false',
    url: 'https://api.bingchunmoli.com/ip',
    desc: '请求的IP',
});
api[0].list.push({
    alias: 'QrCodeController',
    order: '7',
    link: '二维码',
    desc: '二维码',
    list: []
})
api[0].list[6].list.push({
    order: '1',
    deprecated: 'false',
    url: 'https://api.bingchunmoli.com/decode',
    desc: '文件解码',
});
api[0].list[6].list.push({
    order: '2',
    deprecated: 'false',
    url: 'https://api.bingchunmoli.com/generate',
    desc: '生成二维码',
});
api[0].list.push({
    alias: 'ShiCiController',
    order: '8',
    link: '诗词',
    desc: '诗词',
    list: []
})
api[0].list[7].list.push({
    order: '1',
    deprecated: 'false',
    url: 'https://api.bingchunmoli.com/shici/{id}',
    desc: '指定ID诗词',
});
api[0].list[7].list.push({
    order: '2',
    deprecated: 'false',
    url: 'https://api.bingchunmoli.com/shici/random',
    desc: '从缓存中读取一条随机诗词，如果不存在从数据库读取',
});
api[0].list.push({
    alias: 'QqController',
    order: '9',
    link: 'qq、qz头像',
    desc: 'qq、qz头像',
    list: []
})
api[0].list[8].list.push({
    order: '1',
    deprecated: 'false',
    url: 'https://api.bingchunmoli.com/tencent/qq',
    desc: '返回QQ头像',
});
api[0].list[8].list.push({
    order: '2',
    deprecated: 'false',
    url: 'https://api.bingchunmoli.com/tencent/qz',
    desc: '返回qq空间头像',
});
api[0].list[8].list.push({
    order: '3',
    deprecated: 'false',
    url: 'https://api.bingchunmoli.com/tencent/qq/json',
    desc: '加密形式获取qq头像地址',
});
api[0].list[8].list.push({
    order: '4',
    deprecated: 'false',
    url: 'https://api.bingchunmoli.com/tencent/qz/json',
    desc: 'QQ空间头像json形式',
});
api[0].list[8].list.push({
    order: '5',
    deprecated: 'false',
    url: 'https://api.bingchunmoli.com/tencent/qq/json/encrypt',
    desc: '加密的qq头像地址',
});
api[0].list[8].list.push({
    order: '6',
    deprecated: 'false',
    url: 'https://api.bingchunmoli.com/tencent/qq/encrypt',
    desc: '通过加密接口返回qq头像',
});
api[0].list.push({
    alias: 'ThunderDownloadProtocolController',
    order: '10',
    link: '迅雷链接',
    desc: '迅雷链接',
    list: []
})
api[0].list[9].list.push({
    order: '1',
    deprecated: 'false',
    url: 'https://api.bingchunmoli.com/thunder/toRaw',
    desc: '转换迅雷下载协议链接至原始链接',
});
api[0].list[9].list.push({
    order: '2',
    deprecated: 'false',
    url: 'https://api.bingchunmoli.com/thunder/toThunder',
    desc: '原始协议转换为迅雷协议',
});
api[0].list.push({
    alias: 'UserAgentController',
    order: '11',
    link: 'useragent',
    desc: 'UserAgent',
    list: []
})
api[0].list[10].list.push({
    order: '1',
    deprecated: 'false',
    url: 'https://api.bingchunmoli.com/ua/userAgentInfo',
    desc: '从请求头中获取user-agent',
});
api[0].list[10].list.push({
    order: '2',
    deprecated: 'false',
    url: 'https://api.bingchunmoli.com/ua/userAgentInfoByParam',
    desc: '从请求参数中获取 userAgent',
});
api[0].list[10].list.push({
    order: '3',
    deprecated: 'false',
    url: 'https://api.bingchunmoli.com/ua/userAgent',
    desc: '从请求参数中获取，获取不到就获取请求头中的userAgent',
});
api[0].list.push({
    alias: 'WeatherController',
    order: '12',
    link: '天气',
    desc: '天气',
    list: []
})
api[0].list[11].list.push({
    order: '1',
    deprecated: 'false',
    url: 'https://api.bingchunmoli.com/weather/byDay',
    desc: '按天查询天气',
});
api[0].list.push({
    alias: 'YiYanController',
    order: '13',
    link: '一言',
    desc: '一言',
    list: []
})
api[0].list[12].list.push({
    order: '1',
    deprecated: 'false',
    url: 'https://api.bingchunmoli.com/yiyan/{id}',
    desc: '根据ID获取一言',
});
api[0].list[12].list.push({
    order: '2',
    deprecated: 'false',
    url: 'https://api.bingchunmoli.com/yiyan/random',
    desc: '查询随机一条一言数据',
});
document.onkeydown = keyDownSearch;
function keyDownSearch(e) {
    const theEvent = e;
    const code = theEvent.keyCode || theEvent.which || theEvent.charCode;
    if (code == 13) {
        const search = document.getElementById('search');
        const searchValue = search.value.toLocaleLowerCase();

        let searchGroup = [];
        for (let i = 0; i < api.length; i++) {

            let apiGroup = api[i];

            let searchArr = [];
            for (let i = 0; i < apiGroup.list.length; i++) {
                let apiData = apiGroup.list[i];
                const desc = apiData.desc;
                if (desc.toLocaleLowerCase().indexOf(searchValue) > -1) {
                    searchArr.push({
                        order: apiData.order,
                        desc: apiData.desc,
                        link: apiData.link,
                        list: apiData.list
                    });
                } else {
                    let methodList = apiData.list || [];
                    let methodListTemp = [];
                    for (let j = 0; j < methodList.length; j++) {
                        const methodData = methodList[j];
                        const methodDesc = methodData.desc;
                        if (methodDesc.toLocaleLowerCase().indexOf(searchValue) > -1) {
                            methodListTemp.push(methodData);
                            break;
                        }
                    }
                    if (methodListTemp.length > 0) {
                        const data = {
                            order: apiData.order,
                            desc: apiData.desc,
                            link: apiData.link,
                            list: methodListTemp
                        };
                        searchArr.push(data);
                    }
                }
            }
            if (apiGroup.name.toLocaleLowerCase().indexOf(searchValue) > -1) {
                searchGroup.push({
                    name: apiGroup.name,
                    order: apiGroup.order,
                    list: searchArr
                });
                continue;
            }
            if (searchArr.length === 0) {
                continue;
            }
            searchGroup.push({
                name: apiGroup.name,
                order: apiGroup.order,
                list: searchArr
            });
        }
        let html;
        if (searchValue == '') {
            const liClass = "";
            const display = "display: none";
            html = buildAccordion(api,liClass,display);
            document.getElementById('accordion').innerHTML = html;
        } else {
            const liClass = "open";
            const display = "display: block";
            html = buildAccordion(searchGroup,liClass,display);
            document.getElementById('accordion').innerHTML = html;
        }
        const Accordion = function (el, multiple) {
            this.el = el || {};
            this.multiple = multiple || false;
            const links = this.el.find('.dd');
            links.on('click', {el: this.el, multiple: this.multiple}, this.dropdown);
        };
        Accordion.prototype.dropdown = function (e) {
            const $el = e.data.el;
            $this = $(this), $next = $this.next();
            $next.slideToggle();
            $this.parent().toggleClass('open');
            if (!e.data.multiple) {
                $el.find('.submenu').not($next).slideUp("20").parent().removeClass('open');
            }
        };
        new Accordion($('#accordion'), false);
    }
}

function buildAccordion(apiGroups, liClass, display) {
    let html = "";
    let doc;
    if (apiGroups.length > 0) {
         if (apiDocListSize == 1) {
            let apiData = apiGroups[0].list;
            for (let j = 0; j < apiData.length; j++) {
                html += '<li class="'+liClass+'">';
                html += '<a class="dd" href="#_' + apiData[j].link + '">' + apiData[j].order + '.&nbsp;' + apiData[j].desc + '</a>';
                html += '<ul class="sectlevel2" style="'+display+'">';
                doc = apiData[j].list;
                for (let m = 0; m < doc.length; m++) {
                    let spanString;
                    if (doc[m].deprecated == 'true') {
                        spanString='<span class="line-through">';
                    } else {
                        spanString='<span>';
                    }
                    html += '<li><a href="#_1_' + apiData[j].order + '_' + doc[m].order + '_' + doc[m].desc + '">' + apiData[j].order + '.' + doc[m].order + '.&nbsp;' + spanString + doc[m].desc + '<span></a> </li>';
                }
                html += '</ul>';
                html += '</li>';
            }
        } else {
            for (let i = 0; i < apiGroups.length; i++) {
                let apiGroup = apiGroups[i];
                html += '<li class="'+liClass+'">';
                html += '<a class="dd" href="#_' + apiGroup.name + '">' + apiGroup.order + '.&nbsp;' + apiGroup.name + '</a>';
                html += '<ul class="sectlevel1">';

                let apiData = apiGroup.list;
                for (let j = 0; j < apiData.length; j++) {
                    html += '<li class="'+liClass+'">';
                    html += '<a class="dd" href="#_'+apiGroup.order+'_'+ apiData[j].order + '_'+ apiData[j].link + '">' +apiGroup.order+'.'+ apiData[j].order + '.&nbsp;' + apiData[j].desc + '</a>';
                    html += '<ul class="sectlevel2" style="'+display+'">';
                    doc = apiData[j].list;
                    for (let m = 0; m < doc.length; m++) {
                       let spanString;
                       if (doc[m].deprecated == 'true') {
                           spanString='<span class="line-through">';
                       } else {
                           spanString='<span>';
                       }
                       html += '<li><a href="#_'+apiGroup.order+'_' + apiData[j].order + '_' + doc[m].order + '_' + doc[m].desc + '">'+apiGroup.order+'.' + apiData[j].order + '.' + doc[m].order + '.&nbsp;' + spanString + doc[m].desc + '<span></a> </li>';
                   }
                    html += '</ul>';
                    html += '</li>';
                }

                html += '</ul>';
                html += '</li>';
            }
        }
    }
    return html;
}