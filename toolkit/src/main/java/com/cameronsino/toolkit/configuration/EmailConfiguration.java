package com.cameronsino.toolkit.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "spring.mail")
public class EmailConfiguration {
    /**
     * 发件人电子邮箱
     */
    private String from;
    /**
     * 发件人电子邮箱密码  POP3/SMTP服务
     */
    private String password;
    /**
     * 指定发送邮件的主机
     */
    private String host;
    /**
     * 发指定端口
     */
    private Integer port = 25;
    /**
     * 是否开启邮件SSL
     */
    private Boolean sslEnable = true;
    /**
     * 是否开启邮件重试功能
     */
    private Boolean retryEnable = false;
    /**
     * 邮件重试的次数
     */
    private Integer retryFrequency = 3;
    /**
     * 是否启用EmailUtils类
     */
    private Boolean enableEmailUtils = false;

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public Boolean getSslEnable() {
        return sslEnable;
    }

    public void setSslEnable(Boolean sslEnable) {
        this.sslEnable = sslEnable;
    }

    public Boolean getRetryEnable() {
        return retryEnable;
    }

    public void setRetryEnable(Boolean retryEnable) {
        this.retryEnable = retryEnable;
    }

    public Integer getRetryFrequency() {
        return retryFrequency;
    }

    public void setRetryFrequency(Integer retryFrequency) {
        this.retryFrequency = retryFrequency;
    }

    public Boolean getEnableEmailUtils() {
        return enableEmailUtils;
    }

    public void setEnableEmailUtils(Boolean enableEmailUtils) {
        this.enableEmailUtils = enableEmailUtils;
    }
}
