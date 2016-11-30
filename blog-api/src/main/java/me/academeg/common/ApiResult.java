package me.academeg.common;

/**
 * ApiResult
 *
 * @author Yuriy A. Samsonov <y.samsonov@erpscan.com>
 * @version 1.0
 */
public interface ApiResult {

    long getStatus();

    String getMessage();
}
