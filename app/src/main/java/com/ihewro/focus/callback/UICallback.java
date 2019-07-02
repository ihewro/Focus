package com.ihewro.focus.callback;

import java.util.List;

/**
 * <pre>
 *     author : hewro
 *     e-mail : ihewro@163.com
 *     time   : 2019/07/02
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public abstract class UICallback {
    public void doUIWithFlag(boolean flag){};

    public void doUIWithIds(List<Integer> ids){};
}
