package com.telmarketer.test;

import com.telemarket.telemarketer.exceptions.TransformTypeException;
import com.telemarket.telemarketer.http.requests.MimeData;
import com.telemarket.telemarketer.util.ReflectUtil;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.net.URL;

/**
 * 测试工具类
 * <p>
 * Be careful.
 * Author: Hanson
 * Email: imyijie@outlook.com
 * Date: 2016/12/8
 */
@RunWith(JUnit4.class)
public class UtilTest {

    @Test
    public void reflectTransformTest() {
        Assert.assertTrue("abc".equals(ReflectUtil.parseObj("abc", String.class)));
        Assert.assertTrue(Boolean.TRUE.equals(ReflectUtil.parseObj("true", Boolean.class)));
        Assert.assertTrue(new Float(3.444).equals(ReflectUtil.parseObj("3.444", Float.class)));
        Assert.assertTrue(new Double(3.444).equals(ReflectUtil.parseObj("3.444", Double.class)));
        Assert.assertTrue(new Integer(3).equals(ReflectUtil.parseObj("3", Integer.class)));
        Assert.assertTrue(Long.MAX_VALUE == (Long) (ReflectUtil.parseObj(String.valueOf(Long.MAX_VALUE), Long.class)));
        MimeData mimeData = new MimeData("test", new byte[0x00], "test");
        Assert.assertTrue(mimeData.getData().equals(ReflectUtil.parseObj(mimeData, byte[].class)));
        Assert.assertTrue(mimeData.equals(ReflectUtil.parseObj(mimeData, MimeData.class)));
        Assert.assertTrue(ReflectUtil.parseObj(mimeData, URL.class) == null);
    }

    @Test(expected = TransformTypeException.class)
    public void castException() {
        Assert.assertTrue(new Integer(3).equals(ReflectUtil.parseObj("ster", Integer.class)));
    }
}
