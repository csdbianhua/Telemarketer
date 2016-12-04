package com.telemarket.telemarketer.http.responses;

import com.telemarket.telemarketer.http.MimeTypes;
import com.telemarket.telemarketer.http.Status;
import com.telemarket.telemarketer.http.exceptions.ServerInternalException;
import com.telemarket.telemarketer.util.TimeUtil;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

/**
 * 文件响应
 */
public class FileResponse extends Response {

    public FileResponse(Status status, File file) {
        super(status);
        if (file == null) {
            throw new ServerInternalException("Response File 对象为空");
        }
        if (!file.isFile() || !file.canRead()) {
            this.status = Status.NOT_FOUND_404;
            return;
        }
        long l = file.lastModified();
        heads.put("Last-Modified", TimeUtil.toRFC822(ZonedDateTime.ofInstant(Instant.ofEpochMilli(l), ZoneId.systemDefault())));
        String path = file.getAbsolutePath();
        try {
            String contentType = MimeTypes.findContentType(path);
            content = Files.readAllBytes(FileSystems.getDefault().getPath(path)); // TODO 静态文件不应该全部读到内存中下载 同时需要支持gzip以及chunk
            if (contentType.startsWith("text")) {
                contentType += "; charset=" + DEFAULT_CHARSET;
            }
            heads.put("Content-Type", contentType);
        } catch (IOException e) {
            this.status = Status.NOT_FOUND_404;
        }
    }
}
