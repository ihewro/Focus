package com.ihewro.focus.bean;

/**
 * <pre>
 *     author : hewro
 *     e-mail : ihewro@163.com
 *     time   : 2019/05/11
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public class Help {
    private boolean isHelp;
    private String url;

    public Help(boolean isHelp, String url) {
        this.isHelp = isHelp;
        this.url = url;
    }

    public Help(boolean isHelp) {
        this.isHelp = isHelp;
    }

    public boolean isHelp() {
        return isHelp;
    }

    public void setHelp(boolean help) {
        isHelp = help;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
