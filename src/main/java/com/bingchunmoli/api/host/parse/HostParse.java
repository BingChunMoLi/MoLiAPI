package com.bingchunmoli.api.host.parse;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import com.bingchunmoli.api.host.bean.Host;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * @author BingChunMoLi
 */
public class HostParse {

    public static List<Host> parseFromFile(String filePath, String source) throws IOException {
        Path path = Paths.get(filePath);
        BufferedReader reader = FileUtil.getReader(path, Charset.defaultCharset());
        ArrayList<Host> list = new ArrayList<>(500);
        String line;
        while ((line = reader.readLine()) != null){
            if (!line.startsWith("#") && StrUtil.isNotBlank(line)) {
                line = line.trim();
                String[] temp = line.split(" ");
                List<String> tempList = new ArrayList<>(2);
                for (String s : temp) {
                    if (StrUtil.isNotBlank(s)) {
                        tempList.add(s);
                    }
                }
                list.add(new Host(null, tempList.get(0), tempList.get(1), source));
            }
        }
        return list;
    }
}
