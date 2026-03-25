package com.edu.ai.tools;


import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.SimpleTimeZone;

public class TimeTool {

    /*
    // 本地工具
    @Tool(description = "查询某个时区时间", name = "查询时区时间")
    public String time(@ToolParam(description ="时区编码，例如: Asia/shanghai", required = true)String zoneId) {
        ZoneId zone = ZoneId.of(zoneId);
        ZonedDateTime now = ZonedDateTime.now(zone);
        DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm::ss");
        return now.format(format);
    }
    */
}
