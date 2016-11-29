package com.telemarket.telemarketer.http.responses;

import com.telemarket.telemarketer.http.MimeTypes;
import com.telemarket.telemarketer.http.Status;
import com.telemarket.telemarketer.http.exceptions.ServerInternalException;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;

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
        String path = file.getAbsolutePath();
        try {
            String contentType = MimeTypes.findContentType(path);
            content = Files.readAllBytes(FileSystems.getDefault().getPath(path)); // TODO 静态文件不应该全部读到内存中下载
            if (contentType.startsWith("text")) {
                contentType += "; charset=" + CHARSET;
            }
            heads.put("Content-Type", contentType);
        } catch (IOException e) {
            this.status = Status.NOT_FOUND_404;
        }
    }
}
